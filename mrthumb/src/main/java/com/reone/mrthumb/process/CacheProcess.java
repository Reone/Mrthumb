package com.reone.mrthumb.process;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ThumbProvider;

/**
 * Created by wangxingsheng on 2018/10/17.
 * desc: 缓存程序
 */
public abstract class CacheProcess {
    int maxSize;
    private ThumbProvider thumbProvider;

    public CacheProcess(ThumbProvider thumbProvider) {
        this.thumbProvider = thumbProvider;
        this.maxSize = thumbProvider.maxSize();
    }

    public ThumbProvider getThumbProvider() {
        return thumbProvider;
    }

    abstract void start();

    abstract Bitmap get(int index);

    abstract void release();
}
