package com.pronabsen.dragrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import com.pronabsen.dragrecyclerview.callback.DragTouchCallback;
import com.pronabsen.dragrecyclerview.implement.ImplementAdapter;
import com.pronabsen.dragrecyclerview.implement.ImplementRecycleView;

/**
 * Created by pronabsen
 */

public class DragRecyclerView extends RecyclerView implements ImplementRecycleView {
    private ItemTouchHelper mItemTouchHelper;

    private int mHandleId = -1;
    private DragTouchCallback mTouchHelperCallback;

    public DragRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttrs(context, attrs);
    }

    public DragRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttrs(context, attrs);
    }

    private void setAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Drag);
        mHandleId = a.getResourceId(R.styleable.Drag_handler_id, -1);
        a.recycle();
    }

    ImplementAdapter getDragAdapter() {
        return (ImplementAdapter) getAdapter();
    }

    @Override
    public void setAdapter(DragRecyclerView.Adapter adapter) {
        super.setAdapter(adapter);

        getDragAdapter().setHandleId(mHandleId);

        mTouchHelperCallback = new DragTouchCallback((com.pronabsen.dragrecyclerview.listener.OnDragListener) super.getAdapter());
        mItemTouchHelper = new ItemTouchHelper(mTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(this);
        getDragAdapter().setRecycleView(this);
    }

    public DragTouchCallback getTouchHelperCallback() {
        return mTouchHelperCallback;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return mItemTouchHelper;
    }

}
