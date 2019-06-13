package com.reone.mrthumb.process;

import android.graphics.Bitmap;

import com.reone.mrthumb.cache.ThumbCache;
import com.reone.mrthumb.listener.ThumbProvider;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangxingsheng on 2018/10/17.
 * desc:分散式填充程序
 */
public class DispersionProcess extends CacheProcess {
    private ArrayList<Integer> bufferIndex = new ArrayList<>();

    public DispersionProcess(ThumbProvider thumbProvider) {
        super(thumbProvider);
    }

    @Override
    public void start() {
        int base = 1;
        ThumbCache.getInstance().set(0, getThumbProvider().getIndex(0));
        bufferIndex.add(0);
        while (base < maxSize) {
            int step = ((maxSize - 1) / base);
            int i = step;
            while (i < maxSize) {
                if (!ThumbCache.getInstance().hasThumbnail(i)) {
                    ThumbCache.getInstance().set(i, getThumbProvider().getIndex(i));
                    bufferIndex.add(i);
                }
                i += step;
            }
            base++;
        }
    }

    @Override
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

    @Override
    public void release() {
        ThumbCache.getInstance().release();
        bufferIndex.clear();
    }
}
