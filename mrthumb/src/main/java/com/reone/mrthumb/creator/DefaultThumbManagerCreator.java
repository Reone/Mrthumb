package com.reone.mrthumb.creator;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.manager.BaseThumbManager;
import com.reone.mrthumb.manager.DefaultThumbManager;

import java.util.ArrayList;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:
 */
public class DefaultThumbManagerCreator implements ThumbManagerCreator {

    @Override
    public BaseThumbManager createThumbManager(int count, final ArrayList<ProcessListener> processListeners) {
        DefaultThumbManager temp = new DefaultThumbManager(count);
        temp.setProcessListener(new ProcessListener() {
            @Override
            public void onProcess(int index, int cacheCount, int maxCount, long time, long duration) {
                for (ProcessListener listener : processListeners) {
                    listener.onProcess(index, cacheCount, maxCount, time, duration);
                }
            }
        });
        return temp;
    }
}