package com.reone.simple.customdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.reone.mrthumb.listener.ThumbProvider;
import com.reone.mrthumb.manager.BaseThumbManager;
import com.reone.mrthumb.process.CacheProcess;
import com.reone.simple.R;

import java.util.Map;

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
    protected void onThreadStart() {
        //如果需要预先执行操作，可以在此处执行操作，此操作在线程中
        //方法运行完之后，会执行CacheProcess的start方法
    }

    @Override
    public void onBufferStart(String url, Map<String, String> headers, long videoDuration, int retrieverType, int count, int thumbnailWidth, int thumbnailHeight) {
        //如果需要预先执行操作，可以在此处执行操作
        //此方法在线程线程启动前执行，需要调用super.onBufferStart,以启动缓存线程
        super.onBufferStart(url, headers, videoDuration, retrieverType, count, thumbnailWidth, thumbnailHeight);
    }

    @Override
    protected ThumbProvider getThumbProvider() {
        return new ThumbProvider() {
            @Override
            public Bitmap getIndex(int i) {
                //此处用来给CacheProcess提供缩略图数据
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
