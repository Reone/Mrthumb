package com.reone.thumbnailbuffer.player;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.view.Surface;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by wangxingsheng on 2018/6/6.
 * <p>
 * 使用弱引用回收播放器使用资源
 */
public class ReleasePlayerTask<Params, Progress, Result> extends AsyncTask {
    private WeakReference<AudioManager> audioManagerWeakReference;
    private WeakReference<IMediaPlayer> mediaPlayerWeakReference;
    private WeakReference<SurfaceTexture> surfaceTextureWeakReference;
    private WeakReference<Surface> surfaceWeakReference;

    public ReleasePlayerTask(AudioManager audioManager, IMediaPlayer mediaPlayer, SurfaceTexture surfaceTexture, Surface surface) {
        this.audioManagerWeakReference = new WeakReference<>(audioManager);
        this.mediaPlayerWeakReference = new WeakReference<>(mediaPlayer);
        this.surfaceTextureWeakReference = new WeakReference<>(surfaceTexture);
        this.surfaceWeakReference = new WeakReference<>(surface);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        LogUtil.d("ReleasePlayerTask doInBackground");
        AudioManager audioManager = audioManagerWeakReference.get();
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
            audioManagerWeakReference.clear();
            audioManagerWeakReference = null;
            LogUtil.d("ReleasePlayerTask release audioManager");
        }
        IMediaPlayer iMediaPlayer = mediaPlayerWeakReference.get();
        if (iMediaPlayer != null) {
            iMediaPlayer.release();
            mediaPlayerWeakReference.clear();
            mediaPlayerWeakReference = null;
            LogUtil.d("ReleasePlayerTask release iMediaPlayer");
        }
        SurfaceTexture surfaceTexture = surfaceTextureWeakReference.get();
        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTextureWeakReference.clear();
            surfaceTextureWeakReference = null;
            LogUtil.d("ReleasePlayerTask release surfaceTexture");
        }
        Surface surface = surfaceWeakReference.get();
        if (surface != null) {
            surface.release();
            surfaceWeakReference.clear();
            surfaceWeakReference = null;
            LogUtil.d("ReleasePlayerTask release surface");
        }
        return null;
    }
}
