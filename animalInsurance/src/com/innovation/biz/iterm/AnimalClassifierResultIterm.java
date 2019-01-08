package com.innovation.biz.iterm;

import android.util.Log;

import com.innovation.base.GlobalConfigure;
import com.innovation.base.InnApplication;
import com.innovation.base.Model;
import com.innovation.biz.classifier.CowKeyPointsClassifier;
import com.innovation.biz.classifier.CowRotationClassifier;
import com.innovation.biz.classifier.DonkeyRotationAndKeypointsClassifier;
import com.innovation.biz.classifier.PigKeyPointsClassifier;
import com.innovation.biz.classifier.PigRotationClassifier;
import com.innovation.utils.FileUtils;
import com.innovation.utils.PreferencesUtils;
import com.innovation.utils.Rot2AngleType;

import org.tensorflow.demo.CameraConnectionFragment;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.Logger;

import java.io.File;

/**
 * Created by Luolu on 2018/10/30.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class AnimalClassifierResultIterm {
    private static final Logger LOGGER = new Logger(AnimalClassifierResultIterm.class.getName());
    public static int donkeyAngleCalculateTFlite(PostureItem postureItem) {
        int type;
        int maxLeft = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_LEFT, InnApplication.getAppContext());
        int maxMiddle = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_MIDDLE, InnApplication.getAppContext());
        int maxRight = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_RIGHT, InnApplication.getAppContext());
        DetectorActivity.AngleTrackType = 10;
        DonkeyFaceKeyPointsItem donkeyFaceKeyPointsItem = DonkeyFaceKeyPointsItem.getInstance();
        type = Rot2AngleType.getDonkeyAngleType((float) postureItem.rot_x, (float) postureItem.rot_y);
        String imagefilename = "";
        String imageSrcFileName = "";
        String txtfilename = "";
        if (GlobalConfigure.model == Model.BUILD.value()) {
            imagefilename = GlobalConfigure.mediaInsureItem.getBitmapFileName(type);
            imageSrcFileName = GlobalConfigure.mediaInsureItem.getSrcBitmapFileName(type);
            txtfilename = GlobalConfigure.mediaInsureItem.getTxtFileNme(type);
        }
        if (GlobalConfigure.model == Model.VERIFY.value()) {
            imagefilename = GlobalConfigure.mediaPayItem.getBitmapFileName(type);
            txtfilename = GlobalConfigure.mediaPayItem.getTxtFileNme(type);
        }
        //save model outputs
        String contenType = imagefilename.substring(imagefilename.lastIndexOf("/") + 1);
        contenType += ":";
        contenType += "rot_x = " + postureItem.rot_x + "; ";
        contenType += "rot_y = " + postureItem.rot_y + "; ";
        contenType += "rot_z = " + postureItem.rot_z + "; ";
        contenType += "box_x0 = " + postureItem.modelX0 + "; ";
        contenType += "box_y0 = " + postureItem.modelY0 + "; ";
        contenType += "box_x1 = " + postureItem.modelX1 + "; ";
        contenType += "box_y1 = " + postureItem.modelY1 + "; ";
        contenType += "score = " + postureItem.modelDetectedScore + "; ";
        contenType += "point0 = " + donkeyFaceKeyPointsItem.getPointFloat0().toString() + "; ";
        contenType += "point1 = " + donkeyFaceKeyPointsItem.getPointFloat1().toString() + "; ";
        contenType += "point2 = " + donkeyFaceKeyPointsItem.getPointFloat2().toString() + "; ";
        contenType += "point3 = " + donkeyFaceKeyPointsItem.getPointFloat3().toString() + "; ";
        contenType += "point4 = " + donkeyFaceKeyPointsItem.getPointFloat4().toString() + "; ";
        contenType += "point5 = " + donkeyFaceKeyPointsItem.getPointFloat5().toString() + "; ";
        contenType += "point6 = " + donkeyFaceKeyPointsItem.getPointFloat6().toString() + "; ";
        contenType += "point7 = " + donkeyFaceKeyPointsItem.getPointFloat7().toString() + "; ";
        contenType += "point8 = " + donkeyFaceKeyPointsItem.getPointFloat8().toString() + "; ";
        contenType += "point9 = " + donkeyFaceKeyPointsItem.getPointFloat9().toString() + "; ";
        contenType += "point10 = " + donkeyFaceKeyPointsItem.getPointFloat10().toString() + "; ";

        LOGGER.i("donkeyFaceKeyPointsItem:" + donkeyFaceKeyPointsItem.toString());
        // TODO: 2018/8/13 By:LuoLu  图片数量达上限，不保存
        if (DetectorActivity.type1Count < maxLeft && DonkeyRotationAndKeypointsClassifier.donkeyRotationAndKeypointsK1 == true) {
            DetectorActivity.AngleTrackType = 1;
            DetectorActivity.type1Count++;
            LOGGER.i("type1数量:" + DetectorActivity.type1Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
            //File tmpImageSrcFileName = new File(imageSrcFileName);
            //save clip scale bitmap
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);
            //保存src图片
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else if (DetectorActivity.type2Count < maxMiddle && DonkeyRotationAndKeypointsClassifier.donkeyRotationAndKeypointsK2 == true) {
            DetectorActivity.AngleTrackType = 2;
            DetectorActivity.type2Count++;
            LOGGER.i("type2数量:" + DetectorActivity.type2Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
//            File tmpImageSrcFileName = new File(imageSrcFileName);
            //save clip scale bitmap
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else if (DetectorActivity.type3Count < maxRight && DonkeyRotationAndKeypointsClassifier.donkeyRotationAndKeypointsK3 == true) {
            DetectorActivity.AngleTrackType = 3;
            DetectorActivity.type3Count++;
            LOGGER.i("type3数量:" + DetectorActivity.type3Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
//            File tmpImageSrcFileName = new File(imageSrcFileName);
            //save clip scale bitmap
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else {
//            save for Test

        }
//        if (DetectorActivity.type1Count >= maxLeft && DetectorActivity.type2Count >= maxMiddle && DetectorActivity.type3Count >= maxRight) {
//            Log.i("DetectorTypeCountSum:", String.valueOf(DetectorActivity.type3Count + DetectorActivity.type2Count + DetectorActivity.type1Count));
//            GlobalConfigure.numberOk = true;
//            CameraConnectionFragment.collectNumberHandler.sendEmptyMessage(1);
//            Log.i("GlobalConfigure:", String.valueOf(GlobalConfigure.numberOk));
//        }
        return type;
    }

    public static int pigAngleCalculateTFlite(PostureItem postureItem) {
        int type;
        int maxLeft = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_LEFT, InnApplication.getAppContext());
        int maxMiddle = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_MIDDLE, InnApplication.getAppContext());
        int maxRight = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_RIGHT, InnApplication.getAppContext());
        DetectorActivity.AngleTrackType = 10;
        PigFaceKeyPointsItem pigFaceKeyPointsItem = PigFaceKeyPointsItem.getInstance();
        type = Rot2AngleType.getPigAngleType((float) postureItem.rot_x, (float) postureItem.rot_y);
        String imagefilename = "";
        String imageSrcFileName = "";
        String txtfilename = "";
        if (GlobalConfigure.model == Model.BUILD.value()) {
            imagefilename = GlobalConfigure.mediaInsureItem.getBitmapFileName(type);//storage/emulated/0/Android/data/com.innovation.animial/cache/innovation/animal/投保/Current/图片/Angle-01/20180423093314.jpg
            imageSrcFileName = GlobalConfigure.mediaInsureItem.getSrcBitmapFileName(type);
            txtfilename = GlobalConfigure.mediaInsureItem.getTxtFileNme(type);
        }
        if (GlobalConfigure.model == Model.VERIFY.value()) {
            imagefilename = GlobalConfigure.mediaPayItem.getBitmapFileName(type);
            txtfilename = GlobalConfigure.mediaPayItem.getTxtFileNme(type);
        }
        //保存角度信息
        String contenType = imagefilename.substring(imagefilename.lastIndexOf("/") + 1);
        contenType += ":";
        contenType += "rot_x = " + postureItem.rot_x + "; ";
        contenType += "rot_y = " + postureItem.rot_y + "; ";
        contenType += "rot_z = " + postureItem.rot_z + "; ";
        contenType += "box_x0 = " + postureItem.modelX0 + "; ";
        contenType += "box_y0 = " + postureItem.modelY0 + "; ";
        contenType += "box_x1 = " + postureItem.modelX1 + "; ";
        contenType += "box_y1 = " + postureItem.modelY1 + "; ";
        contenType += "score = " + postureItem.modelDetectedScore + "; ";
        contenType += "point0 = " + pigFaceKeyPointsItem.getPointFloat0().toString() + "; ";
        contenType += "point1 = " + pigFaceKeyPointsItem.getPointFloat1().toString() + "; ";
        contenType += "point2 = " + pigFaceKeyPointsItem.getPointFloat2().toString() + "; ";
        contenType += "point3 = " + pigFaceKeyPointsItem.getPointFloat3().toString() + "; ";
        contenType += "point4 = " + pigFaceKeyPointsItem.getPointFloat4().toString() + "; ";
        contenType += "point5 = " + pigFaceKeyPointsItem.getPointFloat5().toString() + "; ";
        contenType += "point6 = " + pigFaceKeyPointsItem.getPointFloat6().toString() + "; ";
        contenType += "point7 = " + pigFaceKeyPointsItem.getPointFloat7().toString() + "; ";
        contenType += "point8 = " + pigFaceKeyPointsItem.getPointFloat8().toString() + "; ";
        contenType += "point9 = " + pigFaceKeyPointsItem.getPointFloat9().toString() + "; ";
        contenType += "point10 = " + pigFaceKeyPointsItem.getPointFloat10().toString() + "; ";
        LOGGER.i("pigFaceKeyPointsItem:" + pigFaceKeyPointsItem.toString());


        if (DetectorActivity.type1Count < maxLeft && PigRotationClassifier.pigPredictAngleType == 1
                && PigKeyPointsClassifier.pigKeypointsK1 == true) {
            DetectorActivity.AngleTrackType = 1;
            DetectorActivity.type1Count++;
            Log.d("angleCalculateTFlite角度：", type + "");
            LOGGER.i("type1数量:" + DetectorActivity.type1Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
            File tmpImageSrcFileName = new File(imageSrcFileName);
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);//保存放大后的图片
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);//保存src图片
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else if (DetectorActivity.type2Count < maxMiddle && PigRotationClassifier.pigPredictAngleType == 2
                && PigKeyPointsClassifier.pigKeypointsK2 == true) {
            DetectorActivity.AngleTrackType = 2;
            DetectorActivity.type2Count++;
            LOGGER.i("type2数量:" + DetectorActivity.type2Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
            File tmpImageSrcFileName = new File(imageSrcFileName);
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);//保存放大后的图片
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);//保存src图片
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else if (DetectorActivity.type3Count < maxRight && PigRotationClassifier.pigPredictAngleType == 3
                && PigKeyPointsClassifier.pigKeypointsK3 == true) {
            DetectorActivity.AngleTrackType = 3;
            DetectorActivity.type3Count++;
            LOGGER.i("type3数量:" + DetectorActivity.type3Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
            File tmpImageSrcFileName = new File(imageSrcFileName);
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);//保存放大后的图片
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);//保存src图片
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else {
//            save for test

        }
//        if (DetectorActivity.type1Count >= maxLeft && DetectorActivity.type2Count >= maxMiddle && DetectorActivity.type3Count >= maxRight) {
//            Log.i("DetectorTypeCountSum:", String.valueOf(DetectorActivity.type3Count + DetectorActivity.type2Count + DetectorActivity.type1Count));
//            GlobalConfigure.numberOk = true;
//            CameraConnectionFragment.collectNumberHandler.sendEmptyMessage(1);
//            Log.i("GlobalConfigure:", String.valueOf(GlobalConfigure.numberOk));
//        }
        return type;
    }

    public static int cowAngleCalculateTFlite(PostureItem postureItem) {
        int type;
        int maxLeft = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_LEFT, InnApplication.getAppContext());
        int maxMiddle = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_MIDDLE, InnApplication.getAppContext());
        int maxRight = PreferencesUtils.getMaxPics(PreferencesUtils.FACE_ANGLE_MAX_RIGHT, InnApplication.getAppContext());
        DetectorActivity.AngleTrackType = 10;
        CowFaceKeyPointsItem cowFaceKeyPointsItem = CowFaceKeyPointsItem.getInstance();
        type = Rot2AngleType.getCowAngleType((float) postureItem.rot_x, (float) postureItem.rot_y);
        String imagefilename = "";
        String imageSrcFileName = "";
        String txtfilename = "";
        if (GlobalConfigure.model == Model.BUILD.value()) {
            imagefilename = GlobalConfigure.mediaInsureItem.getBitmapFileName(type);
            imageSrcFileName = GlobalConfigure.mediaInsureItem.getSrcBitmapFileName(type);
            txtfilename = GlobalConfigure.mediaInsureItem.getTxtFileNme(type);
        }
        if (GlobalConfigure.model == Model.VERIFY.value()) {
            imagefilename = GlobalConfigure.mediaPayItem.getBitmapFileName(type);
            txtfilename = GlobalConfigure.mediaPayItem.getTxtFileNme(type);
        }
        //save model outputs
        String contenType = imagefilename.substring(imagefilename.lastIndexOf("/") + 1);
        contenType += ":";
        contenType += "rot_x = " + postureItem.rot_x + "; ";
        contenType += "rot_y = " + postureItem.rot_y + "; ";
        contenType += "rot_z = " + postureItem.rot_z + "; ";
        contenType += "box_x0 = " + postureItem.modelX0 + "; ";
        contenType += "box_y0 = " + postureItem.modelY0 + "; ";
        contenType += "box_x1 = " + postureItem.modelX1 + "; ";
        contenType += "box_y1 = " + postureItem.modelY1 + "; ";
        contenType += "score = " + postureItem.modelDetectedScore + "; ";
        contenType += "point0 = " + cowFaceKeyPointsItem.getPointFloat0().toString() + "; ";
        contenType += "point1 = " + cowFaceKeyPointsItem.getPointFloat1().toString() + "; ";
        contenType += "point2 = " + cowFaceKeyPointsItem.getPointFloat2().toString() + "; ";
        contenType += "point3 = " + cowFaceKeyPointsItem.getPointFloat3().toString() + "; ";
        contenType += "point4 = " + cowFaceKeyPointsItem.getPointFloat4().toString() + "; ";
        contenType += "point5 = " + cowFaceKeyPointsItem.getPointFloat5().toString() + "; ";
        contenType += "point6 = " + cowFaceKeyPointsItem.getPointFloat6().toString() + "; ";
        contenType += "point7 = " + cowFaceKeyPointsItem.getPointFloat7().toString() + "; ";
        contenType += "point8 = " + cowFaceKeyPointsItem.getPointFloat8().toString() + "; ";
        contenType += "point9 = " + cowFaceKeyPointsItem.getPointFloat9().toString() + "; ";
        contenType += "point10 = " + cowFaceKeyPointsItem.getPointFloat10().toString() + "; ";
        contenType += "point11 = " + cowFaceKeyPointsItem.getPointFloat11().toString() + "; ";
        contenType += "point12 = " + cowFaceKeyPointsItem.getPointFloat12().toString() + "; ";

        LOGGER.i("cowFaceKeyPointsItem:" + cowFaceKeyPointsItem.toString());

        if (DetectorActivity.type1Count < maxLeft && CowRotationClassifier.cowPredictAngleType == 1
                && CowKeyPointsClassifier.cowKeypointsDetectedK1 == true) {

                DetectorActivity.AngleTrackType = 1;
                DetectorActivity.type1Count++;
                LOGGER.i("type1数量:" + DetectorActivity.type1Count);
                FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
                //保存图片
                File tmpimagefile = new File(imagefilename);
                File tmpImageSrcFileName = new File(imageSrcFileName);
                FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);//保存放大后的clip图片
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);//保存src图片

                DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                        DetectorActivity.type2Count, DetectorActivity.type3Count);

        }else if ( DetectorActivity.type2Count < maxMiddle && CowRotationClassifier.cowPredictAngleType == 2
                && CowKeyPointsClassifier.cowKeypointsDetectedK2 == true){
            DetectorActivity.AngleTrackType = 2;
            DetectorActivity.type2Count++;
            LOGGER.i("type2数量:" + DetectorActivity.type2Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
            File tmpImageSrcFileName = new File(imageSrcFileName);
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);//保存src图片
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);//保存放大后的图片
            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        } else if (DetectorActivity.type3Count < maxRight && CowRotationClassifier.cowPredictAngleType == 3
                && CowKeyPointsClassifier.cowKeypointsDetectedK3 == true) {
            DetectorActivity.AngleTrackType = 3;
            DetectorActivity.type3Count++;
            LOGGER.i("type3数量:" + DetectorActivity.type3Count);
            FileUtils.saveInfoToTxtFile(txtfilename, contenType + "angle:" + type);
            //保存图片
            File tmpimagefile = new File(imagefilename);
            File tmpImageSrcFileName = new File(imageSrcFileName);
            FileUtils.saveBitmapToFile(postureItem.clipBitmap, tmpimagefile);//保存放大后的图片
//            FileUtils.saveBitmapToFile(postureItem.srcBitmap, tmpImageSrcFileName);//保存src图片

            DetectorActivity.tracker.getCountOfCurrentImage(DetectorActivity.type1Count,
                    DetectorActivity.type2Count, DetectorActivity.type3Count);
        }else {
            LOGGER.i("other type" );
        }

//        if (DetectorActivity.type1Count >= maxLeft && DetectorActivity.type2Count >= maxMiddle && DetectorActivity.type3Count >= maxRight) {
//            Log.i("DetectorTypeCountSum:", String.valueOf(DetectorActivity.type3Count + DetectorActivity.type2Count + DetectorActivity.type1Count));
//            GlobalConfigure.numberOk = true;
//            CameraConnectionFragment.collectNumberHandler.sendEmptyMessage(1);
//            Log.i("GlobalConfigure:", String.valueOf(GlobalConfigure.numberOk));
//        }
        return type;
    }
}
