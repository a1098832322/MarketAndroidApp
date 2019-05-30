package com.sqh.market.adapter;

import android.util.SparseArray;
import android.view.View;

/**
 * 用于adapter显示的viewHolder
 * Created by 郑龙 on 2017/7/14.
 */
public class MenuDialogAdapterViewHolder {
    //无参构造
    public MenuDialogAdapterViewHolder() {
    }

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();//节省内存，提高性能，使用SparseArray
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
