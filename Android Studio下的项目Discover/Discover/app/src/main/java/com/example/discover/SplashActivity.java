package com.example.discover;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

//启动页
public class SplashActivity extends Activity {
    private LinearLayout mLinearLayout;
    //重写onCreate
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        //在配置文件设置 android:theme="@android:style/Theme.NoTitleBar"来隐藏标题，避免出现title闪屏的效果
        setContentView(R.layout.splash);
        mLinearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout01);

        //判端当前网络是否可用,可用则进入主页
        if(isNetWorkConnetcted()){
            //设置进入起始页的动画
            AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);   //设置透明度从0变化到1
            aa.setDuration(2000); //设置动画时长为2秒
            mLinearLayout.setAnimation(aa);  //将动画设置到起始页
            mLinearLayout.startAnimation(aa);  //启动动画

            //通过handler设置延时两秒后执行r任务
            new Handler().postDelayed(new LoadMainTask(),2000);
        }else{
            showSetNetworkDialog();
        }
    }

    //启动线程,加载主页
    private class LoadMainTask implements Runnable{
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);  //打开主页
            finish();   //关闭起始页
        }
    }

    /*判端网络状态*/
    private boolean isNetWorkConnetcted(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);   //进行类型转换
        NetworkInfo info = manager.getActiveNetworkInfo();   //获取所有可用的网络状态信息
        //info.isConnected();  检查网络是否连接

        boolean result;
        if(info!=null&&info.isConnected()){
            result = true;
        }else{
            result = false;
        }
        return result;
    }

    //弹出网络不可用的对话框，课本94页
    private void showSetNetworkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("网络设置");
        builder.setMessage("网络错误，请检查网络状态");
        builder.setPositiveButton("设置网络",new okClick());
        builder.setPositiveButton("取消",new cleanClick());
        builder.create().show();
    }

    //普通对话框的确定，点击后展示设置页面
    class okClick implements DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog, int which){
            //激活系统组件
            Intent intent = new Intent();
            intent.setClassName("com.android.settings","com.android.settings.WirelessSetting");   //参数：激活activity的包名、类名
            startActivity(intent);
            finish();
        }
    }

    //关闭
    class cleanClick implements DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog, int which){
            finish();  //关闭应用
        }
    }
}
