package com.sqh.market.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * SQLite工具类
 *
 * @author 郑龙
 */

public class SQLUtils {
    private static SQLiteHelper mHelper = null;

    /**
     * 获取数据表中所有数据
     *
     * @return Cursor
     */
    public Cursor selectAllData() {
        return mHelper.select();
    }


    /**
     * 依据条件进行模糊查询
     *
     * @param tag
     * @return ArrayList
     */
    public ArrayList<String> searchByCondition(String... tag) {
        return mHelper.query(tag);
    }

    public long insertNewSearchHistory(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        return mHelper.insert(text, time);
    }

    public void deleteAllData() {
        mHelper.delete();
    }

    public SQLUtils(Context context) {
        mHelper = SQLiteHelper.getInstance(context);
    }


    /**
     * 内部类，SQLiteHelper，提供了基础的数据库创建表，增删改查等操作
     *
     * @author 郑龙
     */
    public static class SQLiteHelper extends SQLiteOpenHelper {
        /**
         * 数据库插入错误代码
         */
        public static final int DATABASE_INSERT_ERROR = -1;

        private static int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "Search_History";
        private static final String TABLE_NAME = "Search_Table";
        private static SQLiteHelper mHelper = null;

        private static Context mContext;

        /**
         * 单例设计模式，顺手将构造方法设为私有
         *
         * @return SQLiteHelper
         */
        public static SQLiteHelper getInstance(Context context) {
            if (mHelper == null) {
                mHelper = new SQLiteHelper(context);
            }
            mContext = context;
            return mHelper;
        }

        private SQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (text VARCHAR(30) PRIMARY KEY, time VARCHAR(20))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        /**
         * 获取表单所有数据
         *
         * @return Cursor
         */
        public Cursor select() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            return cursor;
        }

        /**
         * 根据条件模糊查询数据库数据
         *
         * @param str
         * @return
         */
        public ArrayList<String> query(String... str) {
            ArrayList<String> result_list = new ArrayList<String>();
            SQLiteDatabase db = this.getReadableDatabase();
            //模糊查询的三种方式：
/*
 * 全部查询
        String current_sql_sel = "SELECT  * FROM " + tab_name;
        Cursor c = mDatabase.rawQuery(current_sql_sel, null);*/

            //1.使用这种query方法%号前不能加' ;
            //        Cursor c_test = mDatabase.query(tab_name, new String[]{tab_field02}, tab_field02+"  LIKE ? ",
            //                new String[] { "%" + str[0] + "%" }, null, null, null);

            //2.使用这种query方法%号前必须加'  ;
            //  Cursor  c_test=mDatabase.query(tab_name, new String[]{tab_field02},tab_field02+"  like '%" + str[0] + "%'", null, null, null, null);

            //3.使用这种方式必须在%号前加'  ;
            String current_sql_sel = "SELECT  * FROM " + TABLE_NAME + " where text like '%" + str[0] + "%'";
            Cursor c_test = db.rawQuery(current_sql_sel, null);

            Log.d("tag", "查询完成...");
            while (c_test.moveToNext()) {
                String name = c_test.getString(c_test.getColumnIndex("text"));
                //name.contains(str[0]);
                // 让集合中的数据不重复;
                if (!result_list.contains(name)) {
                    result_list.add(name);
                    Log.d("tag", name);
                }
            }
            c_test.close();

            return result_list;
        }


        /**
         * 插入一条记录
         *
         * @param text
         * @param time
         * @return 结果
         */
        public long insert(String text, String time) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            try {
                cv.put("text", text);
                cv.put("time", time);

                long row = db.insert(TABLE_NAME, null, cv);
                return row;
            } catch (Exception e) {
                Log.e("插入错误！", "RT");
            }

            return DATABASE_INSERT_ERROR;
        }

        /**
         * 删除表中记录的所有数据
         */
        public void delete() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_NAME);
        }
    }
}