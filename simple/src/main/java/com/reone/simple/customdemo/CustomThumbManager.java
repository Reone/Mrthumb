package com.reone.simple.customdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.manager.BaseThumbManager;
import com.reone.mrthumb.process.CacheProcess;
import com.reone.simple.R;

/**
 * Created by wangxingsheng on 2020/6/15.
 * desc:自定义process执行过程
 */
public class CustomThumbManager extends BaseThumbManager {

    private Context context;

    public CustomThumbManager(int maxSize, Context context) {
        super(maxSize);
        this.context = context;
    }

    @Override
    protected ThumbProvider getThumbProvider() {
        return new ThumbProvider() {
            @Override
            public Bitmap getIndex(int i) {
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.demo);
            }

            @Override
            public int maxSize() {
                return 0;
            }
        };
    }

    @Override
    public CacheProcess getCacheProcess() {
        return new CustomProcess(getThumbProvider());
    }
}
