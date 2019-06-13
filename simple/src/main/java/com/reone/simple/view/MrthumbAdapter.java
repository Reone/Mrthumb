package com.reone.simple.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reone.simple.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangxingsheng on 2019-06-13.
 * desc:
 */

public class MrthumbAdapter extends RecyclerView.Adapter<MrthumbAdapter.MrthumbViewHolder> {
    private Context context;
    private List<ProgressData> data;

    public MrthumbAdapter(List<ProgressData> data, Context context) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MrthumbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MrthumbViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_progress_item, parent, false));
    }

    public ProgressData getItem(int index) {
        if (index >= 0 && index < getItemCount()) {
            return data.get(index);
        }
        return new ProgressData(-2);
    }

    @Override
    public void onBindViewHolder(@NonNull final MrthumbViewHolder holder, int position) {
        ProgressData item = data.get(position);
        ViewGroup.LayoutParams param = holder.itemView.getLayoutParams();
        param.width = context.getResources().getDisplayMetrics().widthPixels / getItemCount();
        holder.itemView.setLayoutParams(param);
        switch (item.getState()) {
            case -1:
                holder.progressItem.setBackgroundColor(Color.RED);
                break;
            case 0:
                holder.progressItem.setBackgroundColor(Color.GRAY);
                break;
            case 1:
                holder.progressItem.setBackgroundColor(Color.GREEN);
                break;
            default:
                holder.progressItem.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MrthumbViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_item)
        View progressItem;

        public MrthumbViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
