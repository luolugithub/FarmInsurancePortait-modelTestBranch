package com.innvocation.upload;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.innovation.base.GlobalConfigure;
import com.innovation.base.Model;
import com.innovation.login.RespObject;
import com.innovation.login.Utils;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.UploadObject;
import com.innovation.utils.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.tensorflow.demo.env.Logger;

/**
 * @author wbs on 11/25/17.
 */

public class UploadThread extends HandlerThread {
    private Logger mLogger = new Logger(UploadThread.class);

    private static final int MSG_UPLOAD = 1;
    private static final int MSG_UPLOAD_NEW = 2;

    @SuppressLint("StaticFieldLeak")
    private static UploadThread sInstance;
    private final Context mContext;
    private UploadHandler mHandler;

    public static UploadThread getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UploadThread.class) {
                if (sInstance == null) {
                    sInstance = new UploadThread(context);
                }
            }
        }
        return sInstance;
    }

    private UploadThread(Context context) {
        super("upload-thread");
        start();
        mContext = context;
        mHandler = new UploadHandler(getLooper());
        registerBroadcastReceiver(context);
    }

    private void registerBroadcastReceiver(Context context) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null
                    || !intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }
            mLogger.i("onReceive " + intent.getAction());
            //upload(); //haojie del
        }
    };

    @SuppressLint("HandlerLeak")
    private class UploadHandler extends Handler {
        UploadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD:
//                    upload(2);
                    uploadInternal();
                    break;
                case MSG_UPLOAD_NEW:
//                    upload(2);
                    uploadInternal_new();
                    break;
                default:
            }
        }

        private void uploadInternal() {
            SharedPreferences pref = mContext.getSharedPreferences(Utils.VIDEOINFO_SHAREFILE, Context.MODE_PRIVATE);
            Map<String, ?> map = pref.getAll();
            if (map == null || map.isEmpty()) {
                mLogger.i("0704 No video need upload ！！！！");
                return;
            }
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                int lib_id = Integer.parseInt(entry.getKey());
                String value = (String) entry.getValue();
                String[] split = value.split("\\|");
                if (split.length != 3) {
                    pref.edit().remove(String.valueOf(lib_id)).apply();
                    mLogger.e("remove invalid video %d, %s", lib_id, value);
                    continue;
                }
                File videoFile = new File(split[1]);
                String name = videoFile.getName();
                int index = name.indexOf(".");
                if (index != -1) {
                    name = name.substring(0, index);
                }
                File zipFile = new File(videoFile.getParentFile(), name + ".zip");
                ZipUtil.zipFile(split[1], zipFile.getAbsolutePath());
                if (!zipFile.exists()) {
                    mLogger.e("zip video file failed!");
                    continue;
                }
                UploadResp uploadResp = UploadHelper.uploadVideo(mContext, Integer.valueOf(split[0]), lib_id, split[2], zipFile);
                if (uploadResp != null && uploadResp.status == RespObject.STATUS_0) {
                    pref.edit().remove(String.valueOf(lib_id)).apply();
                    FileUtils.deleteFile(split[1]);
                    mLogger.i("0704 upload local video success");
                }
                FileUtils.deleteFile(zipFile);
            }
        }

        //上传所有未上传的zip文件
        private void uploadInternal_new() {
            if (GlobalConfigure.model == Model.BUILD.value()) {
                upload(Model.BUILD.value());
            }
            if (GlobalConfigure.model == Model.VERIFY.value()) {
                upload(Model.VERIFY.value());
            }
            return;
        }

        private UploadObject upload_zipImage(int model, File zipFile_image, int uid, String libMum)
        {
            UploadObject imgResp = HttpUtils.uploadImages(mContext, model, zipFile_image, uid, libMum);

            if (imgResp == null || imgResp.status != HttpRespObject.STATUS_OK) {
                int status = imgResp == null ? -1 : imgResp.status;
                mLogger.e("upload images failed, status: %d", status);
                return imgResp;
            }
            return imgResp;
        }

        private void upload(int model)
        {
            String zipImagetDir = "";
            String zipVideoDir = "";
            if(model == Model.BUILD.value())
            {
                zipImagetDir = GlobalConfigure.mediaInsureItem.getZipImageDir();//storage/emulated/0/innovation/animal/投保/ZipImage
                zipVideoDir = GlobalConfigure.mediaInsureItem.getZipVideoDir();//storage/emulated/0/innovation/animal/投保/ZipVideo
            }
            else if(model == Model.VERIFY.value())
            {
                zipImagetDir = GlobalConfigure.mediaPayItem.getZipImageDir();//
//                /sdcard/Android/data/com.innovation.animal_cowface/cache/innovation/animal/理赔/ZipVideo/Time-2018_07_04_115209.zip
//                /storage/emulated/0/Android/data/com.innovation.animal_cowface/cache/innovation/animal/理赔/ZipVideo
                zipVideoDir = GlobalConfigure.mediaPayItem.getZipVideoDir();//storage/emulated/0/innovation/animal/投保/ZipVideo
            }

            File file_zipimage = new File(zipImagetDir);
            List<String> list_images = FileUtils.GetFiles(zipImagetDir, "zip", true);
            String tmpfilename = "";
            String namepre = "";
            File tmpfile = null;
            int lastindex = 0;
            if(list_images != null) {
                if (list_images.size() > 0)
                    Collections.sort(list_images);

                for (int i = 0; i < list_images.size(); i++) {
                    tmpfile = new File(list_images.get(i));
//                    tmpfilename = tmpfile.getName();//Time-2018_03_09_142403_image.zip
//                    lastindex = tmpfilename.lastIndexOf("_");
                    if(lastindex < 0) {
                        continue;
                    }
//                    namepre = tmpfilename.substring(0, lastindex);

                    //读猪的编号信息
                    String fname_num = "number.txt";
                    String content = null;
                    try {
                        content = FileUtils.getZipFileContent(tmpfile, fname_num);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(TextUtils.isEmpty(content)){
                        return;
                    }
                    SharedPreferences pref_user = mContext.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                    int userId = pref_user.getInt("uid", 0);

                    UploadObject imgResp = HttpUtils.uploadImages(mContext, model, tmpfile, userId, content);

                    if (imgResp != null && imgResp.status == HttpRespObject.STATUS_OK) {

                        FileUtils.deleteFile(tmpfile);
                        int lib_id = imgResp.libId;
//                        FileUtils2.chageFileName(zipVideoDir, namepre, lib_id);
                    }
                }
            }

            File file_zipvideo = new File(zipVideoDir);
            List<String> list_videos = FileUtils.GetFiles(zipVideoDir, "zip", true);
            String[] split;
            String tmpstr = "";
            String libstr = "";

            if(list_videos != null) {
                if (list_videos.size() > 0)
                    Collections.sort(list_videos);

                for (int i = 0; i < list_videos.size(); i++) {
                    tmpfile = new File(list_videos.get(i));
                    tmpfilename = tmpfile.getName();//Time-2018_03_09_142403_video.zip
                    split = tmpfilename.split("_");//Time-2018_03_09_142403_video_lib_.zip
                    if (split.length == 5) {

                        continue;
                    }
                    if (split.length > 5) {
                        libstr = split[5];
                        String fname_num = "number.txt";
                        String content = null;
                        try {
                            content = FileUtils.getZipFileContent(tmpfile, fname_num);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(TextUtils.isEmpty(content)){
                            continue;
                        }

                        SharedPreferences pref_user = mContext.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                        int userId = pref_user.getInt("uid", 0);

                        UploadObject imgResp = HttpUtils.uploadVideo(mContext, model, Integer.parseInt(libstr), tmpfile, userId, content);
                        if (imgResp != null && imgResp.status == HttpRespObject.STATUS_OK) {
                            FileUtils.deleteFile(tmpfile);
                        }
                    }
                }
            }
        }

    }

    public void upload() {
//        if (NetworkUtil.isWifi(mContext)) {
            mHandler.removeMessages(MSG_UPLOAD);
            mHandler.sendEmptyMessageDelayed(MSG_UPLOAD, 1000);
//        }
        mHandler.removeMessages(MSG_UPLOAD);
        mHandler.sendEmptyMessageDelayed(MSG_UPLOAD, 1000);
    }

    public void upload_all() {
//        if (NetworkUtil.isWifi(mContext)) {
            mHandler.removeMessages(MSG_UPLOAD_NEW);
            mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_NEW, 1000);
//        }
        mHandler.removeMessages(MSG_UPLOAD_NEW);
        mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_NEW, 1000);
    }
}
