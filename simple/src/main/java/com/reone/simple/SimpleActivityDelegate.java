package com.reone.simple;

import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.reone.talklibrary.TalkApp;
import com.reone.simple.player.NiceUtil;
import com.reone.simple.player.NiceVideoPlayer;
import com.reone.simple.player.NiceVideoPlayerController;
import com.reone.simple.player.PlayerState;
import com.reone.simple.view.VideoSeekBar;

import java.util.HashMap;

import static com.reone.simple.SimpleActivity.testVideo;

/**
 * Created by wangxingsheng on 2018/9/27.
 */
public class SimpleActivityDelegate {
    private SimpleActivity simpleActivity;
    NiceVideoPlayer videoPlayer;

    SimpleActivityDelegate(SimpleActivity simpleActivity) {
        this.simpleActivity = simpleActivity;
    }

    protected void initLogArea() {
        simpleActivity.tvPlayerLogArea.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    protected void initVideoPlayer() {
        videoPlayer = new NiceVideoPlayer(simpleActivity);
        videoPlayer.setDefaultMute(false);
        videoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
        videoPlayer.setController(new NiceVideoPlayerController(simpleActivity) {
            @Override
            protected void onPlayStateChanged(int playState) {
                if (callBack != null) {
                    callBack.onPlayStateChanged(playState, videoPlayer.getDuration());
                }
                if (playState == NiceVideoPlayer.STATE_ERROR) {
                    simpleActivity.tvErr.setVisibility(VISIBLE);
                } else {
                    simpleActivity.tvErr.setVisibility(GONE);
                }
                switch (playState) {
                    case NiceVideoPlayer.STATE_IDLE:
                        simpleActivity.videoLoading.setVisibility(GONE);
                        changeNormalBtn(true);
                        startUpdateProgressTimer();
                        break;
                    case NiceVideoPlayer.STATE_PREPARED:
                        simpleActivity.videoLoading.setVisibility(GONE);
                        startUpdateProgressTimer();
                        break;
                    case NiceVideoPlayer.STATE_PLAYING:
                        simpleActivity.videoLoading.setVisibility(GONE);
                        changeNormalBtn(false);
                        break;
                    case NiceVideoPlayer.STATE_PAUSED:
                        simpleActivity.videoLoading.setVisibility(GONE);
                        changeNormalBtn(true);
                        break;
                    case NiceVideoPlayer.STATE_PREPARING:
                    case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                    case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                        simpleActivity.videoLoading.setVisibility(VISIBLE);
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
                new Handler(simpleActivity.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        long position = videoPlayer.getCurrentPosition();
                        long duration = videoPlayer.getDuration();
                        int bufferPercentage = videoPlayer.getBufferPercentage();
                        int progress = (int) (100f * position / duration);
                        simpleActivity.videoSeekBar.seekbar.setProgress(progress);
                        simpleActivity.videoSeekBar.seekbar.setSecondaryProgress(bufferPercentage);
                        simpleActivity.videoSeekBar.seekTime.setText(NiceUtil.formatTime(position));
                        simpleActivity.videoSeekBar.sumTime.setText(NiceUtil.formatTime(duration));
                    }
                });
            }

            @Override
            public void reset() {
                simpleActivity.videoSeekBar.seekbar.setProgress(0);
                simpleActivity.videoSeekBar.seekbar.setSecondaryProgress(0);
                simpleActivity.videoSeekBar.seekTime.setText(NiceUtil.formatTime(0));
                simpleActivity.videoSeekBar.sumTime.setText(NiceUtil.formatTime(0));
                cancelUpdateProgressTimer();
            }
        });
        videoPlayer.continueFromLastPosition(false);
        simpleActivity.frameVideo.removeAllViews();
        simpleActivity.frameVideo.addView(videoPlayer, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoPlayer.setUp(testVideo, new HashMap<String, String>());
        simpleActivity.videoSeekBar.setSeekBarListener(new VideoSeekBar.SeekBarListener() {
            @Override
            public void onSeek(SeekBar seekBar) {
                if (videoPlayer != null) {
                    long position = (long) (videoPlayer.getDuration() * seekBar.getProgress() / 100f);
                    videoPlayer.seekTo(position);
                    if (videoPlayer.isBufferingPaused() || videoPlayer.isPaused()) {
                        videoPlayer.restart();
                    }
                }
                if (simpleActivity.framePreview != null) {
                    simpleActivity.framePreview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSeeking(SeekBar seekBar, int progress) {
                if (videoPlayer != null) {
                    long position = videoPlayer.getDuration() * seekBar.getProgress() / seekBar.getMax();
                    simpleActivity.tvPreview.setText(NiceUtil.formatTime(position));
                    if (callBack != null) {
                        callBack.onSeeking(seekBar);
                    }
                }
            }

            @Override
            public void onSeekStart() {
                if (simpleActivity.framePreview != null) {
                    simpleActivity.framePreview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void onResume() {
        if (videoPlayer != null && (videoPlayer.isBufferingPaused() || videoPlayer.isPaused())) {
            videoPlayer.restart();
        }
    }

    public void onPause() {
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    private void playStateLog(@PlayerState int playState) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(simpleActivity.tvPlayerLogArea.getText())) {
            sb.append(simpleActivity.tvPlayerLogArea.getText().toString());
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
        simpleActivity.tvPlayerLogArea.setText(sb.toString());
        int offset = simpleActivity.tvPlayerLogArea.getLineCount() * simpleActivity.tvPlayerLogArea.getLineHeight();
        if (offset > simpleActivity.tvPlayerLogArea.getHeight()) {
            simpleActivity.tvPlayerLogArea.scrollTo(0, offset - simpleActivity.tvPlayerLogArea.getHeight());
        }
    }

    /**
     * 改变中心播放按钮状态
     *
     * @param showPlayBtn
     */
    private void changeNormalBtn(boolean showPlayBtn) {
        simpleActivity.btnPlay.setVisibility(showPlayBtn ? View.VISIBLE : View.GONE);
        simpleActivity.btnPause.setVisibility(showPlayBtn ? View.GONE : View.VISIBLE);
    }

    public void onDestroy() {
        if (videoPlayer != null) {
            videoPlayer.releaseInBackground();
        }
    }

    public void onBtnPlayClick() {
        if (videoPlayer.isIdle()) {
            videoPlayer.start();
        } else {
            videoPlayer.restart();
        }
        TalkApp.talk("播放");
    }

    public void onBtnPauseClick() {
        videoPlayer.pause();
        TalkApp.talk("暂停");
    }

    private CallBack callBack = null;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onSeeking(SeekBar seekBar);

        void onPlayStateChanged(int playState, long duration);
    }
}
