package com.sqh.market.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sqh.market.R;
import com.sqh.market.activity.SearchActivity;
import com.sqh.market.models.CommodityTypeModel;

import java.util.List;

/**
 * GridView加载数据adapter
 */
public class HomePageGridViewAdapter extends BaseAdapter {

    private List<CommodityTypeModel> listData;
    private LayoutInflater inflater;
    private Context context;
    private int mIndex;//页数下标，表示第几页，从0开始
    private int mPagerSize;//每页显示的最大数量

    public HomePageGridViewAdapter(Context context, List<CommodityTypeModel> listData, int mIndex, int mPagerSize) {
        this.context = context;
        this.listData = listData;
        this.mIndex = mIndex;
        this.mPagerSize = mPagerSize;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？listData.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量mPagerSize的个数
     * 如果不够显示每页的最大数量，那么剩下几个就显示几个 (listData.size() - mIndex*mPagerSize)
     */
    @Override
    public int getCount() {
        return listData.size() > (mIndex + 1) * mPagerSize ? mPagerSize : (listData.size() - mIndex * mPagerSize);
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position + mIndex * mPagerSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPagerSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            holder = new ViewHolder();
            holder.proName = convertView.findViewById(R.id.proName);
            holder.imgUrl = convertView.findViewById(R.id.imgUrl);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //重新确定position（因为拿到的是总的数据源，数据源是分页加载到每页的GridView上的，为了确保能正确的点对不同页上的item）
        final int pos = position + mIndex * mPagerSize;//假设mPagerSize=8，假如点击的是第二页（即mIndex=1）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9

        CommodityTypeModel model = listData.get(pos);
        holder.proName.setText(model.getName());
        final Integer commodityId = model.getId().intValue();
        Glide.with(this.context)
                .load(model.getIconPath())
                .into(holder.imgUrl);
        //添加item监听
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent跳转
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("id", commodityId);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView proName;
        private ImageView imgUrl;
    }
}
