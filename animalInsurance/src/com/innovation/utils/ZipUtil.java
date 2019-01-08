package com.innovation.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.tensorflow.demo.env.Logger;

/**
 * @author wbs on 11/22/17.
 */

public class ZipUtil {
    private static final Logger sLogger = new Logger("zip");

    /**
     * 取得压缩包中的 文件列表(文件夹,文件自选)
     *
     * @param zipFileString  压缩包名字
     * @param bContainFolder 是否包括 文件夹
     * @param bContainFile   是否包括 文件
     * @return
     * @throws Exception
     */
    public static List<File> GetFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws Exception {

        sLogger.v("GetFileList(String)");

        List<File> fileList = new java.util.ArrayList<File>();
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }

            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }//end of while

        inZip.close();

        return fileList;
    }

    /**
     * 返回压缩包中的文件InputStream
     *
     * @param zipFileString 压缩文件的名字
     * @param fileString    解压文件的名字
     * @return InputStream
     * @throws Exception
     */
    public static InputStream UpZip(String zipFileString, String fileString) throws Exception {
        sLogger.v("XZip", "UpZip(String, String)");
        ZipFile zipFile = new ZipFile(zipFileString);
        ZipEntry zipEntry = zipFile.getEntry(fileString);

        return zipFile.getInputStream(zipEntry);

    }


    /**
     * 解压一个压缩文档 到指定位置
     *
     * @param zipFileString 压缩包的名字
     * @param outPathString 指定的路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        sLogger.v("XZip", "UnZipFolder(String, String)");
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();

            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();

    }

    /***
     * 压缩多个文件
     * @param files 要压缩的文件/目录数组
     * @param zipFile 输出的压缩文件
     */
    public static boolean zipFiles(File[] files, File zipFile) {
        ZipOutputStream outZip = null;
        boolean result = true;
        try {
            outZip = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File file : files) {

                long starttime = System.currentTimeMillis();
                Log.d("zipFiles", "zipFiles---ZipUtil");

                zipFile(file.getParent() + File.separator, file.getName(), outZip);

                long endtime = System.currentTimeMillis();
                long duringtime = endtime - starttime;
                Log.d("zipFiles", "ZipUtil--- end!!!! file.getName()====="+ file.getName()+"====time===="+duringtime);
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (outZip != null) {
                try {
                    outZip.finish();
                    outZip.close();
                } catch (Exception e) {
                    result = false;
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 压缩文件,文件夹
     *
     * @param fileName    要压缩的文件/文件夹名字
     * @param zipFileName 指定压缩的目的和名字
     */
    public static boolean zipFile(String fileName, String zipFileName) {
        sLogger.v("%s to %s", fileName, zipFileName);
        boolean zipStatus = false;
        ZipOutputStream outZip = null;
        try {
            outZip = new ZipOutputStream(new FileOutputStream(zipFileName));
            outZip.setLevel(Deflater.DEFAULT_COMPRESSION);
            File file = new File(fileName);
            zipFile(file.getParent() + File.separator, file.getName(), outZip);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outZip != null) {
                    outZip.finish();
                    outZip.close();
                    zipStatus = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                zipStatus = false;
            }
        }
        return zipStatus;
    }

    /**
     * 压缩文件
     *
     * @param folderName
     * @param fileName
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void zipFile(String folderName, String fileName, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;

        File file = new File(folderName + fileName);

        //判断是不是文件
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileName + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                zipFile(folderName, fileName + File.separator + fileList[i], zipOutputSteam);
            }
        }
    }


}
