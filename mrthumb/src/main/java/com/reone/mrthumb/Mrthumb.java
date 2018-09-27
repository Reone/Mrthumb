package com.reone.mrthumb;

import android.graphics.Bitmap;

import com.reone.mrthumb.retriever.MediaMetadataRetrieverCompat;
import com.reone.mrthumb.thumbnail.ThumbnailBuffer;

import java.util.Map;

/**
 * Created by wangxingsheng on 2018/9/27.
 * 拇指先生
 */
public class Mrthumb {
    private ThumbnailBuffer thumbnailBuffer;
    private static Mrthumb mInstance = null;

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

    /**
     * 通过百分比获取缩略图
     *
     * @param percentage 百分比
     * @return 缩略图
     */
    public Bitmap getThumbnail(float percentage) {
        if (thumbnailBuffer != null) {
            return thumbnailBuffer.getThumbnail(percentage);
        }
        return null;
    }

    public void release() {
        if (thumbnailBuffer != null) {
            thumbnailBuffer.release();
        }
    }

    public static class Default {
        public static final int COUNT = 100;
        public static final int RETRIEVER_TYPE = RetrieverType.RETRIEVER_FFMPEG;
        public static final int THUMBNAIL_WIDTH = 320;
        public static final int THUMBNAIL_HEIGHT = 180;
    }
}
