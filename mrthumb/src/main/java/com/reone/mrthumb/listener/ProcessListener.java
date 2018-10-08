package com.reone.mrthumb.listener;

/**
 * Created by wangxingsheng on 2018/9/30.
 */
public interface ProcessListener {

    /**
     * 缩略图加载进度回调
     * @param index 缩略图加载位置
     * @param cacheCount 已缓存数量
     * @param maxCount 需要缓存总数
     * @param time 缓存缩略图所在秒数
     * @param duration 视频总时长
     */
    void onProcess(int index, int cacheCount, int maxCount, long time, long duration);
}