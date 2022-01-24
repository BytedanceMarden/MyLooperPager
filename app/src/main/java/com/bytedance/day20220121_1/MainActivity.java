package com.bytedance.day20220121_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * 轮播图数据
     */
    private List<PagerItem> data = new ArrayList<>();

    /**
     * 轮播图组合控件对象
     */
    private MyLooperPager myLooperPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initAction();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        data.add(new PagerItem("第一张图片", R.mipmap.pic0));
        data.add(new PagerItem("第二张图片", R.mipmap.pic1));
        data.add(new PagerItem("第三张图片", R.mipmap.pic2));
        data.add(new PagerItem("第四张图片", R.mipmap.pic3));
        data.add(new PagerItem("第五张图片", R.mipmap.pic4));
    }

    /**
     * 初始化View
     */
    private void initView() {
        myLooperPager = this.findViewById(R.id.test);
        myLooperPager.setData(innerAdapter, bindTitleListener);
    }

    /**
     * 初始化点击事件
     */
    private void initAction() {
        myLooperPager.setOnMyLooperPagerItemListener(new MyLooperPager.OnMyLooperPagerItemListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "当前位置是：" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * InnerAdapter对象
     */
    MyLooperPager.InnerAdapter innerAdapter = new MyLooperPager.InnerAdapter() {
        @Override
        public View getSubView(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(data.get(position % data.size()).getPicResId());
            return imageView;
        }

        @Override
        public int getDataSize() {
            return data.size();
        }
    };

    /**
     * BindTitleListener对象
     */
    MyLooperPager.BindTitleListener bindTitleListener = new MyLooperPager.BindTitleListener() {
        @Override
        public String getTitle(int position) {
            return data.get(position).getTitle();
        }
    };

}