package com.example.administrator.qqslidingmenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Switch;

/**
 * Created by Administrator on 2017/2/7.
 */

public class MyLinearLayout extends LinearLayout {
    private SlidingLayout slidingLayout;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSlidingLayout(SlidingLayout slidingLayout) {
        this.slidingLayout = slidingLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slidingLayout != null && slidingLayout.getSataes() == SlidingLayout.DragState.open) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slidingLayout != null && slidingLayout.getSataes() == SlidingLayout.DragState.open) {
            if (event.getAction()==MotionEvent.ACTION_UP){
                slidingLayout.close();
            }
            return true;
        }

        return super.onTouchEvent(event);
    }
}
