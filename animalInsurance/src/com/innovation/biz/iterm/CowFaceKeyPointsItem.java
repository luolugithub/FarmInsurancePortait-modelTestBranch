package com.innovation.biz.iterm;



import com.innovation.utils.PointFloat;

/**
 * Author by luolu, Date on 2018/10/12.
 * COMPANY：InnovationAI
 */

public class CowFaceKeyPointsItem {
    public int pointsExists0 = 0;
    public int pointsExists1 = 0;
    public int pointsExists2 = 0;
    public int pointsExists3 = 0;
    public int pointsExists4 = 0;
    public int pointsExists5 = 0;
    public int pointsExists6 = 0;
    public int pointsExists7 = 0;
    public int pointsExists8 = 0;
    public int pointsExists9 = 0;
    public int pointsExists10 = 0;
    public int pointsExists11 = 0;
    public int pointsExists12 = 0;

    public float x = 404, y = 404;
    PointFloat pointFloat0 = new PointFloat(x, y);
    PointFloat pointFloat1 = new PointFloat(x, y);
    PointFloat pointFloat2 = new PointFloat(x, y);
    PointFloat pointFloat3 = new PointFloat(x, y);
    PointFloat pointFloat4 = new PointFloat(x, y);
    PointFloat pointFloat5 = new PointFloat(x, y);
    PointFloat pointFloat6 = new PointFloat(x, y);
    PointFloat pointFloat7 = new PointFloat(x, y);
    PointFloat pointFloat8 = new PointFloat(x, y);
    PointFloat pointFloat9 = new PointFloat(x, y);
    PointFloat pointFloat10 = new PointFloat(x, y);
    PointFloat pointFloat11 = new PointFloat(x, y);
    PointFloat pointFloat12 = new PointFloat(x, y);

    public PointFloat getPointFloat0() {
        return pointFloat0;
    }

    public void setPointFloat0(PointFloat pointFloat0) {
        this.pointFloat0 = pointFloat0;
    }

    public PointFloat getPointFloat1() {
        return pointFloat1;
    }

    public void setPointFloat1(PointFloat pointFloat1) {
        this.pointFloat1 = pointFloat1;
    }

    public PointFloat getPointFloat2() {
        return pointFloat2;
    }

    public void setPointFloat2(PointFloat pointFloat2) {
        this.pointFloat2 = pointFloat2;
    }

    public PointFloat getPointFloat3() {
        return pointFloat3;
    }

    public void setPointFloat3(PointFloat pointFloat3) {
        this.pointFloat3 = pointFloat3;
    }

    public PointFloat getPointFloat4() {
        return pointFloat4;
    }

    public void setPointFloat4(PointFloat pointFloat4) {
        this.pointFloat4 = pointFloat4;
    }

    public PointFloat getPointFloat5() {
        return pointFloat5;
    }

    public void setPointFloat5(PointFloat pointFloat5) {
        this.pointFloat5 = pointFloat5;
    }

    public PointFloat getPointFloat6() {
        return pointFloat6;
    }

    public void setPointFloat6(PointFloat pointFloat6) {
        this.pointFloat6 = pointFloat6;
    }

    public PointFloat getPointFloat7() {
        return pointFloat7;
    }

    public void setPointFloat7(PointFloat pointFloat7) {
        this.pointFloat7 = pointFloat7;
    }

    public PointFloat getPointFloat8() {
        return pointFloat8;
    }

    public void setPointFloat8(PointFloat pointFloat8) {
        this.pointFloat8 = pointFloat8;
    }

    public PointFloat getPointFloat9() {
        return pointFloat9;
    }

    public void setPointFloat9(PointFloat pointFloat9) {
        this.pointFloat9 = pointFloat9;
    }

    public PointFloat getPointFloat10() {
        return pointFloat10;
    }

    public void setPointFloat10(PointFloat pointFloat10) {
        this.pointFloat10 = pointFloat10;
    }

    public PointFloat getPointFloat11() {
        return pointFloat11;
    }

    public void setPointFloat11(PointFloat pointFloat11) {
        this.pointFloat11 = pointFloat11;
    }

    public PointFloat getPointFloat12() {
        return pointFloat12;
    }

    public void setPointFloat12(PointFloat pointFloat12) {
        this.pointFloat12 = pointFloat12;
    }

    static CowFaceKeyPointsItem cowFaceKeyPointsItem;
    public static CowFaceKeyPointsItem getInstance() {
        if (cowFaceKeyPointsItem == null) {
            synchronized (CowFaceKeyPointsItem.class) {
                if (cowFaceKeyPointsItem == null) {
                    cowFaceKeyPointsItem = new CowFaceKeyPointsItem();
                }
            }
        }
        return cowFaceKeyPointsItem;
    }

    private CowFaceKeyPointsItem() {
    }

    @Override
    public String toString() {
        return "CowFaceKeyPointsItem{" +
                "pointsExists0=" + pointsExists0 +
                ", pointsExists1=" + pointsExists1 +
                ", pointsExists2=" + pointsExists2 +
                ", pointsExists3=" + pointsExists3 +
                ", pointsExists4=" + pointsExists4 +
                ", pointsExists5=" + pointsExists5 +
                ", pointsExists6=" + pointsExists6 +
                ", pointsExists7=" + pointsExists7 +
                ", pointsExists8=" + pointsExists8 +
                ", pointsExists9=" + pointsExists9 +
                ", pointsExists10=" + pointsExists10 +
                ", pointsExists11=" + pointsExists11 +
                ", pointsExists12=" + pointsExists12 +
                ", x=" + x +
                ", y=" + y +
                ", pointFloat0=" + pointFloat0 +
                ", pointFloat1=" + pointFloat1 +
                ", pointFloat2=" + pointFloat2 +
                ", pointFloat3=" + pointFloat3 +
                ", pointFloat4=" + pointFloat4 +
                ", pointFloat5=" + pointFloat5 +
                ", pointFloat6=" + pointFloat6 +
                ", pointFloat7=" + pointFloat7 +
                ", pointFloat8=" + pointFloat8 +
                ", pointFloat9=" + pointFloat9 +
                ", pointFloat10=" + pointFloat10 +
                ", pointFloat11=" + pointFloat11 +
                ", pointFloat12=" + pointFloat12 +
                '}';
    }

    public int getPointsExists0() {
        return pointsExists0;
    }

    public void setPointsExists0(int pointsExists0) {
        this.pointsExists0 = pointsExists0;
    }

    public int getPointsExists1() {
        return pointsExists1;
    }

    public void setPointsExists1(int pointsExists1) {
        this.pointsExists1 = pointsExists1;
    }

    public int getPointsExists2() {
        return pointsExists2;
    }

    public void setPointsExists2(int pointsExists2) {
        this.pointsExists2 = pointsExists2;
    }

    public int getPointsExists3() {
        return pointsExists3;
    }

    public void setPointsExists3(int pointsExists3) {
        this.pointsExists3 = pointsExists3;
    }

    public int getPointsExists4() {
        return pointsExists4;
    }

    public void setPointsExists4(int pointsExists4) {
        this.pointsExists4 = pointsExists4;
    }

    public int getPointsExists5() {
        return pointsExists5;
    }

    public void setPointsExists5(int pointsExists5) {
        this.pointsExists5 = pointsExists5;
    }

    public int getPointsExists6() {
        return pointsExists6;
    }

    public void setPointsExists6(int pointsExists6) {
        this.pointsExists6 = pointsExists6;
    }

    public int getPointsExists7() {
        return pointsExists7;
    }

    public void setPointsExists7(int pointsExists7) {
        this.pointsExists7 = pointsExists7;
    }

    public int getPointsExists8() {
        return pointsExists8;
    }

    public void setPointsExists8(int pointsExists8) {
        this.pointsExists8 = pointsExists8;
    }

    public int getPointsExists9() {
        return pointsExists9;
    }

    public void setPointsExists9(int pointsExists9) {
        this.pointsExists9 = pointsExists9;
    }

    public int getPointsExists10() {
        return pointsExists10;
    }

    public void setPointsExists10(int pointsExists10) {
        this.pointsExists10 = pointsExists10;
    }

    public int getPointsExists11() {
        return pointsExists11;
    }

    public void setPointsExists11(int pointsExists11) {
        this.pointsExists11 = pointsExists11;
    }

    public int getPointsExists12() {
        return pointsExists12;
    }

    public void setPointsExists12(int pointsExists12) {
        this.pointsExists12 = pointsExists12;
    }

    public static CowFaceKeyPointsItem getCowFaceKeyPointsItem() {
        return cowFaceKeyPointsItem;
    }

    public static void setCowFaceKeyPointsItem(CowFaceKeyPointsItem cowFaceKeyPointsItem) {
        CowFaceKeyPointsItem.cowFaceKeyPointsItem = cowFaceKeyPointsItem;
    }
}
