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

public class ChangeSignature extends Activity {
    TextView old_sign_tv;   //旧签名
    EditText new_sign_et;   //新签名
    Button sure_change_sign;   //确定修改
    String baseUrl = "http://10.0.2.2:8080/iShareService/servlet/";   //web服务器的地址
    String imgBaseUrl = "http://10.0.2.2:8080/iShareService/images/";  //图片资源
    String userName,sign;   //存放用户基本信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_signature);

        old_sign_tv = (TextView)findViewById(R.id.old_user_sign);
        new_sign_et = (EditText) findViewById(R.id.et_login_password); //新签名
        sure_change_sign = (Button)findViewById(R.id.bt_change_signature) ;//确定修改签名的按钮

        setBaseInfo();
        sure_change_sign.setOnClickListener(new onClick());
    }

    class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String sign = new_sign_et.getText().toString();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader buffer = null;
            HttpURLConnection connGET = null;
            if(sign.length()==0){
                Toast.makeText(ChangeSignature.this, "签名不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                String registerUrl = baseUrl+"ChangeSignature?signature="+sign+"&username="+userName;
                URL url = new URL(registerUrl);
                connGET = (HttpURLConnection) url.openConnection();
                connGET.setConnectTimeout(5000);
                connGET.setRequestMethod("GET");
                if (connGET.getResponseCode() == 200) {
                    buffer = new BufferedReader(new InputStreamReader(connGET.getInputStream()));
                    for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                        stringBuilder.append(s);
                    }
                    changeSuccess(sign);
                    Toast.makeText(ChangeSignature.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
                    //通过handler设置延时1秒后执行r任务
                    new Handler().postDelayed(new ChangeSignature.LoadMainTask(),500);
                    buffer.close();
                }else{
                    Toast.makeText(ChangeSignature.this,"非200.."+connGET.getResponseCode() , Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChangeSignature.this, "get 提交 err.." + e.toString(), Toast.LENGTH_LONG).show();
            }


        }
    }

    //启动线程,加载我的页面
    private class LoadMainTask implements Runnable{
        @Override
        public void run() {
            Intent intent = new Intent(ChangeSignature.this,MyTabFragment.class);
            //startActivity(intent);
            intent.putExtra("newSignature",sign);
            setResult(1, intent); //这里的1就对应到onActivityResult（）方法中的resultCode
            finish();
        }
    }

    //修改签名后进行用户签名修改
    public void changeSuccess(String sign){
        SharedPreferences sp = ChangeSignature.this.getSharedPreferences("save", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("sign", sign);
        editor.commit();
    }

    //设置修改签名页面用户相关信息
    public void setBaseInfo(){
        //获取最新的登录状态，SharePreferences为永久存储，需要手动退出
        SharedPreferences sp = ChangeSignature.this.getSharedPreferences("save", Context.MODE_PRIVATE);
        userName = sp.getString("name","");
        sign = sp.getString("sign","");
        //设置旧签名
        old_sign_tv.setText(sign);
    }
}
