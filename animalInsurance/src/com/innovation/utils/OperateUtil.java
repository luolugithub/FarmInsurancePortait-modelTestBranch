package com.innovation.utils;

import android.util.Log;

import com.innovation.base.GlobalConfigure;
import com.innovation.base.Model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Created by luolu on 2018/8/7.
 */

public class OperateUtil {

    //判断图片目录下是否已经存在图片文件
    public static Map<String, String> imageList(int model)
    {
        Map<String, String> imageMap = new TreeMap<String, String>();
        boolean ifhave = false;
        //获取图片文件
        String imageDri = "";
        if(GlobalConfigure.model == Model.BUILD.value()) {
            imageDri = GlobalConfigure.mediaInsureItem.getImageDir();///storage/emulated/0/innovation/animal/投保/Current/图片
        } else if(GlobalConfigure.model == Model.VERIFY.value()) {
            imageDri = GlobalConfigure.mediaPayItem.getImageDir();///storage/emulated/0/innovation/animal/理赔/Current/图片
        }

        File imageDir_new = new File(imageDri);//图片目录下的文件
        File[] files_image = imageDir_new.listFiles();
        if (!imageDir_new.exists() || files_image.length == 0) {
            return imageMap;
        }

        File tmpFile;
        int count = 0;
        String findangel = "Angle-0";
        String angle = "";
        int index ;
        for (int i = 0; i < files_image.length; i++) {
            tmpFile = files_image[i];//tmpFile===/storage/emulated/0/innovation/animal/Current/图片/1
            String abspath = tmpFile.getAbsolutePath();
            count = testFileHave(abspath, GlobalConfigure.IMAGE_JPEG);
            if(count > 0){
                //获得角度值
                index = abspath.indexOf(findangel);
                if (index < 0) {
                    continue;
                } else {
                    int start = index + findangel.length();
                    angle = abspath.substring(start, start + 1);
                    if (angle.indexOf("0") == 0) {
                        angle = angle.substring(1);
                    }
                    imageMap.put(angle, count+"");
                }
            }
        }
        return imageMap;
    }


    //判断指定目录下是否已经存在指定类型的文件
    private static int testFileHave(String filePath, String fileSuffix) {
        int count = 0;
        File file_parent = new File(filePath);
        List<String> list_all = FileUtils.GetFilesAll(filePath, fileSuffix, true);
        if(list_all == null)
            return count;
        if (!file_parent.exists() || list_all.size() == 0) {
            return count;
        }
        count = list_all.size();
        return count;
    }

    public static String getReminderMsgText(Map<String, String> map) {
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        boolean b = false;
        Log.i("ImageMap", map.toString());
        if(map.containsKey("1")) {
            count1 = Integer.parseInt(map.get("1"));
        }
        if(map.containsKey("2")) {
            count2 = Integer.parseInt(map.get("2"));
        }
        if(map.containsKey("3"))  {
            count3 = Integer.parseInt(map.get("3"));
        }
        if(count1 < 3 && count2 < 7 && count3 < 3){
            return "请将脸放入框中";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("请将");
            if(count1 < 3) {
                sb.append("左");
                b = true;
            }
            if(count2 < 7) {
                if(b)   sb.append("/");
                sb.append("正");
                b = true;
            }
            if(count3 < 3) {
                if(b)   sb.append("/");
                sb.append("右");
            }
            sb.append("脸放入框中");
            return sb.toString();
        }
    }

    public static Vector getImageList(int model) {
        Map<String, String> map = imageList(model);
        Vector listAngles_capture = new Vector();
        //GlobalConfigure.listAngles_capture.clear();
        //查找已经采集的角度
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        Log.i("ImageMap", map.toString());
        if(map.containsKey("1")) {
            count1 = Integer.parseInt(map.get("1"));
        }
        if(map.containsKey("2")) {
            count2 = Integer.parseInt(map.get("2"));
        }
        if(map.containsKey("3")) {
            count3 = Integer.parseInt(map.get("3"));
        }
        // 左脸
        if(count1 < 3) {
            listAngles_capture.add("左脸，数量：" + count1 + "(不足)");
        } else if (count1 >= 7){
            listAngles_capture.add("左脸，数量：" + count1 + "(上限)");
        } else {
            listAngles_capture.add("左脸，数量：" + count1 + "(OK)");
        }

        // 正脸
        if(count2 < 7) {
            listAngles_capture.add("正脸，数量：" + count2 + "(不足)");
        } else if (count2 >= 15){
            listAngles_capture.add("正脸，数量：" + count2 + "(上限)");
        } else {
            listAngles_capture.add("正脸，数量：" + count2 + "(OK)");
        }

        // 右脸
        if(count3 < 3) {
            listAngles_capture.add("右脸，数量：" + count3 + "(不足)");
        } else if (count3 >= 7){
            listAngles_capture.add("右脸，数量：" + count3 + "(上限)");
        } else {
            listAngles_capture.add("右脸，数量：" + count3 + "(OK)");
        }


// TODO: 2018/8/13 By:LuoLu
        if (count1 > 2 && count3 > 2 && count2 > 6){
            Log.i("OperateUtil + count123:", String.valueOf(count3 + count2 + count1));

            GlobalConfigure.numberOk = true;
//            CameraConnectionFragment.collectNumberHandler.sendEmptyMessage(1);
            Log.i("GlobalConfigure.numberOk:", String.valueOf(GlobalConfigure.numberOk));
        }

        listAngles_capture.add(0, "已采集角度：");
        return listAngles_capture;

    }

    public static Vector getImageList(Map<String, String> map) {
        Vector listAngles_capture = new Vector();
        //GlobalConfigure.listAngles_capture.clear();
        //查找已经采集的角度
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        Log.i("ImageMap", map.toString());
        if(map.containsKey("1"))    count1 = Integer.parseInt(map.get("1"));
        if(map.containsKey("2"))    count2 = Integer.parseInt(map.get("2"));
        if(map.containsKey("3"))    count3 = Integer.parseInt(map.get("3"));

        // 左脸
        if(count1 < 3) {
            listAngles_capture.add("左脸，数量：" + count1 + "(不足)");
        } else if (count1 >= 7){
            listAngles_capture.add("左脸，数量：" + count1 + "(上限)");
        } else {
            listAngles_capture.add("左脸，数量：" + count1 + "(OK)");
        }

        // 正脸
        if(count2 < 7) {
            listAngles_capture.add("正脸，数量：" + count2 + "(不足)");
        } else if (count2 >= 15){
            listAngles_capture.add("正脸，数量：" + count2 + "(上限)");
        } else {
            listAngles_capture.add("正脸，数量：" + count2 + "(OK)");
        }

        // 右脸
        if(count3 < 3) {
            listAngles_capture.add("右脸，数量：" + count3 + "(不足)");
        } else if (count3 >= 7){
            listAngles_capture.add("右脸，数量：" + count3 + "(上限)");
        } else {
            listAngles_capture.add("右脸，数量：" + count3 + "(OK)");
        }


// TODO: 2018/8/13 By:LuoLu
        if (count1 > 2 && count3 > 2 && count2 > 6){
            Log.i("OperateUtil + count123:", String.valueOf(count3 + count2 + count1));

            GlobalConfigure.numberOk = true;
//            CameraConnectionFragment.collectNumberHandler.sendEmptyMessage(1);
            Log.i("GlobalConfigure.numberOk:", String.valueOf(GlobalConfigure.numberOk));
        }

        listAngles_capture.add(0, "已采集角度：");
        return listAngles_capture;

    }
}
