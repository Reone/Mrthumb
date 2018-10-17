package com.reone.mrthumb.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        CacheType.DISPERSION,
        CacheType.ORDER
})
@Retention(RetentionPolicy.SOURCE)
public @interface CacheType {
    int DISPERSION = 0;
    int ORDER = 1;
}
