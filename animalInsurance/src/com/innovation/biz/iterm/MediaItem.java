package com.innovation.biz.iterm;

import com.innovation.base.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author wbs on 11/2/17.
 */

public class MediaItem {
    private static final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    private static final String VIDEO_SUFFIX = ".mp4";
    public static final String DETECTED = "detected";
    /**
     * The name of path and video
     */
    private String mGenName;

    private String mVideoFile;

    /**
     * 建库 or 验证
     */
    private Model mModel;

    public MediaItem(Model model) {
        mModel = model;
        mGenName = mSimpleDateFormat.format(new Date(System.currentTimeMillis()));
        mGenName = mGenName + "_" + model.value();
    }

    public Model getModel() {
        return mModel;
    }

    public String genName() {
        return mGenName;
    }

//    public String videoFile(Context context) {
//        if (context == null) {
//            return null;
//        }
//        if (mVideoFile == null) {
//            File mediaDir = new File(StorageUtils.getExternalCacheDir(context), mGenName);
//            if (!mediaDir.exists()) {
//                mediaDir.mkdirs();
//            }
//            mVideoFile = new File(mediaDir, mGenName).getAbsolutePath() + VIDEO_SUFFIX;
//        }
//        return mVideoFile;
//    }



}
