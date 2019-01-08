package com.innovation.biz.classifier;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.SystemClock;
import android.os.Trace;
import android.support.constraint.solver.widgets.Rectangle;

import com.innovation.base.InnApplication;
import com.innovation.biz.iterm.AnimalClassifierResultIterm;
import com.innovation.biz.iterm.PostureItem;
import com.innovation.biz.iterm.PredictRotationIterm;
import com.innovation.utils.BoxUtil;
import com.innovation.utils.PointFloat;
import com.innovation.utils.TensorFlowHelper;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.innovation.utils.ImageUtils.padBitmap2SpRatio;
import static com.innovation.utils.ImageUtils.zoomImage;
import static org.tensorflow.demo.DetectorActivity.offsetX;
import static org.tensorflow.demo.DetectorActivity.offsetY;

/**
 * Author by luolu, Date on 2018/11/20.
 * COMPANY：InnovationAI
 */

public class CattleMultiBoxDetector implements Classifier {
    private static final Logger sLogger = new Logger(CattleMultiBoxDetector.class);
    public static final float MIN_CONFIDENCE = (float) 0.5;

    private boolean isModelQuantized;
    // Config values.
    private int inputSize;
    private int[] intValues;
    private float[][][] outputLocations;
    private float[][] outputScores;
    private float[] outputDetectNum;
    private float[][] outputClassifyResult;
    private ByteBuffer imgData;
    private Interpreter tfLite;
    private Classifier cowFaceRotationDetector;
    private Classifier cowFaceKeyPointsDetector;
    public static String srcCowBitmapName;
    public static RecognitionAndPostureItem cowRecognitionAndPostureItemTFlite;

    private CattleMultiBoxDetector() {
        try {
            cowFaceRotationDetector =
                    CowRotationClassifier.create(
                            InnApplication.getAppContext().getAssets(),
                            TensorFlowHelper.COW_TFLITE_PREDICTION_MODEL_FILE,
                            "",
                            192,
                            true);
            cowFaceKeyPointsDetector =
                    CowKeyPointsClassifier.create(
                            InnApplication.getAppContext().getAssets(),
                            TensorFlowHelper.COW_TFLITE_KEYPOINTS_MODEL_FILE,
                            "",
                            192,
                            true);

        } catch (final Exception e) {
            throw new RuntimeException("cowFaceRotationDetector or cowFaceKeyPointsDetector: Error initializing TensorFlow!", e);
        }
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     * @param inputSize     The size of image input
     * @param isQuantized   Boolean representing model is quantized or not
     */
    public static Classifier create(
            final AssetManager assetManager,
            final String modelFilename,
            final String labelFilename,
            final int inputSize,
            final boolean isQuantized)
            throws IOException {
        final CattleMultiBoxDetector d = new CattleMultiBoxDetector();
        d.inputSize = inputSize;
        try {
            d.tfLite = new Interpreter(TensorFlowHelper.loadModelFile(assetManager, modelFilename));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        d.isModelQuantized = isQuantized;
        // Pre-allocate buffers.
        int numBytesPerChannel;
        if (isQuantized) {
            numBytesPerChannel = 1; // Quantized
        } else {
            numBytesPerChannel = 4; // Floating point
        }
        d.imgData = ByteBuffer.allocateDirect(1 * d.inputSize * d.inputSize * 3 * numBytesPerChannel);
        d.imgData.order(ByteOrder.nativeOrder());
        d.intValues = new int[d.inputSize * d.inputSize];
        d.tfLite.setNumThreads(TensorFlowHelper.NUM_THREADS);
        d.outputLocations = new float[1][TensorFlowHelper.NUM_DETECTIONS][4];
        d.outputClassifyResult = new float[1][10];
        d.outputScores = new float[1][10];
        d.outputDetectNum = new float[1];
        return d;
    }

    @Override
    public List<PointFloat> recognizePointImage(Bitmap bitmap) {
        return null;
    }

    @Override
    public RecognitionAndPostureItem donkeyFaceBoxDetector(Bitmap bitmap) {
        return null;
    }

    @Override
    public PredictRotationIterm cowRotationAndKeypointsClassifier(Bitmap bitmap) {
        return null;
    }

    @Override
    public RecognitionAndPostureItem pigFaceBoxDetector(Bitmap bitmap) {
        return null;
    }

    @Override
    public PredictRotationIterm pigRotationAndKeypointsClassifier(Bitmap bitmap) {
        return null;
    }

    @Override
    public PredictRotationIterm donkeyRotationAndKeypointsClassifier(Bitmap bitmap) {
        return null;
    }

    @Override
    public void enableStatLogging(boolean debug) {
        //inferenceInterface.enableStatLogging(debug);
    }

    @Override
    public String getStatString() {
        return "tflite";
    }

    @Override
    public void close() {
        tfLite.close();
    }

    @Override
    public RecognitionAndPostureItem cowFaceBoxDetector(Bitmap bitmap) {
        cowRecognitionAndPostureItemTFlite = new RecognitionAndPostureItem();
        PostureItem posture = null;
        if (bitmap == null) {
            return null;
        }
        sLogger.i("bitmap height:" + bitmap.getHeight());
        sLogger.i("bitmap width:" + bitmap.getWidth());
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int padSize = height;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
//        srcCowBitmapName = sdf.format(new Date(System.currentTimeMillis())) + ".jpeg";
//        Image
        srcCowBitmapName = DetectorActivity.imageFileName;
//        video
//        srcCowBitmapName = Video2Bitmap.vBitmapName;
        com.innovation.utils.ImageUtils.saveBitmap(bitmap, "cowSRC", srcCowBitmapName);
        try {
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sLogger.i("padBitmap padSize %d:" + padSize);
        Bitmap resizeBitmap = TensorFlowHelper.resizeBitmap(bitmap, inputSize);
        imgData = TensorFlowHelper.convertBitmapToByteBuffer(resizeBitmap, intValues, imgData);

        Trace.beginSection("feed");

        outputLocations = new float[1][TensorFlowHelper.NUM_DETECTIONS][4];
        outputClassifyResult = new float[1][TensorFlowHelper.NUM_DETECTIONS];
        outputScores = new float[1][TensorFlowHelper.NUM_DETECTIONS];
        outputDetectNum = new float[1];

        sLogger.i("inputSize:" + inputSize);

        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, outputLocations);
        outputMap.put(1, outputClassifyResult);
        outputMap.put(2, outputScores);
        outputMap.put(3, outputDetectNum);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        final long startTime = SystemClock.uptimeMillis();


        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

        sLogger.i(srcCowBitmapName + "__Detect face tflite cost:" + (SystemClock.uptimeMillis() - startTime));
        try {
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__Detect face tflite cost:" + (SystemClock.uptimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Trace.endSection();

        float modelY0 = 0;
        float modelX0 = 0;
        float modelY1 = 0;
        float modelX1 = 0;
        RectF[] rectFS = new RectF[TensorFlowHelper.NUM_DETECTIONS];

        sLogger.i(srcCowBitmapName + "__outputScores0 %f:" + outputScores[0][0]);
        sLogger.i(srcCowBitmapName + "__OutClassifyResult0 %f:" + outputClassifyResult[0][0]);
        sLogger.i(srcCowBitmapName + "__OutPDetectNum %f:" + outputDetectNum[0]);
        sLogger.i(srcCowBitmapName + "__outputLocations[0][0][1] %f:" + outputLocations[0][0][1]);
        sLogger.i(srcCowBitmapName + "__outputLocations[0][0][0] %f:" + outputLocations[0][0][0]);
        sLogger.i(srcCowBitmapName + "__outputLocations[0][0][3] %f:" + outputLocations[0][0][3]);
        sLogger.i(srcCowBitmapName + "__outputLocations[0][0][2] %f:" + outputLocations[0][0][2]);
        try {
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__outputScores0 %f:" + outputScores[0][0]);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__OutClassifyResult0 %f:" + outputClassifyResult[0][0]);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__OutPDetectNum %f:" + outputDetectNum[0]);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__outputLocations[0][0][1] %f:" + outputLocations[0][0][1]);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__outputLocations[0][0][0] %f:" + outputLocations[0][0][0]);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__outputLocations[0][0][3] %f:" + outputLocations[0][0][3]);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__outputLocations[0][0][2] %f:" + outputLocations[0][0][2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //box filter
        if (outputDetectNum[0] <= 0 || outputDetectNum[0] > 10) {
            return cowRecognitionAndPostureItemTFlite;
        }
        com.innovation.utils.ImageUtils.saveBitmap(bitmap, "cowDetected", srcCowBitmapName);
        for (int i = 0; i < outputDetectNum[0]; i++) {
            //tracker
            final ArrayList<Recognition> recognitions = new ArrayList<>(1);
            final RectF detection =
                    new RectF(
                            outputLocations[0][i][1] * padSize - offsetY,
                            outputLocations[0][i][0] * padSize - offsetX,
                            outputLocations[0][i][3] * padSize - offsetY,
                            outputLocations[0][i][2] * padSize - offsetX);
            recognitions.add(
                    new Recognition(
                            "",
                            "cowLite",
                            outputScores[0][i],
                            detection, null));
            Trace.endSection();
            cowRecognitionAndPostureItemTFlite.setList(recognitions);


            if (outputScores[0][i] >= MIN_CONFIDENCE) {
                rectFS[i] = new RectF(outputLocations[0][i][1], outputLocations[0][i][0],
                        outputLocations[0][i][3], outputLocations[0][i][2]);
            }
        }

        sLogger.i(srcCowBitmapName + "outputScores0 %f:" + outputScores[0][0]);
        sLogger.i(srcCowBitmapName + "OutClassifyResult0 %f:" + outputClassifyResult[0][0]);
        sLogger.i(srcCowBitmapName + "OutPDetectNum %f:" + outputDetectNum[0]);

        //      result box
        RectF desiredBox = BoxUtil.getDesiredBox(rectFS);
        if (desiredBox.left == 0 && desiredBox.top == 0 && desiredBox.right == 0 && desiredBox.bottom == 0) {
            return cowRecognitionAndPostureItemTFlite;
        }

        modelY0 = desiredBox.left;
        modelX0 = desiredBox.top;
        modelY1 = desiredBox.right;
        modelX1 = desiredBox.bottom;

        sLogger.i(srcCowBitmapName + "modelY0 %f:" + modelY0);
        sLogger.i(srcCowBitmapName + "modelX0 %f:" + modelX0);
        sLogger.i(srcCowBitmapName + "modelY1 %f:" + modelY1);
        sLogger.i(srcCowBitmapName + "modelX1 %f:" + modelX1);
        try {
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__modelY0 %f:" + modelY0);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__modelX0 %f:" + modelX0);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__modelY1 %f:" + modelY1);
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__modelX1 %f:" + modelX1);
            DetectorActivity.writer.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Crop image
        Bitmap clipBitmap = null;
        clipBitmap = com.innovation.utils.ImageUtils.clipBitmap(bitmap, modelY0, modelX0, modelY1, modelX1, 1.1f);
        Bitmap padBitmap2SpRatio = padBitmap2SpRatio(clipBitmap, 1.0f);
        int widthZoom = 320, heightZoom = 320;
        Bitmap resizeClipBitmap = zoomImage(padBitmap2SpRatio, widthZoom, heightZoom);

//rotation predict
        PredictRotationIterm predictRotationIterm = null;
        List<PointFloat> keypointResults = new ArrayList<>();
        predictRotationIterm = cowFaceRotationDetector.cowRotationAndKeypointsClassifier(clipBitmap);
        if (predictRotationIterm != null) {
            //keypoint detect
            keypointResults = cowFaceKeyPointsDetector.recognizePointImage(clipBitmap);
        }

        com.innovation.utils.ImageUtils.saveBitmap(clipBitmap, "cowCrop", srcCowBitmapName);
// draw rect
//                Canvas canvasDrawRecognition = new Canvas(bitmap);
//                com.innovation.utils.ImageUtils.drawRecognitions(canvasDrawRecognition,
//                        modelY0, modelX0, modelY1, modelX1);
//                saveDrawRectBitmap(bitmap);


        if (predictRotationIterm == null || keypointResults == null) {
            return cowRecognitionAndPostureItemTFlite;
        }
        posture = new PostureItem(
                (float) predictRotationIterm.rot_x,
                (float) predictRotationIterm.rot_y,
                (float) predictRotationIterm.rot_z,
                modelX0, modelY0, modelX1, modelY1, outputScores[0][0],
                modelY0 * padSize, modelX0 * padSize,
                modelY1 * padSize, modelX1 * padSize, resizeClipBitmap, null);
        cowRecognitionAndPostureItemTFlite.setPostureItem(posture);
        AnimalClassifierResultIterm.cowAngleCalculateTFlite(cowRecognitionAndPostureItemTFlite.getPostureItem());

        return cowRecognitionAndPostureItemTFlite;
    }

}
