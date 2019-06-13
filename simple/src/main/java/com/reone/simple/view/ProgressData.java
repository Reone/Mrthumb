package com.reone.simple.view;

/**
 * Created by wangxingsheng on 2019-06-13.
 * desc:
 */
public class ProgressData {
    private int state;
    private long time;

    public ProgressData(int state) {
        this.state = state;
    }

    public ProgressData(int state, long time) {
        this.state = state;
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
