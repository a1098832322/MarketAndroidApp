package com.sqh.market.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqh.market.R;
import com.sqh.market.adapter.BuyHistoryAdapter;
import com.sqh.market.constant.Constants;
import com.sqh.market.models.CartModel;
import com.sqh.market.utils.NetUtil;
import com.sqh.market.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 历史购买Activity
 *
 * @author 郑龙
 * @date 2019/4/8 8:39
 */
public class BuyHistoryActivity extends AppCompatActivity {
    /**
     * 返回按钮
     */
    private ImageView btnBack;

    /**
     * 商品list
     */
    private ListView mBoughtHistoryList;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    /**
     * 商品列表adapter
     */
    private BuyHistoryAdapter adapter;

    /**
     * 已购买商品列表
     */
    private List<CartModel> boughtCommodityList = new ArrayList<>();

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
                    //网络请求购买历史信息数据成功
                    adapter = new BuyHistoryAdapter(mContext);
                    mBoughtHistoryList.setAdapter(adapter);
                    adapter.setBoughtCommodityList(boughtCommodityList);

                    //取消动画显示
                    mBoughtHistoryList.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        mContext = this;

        //从SharedPreferences中获取用户id
        uId = (Integer) SharedPreferencesUtil
                .get(mContext, "userInfo", "userId", 0);

        //绑定控件
        init();

        //从网络获取购买历史数据
        initData();
    }

    /**
     * 初始化绑定控件
     */
    private void init() {
        btnBack = findViewById(R.id.buy_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBoughtHistoryList = findViewById(R.id.buy_list);
        mBoughtHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //拿到商品id
                CartModel model = boughtCommodityList.get(position);
                Long commodityId = model.getId();

                //点击跳转到商品详情
                Intent intent = new Intent(BuyHistoryActivity.this, DetailsActivity.class);
                intent.putExtra("id", commodityId + "");
                startActivity(intent);
            }
        });

        mProgressBar = findViewById(R.id.progress);
    }

    /**
     * 从网络获取购物车数据
     */
    protected void initData() {
        //拼接url
        String url = Constants.BASE_URL + Constants.GET_QUERY_BUY_HISTORY_URL + "&uId=" + uId;
        NetUtil.doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string().trim();
                Log.e("result", result);
                JSONObject object = JSON.parseObject(result);
                Message message = Message.obtain();
                if (object.getBoolean("flag")) {
                    message.what = 1;

                    //解析拿到data中的lists数据
                    JSONArray lists = ((JSONObject) object.get("data")).getJSONArray("lists");

                    //将list传递给数据集
                    boughtCommodityList = JSONArray.parseArray(JSON.toJSONString(lists), CartModel.class);
                } else {
                    message.what = 0;
                }

                uiHandler.sendMessage(message);
            }
        });


    }
}
