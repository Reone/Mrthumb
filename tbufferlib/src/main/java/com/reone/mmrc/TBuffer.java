package com.reone.mmrc;

import com.reone.mmrc.retriever.MediaMetadataRetrieverCompat;
import com.reone.mmrc.thumbnail.ThumbnailBuffer;

import java.util.Map;

/**
 * Created by wangxingsheng on 2018/9/27.
 */
public class TBuffer {
    private ThumbnailBuffer thumbnailBuffer;

    public void buffer(String url, Map<String, String> headers, long videoDuration, @RetrieverType int retrieverType, int count, int thumbnailWidth, int thumbnailHeight) {
        try {
            if (thumbnailBuffer == null) {
                thumbnailBuffer = new ThumbnailBuffer(count);
            }
            MediaMetadataRetrieverCompat mmr = new MediaMetadataRetrieverCompat(retrieverType);
            thumbnailBuffer.setMediaMedataRetriever(mmr, videoDuration);
            thumbnailBuffer.execute(url, headers, thumbnailWidth, thumbnailHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Default {
        int count = 100;
        int retrieverType = RetrieverType.RETRIEVER_FFMPEG;
        int thumbnailWidth = 320;
        int thumbnailHeight = 180;
    }
}
