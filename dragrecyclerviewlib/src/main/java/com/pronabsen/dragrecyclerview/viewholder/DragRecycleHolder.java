package com.pronabsen.dragrecyclerview.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.pronabsen.dragrecyclerview.listener.OnClickListener;

/**
 * Created by pronabsen
 */
public class DragRecycleHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public static int mHandleId;
    public static OnClickListener mClickListener;
    private final View mHandle;

    public DragRecycleHolder(View view) {
        super(view);
        mHandle = view.findViewById(mHandleId);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    public View getHandle() {
        return mHandle;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemLongClick(v, getAdapterPosition());
        }
        return true;
    }

}
