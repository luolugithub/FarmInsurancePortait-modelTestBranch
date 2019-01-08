package com.innovation.base;

import com.innovation.biz.iterm.MediaInsureItem;
import com.innovation.biz.iterm.MediaPayItem;


/**
 * Author by luolu, Date on 2018/8/16.
 * COMPANY：InnovationAI
 */

public class GlobalConfigure {

    public static int FrameWidth = 0;
    public static int FrameHeight = 0;
    public static MediaInsureItem mediaInsureItem = null;
    public static MediaPayItem mediaPayItem = null;
    public static String zipVideoFileName = "";
    public static String ZipImageFileName = "";
    public static String ZipFileName = "";
    public static int model = Model.BUILD.value();
    public static Boolean numberOk = false;



    // 当APP采集并上传视频时设为TRUE
    public static  Boolean UPLOAD_VIDEO_FLAG = true;

    public static boolean VIDEO_PROCESS = false;

    //Constants
    public static final String IMAGE_JPEG = "jpg";
    public static final String VIDEO_MP4 = "mp4";
    public static final String IMAGE_SUFFIX = ".jpg";
    public static final String VIDEO_SUFFIX = ".mp4";

    public static final String FILEPRE_IMAGE = "image";
    public static final String FILEPRE_VIDEO = "video";

    public static boolean DeviceOrientation = false;

    public static String VideoFileName = "";
    public static int Func_type;
    public static final int Func_Insurance = 1;
    public static final int Func_Pay = 2;
    public static int waitUploadCount ;


}
