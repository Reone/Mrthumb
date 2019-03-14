package com.reone.mrthumb.listener;

import android.graphics.Bitmap;

/**
 * Created by wangxingsheng on 2018/10/23.
 * desc:提供缩略图，在缩略图缓存数组填充的时候，Process需要通过这个类来获取对应的缓存数据
 */
public interface ThumbProvider {

    /**
     * 获取缩略图
     *
     * @param i 需要获取的下标
     * @return 提供缩略图的bitmap
     */
    Bitmap getIndex(int i);

    /**
     * @return 一共需要缓存多少张缩略图，一般根据进度条选择100
     */
    int maxSize();
}
