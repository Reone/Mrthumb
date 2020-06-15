package com.reone.simple;

import android.graphics.Bitmap;

import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.manager.BaseThumbManager;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:
 */
public class CustomThumbManager extends BaseThumbManager {
    public CustomThumbManager(int maxSize) {
        super(maxSize);
    }
    
    @Override
    protected ThumbProvider getThumbProvider() {
        return new ThumbProvider() {
            @Override
            public Bitmap getIndex(int i) {
                return null;
            }

            @Override
            public int maxSize() {
                return 0;
            }
        };
    }
}
