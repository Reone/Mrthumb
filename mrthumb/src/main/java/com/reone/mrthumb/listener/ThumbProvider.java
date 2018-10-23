package com.reone.mrthumb.listener;

import android.graphics.Bitmap;

/**
 * Created by wangxingsheng on 2018/10/23.
 * desc:
 */
public interface ThumbProvider {
    Bitmap getIndex(int i);

    int maxSize();
}
