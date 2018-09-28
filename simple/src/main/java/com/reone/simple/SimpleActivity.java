package com.reone.simple;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reone.mrthumb.Mrthumb;
import com.reone.mrthumb.MrthumbService;
import com.reone.simple.player.LogUtil;
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
    private SimpleActivityDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);
        delegate = new SimpleActivityDelegate(this);
        delegate.setCallBack(new SimpleActivityDelegate.CallBack() {
            @Override
            public void onSeeking(SeekBar seekBar) {
                Bitmap bitmap = Mrthumb.obtain().getThumbnail((float) seekBar.getProgress() / seekBar.getMax());
                if (bitmap != null && !bitmap.isRecycled()) {
                    imgPreview.setImageBitmap(bitmap);
                    imgPreview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayStateChanged(int playState, long videoDuration) {
                if (playState == NiceVideoPlayer.STATE_PREPARED) {
                    Mrthumb.obtain().buffer(videoUrl, videoDuration, Mrthumb.Default.COUNT);
                    Intent intent = new Intent(SimpleActivity.this, MrthumbService.class);
                    intent.putExtra("url", videoUrl);
                    startService(intent);
                    LogUtil.d("service start");
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
