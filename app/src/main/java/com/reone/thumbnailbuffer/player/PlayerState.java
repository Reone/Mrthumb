package com.reone.thumbnailbuffer.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wangxingsheng on 2018/9/27.
 */
@IntDef({NiceVideoPlayer.STATE_IDLE,
        NiceVideoPlayer.STATE_PREPARING,
        NiceVideoPlayer.STATE_PREPARED,
        NiceVideoPlayer.STATE_COMPLETED,
        NiceVideoPlayer.STATE_BUFFERING_PLAYING,
        NiceVideoPlayer.STATE_BUFFERING_PAUSED,
        NiceVideoPlayer.STATE_PLAYING,
        NiceVideoPlayer.STATE_PAUSED,
        NiceVideoPlayer.STATE_ERROR
})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerState {
}
