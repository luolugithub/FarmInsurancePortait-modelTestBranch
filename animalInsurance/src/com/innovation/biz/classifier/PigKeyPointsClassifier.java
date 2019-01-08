package com.innovation.biz.classifier;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.os.Trace;

import com.innovation.biz.iterm.PigFaceKeyPointsItem;
import com.innovation.biz.iterm.PredictRotationIterm;
import com.innovation.utils.ByteUtil;
import com.innovation.utils.PointFloat;
import com.innovation.utils.TensorFlowHelper;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.innovation.biz.classifier.PigFaceBoxDetector.srcPigBitmapName;
import static com.innovation.utils.ImageUtils.padBitmap;

/**
 * Author by luolu, Date on 2018/10/9.
 * COMPANY：InnovationAI
 */

public class PigKeyPointsClassifier implements Classifier {
    private static final Logger sLogger = new Logger(PigKeyPointsClassifier.class);
    private static final boolean DEBUG = false;
    private static final float MIN_CONFIDENCE = (float) 0.6;
    private static final String TAG = "PigKeyPointsClassifier";

    // Only return this many results.
    private static final int NUM_DETECTIONS = 10;
    private boolean isModelQuantized;
    // Float model
    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    // Number of threads in the java app
    private static final int NUM_THREADS = 4;
    // Config values.
    private int inputSize;
    private int[] intValues;
    private byte[][] keyPoints;
    private byte[][] exists;

    private ByteBuffer imgData;
    private Interpreter tfLite;
    public static boolean pigKeypointsK1;
    public static boolean pigKeypointsK2;
    public static boolean pigKeypointsK3;

    /**
     * Initializes a native TensorFlow session for classifying images.
     *  @param assetManager  The asset manager to be used to load assets.
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
        final PigKeyPointsClassifier d = new PigKeyPointsClassifier();

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
        d.keyPoints = new byte[1][22];
        d.exists = new byte[1][11];
        return d;
    }

    private PigKeyPointsClassifier() {
    }

    @Override
    public RecognitionAndPostureItem donkeyFaceBoxDetector(Bitmap bitmap) {
        return null;
    }

    @Override
    public PredictRotationIterm donkeyRotationAndKeypointsClassifier(Bitmap bitmap) {
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
    public RecognitionAndPostureItem pigFaceBoxDetector(Bitmap bitmap) {
        return null;
    }

    @Override
    public PredictRotationIterm pigRotationAndKeypointsClassifier(Bitmap bitmap) {
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
    public List<PointFloat> recognizePointImage(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        pigKeypointsK1 = false;
        pigKeypointsK2 = false;
        pigKeypointsK3 = false;
        sLogger.i("bitmap height:" + bitmap.getHeight());
        sLogger.i("bitmap width:" + bitmap.getWidth());
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int padSize = Math.max(height, width);
        Bitmap padBitmap = padBitmap(bitmap);
        Bitmap resizeBitmap = TensorFlowHelper.resizeBitmap(padBitmap, inputSize);
        imgData = TensorFlowHelper.convertBitmapToByteBuffer(resizeBitmap, intValues, imgData);
        Trace.beginSection("feed");

        //定义关键点模型变量
        keyPoints = new byte[1][22];
        exists = new byte[1][11];

        sLogger.i("inputSize:" + inputSize);

        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, keyPoints);
        outputMap.put(1, exists);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        final long startTime = SystemClock.uptimeMillis();
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);
        sLogger.i("PigKeyPointsClassifier cost:" + (SystemClock.uptimeMillis() - startTime));

        Trace.endSection();
        float x = 404, y = 404;
        List<PointFloat> points = new ArrayList<>();
        PointFloat point0 = new PointFloat(x, y);
        PointFloat point1 = new PointFloat(x, y);
        PointFloat point2 = new PointFloat(x, y);
        PointFloat point3 = new PointFloat(x, y);
        PointFloat point4 = new PointFloat(x, y);
        PointFloat point5 = new PointFloat(x, y);
        PointFloat point6 = new PointFloat(x, y);
        PointFloat point7 = new PointFloat(x, y);
        PointFloat point8 = new PointFloat(x, y);
        PointFloat point9 = new PointFloat(x, y);
        PointFloat point10 = new PointFloat(x, y);


        char charsOutkeyPoints0  =  ByteUtil.convertByte2Uint8(keyPoints[0][0]);
        char charsOutkeyPoints1  =  ByteUtil.convertByte2Uint8(keyPoints[0][1]);
        char charsOutkeyPoints2  =  ByteUtil.convertByte2Uint8(keyPoints[0][2]);
        char charsOutkeyPoints3  =  ByteUtil.convertByte2Uint8(keyPoints[0][3]);
        char charsOutkeyPoints4  =  ByteUtil.convertByte2Uint8(keyPoints[0][4]);
        char charsOutkeyPoints5  =  ByteUtil.convertByte2Uint8(keyPoints[0][5]);
        char charsOutkeyPoints6  =  ByteUtil.convertByte2Uint8(keyPoints[0][6]);
        char charsOutkeyPoints7  =  ByteUtil.convertByte2Uint8(keyPoints[0][7]);
        char charsOutkeyPoints8  =  ByteUtil.convertByte2Uint8(keyPoints[0][8]);
        char charsOutkeyPoints9  =  ByteUtil.convertByte2Uint8(keyPoints[0][9]);
        char charsOutkeyPoints10 =  ByteUtil.convertByte2Uint8(keyPoints[0][10]);
        char charsOutkeyPoints11 =  ByteUtil.convertByte2Uint8(keyPoints[0][11]);
        char charsOutkeyPoints12 =  ByteUtil.convertByte2Uint8(keyPoints[0][12]);
        char charsOutkeyPoints13 =  ByteUtil.convertByte2Uint8(keyPoints[0][13]);
        char charsOutkeyPoints14 =  ByteUtil.convertByte2Uint8(keyPoints[0][14]);
        char charsOutkeyPoints15 =  ByteUtil.convertByte2Uint8(keyPoints[0][15]);
        char charsOutkeyPoints16 =  ByteUtil.convertByte2Uint8(keyPoints[0][16]);
        char charsOutkeyPoints17 =  ByteUtil.convertByte2Uint8(keyPoints[0][17]);
        char charsOutkeyPoints18 =  ByteUtil.convertByte2Uint8(keyPoints[0][18]);
        char charsOutkeyPoints19 =  ByteUtil.convertByte2Uint8(keyPoints[0][19]);
        char charsOutkeyPoints20 =  ByteUtil.convertByte2Uint8(keyPoints[0][20]);
        char charsOutkeyPoints21 =  ByteUtil.convertByte2Uint8(keyPoints[0][21]);

        char charsOutExists0  =  ByteUtil.convertByte2Uint8(exists[0][0]);
        char charsOutExists1  =  ByteUtil.convertByte2Uint8(exists[0][1]);
        char charsOutExists2  =  ByteUtil.convertByte2Uint8(exists[0][2]);
        char charsOutExists3  =  ByteUtil.convertByte2Uint8(exists[0][3]);
        char charsOutExists4  =  ByteUtil.convertByte2Uint8(exists[0][4]);
        char charsOutExists5  =  ByteUtil.convertByte2Uint8(exists[0][5]);
        char charsOutExists6  =  ByteUtil.convertByte2Uint8(exists[0][6]);
        char charsOutExists7  =  ByteUtil.convertByte2Uint8(exists[0][7]);
        char charsOutExists8  =  ByteUtil.convertByte2Uint8(exists[0][8]);
        char charsOutExists9  =  ByteUtil.convertByte2Uint8(exists[0][9]);
        char charsOutExists10 =  ByteUtil.convertByte2Uint8(exists[0][10]);


        double quantization = 0.00390625;
        double exists = 0.5;
        int[] pointsExists = new int[11];
        PigFaceKeyPointsItem pigFaceKeyPointsItem = PigFaceKeyPointsItem.getInstance();

        if ((charsOutExists0 - 0) * quantization > exists) {
            point0.set((float) (charsOutkeyPoints0 * quantization), (float) (charsOutkeyPoints1 * quantization));
            points.add(point0);
            pointsExists[0] = 1;
            pigFaceKeyPointsItem.setPointsExists0(pointsExists[0]);
            pigFaceKeyPointsItem.setPointFloat0(point0);
            sLogger.i("关键点1:");
            sLogger.i("获取的point1 %d:" + point0.toString());
        }else {
            pointsExists[0] = 0;
            pigFaceKeyPointsItem.setPointsExists0(pointsExists[0]);
            pigFaceKeyPointsItem.setPointFloat0(point0);
        }
        if ((charsOutExists1 - 0) * quantization > exists) {
            point1.set((float) (charsOutkeyPoints2 * quantization), (float) (charsOutkeyPoints3 * quantization));
            points.add(point1);
            pointsExists[1] = 1;
            pigFaceKeyPointsItem.setPointsExists1(pointsExists[1]);
            pigFaceKeyPointsItem.setPointFloat1(point1);
            sLogger.i("关键点2:");
            sLogger.i("获取的point2 %d:" + point1.toString());
        }else {
            pointsExists[1] = 0;
            pigFaceKeyPointsItem.setPointsExists1(pointsExists[1]);
            pigFaceKeyPointsItem.setPointFloat1(point1);
        }
        if ((charsOutExists2 - 0) * quantization > exists) {
            point2.set((float) (charsOutkeyPoints4 * quantization), (float) (charsOutkeyPoints5 * quantization));
            points.add(point2);
            pointsExists[2] = 1;
            pigFaceKeyPointsItem.setPointsExists2(pointsExists[2]);
            pigFaceKeyPointsItem.setPointFloat2(point2);
            sLogger.i("关键点3:");
            sLogger.i("获取的point3 %d:" + point2.toString());
        }else {
            pointsExists[2] = 0;
            pigFaceKeyPointsItem.setPointsExists2(pointsExists[2]);
            pigFaceKeyPointsItem.setPointFloat2(point2);
        }
        if ((charsOutExists3 - 0) * quantization > exists) {
            point3.set((float) (charsOutkeyPoints6 * quantization), (float) (charsOutkeyPoints7 * quantization));
            points.add(point3);
            pointsExists[3] = 1;
            pigFaceKeyPointsItem.setPointsExists3(pointsExists[3]);
            pigFaceKeyPointsItem.setPointFloat3(point3);
            sLogger.i("关键点4:");
            sLogger.i("获取的point4 %d:" + point3.toString());
        }else {
            pointsExists[3] = 0;
            pigFaceKeyPointsItem.setPointsExists3(pointsExists[3]);
            pigFaceKeyPointsItem.setPointFloat3(point3);
        }
        if ((charsOutExists4 - 0) * quantization > exists) {
            point4.set((float) (charsOutkeyPoints8 * quantization), (float) (charsOutkeyPoints9 * quantization));
            points.add(point4);
            pointsExists[4] = 1;
            pigFaceKeyPointsItem.setPointsExists4(pointsExists[4]);
            pigFaceKeyPointsItem.setPointFloat4(point4);
            sLogger.i("关键点5:");
            sLogger.i("获取的point5 %d:" + point4.toString());
        }else {
            pointsExists[4] = 0;
            pigFaceKeyPointsItem.setPointsExists4(pointsExists[4]);
            pigFaceKeyPointsItem.setPointFloat4(point4);
        }
        if ((charsOutExists5 - 0) * quantization > exists) {
            point5.set((float) (charsOutkeyPoints10 * quantization), (float) (charsOutkeyPoints11 * quantization));
            points.add(point5);
            pointsExists[5] = 1;
            pigFaceKeyPointsItem.setPointsExists5(pointsExists[5]);
            pigFaceKeyPointsItem.setPointFloat5(point5);
            sLogger.i("关键点6:");
            sLogger.i("获取的point6 %d:" + point5.toString());
        }else {
            pointsExists[5] = 0;
            pigFaceKeyPointsItem.setPointsExists5(pointsExists[5]);
            pigFaceKeyPointsItem.setPointFloat5(point5);
        }
        if ((charsOutExists6 - 0) * quantization > exists) {
            point6.set((float) (charsOutkeyPoints12 * quantization), (float) (charsOutkeyPoints13 * quantization));
            points.add(point6);
            pointsExists[6] = 1;
            pigFaceKeyPointsItem.setPointsExists6(pointsExists[6]);
            pigFaceKeyPointsItem.setPointFloat6(point6);
            sLogger.i("关键点7:");
            sLogger.i("获取的point7 %d:" + point6.toString());
        }else {
            pointsExists[6] = 0;
            pigFaceKeyPointsItem.setPointsExists6(pointsExists[6]);
            pigFaceKeyPointsItem.setPointFloat6(point6);
        }
        if ((charsOutExists7 - 0) * quantization > exists) {
            point7.set((float) (charsOutkeyPoints14 * quantization), (float) (charsOutkeyPoints15 * quantization));
            points.add(point7);
            pointsExists[7] = 1;
            pigFaceKeyPointsItem.setPointsExists7(pointsExists[7]);
            pigFaceKeyPointsItem.setPointFloat7(point7);
            sLogger.i("关键点8:");
            sLogger.i("获取的point8 %d:" + point7.toString());
        }else {
            pointsExists[7] = 0;
            pigFaceKeyPointsItem.setPointsExists7(pointsExists[7]);
            pigFaceKeyPointsItem.setPointFloat7(point7);
        }
        if ((charsOutExists8 - 0) * quantization > exists) {
            point8.set((float) (charsOutkeyPoints16 * quantization), (float) (charsOutkeyPoints17 * quantization));
            points.add(point8);
            pointsExists[8] = 1;
            pigFaceKeyPointsItem.setPointsExists8(pointsExists[8]);
            pigFaceKeyPointsItem.setPointFloat8(point8);
            sLogger.i("关键点9:");
            sLogger.i("获取的point9 %d:" + point8.toString());
        }else {
            pointsExists[8] = 0;
            pigFaceKeyPointsItem.setPointsExists8(pointsExists[8]);
            pigFaceKeyPointsItem.setPointFloat8(point8);
        }
        if ((charsOutExists9 - 0) * quantization > exists) {
            point9.set((float) (charsOutkeyPoints18 * quantization), (float) (charsOutkeyPoints19 * quantization));
            points.add(point9);
            pointsExists[9] = 1;
            pigFaceKeyPointsItem.setPointsExists9(pointsExists[9]);
            pigFaceKeyPointsItem.setPointFloat9(point9);
            sLogger.i("关键点10:");
            sLogger.i("获取的point10 %d:" + point9.toString());
        }else {
            pointsExists[9] = 0;
            pigFaceKeyPointsItem.setPointsExists9(pointsExists[9]);
            pigFaceKeyPointsItem.setPointFloat9(point9);
        }
        if ((charsOutExists10 - 0) * quantization > exists) {
            point10.set((float) (charsOutkeyPoints20 * quantization), (float) (charsOutkeyPoints21 * quantization));
            points.add(point10);
            pointsExists[10] = 1;
            pigFaceKeyPointsItem.setPointsExists10(pointsExists[10]);
            pigFaceKeyPointsItem.setPointFloat10(point10);
            sLogger.i("关键点11:");
            sLogger.i("获取的point11 %d:" + point10.toString());
        }else {
            pointsExists[10] = 0;
            pigFaceKeyPointsItem.setPointsExists10(pointsExists[10]);
            pigFaceKeyPointsItem.setPointFloat10(point10);
        }
        sLogger.i("获取的关键点 %d:" + points.toString());

        //画关键点
        Canvas canvasDrawPoints = new Canvas(padBitmap);
        if (pointsExists[3] == 0 && pointsExists[5] + pointsExists[9] == 2){
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point10,padSize,"11");
            pigKeypointsK1 = true;
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigKeyP1",srcPigBitmapName);
        }else if (pointsExists[3] + pointsExists[5] + pointsExists[9] == 3){
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point10,padSize,"11");
            pigKeypointsK2 = true;
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigKeyP2",srcPigBitmapName);
        }else if (pointsExists[9] == 0 && pointsExists[3] + pointsExists[5] == 2){
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point10,padSize,"11");
            pigKeypointsK3 = true;
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigKeyP3",srcPigBitmapName);
        } else{
            if (points != null) {
                Paint boxPaint = new Paint();
                boxPaint.setColor(Color.RED);
                boxPaint.setStyle(Paint.Style.STROKE);
                boxPaint.setStrokeWidth(8.0f);
                boxPaint.setStrokeCap(Paint.Cap.ROUND);
                boxPaint.setStrokeJoin(Paint.Join.ROUND);
                boxPaint.setStrokeMiter(100);
                boxPaint.setColor(Color.YELLOW);
                for (PointFloat point : points) {
                    canvasDrawPoints.drawCircle(point.getX() * padSize, point.getY() * padSize, 3, boxPaint);
                }

            }
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,point10,padSize,"11");
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigKeyP10",srcPigBitmapName);
        }
        return points;
    }
}
