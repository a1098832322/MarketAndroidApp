package com.sqh.market.constant;

/**
 * 这里规定了一些可全局使用的常量
 *
 * @author 郑龙
 */

public class Constants {
    /**
     * web端基础地址
     */
    public static final String BASE_URL = "http://wishes-blog.cn/market";
    //本地测试url
    //public static final String BASE_URL = "http://192.168.0.103:8090";

    /**
     * 基础广告地址
     */
    public static final String BASE_ADS_URL = "http://wishes-blog.cn:9002/ads";

    /**
     * 登录Url
     */
    public static final String LOGIN_URL = "/control/login";

    /**
     * 注册Url
     */
    public static final String REGISTER_URL = "/control/register";

    /**
     * 根据用户id获取用户信息的Url
     */
    public static final String GET_USER_INFO_BY_UID_URL = "/control/getUserInfoByUid?uId=";

    /**
     * 修改用户昵称和密码的Url
     */
    public static final String MODIFY_USER_INFO_URL = "/control/changeUserInfo";

    /**
     * 商品类型接口Url
     */
    public static final String GET_COMMODITY_TYPES_URL = "/business/queryCommodityTypes";

    /**
     * 分页获取商品列表Url
     */
    public static final String GET_COMMODITY_URL = "/business/queryPage";

    /**
     * 根据商品id获取商品详情Url
     */
    public static final String GET_COMMODITY_BY_ID_URL = "/business/queryCommodityInfoById?CommodityId=";

    /**
     * 根据输入的商品名模糊搜索商品列表
     */
    public static final String GET_COMMODITY_BY_NAME_URL = "/business/queryCommodityInfoByName?CommodityName=";

    /**
     * 添加进购物车Url
     */
    public static final String POST_ADD_TO_CART_URL = "/business/addToCart";

    /**
     * 购买Url
     */
    public static final String POST_BUY_URL = "/business/buy";

    /**
     * 刷新购物车Url
     */
    public static final String POST_REFRESH_CART_URL = "/business/refreshCart";

    /**
     * 从购物车中删除商品Url
     */
    public static final String POST_DELETE_URL = "/business/deleteFromCart";

    /**
     * 查询用户购物车数据Url
     */
    public static final String GET_QUERY_CART_URL = "/business/queryCartList?limit=9999999&page=1";

    /**
     * 查询购买历史Url
     */
    public static final String GET_QUERY_BUY_HISTORY_URL = "/business/queryBoughtList?limit=9999999&page=1";

    /**
     * 广告Url枚举
     */
    public enum ADS {
        ADS_URL1("/1.jpg", "广告1"),
        ADS_URL2("/2.jpg", "广告2");

        String url;
        String message;

        ADS(String url, String message) {
            this.url = url;
            this.message = message;
        }

        public String getUrl() {
            return url;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 定义首页分页获取商品数量
     */
    public static final int HOME_PAGE_LIMIT = 6;

    /**
     * 定义出现的随机数最大值
     */
    public static final int MAX = 26;

    /**
     * 定义出现的随机数最小值
     */
    public static final int MIN = 0;
}
