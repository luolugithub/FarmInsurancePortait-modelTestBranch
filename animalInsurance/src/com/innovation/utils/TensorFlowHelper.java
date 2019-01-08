package com.innovation.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import com.innovation.exception.UninitializedBufferException;

import org.tensorflow.demo.env.ImageUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luolu on 2018/11/17.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class TensorFlowHelper {

    private static final String LOG_TAG = "TensorFlowHelper";
    public static final int NUM_DETECTIONS = 10;
    public static final float IMAGE_MEAN = 128.0f;
    public static final float IMAGE_STD = 128.0f;
    // Number of threads in the java app
    public static final int NUM_THREADS = 4;
    public static final String DONKEY_DETECT_MODEL_FILE = "donkey_detection_ssdlite_mobilenet_v2_focal_192_uint8_1019.tflite";
    public static final String CATTLE_DETECT_MODEL_FILE = "cow_detect_1029_tf10.tflite";
    public static final String PIG_TFLITE_PREDICTION_MODEL_FILE = "pig_tflite_pose1022.tflite";
    public static final String PIG_TFLITE_KEYPOINTS_MODEL_FILE = "pig_1026_keypoint_tflite_xincai2.tflite";
    public static final String COW_TFLITE_PREDICTION_MODEL_FILE = "cow_rotation_1030_tf10.tflite";
    public static final String COW_TFLITE_KEYPOINTS_MODEL_FILE = "cattle_keypoint1103_xiaokuang_192_tf10_192.tflite";
    public static final String DONKEY_ROTATION_AND_KEYPOINT = "donkey_rotation_and_keypoint1017.tflite";

    /**
     * Memory-map the model file in Assets.
     */
    public static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    public static List<String> loadLabelFile(Context context, String labelFilePath) throws IOException {
        AssetManager assetManager = context.getAssets();
        ArrayList<String> result = new ArrayList<>();
        try(InputStream is = assetManager.open(labelFilePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while((line = bufferedReader.readLine()) != null){
                result.add(line);
            }
            return result;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed load label file.", e);
            throw e;
        }
    }

    public static ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap, int[] intValues, ByteBuffer imgData) {
        if (imgData ==null) {
            Log.e(LOG_TAG, "imgData as result buffer must be initialized before.");
            throw new UninitializedBufferException();
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;

        for (int i = 0; i < bitmap.getWidth(); i++){
            for (int j = 0; j < bitmap.getHeight(); j++ ){
                final int val = intValues[pixel++];
                imgData.put((byte)((val >> 16) & 0xFF));
                imgData.put((byte)((val >> 8) & 0xFF));
                imgData.put((byte)(val &0xFF));
            }
        }
        return imgData;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int inputSize) {
        Matrix frameToCropTransform = ImageUtils.getTransformationMatrix(bitmap.getWidth(), bitmap.getHeight(),
                inputSize, inputSize, 0, true);
        Matrix cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
        Bitmap resizeBitmap = Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resizeBitmap);
        canvas.drawBitmap(bitmap, frameToCropTransform, null);
        return resizeBitmap;
    }

}
