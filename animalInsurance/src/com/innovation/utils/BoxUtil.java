package com.innovation.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Trace;
import android.support.constraint.solver.widgets.Rectangle;

import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Luolu on 2018/11/19.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class BoxUtil {
    private static final Logger logger = new Logger();

    public static RectF getDesiredBox(RectF[] recModelOutput) {
        float offsetRatio = (float) Math.min((float) DetectorActivity.previewWidth, (float) DetectorActivity.previewHeight)
                / Math.max((float) DetectorActivity.previewWidth, (float) DetectorActivity.previewHeight);
        logger.i("offsetRatio %f：" + offsetRatio);
        RectF recSrc = new RectF((1 - offsetRatio) / 2 , 0, (1 + offsetRatio) / 2, 1);
        RectF resultRectF = new RectF();
        float srcLeft = recSrc.left;
        float srcTop = recSrc.top;
        float srcRight = recSrc.right;
        float srcBottom = recSrc.bottom;
        logger.i("recSrc ：" + recSrc.toString());
        logger.i("recModelOutput.length ：" + recModelOutput.length);
        int[] overlapAndBoundry = new int[recModelOutput.length];

//create bitmap
        Bitmap createBitmap = Bitmap.createBitmap(DetectorActivity.previewWidth, DetectorActivity.previewHeight, Bitmap.Config.ARGB_8888);

//        在框的边界上或边界外
        for (int i = 0; i < recModelOutput.length; i++) {
            logger.i("recModelOutput " + i + "：" + recModelOutput[i].toString());
            // draw src rect
            Canvas canvasDrawRecognition = new Canvas(createBitmap);
            com.innovation.utils.ImageUtils.drawRecognitions(canvasDrawRecognition,
                    recModelOutput[i].left, recModelOutput[i].top, recModelOutput[i].right, recModelOutput[i].bottom);

            if (recModelOutput[i].left <= srcLeft || recModelOutput[i].right >= srcRight ||
                    recModelOutput[i].top <= srcTop || recModelOutput[i].bottom >= srcBottom) {
                overlapAndBoundry[i] = 4;
            }
        }


//        save bitmap with src box
        com.innovation.utils.ImageUtils.saveBitmap(createBitmap, "srcTestBox", "srcbox.jpeg");


//        remove overlap box
        int counter = 0;
        for (int i = 0; i < recModelOutput.length; i++) {
            for (int k = i + 1; k < recModelOutput.length; k++) {
                if (recModelOutput[i] != recModelOutput[k]) {
                    //is Rectangle Overlap
                    if (isRectangleOverlap(recModelOutput[i], recModelOutput[k])) {
                        counter++;
                        overlapAndBoundry[i] = 4;
                        overlapAndBoundry[k] = 4;
                        logger.i("isRectangleOverlap ：" + counter);
                        logger.i("Overlaprec" + i + " ：" + recModelOutput[i].toString());
                        logger.i("Overlaprec" + k + " ：" + recModelOutput[k].toString());
                    }
                }
            }
        }
        for (int i = 0; i < recModelOutput.length; i++) {
            logger.i("overlap" + i + "：" + overlapAndBoundry[i]);
            if (overlapAndBoundry[i] == 4) {
                recModelOutput[i].set(0, 0, 0, 0);
            }
        }

//        near center box
        float centerX = recSrc.centerX();
        float centerY = recSrc.centerY();
        double[] distance = new double[recModelOutput.length];
        for (int i = 0; i < recModelOutput.length; i++) {
            logger.i("rest rect length ：" + recModelOutput.length);
            distance[i] = getDistance(centerX, centerY, recModelOutput[i].centerX(), recModelOutput[i].centerY());
            logger.i("rest rect  " + i + "：" + recModelOutput[i].toString());
        }
        int indexOfSmallest = getIndexOfSmallest(distance);
        logger.i("indexOfSmallest ：" + indexOfSmallest);
        resultRectF.left = recModelOutput[indexOfSmallest].left;
        resultRectF.top = recModelOutput[indexOfSmallest].top;
        resultRectF.right = recModelOutput[indexOfSmallest].right;
        resultRectF.bottom = recModelOutput[indexOfSmallest].bottom;

        //result bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(DetectorActivity.previewWidth, DetectorActivity.previewHeight, Bitmap.Config.ARGB_8888);
// draw result rect
        Canvas canvasDrawResult = new Canvas(resultBitmap);
        com.innovation.utils.ImageUtils.drawRecognitions(canvasDrawResult,
                resultRectF.left, resultRectF.top, resultRectF.right, resultRectF.bottom);
        //        save bitmap with result box
        com.innovation.utils.ImageUtils.saveBitmap(resultBitmap, "resultTestBox", "resultBox.jpeg");

        return resultRectF;
    }

    // getting the Index Of Smallest value
    public static int getIndexOfSmallest(double[] array) {
        if (array == null || array.length == 0) {
            return -1; // null or empty
        }

        int smallest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[smallest]) {
                smallest = i;
            }
        }
        return smallest;
    }

    public static boolean isRectangleOverlap(RectF rec1, RectF rec2) {
        return rec1.left < rec2.right && rec2.left < rec1.right && rec1.top < rec2.bottom && rec2.top < rec1.bottom;
    }


    public static double getDistance(float x, float y, float x2, float y2) {
        double distance;
        distance = Math.sqrt(Math.pow(Math.abs(x2 - x), 2) + Math.pow(Math.abs(y2 - y), 2));
        return distance;
    }

    public static void main(String[] args) {
//        float[] rec1 = {0,0,4,4};
//        float[] rec2 = {2,2,6,6};
//        float[] rec3 = {4,4,7,7};
//        float[] rec4 = {3,5,8,8};

//        RectF rec1 = new RectF(0, 0, 4, 4);
//        RectF rec2 = new RectF(2, 2, 6, 6);
//        RectF rec3 = new RectF(4, 4, 7, 7);
//        RectF rec4 = new RectF(3, 5, 8, 8);
//        RectF rec5 = new RectF(0, 0, 4, 4);
//        System.out.println(rec1.left);
//        System.out.println(isRectangleOverlap(rec1, rec2));

//        double[] array = {3,5,8,8,-15,185};
//        System.out.println(getIndexOfSmallest(array));


        // create instance of Random class
//        Random rand = new Random();
//        float random1 = rand.nextInt(50)/100.0f;
//        float random2 = rand.nextInt(40)/100.0f;
//        float random3 = rand.nextInt(60)/100.0f;
//        float random4 = rand.nextInt(30)/100.0f;
//        float random5 = rand.nextInt(70)/100.0f;
//        RectF rectF1 = new RectF(random1,0.1f,0.5f,0.9f);
//        RectF rectF2 = new RectF(random2,0.2f,0.6f,0.8f);
//        RectF rectF3 = new RectF(random3,0.3f,0.95f,0.85f);
//        RectF rectF4 = new RectF(random4,0.01f,0.8f,0.6f);
//        RectF rectF5 = new RectF(random5,0.05f,0.9f,0.5f);

//        RectF rectF1 = new RectF(0.2f,0.5f,0.6f,0.8f);
//        RectF rectF2 = new RectF(0.0f,0.6f,0.7f,0.87f);
//        RectF rectF3 = new RectF(0.4f,0.4f,1.0f,0.9f);
//        RectF rectF4 = new RectF(0.15f,0.35f,0.84f,0.98f);
//        RectF rectF5 = new RectF(0.15f,0.1f,0.87f,0.25f);




//        RectF[] recModelOutput = new RectF[]{rectF1,rectF2,rectF3,rectF4,rectF5};
//        RectF[] recModelOutput = new RectF[]{rectF1,rectF2,rectF3,rectF4,rectF5};
//        RectF desiredBox = BoxUtil.getDesiredBox(recModelOutput);
//        LOGGER.i("desiredBox:" + desiredBox.toString());

    }
}
