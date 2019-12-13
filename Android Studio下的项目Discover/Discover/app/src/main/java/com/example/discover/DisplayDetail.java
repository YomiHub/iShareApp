package com.example.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bean.CommentInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DisplayDetail extends Activity{
    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源

    TextView tv_info_author; //内容作者
    TextView tv_info_title; //内容标题
    TextView tv_info_describe; //内容简述
    TextView tv_info_detail; //内容详情
    TextView tv_info_support; //内容点赞数
    ImageButton btn_info_like;   //内容收藏
    ImageButton btn_info_delete;   //内容删除

    EditText et_comment_detail;  //评论的内容
    Button comment_btn;   //评论按钮

    TextView share_img_title; //图片背景上的文字
    FrameLayout share_detail_img;   //分享的图片背景

    String infoId;
    String infoTitle;   //内容标题
    String infoDescribe;   //内容简述
    String infoDetail;   //内容详情
    Integer type;    //类型：0表示日记，1表示趣事
    Integer support;   //点赞数
    String infoAuthor;  //作者

    Boolean isFocus = false;  //是否收藏
    int focusId;  //收藏ID

    Boolean isMyInfo = false;
    private DisplayDetail.CommentAdapter commentAdapter;   //存放评论
    private int dataSize = 28;     //数据集的条数
    private ListView listView;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);

        tv_info_author = (TextView)findViewById(R.id.findDetail_info_author);  //文章作者

        tv_info_title = (TextView)findViewById(R.id.findDetail_info_title);  //文章标题
        tv_info_describe = (TextView)findViewById(R.id.findDetail_info_describe);  //文章简述
        tv_info_detail = (TextView)findViewById(R.id.detail_info_detail);  //文章详细内容
        tv_info_support  = (TextView)findViewById(R.id.detail_info_support);  //文章点赞数
        btn_info_like = (ImageButton)findViewById(R.id.detail_info_focus);   //文章收藏
        btn_info_delete = (ImageButton)findViewById(R.id.detail_info_delete);   //文章收藏

        et_comment_detail = (EditText) findViewById(R.id.et_comment_detail);  //评论的内容
        comment_btn = (Button)findViewById(R.id.comment_btn);   //评论按钮

        share_img_title = (TextView)findViewById(R.id.share_img_title);  //图片背景的文字
        share_detail_img  = (FrameLayout)findViewById(R.id.share_detail_img);  //图片背景的文字

        //取得启动该Activity的Intent对象
        Intent intent =getIntent();
        //取出Intent中附加的数据
        if(intent.getStringExtra("infoId")!=null){
            infoId =intent.getStringExtra("infoId");
            initialize(infoId);   //设置页面内容
            initializeFocus(infoId);   //改变收藏状态

            tv_info_support.setOnClickListener(new supportClick());
            btn_info_like.setOnClickListener(new focusClick());
            btn_info_delete.setOnClickListener(new deleteClick());
            listView = (ListView)findViewById(R.id.comment_listView);
            initializeAdapter();   //初始化，填入评论
            listView.setAdapter(commentAdapter);
            setListViewHeightBasedOnChildren(listView);
            comment_btn.setOnClickListener(new commentClick());
        }

    }


    /**
     * 查询数据库中的数据
     */
    private JSONArray loadData(String QueryInfoUrl){
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader buffer = null;
        HttpURLConnection connGET = null;

        try {
            URL url = new URL(QueryInfoUrl);
            connGET = (HttpURLConnection) url.openConnection();
            connGET.setConnectTimeout(5000);
            connGET.setRequestMethod("GET");
            if (connGET.getResponseCode() == 200) {
                buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    stringBuilder.append(s);
                }
                //返回测试信息
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());

                //获取到的数据，对Json进行解析
                buffer.close();
                return jsonArray;
            }else{
                Toast.makeText(DisplayDetail.this,"非200", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DisplayDetail.this, "get 提交 err.." + e.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    /**
     * 初始化ListView的适配器，即打开页面展示的评论
     */
    private void initializeAdapter(){
        String QueryCommentUrl = baseUrl+"QueryComment?infoId="+infoId;
        JSONArray jsonArray = loadData(QueryCommentUrl);

        if(jsonArray!=null) {
            try {
                JSONObject totalObject = (JSONObject) jsonArray.get(0);

                dataSize = totalObject.getInt("totalRecord");  //总记录数
                String detail = totalObject.getString("RecordDetail");   //详情

                if (initSetDataToBean(detail) != null) {
                    commentAdapter = new DisplayDetail.CommentAdapter(initSetDataToBean(detail));   //将详情设置到bean中
                }

            } catch (JSONException e) {
                Toast.makeText(DisplayDetail.this, "initializeAdapter异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(DisplayDetail.this, "查询评论为空" , Toast.LENGTH_LONG).show();
        }

    }


    /*刷新适配器*/
    public  void refreshDiscover(){
        listView.setAdapter(null);   //将ListView控件的内容清空
        initializeAdapter();
        listView.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

         int totalHeight = 0;
         for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
                // listAdapter.getCount()返回数据项的数目
                 View listItem = listAdapter.getView(i, null, listView);
                 // 计算子项View 的宽高
                 listItem.measure(0, 0);
              // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
             }

         ViewGroup.LayoutParams params = listView.getLayoutParams();
         params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
       // listView.getDividerHeight()获取子项间分隔符占用的高度
       // params.height最后得到整个ListView完整显示需要的高度
         listView.setLayoutParams(params);
    }

    //评论
    class commentClick implements View.OnClickListener {
        public void onClick(View v) {
            SharedPreferences sp = DisplayDetail.this.getSharedPreferences("save", Context.MODE_PRIVATE);
            String username = sp.getString("name","");

            String commentDetail = et_comment_detail.getText().toString();

            String AddSupportUrl = baseUrl+"AddComment?infoId="+infoId+"&commentUser="+username+"&commentDetail="+commentDetail;
            JSONArray jsonArray = loadData(AddSupportUrl);
            try {

                JSONObject totalObject = (JSONObject)jsonArray.get(0);
                Boolean addOk = totalObject.getBoolean("addOk");  //是否评论成功

                if(addOk){
                    refreshDiscover(); //刷新适配器
                    et_comment_detail.setText("");
                }else{
                    Toast.makeText(DisplayDetail.this, "评论失败，请稍后再试" , Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e){
                Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //点赞
    class supportClick implements View.OnClickListener {
        public void onClick(View v) {
            support = support+1;
            String AddSupportUrl = baseUrl+"AddSupport?InfoId="+infoId;
            JSONArray jsonArray = loadData(AddSupportUrl);
            try {

                JSONObject totalObject = (JSONObject)jsonArray.get(0);
                Boolean addOk = totalObject.getBoolean("addOk");  //是否点赞成功

                if(addOk){
                    overSupport();  //改变点赞图片状态为已经点赞
                }else{
                    support = support-1;  //没有点赞成功
                }

            }catch (JSONException e){
                Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //启动线程,加载主页
    private class LoadMainTask implements Runnable{
        @Override
        public void run() {
            Intent intent = new Intent(DisplayDetail.this,MainActivity.class);
            startActivity(intent);  //打开主页
            finish();   //关闭
        }
    }

    //删除文章
    class deleteClick implements View.OnClickListener {
        public void onClick(View v) {
            String deleteInfoUrl = baseUrl+"DeleteInfo?infoId="+infoId;
            JSONArray jsonArray = loadData(deleteInfoUrl);
            try {

                JSONObject totalObject = (JSONObject)jsonArray.get(0);
                Boolean deleteOk = totalObject.getBoolean("deleteOk");  //是否点赞成功

                if(deleteOk){
                    Toast.makeText(DisplayDetail.this, "删除成功", Toast.LENGTH_LONG).show();
                    //通过handler设置延时1秒后执行r任务，跳转到首页
                    new Handler().postDelayed(new DisplayDetail.LoadMainTask(),1000);

                }else{
                    Toast.makeText(DisplayDetail.this, "删除失败，请稍后再试", Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e){
                Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    //收藏或者取消收藏
    class focusClick implements View.OnClickListener {
        public void onClick(View v) {
            if(isFocus){
                //取消收藏
                String removeFocusUrl = baseUrl+"RemoveFocus?focusId="+focusId;
                JSONArray jsonArray = loadData(removeFocusUrl);

                try {
                    JSONObject totalObject = (JSONObject)jsonArray.get(0);
                    Boolean removeOk = totalObject.getBoolean("removeOk");  //是否取消收藏

                    if(removeOk){
                        nowNotLike();  //改变收藏状态为未收藏
                    }else{
                        Toast.makeText(DisplayDetail.this, "取消收藏失败，请稍后再试.", Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
                }

            }else{
                //添加到我的收藏
                SharedPreferences sp = DisplayDetail.this.getSharedPreferences("save", Context.MODE_PRIVATE);
                String username = sp.getString("name","");
                String addFocusUrl = baseUrl+"AddFocus?InfoId="+infoId+"&username="+username;
                JSONArray jsonArray = loadData(addFocusUrl);

                try {
                    JSONObject totalObject = (JSONObject)jsonArray.get(0);
                    Boolean addOk = totalObject.getBoolean("addOk");  //是否收藏成功
                    if(addOk){
                        overFocus();  //改变收藏状态为已经收藏
                        Toast.makeText(DisplayDetail.this, "收藏成功!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(DisplayDetail.this, "收藏失败，请稍后操作", Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /**
     * 初始化，即打开页面展示的数据
     */
    private void initialize(String info_id){
        String QueryInfoUrl = baseUrl+"QueryInfoById?infoId="+info_id;
        JSONArray jsonArray = loadData(QueryInfoUrl);

        try {
            JSONObject totalObject = (JSONObject)jsonArray.get(0);

            infoTitle = totalObject.getString("infoTitle");  //内容标题
          /*  Toast.makeText(DisplayDetail.this,totalObject.toString(), Toast.LENGTH_LONG).show();*/
            infoDescribe = totalObject.getString("infoDescribe");    //内容简述
        /*    infoDetail = totalObject.getString("infoDetail");  //内容详情*/
            String str = totalObject.getString("infoDetail");
            infoDetail =str.replaceAll("<br>","\n");   //获取回车符

            infoAuthor = totalObject.getString("infoAuthor");    //内容作者
            type = totalObject.getInt("infoType");  //内容类型
            support = totalObject.getInt("infoSupport");  //点赞数

            tv_info_title.setText(infoTitle);
            tv_info_author.setText(infoAuthor);
            tv_info_support.setText(support.toString());

            SharedPreferences sp = DisplayDetail.this.getSharedPreferences("save", Context.MODE_PRIVATE);
            String userName = sp.getString("name","");

            if(infoAuthor.equals(userName)){
                displayDelete();  //显示删除按钮
            }else{
                hideDelete(); //隐藏删除按钮
            }

            if(type==0||type==3){
                setArticlesDiaries();
            }else if(type==1){
                //分享图片
                setImg();
            }else if(type==2){
                //分享的音乐
                setArticlesDiaries();
            }
        }catch (JSONException e){
            Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 初始化，是否收藏
     */
    private void initializeFocus(String info_id){
        //获取最新的登录状态，SharePreferences为永久存储，需要手动退出
        SharedPreferences sp = DisplayDetail.this.getSharedPreferences("save", Context.MODE_PRIVATE);
        String username = sp.getString("name","");

        String QueryIfFocusUrl = baseUrl+"IfAddFocus?username="+username+"&infoId="+info_id;
        JSONArray jsonArray = loadData(QueryIfFocusUrl);

        try {
            JSONObject totalObject = (JSONObject)jsonArray.get(0);
            isFocus = totalObject.getBoolean("isFocus");  //是否收藏

            //Toast.makeText(DisplayDetail.this, "是否收藏" + isFocus.toString(), Toast.LENGTH_LONG).show();

            if(isFocus){
                overFocus();  //改变收藏图片状态为已经收藏
                focusId = totalObject.getInt("focusId");  //设置收藏Id
            }

        }catch (JSONException e){
            Toast.makeText(DisplayDetail.this, "initialize异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 改变为已点赞
     */
    public void overSupport(){
        Drawable drawableLeft = getResources().getDrawable(
                R.drawable.over_support);

        tv_info_support.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                null, null, null);
        tv_info_support.setCompoundDrawablePadding(4);

        tv_info_support.setText(support.toString());  //更新点赞数
    }


    /**
     * 改变为已收藏
     */
    public void overFocus(){
        btn_info_like.setImageResource(getResources().getIdentifier(
                "over_focus", "drawable",
                DisplayDetail.this.getPackageName()));
        isFocus = true;
    }

    /**
     * 改变为未收藏
     */
    public void nowNotLike(){
        btn_info_like.setImageResource(getResources().getIdentifier(
                "now_not_like", "drawable",
                DisplayDetail.this.getPackageName()));
        isFocus = false;
    }
    /**
     * 隐藏文章简述
     */
    public void hideDelete(){
        btn_info_delete.setVisibility(View.GONE);  //隐藏删除
    }

    /**
     * 显示文章简述
     */
    public void displayDelete(){
        btn_info_delete.setVisibility(View.VISIBLE);   //显示删除
    }

    /**
     * 隐藏文章简述
     */
    public void hideDescribe(){
        tv_info_describe.setVisibility(View.GONE);  //隐藏文章简述
    }

    /**
     * 显示文章简述
     */
    public void displayDescribe(){
        tv_info_describe.setVisibility(View.VISIBLE);   //显示文章简述
    }

    /**
     * 隐藏图片
     */
    public void hideImg(){
        share_detail_img.setVisibility(View.GONE);  //隐藏文章简述
    }

    /**
     * 显示图片
     */
    public void displayImg(){
        share_detail_img.setVisibility(View.VISIBLE);  //显示图片
    }

    /**
     * 对于文章和日记类的文章
     */
     public void setArticlesDiaries(){
         tv_info_describe.setText(infoDescribe);
         tv_info_detail.setText(infoDetail);
     }


    /**
     * 对于文章和日记类的文章
     */
    public void setImg(){
        share_img_title.setText(infoTitle);
        tv_info_detail.setText(infoDetail);

        Bitmap one;

        //通过网络链接获取图片
        try {
            one= LoadImgByNet.getBitmap(imgBaseUrl+infoDescribe.trim());
            share_detail_img.setBackground(new BitmapDrawable(getResources(),one));    //以背景的方式设置图片
        }catch(IOException e){
            e.printStackTrace();
        }

        hideDescribe();  //隐藏文章简述
        displayImg();  //显示图片
    }



    //初始化将详情设置到FindInfo bean中
    public List<CommentInfo> initSetDataToBean(String detail){
        List<CommentInfo> CommentInfo = new ArrayList<CommentInfo>();
        try {
            JSONArray detailJsonArray = new JSONArray(detail);
            for (int i = 0; i < detailJsonArray.length(); i++) {
                CommentInfo items = new CommentInfo();

                JSONObject temp = (JSONObject) detailJsonArray.get(i);

                int commentId = temp.getInt("commentId");   //评论的文章ID
                String commentUser = temp.getString("commentUser");    //用户昵称
                int commentInfo = temp.getInt("commentInfo");   //评论的文章ID
                String commentDetail = temp.getString("commentDetail");   //评论详情

                items.setCommentId(commentId);
                items.setInfoId(commentInfo);
                items.setCommentUser(commentUser);
                items.setCommentDetail(commentDetail);
                CommentInfo.add(items);
            }
            return CommentInfo;

        }catch (JSONException e){
            Toast.makeText(DisplayDetail.this, "initSetPeopleDataToBean异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * 根据返回的用户设置卡片的样式
     */

    class CommentAdapter extends BaseAdapter {

        List<CommentInfo> newsItems;

        public CommentAdapter(List<CommentInfo> newsitems){
            this.newsItems = newsitems;
        }

        @Override
        public int getCount() {
            return newsItems.size();
        }

        @Override
        public Object getItem(int position) {
            return newsItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        //在这里将Item设置到每个卡片
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.activity_comment_list, null);
            }

            //用户昵称
            TextView tvTitle = (TextView)view.findViewById(R.id.comment_user_name);
            tvTitle.setText(newsItems.get(position).getCommentUser());

            //用户ID
            TextView getTvId = (TextView)view.findViewById(R.id.comment_id);
            getTvId.setText(newsItems.get(position).getInfoId().toString());

            //评论内容
            TextView tvComment = (TextView)view.findViewById(R.id.comment_detail);
            tvComment.setText(newsItems.get(position).getCommentDetail());

            return view;
        }

        /**
         * 添加数据列表项
         * @param infoItem
         */
        public void addNewsItem(CommentInfo infoItem){
            newsItems.add(infoItem);
        }

    }

}
