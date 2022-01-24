package com.bytedance.day20220121_1;

public class PagerItem {
    /**
     * 标题
     */
    private String title;

    /**
     * 图片资源ID
     */
    private Integer picResId;

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置图片资源ID
     *
     * @param picResId
     */
    public void setPicResId(Integer picResId) {
        this.picResId = picResId;
    }

    /**
     * 获取标题
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 获取图片资源ID
     *
     * @return
     */
    public Integer getPicResId() {
        return picResId;
    }

    /**
     * 构造方法1
     */
    public PagerItem() {

    }

    /**
     * 构造方法2
     *
     * @param title
     * @param picResId
     */
    public PagerItem(String title, Integer picResId) {
        this.title = title;
        this.picResId = picResId;
    }
}
