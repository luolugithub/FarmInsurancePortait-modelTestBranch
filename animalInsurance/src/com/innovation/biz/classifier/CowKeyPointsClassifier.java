package com.innovation.biz.classifier;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
//import android.graphics.PointFloat;
import android.graphics.Paint;
import android.os.SystemClock;
import android.os.Trace;

import com.innovation.biz.iterm.CowFaceKeyPointsItem;
import com.innovation.biz.iterm.PredictRotationIterm;
import com.innovation.utils.ByteUtil;
import com.innovation.utils.FileUtils;
import com.innovation.utils.PointFloat;
import com.innovation.utils.TensorFlowHelper;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.innovation.biz.classifier.CowFaceBoxDetector.srcCowBitmapName;
import static com.innovation.utils.ImageUtils.padBitmap;

/**
 * Author by luolu, Date on 2018/10/9.
 * COMPANY：InnovationAI
 */

public class CowKeyPointsClassifier implements Classifier {
    private static final Logger sLogger = new Logger(CowKeyPointsClassifier.class);
    private boolean isModelQuantized;
    // Config values.
    private int inputSize;
    private int[] intValues;
    private byte[][] keyPoints;
    private byte[][] exists;

    private ByteBuffer imgData;
    private Interpreter tfLite;
    public static boolean cowKeypointsDetectedK1;
    public static boolean cowKeypointsDetectedK2;
    public static boolean cowKeypointsDetectedK3;

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
        final CowKeyPointsClassifier d = new CowKeyPointsClassifier();

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
        d.keyPoints = new byte[1][26];
        d.exists = new byte[1][13];
        return d;
    }

    private CowKeyPointsClassifier() {
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
        cowKeypointsDetectedK1 = false;
        cowKeypointsDetectedK2 = false;
        cowKeypointsDetectedK3 = false;
        sLogger.i("bitmap height:" + bitmap.getHeight());
        sLogger.i("bitmap width:" + bitmap.getWidth());
        sLogger.i("inputSize:" + inputSize);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int padSize = Math.max(height, width);
        Bitmap padBitmap = padBitmap(bitmap);
        Bitmap resizeBitmap = TensorFlowHelper.resizeBitmap(padBitmap, inputSize);
        imgData = TensorFlowHelper.convertBitmapToByteBuffer(resizeBitmap, intValues, imgData);
        Trace.beginSection("feed");

        keyPoints = new byte[1][26];
        exists = new byte[1][13];
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, keyPoints);
        outputMap.put(1, exists);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        final long startTime = SystemClock.uptimeMillis();
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);
        sLogger.i(srcCowBitmapName + "CowKeyPointsClassifier cost:" + (SystemClock.uptimeMillis() - startTime));
        try {
            DetectorActivity.writer.write(srcCowBitmapName + "CowKeyPointsClassifier cost:" + (SystemClock.uptimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Trace.endSection();
        float x = 404, y = 404;
        List<PointFloat> pointFloats = new ArrayList<>();
        PointFloat pointFloat0 = new PointFloat(x, y);
        PointFloat pointFloat1 = new PointFloat(x, y);
        PointFloat pointFloat2 = new PointFloat(x, y);
        PointFloat pointFloat3 = new PointFloat(x, y);
        PointFloat pointFloat4 = new PointFloat(x, y);
        PointFloat pointFloat5 = new PointFloat(x, y);
        PointFloat pointFloat6 = new PointFloat(x, y);
        PointFloat pointFloat7 = new PointFloat(x, y);
        PointFloat pointFloat8 = new PointFloat(x, y);
        PointFloat pointFloat9 = new PointFloat(x, y);
        PointFloat pointFloat10 = new PointFloat(x, y);
        PointFloat pointFloat11 = new PointFloat(x, y);
        PointFloat pointFloat12 = new PointFloat(x, y);

        char charsOutkeyPoints0 = ByteUtil.convertByte2Uint8(keyPoints[0][0]);
        char charsOutkeyPoints1 = ByteUtil.convertByte2Uint8(keyPoints[0][1]);
        char charsOutkeyPoints2 = ByteUtil.convertByte2Uint8(keyPoints[0][2]);
        char charsOutkeyPoints3 = ByteUtil.convertByte2Uint8(keyPoints[0][3]);
        char charsOutkeyPoints4 = ByteUtil.convertByte2Uint8(keyPoints[0][4]);
        char charsOutkeyPoints5 = ByteUtil.convertByte2Uint8(keyPoints[0][5]);
        char charsOutkeyPoints6 = ByteUtil.convertByte2Uint8(keyPoints[0][6]);
        char charsOutkeyPoints7 = ByteUtil.convertByte2Uint8(keyPoints[0][7]);
        char charsOutkeyPoints8 = ByteUtil.convertByte2Uint8(keyPoints[0][8]);
        char charsOutkeyPoints9 = ByteUtil.convertByte2Uint8(keyPoints[0][9]);
        char charsOutkeyPoints10 = ByteUtil.convertByte2Uint8(keyPoints[0][10]);
        char charsOutkeyPoints11 = ByteUtil.convertByte2Uint8(keyPoints[0][11]);
        char charsOutkeyPoints12 = ByteUtil.convertByte2Uint8(keyPoints[0][12]);
        char charsOutkeyPoints13 = ByteUtil.convertByte2Uint8(keyPoints[0][13]);
        char charsOutkeyPoints14 = ByteUtil.convertByte2Uint8(keyPoints[0][14]);
        char charsOutkeyPoints15 = ByteUtil.convertByte2Uint8(keyPoints[0][15]);
        char charsOutkeyPoints16 = ByteUtil.convertByte2Uint8(keyPoints[0][16]);
        char charsOutkeyPoints17 = ByteUtil.convertByte2Uint8(keyPoints[0][17]);
        char charsOutkeyPoints18 = ByteUtil.convertByte2Uint8(keyPoints[0][18]);
        char charsOutkeyPoints19 = ByteUtil.convertByte2Uint8(keyPoints[0][19]);
        char charsOutkeyPoints20 = ByteUtil.convertByte2Uint8(keyPoints[0][20]);
        char charsOutkeyPoints21 = ByteUtil.convertByte2Uint8(keyPoints[0][21]);
        char charsOutkeyPoints22 = ByteUtil.convertByte2Uint8(keyPoints[0][22]);
        char charsOutkeyPoints23 = ByteUtil.convertByte2Uint8(keyPoints[0][23]);
        char charsOutkeyPoints24 = ByteUtil.convertByte2Uint8(keyPoints[0][24]);
        char charsOutkeyPoints25 = ByteUtil.convertByte2Uint8(keyPoints[0][25]);
        char charsOutExists0 = ByteUtil.convertByte2Uint8(exists[0][0]);
        char charsOutExists1 = ByteUtil.convertByte2Uint8(exists[0][1]);
        char charsOutExists2 = ByteUtil.convertByte2Uint8(exists[0][2]);
        char charsOutExists3 = ByteUtil.convertByte2Uint8(exists[0][3]);
        char charsOutExists4 = ByteUtil.convertByte2Uint8(exists[0][4]);
        char charsOutExists5 = ByteUtil.convertByte2Uint8(exists[0][5]);
        char charsOutExists6 = ByteUtil.convertByte2Uint8(exists[0][6]);
        char charsOutExists7 = ByteUtil.convertByte2Uint8(exists[0][7]);
        char charsOutExists8 = ByteUtil.convertByte2Uint8(exists[0][8]);
        char charsOutExists9 = ByteUtil.convertByte2Uint8(exists[0][9]);
        char charsOutExists10 = ByteUtil.convertByte2Uint8(exists[0][10]);
        char charsOutExists11 = ByteUtil.convertByte2Uint8(exists[0][11]);
        char charsOutExists12 = ByteUtil.convertByte2Uint8(exists[0][12]);

        double quantization = 0.00390625;
        double exists = 0.5;
        int[] pointsExists = new int[13];
        CowFaceKeyPointsItem cowFaceKeyPointsItem = CowFaceKeyPointsItem.getInstance();

        if ((charsOutExists0 - 0) * quantization > exists) {
            pointFloat0.set((float) (charsOutkeyPoints0 * quantization), (float)(charsOutkeyPoints1 * quantization));
            pointFloats.add(pointFloat0);
            pointsExists[0] = 1;
            cowFaceKeyPointsItem.setPointsExists0(pointsExists[0]);
            cowFaceKeyPointsItem.setPointFloat0(pointFloat0);
            sLogger.i("关键点1:");
            sLogger.i("获取的point0 %d:" + pointFloat0.toString());
        }else {
            pointsExists[0] = 0;
            cowFaceKeyPointsItem.setPointsExists0(pointsExists[0]);
            cowFaceKeyPointsItem.setPointFloat0(pointFloat0);
        }
        if ((charsOutExists1 - 0) * quantization > exists) {
            pointFloat1.set((float) (charsOutkeyPoints2 * quantization), (float) (charsOutkeyPoints3 * quantization));
            pointFloats.add(pointFloat1);
            pointsExists[1] = 1;
            cowFaceKeyPointsItem.setPointsExists1(pointsExists[1]);
            cowFaceKeyPointsItem.setPointFloat1(pointFloat1);
            sLogger.i("关键点2:");
            sLogger.i("获取的point1 %d:" + pointFloat1.toString());
        }else {
            pointsExists[1] = 0;
            cowFaceKeyPointsItem.setPointsExists1(pointsExists[1]);
            cowFaceKeyPointsItem.setPointFloat1(pointFloat1);
        }
        if ((charsOutExists2 - 0) * quantization > exists) {
            pointFloat2.set((float) (charsOutkeyPoints4 * quantization), (float) (charsOutkeyPoints5 * quantization));
            pointFloats.add(pointFloat2);
            pointsExists[2] = 1;
            cowFaceKeyPointsItem.setPointsExists2(pointsExists[2]);
            cowFaceKeyPointsItem.setPointFloat2(pointFloat2);
            sLogger.i("关键点3:");
            sLogger.i("获取的point2 %d:" + pointFloat2.toString());
        }else {
            pointsExists[2] = 0;
            cowFaceKeyPointsItem.setPointsExists2(pointsExists[2]);
            cowFaceKeyPointsItem.setPointFloat2(pointFloat2);
        }
        if ((charsOutExists3 - 0) * quantization > exists) {
            pointFloat3.set((float) (charsOutkeyPoints6 * quantization), (float) (charsOutkeyPoints7 * quantization));
            pointFloats.add(pointFloat3);
            pointsExists[3] = 1;
            cowFaceKeyPointsItem.setPointsExists3(pointsExists[3]);
            cowFaceKeyPointsItem.setPointFloat3(pointFloat3);
            sLogger.i("关键点4:");
            sLogger.i("获取的point3 %d:" + pointFloat3.toString());
        }else {
            pointsExists[3] = 0;
            cowFaceKeyPointsItem.setPointsExists3(pointsExists[3]);
            cowFaceKeyPointsItem.setPointFloat3(pointFloat3);
        }
        if ((charsOutExists4 - 0) * quantization > exists) {
            pointFloat4.set((float) (charsOutkeyPoints8 * quantization), (float) (charsOutkeyPoints9 * quantization));
            pointFloats.add(pointFloat4);
            pointsExists[4] = 1;
            cowFaceKeyPointsItem.setPointsExists4(pointsExists[4]);
            cowFaceKeyPointsItem.setPointFloat4(pointFloat4);
            sLogger.i("关键点5:");
            sLogger.i("获取的point4 %d:" + pointFloat4.toString());
        }else {
            pointsExists[4] = 0;
            cowFaceKeyPointsItem.setPointsExists4(pointsExists[4]);
            cowFaceKeyPointsItem.setPointFloat4(pointFloat4);
        }
        if ((charsOutExists5 - 0) * quantization > exists) {
            pointFloat5.set((float) (charsOutkeyPoints10 * quantization), (float) (charsOutkeyPoints11 * quantization));
            pointFloats.add(pointFloat5);
            pointsExists[5] = 1;
            cowFaceKeyPointsItem.setPointsExists5(pointsExists[5]);
            cowFaceKeyPointsItem.setPointFloat5(pointFloat5);
            sLogger.i("关键点6:");
            sLogger.i("获取的point5 %d:" + pointFloat5.toString());
        }else {
            pointsExists[5] = 0;
            cowFaceKeyPointsItem.setPointsExists5(pointsExists[5]);
            cowFaceKeyPointsItem.setPointFloat5(pointFloat5);
        }
        if ((charsOutExists6 - 0) * quantization > exists) {
            pointFloat6.set((float) (charsOutkeyPoints12 * quantization), (float) (charsOutkeyPoints13 * quantization));
            pointFloats.add(pointFloat6);
            pointsExists[6] = 1;
            cowFaceKeyPointsItem.setPointsExists6(pointsExists[6]);
            cowFaceKeyPointsItem.setPointFloat6(pointFloat6);
            sLogger.i("关键点7:");
            sLogger.i("获取的point6 %d:" + pointFloat6.toString());
        }else {
            pointsExists[6] = 0;
            cowFaceKeyPointsItem.setPointsExists6(pointsExists[6]);
            cowFaceKeyPointsItem.setPointFloat6(pointFloat6);
        }
        if ((charsOutExists7 - 0) * quantization > exists) {
            pointFloat7.set((float) (charsOutkeyPoints14 * quantization), (float) (charsOutkeyPoints15 * quantization));
            pointFloats.add(pointFloat7);
            pointsExists[7] = 1;
            cowFaceKeyPointsItem.setPointsExists7(pointsExists[7]);
            cowFaceKeyPointsItem.setPointFloat7(pointFloat7);
            sLogger.i("关键点8:");
            sLogger.i("获取的point7 %d:" + pointFloat7.toString());
        }else {
            pointsExists[7] = 0;
            cowFaceKeyPointsItem.setPointsExists7(pointsExists[7]);
            cowFaceKeyPointsItem.setPointFloat7(pointFloat7);
        }
        if ((charsOutExists8 - 0) * quantization > exists) {
            pointFloat8.set((float) (charsOutkeyPoints16 * quantization), (float) (charsOutkeyPoints17 * quantization));
            pointFloats.add(pointFloat8);
            pointsExists[8] = 1;
            cowFaceKeyPointsItem.setPointsExists8(pointsExists[8]);
            cowFaceKeyPointsItem.setPointFloat8(pointFloat8);
            sLogger.i("关键点9:");
            sLogger.i("获取的point8 %d:" + pointFloat8.toString());
        }else {
            pointsExists[8] = 0;
            cowFaceKeyPointsItem.setPointsExists8(pointsExists[8]);
            cowFaceKeyPointsItem.setPointFloat8(pointFloat8);
        }
        if ((charsOutExists9 - 0) * quantization > exists) {
            pointFloat9.set((float) (charsOutkeyPoints18 * quantization), (float) (charsOutkeyPoints19 * quantization));
            pointFloats.add(pointFloat9);
            pointsExists[9] = 1;
            cowFaceKeyPointsItem.setPointsExists9(pointsExists[9]);
            cowFaceKeyPointsItem.setPointFloat9(pointFloat9);
            sLogger.i("关键点10:");
            sLogger.i("获取的point9 %d:" + pointFloat9.toString());
        }else {
            pointsExists[9] = 0;
            cowFaceKeyPointsItem.setPointsExists9(pointsExists[9]);
            cowFaceKeyPointsItem.setPointFloat9(pointFloat9);
        }
        if ((charsOutExists10 - 0) * quantization > exists) {
            pointFloat10.set((float) (charsOutkeyPoints20 * quantization), (float) (charsOutkeyPoints21 * quantization));
            pointFloats.add(pointFloat10);
            pointsExists[10] = 1;
            cowFaceKeyPointsItem.setPointsExists10(pointsExists[10]);
            cowFaceKeyPointsItem.setPointFloat10(pointFloat10);
            sLogger.i("关键点11:");
            sLogger.i("获取的point10 %d:" + pointFloat10.toString());
        }else {
            pointsExists[10] = 0;
            cowFaceKeyPointsItem.setPointsExists10(pointsExists[10]);
            cowFaceKeyPointsItem.setPointFloat10(pointFloat10);
        }
        if ((charsOutExists11 - 0) * quantization > exists) {
            pointFloat11.set((float) (charsOutkeyPoints22 * quantization), (float) (charsOutkeyPoints23 * quantization));
            pointFloats.add(pointFloat11);
            pointsExists[11] = 1;
            cowFaceKeyPointsItem.setPointsExists11(pointsExists[11]);
            cowFaceKeyPointsItem.setPointFloat11(pointFloat11);
            sLogger.i("关键点12:");
            sLogger.i("获取的point11 %d:" + pointFloat11.toString());
        }else {
            pointsExists[11] = 0;
            cowFaceKeyPointsItem.setPointsExists11(pointsExists[11]);
            cowFaceKeyPointsItem.setPointFloat11(pointFloat11);
        }
        if ((charsOutExists12 - 0) * quantization > exists) {
            pointFloat12.set((float) (charsOutkeyPoints24 * quantization), (float) (charsOutkeyPoints25 * quantization));
            pointFloats.add(pointFloat12);
            pointsExists[12] = 1;
            cowFaceKeyPointsItem.setPointsExists12(pointsExists[12]);
            cowFaceKeyPointsItem.setPointFloat12(pointFloat12);
            sLogger.i("关键点13:");
            sLogger.i("获取的point12 %d:" + pointFloat12.toString());
        }else {
            pointsExists[12] = 0;
            cowFaceKeyPointsItem.setPointsExists12(pointsExists[12]);
            cowFaceKeyPointsItem.setPointFloat12(pointFloat12);
        }


        sLogger.i(srcCowBitmapName + "获取的关键点 %d:" + pointFloats.toString());
        try {
            DetectorActivity.writer.write("\r\n");
            DetectorActivity.writer.write(srcCowBitmapName + "__获取的关键点 %d:" + pointFloats.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //画关键点
        Canvas canvasDrawPoints = new Canvas(padBitmap);
        if (pointsExists[11] + pointsExists[12] == 2
                && pointsExists[7] + pointsExists[8] + pointsExists[9] + pointsExists[10] > 0
                && pointsExists[4] + pointsExists[5] == 0){
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat10,padSize,"11");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat11,padSize,"12");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat12,padSize,"13");
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"cowKeypointsP1",srcCowBitmapName);
            cowKeypointsDetectedK1 = true;
        }else if ((pointsExists[4] + pointsExists[6] + pointsExists[11] == 3
                && (pointsExists[0] + pointsExists[1] + pointsExists[2] + pointsExists[3] > 0
                || pointsExists[7] + pointsExists[8] + pointsExists[9] + pointsExists[10] > 0))
                || (pointsExists[3] + pointsExists[6] + pointsExists[10] == 3
                && (pointsExists[0] + pointsExists[1] + pointsExists[2] + pointsExists[4] > 0
                || pointsExists[7] + pointsExists[8] + pointsExists[9] + pointsExists[11] > 0))){
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat10,padSize,"11");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat11,padSize,"12");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat12,padSize,"13");
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"cowKeypointsP2",srcCowBitmapName);
            cowKeypointsDetectedK2 = true;
        }else if (pointsExists[11] + pointsExists[12] == 0
                && pointsExists[4] + pointsExists[5] == 2
                && pointsExists[0] + pointsExists[1] + pointsExists[2] + pointsExists[3] > 0){
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat10,padSize,"11");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat11,padSize,"12");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat12,padSize,"13");
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"cowKeypointsP3",srcCowBitmapName);
            cowKeypointsDetectedK3 = true;
        }else {
            if (pointFloats != null) {
                Paint boxPaint = new Paint();
                boxPaint.setColor(Color.RED);
                boxPaint.setStyle(Paint.Style.STROKE);
                boxPaint.setStrokeWidth(8.0f);
                boxPaint.setStrokeCap(Paint.Cap.ROUND);
                boxPaint.setStrokeJoin(Paint.Join.ROUND);
                boxPaint.setStrokeMiter(100);
                boxPaint.setColor(Color.YELLOW);
                for (PointFloat point : pointFloats) {
                    canvasDrawPoints.drawCircle(point.getX() * padSize, point.getY() * padSize, 3, boxPaint);
                }

            }
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat0,padSize,"1");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat1,padSize,"2");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat2,padSize,"3");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat3,padSize,"4");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat4,padSize,"5");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat5,padSize,"6");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat6,padSize,"7");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat7,padSize,"8");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat8,padSize,"9");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat9,padSize,"10");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat10,padSize,"11");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat11,padSize,"12");
            com.innovation.utils.ImageUtils.drawKeypoints(canvasDrawPoints,pointFloat12,padSize,"13");
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"cowKeypointsP10",srcCowBitmapName);
        }

        return pointFloats;
    }

}
