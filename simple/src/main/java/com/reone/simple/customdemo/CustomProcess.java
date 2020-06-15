package com.reone.simple.customdemo;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.process.CacheProcess;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:自定义缓存过程
 */
public class CustomProcess extends CacheProcess {

    public CustomProcess(ThumbProvider thumbProvider) {
        super(thumbProvider);
    }

    @Override
    public void start() {

    }

    @Override
    public Bitmap get(int index) {
        return getThumbProvider().getIndex(index);
    }

    @Override
    public void release() {

    }
}