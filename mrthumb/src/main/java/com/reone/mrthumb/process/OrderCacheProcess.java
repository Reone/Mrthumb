package com.reone.mrthumb.process;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ThumbProvider;

/**
 * Created by wangxingsheng on 2018/10/17.
 * desc:顺序填充
 */
public class OrderCacheProcess extends CacheProcess{

    public OrderCacheProcess(ThumbProvider thumbProvider) {
        super(thumbProvider);
    }

    @Override
    void start() {

    }

    @Override
    Bitmap get(int index) {
        return null;
    }

    @Override
    void release() {

    }
}
