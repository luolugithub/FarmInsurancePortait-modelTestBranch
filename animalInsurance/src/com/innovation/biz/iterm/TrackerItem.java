package com.innovation.biz.iterm;

import android.graphics.RectF;

/**
 * Author by luolu, Date on 2018/10/19.
 * COMPANY：InnovationAI
 */

public class TrackerItem {

    public RectF mRect; //绘制框
    public int mAngletype; //角度说明
    public float mRot_x; //原始X轴弧度值
    public float mRot_y; //原始Y轴弧度值
    public float mRot_z; //原始Z轴弧度值

    public TrackerItem(int angletype, RectF rect, float rot_x, float rot_y, float rot_z)
    {
        this.mAngletype = angletype;
        this.mRect = rect;
        this.mRot_x = rot_x;
        this.mRot_y = rot_y;
        this.mRot_z = rot_z;
    }


}
