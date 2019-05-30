package com.sqh.market.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sqh.market.constant.Codes;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 用户登录检查工具,用于业务控制，判断是否需要登录才能进行接下来的操作
 *
 * @author 郑龙
 */
public class LoginCheckUtil {
    private static Context mContext;

    /**
     * 封装一个简易的判断登录方法，用于简单判断控件是否显示
     *
     * @param context context
     * @return true/false
     */
    public static boolean isLogin(Context context) {
        mContext = context;
        return isLogin();
    }

    /**
     * 判断登录
     *
     * @return true/false
     */
    private static boolean isLogin() {
        SharedPreferences data = mContext.getSharedPreferences("userInfo", 0); //获取data.xml
        String account = data.getString("account", ""); //取得所需数据
        String password = data.getString("password", ""); //取得所需数据

        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return false;
        } else {
            return true;
        }
    }
}
