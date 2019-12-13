package com.example.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class AccountManagement extends Activity {
    ImageView my_head_img;   //用户头像
    TextView my_name,status;   //用户昵称和心情
    Button out_login;   //退出登录

    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源
    String userName,headImg;   //存放用户基本信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        my_head_img = (ImageView)findViewById(R.id.account_images_head);   //我的头像
        my_name = (TextView)findViewById(R.id.account_t_name); //我的昵称
        status = (TextView)findViewById(R.id.account_t_status); //登录状态
        out_login = (Button)findViewById(R.id.account_bt_out_login) ;//退出登录的按钮

        setBaseInfo();
        out_login.setOnClickListener(new onClick());
    }
    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            exitSuccess();
            Toast.makeText(AccountManagement.this, "退出成功！", Toast.LENGTH_LONG).show();

            //通过handler设置延时1秒后执行r任务，跳转到首页
            new Handler().postDelayed(new AccountManagement.LoadMainTask(),1000);
        }
    }

    //启动线程,加载主页
    private class LoadMainTask implements Runnable{
        @Override
        public void run() {
            Intent intent = new Intent(AccountManagement.this,MainActivity.class);
            startActivity(intent);  //打开主页
            finish();   //关闭
        }
    }

    //退出登录后进行用户登录状态修改
    public void exitSuccess(){
        //退出登录后更改登录状态
        SharedPreferences sp = AccountManagement.this.getSharedPreferences("save", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", false);
        editor.putString("name", "");
        editor.putString("sign", "");
        editor.putString("img", "");
        editor.commit();
    }

    //设置账号管理页面用户相关信息
    public void setBaseInfo(){
        //获取最新的登录状态，SharePreferences为永久存储，需要手动退出
        SharedPreferences sp = AccountManagement.this.getSharedPreferences("save", Context.MODE_PRIVATE);
        //boolean isLogin = sp.getBoolean("isLogin",false);    //第一版本不做登录判断，在跳转前要求登录状态才能够跳转到这个页面
        status.setText("已登录");

        Bitmap one;     //用于获取网络图片
        userName = sp.getString("name","");
        headImg = sp.getString("headImg","");
        //设置用户昵称、头像
        my_name.setText(userName);

        //通过网络链接获取图片
        try {
            one= LoadImgByNet.getBitmap(imgBaseUrl+headImg);
            my_head_img.setImageBitmap(one);   //设置用户头像
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
