package com.reone.thumbnailbuffer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reone.mmrc.MediaMetadataRetrieverCompat;
import com.reone.mmrc.thumbnail.ThumbnailBuffer;
import com.reone.talklibrary.TalkApp;
import com.reone.thumbnailbuffer.player.LogUtil;
import com.reone.thumbnailbuffer.player.NiceUtil;
import com.reone.thumbnailbuffer.player.NiceVideoPlayer;
import com.reone.thumbnailbuffer.player.NiceVideoPlayerController;
import com.reone.thumbnailbuffer.player.PlayerState;
import com.reone.thumbnailbuffer.view.VideoSeekBar;

import java.util.HashMap;

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

    private NiceVideoPlayer videoPlayer;
    private static final String testVideo = "http://domhttp.kksmg.com/2018/05/23/ocj_800k_037c50e5c82010c7c57c9f1935462f9c.mp4";
    private ThumbnailBuffer thumbnailBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);
        initVideoPlayer();
        initLogArea();
    }

    @OnClick({R.id.btn_play, R.id.btn_pause, R.id.btn_zoom_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                if (videoPlayer.isIdle()) {
                    videoPlayer.start();
                } else {
                    videoPlayer.restart();
                }
                TalkApp.talk("播放");
                break;
            case R.id.btn_pause:
                videoPlayer.pause();
                TalkApp.talk("暂停");
                break;
            case R.id.btn_zoom_out:
                break;
        }
    }

    /**
     * 显示缩略图
     *
     * @param position
     */
    private void showPreView(long position) {
        tvPreview.setText(NiceUtil.formatTime(position));
        //这里的隐藏是非常必要的。播放第二个视频的时候，初始状态它的缩略图view显示的是上一个视频的bitmap，而这个bitmap已经回收了
        //在ImageView隐藏时不会出现问题，当获取到新的bitmap后再进行显示操作
        try {
            Bitmap bitmap = getFrameAtTime(position);
            if (bitmap != null && !bitmap.isRecycled()) {
                imgPreview.setImageBitmap(bitmap);
                imgPreview.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer != null && (videoPlayer.isBufferingPaused() || videoPlayer.isPaused())) {
            videoPlayer.restart();
        }
    }

    private void initLogArea() {
        tvPlayerLogArea.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initVideoPlayer() {
        videoPlayer = new NiceVideoPlayer(this);
        videoPlayer.setDefaultMute(false);
        videoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
        videoPlayer.setController(new NiceVideoPlayerController(this) {
            @Override
            protected void onPlayStateChanged(int playState) {
                if (playState == NiceVideoPlayer.STATE_ERROR) {
                    tvErr.setVisibility(VISIBLE);
                } else {
                    tvErr.setVisibility(GONE);
                }
                switch (playState) {
                    case NiceVideoPlayer.STATE_IDLE:
                        videoLoading.setVisibility(GONE);
                        changeNormalBtn(true);
                        startUpdateProgressTimer();
                        break;
                    case NiceVideoPlayer.STATE_PREPARED:
                        initMediaMedataRetriever(testVideo);
                        videoLoading.setVisibility(GONE);
                        startUpdateProgressTimer();
                        break;
                    case NiceVideoPlayer.STATE_PLAYING:
                        videoLoading.setVisibility(GONE);
                        changeNormalBtn(false);
                        break;
                    case NiceVideoPlayer.STATE_PAUSED:
                        videoLoading.setVisibility(GONE);
                        changeNormalBtn(true);
                        break;
                    case NiceVideoPlayer.STATE_PREPARING:
                    case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                    case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                        videoLoading.setVisibility(VISIBLE);
                        break;
                    case NiceVideoPlayer.STATE_ERROR:
                    case NiceVideoPlayer.STATE_COMPLETED:
                        cancelUpdateProgressTimer();
                        reset();
                        break;
                }
                playStateLog(playState);
            }

            @Override
            protected void updateProgress() {
                new Handler(SimpleActivity.this.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        long position = videoPlayer.getCurrentPosition();
                        long duration = videoPlayer.getDuration();
                        int bufferPercentage = videoPlayer.getBufferPercentage();
                        int progress = (int) (100f * position / duration);
                        videoSeekBar.seekbar.setProgress(progress);
                        videoSeekBar.seekbar.setSecondaryProgress(bufferPercentage);
                        videoSeekBar.seekTime.setText(NiceUtil.formatTime(position));
                        videoSeekBar.sumTime.setText(NiceUtil.formatTime(duration));
                    }
                });
            }

            @Override
            public void reset() {
                videoSeekBar.seekbar.setProgress(0);
                videoSeekBar.seekbar.setSecondaryProgress(0);
                videoSeekBar.seekTime.setText(NiceUtil.formatTime(0));
                videoSeekBar.sumTime.setText(NiceUtil.formatTime(0));
                cancelUpdateProgressTimer();
            }
        });
        videoPlayer.continueFromLastPosition(false);
        frameVideo.removeAllViews();
        frameVideo.addView(videoPlayer, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoPlayer.setUp(testVideo, new HashMap<String, String>());
        videoSeekBar.setSeekBarListener(new VideoSeekBar.SeekBarListener() {
            @Override
            public void onSeek(SeekBar seekBar) {
                if (videoPlayer != null) {
                    long position = (long) (videoPlayer.getDuration() * seekBar.getProgress() / 100f);
                    videoPlayer.seekTo(position);
                    if (videoPlayer.isBufferingPaused() || videoPlayer.isPaused()) {
                        videoPlayer.restart();
                    }
                }
                if (framePreview != null) {
                    framePreview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSeeking(SeekBar seekBar, int progress) {
                if (videoPlayer != null) {
                    long position = (long) (videoPlayer.getDuration() * seekBar.getProgress() / 100f);
                    //如果不是全屏状态，不需要显示缩略图
                    showPreView(position);
                }
            }

            @Override
            public void onSeekStart() {
                if (framePreview != null) {
                    framePreview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 获取时间点的缩略图
     *
     * @param time 单位毫秒
     * @return 缩略图
     */
    public Bitmap getFrameAtTime(long time) {
        if (videoPlayer == null) return null;
        LogUtil.d("getFrameAtTime at time:" + time + " duration:" + videoPlayer.getDuration());
        if (thumbnailBuffer == null) {
            return null;
        }
        return thumbnailBuffer.getThumbnail((float) time / videoPlayer.getDuration());
    }

    private void initMediaMedataRetriever(String testVideo) {
        try {
            if (TextUtils.isEmpty(testVideo) || videoPlayer == null) return;
            if (thumbnailBuffer == null) {
                thumbnailBuffer = new ThumbnailBuffer(100);
            }
            MediaMetadataRetrieverCompat mmr = new MediaMetadataRetrieverCompat(MediaMetadataRetrieverCompat.RETRIEVER_FFMPEG);
            thumbnailBuffer.setMediaMedataRetriever(mmr, videoPlayer.getDuration());
            thumbnailBuffer.execute(testVideo, null, 320, 180);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("initMediaMedataRetriever ——> e" + e.getMessage());
        }
    }

    private void playStateLog(@PlayerState int playState) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(tvPlayerLogArea.getText())) {
            sb.append(tvPlayerLogArea.getText().toString());
            sb.append("\n");
        }
        switch (playState) {
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                sb.append("STATE_BUFFERING_PAUSED");
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                sb.append("STATE_BUFFERING_PLAYING");
                break;
            case NiceVideoPlayer.STATE_COMPLETED:
                sb.append("STATE_COMPLETED");
                break;
            case NiceVideoPlayer.STATE_ERROR:
                sb.append("STATE_ERROR");
                break;
            case NiceVideoPlayer.STATE_IDLE:
                sb.append("STATE_IDLE");
                break;
            case NiceVideoPlayer.STATE_PAUSED:
                sb.append("STATE_PAUSED");
                break;
            case NiceVideoPlayer.STATE_PLAYING:
                sb.append("STATE_PLAYING");
                break;
            case NiceVideoPlayer.STATE_PREPARED:
                sb.append("STATE_PREPARED");
                break;
            case NiceVideoPlayer.STATE_PREPARING:
                sb.append("STATE_PREPARING");
                break;
        }
        tvPlayerLogArea.setText(sb.toString());
        int offset = tvPlayerLogArea.getLineCount() * tvPlayerLogArea.getLineHeight();
        if (offset > tvPlayerLogArea.getHeight()) {
            tvPlayerLogArea.scrollTo(0, offset - tvPlayerLogArea.getHeight());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null) {
            videoPlayer.releaseInBackground();
        }
        if (thumbnailBuffer != null) {
            thumbnailBuffer.release();
        }
    }

    /**
     * 改变中心播放按钮状态
     *
     * @param showPlayBtn
     */
    private void changeNormalBtn(boolean showPlayBtn) {
        btnPlay.setVisibility(showPlayBtn ? View.VISIBLE : View.GONE);
        btnPause.setVisibility(showPlayBtn ? View.GONE : View.VISIBLE);
    }
}
