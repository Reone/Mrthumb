package com.reone.mrthumb.tools;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ProcessListener;
import com.reone.mrthumb.process.ThumbnailBuffer;
import com.reone.mrthumb.retriever.MediaMetadataRetrieverCompat;

import java.util.Map;

/**
 * Created by wangxingsheng on 2018/9/30.
 */
public class MrthumbPool {
    private ThumbnailBuffer thumbnailBuffer;

    public MrthumbPool(int count) {
        thumbnailBuffer = new ThumbnailBuffer(count);
    }

    public void setMediaMedataRetriever(MediaMetadataRetrieverCompat mmr, long videoDuration) {
        thumbnailBuffer.setMediaMedataRetriever(mmr, videoDuration);
    }

    public void execute(String url, Map<String, String> headers, int thumbnailWidth, int thumbnailHeight) {
        try {
            thumbnailBuffer.execute(url, headers, thumbnailWidth, thumbnailHeight);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getThumbnail(float percentage) {
        return thumbnailBuffer.getThumbnail(percentage);
    }

    public void release() {
        thumbnailBuffer.release();
    }

    public void setProcessListener(ProcessListener processListener) {
        thumbnailBuffer.setProcessListener(processListener);
    }
}
