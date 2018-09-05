package com.orego.corporation.orego.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orego.corporation.orego.BuildConfig;
import com.orego.corporation.orego.R;

import java.util.List;
import java.util.Random;




/**
 * Created by chensuilun on 2016/11/15.
 */
public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> implements View.OnClickListener {
    public static final int VIEW_TYPE_IMAGE = 0;
    public static final int VIEW_TYPE_TEXT = 1;
    private static final String TAG = "DemoAdapter";
    private List<String> mItems;
    private int mType = VIEW_TYPE_IMAGE;
    private OnItemClickListener mOnItemClickListener;


    public DemoAdapter(List<String> items) {
        this(items, VIEW_TYPE_IMAGE);
    }

    public DemoAdapter(List<String> items, int type) {
        this.mItems = items;
        mType = type;
    }

    public void addData() {
        if (mItems != null) {
            for (int i = 0; i < 10; i++) {
                mItems.add("Extra:" + i);
            }
            notifyDataSetChanged();
        }
    }

    private static final Random RANDOM = new Random();

    public int dataChange() {
        int result = 0;
        if (mItems != null) {
            if (RANDOM.nextBoolean()) {
                for (int i = 0; i < 10; i++) {
                    mItems.add("Extra:" + i);
                }
                result = 1;
            } else {
                int size = mItems.size();
                int cut = size / 2;
                for (int i = size - 1; i > cut; i--) {
                    mItems.remove(i);
                }
                result = -1;
            }
            notifyDataSetChanged();
        }
        return result;
    }

    public DemoAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        return mType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onCreateViewHolder: type:" + viewType);
        }
        Log.d("ABC", "DEMO createViewHolder");
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_demo, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onBindViewHolder: position:" + position);
        }
        String item = mItems.get(position);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    /**
     * @author chensuilun
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * @author chensuilun
     */
    public interface OnItemClickListener {

        void onItemClick(View view, int position);

    }
}
