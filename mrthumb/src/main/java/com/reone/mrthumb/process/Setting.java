package com.reone.mrthumb.process;

import com.reone.mrthumb.RetrieverType;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wangxingsheng on 2018/10/8.
 */
public class Setting implements Serializable {
    private static final long serialVersionUID = -4210383257744820771L;
    private int maxSize;
    private @RetrieverType
    int mmrType;
    private long videoDuration;
    private String videoUrl;
    private Map<String, String> videoHeaders;
    private int thumbWidth;
    private int thumbHeight;
    private boolean enable = true;
    private boolean dispersionBuffer = true;

    public boolean isDispersionBuffer() {
        return dispersionBuffer;
    }

    public void setDispersionBuffer(boolean dispersionBuffer) {
        this.dispersionBuffer = dispersionBuffer;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public @RetrieverType
    int getMmrType() {
        return mmrType;
    }

    public void setMmrType(@RetrieverType int mmrType) {
        this.mmrType = mmrType;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Map<String, String> getVideoHeaders() {
        return videoHeaders;
    }

    public void setVideoHeaders(Map<String, String> videoHeaders) {
        this.videoHeaders = videoHeaders;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }
}
