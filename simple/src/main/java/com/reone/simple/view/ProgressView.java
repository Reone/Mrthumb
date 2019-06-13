package com.reone.simple.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.reone.simple.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by wangxingsheng on 2019-06-13.
 * desc:为了方便展示缩略图的加载进度
 */
public class ProgressView extends BaseCustomizeFrame {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    MrthumbAdapter mrthumbAdapter;
    private List<ProgressData> list;

    public ProgressView(@NonNull Context context) {
        super(context);
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initEventAndData() {
        list = new ArrayList<>();
        mrthumbAdapter = new MrthumbAdapter(list, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(mrthumbAdapter);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.layout_recycle_view;
    }

    /**
     * 缩略图加载进度回调
     *
     * @param index      缩略图加载位置
     * @param cacheCount 已缓存数量
     * @param maxCount   需要缓存总数
     * @param time       缓存缩略图所在秒数
     * @param duration   视频总时长
     */
    public void process(int index, int cacheCount, int maxCount, long time, long duration) {
        initList(maxCount);
        if (index >= 0 && index < maxCount) {
            mrthumbAdapter.getItem(index).setState(1);
            mrthumbAdapter.getItem(index).setTime(time);
            mrthumbAdapter.notifyItemChanged(index);
        }
    }

    private void initList(int maxCount) {
        if (list.size() < maxCount) {
            int count = maxCount - list.size();
            for (int i = 0; i < count; i++) {
                list.add(new ProgressData(0));
            }
            recyclerView.setAdapter(mrthumbAdapter);
            mrthumbAdapter.notifyDataSetChanged();
        }
    }
}
