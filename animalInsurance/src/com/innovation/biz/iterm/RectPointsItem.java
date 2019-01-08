package com.innovation.biz.iterm;



import com.innovation.utils.PointFloat;

/**
 * Author by luolu, Date on 2018/10/12.
 * COMPANY：InnovationAI
 */

public class RectPointsItem {

    public float x = 404, y = 404;
    private PointFloat rectPointFloat0 = new PointFloat(x, y);
    private PointFloat rectPointFloat1 = new PointFloat(x, y);
    private PointFloat rectPointFloat2 = new PointFloat(x, y);
    private PointFloat rectPointFloat3 = new PointFloat(x, y);
    private PointFloat rectPointFloat4 = new PointFloat(x, y);
    private PointFloat rectPointFloat5 = new PointFloat(x, y);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public PointFloat getRectPointFloat0() {
        return rectPointFloat0;
    }

    public void setRectPointFloat0(PointFloat rectPointFloat0) {
        this.rectPointFloat0 = rectPointFloat0;
    }

    public PointFloat getRectPointFloat1() {
        return rectPointFloat1;
    }

    public void setRectPointFloat1(PointFloat rectPointFloat1) {
        this.rectPointFloat1 = rectPointFloat1;
    }

    public PointFloat getRectPointFloat2() {
        return rectPointFloat2;
    }

    public void setRectPointFloat2(PointFloat rectPointFloat2) {
        this.rectPointFloat2 = rectPointFloat2;
    }

    public PointFloat getRectPointFloat3() {
        return rectPointFloat3;
    }

    public void setRectPointFloat3(PointFloat rectPointFloat3) {
        this.rectPointFloat3 = rectPointFloat3;
    }

    public PointFloat getRectPointFloat4() {
        return rectPointFloat4;
    }

    public void setRectPointFloat4(PointFloat rectPointFloat4) {
        this.rectPointFloat4 = rectPointFloat4;
    }

    public PointFloat getRectPointFloat5() {
        return rectPointFloat5;
    }

    public void setRectPointFloat5(PointFloat rectPointFloat5) {
        this.rectPointFloat5 = rectPointFloat5;
    }

    static RectPointsItem rectPointsItem;
    public static RectPointsItem getInstance() {
        if (rectPointsItem == null) {
            synchronized (RectPointsItem.class) {
                if (rectPointsItem == null) {
                    rectPointsItem = new RectPointsItem();
                }
            }
        }
        return rectPointsItem;
    }

    private RectPointsItem() {
    }


    public static RectPointsItem getRectPointsItem() {
        return rectPointsItem;
    }

    public static void setRectPointsItem(RectPointsItem rectPointsItem) {
        RectPointsItem.rectPointsItem = rectPointsItem;
    }
}
