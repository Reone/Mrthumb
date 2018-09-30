package com.reone.mrthumb.listener;

/**
 * Created by wangxingsheng on 2018/9/30.
 */
public interface ProcessListener {
    void onProcess(int index, int cacheCount, int maxCount, long time, long duration);
}