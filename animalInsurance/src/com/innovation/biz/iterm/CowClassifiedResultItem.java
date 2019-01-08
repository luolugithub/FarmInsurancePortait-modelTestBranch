package com.innovation.biz.iterm;


import android.graphics.Bitmap;

/**
 * Author by luolu, Date on 2018/10/12.
 * COMPANY：InnovationAI
 */

public class CowClassifiedResultItem {

    public Bitmap clipBitmap;
    public Bitmap srcBitmap;

    public Bitmap getClipBitmap() {
        return clipBitmap;
    }

    public void setClipBitmap(Bitmap clipBitmap) {
        this.clipBitmap = clipBitmap;
    }

    public Bitmap getSrcBitmap() {
        return srcBitmap;
    }

    public void setSrcBitmap(Bitmap srcBitmap) {
        this.srcBitmap = srcBitmap;
    }

    static CowClassifiedResultItem cowClassifiedResultItem;
    public static CowClassifiedResultItem getInstance() {
        if (cowClassifiedResultItem == null) {
            synchronized (CowClassifiedResultItem.class) {
                if (cowClassifiedResultItem == null) {
                    cowClassifiedResultItem = new CowClassifiedResultItem();
                }
            }
        }
        return cowClassifiedResultItem;
    }

    private CowClassifiedResultItem() {
    }




    public static CowClassifiedResultItem getCowClassifiedResultItem() {
        return cowClassifiedResultItem;
    }

    public static void setCowClassifiedResultItem(CowClassifiedResultItem cowClassifiedResultItem) {
        CowClassifiedResultItem.cowClassifiedResultItem = cowClassifiedResultItem;
    }
}
