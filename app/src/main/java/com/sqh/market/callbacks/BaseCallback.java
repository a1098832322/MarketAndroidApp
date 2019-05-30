package com.sqh.market.callbacks;

/**
 * 基础callback
 *
 * @author 郑龙
 */
public interface BaseCallback {
    /**
     * 发送必要数据
     *
     * @param obj 数据对象
     */
    void sendMessage(Object obj);
}
