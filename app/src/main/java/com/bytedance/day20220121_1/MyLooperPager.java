package com.bytedance.day20220121_1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

/**
 * 轮播图组合控件
 * 包括：ViewPager（轮播图）、TextView（标题）、LinearLayout（圆点指示器容器）三个控件
 */
public class MyLooperPager extends LinearLayout {
    /**
     * 组合控件中ViewPager控件
     */
    private MyViewPager myLooperPager;

    /**
     * 组合控件中标题控件
     */
    private TextView myLooperTitle;

    /**
     * 组合控件中指示器容器控件
     */
    private LinearLayout myLooperPointContainer;

    /**
     * 对外暴露接口
     */
    private BindTitleListener bindTitleListener;

    /**
     * 自定义PagerAdapter对象
     */
    private InnerAdapter innerAdapter;

    /**
     * 回调接口
     */
    private OnMyLooperPagerItemListener onMyLooperPagerItemListener;

    /**
     * 是否展示标题栏
     */
    private boolean isShowTitle;

    /**
     * 轮播时间间隔
     */
    private int switchTime;

    /**
     * 构造方法1
     *
     * @param context
     */
    public MyLooperPager(Context context) {
        this(context, null);
    }

    /**
     * 构造方法2
     *
     * @param context
     * @param attrs
     */
    public MyLooperPager(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法3
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public MyLooperPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
        initAction();
    }

    /**
     * 初始化属性值
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyLooperPager);
        isShowTitle = typedArray.getBoolean(R.styleable.MyLooperPager_isShowTitle, true);
        switchTime = typedArray.getInt(R.styleable.MyLooperPager_switchTime, -1);
        typedArray.recycle();
    }

    /**
     * 初始化View
     */
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.my_looper_pager_layout, this, true);
        myLooperPager = findViewById(R.id.my_looper_pager);
        if (switchTime != -1) {
            myLooperPager.setDelayTime(switchTime);
        }
        // 设置viewPager的预加载页面数量
        myLooperPager.setOffscreenPageLimit(3);
        myLooperPager.setPageMargin(SizeUtils.dip2px(context, 20f));
        myLooperTitle = findViewById(R.id.my_looper_title);
        myLooperPointContainer = findViewById(R.id.my_looper_point_container);
        if (!isShowTitle) {
            myLooperTitle.setVisibility(GONE);
        }
    }

    /**
     * 初始化事件
     */
    private void initAction() {
        myLooperPager.setOnMyViewPagerItemListener(new MyViewPager.OnMyViewPagerItemListener() {
            @Override
            public void onItemClick(int position) {
                if (onMyLooperPagerItemListener != null && innerAdapter != null) {
                    onMyLooperPagerItemListener.onItemClick(position % innerAdapter.getDataSize());
                }
            }
        });

        myLooperPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * page切换时的回调
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            /**
             * page切换停下来的回调
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                // 设置标题
                if (bindTitleListener != null && innerAdapter != null) {
                    myLooperTitle.setText(bindTitleListener.getTitle(position % innerAdapter.getDataSize()));
                }
                // 切换指示器焦点
                updateIndicatorPoint();

            }

            /**
             * page切换状态改变的回调
             * @param state
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 动态创建圆点指示器
     */
    private void updateIndicatorPoint() {
        if (innerAdapter != null) {
            int count = innerAdapter.getDataSize();
            myLooperPointContainer.removeAllViews();
            for (int i = 0; i < count; i++) {
                View view = new View(getContext());
                if (myLooperPager.getCurrentItem() % innerAdapter.getDataSize() == i) {
                    view.setBackgroundColor(Color.RED);
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
                // 圆点指示器添加到父容器（LinearLayout），需要创建LinearLayout.LayoutParams对象
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(SizeUtils.dip2px(getContext(), 5f), SizeUtils.dip2px(getContext(), 5f));
                lp.setMargins(SizeUtils.dip2px(getContext(), 5f), 0, SizeUtils.dip2px(getContext(), 5f), 0);
                view.setLayoutParams(lp);
                // 添加到容器中
                myLooperPointContainer.addView(view);
            }
        }
    }


    /**
     * 自定义PagerAdapter
     */
    public abstract static class InnerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @NotNull
        @Override
        public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
            View subView = getSubView(container, position);
            container.addView(subView);
            return subView;
        }

        public abstract View getSubView(ViewGroup container, int position);

        public abstract int getDataSize();
    }


    /**
     * 设置数据（对外暴露的方法）
     *
     * @param innerAdapter
     * @param listener
     */
    public void setData(InnerAdapter innerAdapter, BindTitleListener listener) {
        this.innerAdapter = innerAdapter;
        myLooperPager.setAdapter(innerAdapter);
        myLooperPager.setCurrentItem(Integer.MAX_VALUE / 2);
        this.bindTitleListener = listener;
        if (bindTitleListener != null) {
            myLooperTitle.setText(listener.getTitle(myLooperPager.getCurrentItem() % innerAdapter.getDataSize()));
        }
        // 根据数据的个数，动态创建圆点指示器（indicator）
        updateIndicatorPoint();
    }


    /**
     * 绑定Title信息的接口
     */
    public interface BindTitleListener {
        /**
         * 获取Title
         *
         * @return
         */
        String getTitle(int position);
    }

    /**
     * MyLooperPager的回调接口
     */
    public interface OnMyLooperPagerItemListener {
        /**
         * Item点击事件处理逻辑
         *
         * @param position
         */
        void onItemClick(int position);
    }

    /**
     * 设置MyLooperPager的回调接口
     *
     * @param onMyLooperPagerItemListener
     */
    public void setOnMyLooperPagerItemListener(OnMyLooperPagerItemListener onMyLooperPagerItemListener) {
        this.onMyLooperPagerItemListener = onMyLooperPagerItemListener;
    }

}
