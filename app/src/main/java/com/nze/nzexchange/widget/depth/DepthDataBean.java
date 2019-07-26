package com.nze.nzexchange.widget.depth;

public class DepthDataBean {
    public DepthDataBean() {
    }

    public DepthDataBean(double mPrice, double mVolume) {
        this.mPrice = mPrice;
        this.mVolume = mVolume;
    }

    public DepthDataBean(double mPrice, double mVolume, String mPriceStr, String mVolueStr) {
        this.mPrice = mPrice;
        this.mVolume = mVolume;
        this.mPriceStr = mPriceStr;
        this.mVolueStr = mVolueStr;
    }

    private double mPrice;
    private double mVolume;
    private String mPriceStr;
    private String mVolueStr;

    public String getPriceStr() {
        return mPriceStr;
    }

    public void setPriceStr(String mPriceStr) {
        this.mPriceStr = mPriceStr;
    }

    public String getVolueStr() {
        return mVolueStr;
    }

    public void setVolueStr(String mVolueStr) {
        this.mVolueStr = mVolueStr;
    }

    public double getVolume() {
        return mVolume;
    }

    public void setVolume(double volume) {
        this.mVolume = volume;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        this.mPrice = price;
    }
}
