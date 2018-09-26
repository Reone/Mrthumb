package com.reone.thumbnailbuffer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.reone.talklibrary.TalkApp;
import com.reone.thumbnailbuffer.player.NiceUtil;
import com.reone.thumbnailbuffer.player.NiceVideoPlayer;
import com.reone.thumbnailbuffer.player.NiceVideoPlayerController;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);
        initVideoPlayer();
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
                TalkApp.talk("click play");
                break;
            case R.id.btn_pause:
                videoPlayer.pause();
                TalkApp.talk("click pause");
                break;
            case R.id.btn_zoom_out:
                TalkApp.talk("click out");
                break;
        }
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
                        videoLoading.setVisibility(VISIBLE);
                        changeNormalBtn(true);
                        break;
                    case NiceVideoPlayer.STATE_PREPARED:
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
            }

            @Override
            protected void updateProgress() {
                long position = videoPlayer.getCurrentPosition();
                long duration = videoPlayer.getDuration();
                int bufferPercentage = videoPlayer.getBufferPercentage();
                int progress = (int) (100f * position / duration);
                videoSeekBar.seekbar.setProgress(progress);
                videoSeekBar.seekbar.setSecondaryProgress(bufferPercentage);
                videoSeekBar.seekTime.setText(NiceUtil.formatTime(position));
                videoSeekBar.sumTime.setText(NiceUtil.formatTime(duration));
            }

            @Override
            public void reset() {
                videoSeekBar.seekbar.setProgress(0);
                videoSeekBar.seekbar.setSecondaryProgress(0);
                videoSeekBar.seekTime.setText(NiceUtil.formatTime(0));
                videoSeekBar.sumTime.setText(NiceUtil.formatTime(0));
            }
        });
        videoPlayer.continueFromLastPosition(false);
        frameVideo.removeAllViews();
        frameVideo.addView(videoPlayer, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoPlayer.setUp(testVideo, new HashMap<String, String>());
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
