package com.innovation.biz.iterm;

import android.graphics.Bitmap;

/**
 * Author by luolu, Date on 2018/10/11.
 * COMPANY：InnovationAI
 */

public class PostureItem {
    public float rot_x = -500; //原始X轴弧度值
    public float rot_y = -500; //原始Y轴弧度值
    public float rot_z = -500; //原始Z轴弧度值
    // TODO: 2018/9/12 By:LuoLu
    public float modelX0; //modelx0
    public float modelY0; //modely0
    public float modelX1; //modelx1
    public float modelY1; //modely1

    public float modelDetectedScore; //modelScore

    public float screenX0; //屏幕x0
    public float screenY0; //屏幕y0
    public float screenX1; //屏幕x1
    public float screenY1; //屏幕y1

    public Bitmap clipBitmap; //保存的原始大小的图片

    public Bitmap srcBitmap; //保存的放大到指定倍数的图片

    public PostureItem(float x, float y, float z, float x0, float y0, float x1, float y1,float score,
                       float sX0, float sY0, float sX1, float sY1, Bitmap bitmapClip, Bitmap bitmapSrc) {
        rot_x = x;
        rot_y = y;
        rot_z = z;

        modelX0 = x0;
        modelY0 = y0;
        modelX1 = x1;
        modelY1 = y1;

        modelDetectedScore = score;

        screenX0 = sX0;
        screenY0 = sY0;
        screenX1 = sX1;
        screenY1 = sY1;

        clipBitmap = bitmapClip;
        srcBitmap = bitmapSrc;
    }
}
