package com.reone.mrthumb.cache;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangxingsheng on 2018/5/19.
 * 分散式填充与获取
 * eg:数组0~8位置上，填充顺序为 0,8 -> 4 -> 2,6 -> 1,3,5,7
 */
public abstract class DispersionThumbCache {
    private int maxSize;
    private ArrayList<Integer> bufferIndex = new ArrayList<>();

    protected DispersionThumbCache(int max) {
        maxSize = max;
        ThumbCache.getInstance().setCacheMax(maxSize);
    }

    public abstract Bitmap getIndex(int index);

    public Bitmap get(int index) {
        if (bufferIndex.contains(index)) {
            return ThumbCache.getInstance().get(index);
        } else if (index == 0 || index == maxSize - 1) {
            return null;
        } else {
            ArrayList<Integer> softIndex = new ArrayList<>(bufferIndex);
            if (softIndex.size() == 0) {
                return null;
            }
            softIndex.add(index);
            Collections.sort(softIndex);
            int i = softIndex.indexOf(index);
            if (i == 0) {
                return ThumbCache.getInstance().get(softIndex.get(i + 1));
            } else if (i == softIndex.size() - 1) {
                return ThumbCache.getInstance().get(softIndex.get(i - 1));
            } else if (Math.abs(softIndex.get(i) - softIndex.get(i - 1))
                    < Math.abs(softIndex.get(i + 1) - Math.abs(softIndex.get(i)))) {
                return ThumbCache.getInstance().get(softIndex.get(i - 1));
            } else {
                return ThumbCache.getInstance().get(softIndex.get(i + 1));
            }
        }
    }

    public void start() {
        int base = 1;
        while (base < maxSize) {
            int step = ((maxSize - 1) / base);
            int i = 0;
            while (i < maxSize) {
                if (!ThumbCache.getInstance().hasThumbnail(i)) {
                    ThumbCache.getInstance().set(i, getIndex(i));
                    bufferIndex.add(i);
                }
                i += step;
            }
            base++;
        }
    }

    public void release() {
        ThumbCache.getInstance().release();
        bufferIndex.clear();
    }
}
