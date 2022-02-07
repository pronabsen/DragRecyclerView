package com.pronabsen.dragrecyclerview.listener;

import android.view.View;

/**
 * Created by pronabsen
 */
public interface OnClickListener {
    void onItemClick(View v, int position);

    void onItemLongClick(View v, int position);
}
