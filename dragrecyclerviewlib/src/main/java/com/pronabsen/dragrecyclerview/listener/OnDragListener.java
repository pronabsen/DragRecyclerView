package com.pronabsen.dragrecyclerview.listener;

/**
 * Created by pronabsen
 */

public interface OnDragListener {

    void onMove(int fromPosition, int toPosition);

    void onSwiped(int position);

    void onDrop(int fromPosition, int toPosition);

}
