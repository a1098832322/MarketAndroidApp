package com.sqh.market.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sqh.market.MainActivity;
import com.sqh.market.R;


/**
 * 设置activity
 *
 * @author 郑龙
 */

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout mBtnPersonal, mBtnPic, mBtnExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //将本activity也添加入需要关闭的list中
        MainActivity.activityList.add(this);

        toolbar = findViewById(R.id.setting_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("设置");//设置ToolBar的标题

        //返回按钮颜色显示不正常时,以下三行是修改回退按钮为白色的逻辑
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);

        //返回按钮监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //绑定菜单控件
        mBtnPersonal = findViewById(R.id.menu_personal_data);
        mBtnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "当前已是最新版本！   V1.0.0", Toast.LENGTH_LONG).show();
            }
        });

        mBtnPic = findViewById(R.id.menu_pic_setting);
        mBtnPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SoftwareSettingActivity.class);
                startActivity(intent);
            }
        });

        mBtnExit = findViewById(R.id.menu_exit);
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.exitApp();
            }
        });

    }
}
