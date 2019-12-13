package com.example.discover;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditTabFragment extends Fragment {
    RelativeLayout bt_share_funny,bt_share_pic,bt_share_music,bt_share_diary;   //四种类型的内容分享
    LinearLayout edit_detail_info_layout;
    EditText et_edit_title_info,et_edit_describe_info;   //标题和简述
    EditText et_edit_detail_info;  //分享的详情
    Button edit_sure_share;   //发布按钮_
    ImageButton back;    //退出应用
    ImageView share_funny_image_bc,share_pic_image_bc,share_music_image_bc,share_diary_image_bc;
    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源
    Integer infoType = 0;  //默认是分享趣事

    Activity mActivity;     //存放当前的activity
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View moreLayout = inflater.inflate(R.layout.edit_tab_content, container, false);
        return moreLayout;
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

    //绑定点击事件
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = this.getActivity();

        bt_share_funny = (RelativeLayout) getActivity().findViewById(R.id.bt_share_funny);   //分享趣事
        bt_share_pic = (RelativeLayout) getActivity().findViewById(R.id.bt_share_pic); //分享图片
        bt_share_music = (RelativeLayout) getActivity().findViewById(R.id.bt_share_music);  //分享音乐
        bt_share_diary = (RelativeLayout) getActivity().findViewById(R.id.bt_share_diary); //发布日记
        edit_detail_info_layout = (LinearLayout) getActivity().findViewById(R.id.edit_detail_info_layout);// 详情框

        et_edit_title_info = (EditText) getActivity().findViewById(R.id.et_edit_title_info); //分享标题
        et_edit_describe_info = (EditText) getActivity().findViewById(R.id.et_edit_describe_info); //分享简述
        et_edit_detail_info = (EditText) getActivity().findViewById(R.id.et_edit_detail_info); //分享简述

        edit_sure_share = (Button) getActivity().findViewById(R.id.edit_sure_share);   //确定分享

        share_funny_image_bc = (ImageView) getActivity().findViewById(R.id.share_funny_image_bc);
        share_pic_image_bc = (ImageView) getActivity().findViewById(R.id.share_pic_image_bc);
        share_music_image_bc = (ImageView) getActivity().findViewById(R.id.share_music_image_bc);
        share_diary_image_bc = (ImageView) getActivity().findViewById(R.id.share_diary_image_bc);

        //默认显示分享趣事
        changeEditDisplay(infoType);

        bt_share_funny.setOnClickListener(new btnClick());
        bt_share_pic.setOnClickListener(new btnClick());
        bt_share_music.setOnClickListener(new btnClick());
        bt_share_diary.setOnClickListener(new btnClick());
        edit_detail_info_layout.setOnClickListener(new btnClick());
        edit_sure_share.setOnClickListener(new okClick());

        back = (ImageButton)getActivity().findViewById(R.id.back_button3);
        back.setOnClickListener(new EditTabFragment.mOnClick());
    }

    class mOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mActivity.finish();
        }
    }

    //改变UI
    public void changeEditDisplay(int selectType) {
        if(selectType==0){
            setAllBc();
            setAllNone();
            share_funny_image_bc.setImageResource(R.drawable.share_funny_select);
            et_edit_title_info.setHint("在这里填写标题");
            et_edit_describe_info.setHint("简述你要分享的趣事");
            et_edit_detail_info.setHint("在这详细介绍趣事");
            edit_sure_share.setText("分享趣事");

        }else if(selectType==1){
            setAllBc();
            setAllNone();
            share_pic_image_bc.setImageResource(R.drawable.share_pic_select);
            et_edit_title_info.setHint("在这里填写标题");
            et_edit_describe_info.setHint("在这里填写图片链接：test1.jpeg");
            et_edit_detail_info.setHint("在这里填写图片详细介绍");
            edit_sure_share.setText("分享图片");
        }else if(selectType==2){
            setAllBc();
            setAllNone();
            share_music_image_bc.setImageResource(R.drawable.share_music_select);
            et_edit_title_info.setHint("在这里填写标题");
            et_edit_describe_info.setHint("在这里填写歌名");
            et_edit_detail_info.setHint("在这里填写音乐链接");
            edit_sure_share.setText("分享音乐");
        }else if(selectType==3){
            setAllBc();
            setAllNone();
            share_diary_image_bc.setImageResource(R.drawable.share_diary_select);
            et_edit_title_info.setHint("在这里填写标题");
            et_edit_describe_info.setHint("在这里填写今天的心情");
            et_edit_detail_info.setHint("在这里记录今天的事");
            edit_sure_share.setText("发布日记");
        }
    }

    //让所有的按钮变成灰色
    public void setAllBc(){
        share_funny_image_bc.setImageResource(R.drawable.share_funny_no_select);
        share_pic_image_bc.setImageResource(R.drawable.share_pic_no_select);
        share_music_image_bc.setImageResource(R.drawable.share_music_no_select);
        share_diary_image_bc.setImageResource(R.drawable.share_diary_no_select);
    }

    //清空所有文本输入框的值
    public void setAllNone(){
        et_edit_title_info.setText("");
        et_edit_describe_info.setText("");
        et_edit_detail_info.setText("");
    }

    //点击不同分享内容的时候改变UI
    class btnClick implements View.OnClickListener {
        public void onClick(View v) {
            if (v == bt_share_funny) {
                infoType = 0;
                changeEditDisplay(infoType);
            /*Toast.makeText(getActivity(), textView.getText(), Toast.LENGTH_LONG).show();*/
            }else if(v == bt_share_pic){
                infoType = 1;
                changeEditDisplay(infoType);
            }else if(v == bt_share_music){
                infoType = 2;
                changeEditDisplay(infoType);
            }else if(v == bt_share_diary){
                infoType = 3;
                changeEditDisplay(infoType);
            }else if(v == edit_detail_info_layout){
                et_edit_detail_info.setFocusable(true);
                et_edit_detail_info.setFocusableInTouchMode(true);

            }

        }
    }

    //点击不同分享内容的时候改变UI
    class okClick implements View.OnClickListener {
        public void onClick(View v) {
            SharedPreferences sp = mActivity.getSharedPreferences("save", Context.MODE_PRIVATE);
            Boolean isLogin = sp.getBoolean("isLogin",false);

            if(isLogin){
                setVersion();
                String userName = sp.getString("name","");
                String title = et_edit_title_info.getText().toString();
                String describe = et_edit_describe_info.getText().toString();
                String str = et_edit_detail_info.getText().toString();
                String detail=str.replaceAll("\n", "<br>");

                Boolean boo = title.length()>0&&describe.length()>0&&detail.length()>0;

                if(boo){
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader buffer = null;
                    HttpURLConnection connGET = null;
                    try {
                        String registerUrl = baseUrl+"AddInfo?infoTitle="+title+"&infoDescribe="+describe+"&infoDetail="+detail +"&type="+infoType+"&infoAuthor="+userName;
                        URL url = new URL(registerUrl);
                        connGET = (HttpURLConnection) url.openConnection();
                        connGET.setConnectTimeout(5000);
                        connGET.setRequestMethod("GET");
                        if (connGET.getResponseCode() == 200) {
                            buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                            for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                                stringBuilder.append(s);
                            }
                            setAllNone();
                            Toast.makeText(getActivity(), stringBuilder, Toast.LENGTH_LONG).show();
                            //Toast.makeText(getActivity(), detail, Toast.LENGTH_LONG).show();
                            buffer.close();
                        }else{
                            Toast.makeText(getActivity(), "非200.."+connGET.getResponseCode(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "get 提交 err.." + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "清将信息填写完整!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getActivity(), "清先登录!", Toast.LENGTH_LONG).show();
            }

        }
    }
}
