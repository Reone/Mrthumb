package com.reone.mrthumb;

/**
 * Created by wangxingsheng on 2018/9/27.
 */

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        RetrieverType.RETRIEVER_ANDROID,
        RetrieverType.RETRIEVER_FFMPEG
})
@Retention(RetentionPolicy.SOURCE)
public @interface RetrieverType {
    int RETRIEVER_FFMPEG = 0;
    int RETRIEVER_ANDROID = 1;
}
