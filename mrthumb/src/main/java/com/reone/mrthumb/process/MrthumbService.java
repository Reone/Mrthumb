package com.reone.mrthumb.process;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.reone.tbufferlib.BuildConfig;

/**
 * Created by wangxingsheng on 2018/9/28.
 */
public class MrthumbService extends IntentService {

    public MrthumbService() {
        this("MrthumbService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MrthumbService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private void log(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(MrthumbService.class.getSimpleName(), log);
        }
    }

}
