package com.innovation.biz.classifier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Trace;

import com.innovation.base.InnApplication;
import com.innovation.biz.iterm.AnimalClassifierResultIterm;
import com.innovation.biz.iterm.PostureItem;
import com.innovation.biz.iterm.PredictRotationIterm;
import com.innovation.utils.PointFloat;
import com.innovation.utils.TensorFlowHelper;

import org.tensorflow.demo.CameraConnectionFragment;
import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.innovation.utils.ImageUtils.padBitmap2SpRatio;
import static com.innovation.utils.ImageUtils.zoomImage;
import static org.tensorflow.demo.CameraConnectionFragment.textAngelX;
import static org.tensorflow.demo.CameraConnectionFragment.textAngelY;
import static org.tensorflow.demo.DetectorActivity.offsetX;
import static org.tensorflow.demo.DetectorActivity.offsetY;

/**
 * @author luolu .2018/8/4
 */
public class PigFaceBoxDetector implements Classifier {
    private static final Logger sLogger = new Logger(PigFaceBoxDetector.class);
    private static final float MIN_CONFIDENCE = (float) 0.5;
    // Only return this many results.
    private static final int NUM_DETECTIONS = 10;
    // Float model
    // Number of threads in the java app
    private static final int NUM_THREADS = 4;


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
    private Classifier pigFaceRotationDetector;
    private Classifier pigFaceKeyPointsDetector;
    public static String srcPigBitmapName;
    public static RecognitionAndPostureItem pigTFliteRecognitionAndPostureItem;

    private PigFaceBoxDetector() {
        try {
            pigFaceRotationDetector =
                    PigRotationClassifier.create(
                            InnApplication.getAppContext().getAssets(),
                            TensorFlowHelper.PIG_TFLITE_PREDICTION_MODEL_FILE,
                            "",
                            192,
                            true);
            pigFaceKeyPointsDetector =
                    PigKeyPointsClassifier.create(
                            InnApplication.getAppContext().getAssets(),
                            TensorFlowHelper.PIG_TFLITE_KEYPOINTS_MODEL_FILE,
                            "",
                            192,
                            true);

        } catch (final Exception e) {
            throw new RuntimeException("pigFaceRotationDetector or pigFaceKeyPointsDetector: Error initializing!", e);
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
        final PigFaceBoxDetector d = new PigFaceBoxDetector();
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
        d.tfLite.setNumThreads(NUM_THREADS);
        d.outputLocations = new float[1][NUM_DETECTIONS][4];
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
    public RecognitionAndPostureItem cowFaceBoxDetector(Bitmap bitmap) {
        return null;
    }

    @Override
    public PredictRotationIterm cowRotationAndKeypointsClassifier(Bitmap bitmap) {
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
    public RecognitionAndPostureItem pigFaceBoxDetector(Bitmap bitmap) {
        pigTFliteRecognitionAndPostureItem = new RecognitionAndPostureItem();
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
        srcPigBitmapName = sdf.format(new Date(System.currentTimeMillis())) + ".jpeg";
        com.innovation.utils.ImageUtils.saveBitmap(bitmap, "pigSrcImage", srcPigBitmapName);
        sLogger.i("padBitmap padSize %d:" + padSize);
        Bitmap resizeBitmap = TensorFlowHelper.resizeBitmap(bitmap, inputSize);
        imgData = TensorFlowHelper.convertBitmapToByteBuffer(resizeBitmap, intValues, imgData);
        Trace.beginSection("feed");

        outputLocations = new float[1][NUM_DETECTIONS][4];
        outputClassifyResult = new float[1][NUM_DETECTIONS];
        outputScores = new float[1][NUM_DETECTIONS];
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

        sLogger.i("pig Detect face tflite cost:" + (SystemClock.uptimeMillis() - startTime));

        float modelY0 = 0;
        float modelX0 = 0;
        float modelY1 = 0;
        float modelX1 = 0;
        if (outputDetectNum[0] > 1) {
            CameraConnectionFragment.showToast("检测到干扰对象，请调整！");
        }
        if (outputScores[0][0] > 1 || outputScores[0][0] < MIN_CONFIDENCE || outputDetectNum[0] <= 0) {
            return pigTFliteRecognitionAndPostureItem;
        }
        com.innovation.utils.ImageUtils.saveBitmap(bitmap, "pigDetected", srcPigBitmapName);
        sLogger.i("outputScores0 %f:" + outputScores[0][0]);
        sLogger.i("OutClassifyResult0 %f:" + outputClassifyResult[0][0]);
        sLogger.i("OutPDetectNum %f:" + outputDetectNum[0]);
        modelY0 = (float) outputLocations[0][0][1];
        modelX0 = (float) outputLocations[0][0][0];
        modelY1 = (float) outputLocations[0][0][3];
        modelX1 = (float) outputLocations[0][0][2];

        sLogger.i("modelY0 %f:" + modelY0);
        sLogger.i("modelX0 %f:" + modelX0);
        sLogger.i("modelY1 %f:" + modelY1);
        sLogger.i("modelX1 %f:" + modelX1);
//            tracker
        final ArrayList<Recognition> recognitions = new ArrayList<>(1);
        final RectF detection =
                new RectF(
                        modelY0 * padSize - offsetY,
                        modelX0 * padSize - offsetX,
                        modelY1 * padSize - offsetY,
                        modelX1 * padSize - offsetX);
        recognitions.add(
                new Recognition(
                        "",
                        "pigLite",
                        outputScores[0][0],
                        detection, null));
        Trace.endSection(); // "recognizeImage"
        pigTFliteRecognitionAndPostureItem.setList(recognitions);
        if (modelY0 < 0 && modelY0 > 1 &&
                modelX0 < 0 && modelX0 > 1 &&
                modelY1 < 0 && modelY1 > 1 &&
                modelX1 < 0 && modelX1 > 1) {
            return pigTFliteRecognitionAndPostureItem;
        }

        //clip image
        Bitmap clipBitmap = null;
        clipBitmap = com.innovation.utils.ImageUtils.clipBitmap(bitmap, modelY0, modelX0, modelY1, modelX1, 1.2f);
// TODO: 2018/9/25 By:LuoLu keypointDetect
        List<PointFloat> keypointResults = new ArrayList<>();
        keypointResults = pigFaceKeyPointsDetector.recognizePointImage(clipBitmap);
        if (keypointResults != null) {
            sLogger.i("keypointResults :" + keypointResults.toString());
        }
        com.innovation.utils.ImageUtils.saveBitmap(clipBitmap, "pigClip", srcPigBitmapName);
// draw rect
//                Canvas canvasDrawRecognition = new Canvas(bitmap);
//                com.innovation.utils.ImageUtils.drawRecognitions(canvasDrawRecognition,
//                        modelY0, modelX0, modelY1, modelX1);
//                saveDrawRectBitmap(bitmap);

        PredictRotationIterm predictRotationIterm = null;
        predictRotationIterm = pigFaceRotationDetector.pigRotationAndKeypointsClassifier(clipBitmap);

        Bitmap padBitmap2SpRatio = padBitmap2SpRatio(clipBitmap, 1.0f);
        int widthZoom = 320, heightZoom = 320;
        Bitmap resizeClipBitmap = zoomImage(padBitmap2SpRatio, widthZoom, heightZoom);

        // TODO: 2018/10/30 By:LuoLu
        if (keypointResults != null && predictRotationIterm != null) {
            posture = new PostureItem(
                    (float) predictRotationIterm.rot_x,
                    (float) predictRotationIterm.rot_y,
                    (float) predictRotationIterm.rot_z,
                    modelX0, modelY0, modelX1, modelY1, outputScores[0][0],
                    modelY0 * padSize, modelX0 * padSize,
                    modelY1 * padSize, modelX1 * padSize, resizeClipBitmap, bitmap);
            pigTFliteRecognitionAndPostureItem.setPostureItem(posture);
            AnimalClassifierResultIterm.pigAngleCalculateTFlite(pigTFliteRecognitionAndPostureItem.getPostureItem());
        } else {
            pigTFliteRecognitionAndPostureItem.setPostureItem(null);
        }

        return pigTFliteRecognitionAndPostureItem;
    }
}
