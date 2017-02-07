package com.example.administrator.qqslidingmenu.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.qqslidingmenu.R;

import java.util.Random;

/**
 * Created by Administrator on 2017/2/6.
 */

public class SlidingActivity extends Activity {
    private ListView menu_listview, main_listview;
    private SlidingLayout layout;
    private ImageView iv_head;
    private MyLinearLayout my_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        main_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                ScaleAnimation animation = new ScaleAnimation(0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF);
                animation.setDuration(300);
                textView.setAnimation(animation);
                animation.start();
                return textView;
            }
        });
        menu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);

                return textView;
            }
        });
        layout.setOnDragStateChangeListener(new SlidingLayout.onDragStateChangeListener() {
            @Override
            public void onClose() {
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                iv_head.startAnimation(shake);
            }

            @Override
            public void onOpen() {
                Random random = new Random();
                menu_listview.smoothScrollToPosition(random.nextInt(menu_listview.getCount()));
            }

            @Override
            public void onDraging(float fraction) {
                ValueAnimator valueAnimator = new ValueAnimator();
                valueAnimator.ofFloat(0, fraction);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float animatedValue = (float) animation.getAnimatedValue();
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0,animatedValue);
                        iv_head.setAnimation(alphaAnimation);
                        alphaAnimation.setDuration(2000);
                        alphaAnimation.start();
                    }
                });

            }
        });
    }

    private void initView() {
        setContentView(R.layout.sliding_activity);
        main_listview = (ListView) findViewById(R.id.main_listview);
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        layout = (SlidingLayout) findViewById(R.id.slidingLayout);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
        my_layout.setSlidingLayout(layout);
    }
}
