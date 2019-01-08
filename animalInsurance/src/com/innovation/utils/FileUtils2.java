package com.innovation.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: syc
 * Date: 2015/9/6
 * Time: 10:06
 * Email: ycshi@isoftstone.com
 * Dest:  管理缓存
 */

public class FileUtils2 {
    private static final String ROOT_DIR = "InnoFarm";

    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 向sd卡中存储数据 (以流的方式 主要负责网上传过来的apk 同步文件等)
     *
     * @param file  文件
     * @param bytes 输入字节
     * @return 是否成功写入
     */
    public static boolean writeBytesToSDCard(File file, byte[] bytes) {
        boolean flag = true;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean createImagePathIfNotExit() {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath = sdPath + "/innoImages";
        File file = new File(sdPath);
        try {
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean imageFileExit(String path) {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sdPath = sdPath + "/innoImages/" + path;
        return new File(sdPath).exists();
    }



    //读取指定目录下的所有文件的文件名
    public static List<File> getFileName(File[] files) {
        List<File> fileList = new ArrayList<>();
        if (files != null) { // 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {//检查此路径名的文件是否是一个目录(文件夹)
                    getFileName(file.listFiles());//若是文件目录。继续读1
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(isSpace(filePath) ? null : new File(filePath));
    }

    private static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
