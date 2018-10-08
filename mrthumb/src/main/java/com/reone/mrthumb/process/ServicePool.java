package com.reone.mrthumb.process;

import java.lang.ref.WeakReference;

/**
 * Created by wangxingsheng on 2018/10/8.
 */
public class ServicePool {
    private WeakReference<MrthumbService> mrthumbServiceWeakReference;
    private static ServicePool mInstance = null;

    public static ServicePool getInstance() {
        if (mInstance == null) {
            synchronized (ServicePool.class) {
                if (mInstance == null) {
                    mInstance = new ServicePool();
                }
            }
        }
        return mInstance;
    }

    public MrthumbService getMrthumbService() {
        if (mrthumbServiceWeakReference == null) return null;
        return mrthumbServiceWeakReference.get();
    }

    public void setMrthumbService(MrthumbService mrthumbService) {
        this.mrthumbServiceWeakReference = new WeakReference<>(mrthumbService);
    }
}
