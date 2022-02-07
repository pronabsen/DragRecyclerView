package com.pronabsen.dragrecyclerview.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.pronabsen.dragrecyclerview.viewholder.DragRecycleHolder;
import com.pronabsen.dragrecyclerview.DragRecyclerView;
import com.pronabsen.dragrecyclerview.implement.ImplementAdapter;
import com.pronabsen.dragrecyclerview.listener.OnClickListener;
import com.pronabsen.dragrecyclerview.listener.OnDragListener;

import java.util.List;

/**
 * Created by pronabsen
 */

public abstract class DragRecycleAdapter extends RecyclerView.Adapter implements ImplementAdapter, OnDragListener {

    private final Context mContext;
    private List mData;
    private boolean isHandleDragEnabled = true;
    private OnDragListener mDragListener;
    private DragRecyclerView mRecyclerView;

    public DragRecycleAdapter(Context context, List data) {
        mContext = context;
        mData = data;
    }

    @Override
    public void onBindViewHolder(DragRecyclerView.ViewHolder viewHolder, int position) {
        final DragRecycleHolder holder = (DragRecycleHolder) viewHolder;

        View handle = holder.getHandle();
        if (handle == null || !isHandleDragEnabled) {
            return;
        }
        handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mRecyclerView.getItemTouchHelper().startDrag(holder);
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (mDragListener != null) {
            mDragListener.onMove(fromPosition, toPosition);
        }

        notifyItemMoved(fromPosition, toPosition);
    }


    @Override
    public void onSwiped(int position) {
        mData.remove(position);
        notifyItemRemoved(position);

        if (mDragListener != null) {
            mDragListener.onSwiped(position);
        }
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        List listData = getData();
        listData.add(toPosition, listData.remove(fromPosition));

        if (mDragListener != null) {
            mDragListener.onDrop(fromPosition, toPosition);
        }
    }

    public List getData() {
        return mData;
    }

    public void setOnItemDragListener(OnDragListener dragListener) {
        mDragListener = dragListener;
    }

    public void setOnItemClickListener(OnClickListener clickListener) {
        DragRecycleHolder.mClickListener = clickListener;
    }

    public void setHandleId(int handleId) {
        DragRecycleHolder.mHandleId = handleId;
    }

    public void setHandleDragEnabled(boolean dragEnabled) {
        isHandleDragEnabled = dragEnabled;
    }

    public void setRecycleView(DragRecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void setLongPressDragEnabled(boolean set) {
        mRecyclerView.getTouchHelperCallback().setLongPressDragEnabled(set);
    }

    public void setSwipeEnabled(boolean set) {
        mRecyclerView.getTouchHelperCallback().setItemViewSwipeEnabled(set);
    }


}
