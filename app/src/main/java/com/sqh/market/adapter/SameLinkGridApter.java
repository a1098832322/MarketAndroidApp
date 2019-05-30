package com.sqh.market.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sqh.market.R;
import com.sqh.market.models.CommodityModel;
import com.sqh.market.utils.ImageUtil;

import java.util.List;


/**
 * GridView适配器
 *
 * @author 郑龙
 */
public class SameLinkGridApter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<CommodityModel> listItemSameLink;

    public SameLinkGridApter(Context context, List<CommodityModel> listItemRecommend) {
        this.context = context;
        this.listItemSameLink = listItemRecommend;
        inflater = LayoutInflater.from(context);
    }

    @Override

    public int getCount() {
        return listItemSameLink.size();
    }

    @Override
    public Object getItem(int i) {
        return listItemSameLink.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommodityViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_commodity_for_samelink, null);

            //布局文件中所有组件的对象封装到ViewHolder对象中
            holder = new CommodityViewHolder();
            holder.commodityName = convertView.findViewById(R.id.commodityName);
            holder.commodityInfo = convertView.findViewById(R.id.commodityInfo);
            holder.commodityPrice = convertView.findViewById(R.id.commodityPrice);
            holder.img = convertView.findViewById(R.id.commodityImg);
            //把ViewHolder对象封装到View对象中
            convertView.setTag(holder);

        } else {
            holder = (CommodityViewHolder) convertView.getTag();
        }


        //获取点击的子菜单的View
        CommodityModel commodity = listItemSameLink.get(position);
        String name = commodity.getCommodityName();

        //需要裁剪掉base64编码的头才能正常显示
        Bitmap bitmap = ImageUtil.base64ToBitmap(
                commodity.getCommodityImg()
                        .substring(commodity.getCommodityImg().indexOf("base64") + 6));

        holder.commodityName.setText(name);
        holder.commodityName.setTextSize(18);
        holder.img.setImageBitmap(bitmap);

        return convertView;
    }
}