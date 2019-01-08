package com.innovation.tuil2;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.spec.ECField;

/**
 * Created by kangjh on 2016/4/19.
 */
public class FileUtils {

    /**
     * 创建新的path
     * @param path 输出路径
     * @return 返回创建的file
     */
    public static File createPathIfNotExit(String path){
        File file=new File(path);
        if (!file.exists()){
            file.mkdir();
        }
        return file;
    }


    /**
     * create folder by file path
     *
     * @param filePath
     * @return
     */
    public static File createFolder(String filePath) {
        File file = null;
        if (TextUtils.isEmpty(filePath)) {
            return file;
        }
        file = new File(filePath);
        if (null != file && !file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 如果没file则创建有则返回已有file
     * @param filePath file的路径
     * @return file内容
     */
    public static File createFileIfNotExit(String filePath){
        File file=new File(filePath);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 创建新的文件
     * @param filePath 文件路径
     * @return 新的文件
     */
    public static File createNewFile(String filePath){
        File file=new File(filePath);
        try {
            if (!file.exists()){
               file.createNewFile();
            }else{
                file.delete();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 向sd卡中存储数据 （主要负责向 sd卡中存储String字符串）
     * @param file 文件
     * @param info 输入信息内容
     * @return 是否成功写入
     */
    public static boolean writeDatesToSDCard(File file,String info){
        boolean flag=true;
        try {
            BufferedWriter writer=new BufferedWriter(new FileWriter(file));
            writer.write(info);
            writer.write("\t\r\n");
            writer.flush();
        } catch (IOException e) {
            flag=false;
            e.printStackTrace();
        }
      return  flag;
    }

    /**
     * 向sd卡中存储数据 (以流的方式 主要负责网上传过来的apk 同步文件等)
     * @param file 文件
     * @param inputStream 输入流
     * @return 是否成功写入
     */
    public static void writeDatesToSDCard(File file,InputStream inputStream) throws IOException {
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(file);
            byte[] buf = new byte[4096];
            int ch = -1;
            while ((ch = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            try {
                if (fos != null) {fos.close();}
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *  读取file文本的内容
     * @param file 文本路径
     * @return
     */
    public static String readSDCardMsg(File file){
        StringBuffer sb=new StringBuffer();
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String buffer=null;
            while ((buffer=reader.readLine())!=null){
                sb.append(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  sb.toString();
    }
}
