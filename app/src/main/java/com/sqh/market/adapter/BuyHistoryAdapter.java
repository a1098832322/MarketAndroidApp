package com.sqh.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sqh.market.R;
import com.sqh.market.models.CartModel;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 购买历史Adapter
 *
 * @author 郑龙
 */
public class BuyHistoryAdapter extends BaseAdapter {
    private List<CartModel> boughtCommodityList;
    private Context context;

    public BuyHistoryAdapter(Context context) {
        this.context = context;
        //实话ImageLoader
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
    }

    public void setBoughtCommodityList(List<CartModel> shoppingCartBeanList) {
        this.boughtCommodityList = shoppingCartBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return boughtCommodityList == null ? 0 : boughtCommodityList.size();
    }

    @Override
    public Object getItem(int position) {
        return boughtCommodityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_buy, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CartModel cartModel = boughtCommodityList.get(position);

        if (!StringUtils.isEmpty(cartModel.getCommodityInfo())) {
            //商品简介最多只显示40个字符
            holder.tvCommodityAttr.setText(cartModel.getCommodityInfo().length() > 50
                    ? cartModel.getCommodityInfo().substring(0, 46) + "......"
                    : cartModel.getCommodityInfo());
        } else {
            holder.tvCommodityAttr.setText("");
        }
        holder.tvCommodityName.setText(cartModel.getCommodityName());
        holder.tvCommodityPrice.setText("共计： ￥"
                + (cartModel.getCommodityPrice()
                * cartModel.getNumber()) + "");
        holder.tvCommodityNum.setText(" X" + cartModel.getNumber() + "");
        ImageLoader.getInstance().displayImage(cartModel.getCommodityImg(), holder.ivShowPic);

        return convertView;
    }

    //初始化控件
    class ViewHolder {
        ImageView ivShowPic;
        TextView tvCommodityName, tvCommodityAttr, tvCommodityPrice, tvCommodityNum;

        public ViewHolder(View itemView) {
            ivShowPic = itemView.findViewById(R.id.iv_show_pic);
            tvCommodityName = itemView.findViewById(R.id.tv_commodity_name);
            tvCommodityAttr = itemView.findViewById(R.id.tv_commodity_attr);
            tvCommodityPrice = itemView.findViewById(R.id.tv_commodity_price);
            tvCommodityNum = itemView.findViewById(R.id.tv_commodity_num);
        }
    }
}
