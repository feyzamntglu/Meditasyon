package com.example.feyzaminnetoglu;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;
    public RecyclerItemClickListener(Context context, AdapterView.OnItemClickListener onItemClickListener){
        this.mListener=(OnItemClickListener) onItemClickListener;
        this.mGestureDetector=new GestureDetector(context, (GestureDetector.OnGestureListener) new GestureDetector.SimpleOnGestureListener(){

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent( RecyclerView recyclerView, MotionEvent motionEvent) {
        View view=recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if(view != null && this.mListener != null && this.mGestureDetector.onTouchEvent(motionEvent)){
            this.mListener.onItemClick(view, recyclerView.getChildAdapterPosition(view));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }
    public static interface OnItemClickListener{
        public void onItemClick(View view1, int position);
    }
}
