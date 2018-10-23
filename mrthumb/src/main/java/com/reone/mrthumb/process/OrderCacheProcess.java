package com.reone.mrthumb.process;

import android.graphics.Bitmap;

import com.reone.mrthumb.cache.ThumbCache;
import com.reone.mrthumb.listener.ThumbProvider;

/**
 * Created by wangxingsheng on 2018/10/17.
 * desc:顺序填充
 */
public class OrderCacheProcess extends CacheProcess {

    public OrderCacheProcess(ThumbProvider thumbProvider) {
        super(thumbProvider);
    }

    @Override
    void start() {
        for (int i = 0; i < maxSize; i++) {
            if (ThumbCache.getInstance().hasThumbnail(i)) return;
            try {
                ThumbCache.getInstance().set(i, getThumbProvider().getIndex(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    Bitmap get(int index) {
        return ThumbCache.getInstance().get(index);
    }

    @Override
    void release() {
        ThumbCache.getInstance().release();
    }
}
