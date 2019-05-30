package com.sqh.market.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.sqh.market.MainActivity;
import com.sqh.market.R;

/**
 * 启动动画Activity
 *
 * @author 郑龙
 * @date 2019/4/8 11:13
 */
public class AnimationActivity extends AppCompatActivity {
    /**
     * 入场动画布局
     */
    private LinearLayout inLayout;

    /**
     * 最终动画布局
     */
    private LinearLayout hideLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        inLayout = findViewById(R.id.text_lin);//要显示的字体
        hideLayout = findViewById(R.id.text_hide_lin);

        //动画1
        Animation animation = AnimationUtils.loadAnimation(AnimationActivity.this, R.anim.text_splash_position);
        //动画2
        final Animation hideLayoutAnimation = AnimationUtils.loadAnimation(AnimationActivity.this, R.anim.text_canvas);

        inLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //第一个动画执行完后执行第二个动画就是那个字体显示那部分
                hideLayout.startAnimation(hideLayoutAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        hideLayoutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //第二个动画执行完之后跳转到主页
                Intent intent = new Intent(AnimationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
