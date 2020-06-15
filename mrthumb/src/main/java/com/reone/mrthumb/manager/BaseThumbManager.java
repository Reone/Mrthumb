package com.reone.mrthumb.manager;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.reone.mrthumb.Mrthumb;
import com.reone.mrthumb.cache.ThumbCache;
import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.process.CacheProcess;
import com.reone.mrthumb.process.DispersionProcess;
import com.reone.mrthumb.process.OrderCacheProcess;
import com.reone.mrthumb.type.RetrieverType;
import com.reone.tbufferlib.BuildConfig;

import java.util.Map;

/**
 * Created by wangxingsheng on 2018/5/19.
 * 执行Process
 */
public abstract class BaseThumbManager {
    protected int maxSize;
    private CacheProcess process;
    private Thread initThread = new Thread("BaseMainThread") {
        @Override
        public void run() {
            long startBufferTime = SystemClock.elapsedRealtime();
            onThreadStart();
            try {
                if (Mrthumb.obtain().isEnable()) {
                    process = getCacheProcess();
                    process.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            log("ThumbnailBuffer end buffer at " + (SystemClock.elapsedRealtime() - startBufferTime) + "/n");
        }
    };

    public BaseThumbManager(int maxSize) {
        this.maxSize = maxSize;
        ThumbCache.getInstance().setCacheMax(maxSize);
    }

    protected void execute() {
        if (!initThread.isInterrupted()) {
            initThread.interrupt();
        }
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

    protected void onThreadStart() {

    }

    /**
     * 获取缓存过程，如果返回为空，使用默认
     */
    public CacheProcess getCacheProcess() {
        if (Mrthumb.obtain().isDispersionBuffer()) {
            return new DispersionProcess(getThumbProvider());
        } else {
            return new OrderCacheProcess(getThumbProvider());
        }
    }

    protected abstract ThumbProvider getThumbProvider();

    private void log(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(BaseThumbManager.class.getSimpleName(), log);
        }
    }

    public void release() {
        if (initThread != null) {
            initThread.interrupt();
        }
        if (process != null) {
            process.release();
        }
    }

    private void logBitmapSize(Bitmap bitmap) {
        if (bitmap == null) return;
        log("ThumbnailBuffer bitmap size " + bitmap.getByteCount());
    }

    /**
     * 开始缓存回调，需要调用{@linkplain BaseThumbManager#execute()}让缓存线程启动
     *
     * @param url             视频链接
     * @param headers         指定头
     * @param videoDuration   视频时长
     * @param retrieverType   解码器类型
     * @param thumbnailWidth  生成缩略图宽度
     * @param thumbnailHeight 生成缩略图高度
     */
    public void onBufferStart(String url, Map<String, String> headers, long videoDuration, @RetrieverType int retrieverType, int count, int thumbnailWidth, int thumbnailHeight){
        execute();
    }
}
