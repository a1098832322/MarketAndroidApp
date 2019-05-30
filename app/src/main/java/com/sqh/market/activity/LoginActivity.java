package com.sqh.market.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqh.market.MainActivity;
import com.sqh.market.R;
import com.sqh.market.constant.Constants;
import com.sqh.market.utils.NetUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private long exitTime;
    private SQLiteDatabase sqlitedb = null;
    String account = "";
    String password = "";
    String userName = "";
    int userId = 0;


    //绑定操作
    private EditText _mobile_Text;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;

    private void init() {
        _mobile_Text = findViewById(R.id.input_mobile);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        init();

        //登录按钮点击事件
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //注册按钮点击事件
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    /**
     * 登录
     */
    public void login() {
        Log.d(TAG, "Login");

        account = _mobile_Text.getText().toString();
        password = _passwordText.getText().toString();
        postLoginRequest(account, password);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }


    /**
     * 成功登录的情况
     */
    public void onLoginSuccess(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE); //私有数据
        //获取SharedPreferences编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("username", userName);
        editor.putString("password", password);
        editor.putInt("userId", userId);
        editor.commit();//提交修改

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
        finish();
    }

    /**
     * 登录失败的情况
     */
    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    /**
     * 验证方法
     *
     * @return true/false 合法/非法
     */
    public boolean validate() {
        boolean valid = true;

        String email = _mobile_Text.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _mobile_Text.setError("请输入有效账号");
            valid = false;
        } else {
            _mobile_Text.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("请输入4-10位的密码");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /**
     * 根据handler结果进行不同操作
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    onLoginSuccess(msg.obj.toString());
                    break;
                default:
                    onLoginFailed(msg.obj.toString());
                    break;
            }
        }
    };

    /**
     * 使用账号密码登录
     *
     * @param account  账号
     * @param password 密码
     */
    private void postLoginRequest(String account, String password) {

        //post请求来获得数据
        //创建一个RequestBody，存放重要数据的键值对

        //创建一个请求对象，传入URL地址和相关数据的键值对的对象
        RequestBody body = new FormBody.Builder()
                .add("account", account)
                .add("password", password).build();

        //发送网络请求
        String url = Constants.BASE_URL + Constants.LOGIN_URL;
        NetUtil.doPost(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = -1;
                handler.sendMessage(message);
                Log.e("登录失败！", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                Message message = Message.obtain();
                try {
                    String jsonStr = response.body().string().trim();
                    JSONObject json = JSON.parseObject(jsonStr);
                    Log.i("response", jsonStr);
                    if (json.getBoolean("flag")) {
                        //拿到userId
                        String[] data = json.getString("data").split(",");

                        userId = Integer.parseInt(data[0]);
                        userName = data[1];

                        message.what = 1;
                        message.obj = json.getString("message");
                    } else {
                        message.what = 0;
                        message.obj = json.getString("message");
                    }
                } catch (Exception e) {
                    if (e instanceof NullPointerException) {
                        Log.e("network error!", e.getMessage());
                    }
                    message.what = 0;
                    message.obj = "登陆失败！";
                }

                handler.sendMessage(message);
            }
        });
    }

}
