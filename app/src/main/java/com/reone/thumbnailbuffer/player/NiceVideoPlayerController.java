package com.reone.thumbnailbuffer.player;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by XiaoJianjun on 2017/6/21.
 * 控制器抽象类
 */
public abstract class NiceVideoPlayerController
        extends FrameLayout implements View.OnTouchListener {

    private Context mContext;
    protected INiceVideoPlayer mNiceVideoPlayer;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition;
    private boolean mTouchOnlyFullScreen = true;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;
    private boolean mNeedClick;
    private static final int THRESHOLD = 80;
    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;

    /**
     * 是否需要位置手势
     */
    private boolean needPositionGesture;
    /**
     * 是否需要声音手势
     */
    private boolean needVolumeGesture;
    /**
     * 是否需要亮度手势
     */
    private boolean needBrightnessGesture;
    /**
     * 是否需要点击手势
     */
    private boolean needClickGesture;
    /**
     * 是否需要手势
     */
    private boolean needGesture;

    public NiceVideoPlayerController(Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
    }

    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        mNiceVideoPlayer = niceVideoPlayer;
    }

    /**
     * 获取需要显示视频的区域
     * 使用FrameLayout承接
     */
    public abstract FrameLayout getTextureViewContainer();
    /**
     * 设置播放的视频的标题
     *
     * @param title 视频标题
     */
    public abstract void setTitle(String title);

    /**
     * 视频底图
     *
     * @param resId 视频底图资源
     */
    public abstract void setImage(int resId);

    /**
     * 视频底图ImageView控件，提供给外部用图片加载工具来加载网络图片
     *
     * @return 底图ImageView
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长.
     */
    public abstract void setLength(long length);

    /**
     * 当播放器的播放状态发生变化，在此方法中国你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  <ul>
     *                  <li>{@link NiceVideoPlayer#STATE_IDLE}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PREPARING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PREPARED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PLAYING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PAUSED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_BUFFERING_PLAYING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_BUFFERING_PAUSED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_ERROR}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_COMPLETED}</li>
     *                  </ul>
     */
    protected abstract void onPlayStateChanged(int playState);

    /**
     * 当播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     *
     * @param playMode 播放器的模式：
     *                 <ul>
     *                 <li>{@link NiceVideoPlayer#MODE_NORMAL}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_FULL_SCREEN}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_TINY_WINDOW}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_NORMAL_CTRL}</li>
     *                 </ul>
     */
    protected abstract void onPlayModeChanged(int playMode);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    protected abstract void reset();

    /**
     * 开启更新进度的计时器。
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    NiceVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    protected abstract void updateProgress();

    public void setNeedPositionGesture(boolean needPositionGesture) {
        this.needPositionGesture = needPositionGesture;
    }

    public void setNeedVolumeGesture(boolean needVolumeGesture) {
        this.needVolumeGesture = needVolumeGesture;
    }

    public void setNeedBrightnessGesture(boolean needBrightnessGesture) {
        this.needBrightnessGesture = needBrightnessGesture;
    }

    public void setNeedClickGesture(boolean needClickGesture) {
        this.needClickGesture = needClickGesture;
    }

    public void setNeedGesture(boolean needGesture){
        this.needGesture = needGesture;
    }

    public void setTouchOnlyFullScreen(boolean touchOnlyFullScreen) {
        this.mTouchOnlyFullScreen = touchOnlyFullScreen;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 只有全屏的时候才能拖动位置、亮度、声音
        if(mTouchOnlyFullScreen){
            if (!mNiceVideoPlayer.isFullScreen()) {
                return false;
            }
        }
//        // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
//        if (mNiceVideoPlayer.isIdle()
//                || mNiceVideoPlayer.isError()
//                || mNiceVideoPlayer.isPreparing()
//                || mNiceVideoPlayer.isPrepared()
//                || mNiceVideoPlayer.isCompleted()) {
//            hideChangePosition();
//            hideChangeBrightness();
//            hideChangeVolume();
//            return false;
//        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                mNeedClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if(absDeltaX >= THRESHOLD || absDeltaY >= THRESHOLD){
                    mNeedClick = false;
                }
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness && needGesture) {
                    // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
                    if (absDeltaX >= THRESHOLD) {
                        if(needPositionGesture){
                            cancelUpdateProgressTimer();
                            mNeedChangePosition = true;
                            mGestureDownPosition = mNiceVideoPlayer.getCurrentPosition();
                        }
                    } else if (absDeltaY >= THRESHOLD) {
                        if (mDownX < getWidth() * 0.5f) {
                            if(needBrightnessGesture){
                                // 左侧改变亮度
                                mNeedChangeBrightness = true;
                                mGestureDownBrightness = NiceUtil.scanForActivity(mContext)
                                        .getWindow().getAttributes().screenBrightness;
                            }
                        } else {
                            if(needVolumeGesture){
                                // 右侧改变声音
                                mNeedChangeVolume = true;
                                mGestureDownVolume = mNiceVideoPlayer.getVolume();
                            }
                        }
                    }
                }
                if (mNeedChangePosition) {
                    long duration = mNiceVideoPlayer.getDuration();
                    long toPosition = (long) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }
                if (mNeedChangeBrightness) {
                    deltaY = -deltaY;
                    float deltaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + deltaBrightness;
                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = NiceUtil.scanForActivity(mContext)
                            .getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    NiceUtil.scanForActivity(mContext).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }
                if (mNeedChangeVolume) {
                    deltaY = -deltaY;
                    int maxVolume = mNiceVideoPlayer.getMaxVolume();
                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    mNiceVideoPlayer.setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    showChangeVolume(newVolumeProgress);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mNeedClick && needClickGesture){
                    onTouchClick();
                }
            case MotionEvent.ACTION_CANCEL:
                if (mNeedChangePosition && needPositionGesture) {
                    mNiceVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                if (mNeedChangeBrightness && needBrightnessGesture) {
                    hideChangeBrightness();
                    return true;
                }
                if (mNeedChangeVolume && needVolumeGesture) {
                    hideChangeVolume();
                    return true;
                }
                break;
        }
        return needGesture;
    }

    /**
     * 手势touch down时调用
     */
    protected abstract void onTouchClick();

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangePosition();

    /**
     * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolumeProgress 新的音量进度，取值1到100。
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

    /**
     * 手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeVolume();

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress 新的亮度进度，取值1到100。
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeBrightness();

    public abstract int getThumbnailWidth();

    public abstract int getThumbnailHeight();

    protected abstract void videoStatus(int tag);

    protected abstract void release();
}
