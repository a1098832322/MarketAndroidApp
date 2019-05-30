package com.sqh.market.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * 图片工具类
 *
 * @author 郑龙
 */
public class ImageUtil {
    /**
     * 　　* 将bitmap转换成base64字符串
     * <p>
     * 　　*
     * <p>
     * 　　* @param bitmap
     * <p>
     * 　　* @return base64 字符串
     * <p>
     */

    public static String bitmaptoString(Bitmap bitmap, int bitmapQuality) {

        // 将Bitmap转换成字符串

        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        return string;

    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
