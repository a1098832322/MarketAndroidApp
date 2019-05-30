package com.sqh.market.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sqh.market.R;

/**
 * 软件设置Activity
 *
 * @author 郑龙
 * @date 2019/4/8 10:58
 */
public class SoftwareSettingActivity extends AppCompatActivity {
    /**
     * 返回按钮
     */
    private ImageView mBtnBack;

    /**
     * 清除缓存按钮
     */
    private View mBtnClear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_setting);

        //绑定控件
        init();
    }

    /**
     * 初始化绑定控件
     */
    private void init() {
        mBtnClear = findViewById(R.id.clear_data);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SoftwareSettingActivity.this, "清除缓存成功！", Toast.LENGTH_LONG).show();
            }
        });
        mBtnBack = findViewById(R.id.back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
