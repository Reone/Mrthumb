package com.reone.mrthumb.process;

/**
 * Created by wangxingsheng on 2018/10/8.
 */
public class ServicePool {
    private MrthumbService mrthumbService;
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
        return mrthumbService;
    }

    public void setMrthumbService(MrthumbService mrthumbService) {
        this.mrthumbService = mrthumbService;
    }
}
