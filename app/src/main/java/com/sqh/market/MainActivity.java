package com.sqh.market;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sqh.market.activity.LoginActivity;
import com.sqh.market.activity.SignupActivity;
import com.sqh.market.adapter.MainActivityFragmentAdapter;
import com.sqh.market.fragments.AboutMe;
import com.sqh.market.fragments.Classify;
import com.sqh.market.fragments.HomePage;
import com.sqh.market.utils.LoginCheckUtil;
import com.sqh.market.utils.SharedPreferencesUtil;
import com.sqh.market.viewpage.MainActivityViewPager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static MainActivityViewPager mViewPage;
    private List<Fragment> mFragments;
    private NavigationView navigationView;
    private RadioGroup radioGroup;
    private RadioButton rb_home;
    private RadioButton rb_category;
    private RadioButton rb_mine;
    private Menu mMenu;
    private long exitTime = 0;

    /**
     * 新安装应用时，申请权限
     */
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 999;


    /**
     * 首次进入应用标志
     */
    private String First_Key = "FIRST_OPEN";

    /**
     * 用户登录之后显示用户头像及昵称
     */
    private LinearLayout userHeaderInfoLayout;

    /**
     * 未登录时显示提示信息
     */
    private RelativeLayout unLoginLayout;

    /**
     * 登陆后在头像下方显示的用户名
     */
    private TextView mHeaderUserNameText;

    /**
     * 调用MainActivity内的退出方法
     *
     * @param flag
     */
    public static List<Activity> activityList = new LinkedList();

    /**
     * 退出方法
     */
    public static void exitApp() {
        for (Activity act : activityList) {
            act.finish();
        }
        System.exit(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初次进入App时请求相关权限
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA
                        , Manifest.permission.INTERNET
                        , Manifest.permission.ACCESS_NETWORK_STATE}
                , REQUEST_CODE_ASK_PERMISSIONS);

        //需要关闭的list中添加此方法
        MainActivity.activityList.add(this);

        Fresco.initialize(this); //初始化fresco类

        boolean isFirstOpen = SharedPreferencesUtil.contains(this, First_Key);   //首次登陆教程
        if (!isFirstOpen) {
            SharedPreferencesUtil.put(this, First_Key, "first");
            Toast.makeText(MainActivity.this, "欢迎！！！", Toast.LENGTH_LONG).show();
        }

        //绑定控件
        init();

        //根据登录状态显示控件
        refreshUI(LoginCheckUtil.isLogin(this));

        //fragment适配及滑动操作
        MyFragment();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        mMenu = menu;
        mMenu.findItem(R.id.denglu).setVisible(!LoginCheckUtil.isLogin(this));
        mMenu.findItem(R.id.zhuce).setVisible(!LoginCheckUtil.isLogin(this));
        mMenu.findItem(R.id.zhuxiao).setVisible(LoginCheckUtil.isLogin(this));

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.shouye) {
            // Handle the camera action
            mViewPage.setCurrentItem(0);
            //底部ImageView点击之后变色
            rb_home.setChecked(true);
            setTabState();
        } else if (id == R.id.fenlei) {
            mViewPage.setCurrentItem(1);
            //底部ImageView点击之后变色
            rb_category.setChecked(true);
            setTabState();
        } else if (id == R.id.wode) {
            mViewPage.setCurrentItem(2);
            //底部ImageView点击之后变色
            rb_mine.setChecked(true);
            setTabState();
        } else if (id == R.id.denglu) {
            //intent跳转
            if (LoginCheckUtil.isLogin(this)) {
                Toast.makeText(this, "您已登录过了，请先注销", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.zhuce) {
            //intent跳转
            if (LoginCheckUtil.isLogin(this)) {
                Toast.makeText(this, "您已登录过了，请先注销", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.zhuxiao) {
            //intent跳转
            if (LoginCheckUtil.isLogin(this)) {
                SharedPreferences.Editor editor = getSharedPreferences("userInfo", 0).edit();
                editor.clear();
                editor.commit();
                Toast.makeText(this, "您已注销", Toast.LENGTH_SHORT).show();

                //刷新UI显示
                refreshUI(LoginCheckUtil.isLogin(this));
            } else {
                Toast.makeText(this, "您未登录不用注销", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新UI显示
        refreshUI(LoginCheckUtil.isLogin(this));
    }

    /**
     * 自定义的fragment适配器
     */
    private void MyFragment() {
        MainActivityFragmentAdapter fragmentAdapter = new MainActivityFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPage.setAdapter(fragmentAdapter);
        //预渲染页面数量
        mViewPage.setOffscreenPageLimit(2);
        //禁用滑动
        mViewPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.imageButton1:
                        mViewPage.setCurrentItem(0);
                        //底部ImageView点击之后变色
                        break;
                    case R.id.imageButton2:
                        mViewPage.setCurrentItem(1);
                        break;
                    case R.id.imageButton5:
                        mViewPage.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void init() {
        mFragments = new ArrayList<>();

        mFragments.add(new HomePage());
        mFragments.add(new Classify());
        mFragments.add(new AboutMe());

        mViewPage = findViewById(R.id.viewPage);
        radioGroup = findViewById(R.id.radioGroup);
        rb_home = findViewById(R.id.imageButton1);
        rb_category = findViewById(R.id.imageButton2);
        rb_mine = findViewById(R.id.imageButton5);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        userHeaderInfoLayout = headerView.findViewById(R.id.user_header_info);
        unLoginLayout = headerView.findViewById(R.id.un_login_dead);
        mHeaderUserNameText = headerView.findViewById(R.id.user_name_header);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1:
                mViewPage.setCurrentItem(0);
                break;
            case R.id.imageButton2:
                mViewPage.setCurrentItem(1);
                break;
            case R.id.imageButton5:
                mViewPage.setCurrentItem(2);
                break;
            default:
                break;
        }
        setTabState();
    }


    //设置选中和未选择的状态
    private void setTabState() {
        setHome();
        setCategory();
        setMine();
    }

    private void setHome() {
        if (rb_home.isChecked()) {
            rb_home.setTextColor(ContextCompat.getColor(this, R.color.button_press));
        } else {
            rb_home.setTextColor(ContextCompat.getColor(this, R.color.button_normal));
        }
    }

    private void setCategory() {
        if (rb_category.isChecked()) {
            rb_category.setTextColor(ContextCompat.getColor(this, R.color.button_press));
        } else {
            rb_category.setTextColor(ContextCompat.getColor(this, R.color.button_normal));
        }
    }

    private void setMine() {
        if (rb_mine.isChecked()) {
            rb_mine.setTextColor(ContextCompat.getColor(this, R.color.button_press));
        } else {
            rb_mine.setTextColor(ContextCompat.getColor(this, R.color.button_normal));
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                exitApp();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 用户登陆之后的相关UI控制器
     *
     * @param isShow 是否显示
     */
    private void refreshUI(boolean isShow) {
        if (isShow) {
            //从sp中获取存储的用户昵称
            Object obj = SharedPreferencesUtil
                    .get(this, "userInfo", "username", "新注册用户");
            String userName = obj == null ? "新注册用户" : obj.toString();
            mHeaderUserNameText.setText(userName);
        }

        //isShow为true时显示
        userHeaderInfoLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);

        //isShow为true时隐藏
        unLoginLayout.setVisibility(!isShow ? View.VISIBLE : View.GONE);

    }

}
