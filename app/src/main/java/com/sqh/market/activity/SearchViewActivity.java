package com.sqh.market.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.sqh.market.R;
import com.sqh.market.utils.SQLUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * 搜索页
 *
 * @author 郑龙
 */

public class SearchViewActivity extends AppCompatActivity {
    private ArrayList<String> mStrs = new ArrayList<>();
    private SearchView mSearchView;
    private ListView mListView;
    private TextView history_tv;
    private TextView cancel_tv;

    private String mFatherContextName;

    private ArrayAdapter<String> adapter;
    private SQLUtils sqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_view);

        //获得父级Activity名字的一些信息
        Intent intent = getIntent();
        mFatherContextName = intent.getStringExtra("fatherName");

        //初始化ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //初始化绑定控件
        init();

        //初始化SQLite数据库
        initDB();

        //初始化搜索历史数据
        initData();

        //使用简单适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStrs);
        mListView.setAdapter(adapter);
        mListView.setTextFilterEnabled(true);
        mSearchView.setSubmitButtonEnabled(true);

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    // 清除ListVie的过滤
                    mListView.clearTextFilter();
                } else {
                    mListView.setFilterText(query);
                }

                //更新历史记录
                try {
                    if (query != null) {
                        mStrs.add(query);
                        long id = sqlite.insertNewSearchHistory(query);
                        Log.e("insert", id + "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mStrs.clear();
                initData();
                adapter = new ArrayAdapter<>(SearchViewActivity.this, android.R.layout.simple_list_item_1, mStrs);
                mListView.setAdapter(adapter);

                //获取搜索文本
                String searchContent = mSearchView.getQuery().toString();

                //如果搜索文本不为空则跳转
                if (StringUtils.isNotBlank(searchContent)) {
                    Intent intent = new Intent(SearchViewActivity.this, SearchActivity.class);
                    intent.putExtra("content", searchContent);//回传测试用JSON字符串
                    startActivity(intent);
                    //结束掉当前搜索页
                    finish();
                } else {
                    Toast.makeText(SearchViewActivity.this, "请输入搜索文本！", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    mListView.setFilterText(newText);
                } else {
                    mListView.clearTextFilter();
                }

                return false;
            }
        });

        //清空历史
        history_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite.deleteAllData();
                mStrs.clear();
                adapter = new ArrayAdapter<>(SearchViewActivity.this, android.R.layout.simple_list_item_1, mStrs);
                mListView.setAdapter(adapter);
            }
        });

        //点击搜索历史列表得到值显示在搜索栏上
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) ((TextView) view).getText();
                mSearchView.setQuery(str, false);

            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(SearchViewActivity.this, mFatherContextName);
                setResult(RESULT_CANCELED, intent);//记录返回标识
                finish();
            }
        });
    }

    /**
     * 初始化搜索历史数据
     */
    public void initData() {
        Cursor cursor;
        cursor = sqlite.selectAllData();

        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex("text");
            mStrs.add(cursor.getString(nameColumnIndex));
        }

    }

    /**
     * 初始化绑定控件
     */
    public void init() {
        mSearchView = findViewById(R.id.searchView);
        mListView = findViewById(R.id.listView);
        history_tv = findViewById(R.id.tv_history);
        cancel_tv = findViewById(R.id.tv_cancel);
    }

    /**
     * 初始化SQLite数据库
     */
    public void initDB() {
        sqlite = new SQLUtils(getApplicationContext());
    }

    /**
     * 点击返回按钮时，finish掉当前搜索页
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
