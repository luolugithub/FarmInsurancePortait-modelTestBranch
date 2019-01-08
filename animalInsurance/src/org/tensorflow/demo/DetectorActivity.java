/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.demo;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Toast;

import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.base.Model;
import com.innovation.biz.classifier.CowFaceBoxDetector;
import com.innovation.biz.classifier.CowRotationClassifier;
import com.innovation.biz.classifier.DonkeyFaceBoxDetector;
import com.innovation.biz.classifier.DonkeyRotationAndKeypointsClassifier;
import com.innovation.biz.classifier.PigFaceBoxDetector;
import com.innovation.biz.classifier.PigRotationClassifier;
import com.innovation.biz.iterm.AnimalClassifierResultIterm;
import com.innovation.biz.iterm.MediaInsureItem;
import com.innovation.biz.iterm.MediaPayItem;
import com.innovation.biz.iterm.PredictRotationIterm;
import com.innovation.biz.processor.InsureDataProcessor;
import com.innovation.biz.processor.PayDataProcessor;
import com.innovation.utils.BoxUtil;
import com.innovation.utils.FileUtils;
import com.innovation.utils.TensorFlowHelper;
import com.innovation.utils.Video2Bitmap;

import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static com.innovation.utils.ImageUtils.padBitmap;
import static com.innovation.utils.ImageUtils.rotateBitmap;


/**
 * @author luolu on 2018/6/17.
 */

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final String TAG = "DetectorActivity";
    private static final Logger LOGGER = new Logger();
    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 960);
    private static final float TEXT_SIZE_DIP = 10;

    private Integer sensorOrientation;
    private Classifier donkeyBoxDetector;
    private Classifier cowTFliteDetector;
    private Classifier pigTFliteDetector;

    public static int previewWidth = 0;
    public static int previewHeight = 0;
    private byte[][] yuvBytes;
    private int[] rgbBytes = null;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private boolean computing = false;
    private long timestamp = 0;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    public static MultiBoxTracker tracker;
    private byte[] luminance;
    private BorderedText borderedText;

    private static final int TFLITE_INPUT_SIZE = 192;
    private static final boolean TFLITE_IS_QUANTIZED = true;

    public static int type1Count = 0;
    public static int type2Count = 0;
    public static int type3Count = 0;
    public static int AngleTrackType = 0;
    public static int offsetX;
    public static int offsetY;
    public static String imageFileName;
    public static File root;
    public static File gpxfile;
    public static FileWriter writer;


    @Override
    public synchronized void onResume() {
        type1Count = 0;
        type2Count = 0;
        type3Count = 0;
        super.onResume();
        LOGGER.i("onResume,type1Count:", type1Count);
        LOGGER.i("onResume,type2Count:", type2Count);
        LOGGER.i("onResume,type3Count:", type3Count);
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        LOGGER.i("previewWidth: " + previewWidth);
        LOGGER.i("previewHeight: " + previewHeight);

        final Display display = getWindowManager().getDefaultDisplay();
        final int screenOrientation = display.getRotation();
        sensorOrientation = rotation - getScreenOrientation();

        LOGGER.i("screenOrientation: " + screenOrientation);
        LOGGER.i("sensorOrientation: " + sensorOrientation);

        rgbBytes = new int[previewWidth * previewHeight];
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        croppedBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

            frameToCropTransform = ImageUtils.getTransformationMatrix(
                    previewWidth, previewHeight, previewWidth, previewHeight, screenOrientation, true);


        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);


        yuvBytes = new byte[3][];

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });
    }

    public static OverlayView trackingOverlay;

    @Override
    public void onImageAvailable(final ImageReader reader) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_tracking_new;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onSetDebug(final boolean debug) {
    }

    public void reInitCurrentCounter(int a, int b, int c) {
        type1Count = a;
        type2Count = b;
        type3Count = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        previewHeight = 1280;
        previewWidth = 960;
        ANIMAL_TYPE = 2;
        try {
            root = new File("/sdcard/Android/data/com.innovation.animal_model_image/cache/innovation/animal/投保/Test/", "CattleTestDoc");
            if (!root.exists()) {
                root.mkdirs();
            }
            gpxfile = new File(root, "1123CattleTestDoc.txt");
            writer = new FileWriter(gpxfile);
            writer.write("1123CattleTest\r\n");

        } catch (IOException ioe) {
        }
        GlobalConfigure.VideoFileName = "/sdcard/mivideo/20181122023233675.mp4";
        tracker = new MultiBoxTracker(DetectorActivity.this);
        GlobalConfigure.model = 1;
        if (GlobalConfigure.mediaInsureItem == null) {
            GlobalConfigure.mediaInsureItem = new MediaInsureItem(DetectorActivity.this);
        }
        if (GlobalConfigure.mediaPayItem == null) {
            GlobalConfigure.mediaPayItem = new MediaPayItem(DetectorActivity.this);
        }
        // TODO: 2018/9/26 By:LuoLu  currentInit dir
        if (GlobalConfigure.model == Model.BUILD.value()) {
            InsureDataProcessor.getInstance(DetectorActivity.this).handleMediaResource_build(DetectorActivity.this);
            GlobalConfigure.mediaInsureItem.currentDel();
            GlobalConfigure.mediaInsureItem.currentInit();
        } else if (GlobalConfigure.model == Model.VERIFY.value()) {
            PayDataProcessor.getInstance(DetectorActivity.this).handleMediaResource_build(DetectorActivity.this);
            GlobalConfigure.mediaPayItem.currentDel();
            GlobalConfigure.mediaPayItem.currentInit();
        }
        try {
//            donkeyBoxDetector =
//                    DonkeyFaceBoxDetector.create(
//                            getAssets(),
//                            TensorFlowHelper.DONKEY_DETECT_MODEL_FILE,
//                            "",
//                            TFLITE_INPUT_SIZE,
//                            TFLITE_IS_QUANTIZED);

            cowTFliteDetector =
                    CowFaceBoxDetector.create(
                            getAssets(),
                            TensorFlowHelper.CATTLE_DETECT_MODEL_FILE,
                            "",
                            TFLITE_INPUT_SIZE,
                            TFLITE_IS_QUANTIZED);

        } catch (final Exception e) {
            throw new RuntimeException("modelTest Error initializing TensorFlow!", e);
        }

        modelTest();
//        video2BitmapTest();


    }

    private void modelTest() {
        Bitmap bitmap = null;
        Classifier.RecognitionAndPostureItem recognitionAndPostureItem = null;
        PredictRotationIterm predictRotationIterm = null;
        List<Point> points;
        int angletype = 0;
        String filePath = "/sdcard/image/val2017";

        File fileAll = new File(filePath);

        File[] files = fileAll.listFiles();
        for (int i = 0; i < files.length; i++) {
            LOGGER.i("第"+ i +"张图片");
            if (i == files.length - 1){
                LOGGER.i("图片读取完毕！");
                Toast.makeText(DetectorActivity.this,"Test Finish!!",Toast.LENGTH_SHORT).show();
            }
            File file = files[i];
            imageFileName = file.getName();
            LOGGER.i("imageName：" + file.getName());
            File f = new File(file.getPath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                Bitmap workingBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);
                Bitmap padBitmap = padBitmap(mutableBitmap);
//cattle
                cowTFliteDetector.cowFaceBoxDetector(padBitmap);
                if (CowFaceBoxDetector.cowRecognitionAndPostureItemTFlite != null) {
                    tracker.trackAnimalResults(CowFaceBoxDetector.cowRecognitionAndPostureItemTFlite.getPostureItem(),
                            CowRotationClassifier.cowPredictAngleType);

                }

//                donkey
//                donkeyBoxDetector.donkeyFaceBoxDetector(padBitmap);
//                if (DonkeyFaceBoxDetector.donkeyRecognitionAndPostureItemTFlite != null) {
//                    tracker.trackAnimalResults(DonkeyFaceBoxDetector.donkeyRecognitionAndPostureItemTFlite.getPostureItem(),
//                            DonkeyRotationAndKeypointsClassifier.donkeyPredictAngleType);
//                }

                } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void video2BitmapTest(){
        //video process
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long duration = Video2Bitmap.extractVideoInfo(retriever);
        LOGGER.i("duration %f:" + duration);
        for (int i = 0; i < duration; i += 200) {
            LOGGER.i("第"+ i +"张图片");
            // 1. extract video info
            Bitmap bitmapFVideo = Video2Bitmap.extractImage(retriever, i);
            if (bitmapFVideo == null) {
                continue;
            }

            // 2. classify img
            Bitmap mutableBitmap = bitmapFVideo.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap rotateBitmap = rotateBitmap(mutableBitmap, 90);
            Canvas canvas = new Canvas(rotateBitmap);
            Bitmap padBitmap = padBitmap(rotateBitmap);
//cattle
            cowTFliteDetector.cowFaceBoxDetector(padBitmap);
            if (CowFaceBoxDetector.cowRecognitionAndPostureItemTFlite != null) {
                tracker.trackAnimalResults(CowFaceBoxDetector.cowRecognitionAndPostureItemTFlite.getPostureItem(),
                        CowRotationClassifier.cowPredictAngleType);

            }

//                donkey
//            donkeyBoxDetector.donkeyFaceBoxDetector(padBitmap);
//            if (DonkeyFaceBoxDetector.donkeyRecognitionAndPostureItemTFlite != null) {
//                tracker.trackAnimalResults(DonkeyFaceBoxDetector.donkeyRecognitionAndPostureItemTFlite.getPostureItem(),
//                        DonkeyRotationAndKeypointsClassifier.donkeyPredictAngleType);
//            }

        }
        retriever.release();
                try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
