package com.reone.mrthumb;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.reone.mrthumb.cache.ThumbCache;
import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.process.CacheProcess;
import com.reone.tbufferlib.BuildConfig;

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
                    CacheProcess customProcess = getCustomProcess();
                    if (customProcess != null) {
                        process = getCustomProcess();//自定义Process
                    }
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

    public void execute() {
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

    protected abstract void onThreadStart();

    public abstract CacheProcess getCustomProcess();

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
}
