package com.sqh.market.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.sqh.market.R;

/**
 * 关于我们Activity
 *
 * @author 郑龙
 * @date 2019/4/8 10:39
 */
public class AboutUsActivity extends AppCompatActivity {
    /**
     * 返回按钮
     */
    private ImageView mBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //初始化绑定控件
        init();
    }

    /**
     * 初始化绑定控件
     */
    private void init() {
        mBtnBack = findViewById(R.id.back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
