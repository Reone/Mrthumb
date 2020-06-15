package com.reone.mrthumb.creator;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.manager.BaseThumbManager;

import java.util.ArrayList;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:
 */
public interface ThumbManagerCreator {
    /**
     * 获取ThumbManager
     *
     * @param processListeners 添加在Mrthumb上的监听
     * @return ThumbManager不能为空
     */
    BaseThumbManager createThumbManager(int count, ArrayList<ProcessListener> processListeners);
}