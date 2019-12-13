package com.example.discover;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bean.FindInfo;
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
import java.util.concurrent.ScheduledExecutorService;

public class DiscoverTabFragment extends Fragment implements AbsListView.OnScrollListener{
    Activity mActivity;     //存放当前的activity
    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源

    private TextView title;
    ImageButton back;
    private DiscoverTabFragment.PaginationAdapter adapterAction;
    private ScheduledExecutorService scheduledExecutorService;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View discoverLayout = inflater.inflate(R.layout.discover_tab_content, container, false);
        return discoverLayout;
    }

    private ListView listView;
    private int page = 1; //请求页数
    private int count = 10; //每次请求的数量
    private int visibleLastIndex = 0;  //最后的可视项索引
    private int visibleItemCount;    // 当前窗口可见项总数
    private int dataSize = 28;     //数据集的条数
    private DiscoverTabFragment.PaginationAdapter adapter;
    private View loadMoreView;
    private Button loadMoreButton;
    private Handler handler = new Handler();

    //返回测试信息
    /*TextView testTxt;*/
    public  void refreshDiscover(){
        page = 1;
        visibleLastIndex = 0;
        listView.setAdapter(null);   //将ListView控件的内容清空
        initializeAdapter();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /** Called when the activity is first created. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = this.getActivity();
        /*setContentView(R.layout.main);*/
        loadMoreView = getLayoutInflater().inflate(R.layout.activity_find_loadmore, null);
        loadMoreButton = (Button)loadMoreView.findViewById(R.id.loadMoreButton);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadMoreButton.setText("正在加载中...");  //设置按钮文字
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadMoreData();
                        adapter.notifyDataSetChanged();
                        loadMoreButton.setText("查看更多..."); //恢复按钮文字
                    }
                },2000);

            }
        });

        //返回测试信息
       /* testTxt = (TextView)getActivity().findViewById(R.id.test_discover_tab);*/
        back = (ImageButton)getActivity().findViewById(R.id.back_button);
        back.setOnClickListener(new mOnClick());

        listView = (ListView)getActivity().findViewById(R.id.discover_listView);
        listView.addFooterView(loadMoreView);  //设置列表底部视图
        initializeAdapter();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView getTvId = (TextView)view.findViewById(R.id.cardId);
                /*Toast.makeText(mActivity,"点击了"+getTvId.getText().toString()+"id", Toast.LENGTH_SHORT).show();*/
                SharedPreferences sp = mActivity.getSharedPreferences("save", Context.MODE_PRIVATE);
                Boolean isLogin = sp.getBoolean("isLogin",false);

                if(isLogin){
                    //点击了进入详情
                    Intent display_info_intent = new Intent();
                    display_info_intent.setClass(mActivity, DisplayDetail.class);  //创建intent对象，并制定跳转页面
                    display_info_intent.putExtra("infoId",getTvId.getText().toString());
                    mActivity.setResult(1, display_info_intent); //这里的1就对应到onActivityResult（）方法中的resultCode
                    startActivity(display_info_intent);    //跳转到详情页面
                }else{
                    //尚未登录
                    Toast.makeText(getActivity(),"请先登录", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    class mOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
           mActivity.finish();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = adapter.getCount()-1; //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && visibleLastIndex == lastIndex) {
            // 如果是自动加载,可以在这里放置异步加载数据的代码
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //TODO now visible to user
        } else {
            refreshDiscover();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;  //最后的可视项索引

        Log.e("firstVisibleItem = ",firstVisibleItem+"");
        Log.e("visibleItemCount = ",visibleItemCount+"");
        Log.e("totalItemCount = ",totalItemCount+"");

        //如果所有的记录选项等于数据集的条数，则移除列表底部视图
        if(totalItemCount == dataSize+1){
            listView.removeFooterView(loadMoreView);
            Toast.makeText(getActivity(),  "数据全部加载完!", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 查询数据库中的数据
     */
    private JSONArray loadDataFromDataBase(){

        //Toast.makeText(getActivity(),"保存",Toast.LENGTH_SHORT).show();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader buffer = null;
        HttpURLConnection connGET = null;

        try {
            String discoverUrl = baseUrl+"QueryDiscover?page="+page+"&count="+count;
            URL url = new URL(discoverUrl);
            connGET = (HttpURLConnection) url.openConnection();
            connGET.setConnectTimeout(5000);
            connGET.setRequestMethod("GET");
            if (connGET.getResponseCode() == 200) {
                buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                    stringBuilder.append(s);
                }
                if(stringBuilder.toString()!="null") {
                    //返回测试信息
                    JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                    /*   testTxt.setText(baseUrl+"QueryDiscover?page="+page+"&count="+count);*/
                    //获取到的数据，对Json进行解析
                    page = page + 1;  //一次成功请求后更新请求页面
                    buffer.close();
                    return jsonArray;
                }else{
                    return null;
                }
            }else{
                Toast.makeText(getActivity(),"非200", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "get 提交 err.." + e.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    //初始化将详情设置到FindInfo bean中
    public List<FindInfo> initSetDataToBean(String detail){
        List<FindInfo> findInfo = new ArrayList<FindInfo>();
        try {
            JSONArray detailJsonArray = new JSONArray(detail);
            for (int i = 0; i < detailJsonArray.length(); i++) {
                FindInfo items = new FindInfo();

                JSONObject temp = (JSONObject) detailJsonArray.get(i);

                Integer infoId = temp.getInt("infoId");    //内容ID
                String infoTitle = temp.getString("infoTitle");   //内容标题
                String infoDescribe = temp.getString("infoDescribe");   //内容简述
                String infoDetail = temp.getString("infoDetail");   //内容详情
                Integer type = temp.getInt("infoType");    //类型：0表示日记，1表示趣事
                Integer support = temp.getInt("infoSupport");   //点赞数
                String infoAuthor = temp.getString("infoAuthor");  //作者

                items.setInfoId(infoId);
                items.setInfoTitle(infoTitle);
                items.setInfoDescribe(infoDescribe);
                items.setInfoDetail(infoDetail);
                items.setType(type);
                items.setSupport(support);
                items.setInfoAuthor(infoAuthor);

                findInfo.add(items);
            }
            return findInfo;

        }catch (JSONException e){
            Toast.makeText(getActivity(), "initSetDataToBean异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }


    //加载更多将详情设置到FindInfo bean中
    public void loadMoreSetDataToBean(String detail){
        try {
            JSONArray detailJsonArray = new JSONArray(detail);

            for (int i = 0; i < detailJsonArray.length(); i++) {
                FindInfo items = new FindInfo();
                JSONObject temp = (JSONObject) detailJsonArray.get(i);
                Integer infoId = temp.getInt("infoId");    //内容ID
                String infoTitle = temp.getString("infoTitle");   //内容标题
                String infoDescribe = temp.getString("infoDescribe");   //内容简述
                String infoDetail = temp.getString("infoDetail");   //内容详情

                Integer type = temp.getInt("infoType");    //类型：0表示日记，1表示趣事
                Integer support = temp.getInt("infoSupport");   //点赞数
                String infoAuthor = temp.getString("infoAuthor");  //作者

                items.setInfoId(infoId);
                items.setInfoTitle(infoTitle);
                items.setInfoDescribe(infoDescribe);
                items.setInfoDetail(infoDetail);
                items.setType(type);
                items.setSupport(support);
                items.setInfoAuthor(infoAuthor);

                adapter.addNewsItem(items);   //与初始化是有差异的
            }

        }catch (JSONException e){
            Toast.makeText(getActivity(), "loadMoreSetDataToBean异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
        }

    }
    /**
     * 初始化ListView的适配器，即打开页面展示的数据
     */
    private void initializeAdapter(){
        // 设置线程策略
        setVersion();
        JSONArray jsonArray = loadDataFromDataBase();

        if(jsonArray!=null){
            try {
                JSONObject totalObject = (JSONObject)jsonArray.get(0);

                dataSize = totalObject.getInt("totalRecord");  //总记录数
                String detail= totalObject.getString("RecordDetail");   //详情

                if(initSetDataToBean(detail)!=null) {
                    adapter = new PaginationAdapter(initSetDataToBean(detail));
                }
            }catch (JSONException e){
                Toast.makeText(getActivity(), "initializeAdapter异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            listView.removeFooterView(loadMoreView);
            Toast.makeText(mActivity, "查询为空" , Toast.LENGTH_LONG).show();
        }
    }

    //APP如果在主线程中请求网络操作，将会抛出异常，所以需要用线程来操作网络请求
    void setVersion() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() //探测SQLite数据库操作
                .penaltyLog() //打印logcat
                .penaltyDeath()
                .build());
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData(){
        JSONArray jsonArray = loadDataFromDataBase();   //从服务器获取资源

        try {
            JSONObject total = (JSONObject) jsonArray.get(0);
            dataSize = total.getInt("totalRecord");  //总记录数
            String detail= total.getString("RecordDetail");   //详情

            loadMoreSetDataToBean(detail);   //将更多的详情设置到bean中
        }catch (JSONException e){
            Toast.makeText(getActivity(), "loadMoreData()异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 将一组数据传到ListView等UI显示组件
     */
    class PaginationAdapter extends BaseAdapter {

        List<FindInfo> newsItems;

        public PaginationAdapter(List<FindInfo> newsitems){
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
                view = getLayoutInflater().inflate(R.layout.activity_find_list_item, null);
            }

            //标题
            TextView tvTitle = (TextView)view.findViewById(R.id.cardTitle);
            tvTitle.setText(newsItems.get(position).getInfoTitle());

            //文章ID
            TextView tvId = (TextView)view.findViewById(R.id.cardId);
            tvId.setText(newsItems.get(position).getInfoId().toString());

            int type = newsItems.get(position).getType();

            if(type==0||type==3){
                ImageView cardImgTip = (ImageView)view.findViewById(R.id.cardImgTip);
                if(type==0){
                    cardImgTip.setImageResource(R.drawable.share_funny_select);
                }else{
                    cardImgTip.setImageResource(R.drawable.share_diary_select);
                }

                TextView tvContent = (TextView)view.findViewById(R.id.cardContent);
                tvContent.setVisibility(View.VISIBLE);  //显示文字框

                ImageView ivContent = (ImageView)view.findViewById(R.id.cardContent_pic);
                ivContent.setVisibility(View.GONE);   //设置图片显示框隐藏

                LinearLayout layoutMusic = (LinearLayout) view.findViewById(R.id.cardContent_music);
                layoutMusic.setVisibility(View.GONE);   //设置音乐显示框隐藏

                //分享的普通内容

                tvContent.setText(newsItems.get(position).getInfoDescribe());
            }else if(type==1){
                ImageView cardImgTip = (ImageView)view.findViewById(R.id.cardImgTip);
                cardImgTip.setImageResource(R.drawable.share_pic_select);
                //分享图片
                TextView tvContent = (TextView)view.findViewById(R.id.cardContent);
                tvContent.setVisibility(View.GONE);  //隐藏文字框
                LinearLayout layoutMusic = (LinearLayout) view.findViewById(R.id.cardContent_music);
                layoutMusic.setVisibility(View.GONE);   //设置音乐显示框隐藏

                ImageView ivContent = (ImageView)view.findViewById(R.id.cardContent_pic);
                //通过网络链接获取图片
                Bitmap one;
                String describeUrl = newsItems.get(position).getInfoDescribe().trim();
                try {
                    one= LoadImgByNet.getBitmap(imgBaseUrl+describeUrl);  //设置图片
                    ivContent.setImageBitmap(one);
                }catch(IOException e){
                    e.printStackTrace();
                }
                ivContent.setVisibility(View.VISIBLE);   //设置图片显示框

            }else if(type==2){
                ImageView cardImgTip = (ImageView)view.findViewById(R.id.cardImgTip);
                cardImgTip.setImageResource(R.drawable.share_music_select);

                //分享的音乐
                TextView tvContent = (TextView)view.findViewById(R.id.cardContent);
                tvContent.setVisibility(View.GONE);  //显示文字框
                ImageView ivContent = (ImageView)view.findViewById(R.id.cardContent_pic);
                ivContent.setVisibility(View.GONE);   //设置图片显示框隐藏

                LinearLayout layoutMusic = (LinearLayout) view.findViewById(R.id.cardContent_music);

                TextView tvMusicContent = (TextView)view.findViewById(R.id.cardTitle_music);
                tvMusicContent.setText(newsItems.get(position).getInfoDescribe());

                layoutMusic.setVisibility(View.VISIBLE);   //设置音乐显示框显示
            }
            return view;
        }

        /**
         * 添加数据列表项
         * @param infoItem
         */
        public void addNewsItem(FindInfo infoItem){
            newsItems.add(infoItem);
        }

    }
}
