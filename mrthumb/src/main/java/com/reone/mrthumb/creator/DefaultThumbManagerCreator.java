package com.reone.mrthumb.creator;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.manager.DefaultThumbManager;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:
 */
public class DefaultThumbManagerCreator implements ThumbManagerCreator<DefaultThumbManager> {

    @Override
    public void onBuffer(DefaultThumbManager thumbManager, String url, Map<String, String> headers, long videoDuration, int retrieverType, int thumbnailWidth, int thumbnailHeight) {
        if (thumbManager != null) {
            thumbManager.setMediaMedataRetriever(retrieverType, videoDuration);
            try {
                thumbManager.execute(url, headers, thumbnailWidth, thumbnailHeight);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DefaultThumbManager createThumbManager(int count, final ArrayList<ProcessListener> processListeners) {
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