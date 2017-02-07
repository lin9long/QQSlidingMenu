package com.example.administrator.qqslidingmenu.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.administrator.qqslidingmenu.R;

/**
 * Created by Administrator on 2017/2/5.
 */

public class DrawLayout extends FrameLayout {

    private View redChild;
    private View blueChild;
    private ViewDragHelper viewDragHelper;

    public DrawLayout(Context context) {
        super(context);
        initData();
    }

    public DrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public DrawLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redChild = getChildAt(0);
        blueChild = getChildAt(1);
    }

    //如果子控件没有特殊摆放需求，可以直接集成FramLayout，省略onMeasure的方法
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = (int) getResources().getDimension(R.dimen.width);
//        int measurespec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        redChild.measure(measurespec, measurespec);
//        blueChild.measure(measurespec, measurespec);
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = getPaddingLeft();
        int top = getPaddingTop();
        redChild.layout(left, top, left + redChild.getMeasuredWidth(), top + redChild.getMeasuredHeight());
        blueChild.layout(left, redChild.getBottom(), left + blueChild.getMeasuredWidth(), top + redChild.getBottom() + blueChild.getMeasuredHeight());
    }

    /**
     * 将触摸事件传递给viewDragHelper，响应触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 让viewDragHelper帮我们判断是否拦截事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean onIntercept = viewDragHelper.shouldInterceptTouchEvent(ev);
        return onIntercept;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 返回当前尝试捕获移动的view
         * @param child 当前选中的view对象
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == blueChild || child == redChild;
        }

        /**
         * 修正水平坐标的值
         * @param child
         * @param left 当前控件移动前距离左边的位置
         * @param dx 移动的水平偏移量
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - blueChild.getMeasuredWidth()) {
                left = getMeasuredWidth() - blueChild.getMeasuredWidth();
            }
            return left;
        }

        /**
         * 修正垂直坐标的值
         * @param child
         * @param top 当前控件移动前距离顶部位置
         * @param dy 移动的垂直偏移量
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - blueChild.getMeasuredHeight()) {
                top = getMeasuredHeight() - blueChild.getMeasuredHeight();
            }
            return top;
        }

        /**
         * 水平最大的移动距离
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - blueChild.getMeasuredWidth();
        }

        /**
         * 垂直最大的移动距离
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - blueChild.getMeasuredHeight();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == blueChild) {
                redChild.layout(left, redChild.getTop() + dy, left + redChild.getMeasuredWidth(),
                        redChild.getTop() + redChild.getMeasuredHeight() + dy);
            } else if (changedView == redChild) {
                blueChild.layout(left, blueChild.getTop() + dy, left + blueChild.getMeasuredWidth(),
                        blueChild.getTop() + blueChild.getMeasuredHeight() + dy);
            }
            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - blueChild.getMeasuredWidth());
            setViewAnimation(fraction);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centreLeft = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centreLeft) {
                //自动向左移动
                viewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
                //刷新整个DrawLayout的界面
                ViewCompat.postInvalidateOnAnimation(DrawLayout.this);
            } else {
                //自动向右移动
                viewDragHelper.smoothSlideViewTo(releasedChild, getMeasuredWidth() - blueChild.getMeasuredWidth(), releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DrawLayout.this);
            }
        }

    };

    private void setViewAnimation(float fraction) {
        // redChild.setAlpha(fraction);
        redChild.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
        redChild.setRotationX(360 * fraction);
        blueChild.setRotationY(360 * fraction);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DrawLayout.this);
        }
    }
}
