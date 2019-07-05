package com.sqh.market.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sqh.market.R;
import com.sqh.market.models.CommodityModel;
import com.sqh.market.utils.ImageUtil;

import java.util.List;

/**
 * 要显示的商品的adapter
 *
 * @author 郑龙
 */
public class CommodityItemAdapter extends BaseAdapter {
    private Handler mHandler;
    private Context mContext;
    private List<CommodityModel> menuDatas;
    private int selectedPos = -1;
    private String mClickedItemName = null;

    //正常调用时的构造方法
    public CommodityItemAdapter(Context mContext, List<CommodityModel> menuDatas) {
        this.mContext = mContext;
        this.menuDatas = menuDatas;

        //实例化ImageLoader
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
    }

    //debug时使用的构造方法
    public CommodityItemAdapter(Context mContext, List<CommodityModel> menuDatas, Handler handler) {
        this.mContext = mContext;
        this.menuDatas = menuDatas;
        this.mHandler = handler;

        //实例化ImageLoader
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
    }

    //选中的position,及时更新数据
    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    //绑定数据源
    public void setData(List<CommodityModel> data) {
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
        CommodityViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_commodity_for_searchview, null);

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
        CommodityModel commodity = menuDatas.get(position);
        String name = commodity.getCommodityName();
        String info = commodity.getCommodityInfo();
        Double price = commodity.getCommodityPrice();

        holder.commodityInfo.setText(info);
        holder.commodityName.setText(name);
        holder.commodityPrice.setText("￥ " + price + "元");
        //绑定图片到控件
        ImageLoader.getInstance().displayImage(commodity.getCommodityOtherImgUrls(), holder.img);

        return convertView;
    }


}
