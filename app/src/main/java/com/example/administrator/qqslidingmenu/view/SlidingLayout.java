package com.example.administrator.qqslidingmenu.view;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/2/6.
 */

public class SlidingLayout extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private View main;
    private float fraction;
    private FloatEvaluator fev = new FloatEvaluator();
    private IntEvaluator iev = new IntEvaluator();
    private View menu;
    private float horizontalRange;
    private int width;

    public SlidingLayout(Context context) {
        super(context);
        init();
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        horizontalRange = width * 0.6f;
    }

    //加载完成后获取子view控件
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        main = getChildAt(1);
    }

    //枚举两个状态，同时给初始值赋予关闭状态
    enum DragState {
        close, open;
    }

    public DragState state = DragState.close;

    public DragState getSataes() {
        return state;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean onIntercept = viewDragHelper.shouldInterceptTouchEvent(ev);
        return onIntercept;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {


        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == main || child == menu;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {

            return (int) horizontalRange;
        }


        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == main) {
                if (left < 0) left = 0;
                if (left > horizontalRange) left = (int) horizontalRange;
            }

            return left;

        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menu) {
                //让menu菜单保持当前位置不动，但dx值必须要生成，确保main可以移动，此处可以写死menu的layout方法
                menu.layout(0, 0, menu.getMeasuredWidth(), menu.getMeasuredHeight());
                int newLeft = main.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;
                if (newLeft > horizontalRange) newLeft = (int) horizontalRange;
                main.layout(newLeft, main.getTop() + dy, newLeft + main.getMeasuredWidth(),
                       main.getMeasuredHeight() + dy);
            }
            fraction = (float) main.getLeft() / horizontalRange;
            excuteAnim();
            if (fraction == 0 && state != DragState.close) {
                if (mListener != null) {
                    mListener.onClose();
                    state = DragState.close;
                }
            }
            if (fraction == 1 && state != DragState.open) {
                if (mListener != null) {
                    mListener.onOpen();
                    state = DragState.open;
                }
            }

            if (mListener != null) mListener.onDraging(fraction);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (main.getLeft() > horizontalRange / 2) {
                open();
            } else {
                close();
            }
//            if (xvel > 200) {
//                open();
//            } else if (xvel < -200) {
//                close();
//            }
        }


        private void excuteAnim() {
            main.setScaleX(fev.evaluate(fraction, 1f, 0.8f));
            main.setScaleY(fev.evaluate(fraction, 1f, 0.8f));

            menu.setTranslationX(iev.evaluate(fraction, -menu.getMeasuredWidth(), 0));
            menu.setScaleX(fev.evaluate(fraction, 0.6f, 1f));
            menu.setScaleY(fev.evaluate(fraction, 0.6f, 1f));

            //获取背景设置背景的渐变色（黑色-透明的渐变效果）
            getBackground().setColorFilter((int) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
        }

    };

    public void open() {
        viewDragHelper.smoothSlideViewTo(main, (int) horizontalRange, 0);
        ViewCompat.postInvalidateOnAnimation(SlidingLayout.this);

    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(main, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SlidingLayout.this);

    }


    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlidingLayout.this);
        }
    }

    public onDragStateChangeListener mListener;

    public void setOnDragStateChangeListener(onDragStateChangeListener listener) {
        this.mListener = listener;
    }

    public interface onDragStateChangeListener {
        void onClose();

        void onOpen();

        void onDraging(float fraction);
    }
}
