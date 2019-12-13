package com.example.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChangePassword extends Activity {
    EditText old_pass_et;   //旧密码
    EditText new_pass_et;   //新密码
    Button sure_change_pass;   //确定修改
    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源
    String userName,oldPass,newPass;   //存放用户基本信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        old_pass_et = (EditText) findViewById(R.id.et_old_login_password); //新密码
        new_pass_et = (EditText) findViewById(R.id.et_new_login_password); //新密码
        sure_change_pass = (Button)findViewById(R.id.bt_change_password) ;//确定修改密码的按钮

        setBaseInfo();
        sure_change_pass.setOnClickListener(new ChangePassword.onClick());
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            oldPass = old_pass_et.getText().toString();
            newPass = new_pass_et.getText().toString();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader buffer = null;
            HttpURLConnection connGET = null;
            if(oldPass.length()==0||newPass.length()==0){
                Toast.makeText(ChangePassword.this, "密码不能为空", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                String registerUrl = baseUrl+"ChangePassword?oldPass="+oldPass+"&newPass="+newPass+"&username="+userName;
                URL url = new URL(registerUrl);
                connGET = (HttpURLConnection) url.openConnection();
                connGET.setConnectTimeout(5000);
                connGET.setRequestMethod("GET");
                if (connGET.getResponseCode() == 200) {
                    buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                    for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                        stringBuilder.append(s);
                    }
                    Toast.makeText(ChangePassword.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();

                    //通过handler设置延时1秒后执行r任务，跳转到我的页面
                    new Handler().postDelayed(new ChangePassword.LoadMainTask(),500);
                    buffer.close();
                }else{
                    Toast.makeText(ChangePassword.this,"非200.."+connGET.getResponseCode() , Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChangePassword.this, "get 提交 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }


        }
    }

    //启动线程,加载我的页面
    private class LoadMainTask implements Runnable{
        @Override
        public void run() {
            Intent intent = new Intent(ChangePassword.this,MyTabFragment.class);
            /*startActivity(intent);*/
            setResult(2, intent); //这里的2就对应到onActivityResult（）方法中的resultCode
            finish();
        }
    }

    //获取用户信息
    public void setBaseInfo(){
        //获取最新的登录状态，SharePreferences为永久存储，需要手动退出
        SharedPreferences sp = ChangePassword.this.getSharedPreferences("save", Context.MODE_PRIVATE);
        userName = sp.getString("name","");
    }
}
