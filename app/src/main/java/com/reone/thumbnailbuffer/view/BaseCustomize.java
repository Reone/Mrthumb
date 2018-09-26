package com.reone.thumbnailbuffer.view;

import android.content.res.TypedArray;

/**
 * Created by wangxingsheng on 2018/6/29.
 *
 * 提供一些自定义所需的方法
 *
 */
public interface BaseCustomize {
    /**
     * 自定义布局
     * @return
     */
    int getLayoutResource();

    /**
     * 获取自定义属性
     * @return
     */
    int[] getStyleableResource();

    /**
     * 初始化view
     */
    void inflateView();

    /**
     * 设置自定义属性
     * @param typedArray
     */
    void customAttr(TypedArray typedArray);

    /**
     * 设置自定义事件
     */
    void initEventAndData();
}
