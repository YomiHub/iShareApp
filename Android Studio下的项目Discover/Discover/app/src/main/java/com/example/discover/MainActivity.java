package com.example.discover;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private MyTabFragment myFragment;   //用于展示我的Fragment
    private DiscoverTabFragment discoverFragment;   //用于展示发现的Fragment
    private  FindTabFragment findFragment;  //用于展示查找的Fragment
    private EditTabFragment moreFragment;  //用于展示发布的Fragment

    private View myLayout;   //我的布局
    private View discoverLayout;  //发现界面布局
    private View findLayout;  //查找界面布局
    private View editLayout;   //发布界面布局

    private ImageView myImage;  //在Tab布局上显示我的图标的控件
    private ImageView discoverImage;  //在Tab布局上显示发现图标的控件
    private ImageView findImage;  //在Tab布局上显示查找图标的控件
    private ImageView moreImage;  //在Tab布局上显示更多图标的控件

    private TextView myText;  //在Tab布局上显示我的标题的控件
    private TextView discoverText;  //在Tab布局上显示发现标题的控件
    private TextView findText;  //在Tab布局上显示查找标题的控件
    private TextView moreText;  //在Tab布局上显示更多标题的控件

    /**
     * 用于对Fragment进行管理,事务
     */
    private FragmentTransaction ftr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_tab);

        // 初始化布局元素
        initViews();

        // 第一次启动时选中第0个tab
        setTabSelection(0);
    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
        myLayout = findViewById(R.id.my_layout);
        discoverLayout = findViewById(R.id.discover_layout);
        findLayout = findViewById(R.id.find_layout);
        editLayout = findViewById(R.id.edit_layout);

        myImage = (ImageView) findViewById(R.id.my_image);
        discoverImage = (ImageView) findViewById(R.id.discover_image);
        findImage = (ImageView) findViewById(R.id.find_image);
        moreImage = (ImageView) findViewById(R.id.more_image);

        myText = (TextView) findViewById(R.id.my_text);
        discoverText = (TextView) findViewById(R.id.discover_text);
        findText = (TextView) findViewById(R.id.find_text);
        moreText = (TextView) findViewById(R.id.more_text);

        discoverLayout.setOnClickListener(this);
        findLayout.setOnClickListener(this);
        editLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discover_layout:
                // 当点击了发现tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.find_layout:
                // 当点击了查找tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.edit_layout:
                // 当点击了发布tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.my_layout:
                // 当点击了我的tab时，选中第4个tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     *            每个tab页对应的下标0表示发现，1表示查找，2表示发布，3表示我的。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentManager fm = getSupportFragmentManager();
        ftr  = fm.beginTransaction();//开启一个事务
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(ftr);//自定义一个方法，来隐藏所有的fragment
        switch (index) {
            case 0:
                // 当点击了发现tab时，改变控件的图片和文字颜色
                discoverImage.setImageResource(R.drawable.tab_icon_two);
                discoverText.setTextColor(Color.GREEN);
                if (discoverFragment == null) {
                    // 如果discoverFragment为空，则创建一个并添加到界面上
                    discoverFragment = new DiscoverTabFragment();
                    ftr.add(R.id.content, discoverFragment);
                } else {
                    // 如果discoverFragment不为空，则直接将它显示出来
                    ftr.show(discoverFragment);
                }
                break;
            case 1:
                // 当点击了查找tab时，改变控件的图片和文字颜色
                findImage.setImageResource(R.drawable.tab_icon_three);
                findText.setTextColor(Color.GREEN);
                if (findFragment == null) {
                    // 如果findFragment为空，则创建一个并添加到界面上
                    findFragment = new FindTabFragment();
                    ftr.add(R.id.content, findFragment);
                } else {
                    // 如果findFragment不为空，则直接将它显示出来
                    ftr.show(findFragment);
                }
                break;
            case 2:
            default:
                // 当点击了发布tab时，改变控件的图片和文字颜色
                moreImage.setImageResource(R.drawable.tab_icon_four);
                moreText.setTextColor(Color.GREEN);
                if (moreFragment == null) {
                    // 如果moreFragment为空，则创建一个并添加到界面上
                    moreFragment = new EditTabFragment();
                    ftr.add(R.id.content, moreFragment);
                } else {
                    // 如果moreFragment不为空，则直接将它显示出来
                    ftr.show(moreFragment);
                }
                break;
            case 3:
                // 当点击了我的tab时，改变控件的图片和文字颜色
                myImage.setImageResource(R.drawable.tab_icon_one);
                myText.setTextColor(Color.GREEN);
                if (myFragment == null) {
                    // 如果myFragment为空，则创建一个并添加到界面上
                    myFragment = new MyTabFragment();
                    ftr.add(R.id.content, myFragment);
                } else {
                    // 如果myFragment不为空，则直接将它显示出来
                    ftr.show(myFragment);
                }
                break;
        }
        ftr.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        myImage.setImageResource(R.drawable.tab_icon_one_un_select);
        myText.setTextColor(Color.parseColor("#82858b"));
        discoverImage.setImageResource(R.drawable.tab_icon_two_un_select);
        discoverText.setTextColor(Color.parseColor("#82858b"));
        findImage.setImageResource(R.drawable.tab_icon_three_un_select);
        findText.setTextColor(Color.parseColor("#82858b"));
        moreImage.setImageResource(R.drawable.tab_icon_four_un_select);
        moreText.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (myFragment != null) {
            transaction.hide(myFragment);
        }
        if (discoverFragment != null) {
            transaction.hide(discoverFragment);
        }
        if (findFragment != null) {
            transaction.hide(findFragment);
        }
        if (moreFragment != null) {
            transaction.hide(moreFragment);
        }
    }
}
