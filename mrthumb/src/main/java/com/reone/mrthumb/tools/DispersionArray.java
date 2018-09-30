package com.reone.mrthumb.tools;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangxingsheng on 2018/5/19.
 * 分散式填充与获取
 * eg:数组0~8位置上，填充顺序为 0,8 -> 4 -> 2,6 -> 1,3,5,7
 */
public abstract class DispersionArray<T> {
    private int maxSize;
    private T[] array;
    private ArrayList<Integer> bufferIndex = new ArrayList<>();

    public DispersionArray(int max) {
        maxSize = max;
        array = (T[]) new Object[max];
    }

    public abstract T getIndex(int index);

    public T get(int index) {
        if (bufferIndex.contains(index)) {
            return array[index];
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
                return array[softIndex.get(i + 1)];
            } else if (i == softIndex.size() - 1) {
                return array[softIndex.get(i - 1)];
            } else if (Math.abs(softIndex.get(i) - softIndex.get(i - 1))
                    < Math.abs(softIndex.get(i + 1) - Math.abs(softIndex.get(i)))) {
                return array[softIndex.get(i - 1)];
            } else {
                return array[softIndex.get(i + 1)];
            }
        }
    }

    public void start() {
        int base = 1;
        while (base < maxSize) {
            int step = ((maxSize - 1) / base);
            int i = 0;
            while (i < maxSize) {
                if (array != null && array[i] == null) {
                    array[i] = getIndex(i);
                    bufferIndex.add(i);
                }
                i += step;
            }
            base++;
        }
    }

    public void release() {
        if (array != null) {
            for (Object obj : array) {
                if (obj instanceof Bitmap) {
                    ((Bitmap) obj).recycle();
                }
            }
            array = null;
            bufferIndex.clear();
        }
    }
}
