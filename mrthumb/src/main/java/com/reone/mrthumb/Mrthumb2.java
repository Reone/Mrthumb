package com.reone.mrthumb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.process.MrthumbService;
import com.reone.mrthumb.process.ServicePool;
import com.reone.mrthumb.process.Setting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wangxingsheng on 2018/9/27.
 * 拇指先生
 */
public class Mrthumb2 {
    private ArrayList<ProcessListener> listenerList = new ArrayList<>();
    private static Mrthumb2 mInstance = null;
    private WeakReference<Context> contextWeakReference;
    private WeakReference<Intent> intentWeakReference;

    public static Mrthumb2 obtain() {
        if (mInstance == null) {
            synchronized (Mrthumb2.class) {
                if (mInstance == null) {
                    mInstance = new Mrthumb2();
                }
            }
        }
        return mInstance;
    }

    public void buffer(Context context, String url, long videoDuration) {
        this.buffer(context, url, null, videoDuration, Default.RETRIEVER_TYPE, Default.COUNT, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    public void buffer(Context context, String url, long videoDuration, int count) {
        this.buffer(context, url, null, videoDuration, Default.RETRIEVER_TYPE, count, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    public void buffer(Context context, String url, Map<String, String> headers, long videoDuration) {
        this.buffer(context, url, headers, videoDuration, Default.RETRIEVER_TYPE, Default.COUNT, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
    }

    public void buffer(Context context, String url, Map<String, String> headers, long videoDuration, int count) {
        this.buffer(context, url, headers, videoDuration, Default.RETRIEVER_TYPE, count, Default.THUMBNAIL_WIDTH, Default.THUMBNAIL_HEIGHT);
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
    public void buffer(Context context, String url, Map<String, String> headers, long videoDuration, @RetrieverType int retrieverType, int count, int thumbnailWidth, int thumbnailHeight) {
        Intent serviceIntent = new Intent(context, MrthumbService.class);
        Setting setting = new Setting();
        setting.setDispersionBuffer(true);
        setting.setEnable(true);
        setting.setVideoUrl(url);
        setting.setVideoHeaders(headers);
        setting.setMmrType(retrieverType);
        setting.setThumbWidth(thumbnailWidth);
        setting.setThumbHeight(thumbnailHeight);
        setting.setMaxSize(count);
        setting.setVideoDuration(videoDuration);
        serviceIntent.putExtra("setting", setting);
        contextWeakReference = new WeakReference<>(context);
        intentWeakReference = new WeakReference<>(serviceIntent);
        context.startService(serviceIntent);
    }

    /**
     * 通过百分比获取缩略图
     *
     * @param percentage 百分比
     * @return 缩略图
     */
    public Bitmap getThumbnail(float percentage) {
        if (ServicePool.getInstance().getMrthumbService() != null) {
            return ServicePool.getInstance().getMrthumbService().getThumbnail(percentage);
        }
        return null;
    }

    public void release() {
        if (ServicePool.getInstance().getMrthumbService() != null) {
            ServicePool.getInstance().getMrthumbService().release();
        }
        if (contextWeakReference != null && contextWeakReference.get() != null
                && intentWeakReference != null && intentWeakReference.get() != null) {
            contextWeakReference.get().stopService(intentWeakReference.get());
        }
        listenerList.clear();
    }

    public void addProcessListener(ProcessListener processListener) {
        listenerList.add(processListener);
        if (ServicePool.getInstance().getMrthumbService() != null) {
            ServicePool.getInstance().getMrthumbService().setProcessListener(new ProcessListener() {
                @Override
                public void onProcess(int index, int cacheCount, int maxCount, long time, long duration) {
                    for (ProcessListener listener : listenerList) {
                        listener.onProcess(index, cacheCount, maxCount, time, duration);
                    }
                }
            });
        }
    }

    public static class Default {
        public static final int COUNT = 100;
        public static final int RETRIEVER_TYPE = RetrieverType.RETRIEVER_FFMPEG;
        public static final int THUMBNAIL_WIDTH = 320;
        public static final int THUMBNAIL_HEIGHT = 180;
    }
}
