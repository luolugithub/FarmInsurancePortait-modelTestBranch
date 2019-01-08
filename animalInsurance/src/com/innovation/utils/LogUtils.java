package com.innovation.utils;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by haojie on 2018/5/17.
 */

public class LogUtils {
    private final static String TAG = "LogUtils";
    private final static String LogFileName = "HttpLog.txt";

    public static void writeLogFile() {



    }

    private static File getLogFile(Context context)
    {
        File dataDir =StorageUtils.getExternalLogDir(context);
        File file = null;
        //创建投保目录
        if (dataDir == null)
            return null;


        file = new File(StorageUtils.getExternalLogDir(context), LogFileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!file.exists())
            return null;

        return file;

    }
    //haojie add
    public static void saveInfoToTxtFile(Context context, String conent) {
        File file = getLogFile(context);
        String filename = file.getAbsolutePath();
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename, true)));
            out.write(conent+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
