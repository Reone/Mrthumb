package com.reone.thumbnailbuffer.player;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.view.Surface;

import com.reone.mmrc.MediaMetadataRetrieverCompat;
import com.reone.mmrc.thumbnail.ThumbnailBuffer;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by wangxingsheng on 2018/6/6.
 *
 * 使用弱引用回收播放器使用资源
 *
 */
public class ReleasePlayerTask<Params, Progress, Result> extends AsyncTask {
    private WeakReference<AudioManager> audioManagerWeakReference;
    private WeakReference<IMediaPlayer> mediaPlayerWeakReference;
    private WeakReference<SurfaceTexture> surfaceTextureWeakReference;
    private WeakReference<Surface> surfaceWeakReference;
    private WeakReference<ThumbnailBuffer> thumbnailBufferWeakReference;
    private WeakReference<MediaMetadataRetrieverCompat> mediaMetadataRetrieverCompatWeakReference;

    public ReleasePlayerTask(AudioManager audioManager, IMediaPlayer mediaPlayer, SurfaceTexture surfaceTexture, Surface surface, ThumbnailBuffer thumbnailBuffer, MediaMetadataRetrieverCompat mmr) {
        this.audioManagerWeakReference = new WeakReference<>(audioManager);
        this.mediaPlayerWeakReference = new WeakReference<>(mediaPlayer);
        this.surfaceTextureWeakReference = new WeakReference<>(surfaceTexture);
        this.surfaceWeakReference = new WeakReference<>(surface);
        this.thumbnailBufferWeakReference = new WeakReference<>(thumbnailBuffer);
        this.mediaMetadataRetrieverCompatWeakReference = new WeakReference<>(mmr);
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
        if(surfaceTexture !=null){
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
        ThumbnailBuffer thumbnailBuffer = thumbnailBufferWeakReference.get();
        if(thumbnailBuffer!=null){
            thumbnailBuffer.release();
            thumbnailBufferWeakReference.clear();
            thumbnailBufferWeakReference = null;
            LogUtil.d("ReleasePlayerTask release thumbnailBuffer");
        }
        MediaMetadataRetrieverCompat mmr = mediaMetadataRetrieverCompatWeakReference.get();
        if(mmr!=null){
            mmr.release();
            mediaMetadataRetrieverCompatWeakReference.clear();
            mediaMetadataRetrieverCompatWeakReference = null;
            LogUtil.d("ReleasePlayerTask release mmr");
        }
        return null;
    }
}
