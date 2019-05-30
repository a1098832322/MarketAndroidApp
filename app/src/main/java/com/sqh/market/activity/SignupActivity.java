package com.sqh.market.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqh.market.R;
import com.sqh.market.constant.Constants;
import com.sqh.market.utils.NetUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText _mobileText;
    private EditText _passwordText;
    private EditText _vertifyText;
    private Button _signupButton;
    private TextView _loginLink;
    private ImageView _back;

    /**
     * 用户昵称
     */
    private String userName;

    private void init() {
        _mobileText = findViewById(R.id.input_mobile);
        _passwordText = findViewById(R.id.input_password);
        _vertifyText = findViewById(R.id.input_verify);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);
        _back = findViewById(R.id.signup_back);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

        //登录按钮点击事件
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        //返回按钮点击事件
        _back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //登录按钮链接
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }


    /**
     * 注册
     */
    public void signUp() {
        Log.d(TAG, "SignUp");

        if (!validate()) {
            onSignupFailed("注册失败！");
            return;
        }

        String account = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String vertify = _vertifyText.getText().toString();

        //初始化一个默认的用户名
        userName = "新注册用户" + System.currentTimeMillis();
        //避免超出最长值，这里只取前12位
        userName = userName.substring(0, 12);

        if (!vertify.equals(password)) {
            _vertifyText.setError("两次密码输入不一致！");
        } else {
            postSignUpRequest(userName, account, password);
        }
    }

    /**
     * 注册消息handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    onSignupSuccess(msg.obj.toString());
                    break;
                default:
                    onSignupFailed(msg.obj.toString());
                    break;
            }
        }
    };


    private void postSignUpRequest(String userName, String account, String password) {

        //post请求来获得数据
        //创建一个RequestBody，存放重要数据的键值对
        RequestBody body = new FormBody.Builder()
                .add("uname", userName)
                .add("account", account)
                .add("password", password).build();

        //自定义回调操作
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = -1;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                Message message = Message.obtain();
                //获取返回结果
                String result = response.body().string().trim();
                try {
                    JSONObject json = JSON.parseObject(result);
                    if (json.getBoolean("flag")) {
                        message.what = 1;
                        message.obj = json.getString("message");
                    } else {
                        message.what = 0;
                        message.obj = json.getString("message");
                    }
                } catch (Exception e) {
                    Log.e("network error!", e.getMessage());
                    message.what = 0;
                    message.obj = "注册失败！";
                }

                mHandler.sendMessage(message);
            }
        };

        //发送注册请求
        NetUtil.doPost(Constants.BASE_URL + Constants.REGISTER_URL, body, callback);

    }


    public void onSignupSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);

        //更新sp数据
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE); //私有数据
        //获取SharedPreferences编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", userName);
        editor.commit();//提交修改

        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;


        String password = _passwordText.getText().toString().trim();
        String vertify = _vertifyText.getText().toString().trim();
        String mobile = _mobileText.getText().toString();


        if (TextUtils.isEmpty(mobile)) {
            _mobileText.setError("输入有效手机号");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (vertify.isEmpty()) {
            _vertifyText.setError("请再次确认密码！");
            valid = false;
        } else {
            _vertifyText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("输入4-10位密码");
            valid = false;
        } else {
            _passwordText.setError(null);
        }


        return valid;
    }


}
