package com.reone.mrthumb.core;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.reone.mrthumb.Mrthumb;
import com.reone.mrthumb.cache.ThumbCache;
import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.process.CacheProcess;
import com.reone.mrthumb.process.DispersionProcess;
import com.reone.mrthumb.process.OrderCacheProcess;
import com.reone.mrthumb.retriever.MediaMetadataRetrieverCompat;
import com.reone.mrthumb.type.RetrieverType;
import com.reone.tbufferlib.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxingsheng on 2018/5/19.
 */
public class ThumbMainThread {
    private MediaMetadataRetrieverCompat mmr;
    private int maxSize;
    private int cacheCount;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private long duration;
    private String mUrl;
    private Map<String, String> mHeaders;
    private ProcessListener processListener;
    private CacheProcess process;

    public ThumbMainThread(int maxSize) {
        this.maxSize = maxSize;
        ThumbCache.getInstance().setCacheMax(maxSize);
    }

    public void setMediaMedataRetriever(@RetrieverType int retrieverType, long duration) {
        this.mmr = new MediaMetadataRetrieverCompat(retrieverType);
        this.duration = duration;
        log("ThumbnailBuffer mmr = " + mmr + " duration = " + duration);
    }

    public void execute(final String url, final Map<String, String> headers, int thumbnailWidth, int thumbnailHeight) throws IllegalAccessException {
        log("ThumbnailBuffer url = " + url);
        log("ThumbnailBuffer headers = " + headers);
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
        if (url == null || mmr == null) {
            throw new IllegalAccessException("url or mmr is null");
        }
        if (url.equals(mUrl)) return;
        release();
        mUrl = url;
        mHeaders = headers;
        if (!initThread.isInterrupted()) {
            initThread.interrupt();
        }
        cacheCount = 0;
        initThread.start();
    }

    /**
     * 通过百分比获取缩略图
     *
     * @param percentage 选择时间点占总时长的百分比
     * @return 缩略图
     */
    public Bitmap getThumbnail(float percentage) {
        int index = (int) ((maxSize - 1) * percentage);
        if (process == null) return null;
        Bitmap bitmap = process.get(index);
        logBitmapSize(bitmap);
        return bitmap;
    }

    private Thread initThread = new Thread("ThumbMainThread") {
        @Override
        public void run() {
            log("ThumbnailBuffer start buffer " + mUrl + " headers " + mHeaders);
            long startBufferTime = SystemClock.elapsedRealtime();
            try {
                if (mHeaders == null) {
                    mmr.setDataSource(mUrl, new HashMap<String, String>());
                } else {
                    mmr.setDataSource(mUrl, mHeaders);
                }
                mmr.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_DURATION);
                if (Mrthumb.obtain().isEnable()) {
                    if (Mrthumb.obtain().isDispersionBuffer()) {
                        process = new DispersionProcess(thumbProvider);
                    } else {
                        process = new OrderCacheProcess(thumbProvider);
                    }
                    process.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            log("ThumbnailBuffer end buffer at " + (SystemClock.elapsedRealtime() - startBufferTime) + "/n" + mUrl);
        }
    };

    private ThumbProvider thumbProvider = new ThumbProvider() {
        @Override
        public Bitmap getIndex(int index) {
            Bitmap bitmap = null;
            try {
                long time = index * duration / maxSize;
                log("ThumbnailBuffer dispersions record buffer i = " + index + " at time:" + time);
                bitmap = mmr.getScaledFrameAtTime(time * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST,
                        thumbnailWidth, thumbnailHeight);
                if (processListener != null) {
                    processListener.onProcess(index, ++cacheCount, maxSize, time, duration);
                }
            } catch (Exception ignore) {
            }
            return bitmap;
        }

        @Override
        public int maxSize() {
            return maxSize;
        }
    };

    private void log(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(ThumbMainThread.class.getSimpleName(), log);
        }
    }

    public void release() {
        if (initThread != null) {
            initThread.interrupt();
        }
        if (process != null) {
            process.release();
        }
        mUrl = null;
        mHeaders = null;
    }

    public void setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    private void logBitmapSize(Bitmap bitmap) {
        if (bitmap == null) return;
        log("ThumbnailBuffer bitmap size " + bitmap.getByteCount());
    }
}
