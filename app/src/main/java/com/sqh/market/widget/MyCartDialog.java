package com.sqh.market.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.sqh.market.R;
import com.sqh.market.callbacks.BaseCallback;

import org.apache.commons.lang3.StringUtils;

/**
 * 购物车及购买按钮点击后弹出的小dialog
 * (二次封装github开源dialog组件)
 * url：
 * https://github.com/orhanobut/dialogplus
 *
 * @author 郑龙
 */
public class MyCartDialog {
    private Context mContext;

    public MyCartDialog(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 创建dialog
     *
     * @param callback 回调
     */
    public DialogPlus createDialog(final BaseCallback callback) {
        return DialogPlus.newDialog(mContext)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.dialog_cart_layout))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        View contentView = dialog.getHolderView();
                        EditText editText = contentView.findViewById(R.id.et_count);
                        int count = Integer.parseInt(StringUtils.isBlank(editText.getText().toString()) ? "1"
                                : editText.getText().toString());

                        switch (view.getId()) {
                            case R.id.btn_close:
                                dialog.dismiss();
                                break;
                            case R.id.iv_count_minus:
                                if (count > 1) {
                                    count -= 1;
                                }
                                editText.setText(count + "");
                                break;
                            case R.id.iv_count_add:
                                editText.setText((++count) + "");
                                break;
                            case R.id.btn_ok:
                                String number = StringUtils.isBlank(editText.getText().toString()) ? "1"
                                        : editText.getText().toString();
                                editText.setText(number);
                                callback.sendMessage(number);
                                break;
                            default:
                                break;

                        }
                    }
                }).create();
    }

}
