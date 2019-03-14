package com.reone.mrthumb.process;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ThumbProvider;

/**
 * Created by wangxingsheng on 2018/10/17.
 * desc: 缓存过程，不同的实现类，使缓存通过不同的顺序进行缩略图获取，以及缓存中缩略图的使用
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

    /**
     * 开始获取缩略图存入缓存
     */
    public abstract void start();

    /**
     * 从缓存中获取缩略图
     *
     * @param index 缩略图在数组中的下标
     * @return 缩略图
     */
    public abstract Bitmap get(int index);

    /**
     * 回收缩略图缓存
     */
    public abstract void release();
}
