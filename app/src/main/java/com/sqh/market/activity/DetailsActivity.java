package com.sqh.market.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.dialogplus.DialogPlus;
import com.sqh.market.R;
import com.sqh.market.adapter.GlideImageLoader;
import com.sqh.market.adapter.SameLinkGridApter;
import com.sqh.market.callbacks.BaseCallback;
import com.sqh.market.constant.Constants;
import com.sqh.market.models.CommodityModel;
import com.sqh.market.utils.LoginCheckUtil;
import com.sqh.market.utils.NetUtil;
import com.sqh.market.utils.SharedPreferencesUtil;
import com.sqh.market.widget.MyCartDialog;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 商品详情界面
 *
 * @author 郑龙
 */
public class DetailsActivity extends AppCompatActivity {
    /**
     * 返回图标
     */
    private ImageView imgBack;

    /**
     * 轮播图
     */
    private Banner banner;

    /**
     * 商品单价
     */
    private TextView detailPrice;

    /**
     * 商品名
     */
    private TextView detailName;

    /**
     * 商品剩余
     */
    private TextView detailSurplus;

    /**
     * 商品简介
     */
    private TextView detailInfo;

    /**
     * 商品id
     */
    private String productId = "";

    /**
     * 同类链接数据
     */
    private GridView sameLinkGrid;

    /**
     * 同类商品加载进度条
     */
    private ProgressBar progressBar;

    private SameLinkGridApter sameLinkGridApter;

    private List<CommodityModel> listItemSameLink = new ArrayList<>();

    /**
     * 购买按钮
     */
    private Button mBtnBuy;

    /**
     * 购物车按钮
     */
    private Button mBtnCart;

    /**
     * 购物车dialog
     */
    private DialogPlus mCartDialog;

    /**
     * 购买dialog
     */
    private DialogPlus mBuyDialog;

    /**
     * context
     */
    private Context mContext;

    /**
     * 商品信息实体
     */
    private CommodityModel mCommodity;

    /**
     * 购物车按钮点击回调事件
     */
    private BaseCallback cartCallback = new BaseCallback() {
        @Override
        public void sendMessage(final Object obj) {
            //判断登录状态
            if (LoginCheckUtil.isLogin(mContext)) {
                //从SharedPreferences中获取用户id
                Integer uId = (Integer) SharedPreferencesUtil
                        .get(mContext, "userInfo", "userId", 0);

                //构造请求体
                RequestBody body = new FormBody.Builder()
                        .add("uId", uId + "")
                        .add("cIds", mCommodity.getId() + "")
                        .add("numbers", obj.toString())
                        .build();

                //进行网络请求
                NetUtil.doPost(Constants.BASE_URL + Constants.POST_ADD_TO_CART_URL
                        , body, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                uiHandler.sendEmptyMessage(-1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = response.body().string().trim();
                                Message msg = Message.obtain();
                                JSONObject object = JSON.parseObject(result);
                                if (object.getBoolean("flag")) {
                                    //请求处理成功且响应成功！
                                    msg.obj = object.getString("message");
                                    msg.what = 3;
                                } else {
                                    //消息响应成功，但处理失败！
                                    msg.what = 0;
                                }

                                //发送handler消息
                                uiHandler.sendMessage(msg);
                            }
                        });

            } else {
                //如果未登录
                uiHandler.sendEmptyMessage(-2);
            }

        }
    };

    /**
     * 购买按钮点击回调事件
     */
    private BaseCallback buyCallback = new BaseCallback() {
        @Override
        public void sendMessage(Object obj) {
            //判断登录状态
            if (LoginCheckUtil.isLogin(mContext)) {
                //从SharedPreferences中获取用户id
                Integer uId = (Integer) SharedPreferencesUtil
                        .get(mContext, "userInfo", "userId", 0);

                //构造请求体
                RequestBody body = new FormBody.Builder()
                        .add("uId", uId + "")
                        .add("cIds", mCommodity.getId() + "")
                        .add("numbers", obj.toString())
                        .build();

                //进行网络请求
                NetUtil.doPost(Constants.BASE_URL + Constants.POST_BUY_URL
                        , body, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                uiHandler.sendEmptyMessage(-1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result = response.body().string().trim();
                                Message msg = Message.obtain();
                                JSONObject object = JSON.parseObject(result);
                                if (object.getBoolean("flag")) {
                                    //请求处理成功且响应成功！
                                    msg.obj = object.getString("message");
                                    msg.what = 4;
                                } else {
                                    //消息响应成功，但处理失败！
                                    msg.what = 0;
                                }

                                //发送handler消息
                                uiHandler.sendMessage(msg);
                            }
                        });

            } else {
                //如果未登录
                uiHandler.sendEmptyMessage(-2);
            }
        }
    };


    /**
     * 用于从网络初始化UI的handler
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -2:
                    //未登录，需要登录！
                    Toast.makeText(mContext, "您还未登录！请先登录！", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    //网络请求失败
                    Toast.makeText(mContext, "网络请求失败！获取商品类别失败！"
                            , Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    //网络请求成功，但是返回状态为失败
                    Toast.makeText(mContext, msg.obj == null ? "请求处理失败！获取商品类别失败！"
                            : msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //网络请求商品信息数据成功
                    initCommodity(mCommodity);
                    //加载同类产品
                    initSameCommodity(mCommodity);
                    break;
                case 2:
                    //获取同类商品数据后绑定到控件
                    sameLinkGridApter = new SameLinkGridApter(DetailsActivity.this, listItemSameLink);
                    sameLinkGrid.setAdapter(sameLinkGridApter);
                    //取消进度条动画并显示商品列表
                    progressBar.setVisibility(View.GONE);
                    sameLinkGrid.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    //添加进购物车回调
                    Toast.makeText(mContext, "添加进购物车" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    //dismiss掉dialog
                    mCartDialog.dismiss();
                    break;
                case 4:
                    //购买回调
                    Toast.makeText(mContext, "购买商品" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    //dismiss掉dialog
                    mBuyDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //获得context
        mContext = this;

        //获得商品id
        Intent intent = getIntent();
        productId = intent.getStringExtra("id");

        //初始化控件
        init();

        //网络加载商品信息数据
        initCommodityData();
    }

    /**
     * 初始化绑定控件
     */
    private void init() {
        mBtnBuy = findViewById(R.id.btn_buy);
        //设置购买按钮的图标
        Drawable btnBuyDrawable = getResources().getDrawable(R.drawable.buy);
        //必须设置图片大小，否则不显示图片
        btnBuyDrawable.setBounds(0, 0, 100, 100);
        mBtnBuy.setCompoundDrawables(btnBuyDrawable, null, null, null);
        mBtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginCheckUtil.isLogin(DetailsActivity.this)) {
                    MyCartDialog dialog = new MyCartDialog(DetailsActivity.this);
                    mBuyDialog = dialog.createDialog(buyCallback);
                    mBuyDialog.show();
                } else {
                    //Toast提示登录
                    uiHandler.sendEmptyMessage(-2);
                }

            }
        });

        mBtnCart = findViewById(R.id.btn_cart);
        //设置购物车按钮的图标
        Drawable btnCartDrawable = getResources().getDrawable(R.drawable.cart);
        //必须设置图片大小，否则不显示图片
        btnCartDrawable.setBounds(0, 0, 100, 100);
        mBtnCart.setCompoundDrawables(btnCartDrawable, null, null, null);
        mBtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginCheckUtil.isLogin(DetailsActivity.this)) {
                    MyCartDialog dialog = new MyCartDialog(DetailsActivity.this);
                    mCartDialog = dialog.createDialog(cartCallback);
                    mCartDialog.show();
                } else {
                    //Toast提示登录
                    uiHandler.sendEmptyMessage(-2);
                }
            }
        });

        banner = findViewById(R.id.detail_banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImageLoader(new GlideImageLoader());
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(DetailsActivity.this, ShowImageActivity.class);
                intent.putExtra("imgUrl", mCommodity.getCommodityOtherImgUrls());
                startActivity(intent);
            }
        });

        detailInfo = findViewById(R.id.detail_info);
        detailSurplus = findViewById(R.id.detail_surplus);
        detailName = findViewById(R.id.detail_name);
        detailPrice = findViewById(R.id.detail_price);

        progressBar = findViewById(R.id.progress);

        sameLinkGrid = findViewById(R.id.samelink_info_gridview);
        sameLinkGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
                //将对应的产品id传到详情界面
                intent.putExtra("id", listItemSameLink.get(position).getId() + "");
                startActivity(intent);
                finish();
            }
        });

        imgBack = findViewById(R.id.detail_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 从网络获取同类商品推荐数据
     *
     * @param commodityModel 商品model，用于获取商品类别id
     */
    private void initSameCommodity(CommodityModel commodityModel) {
        int randomPage = (int) (Math.random() * (Constants.MAX - Constants.MIN))
                + Constants.MIN;

        //拼接访问字符串(只查询三条，使用随机页码)
        String url = Constants.BASE_URL + Constants.GET_COMMODITY_URL
                + "?limit=3&page=" + randomPage;
        if (commodityModel.getCommodityType() != null
                && commodityModel.getCommodityType() > 0L) {
            url = url + "&commodityType=" + commodityModel.getCommodityType();
        }

        NetUtil.doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("网络请求失败！", "网络请求失败！获取商品列表失败！");
                Message message = Message.obtain();
                message.what = -1;
                uiHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string().trim();
                com.alibaba.fastjson.JSONObject obj = JSON.parseObject(result);

                Message message = Message.obtain();
                if (obj.getBoolean("flag")) {
                    message.what = 2;

                    //拿到data内数据
                    com.alibaba.fastjson.JSONObject data = (com.alibaba.fastjson.JSONObject) obj.get("data");

                    //获得商品类型数据
                    List<CommodityModel> types = com.alibaba.fastjson.JSONArray
                            .parseArray(JSON.toJSONString(data.get("lists"))
                                    , CommodityModel.class);
                    listItemSameLink = types;
                } else {
                    message.what = 0;
                }

                message.obj = obj.getString("message");
                uiHandler.sendMessage(message);

            }
        });
    }

    /**
     * 根据网络请求结果，初始化控件显示
     *
     * @param commodity 商品信息实体
     */
    private void initCommodity(CommodityModel commodity) {
        if (commodity != null) {
            String[] imageUrls = commodity.getCommodityOtherImgUrls().split(",");
            //轮播图片
            banner.setImages(Arrays.asList(imageUrls));
            banner.start();

            //商品信息以、单价、简介和剩余
            detailSurplus.setText("剩余：" + commodity.getCommoditySurplus() + " 件");
            detailName.setText(commodity.getCommodityName());
            detailPrice.setTextColor(Color.RED);
            detailPrice.setText("￥ " + commodity.getCommodityPrice() + " 元");
            detailInfo.setText(commodity.getCommodityInfo());
        }

    }

    /**
     * 从网络加载商品信息数据
     */
    private void initCommodityData() {
        //构造url
        String url = Constants.BASE_URL
                + Constants.GET_COMMODITY_BY_ID_URL
                + productId;

        //发起网络请求
        NetUtil.doGet(url
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = Message.obtain();
                        message.what = -1;
                        uiHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string().trim();
                        com.alibaba.fastjson.JSONObject obj = JSON.parseObject(result);

                        Message message = Message.obtain();

                        //如果请求成功
                        if (obj.getBoolean("flag")) {
                            mCommodity = JSON.parseObject(com.alibaba
                                            .fastjson.JSONObject.toJSONString(obj.get("data"))
                                    , CommodityModel.class);
                            message.what = 1;
                        } else {
                            message.what = 0;
                        }

                        message.obj = obj.getString("message");

                        //发送消息
                        uiHandler.sendMessage(message);
                    }
                }
        );
    }
}

