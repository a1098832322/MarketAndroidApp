package com.sqh.market.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sqh.market.R;
import com.sqh.market.models.CartModel;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 购物车Adapter
 */
public class ShoppingCartAdapter extends BaseAdapter {
    private boolean isShow = true;//是否显示编辑/完成
    private List<CartModel> shoppingCartBeanList;
    private CheckInterface checkInterface;
    private ModifyCountInterface modifyCountInterface;
    private Context context;

    public ShoppingCartAdapter(Context context) {
        this.context = context;
        //实例化ImageLoader
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
    }

    public void setShoppingCartBeanList(List<CartModel> shoppingCartBeanList) {
        this.shoppingCartBeanList = shoppingCartBeanList;
        notifyDataSetChanged();
    }

    /**
     * 单选接口
     *
     * @param checkInterface
     */
    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    /**
     * 改变商品数量接口
     *
     * @param modifyCountInterface
     */
    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }

    @Override
    public int getCount() {
        return shoppingCartBeanList == null ? 0 : shoppingCartBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return shoppingCartBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * 是否显示可编辑
     *
     * @param flag
     */
    public void isShow(boolean flag) {
        isShow = flag;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CartModel cartModel = shoppingCartBeanList.get(position);

        if (cartModel.isChoosed()) {
            holder.ckOneChose.setChecked(true);
        } else {
            holder.ckOneChose.setChecked(false);
        }

        if (!StringUtils.isEmpty(cartModel.getCommodityInfo())) {
            //商品简介最多只显示40个字符
            holder.tvCommodityAttr.setText(cartModel.getCommodityInfo().length() > 40
                    ? cartModel.getCommodityInfo().substring(0, 36) + "......"
                    : cartModel.getCommodityInfo());
        } else {
            holder.tvCommodityAttr.setText("");
        }
        holder.tvCommodityName.setText(cartModel.getCommodityName());
        holder.tvCommodityPrice.setText(cartModel.getCommodityPrice() + "");
        holder.tvCommodityNum.setText(" X" + cartModel.getNumber() + "");
        holder.tvCommodityShowNum.setText(cartModel.getNumber() + "");
        ImageLoader.getInstance().displayImage(cartModel.getCommodityImg(), holder.ivShowPic);
        //单选框按钮
        holder.ckOneChose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cartModel.setChoosed(((CheckBox) v).isChecked());
                        checkInterface.checkGroup(position, ((CheckBox) v).isChecked());//向外暴露接口
                    }
                }
        );
        //增加按钮
        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCountInterface.doIncrease(position, holder.tvCommodityShowNum, holder.ckOneChose.isChecked());//暴露增加接口
            }
        });
        //删减按钮
        holder.ivSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCountInterface.doDecrease(position, holder.tvCommodityShowNum, holder.ckOneChose.isChecked());//暴露删减接口
            }
        });
        //删除弹窗
        holder.tvCommodityDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(context).create();
                alert.setTitle("操作提示");
                alert.setMessage("您确定要将这些商品从购物车中移除吗？");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                modifyCountInterface.childDelete(position);//从购物车中删除

                            }
                        });
                alert.show();
            }
        });
        //判断是否在编辑状态下
        if (isShow) {
            holder.tvCommodityName.setVisibility(View.VISIBLE);
            holder.rlEdit.setVisibility(View.GONE);
            holder.tvCommodityNum.setVisibility(View.VISIBLE);
            holder.tvCommodityDelete.setVisibility(View.GONE);
        } else {
            holder.tvCommodityName.setVisibility(View.VISIBLE);
            holder.rlEdit.setVisibility(View.VISIBLE);
            holder.tvCommodityNum.setVisibility(View.GONE);
            holder.tvCommodityDelete.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    //初始化控件
    class ViewHolder {
        ImageView ivShowPic, tvCommodityDelete;
        TextView tvCommodityName, tvCommodityAttr, tvCommodityPrice, tvCommodityNum, tvCommodityShowNum, ivSub, ivAdd;
        CheckBox ckOneChose;
        LinearLayout rlEdit;

        public ViewHolder(View itemView) {
            ckOneChose = itemView.findViewById(R.id.ck_chose);
            ivShowPic = itemView.findViewById(R.id.iv_show_pic);
            ivSub = itemView.findViewById(R.id.iv_sub);
            ivAdd = itemView.findViewById(R.id.iv_add);
            tvCommodityName = itemView.findViewById(R.id.tv_commodity_name);
            tvCommodityAttr = itemView.findViewById(R.id.tv_commodity_attr);
            tvCommodityPrice = itemView.findViewById(R.id.tv_commodity_price);
            tvCommodityNum = itemView.findViewById(R.id.tv_commodity_num);
            tvCommodityShowNum = itemView.findViewById(R.id.tv_commodity_show_num);
            tvCommodityDelete = itemView.findViewById(R.id.tv_commodity_delete);
            rlEdit = itemView.findViewById(R.id.rl_edit);
        }
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param position  元素位置
         * @param isChecked 元素选中与否
         */
        void checkGroup(int position, boolean isChecked);
    }


    /**
     * 改变数量的接口
     */
    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param position      元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        void doIncrease(int position, View showCountView, boolean isChecked);

        /**
         * 删减操作
         *
         * @param position      元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        void doDecrease(int position, View showCountView, boolean isChecked);

        /**
         * 删除子item
         *
         * @param position
         */
        void childDelete(int position);
    }
}
