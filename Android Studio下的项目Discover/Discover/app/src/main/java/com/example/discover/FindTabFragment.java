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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bean.FindInfo;
import com.example.bean.FindPeople;
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

public class FindTabFragment extends Fragment implements AbsListView.OnScrollListener{
    Activity mActivity;     //存放当前的activity
    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View findLayout = inflater.inflate(R.layout.find_tab_content, container, false);
        return findLayout;
    }
    private ListView listView;
    private EditText searchView;  //搜索内容框
    private Button clearInfo;  //找文按钮
    private Button seekPeople;  //找人按钮
    private Button seekInfo;  //找文按钮
    private TextView hintTv;  //提示信息
    ImageButton back;  //退出应用


    private String keyWord;  //搜索框的文
    private Boolean isHot = true;  //用于判断加载热文还是查找的内容
    private int page = 1; //请求页数
    private int visibleLastIndex = 0;  //最后的可视项索引
    private int visibleItemCount;    // 当前窗口可见项总数
    private int dataSize = 28;     //数据集的条数
    private FindTabFragment.PaginationAdapter adapter;   //存放热门和查文信息
    private FindTabFragment.PeopleAdapter peopleAdapter;   //存放查人
    private Handler handler = new Handler();

    //返回测试信息
    /*TextView testTxt;*/

    /** Called when the activity is first created. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = this.getActivity();

        back = (ImageButton)getActivity().findViewById(R.id.back_button2);
        back.setOnClickListener(new FindTabFragment.mOnClick());

        //返回测试信息
        /* testTxt = (TextView)getActivity().findViewById(R.id.test_discover_tab);*/

        listView = (ListView)getActivity().findViewById(R.id.find_listView);
        initializeAdapter();   //初始化，填入热门
        listView.setAdapter(adapter);

        //点击进入详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView getTvId = (TextView)view.findViewById(R.id.seek_list_cardId);
                if(getTvId.length()!=0){
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
            }
        });

        //输入框
        searchView = (EditText)getActivity().findViewById(R.id.et_seek_search);

        //找人、找文按钮、清空文本框内容的button
        seekPeople = (Button) getActivity().findViewById(R.id.find_search_by_name);
        seekInfo = (Button) getActivity().findViewById(R.id.find_search_by_keyWord);
        clearInfo = (Button) getActivity().findViewById(R.id.seek_bt_clear);

        //提示信息
        hintTv = (TextView) getActivity().findViewById(R.id.seek_list_hint_info);

        seekPeople.setOnClickListener(new mClick());
        seekInfo.setOnClickListener(new mClick());
        clearInfo.setOnClickListener(new mClick());
    }

    class mOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mActivity.finish();
        }
    }

    class mClick implements View.OnClickListener {

        public void onClick(View v) {
            keyWord = searchView.getText().toString();

            if(keyWord == ""){
                Toast.makeText(getActivity(), "查找内容不能为空！", Toast.LENGTH_LONG).show();
                return;
            }

            if (v == seekPeople) {
                //点击了找人
                hintTv.setText("查询结果：");
                listView.setAdapter(null);   //将ListView控件的内容清空
                initSearchPeopleAdapter();
                listView.setAdapter(peopleAdapter);   //将查询结果插入页面

                peopleAdapter.notifyDataSetChanged();
            }else if(v == seekInfo){
                //点击了找文
                isHot = false;
                hintTv.setText("查询结果：");
                listView.setAdapter(null);
                initSearchInfoAdapter();
                listView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }else if(v == clearInfo){
                //点击了清空
                isHot = true;
                hintTv.setText("大家都在看：");

                listView.setAdapter(null);
                initializeAdapter();   //初始化，填入热门
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                searchView.setText("");
            }

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
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;  //最后的可视项索引

        //如果所有的记录选项等于数据集的条数，则移除列表底部视图
        if(totalItemCount == dataSize+1){
            Toast.makeText(getActivity(),  "数据全部加载完!", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 查询数据库中的数据
     */
    private JSONArray loadDataFromDataBase(String QueryInfoUrl){

        //Toast.makeText(getActivity(),"保存",Toast.LENGTH_SHORT).show();
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
                if(stringBuilder.toString()!="null"){
                    //返回测试信息
                    JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                    /*   testTxt.setText(baseUrl+"QueryDiscover?page="+page+"&count="+count);*/
                    //获取到的数据，对Json进行解析
                    page = page+1;  //一次成功请求后更新请求页面
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

    //初始化将详情设置到FindInfo bean中
    public List<FindPeople> initSetPeopleDataToBean(String detail){
        List<FindPeople> findPeople = new ArrayList<FindPeople>();
        try {
            JSONArray detailJsonArray = new JSONArray(detail);
            for (int i = 0; i < detailJsonArray.length(); i++) {
                FindPeople items = new FindPeople();

                JSONObject temp = (JSONObject) detailJsonArray.get(i);

                String userName = temp.getString("userName");    //用户昵称
                String signature = temp.getString("signature");   //用户签名
                String userLogImage = temp.getString("userLogImage");   //用户头像

                items.setUserName(userName);
                items.setSignature(signature);
                items.setUserLogImage(userLogImage);
                findPeople.add(items);
            }
            return findPeople;

        }catch (JSONException e){
            Toast.makeText(getActivity(), "initSetPeopleDataToBean异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * 初始化ListView的适配器，即打开页面展示的数据
     */
    private void initializeAdapter(){
        // 设置线程策略
        setVersion();
        String QueryHotInfoUrl = baseUrl+"QueryHotInfo";
        JSONArray jsonArray = loadDataFromDataBase(QueryHotInfoUrl);

        if(jsonArray!=null) {
            try {
                JSONObject totalObject = (JSONObject) jsonArray.get(0);

                dataSize = totalObject.getInt("totalRecord");  //总记录数
                String detail = totalObject.getString("RecordDetail");   //详情

                if (initSetDataToBean(detail) != null) {
                    adapter = new PaginationAdapter(initSetDataToBean(detail));   //将详情设置到bean中
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "initializeAdapter异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "查询为空" , Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 初始化ListView的适配器，将查找文结果显示出来
     */
    private void initSearchInfoAdapter(){
        // 设置线程策略
        setVersion();
        String QueryHotInfoUrl = baseUrl+"QueryInfoByKey?key="+keyWord;
        JSONArray jsonArray = loadDataFromDataBase(QueryHotInfoUrl);

        if(jsonArray!=null) {
            try {
                JSONObject totalObject = (JSONObject) jsonArray.get(0);

                dataSize = totalObject.getInt("totalRecord");  //总记录数
                String detail = totalObject.getString("RecordDetail");   //详情

                if (initSetDataToBean(detail) != null) {
                    adapter = new PaginationAdapter(initSetDataToBean(detail));   //将详情设置到bean中
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "initializeAdapter异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "查询为空" , Toast.LENGTH_LONG).show();
        }

    }
    /**
     * 初始化ListView的适配器，将查找人结果显示出来
     */
    private void initSearchPeopleAdapter(){
        // 设置线程策略
        setVersion();
        String QueryPeopleInfoUrl = baseUrl+"QueryPeopleInfoByKey?nameKey="+keyWord;
        JSONArray jsonArray = loadDataFromDataBase(QueryPeopleInfoUrl);

        if(jsonArray!=null) {
            try {
                JSONObject totalObject = (JSONObject) jsonArray.get(0);

                dataSize = totalObject.getInt("totalRecord");  //总记录数
                String detail = totalObject.getString("RecordDetail");   //详情

                if (initSetPeopleDataToBean(detail) != null) {
                    peopleAdapter = new PeopleAdapter(initSetPeopleDataToBean(detail));   //将详情设置到bean中
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "initializeAdapter异常 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "查询为空" , Toast.LENGTH_LONG).show();
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
     * 将Int转为String
     */
    private  String IntToString(Integer num){
        try {
            String str = num.toString();    //第几页
            return str;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据返回的文章类型设置卡片的样式
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
                view = getLayoutInflater().inflate(R.layout.activity_seek_list_item, null);
            }
            //将热门标识去掉
            if(!isHot){
                TextView textViewImgBc = (TextView)  view.findViewById(R.id.seek_list_textView);
                textViewImgBc.setCompoundDrawables(null, null, null, null);
            }

            //内容标题
            TextView tvTitle = (TextView)view.findViewById(R.id.seek_list_textView);
            tvTitle.setText(newsItems.get(position).getInfoTitle());

            //内容id
            TextView getTvId = (TextView)view.findViewById(R.id.seek_list_cardId);
            getTvId.setText(newsItems.get(position).getInfoId().toString());

            //内容点赞数
            TextView tvSupport = (TextView)view.findViewById(R.id.seek_list_textView2);
            String num = IntToString(newsItems.get(position).getSupport());
            tvSupport.setText(num);

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

    /**
     * 根据返回的用户设置卡片的样式
     */

    class PeopleAdapter extends BaseAdapter {

        List<FindPeople> newsItems;

        public PeopleAdapter(List<FindPeople> newsitems){
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
                view = getLayoutInflater().inflate(R.layout.activity_seek_list_item, null);
            }

            //用户昵称
            TextView tvTitle = (TextView)view.findViewById(R.id.seek_list_textView);
            tvTitle.setText(newsItems.get(position).getUserName());

            //这是用户，内容id为空
            TextView getTvId = (TextView)view.findViewById(R.id.seek_list_cardId);
            getTvId.setText("");

            //用户签名
            TextView tvSupport = (TextView)view.findViewById(R.id.seek_list_textView2);
            tvSupport.setText(newsItems.get(position).getSignature());

            //用户头像
            //通过网络链接获取图片
            String imgUrl = newsItems.get(position).getUserLogImage();
            Bitmap one;
            ImageView userImg = (ImageView) view.findViewById(R.id.seek_list_imgView);

            try {
                one= LoadImgByNet.getBitmap(imgBaseUrl+imgUrl);
                userImg.setImageBitmap(one);
            }catch(IOException e){
                e.printStackTrace();
            }
            TextView textViewImgBc = (TextView)  view.findViewById(R.id.seek_list_textView);
            textViewImgBc.setCompoundDrawables(null, null, null, null);
          /*TableRow userImg = (TableRow) view.findViewById(R.id.seek_list_tableRow);
          String imgUrl = newsItems.get(position).getUserLogImage();
            Bitmap one;
            try {
                one= LoadImgByNet.getBitmap(imgBaseUrl+imgUrl);
                *//*userImg.setBackground(new BitmapDrawable(getResources(),one));    //以背景的方式设置头像*//*

                Drawable drawable = new BitmapDrawable(getResources(),one);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(drawable.getMinimumWidth(), 0,0 , drawable.getMinimumHeight());
                textViewImgBc.setCompoundDrawables(drawable, null, null, null);

            }catch(IOException e){
                e.printStackTrace();
            }*/
            return view;
        }

        /**
         * 添加数据列表项
         * @param infoItem
         */
        public void addNewsItem(FindPeople infoItem){
            newsItems.add(infoItem);
        }

    }
}
