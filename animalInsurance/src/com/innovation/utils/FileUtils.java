package com.innovation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.innovation.base.GlobalConfigure;
import com.innovation.biz.classifier.CowFaceBoxDetector;
import com.innovation.biz.classifier.DonkeyFaceBoxDetector;
import com.innovation.biz.classifier.PigFaceBoxDetector;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;

/**
 * @author wbs on 11/2/17.
 */

public class FileUtils {
    private final static String TAG = "FileUtils";

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
            boolean ok = file.delete();
            Log.d("FileUtils2", "file.delete()===" + ok);
        } else {
            file.delete();
        }
        return true;
    }

    public static void deleteFileAll(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
            file.delete();
        } else {
            file.delete();
        }
    }


    public static void deleteFile(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        deleteFile(new File(name));
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File file) {
        if (bitmap == null || file == null || file.getParentFile() == null) {
            return false;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Bitmap readBitmapFromFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //haojie add
    public static void saveInfoToTxtFile(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent + "\r\n");
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


    public static void saveInfoToCatchFile(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent + "\r\n");
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


    public static List<String> GetFiles(String Path, String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
    {
        List<String> lstFile = new ArrayList<String>();
        //结果 List
        File[] files = new File(Path).listFiles();
        if (files == null) {
            Log.d(TAG, "GetFiles return null ! file.path ==" + Path);
            return null;
        }

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))  //判断扩展名
                    lstFile.add(f.getPath());

                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
                GetFiles(f.getPath(), Extension, IsIterative);
        }
        return lstFile;
    }

    public static List<String> GetFilesAll(String Path, String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
    {
        List<String> lstFile = new ArrayList<String>();
        //结果 List
        File[] files = new File(Path).listFiles();
        if (files == null) {
            Log.d(TAG, "GetFiles return null ! file.path ==" + Path);
            return null;
        }

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))  //判断扩展名
                    lstFile.add(f.getPath());

                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
            {
                List<String> tmplstFile = new ArrayList<String>();
                tmplstFile = GetFilesAll(f.getPath(), Extension, IsIterative);
                lstFile.addAll(tmplstFile);
            }
        }
        return lstFile;
    }

    public static void chageFileName(String zipVideoDir, String srcpre, int lib_id) {
        File file_zipvideo = new File(zipVideoDir);
        //获得对应视频文件
        String fname_src = srcpre + "_" + GlobalConfigure.FILEPRE_VIDEO + ".zip";
        File zipFile_video_from = new File(zipVideoDir, fname_src);
        if (!zipFile_video_from.exists())//视频文件不存在
            return;
        //更改视频文件为带lib_id的
        String fname_des = srcpre + "_" + GlobalConfigure.FILEPRE_VIDEO + "_" + lib_id + "_" + ".zip";
        File zipFile_video_to = new File(zipVideoDir, fname_des);
        zipFile_video_from.renameTo(zipFile_video_to);   //重命名文件
    }

    /**
     * 读取zip文件中制定文件的内容
     *
     * @param zipFile      目标zip文件对象
     * @param readFileName 目标读取文件名字
     * @return 文件内容
     * @throws ZipException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static String getZipFileContent(File zipFile, String readFileName) throws ZipException, IOException {
        StringBuilder content = new StringBuilder();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

        ZipEntry ze;
        // 枚举zip文件内的文件/
        while (entries.hasMoreElements()) {
            ze = entries.nextElement();
            // 读取目标对象
            if (ze.getName().equals(readFileName)) {
                Scanner scanner = new Scanner(zip.getInputStream(ze));
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine());
                }
                scanner.close();
            }
        }
        zip.close();

        return content.toString();
    }

    //end add


    public static String saveFile(Context c, String fileName, Bitmap bitmap) {
        return saveFile(c, "", fileName, bitmap);
    }

    public static String saveFile(Context c, String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        return saveFile(c, filePath, fileName, bytes);
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static String saveFile(Context c, String filePath, String fileName, byte[] bytes) {
        String fileFullName = "";
        FileOutputStream fos = null;
        String dateFolder = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                .format(new Date());
        try {
            String suffix = "";
            if (filePath == null || filePath.trim().length() == 0) {
                filePath = Environment.getExternalStorageDirectory() + "/cowFace/" + dateFolder + "/";
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File fullFile = new File(filePath, fileName + suffix);
            fileFullName = fullFile.getPath();
            fos = new FileOutputStream(new File(filePath, fileName + suffix));
            fos.write(bytes);
        } catch (Exception e) {
            fileFullName = "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    fileFullName = "";
                }
            }
        }
        return fileFullName;
    }


    public static void writeStringToFile(String json, String filePath) {
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File jsonFile = new File(dir, "nos.json");
            byte[] bytes = json.getBytes();
            int b = json.length();
            FileOutputStream fos = null;
            fos = new FileOutputStream(jsonFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String readString(String fileName) {
        StringBuffer buffer = new StringBuffer();
        try {
            FileReader fr = new FileReader("demo.txt");
            /**
             * 用Reader中的read方法读取字符。
             */
            int ch = 0;
            while ((ch = fr.read()) != -1) {
                buffer.append(ch);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 移动文件到指定路径
     * @param oldPath
     * @param newPath
     */
    public   static   void  moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }

    /**
     * 复制文件
     * @param oldPath
     * @param newPath
     */
    public   static   void  copyFile(String oldPath, String newPath) {
        try  {
            int  bytesum =  0 ;
            int  byteread =  0 ;
            File oldfile =  new  File(oldPath);
            if  (oldfile.exists()) {  //文件存在时
//读入原文件
                InputStream inStream =  new  FileInputStream(oldPath);
                FileOutputStream fs =  new  FileOutputStream(newPath);
                byte [] buffer =  new   byte [ 1444 ];
                int  length;
                while  ( (byteread = inStream.read(buffer)) != - 1 ) {
                    bytesum += byteread;  //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer,  0 , byteread);
                }
                inStream.close();
            }
        }
        catch  (Exception e) {
            System.out.println( "复制单个文件操作出错" );
            e.printStackTrace();

        }
    }

    /**
     * 删除文件
     * @param filePathAndName
     */
    public static void delFile(String filePathAndName) {
        try  {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myDelFile =  new  File(filePath);
            myDelFile.delete();

        }
        catch  (Exception e) {
            System.out.println( "删除文件操作出错" );
            e.printStackTrace();

        }

    }

    /**
     * 获取路径下的所有指定类型文件
     * @param directoryPath 需要遍历的文件夹路径
     * @return
     */
    public static List<String> getAllFile(String directoryPath, String fileType) {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // folder skip
            } else {
                String fullPathName = file.getAbsolutePath();
                if(fileType == null || fileType.equals("") || fullPathName.endsWith(fileType)){
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }


    /**
     * 对文件md5加密
     *
     * @param path 文件路径
     * @return
     */
    public static String getMD5FromPath(String path) {
        File destFile = new File(path);
        try {
            String str = org.apache.commons.io.FileUtils.readFileToString(destFile, "UTF-8");
            // 生成一个MD5加密计算摘要
//            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
//            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return str.getBytes().length+"";
        } catch (Exception e) {
            return e.getMessage() + "  md5加密失败";
        }
    }


    public static void generateCostTimeTxt(String sBody) {
        String sFileName = null;
        try {
            File root = new File("/sdcard/Android/data/com.innovation.animal_model_test/cache/innovation/animal/投保/Current/视频/", "costTime");
            if (!root.exists()) {
                root.mkdirs();
            }
            if (ANIMAL_TYPE == 1){
                sFileName =  PigFaceBoxDetector.srcPigBitmapName + ".txt";
            }else if (ANIMAL_TYPE == 2){
                sFileName = CowFaceBoxDetector.srcCowBitmapName + ".txt";
            }else if (ANIMAL_TYPE == 3){
                sFileName = DonkeyFaceBoxDetector.srcDonkeyBitmapName + ".txt";
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
