package com.reone.simple.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;


import com.reone.simple.R;

import java.lang.reflect.Field;

import butterknife.BindView;

/**
 * Created by wangxingsheng on 2018/5/10.
 */
public class VideoSeekBar extends BaseCustomizeFrame implements SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.seek_time)
    public TextView seekTime;

    @BindView(R.id.seekbar)
    public SeekBar seekbar;

    @BindView(R.id.sum_time)
    public TextView sumTime;
    private SeekBarListener listener = null;

    public VideoSeekBar(@NonNull Context context) {
        super(context);
    }

    public VideoSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSeekBarListener(SeekBarListener listener) {
        this.listener = listener;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.layout_video_seekbar;
    }

    @Override
    public int[] getStyleableResource() {
        return R.styleable.VideoSeekBar;
    }

    @Override
    public void inflateView() {

    }

    @Override
    public void customAttr(TypedArray typedArray) {
        float textSize = typedArray.getInteger(R.styleable.VideoSeekBar_vsb_text_size, -1);
        if (textSize != -1) {
            seekTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            sumTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        }
        int progressHeight = (int) typedArray.getDimension(R.styleable.VideoSeekBar_vsb_seek_bar_progress_height,-1);
        if(progressHeight != -1){
            try {
                Class<?> superclass = seekbar.getClass().getSuperclass().getSuperclass();
                Field mMaxHeight = superclass.getDeclaredField("mMaxHeight");
                Field mMinHeight = superclass.getDeclaredField("mMinHeight");
                mMaxHeight.setAccessible(true);
                mMinHeight.setAccessible(true);
                mMaxHeight.set(seekbar,progressHeight);
                mMinHeight.set(seekbar,progressHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Drawable style = typedArray.getDrawable(R.styleable.VideoSeekBar_vsb_seek_bar_style);
        if (style != null) {
            seekbar.setProgressDrawable(style);
        }
    }

    @Override
    public void initEventAndData() {
        seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener != null) {
            listener.onSeeking(seekBar, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null) {
            listener.onSeekStart();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null) {
            listener.onSeek(seekBar);
        }
    }

    public interface SeekBarListener {
        void onSeek(SeekBar seekBar);

        void onSeeking(SeekBar seekBar, int progress);

        void onSeekStart();
    }
}
