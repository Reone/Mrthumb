package com.reone.mrthumb.core;

import android.graphics.Bitmap;
import android.util.Log;

import com.reone.mrthumb.cache.ThumbCache;
import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.retriever.MediaMetadataRetrieverCompat;
import com.reone.mrthumb.type.RetrieverType;
import com.reone.tbufferlib.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxingsheng on 2019-08-30.
 * desc:
 */
public class LocalMainThread extends BaseMainThread {

    private MediaMetadataRetrieverCompat mmr;
    private int cacheCount;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private long duration;
    private String mUrl;
    private Map<String, String> mHeaders;
    private ProcessListener processListener;

    public LocalMainThread(int maxSize) {
        super(maxSize);
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
        cacheCount = 0;
        super.execute();
    }

    @Override
    protected void initThread() {
        if (mHeaders == null) {
            mmr.setDataSource(mUrl, new HashMap<String, String>());
        } else {
            mmr.setDataSource(mUrl, mHeaders);
        }
        mmr.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_DURATION);
    }

    @Override
    protected ThumbProvider getThumbProvider() {
        return new ThumbProvider() {
            @Override
            public Bitmap getIndex(int index) {
                Bitmap bitmap = null;
                try {
                    long time = index * duration / maxSize;
                    log("ThumbnailBuffer dispersions record buffer i = " + index + " at time:" + time);
                    bitmap = mmr.getScaledFrameAtTime(time * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST,
                            thumbnailWidth, thumbnailHeight);
                    if (bitmap == null) {
                        log("ThumbnailBuffer dispersions record buffer i = " + index + " is null");
                    }
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
    }

    @Override
    public void release() {
        super.release();
        mUrl = null;
        mHeaders = null;
    }

    public void setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    private void log(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(BaseMainThread.class.getSimpleName(), log);
        }
    }
}
