package com.reone.mrthumb.manager;

import android.graphics.Bitmap;
import android.util.Log;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.retriever.MediaMetadataRetrieverCompat;
import com.reone.mrthumb.type.RetrieverType;
import com.reone.tbufferlib.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxingsheng on 2019-08-30.
 * desc:执行Process
 */
public class DefaultThumbManager extends BaseThumbManager {

    private MediaMetadataRetrieverCompat mmr;
    private int cacheCount;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private long duration;
    private String mUrl;
    private Map<String, String> mHeaders;
    private ProcessListener processListener;

    public DefaultThumbManager(int maxSize) {
        super(maxSize);
    }

    @Override
    public void onBufferStart(String url, Map<String, String> headers, long videoDuration, @RetrieverType int retrieverType, int count, int thumbnailWidth, int thumbnailHeight) {
        this.mmr = new MediaMetadataRetrieverCompat(retrieverType);
        this.duration = videoDuration;
        log("ThumbnailBuffer mmr = " + mmr + " duration = " + duration);
        log("ThumbnailBuffer url = " + url);
        log("ThumbnailBuffer headers = " + headers);
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
        if (url == null || mmr == null) {
            throw new RuntimeException("url or mmr is null");
        }
        if (url.equals(mUrl)) return;
        release();
        mUrl = url;
        mHeaders = headers;
        cacheCount = 0;
        super.execute();
    }

    @Override
    protected void onThreadStart() {
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
            Log.d(BaseThumbManager.class.getSimpleName(), log);
        }
    }

}
