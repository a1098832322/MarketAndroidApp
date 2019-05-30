package com.sqh.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sqh.market.R;
import com.sqh.market.models.CommodityTypeModel;

import java.util.List;


/**
 * 要显示的主菜单的adapter
 * Created by zl on 2016/5/24.
 */
public class MenuDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommodityTypeModel> menuDatas;
    private int selectedPos = -1;

    public MenuDialogAdapter(Context mContext, List<CommodityTypeModel> menuDatas) {
        this.mContext = mContext;
        this.menuDatas = menuDatas;
    }

    //选中的position,及时更新数据
    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    //添加绑定的数据源
    public void setData(List<CommodityTypeModel> data) {
        this.menuDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (menuDatas == null) {
            return 0;
        }
        return menuDatas.size();
    }

    @Override
    public Object getItem(int position) {
        if (menuDatas == null) {
            return null;
        }
        return menuDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_menu_item, null);
        }


        //绑定item内的TextView和RadioButton
        TextView nameText = MenuDialogAdapterViewHolder.get(convertView, R.id.menu_item_textview);
        RadioButton clickButton = MenuDialogAdapterViewHolder.get(convertView, R.id.radioButton);
        clickButton.setChecked(selectedPos == position);//改变点击选中状态

        //修改item高度,使其达到甲方要求的每页10个item显示要求
        ViewGroup.LayoutParams lp = nameText.getLayoutParams();
        lp.height = parent.getHeight() / 10;

        //获取选中的item的标题
        CommodityTypeModel menuData = menuDatas.get(position);
        String str = menuData.getName();
        nameText.setText(str);//设置标题

        convertView.setSelected(selectedPos == position);//设置选中时的view
        nameText.setSelected(selectedPos == position);//判断菜单的点击状态

        //选中后的标题字体及RadioButton颜色
        nameText.setTextColor(selectedPos == position ? 0xFF387ef5 : 0xFF222222);
        clickButton.setTextColor(selectedPos == position ? 0xFF787878 : 0xFF387ef5);

        return convertView;
    }
}
