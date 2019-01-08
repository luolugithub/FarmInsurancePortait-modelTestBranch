package com.innovation.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.innovation.base.GlobalConfigure;

import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Luolu on 2018/11/16.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class Video2Bitmap {
    private static final Logger logger = new Logger();
    public static String vBitmapName;

    public static Bitmap extractImage(MediaMetadataRetriever retriever, int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        String extractVideo2BitmapName = sdf.format(new Date(System.currentTimeMillis()));
        Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        File videoFile = new File(GlobalConfigure.VideoFileName);
        vBitmapName = extractVideo2BitmapName + "_" + (i / 500) + ".jpeg";
//        String video2BitmapSrc = "/sdcard/Android/data/com.innovation.animal_model_image/cache/innovation/animal/v2BitmapSrc";
//        File file = new File(video2BitmapSrc, vBitmapName);
//        FileUtils.saveBitmapToFile(bitmap, file);
//        logger.i("extract img " + file.getAbsolutePath());
        return bitmap;
    }

    public static long extractVideoInfo(MediaMetadataRetriever retriever) {
        retriever.setDataSource(GlobalConfigure.VideoFileName);
        String w = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String h = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        logger.i("width=%s, height=%s, duration=%s", w, h, duration);
        return Long.parseLong(duration);
    }



}
