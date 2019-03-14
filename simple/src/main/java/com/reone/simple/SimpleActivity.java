package com.reone.simple;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reone.mrthumb.Mrthumb;
import com.reone.mrthumb.listener.ProcessListener;
import com.reone.simple.player.NiceVideoPlayer;
import com.reone.simple.view.VideoSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleActivity extends AppCompatActivity {

    @BindView(R.id.frame_video)
    FrameLayout frameVideo;
    @BindView(R.id.img_preview)
    AppCompatImageView imgPreview;
    @BindView(R.id.tv_preview)
    TextView tvPreview;
    @BindView(R.id.tv_err)
    TextView tvErr;
    @BindView(R.id.tv_thumb_log_area)
    TextView tvThumbLogArea;
    @BindView(R.id.tv_player_log_area)
    TextView tvPlayerLogArea;
    @BindView(R.id.frame_preview)
    FrameLayout framePreview;
    @BindView(R.id.btn_play)
    AppCompatImageView btnPlay;
    @BindView(R.id.btn_pause)
    AppCompatImageView btnPause;
    @BindView(R.id.video_seek_bar)
    VideoSeekBar videoSeekBar;
    @BindView(R.id.btn_zoom_out)
    AppCompatImageView btnZoomOut;
    @BindView(R.id.video_loading)
    FrameLayout videoLoading;

    protected static final String videoUrl = "http://domhttp.kksmg.com/2018/05/23/ocj_800k_037c50e5c82010c7c57c9f1935462f9c.mp4";
    //本demo意在展示Mrthumb的使用，所以将无关的操作放在了delegate中
    private SimpleActivityDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);
        delegate = new SimpleActivityDelegate(this);
        delegate.setCallBack(new SimpleActivityDelegate.CallBack() {

            /**
             * 拖动进度条过程中回调
             */
            @Override
            public void onSeeking(SeekBar seekBar) {
                float percentage = (float) seekBar.getProgress() / seekBar.getMax();
                Bitmap bitmap = Mrthumb.obtain().getThumbnail(percentage);
                if (bitmap != null && !bitmap.isRecycled()) {
                    imgPreview.setImageBitmap(bitmap);
                    imgPreview.setVisibility(View.VISIBLE);
                }
            }

            /**
             * 播放器视频源加载状态回调
             */
            @Override
            public void onPlayStateChanged(int playState, long videoDuration) {
                if (playState == NiceVideoPlayer.STATE_PREPARED) {
                    //视频准备好后开始加载缩略图
                    Mrthumb.obtain().dispersion(true).buffer(videoUrl, videoDuration, Mrthumb.Default.COUNT);
//                    更详细的可以调用如下方法
//                    Mrthumb.obtain().buffer(videoUrl, null, videoDuration, Mrthumb.Default.RETRIEVER_TYPE, Mrthumb.Default.COUNT, Mrthumb.Default.THUMBNAIL_WIDTH, Mrthumb.Default.THUMBNAIL_HEIGHT);
                }
            }
        });
        Mrthumb.obtain().addProcessListener(new ProcessListener() {

            @Override
            public void onProcess(final int index, final int cacheCount, final int maxCount, final long time, final long duration) {
                if (delegate != null) {
                    delegate.thumbProcessLog("cache " + time / 1000 + "s at " + index + " process:" + (cacheCount * 100 / maxCount) + "%");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        delegate.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        delegate.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delegate.onDestroy();
        Mrthumb.obtain().release();
    }
}
