package com.bytedance.day20220121_1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;


/**
 * 支持自动轮播的自定义ViewPager
 */
public class MyViewPager extends ViewPager {

    /**
     * 自动轮播的时间间隔
     */
    private long delayTime = 2000;

    /**
     * 是否为点击事件
     */
    private boolean isClick = false;

    /**
     * down事件的X坐标
     */
    private float downX;

    /**
     * down事件的Y坐标
     */
    private float downY;

    /**
     * up事件的X坐标
     */
    private float upX;

    /**
     * up事件的Y坐标
     */
    private float upY;

    /**
     * down事件发生时间
     */
    private long downTime;

    /**
     * up事件发生时间
     */
    private long upTime;

    /**
     * MyViewPager的接口回调
     */
    public OnMyViewPagerItemListener onMyViewPagerItemListener;

    /**
     * MyViewPager的回调接口
     */
    public interface OnMyViewPagerItemListener {
        /**
         * Item点击事件处理逻辑
         *
         * @param position
         */
        void onItemClick(int position);
    }

    /**
     * 设置MyViewPager的接口回调
     *
     * @param onMyViewPagerItemListener
     */
    public void setOnMyViewPagerItemListener(OnMyViewPagerItemListener onMyViewPagerItemListener) {
        this.onMyViewPagerItemListener = onMyViewPagerItemListener;
    }

    /**
     * 自动轮播的Runnable对象
     * 自动更改currentItem值，实现自动轮播功能
     */
    private Runnable taskRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            postDelayed(this, delayTime);
        }
    };

    /**
     * 构造方法1
     *
     * @param context
     */
    public MyViewPager(@NonNull @NotNull Context context) {
        this(context, null);
    }

    /**
     * 构造方法2
     *
     * @param context
     * @param attrs
     */
    public MyViewPager(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        // 初始化事件处理
        initAction();
    }

    /**
     * 初始化事件处理
     * 问题：解决触摸时仍然自动轮播的问题，以及点击事件和触摸事件的滑动冲突问题
     * 思路: 1.触摸时仍然自动轮播的问题可以判断当前事件类型（down、up、cancel）,启动/取消自动轮播
     * 2.点击事件和触摸事件的滑动冲突问题可以不设置显性的setOnClickListener，即子View不消费事件，才会使该onTouch方法生效。具体可以判断点击事件和滑动事件的类型，并调用回调接口中点击事件的处理方法
     */
    private void initAction() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        downTime = System.currentTimeMillis();
                        stopLooper();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        upX = event.getX();
                        upY = event.getY();
                        upTime = System.currentTimeMillis();
                        float dx = Math.abs(upX - downX);
                        float dy = Math.abs(upY - downY);
                        float dTime = upTime - downTime;
                        // 判断当前事件是否为点击事件
                        if (dx <= 5 && dy <= 5 && dTime <= 1000) {
                            isClick = true;
                        } else {
                            isClick = false;
                        }
                        // 判断当前事件为点击事件，调用回调接口中点击事件的处理方法
                        if (isClick && onMyViewPagerItemListener != null) {
                            onMyViewPagerItemListener.onItemClick(getCurrentItem());
                        }
                        startLooper();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 当View附加到Window的时候回调
     * 此时开启自动轮播
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLooper();
    }

    /**
     * 当View与Window分离的时候回调
     * 此时关闭自动轮播，防止资源浪费
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLooper();
    }

    /**
     * 开启自动轮播
     */
    private void startLooper() {
        removeCallbacks(taskRunnable);
        // 延迟多少毫秒后开始运行指定的线程
        postDelayed(taskRunnable, delayTime);
    }

    /**
     * 关闭自动轮播
     */
    private void stopLooper() {
        // 删除指定的Runnable对象，使线程对象停止运行
        removeCallbacks(taskRunnable);
    }

    /**
     * 设置自动轮播的时间间隔
     *
     * @param delayTime
     */
    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }
}
