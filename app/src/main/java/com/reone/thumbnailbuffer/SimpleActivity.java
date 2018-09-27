package com.reone.thumbnailbuffer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reone.mmrc.MediaMetadataRetrieverCompat;
import com.reone.mmrc.thumbnail.ThumbnailBuffer;
import com.reone.thumbnailbuffer.player.NiceVideoPlayer;
import com.reone.thumbnailbuffer.view.VideoSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    protected static final String testVideo = "http://domhttp.kksmg.com/2018/05/23/ocj_800k_037c50e5c82010c7c57c9f1935462f9c.mp4";
    private ThumbnailBuffer thumbnailBuffer;
    private SimpleActivityDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);
        delegate = new SimpleActivityDelegate(this);
        delegate.initVideoPlayer();
        delegate.initLogArea();
        delegate.setCallBack(new SimpleActivityDelegate.CallBack() {
            @Override
            public void onSeeking(SeekBar seekBar) {
                Bitmap bitmap = thumbnailBuffer.getThumbnail((float) seekBar.getProgress() / seekBar.getMax());
                if (bitmap != null && !bitmap.isRecycled()) {
                    imgPreview.setImageBitmap(bitmap);
                    imgPreview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayStateChanged(int playState, long duration) {
                if (playState == NiceVideoPlayer.STATE_PREPARED) {
                    try {
                        if (thumbnailBuffer == null) {
                            thumbnailBuffer = new ThumbnailBuffer(100);
                        }
                        MediaMetadataRetrieverCompat mmr = new MediaMetadataRetrieverCompat(MediaMetadataRetrieverCompat.RETRIEVER_FFMPEG);
                        thumbnailBuffer.setMediaMedataRetriever(mmr, duration);
                        thumbnailBuffer.execute(testVideo, null, 320, 180);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @OnClick({R.id.btn_play, R.id.btn_pause, R.id.btn_zoom_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                delegate.onBtnPlayClick();
                break;
            case R.id.btn_pause:
                delegate.onBtnPauseClick();
                break;
            case R.id.btn_zoom_out:
                break;
        }
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
        if (thumbnailBuffer != null) {
            thumbnailBuffer.release();
        }
    }
}
