package com.sqh.market.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.sqh.market.R;
import com.sqh.market.constant.Constants;
import com.sqh.market.utils.LoginCheckUtil;
import com.sqh.market.utils.NetUtil;
import com.sqh.market.utils.SharedPreferencesUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 郑龙 on 2017/8/19.
 */

public class PersonalActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNameText;//显示用户名的text
    private TextView mMenu;//Toolbar上的菜单
    private View mMenuLayout, mWarnLayout;
    private ImageView mImageView;

    /**
     * 账号显示（不允许修改）
     */
    private TextView mAccount;

    /**
     * 密码和昵称
     */
    private EditText mUserNameText, mPasswordText;

    /**
     * 账号密码昵称
     */
    private String account, userName, password;
    private Integer userId;

    /**
     * 网络请求用handler
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    //网络请求失败
                    Toast.makeText(getApplicationContext(), "网络请求失败,修改失败！", Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    //修改失败
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    //修改成功
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    mMenu.setText("修改");//修改显示文字
                    //请求成功时，写入sp并刷新本地存储的数据
                    SharedPreferencesUtil.put(getApplicationContext(), "userInfo",
                            "username", userName);
                    SharedPreferencesUtil.put(getApplicationContext(), "userInfo",
                            "password", password);

                    refreshUI();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 是否点击编辑按钮的标志
     */
    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_item_personal);

        init();//初始化绑定控件们

        /*Toolbar们*/
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("个人中心");//设置ToolBar的标题

        //返回按钮颜色显示不正常时,以下三行是修改回退按钮为白色的逻辑
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);//返回按钮监听事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //从编辑变成完成
        if (LoginCheckUtil.isLogin(getApplicationContext())) {
            mNameText.setText(userName);

            //修改layout
            mAccount.setText(account);
            mUserNameText.setText(userName);
            mPasswordText.setText(password);
        } else {
            //设置默认名称
            mNameText.setText("请先登录");
        }

        //设置到默认头像
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.cutecat));

        /*Toolbar上的"编辑"按钮*/
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = !isEdit;
                if (isEdit) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUI();
                        }
                    });


                    //从完成变成编辑
                    mMenu.setText("完成");//修改显示文字
                } else {
                    //从编辑变成完成
                    final String name = mUserNameText.getText().toString();
                    final String pwd = mPasswordText.getText().toString();

                    //构造post请求体
                    RequestBody body = new FormBody.Builder()
                            .add("uId", userId + "")
                            .add("userName", name)
                            .add("password", pwd)
                            .build();

                    //进行post请求
                    NetUtil.doPost(Constants.BASE_URL
                                    + Constants.MODIFY_USER_INFO_URL,
                            body, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("网络请求失败", "修改用户昵称和密码失败！网络请求失败！");
                                    Message message = Message.obtain();
                                    message.what = -1;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result = response.body().string().trim();
                                    JSONObject object = JSONObject.parseObject(result);

                                    Message message = Message.obtain();
                                    if (object.getBoolean("flag")) {
                                        message.what = 1;
                                        userName = name;
                                        password = pwd;
                                    } else {
                                        message.what = 0;
                                    }
                                    message.obj = object.getString("message");
                                    mHandler.sendMessage(message);
                                }
                            });


                }

                mWarnLayout.setVisibility(isEdit ? View.GONE : View.VISIBLE);
                mMenuLayout.setVisibility(isEdit ? View.VISIBLE : View.GONE);
            }
        });

    }

    /**
     * 刷新显示UI
     */
    private void refreshUI() {
        //刷新显示数据
        getDataFromSp();
        //设置显示名字
        mNameText.setText(userName);
        mUserNameText.setText(userName);
        mPasswordText.setText(password);
    }


    private void init() {
        mToolbar = findViewById(R.id.personal_toolbar);
        mWarnLayout = findViewById(R.id.user_info_show_layout);
        mMenu = findViewById(R.id.tv_edit);

        mMenuLayout = findViewById(R.id.user_Info_edit_layout);
        mImageView = findViewById(R.id.cat_avatar);

        mNameText = findViewById(R.id.tv_username);

        mAccount = findViewById(R.id.account);
        mUserNameText = findViewById(R.id.username);
        mPasswordText = findViewById(R.id.password);

        //刷新数据
        getDataFromSp();
    }

    /**
     * 从sp中获取存储的用户昵称,账号密码等数据
     */
    private void getDataFromSp() {
        Object objName = SharedPreferencesUtil
                .get(getApplicationContext(), "userInfo", "username", "新注册用户");
        userName = objName == null ? "" : objName.toString();

        Object objAccount = SharedPreferencesUtil
                .get(getApplicationContext(), "userInfo", "account", "");
        account = objAccount == null ? "" : objAccount.toString();

        Object objPassword = SharedPreferencesUtil
                .get(getApplicationContext(), "userInfo", "password", "");
        password = objAccount == null ? "" : objPassword.toString();

        Object objUserId = SharedPreferencesUtil
                .get(getApplicationContext(), "userInfo", "userId", -1);
        userId = objUserId == null ? -1 : (Integer) objUserId;
    }
}
