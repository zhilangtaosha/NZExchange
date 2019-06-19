package com.nze.nzexchange.widget.depth;

public class DepthDataBean {
    public DepthDataBean() {
    }

    public DepthDataBean(double mPrice, double mVolume) {
        this.mPrice = mPrice;
        this.mVolume = mVolume;
    }

    private double mPrice;
    private double mVolume;

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
