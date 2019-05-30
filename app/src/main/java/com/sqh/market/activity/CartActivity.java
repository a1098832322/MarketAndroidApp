package com.sqh.market.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.sqh.market.R;
import com.sqh.market.adapter.ShoppingCartAdapter;
import com.sqh.market.constant.Constants;
import com.sqh.market.models.CartModel;
import com.sqh.market.utils.NetUtil;
import com.sqh.market.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends Activity implements View.OnClickListener,
        ShoppingCartAdapter.CheckInterface, ShoppingCartAdapter.ModifyCountInterface {
    private static final String TAG = "CartActivity";

    /**
     * 返回按钮
     */
    private ImageView btnBack;

    /**
     * 全选框
     */
    private CheckBox ckAll;

    /**
     * 总额
     */
    private TextView tvShowPrice;

    /**
     * 结算
     */
    private TextView tvSettlement;

    /**
     * 编辑按钮
     */
    private TextView btnEdit;

    /**
     * 商品列表
     */
    private ListView commodityListView;

    /**
     * adapter
     */
    private ShoppingCartAdapter shoppingCartAdapter;
    private boolean flag = false;
    private List<CartModel> shoppingCartBeanList = new ArrayList<>();
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量

    /**
     * context
     */
    private Context mContext;

    /**
     * uId
     */
    private Integer uId;

    /**
     * 用于从网络初始化UI的handler
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //定义一个弹出层dialog
            AlertDialog alert = new AlertDialog.Builder(mContext).create();

            switch (msg.what) {
                case -2:
                    //未登录，需要登录！
                    Toast.makeText(mContext, "您还未登录！请先登录！", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    //网络请求失败
                    Toast.makeText(mContext, "网络请求失败！"
                            , Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    //网络请求成功，但是返回状态为失败
                    Toast.makeText(mContext, msg.obj == null ? "请求处理失败！获取商品数据失败！"
                            : msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //网络请求购物车信息数据成功
                    shoppingCartAdapter = new ShoppingCartAdapter(CartActivity.this);
                    shoppingCartAdapter.setCheckInterface(CartActivity.this);
                    shoppingCartAdapter.setModifyCountInterface(CartActivity.this);
                    commodityListView.setAdapter(shoppingCartAdapter);
                    shoppingCartAdapter.setShoppingCartBeanList(shoppingCartBeanList);
                    break;
                case 2:
                    //购买结算
                    alert.setTitle("提示");
                    alert.setMessage(msg.obj == null ? "购买请求处理失败！"
                            : msg.obj.toString());
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish掉购物车窗口
                                    finish();
                                }
                            });
                    alert.show();
                    break;
                case 3:
                    //删除商品
                    alert.setTitle("提示");
                    alert.setMessage(msg.obj == null ? "从购物车中移除商品失败！"
                            : msg.obj.toString());
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                    break;
                case 4:
                    Toast.makeText(mContext, msg.obj == null ? "刷新购物车数据失败！"
                            : msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ViewUtils.inject(this);
        mContext = this;

        //从SharedPreferences中获取用户id
        uId = (Integer) SharedPreferencesUtil
                .get(mContext, "userInfo", "userId", 0);

        //初始化绑定控件
        init();

        //初始化数据
        initData();
    }

    /**
     * 初始化绑定控件
     */
    private void init() {
        btnBack = findViewById(R.id.cart_back);
        ckAll = findViewById(R.id.ck_all);
        tvShowPrice = findViewById(R.id.tv_show_price);
        tvSettlement = findViewById(R.id.tv_settlement);
        btnEdit = findViewById(R.id.btn_edit);
        commodityListView = findViewById(R.id.list_shopping_cart);

        btnEdit.setText("编辑");
        btnEdit.setOnClickListener(this);
        ckAll.setOnClickListener(this);
        tvSettlement.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    /**
     * 从网络获取购物车数据
     */
    protected void initData() {
        //拼接url
        String url = Constants.BASE_URL + Constants.GET_QUERY_CART_URL + "&uId=" + uId;
        NetUtil.doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string().trim();
                JSONObject object = JSON.parseObject(result);
                Message message = Message.obtain();
                if (object.getBoolean("flag")) {
                    message.what = 1;

                    //解析拿到data中的lists数据
                    JSONArray lists = ((JSONObject) object.get("data")).getJSONArray("lists");

                    //将list传递给数据集
                    shoppingCartBeanList = JSONArray.parseArray(JSON.toJSONString(lists), CartModel.class);
                } else {
                    message.what = 0;
                }

                uiHandler.sendMessage(message);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //全选按钮
            case R.id.ck_all:
                if (shoppingCartBeanList.size() != 0) {
                    if (ckAll.isChecked()) {
                        for (int i = 0; i < shoppingCartBeanList.size(); i++) {
                            shoppingCartBeanList.get(i).setChoosed(true);
                        }
                        shoppingCartAdapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < shoppingCartBeanList.size(); i++) {
                            shoppingCartBeanList.get(i).setChoosed(false);
                        }
                        shoppingCartAdapter.notifyDataSetChanged();
                    }
                }
                statistics();
                break;
            case R.id.btn_edit:
                flag = !flag;
                if (flag) {
                    //编辑状态
                    btnEdit.setText("完成");
                    shoppingCartAdapter.isShow(false);
                } else {
                    //编辑完成状态
                    btnEdit.setText("编辑");
                    shoppingCartAdapter.isShow(true);
                    //刷新购物车数据
                    refreshCart();
                }
                break;
            case R.id.tv_settlement: //结算
                pay();
                break;
            case R.id.cart_back:
                finish();
                break;
        }
    }

    /**
     * 在编辑完成后，刷新购物车
     */
    private void refreshCart() {
        String cIds = "";
        String numbers = "";

        //选中的需要提交的商品清单
        for (CartModel bean : shoppingCartBeanList) {
            //拼接商品id
            cIds += bean.getId() + ",";
            //拼接商品数量
            numbers += bean.getNumber() + ",";
        }

        //清理末尾逗号
        cIds = cIds.substring(0, cIds.lastIndexOf(","));
        numbers = numbers.substring(0, numbers.lastIndexOf(","));

        //构造url
        String url = Constants.BASE_URL + Constants.POST_REFRESH_CART_URL;
        //构造请求体
        RequestBody body = new FormBody.Builder()
                .add("uId", uId + "")
                .add("cIds", cIds)
                .add("numbers", numbers)
                .build();
        //向服务器发送刷新购物车数据请求
        NetUtil.doPost(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string().trim();
                Message message = Message.obtain();
                JSONObject object = JSON.parseObject(result);
                if (object.getBoolean("flag")) {
                    message.what = 4;
                    message.obj = object.getString("message");

                    //重新刷新购物车数据
                    initData();
                } else {
                    message.what = 0;
                }
                uiHandler.sendMessage(message);
            }
        });
    }

    /**
     * 结算订单、支付
     */
    private void pay() {
        String cIds = "";
        String numbers = "";

        //选中的需要提交的商品清单
        for (CartModel bean : shoppingCartBeanList) {
            boolean choosed = bean.isChoosed();
            if (choosed) {
                //拼接商品id
                cIds += bean.getId() + ",";
                //拼接商品数量
                numbers += bean.getNumber() + ",";
            }
        }

        //清理末尾逗号
        cIds = cIds.substring(0, cIds.lastIndexOf(","));
        numbers = numbers.substring(0, numbers.lastIndexOf(","));

        //构造url
        String url = Constants.BASE_URL + Constants.POST_BUY_URL;
        //构造请求体
        RequestBody body = new FormBody.Builder()
                .add("uId", uId + "")
                .add("cIds", cIds)
                .add("numbers", numbers)
                .build();
        //向服务器发送购买请求
        NetUtil.doPost(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string().trim();
                Message message = Message.obtain();
                JSONObject object = JSON.parseObject(result);
                if (object.getBoolean("flag")) {
                    message.what = 2;
                    message.obj = object.getString("message");
                } else {
                    message.what = 0;
                }
                uiHandler.sendMessage(message);
            }
        });
    }

    /**
     * 单选
     *
     * @param position  组元素位置
     * @param isChecked 组元素选中与否
     */
    @Override
    public void checkGroup(int position, boolean isChecked) {
        shoppingCartBeanList.get(position).setChoosed(isChecked);
        if (isAllCheck()) {
            ckAll.setChecked(true);
        } else {
            ckAll.setChecked(false);
        }
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }

    /**
     * 遍历list集合
     *
     * @return
     */
    private boolean isAllCheck() {

        for (CartModel group : shoppingCartBeanList) {
            if (!group.isChoosed())
                return false;
        }
        return true;
    }

    /**
     * 统计操作
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作
     * 3.给底部的textView进行数据填充
     */
    public void statistics() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < shoppingCartBeanList.size(); i++) {
            CartModel model = shoppingCartBeanList.get(i);
            if (model.isChoosed()) {
                totalCount++;
                totalPrice += model.getCommodityPrice() * model.getNumber();
            }
        }

        //保留两位小数
        DecimalFormat df = new DecimalFormat("#.00");
        String str = df.format(totalPrice);
        tvShowPrice.setText("合计:" + str);
        tvSettlement.setText("结算(" + totalCount + ")");
    }

    /**
     * 增加
     *
     * @param position      组元素位置
     * @param showCountView 用于展示变化后数量的View
     * @param isChecked     子元素选中与否
     */
    @Override
    public void doIncrease(int position, View showCountView, boolean isChecked) {
        CartModel model = shoppingCartBeanList.get(position);
        int currentCount = model.getNumber();
        currentCount++;
        model.setNumber(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }

    /**
     * 删减
     *
     * @param position      组元素位置
     * @param showCountView 用于展示变化后数量的View
     * @param isChecked     子元素选中与否
     */
    @Override
    public void doDecrease(int position, View showCountView, boolean isChecked) {
        CartModel model = shoppingCartBeanList.get(position);
        int currentCount = model.getNumber();
        if (currentCount == 1) {
            return;
        }
        currentCount--;
        model.setNumber(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }

    /**
     * 删除
     *
     * @param position
     */
    @Override
    public void childDelete(int position) {
        //向服务器发送删除请求
        String url = Constants.BASE_URL + Constants.POST_DELETE_URL;

        //构造请求体
        CartModel model = shoppingCartBeanList.get(position);
        int currentCount = model.getNumber();
        RequestBody body = new FormBody.Builder()
                .add("uId", uId + "")
                .add("cIds", model.getId() + "")
                .add("numbers", currentCount + "")
                .build();

        NetUtil.doPost(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string().trim();
                JSONObject object = JSON.parseObject(result);
                Message message = Message.obtain();
                if (object.getBoolean("flag")) {
                    message.what = 3;
                    message.obj = object.getString("message");
                } else {
                    message.what = 0;
                }
                uiHandler.sendMessage(message);
            }
        });


        //listView中remove掉item
        shoppingCartBeanList.remove(position);
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }
}
