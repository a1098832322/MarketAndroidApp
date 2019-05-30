package com.sqh.market.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Toast;

import com.sqh.market.callbacks.ImageActivityCallback;
import com.sqh.market.utils.NetUtil;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyImageView extends AppCompatImageView {
    /**
     * 子线程不能操作UI，通过Handler设置图片
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    //网络请求失败
                    Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    //网络请求成功，但是返回状态为失败
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //网络请求成功
                    Bitmap bitmap = (Bitmap) msg.obj;
                    setImageBitmap(bitmap);
                    break;

            }
        }
    };

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置网络图片
     *
     * @param url      网络图片url
     * @param callback 自定义回调
     */
    public void setImageURL(String url, final ImageActivityCallback callback) {
        NetUtil.doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = -1;
                uiHandler.sendMessage(message);

                if (callback != null) {
                    callback.onFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Message message = Message.obtain();
                message.obj = bitmap;
                message.what = 1;
                uiHandler.sendMessage(message);

                if (callback != null) {
                    callback.sendMessage(bitmap);
                    callback.onSuccess();
                }
            }
        });

    }

    /**
     * 设置网络图片
     *
     * @param url 网络图片url
     */
    public void setImageURL(String url) {
        setImageURL(url, null);
    }
}
