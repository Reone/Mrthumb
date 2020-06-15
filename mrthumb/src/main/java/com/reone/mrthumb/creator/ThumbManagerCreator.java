package com.reone.mrthumb.creator;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.manager.BaseThumbManager;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:
 */
public interface ThumbManagerCreator<TMC extends BaseThumbManager> {
    /**
     * 开始获取缓存
     *
     * @param url             视频链接
     * @param headers         指定头
     * @param videoDuration   视频时长
     * @param retrieverType   解码器类型
     * @param thumbnailWidth  生成缩略图宽度
     * @param thumbnailHeight 生成缩略图高度
     */
    void onBuffer(TMC thumbManager, String url, Map<String, String> headers, long videoDuration, int retrieverType, int thumbnailWidth, int thumbnailHeight);

    /**
     * 获取ThumbManager
     *
     * @param processListeners 添加在Mrthumb上的监听
     * @return ThumbManager不能为空
     */
    TMC createThumbManager(int count, ArrayList<ProcessListener> processListeners);
}