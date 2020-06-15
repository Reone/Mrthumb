package com.reone.mrthumb;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.manager.DefaultThumbManager;
import com.reone.mrthumb.type.RetrieverType;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wangxingsheng on 2018/9/27.
 * 拇指先生
 * 原理: 预先缓存缩略图和下标，需要时读取
 */
public class Mrthumb {
    private static Mrthumb mInstance = null;
    private ArrayList<ProcessListener> listenerList = new ArrayList<>();
    private DefaultThumbManager thumbThread;
    private boolean dispersionBuffer = true;
    private boolean enable = true;

    public static Mrthumb obtain() {
        if (mInstance == null) {
            synchronized (Mrthumb.class) {
                if (mInstance == null) {
                    mInstance = new Mrthumb();
                }
            }
        }
        return mInstance;
    }

    public void buffer(String url, long videoDuration) {
        this.buffer(url, null, videoDuration, Default.RETRIEVER_TYPE, Default.COUNT, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    public void buffer(String url, long videoDuration, int count) {
        this.buffer(url, null, videoDuration, Default.RETRIEVER_TYPE, count, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    public void buffer(String url, Map<String, String> headers, long videoDuration) {
        this.buffer(url, headers, videoDuration, Default.RETRIEVER_TYPE, Default.COUNT, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    public void buffer(String url, Map<String, String> headers, long videoDuration, int count) {
        this.buffer(url, headers, videoDuration, Default.RETRIEVER_TYPE, count, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    /**
     * @param url             视频链接
     * @param headers         指定头
     * @param videoDuration   视频时长
     * @param retrieverType   解码器类型
     * @param count           单视频生成缩略图数量
     * @param thumbnailWidth  生成缩略图宽度
     * @param thumbnailHeight 生成缩略图高度
     */
    public void buffer(String url, Map<String, String> headers, long videoDuration, @RetrieverType int retrieverType, int count, int thumbnailWidth, int thumbnailHeight) {
        try {
            initMrthumbPool(count);
            thumbThread.setMediaMedataRetriever(retrieverType, videoDuration);
            thumbThread.execute(url, headers, thumbnailWidth, thumbnailHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMrthumbPool(int count) {
        if (thumbThread == null) {
            thumbThread = new DefaultThumbManager(count);
            thumbThread.setProcessListener(new ProcessListener() {
                @Override
                public void onProcess(int index, int cacheCount, int maxCount, long time, long duration) {
                    for (ProcessListener listener : listenerList) {
                        listener.onProcess(index, cacheCount, maxCount, time, duration);
                    }
                }
            });
        }
    }

    /**
     * 通过百分比获取缩略图
     *
     * @param percentage 百分比
     * @return 缩略图
     */
    public Bitmap getThumbnail(float percentage) {
        if (thumbThread != null) {
            return thumbThread.getThumbnail(percentage);
        }
        return null;
    }

    public void release() {
        if (thumbThread != null) {
            thumbThread.release();
        }
        listenerList.clear();
    }

    public void addProcessListener(ProcessListener processListener) {
        listenerList.add(processListener);
    }

    public boolean isDispersionBuffer() {
        return dispersionBuffer;
    }

    public Mrthumb dispersion(boolean dispersionBuffer) {
        this.dispersionBuffer = dispersionBuffer;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static class Default {
        public static final int COUNT = 100;
        public static final int RETRIEVER_TYPE = RetrieverType.RETRIEVER_ANDROID;
        public static final int THUMBNAIL_WIDTH = 320;
        public static final int THUMBNAIL_HEIGHT = 180;
    }
}
