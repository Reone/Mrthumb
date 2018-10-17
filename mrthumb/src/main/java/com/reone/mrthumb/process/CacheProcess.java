package com.reone.mrthumb.process;

import android.graphics.Bitmap;

/**
 * Created by wangxingsheng on 2018/10/17.
 * desc: 缓存程序
 */
public interface CacheProcess {
    void start();

    void stop();

    void pause();

    Bitmap get();
}
