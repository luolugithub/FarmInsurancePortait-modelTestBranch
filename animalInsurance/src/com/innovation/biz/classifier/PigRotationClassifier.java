package com.innovation.biz.classifier;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.os.Trace;

import com.innovation.biz.iterm.PredictRotationIterm;
import com.innovation.utils.ByteUtil;
import com.innovation.utils.PointFloat;
import com.innovation.utils.Rot2AngleType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.innovation.biz.classifier.PigFaceBoxDetector.srcPigBitmapName;
import static com.innovation.utils.ImageUtils.padBitmap;

/**
 * @author luolu .2018/8/4
 */
public class PigRotationClassifier implements Classifier {
    private static final Logger sLogger = new Logger(PigRotationClassifier.class);
    private boolean isModelQuantized;
    private int inputSize;
    private int[] intValues;
    private byte[][] detectRotation;

    private ByteBuffer imgData;

    private Interpreter tfLite;
    public static int pigPredictAngleType;

    /**
     * Initializes a native TensorFlow session for classifying images.
     *  @param assetManager The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     * @param inputSize The size of image input
     * @param isQuantized Boolean representing model is quantized or not
     */
    public static Classifier create(
            final AssetManager assetManager,
            final String modelFilename,
            final String labelFilename,
            final int inputSize,
            final boolean isQuantized)
            throws IOException {
        final PigRotationClassifier d = new PigRotationClassifier();

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
        d.detectRotation = new byte[1][3];
        return d;
    }

    private PigRotationClassifier() {}

    @Override
    public List<PointFloat> recognizePointImage(Bitmap bitmap) {
        return null;
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
    public PredictRotationIterm pigRotationAndKeypointsClassifier(Bitmap bitmap) {
        PredictRotationIterm predictRotationIterm = null;
        if (bitmap == null) {
            return null;
        }
        pigPredictAngleType = 10;
        sLogger.i("bitmap height:" + bitmap.getHeight());
        sLogger.i("bitmap width:" + bitmap.getWidth());
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int padSize = Math.max(height, width);
        Bitmap padBitmap = padBitmap(bitmap);
        Bitmap resizeBitmap = TensorFlowHelper.resizeBitmap(padBitmap, inputSize);
        imgData = TensorFlowHelper.convertBitmapToByteBuffer(resizeBitmap, intValues, imgData);
        Trace.beginSection("feed");
        detectRotation = new byte[1][3];
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, detectRotation);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        final long startTime = SystemClock.uptimeMillis();
        tfLite.run(imgData, detectRotation);
        sLogger.i("RotationPredict pigtflite cost:" + (SystemClock.uptimeMillis() - startTime));
        int quantization =  128;
        double quantizationScale =  0.0175615;
        float predictRotX;
        float predictRotY;
        float predictRotZ;
        float rotScale = (float) 57.6;
        char charsOutRotation0 = ByteUtil.convertByte2Uint8(detectRotation[0][0]);
        char charsOutRotation1 = ByteUtil.convertByte2Uint8(detectRotation[0][1]);
        char charsOutRotation2 = ByteUtil.convertByte2Uint8(detectRotation[0][2]);

        predictRotX = (float)((charsOutRotation0 - quantization) * quantizationScale);
        predictRotY = (float)((charsOutRotation1 - quantization) * quantizationScale);
        predictRotZ = (float)((charsOutRotation2 - quantization) * quantizationScale);
        sLogger.i("predictRotX %f:" + predictRotX );
        sLogger.i("predictRotY %f:" + predictRotY );
        sLogger.i("predictRotZ %f:" + predictRotZ );

        predictRotationIterm = new PredictRotationIterm(
                predictRotX,
                predictRotY,
                predictRotZ);

        // TODO: 2018/11/1 By:LuoLu
        pigPredictAngleType = Rot2AngleType.getPigAngleType(predictRotX,
                predictRotY);
        if (pigPredictAngleType == 1){
            // draw rotation
            Canvas canvasDrawRecognition = new Canvas(padBitmap);
            com.innovation.utils.ImageUtils.drawText(canvasDrawRecognition,
                    "角度:"+pigPredictAngleType, 10,30,
                    "X:"+String.valueOf(predictRotX * rotScale),10,60,
                    "Y:"+String.valueOf(predictRotY * rotScale),10,90);
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigRotP1",srcPigBitmapName);
        }else if (pigPredictAngleType == 2){
            // draw rotation
            Canvas canvasDrawRecognition = new Canvas(padBitmap);
            com.innovation.utils.ImageUtils.drawText(canvasDrawRecognition,
                    "角度:"+pigPredictAngleType, 10,30,
                    "X:"+String.valueOf(predictRotX * rotScale),10,60,
                    "Y:"+String.valueOf(predictRotY * rotScale),10,90);
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigRotP2",srcPigBitmapName);
        }else if (pigPredictAngleType == 3){
            // draw rotation
            Canvas canvasDrawRecognition = new Canvas(padBitmap);
            com.innovation.utils.ImageUtils.drawText(canvasDrawRecognition,
                    "角度:"+pigPredictAngleType,10,30,
                    "X:"+String.valueOf(predictRotX * rotScale),10,60,
                    "Y:"+String.valueOf(predictRotY * rotScale),10,90);
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigRotP3",srcPigBitmapName);
        }else {
            // draw rotation
            Canvas canvasDrawRecognition = new Canvas(padBitmap);
            com.innovation.utils.ImageUtils.drawText(canvasDrawRecognition,
                    "角度:"+pigPredictAngleType,10,30,
                    "X:"+String.valueOf(predictRotX * rotScale),10,60,
                    "Y:"+String.valueOf(predictRotY * rotScale),10,90);
            com.innovation.utils.ImageUtils.saveBitmap(padBitmap,"pigRotP10",srcPigBitmapName);
        }


        return predictRotationIterm;
    }
}
