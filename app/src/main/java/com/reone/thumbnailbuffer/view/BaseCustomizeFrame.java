package com.reone.thumbnailbuffer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.ButterKnife;

/**
 * Created by wangxingsheng on 2018/6/29.
 *
 * 一般的自定义view可以直接继承此类
 *
 */
public abstract class BaseCustomizeFrame extends FrameLayout implements BaseCustomize {
    public BaseCustomizeFrame(@NonNull Context context) {
        this(context,null);
    }

    public BaseCustomizeFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseCustomizeFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, getLayoutResource(), this);
        ButterKnife.bind(this);
        inflateView();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, getStyleableResource());
            customAttr(a);
            a.recycle();
        }
        initEventAndData();
    }
}
