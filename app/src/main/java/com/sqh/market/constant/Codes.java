package com.sqh.market.constant;

/**
 * 这里定义了一些结果代码
 *
 * @author 郑龙
 */
public class Codes {
    /**
     * 登录检查结果代码
     */
    public enum LOGIN_CHECK_CODES {
        LOGIN_SUCCESS(1, "登陆成功"),
        LOGOUT(-1, "未登录或已注销"),
        UNCHECK(0, "不需要进行登录检查");

        int code;
        String message;

        LOGIN_CHECK_CODES(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
