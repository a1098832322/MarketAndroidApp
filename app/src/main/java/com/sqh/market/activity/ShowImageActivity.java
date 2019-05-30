package com.sqh.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sqh.market.R;
import com.sqh.market.callbacks.ImageActivityCallback;
import com.sqh.market.widget.MyImageView;

import org.apache.commons.lang3.StringUtils;

public class ShowImageActivity extends Activity implements View.OnTouchListener, View.OnClickListener {
    private float lastX[] = {0, 0};//用来记录上一次两个触点的横坐标
    private float lastY[] = {0, 0};//用来记录上一次两个触点的纵坐标

    private float windowWidth, windowHeight;//当前窗口的宽和高
    private float imageHeight, imageWidth;//imageview中图片的宽高（注意：不是imageview的宽和高）

    /**
     * 图片控件
     */
    private MyImageView imageView;

    /**
     * temp bitmap
     */
    private Bitmap bitmap = null;

    /**
     * 返回按钮
     */
    private ImageView mBtnBack;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    private static Matrix currentMatrix = new Matrix();//保存当前窗口显示的矩阵
    private Matrix touchMatrix, mmatrix;
    private boolean flag = false;//用来标记是否进行过移动前的首次点击
    private float moveLastX, moveLastY;//进行移动时用来记录上一个触点的坐标

    private static float max_scale = 4f;//缩放的最大值
    private static float min_scale = 0.8f;//缩放的最小值

    private ImageActivityCallback mCallback = new ImageActivityCallback() {
        @Override
        public void onSuccess() {
            uiHandler.sendEmptyMessage(1);
        }

        @Override
        public void onFail() {
            imageView.setImageDrawable(getDrawable(R.drawable.no_img));
        }

        @Override
        public void sendMessage(Object obj) {
            bitmap = (Bitmap) obj;
        }
    };

    /**
     * 修改ui用的handler
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    //失败时
                    break;
                case 1:
                    //成功时
                    //成功时，将图片居中
                    center(bitmap);

                    //取消动画显示，显示Layout
                    mProgressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        //获取窗口的宽高
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowWidth = windowManager.getDefaultDisplay().getWidth();
        windowHeight = windowManager.getDefaultDisplay().getHeight();

        //初始化绑定控件
        init();

        //初始化ImageView
        initImageView(getIntent());

        //将图片居中
        center(bitmap);
    }

    /**
     * 初始化绑定控件
     */
    private void init() {
        imageView = findViewById(R.id.imageView);
        imageView.setOnTouchListener(this);
        mBtnBack = findViewById(R.id.img_back);
        mBtnBack.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress);
    }

    /**
     * 初始化ImageView控件
     *
     * @param intent 父页面传入的图片url
     */
    private void initImageView(Intent intent) {
        String imageUrl = intent.getStringExtra("imgUrl");
        if (StringUtils.isNotBlank(imageUrl)) {
            //如果图片url合法，则从网络地址加载图片
            imageView.setImageURL(imageUrl, mCallback);
        } else {
            //否则显示默认图片
        }
    }

    /**
     * 开始时将图片居中显示
     */
    private void center(Bitmap bitmap) {

        //获取imageView中图片的实际高度
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) (imageView).getDrawable()).getBitmap();
        }

        imageHeight = bitmap.getHeight();
        imageWidth = bitmap.getWidth();

        //变换矩阵，使其移动到屏幕中央
        Matrix matrix = new Matrix();
        matrix.postTranslate(windowWidth / 2 - imageWidth / 2, windowHeight / 2 - imageHeight / 2);
        //保存到currentMatrix
        currentMatrix.set(matrix);
        imageView.setImageMatrix(matrix);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //注意这一句的写法，用在多点触控中
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // 在这里解释一下，在程序中我们将单点控制移动和双点控制缩放区分开（但是双点也是可以
            // 控制移动的)flag 的作用很简单，主要是用在单点移动时判断是否此次点击是否将要移动（不好描述，请读者自行细想一下）
            // 否则容易与双点操作混乱在一起，给用户带来较差的用户体验

            case MotionEvent.ACTION_DOWN://第一个触点按下，将第一次的坐标保存下来
                lastX[0] = motionEvent.getX(0);
                lastY[0] = motionEvent.getY(0);
                moveLastX = motionEvent.getX();
                moveLastY = motionEvent.getY();
                flag = true;//第一次点击，说明有可能要进行单点移动，flag设为true
                break;
            case MotionEvent.ACTION_POINTER_DOWN://第二个触点按下，保存下来
                lastX[1] = motionEvent.getX(1);
                lastY[1] = motionEvent.getY(1);
                flag = false;//第二次点击，说明要进行双点操作,而不是单点移动，所以设为false
                break;
            case MotionEvent.ACTION_MOVE:
                //计算上一次触点间的距离
                float lastDistance = getDistance(lastX[0], lastY[0], lastX[1], lastY[1]);

                //如果有两个触点，进行放缩操作
                if (motionEvent.getPointerCount() == 2) {
                    //得到当前触点之间的距离
                    float currentDistance = getDistance(motionEvent.getX(0), motionEvent.getY(0), motionEvent.getX(1), motionEvent.getY(1));
                    touchMatrix = new Matrix();
                    //矩阵初始化为当前矩阵
                    touchMatrix.set(currentMatrix);

                    float pp[] = new float[9];
                    touchMatrix.getValues(pp);
                    float leftPosition = pp[2];//图片左边的位置
                    float upPostion = pp[5];//图片顶部的位置
                    /*
                     * 缩放之前对图片进行平移，将缩放中心平移到将要缩放的位置
                     * */

                    float l = (motionEvent.getX(0) + motionEvent.getX(1)) / 2 - leftPosition;
                    float t = (motionEvent.getY(0) + motionEvent.getY(1)) / 2 - upPostion;

                    touchMatrix.postTranslate(-(currentDistance / lastDistance - 1) * l,
                            -(currentDistance / lastDistance - 1) * t);
                    float p[] = new float[9];
                    touchMatrix.getValues(p);
                    //根据判断当前缩放的大小来判断是否达到缩放边界
                    if (p[0] * currentDistance / lastDistance < min_scale || p[0] * currentDistance / lastDistance > max_scale) {
                        //超过边界值时，设置为先前记录的矩阵
                        touchMatrix.set(mmatrix);
                        imageView.setImageMatrix(touchMatrix);
                    } else {
                        //图像缩放
                        touchMatrix.preScale(currentDistance / lastDistance, currentDistance / lastDistance);

                        //根据两个触点移动的距离实现位移（双触点平移）
                        float movex = (motionEvent.getX(0) - lastX[0] + motionEvent.getX(1) - lastX[1]) / 2;
                        float movey = (motionEvent.getY(0) - lastY[0] + motionEvent.getY(1) - lastY[1]) / 2;
                        touchMatrix.postTranslate(movex, movey);
                        //保存最后的矩阵，当缩放超过边界值时就设置为此矩阵
                        mmatrix = touchMatrix;
                        imageView.setImageMatrix(touchMatrix);
                    }

                } else {
                    if (flag) {

                        //只有一个触点时进行位移
                        Matrix tmp = new Matrix();//临时矩阵用来判断此次平移是否会导致平移越界
                        tmp.set(currentMatrix);
                        tmp.postTranslate(-moveLastX + motionEvent.getX(0), -moveLastY + motionEvent.getY(0));

                        if (!isTranslateOver(tmp)) {
                            //如果不越界就进行平移
                            touchMatrix = new Matrix();
                            touchMatrix.set(currentMatrix);
                            touchMatrix.postTranslate(-moveLastX + motionEvent.getX(0), -moveLastY + motionEvent.getY(0));
                            imageView.setImageMatrix(touchMatrix);
                        } else {
                            //如果会越界就保存当前位置，并且不进行矩阵变换
                            currentMatrix = touchMatrix;
                            moveLastX = motionEvent.getX(0);
                            moveLastY = motionEvent.getY(0);
                            imageView.setImageMatrix(touchMatrix);
                        }


                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:

                //松开手时，保存当前矩阵，此时的位置保存下来
                //flag设为控制
                currentMatrix = touchMatrix;
                moveLastX = motionEvent.getX(0);
                moveLastY = motionEvent.getY(0);
                flag = false;

                break;
        }
        return true;
    }

    /**
     * 得到两点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * 判断平移是否越界
     *
     * @param matrix
     * @return
     */
    private boolean isTranslateOver(Matrix matrix) {
        float p[] = new float[9];
        matrix.getValues(p);
        float leftPosition = p[2];
        float rightPosition = (p[2] + imageWidth * p[0]);

        float upPostion = p[5];
        float downPostion = p[5] + imageHeight * p[0];

        float leftSide, rightSide, upSide, downSide;
        leftSide = windowWidth / 4;
        rightSide = windowWidth / 4 * 3;
        upSide = windowHeight / 4;
        downSide = windowHeight / 4 * 3;
        return (leftPosition > rightSide || rightPosition < leftSide || upPostion > downSide || downPostion < upSide);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回按钮点击事件
            case R.id.img_back:
                finish();
                break;
            default:
                break;
        }
    }
}
