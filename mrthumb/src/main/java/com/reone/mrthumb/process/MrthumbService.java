package com.reone.mrthumb.process;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.retriever.MediaMetadataRetrieverCompat;
import com.reone.mrthumb.tools.DispersionBufferList;
import com.reone.tbufferlib.BuildConfig;

import java.util.HashMap;

/**
 * Created by wangxingsheng on 2018/9/28.
 */
public class MrthumbService extends IntentService {

    private Setting setting;
    private MediaMetadataRetrieverCompat mmr;
    private Bitmap[] thumbnails;
    private Bitmap lastThumbnail;
    private DispersionBufferList thumbnailDispersions;
    private ProcessListener processListener;
    private int cacheCount;

    public MrthumbService() {
        this("MrthumbService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MrthumbService(String name) {
        super(name);
        ServicePool.getInstance().setMrthumbService(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        setting = (Setting) intent.getSerializableExtra("setting");
        if (setting == null) return;
        mmr = new MediaMetadataRetrieverCompat(setting.getMmrType());
        cacheCount = 0;
        thumbnails = new Bitmap[setting.getMaxSize()];
        initThumbnailDispersions(setting.getMaxSize());
        release();
        startBuffer();
    }

    private void startBuffer() {
        log("ThumbnailBuffer start buffer " + setting.getVideoUrl() + " headers " + setting.getVideoHeaders());
        long startBufferTime = SystemClock.elapsedRealtime();
        try {
            if (setting.getVideoHeaders() == null) {
                mmr.setDataSource(setting.getVideoUrl(), new HashMap<String, String>());
            } else {
                mmr.setDataSource(setting.getVideoUrl(), setting.getVideoHeaders());
            }
            mmr.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_DURATION);
            if (setting.isEnable()) {
                if (setting.isDispersionBuffer()) {
                    dispersionBuffer();
                } else {
                    orderBuffer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log("ThumbnailBuffer end buffer at " + (SystemClock.elapsedRealtime() - startBufferTime) + "/n" + setting.getVideoUrl());
    }


    /**
     * 通过百分比获取缩略图
     *
     * @param percentage
     * @return
     */
    public Bitmap getThumbnail(float percentage) {
        Bitmap bitmap = null;
        if (setting.isDispersionBuffer()) {
            bitmap = getDispersionThumbnail(percentage);
        } else {
            bitmap = getOrderBitmap(percentage);
        }
        logBitmapSize(bitmap);
        return bitmap;
    }

    /**
     * 顺序填充缩略图
     */
    private void orderBuffer() {
        for (int i = 0; i < setting.getMaxSize(); i++) {
            if (thumbnails == null || thumbnails[i] != null) return;
            try {
                long time = i * setting.getVideoDuration() / setting.getMaxSize();
                lastThumbnail = mmr.getScaledFrameAtTime(time * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST,
                        setting.getThumbWidth(), setting.getThumbHeight());
                thumbnails[i] = lastThumbnail;
                log("ThumbnailBuffer order buffer i = " + i);
                if (processListener != null) {
                    processListener.onProcess(i, ++cacheCount, setting.getMaxSize(), time, setting.getVideoDuration());
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 顺序方式获取
     *
     * @param percentage
     * @return
     */
    private Bitmap getOrderBitmap(float percentage) {
        if (thumbnails == null) return null;
        int index = (int) ((setting.getMaxSize() - 1) * percentage);
        Bitmap bitmap = thumbnails[index];
        log("ThumbnailBuffer percentage = " + percentage + " index = " + index);
        if (bitmap == null) {
            bitmap = lastThumbnail;
        }
        return bitmap;
    }

    /**
     * 分散填充缩略图
     */
    private void dispersionBuffer() {
        if (thumbnailDispersions != null) {
            thumbnailDispersions.start();
        }
    }

    /**
     * 分散获取缩略图
     */
    private Bitmap getDispersionThumbnail(float percentage) {
        if (thumbnailDispersions == null) return null;
        int index = (int) ((setting.getMaxSize() - 1) * percentage);
        return (Bitmap) thumbnailDispersions.get(index);
    }

    /**
     * 初始化分散式缩略图数组
     */
    private void initThumbnailDispersions(final int maxSize) {
        thumbnailDispersions = new DispersionBufferList<Bitmap>(maxSize) {
            @Override
            public Bitmap getIndex(int index) {
                Bitmap bitmap = null;
                try {
                    long time = index * setting.getVideoDuration() / maxSize;
                    log("ThumbnailBuffer dispersions record buffer i = " + index + " at time:" + time);
                    bitmap = mmr.getScaledFrameAtTime(time * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST,
                            setting.getThumbWidth(), setting.getThumbHeight());
                    if (processListener != null) {
                        processListener.onProcess(index, ++cacheCount, maxSize, time, setting.getVideoDuration());
                    }
                } catch (Exception ignore) {
                }
                return bitmap;
            }
        };
    }

    public void release() {
        if (thumbnails != null) {
            try {
                for (Bitmap bitmap : thumbnails) {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            thumbnails = null;
        }
        if (lastThumbnail != null) {
            try {
                lastThumbnail.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastThumbnail = null;
        }
        if (thumbnailDispersions != null) {
            thumbnailDispersions.release();
            thumbnailDispersions = null;
        }
    }

    public void setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    private void log(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(MrthumbService.class.getSimpleName(), log);
        }
    }

    private void logBitmapSize(Bitmap bitmap) {
        if (bitmap == null) return;
        log(" bitmap size " + bitmap.getByteCount());
    }

}
