package com.sqh.market.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqh.market.R;
import com.sqh.market.activity.DetailsActivity;
import com.sqh.market.activity.SearchViewActivity;
import com.sqh.market.adapter.CommodityItemAdapter;
import com.sqh.market.adapter.MenuDialogAdapter;
import com.sqh.market.constant.Constants;
import com.sqh.market.models.CommodityModel;
import com.sqh.market.models.CommodityTypeModel;
import com.sqh.market.utils.NetUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 主要Activity，使用时需要改成FragmentActivity
 * Created by 郑龙 on 2017/7/10.
 */

public class Classify extends Fragment {
    private Handler handler;

    private Context mContext;
    private ListView mCommodityTypeListView, mCommodityListView;
    private MenuDialogAdapter mListView1Adapter;
    private CommodityItemAdapter mListView2Adapter;
    /**
     * 搜索框
     */
    private SearchView searchView;

    private View mProgressBar;

    /**
     * 商品类型菜单list数据源
     */
    private List<CommodityTypeModel> commodityTypeList;

    /**
     * 商品List数据源
     */
    private List<CommodityModel> commodityList;

    /**
     * 用于从网络初始化UI的handler
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    //网络请求失败
                    Toast.makeText(mContext, "网络请求失败！获取商品类别失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    //网络请求成功，但是返回状态为失败
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //网络请求商品类别返回成功时，初始化控件
                    //绑定数据以及设置Adapter
                    mListView1Adapter = new MenuDialogAdapter(mContext, commodityTypeList);
                    mCommodityTypeListView.setAdapter(mListView1Adapter);
                    break;
                case 2:
                    //成功获取商品列表
                    if (mListView2Adapter == null) {
                        mListView2Adapter = new CommodityItemAdapter(mContext, commodityList, handler);//方便调试时Toast输出提示所以传入Handler，否则可用上一个
                        mCommodityListView.setAdapter(mListView2Adapter);
                    } else {
                        mListView2Adapter.setData(commodityList);
                        mListView2Adapter.notifyDataSetChanged();//刷新
                    }
                    break;

            }

            //显示列表，隐藏进度条
            loadingAnim(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, null);
        mContext = getActivity();

        //加载view
        initViews(view);

        //从服务器获取商品类别列表
        getDataFromServer();

        handler = new Handler();


        //设置searchView点击事件
        setSearchViewIntent();

        return view;
    }

    /**
     * searchView点击跳转
     */
    public void setSearchViewIntent() {
        searchView.setFocusable(false);
        searchView.clearFocus();

        //设置搜索框焦点
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    searchView.clearFocus();
                else {
                    Intent intent = new Intent(getActivity(), SearchViewActivity.class);
                    intent.putExtra("fatherName", ".Classify");
                    startActivityForResult(intent, 1);
                }
            }
        });
    }


    /**
     * 从网络获取初始数据
     */
    private void getDataFromServer() {
        String categoryTypeUrl = Constants.BASE_URL + Constants.GET_COMMODITY_TYPES_URL;
        NetUtil.doGet(categoryTypeUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("网络请求失败！", "网络请求失败！获取商品类别列表失败！");
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
                    message.what = 1;
                    //获得商品类型数据
                    List<CommodityTypeModel> types = com.alibaba.fastjson.JSONArray
                            .parseArray(JSON.toJSONString(obj.get("data"))
                                    , CommodityTypeModel.class);
                    commodityTypeList = types;
                } else {
                    message.what = 0;
                }

                message.obj = obj.getString("message");
                uiHandler.sendMessage(message);
            }
        });
    }


    /**
     * 通过商品类别id获取商品列表
     *
     * @param typeId 商品类别id
     */
    private void getCommodityFromServerByTypeId(Long typeId) {
        //拼接访问字符串
        String url = Constants.BASE_URL + Constants.GET_COMMODITY_URL;
        if (typeId != null && typeId > 0L) {
            url = url + "?commodityType=" + typeId;
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
                    JSONObject data = (JSONObject) obj.get("data");

                    //获得商品类型数据
                    List<CommodityModel> types = com.alibaba.fastjson.JSONArray
                            .parseArray(JSON.toJSONString(data.get("lists"))
                                    , CommodityModel.class);
                    commodityList = types;
                } else {
                    message.what = 0;
                }

                message.obj = obj.getString("message");
                uiHandler.sendMessage(message);
            }
        });
    }

    /**
     * 当进行网络请求时，播放进度条动画
     *
     * @param isLoading 是否正在网络请求
     */
    private void loadingAnim(boolean isLoading) {
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mCommodityListView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    /**
     * 绑定操作控件
     *
     * @param view
     */
    private void initViews(View view) {
        //一级菜单，绑定两个ListView控件
        mCommodityTypeListView = view.findViewById(R.id.left);
        mCommodityListView = view.findViewById(R.id.right);
        mProgressBar = view.findViewById(R.id.progress);

        searchView = view.findViewById(R.id.home_serachview);

        //商品类别菜单点击事件
        mCommodityTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView1Adapter != null) {
                    mListView1Adapter.setSelectedPos(position);
                }
                if (mListView2Adapter != null) {
                    mListView2Adapter.setSelectedPos(-1);
                }

                CommodityTypeModel menuData = (CommodityTypeModel) parent.getItemAtPosition(position);

                //根据类型id从服务器获取商品列表
                getCommodityFromServerByTypeId(menuData.getId());
                //加载中动效
                loadingAnim(true);
            }
        });

        //商品item点击事件
        mCommodityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CommodityModel commodity = (CommodityModel) adapterView.getItemAtPosition(position);
                //跳转到详情
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id", commodity.getId() + "");
                startActivity(intent);
            }
        });
    }

}
