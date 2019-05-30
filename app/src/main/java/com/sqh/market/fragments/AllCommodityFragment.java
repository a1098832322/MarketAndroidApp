package com.sqh.market.fragments;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqh.market.R;
import com.sqh.market.activity.DetailsActivity;
import com.sqh.market.activity.SearchViewActivity;
import com.sqh.market.adapter.CommodityItemAdapter;
import com.sqh.market.constant.Constants;
import com.sqh.market.models.CommodityModel;
import com.sqh.market.utils.NetUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 搜索activity中显示的view
 */
public class AllCommodityFragment extends Fragment {


    /**
     * 刷新的layout
     */
    private SwipeRefreshLayout mRefreshLayout;
    private ListView mListView;

    private View mProgressBar;

    /**
     * 传入的商品类型id
     */
    private int commodityTypeId = 0;

    private String searchContent = null;

    /**
     * 适配器
     */
    private CommodityItemAdapter mAdapter;

    private SearchView searchView;      //搜索栏

    /**
     * 当前页的页码
     */
    private int pageIndex = 1;


    /**
     * 商品的list
     */
    private List<CommodityModel> mCommodityList = new ArrayList<>();

    /**
     * 设置商品类型
     *
     * @param commodityTypeId 商品类型id
     */
    public void setCommodityTypeId(int commodityTypeId) {
        this.commodityTypeId = commodityTypeId;
    }

    /**
     * 设置搜索的商品名
     *
     * @param searchContent 搜索文本
     */
    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    /**
     * 用于从网络初始化UI的handler
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    //网络请求失败
                    Toast.makeText(getActivity().getApplicationContext(), "网络请求失败！获取商品类别失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    //网络请求成功，但是返回状态为失败
                    Toast.makeText(getActivity().getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //成功获取商品列表
                    if (mAdapter == null) {
                        mAdapter = new CommodityItemAdapter(getActivity(), mCommodityList);
                        mListView.setAdapter(mAdapter);
                    } else {
                        mAdapter.setData(mCommodityList);
                        //刷新List显示
                        mAdapter.notifyDataSetChanged();
                    }
                    break;

            }

            //判断刷新动画
            if (mRefreshLayout.isRefreshing()) {
                //停止动画
                mRefreshLayout.setRefreshing(false);
            }

            //显示列表，隐藏进度条
            loadingAnim(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_commodity, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = view.findViewById(R.id.dataList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化控件
        init();

        //设置从别的页面传过来的搜索内容
        Intent intent = getActivity().getIntent();
        //从Intent中获取id 和 筛选条件
        commodityTypeId = intent.getIntExtra("id", 0);

        if (commodityTypeId > 0) {
            //播放加载动画
            loadingAnim(true);
            //从服务器获取数据
            getCommodityFromServerByTypeId((long) commodityTypeId, pageIndex);
        } else if (StringUtils.isNotBlank(searchContent)) {
            //播放加载动画
            loadingAnim(true);
            //从服务器获取数据
            getCommodityFromServerBySearchName(searchContent);
        }

        //设置搜索框默认显示文本
        searchView.setQueryHint("搜索商品....");


        //设置搜索栏监听
        setSearchViewIntent();


        //设置列表Item点击监听事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CommodityModel model = mCommodityList.get(i);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id", model.getId() + "");
                startActivity(intent);
            }
        });

        // 监听刷新操作
        mRefreshLayout.setOnRefreshListener(mRefreshListener);

    }

    /**
     * 当进行网络请求时，播放进度条动画
     *
     * @param isLoading 是否正在网络请求
     */
    private void loadingAnim(boolean isLoading) {
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mListView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }


    /**
     * 通过输入的商品名称搜索商品列表
     *
     * @param searchContent 商品名
     */
    private void getCommodityFromServerBySearchName(String searchContent) {
        //拼接访问字符串
        String url = Constants.BASE_URL + Constants.GET_COMMODITY_BY_NAME_URL;
        if (StringUtils.isNotBlank(searchContent)) {
            url = url + searchContent;
        } else {
            //搜索文本为空时，不作操作
            return;
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
                    message.what = 1;

                    //获得商品列表数据
                    List<CommodityModel> types = com.alibaba.fastjson.JSONArray
                            .parseArray(JSON.toJSONString(obj.getJSONArray("data"))
                                    , CommodityModel.class);
                    mCommodityList = types;

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
     * @param page   当前页
     */
    private void getCommodityFromServerByTypeId(Long typeId, Integer page) {
        //拼接访问字符串
        String url = Constants.BASE_URL + Constants.GET_COMMODITY_URL + "?1=1";
        if (typeId != null && typeId > 0L) {
            url = url + "&commodityType=" + typeId;
        }

        if (page != null && page > 0) {
            url = url + "&page=" + page;
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
                    message.what = 1;

                    //拿到data内数据
                    JSONObject data = (JSONObject) obj.get("data");

                    //获得商品类型数据
                    List<CommodityModel> types = com.alibaba.fastjson.JSONArray
                            .parseArray(JSON.toJSONString(data.get("lists"))
                                    , CommodityModel.class);
                    mCommodityList = types;

                    pageIndex = data.getInteger("currentPage");
                } else {
                    message.what = 0;
                }

                message.obj = obj.getString("message");
                uiHandler.sendMessage(message);
            }
        });
    }


    /**
     * 初始化控件。
     */
    public void init() {
        mListView = getView().findViewById(R.id.dataList);
        mRefreshLayout = getView().findViewById(R.id.refresh_layout);
        searchView = getView().findViewById(R.id.serachview);
        mProgressBar = getView().findViewById(R.id.progressLayout);
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (commodityTypeId > 0) {
                //如果是根据商品类型id来查询
                getCommodityFromServerByTypeId((long) commodityTypeId, pageIndex);
            } else if (StringUtils.isNotBlank(searchContent)) {
                //按商品名来查询
                getCommodityFromServerBySearchName(searchContent);
            }
        }
    };

    /**
     * 加载更多。
     */
    private SwipeMenuRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeMenuRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (commodityTypeId > 0) {
                //如果是根据商品类型id来查询
                pageIndex++;
                getCommodityFromServerByTypeId((long) commodityTypeId, pageIndex);
            } else if (StringUtils.isNotBlank(searchContent)) {
                //按商品名来查询
                getCommodityFromServerBySearchName(searchContent);
            }


        }
    };


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
                    intent.putExtra("fatherName", ".AllCommondityFragment");
                    startActivityForResult(intent, 1);
                }
            }
        });
    }
}
