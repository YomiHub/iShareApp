package com.example.discover;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyTabFragment extends Fragment{
    TableRow account_set_bt,signature_set_bt,password_set_bt,share_list_bt,diary_list_bt,focus_list_bt,about_iShare;   //相关功能
    View myLayout;  //“我的”页面
    Button pop_register,pop_login;    //弹出对话框的按钮
    ImageView my_head_img;   //用户头像
    TextView my_name,my_sign;   //用户昵称和心情

    /*Button go_login,go_register;*/
    String userName,passWord,signature,headImg;   //存放用户基本信息
    RelativeLayout login,register;    //注册、登录页面
    LinearLayout login_top,un_login_top;    //“我的”页面顶部两种状态下的布局
    Activity mActivity;     //存放当前的activity
    /*TextView test3;  //用于返回测试信息*/

    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源

    boolean isLogin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            myLayout = inflater.inflate(R.layout.my_tab_content, container, false);
        return myLayout;
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
        // 设置线程策略
        setVersion();
        login_top = ( LinearLayout)getActivity().findViewById(R.id.login_top);   //“我的”页面顶部登录后显示的用户信息
        un_login_top = (LinearLayout)getActivity().findViewById(R.id.un_login_top); //“我的”页面未登录时显示的登录、注册按钮

        my_head_img = ( ImageView) getActivity().findViewById(R.id.images_head);   //我的头像
        my_name = (TextView) getActivity().findViewById(R.id.t_name); //我的昵称
        my_sign = (TextView) getActivity().findViewById(R.id.t_sign); //我的签名

        account_set_bt =(TableRow)getActivity().findViewById(R.id.more_page_row1);   //"我的"界面第一个功能，账号管理
        account_set_bt.setOnClickListener(new mClick());


        signature_set_bt =(TableRow)getActivity().findViewById(R.id.more_page_row2);   //"我的"界面第二个功能，修改签名
        signature_set_bt.setOnClickListener(new mClick());

        password_set_bt =(TableRow)getActivity().findViewById(R.id.more_page_row3);   //"我的"界面第三个功能，修改密码
        password_set_bt.setOnClickListener(new mClick());

        share_list_bt =(TableRow)getActivity().findViewById(R.id.more_page_row4);   //"我的"界面第四个功能，我的分享
        share_list_bt.setOnClickListener(new mClick());

        diary_list_bt =(TableRow)getActivity().findViewById(R.id.more_page_row5);   //"我的"界面第五个功能，我的日记
        diary_list_bt.setOnClickListener(new mClick());

        focus_list_bt =(TableRow)getActivity().findViewById(R.id.more_page_row6);   //"我的"界面第六个功能，我的日记
        focus_list_bt.setOnClickListener(new mClick());

        about_iShare = (TableRow)getActivity().findViewById(R.id.more_page_row7);   //"我的"界面第七个功能，关于iShare
        about_iShare.setOnClickListener(new mClick());

        pop_register = (Button)getActivity().findViewById(R.id.pop_register);   //弹出注册对话框的按钮
        pop_login = (Button)getActivity().findViewById(R.id.pop_login);    //弹出登录对话框的按钮
       /* test3 = (TextView) getActivity().findViewById(R.id.test3_info);   //测试*/

        //根据登录状态显示不同的UI ，用SharePreferences判断用户是否登录
        changeStatus();

        pop_register.setOnClickListener(new popClick());    //弹出注册登录对话框事件
        pop_login.setOnClickListener(new popClick());

    }

    //“我的”页面中相关点击事件
    class mClick implements View.OnClickListener {

        public void onClick(View v) {
            if(isLogin) {
                if (v == account_set_bt) {
                    //点击了账号管理
                    Intent account_manage_intent = new Intent();
                    account_manage_intent.setClass(mActivity, AccountManagement.class);  //创建intent对象，并制定跳转页面
                    startActivity(account_manage_intent);    //跳转到账号管理页面

                /*TextView textView = (TextView) getActivity().findViewById(R.id.test_text_view);  //用于放测试的提示信息
                Toast.makeText(getActivity(), textView.getText(), Toast.LENGTH_LONG).show();*/
                }else if(v == signature_set_bt){
                    //点击了更改签名
                    Intent signature_manage_intent = new Intent();
                    signature_manage_intent.setClass(mActivity, ChangeSignature.class);  //创建intent对象，并指定跳转页面
                    //startActivity(signature_manage_intent);    //跳转到更新签名页面
                    startActivityForResult(signature_manage_intent,1);   //需要返回该页面
                }else if(v == password_set_bt){
                    //点击了更改密码
                    Intent password_manage_intent = new Intent();
                    password_manage_intent.setClass(mActivity, ChangePassword.class);  //创建intent对象，并指定跳转页面
                    /*startActivity(password_manage_intent);   */
                    startActivityForResult(password_manage_intent,2);   // 跳转到修改密码页面
                }else if(v == share_list_bt){
                    //点击了我的分享
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MyShareList.class);  //创建intent对象，并指定跳转页面
                    intent.putExtra("requestType",0);
                    mActivity.setResult(4, intent); //这里的4就对应到onActivityResult（）方法中的resultCode
                    startActivity(intent);    //跳转到list
                }else if(v==diary_list_bt){
                    //点击了我的日记
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MyShareList.class);  //创建intent对象，并指定跳转页面
                    intent.putExtra("requestType",1);
                    mActivity.setResult(5, intent); //这里的4就对应到onActivityResult（）方法中的resultCode
                    startActivity(intent);    //跳转到list
                }else if(v==focus_list_bt){
                    //点击了我的收藏
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MyShareList.class);  //创建intent对象，并指定跳转页面
                    intent.putExtra("requestType",2);
                    mActivity.setResult(6, intent); //这里的4就对应到onActivityResult（）方法中的resultCode
                    startActivity(intent);    //跳转到list
                } else if(v==about_iShare){
                    //点击了关于iShare
                    Intent about_iShare_intent = new Intent();
                    about_iShare_intent.setClass(mActivity, AboutIShare.class);  //创建intent对象，并指定跳转页面
                    startActivity(about_iShare_intent);    //跳转到iShare介绍页面
                }
            }else{

                Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
            }
        }
    }

    //登录、注册弹框
    class popClick implements View.OnClickListener {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        public void onClick(View v) {

            if (v == pop_register) {
                register= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_register, null);  //从另外的布局关联组件
                builder.setTitle("用户注册");
                builder.setTitle("i 分享").setView(register);
                builder.setNegativeButton("取消", new exitClick());
                /*dialog.setPositiveButton("注册", new registerClick());*/
                builder.setPositiveButton("注册", null);   //不监听，防止被关闭
                builder.setIcon(R.drawable.app_icon);
                final AlertDialog dialog = builder.create();
                dialog.show();
                //不会自动关闭对话框，可手动关闭
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(),"保存",Toast.LENGTH_SHORT).show();
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader buffer = null;
                        HttpURLConnection connGET = null;

                        EditText txtUserName,txtUserPass1,txtUserPass2,txtSignature;
                        TextView test1;
                        String passWord2;

                        txtUserName = (EditText)register.findViewById(R.id.et_username);
                        txtUserPass1 = (EditText)register.findViewById(R.id.et_password1);
                        txtUserPass2 = (EditText)register.findViewById(R.id.et_password2);
                        txtSignature= (EditText)register.findViewById(R.id.et_signature);
                        test1 = (TextView)register.findViewById(R.id.test1_view_info);

                        userName = txtUserName.getText().toString();
                        passWord = txtUserPass1.getText().toString();
                        passWord2 = txtUserPass2.getText().toString();
                        signature = txtSignature.getText().toString();

                        //从密码框中取值比较
                        if(!passWord.equals(passWord2)){
                            test1.setText("两次密码不一致！");
                            Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            String registerUrl = baseUrl+"Register?username="+userName+"&password="+passWord+"&signature="+signature;
                            URL url = new URL(registerUrl);
                            connGET = (HttpURLConnection) url.openConnection();
                            connGET.setConnectTimeout(5000);
                            connGET.setRequestMethod("GET");
                            if (connGET.getResponseCode() == 200) {
                                buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                                    stringBuilder.append(s);
                                }
                                test1.setText(stringBuilder);
                                dialog.dismiss();   //注册成功，手动关闭对话框
                                buffer.close();
                            }else{
                                test1.setText("非200.."+connGET.getResponseCode() );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            test1.setText("get 提交 err.." + e.toString());
                        }
                        Toast.makeText(getActivity(), test1.getText(), Toast.LENGTH_LONG).show();

                    }

                });
            } else if (v == pop_login) {
                login = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_login, null);  //从另外的布局关联组件
                builder.setTitle("i 分享").setView(login);
                builder.setNegativeButton("取消", new exitClick());
                builder.setPositiveButton("登录", null);
                builder.setIcon(R.drawable.app_icon);
                final AlertDialog dialog2 = builder.create();
                dialog2.show();
                //不会自动关闭对话框，可手动关闭
                dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(),"保存",Toast.LENGTH_SHORT).show();
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader buffer = null;
                        HttpURLConnection connGET = null;

                        EditText txtUserName2,txtUserPass2;
                        TextView test2;

                        txtUserName2 = (EditText)login.findViewById(R.id.et_login_username);
                        txtUserPass2 = (EditText)login.findViewById(R.id.et_login_password);
                        test2 = (TextView)login.findViewById(R.id.test2_view_info);


                        userName = txtUserName2.getText().toString();
                        passWord = txtUserPass2.getText().toString();
                        //从密码框中取值比较
                        if(userName.length()==0||passWord.length()==0){
                            test2.setText("用户名和密码不能为空!");
                            Toast.makeText(getActivity(), "用户名和密码不能为空!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            String LoginUrl = baseUrl+"Login?username="+userName+"&password="+passWord;
                            URL url = new URL(LoginUrl);
                            connGET = (HttpURLConnection) url.openConnection();
                            connGET.setConnectTimeout(5000);
                            connGET.setRequestMethod("GET");
                            if (connGET.getResponseCode() == 200) {
                                buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                                for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                                    stringBuilder.append(s);
                                }
                                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                                JSONObject userObject = (JSONObject)jsonArray.get(0);
                                userName = userObject.getString("userName");
                                signature = userObject.getString("signature");
                                headImg = userObject.getString("userLogImage");

                                loginSuccess(userName,signature,headImg);

                                dialog2.dismiss();   //登录成功，手动关闭对话框
                                buffer.close();
                            }else{
                                test2.setText("登录失败"+connGET.getResponseCode());
                                Toast.makeText(getActivity(), "非200.."+connGET.getResponseCode(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            test2.setText("登录失败.." + e.toString());
                            Toast.makeText(getActivity(), "get 提交 err.." + e.toString(), Toast.LENGTH_LONG).show();
                        }

                        //dialog2.dismiss()
                    }

                });
            }
        }
    }

    //登录成功后的界面以及状态更改
    public void loginSuccess(String name,String sign,String headImg){
        //登录成功后更改登录状态为已登录
        SharedPreferences sp = mActivity.getSharedPreferences("save", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", true);
        editor.putString("name", name);
        editor.putString("sign", sign);
        editor.putString("headImg", headImg);
        editor.commit();
        Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_LONG).show();
        //UI更新
        changeStatus();
    }


    //根据状态显示或隐藏“我的”页面中头部信息
    public void changeStatus(){
        //获取最新的登录状态，SharePreferences为永久存储，需要手动退出
        SharedPreferences sp = mActivity.getSharedPreferences("save", Context.MODE_PRIVATE);
        isLogin = sp.getBoolean("isLogin",false);

        if(isLogin){
            Bitmap one;
            userName = sp.getString("name","");
            signature = sp.getString("sign","");
            headImg = sp.getString("headImg","");
            //设置用户昵称、头像、心情
            my_name.setText(userName);
            my_sign.setText(signature);

            //通过网络链接获取图片
            try {
                one= LoadImgByNet.getBitmap(imgBaseUrl+headImg);
                my_head_img.setImageBitmap(one);
            }catch(IOException e){
                e.printStackTrace();
            }

            //已经登录时设置用户相关信息为显示状态
            //username = this.getActivity().getSharedPreferences("config", Context.MODE_PRIVATE).getString("fname", "");
            login_top.setVisibility(View.VISIBLE);
            un_login_top.setVisibility(View.GONE);
        }else{
            //尚未登录，设置登录、注册按钮为显示状态
            un_login_top.setVisibility(View.VISIBLE);
            login_top.setVisibility(View.GONE);
        }
    }

    //注册对话框的“注册”按钮
   /*class registerClick  implements DialogInterface.OnClickListener{
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader buffer = null;
        HttpURLConnection connGET = null;

        EditText txtUserName,txtUserPass1,txtUserPass2,txtSignature;
        TextView test1;
        String passWord2;
        public void onClick(DialogInterface dialog,int which){
            txtUserName = (EditText)register.findViewById(R.id.et_username);
            txtUserPass1 = (EditText)register.findViewById(R.id.et_password1);
            txtUserPass2 = (EditText)register.findViewById(R.id.et_password2);
            txtSignature= (EditText)register.findViewById(R.id.et_signature);
            test1 = (TextView)register.findViewById(R.id.test1_view_info);

            userName = txtUserName.getText().toString();
            passWord = txtUserPass1.getText().toString();
            passWord2 = txtUserPass2.getText().toString();
            signature = txtSignature.getText().toString();

            //从密码框中取值比较
            if(!passWord.equals(passWord2)){
                test1.setText("两次密码不一致！");
                Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                String registerUrl = baseUrl+"Register?username="+userName+"&password="+passWord+"&signature="+signatstringbuilder用法ure;
                URL url = new URL(registerUrl);
                connGET = (HttpURLConnection) url.openConnection();
                connGET.setConnectTimeout(5000);
                connGET.setRequestMethod("GET");
                if (connGET.getResponseCode() == 200) {
                    buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                    for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                        stringBuilder.append(s);
                    }
                    test1.setText(stringBuilder);
                    buffer.close();
                }else{
                    test1.setText("非200.."+connGET.getResponseCode() );
                }
            } catch (Exception e) {
                e.printStackTrace();
                test1.setText("get 提交 err.." + e.toString());
            }
            Toast.makeText(getActivity(), test1.getText(), Toast.LENGTH_LONG).show();
            //dialog.dismiss();
            test3.setText(userName+"——"+passWord+"——"+passWord2+"——"+signature+"——"+baseUrl+"Register?username="+userName+"&password="+passWord+"&signature="+signature+"_"+test1.getText());
        }
    }*/


    //输入对话框的“退出”按钮事件
    class exitClick implements DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog,int which){
            dialog.cancel();
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (resultCode){
            //requestCode：就是之前在主界面的.startActivityForResult的第二个参数值，通过判断requestCode就可以知道是哪张页面返回回来的
            //resultCode： 根据它来判断返回页面所需要的不同操作
            case 1:
                changeStatus();
                //("新签名为："+data.getStringExtra("newSignature"));    //是从修改签名页面跳转回来的
                break;
            case 2:
                break;
            default:
                break;
        }
    }
}


