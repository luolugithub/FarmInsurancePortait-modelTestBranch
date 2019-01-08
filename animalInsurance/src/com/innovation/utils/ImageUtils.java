package com.innovation.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;

import com.innovation.base.InnApplication;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;

import com.innovation.base.GlobalConfigure;

import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.tensorflow.demo.tracking.MultiBoxTracker.TEXT_SIZE_DIP;

/**
 * Author by luolu, Date on 2018/10/31.
 * COMPANY：InnovationAI
 */

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static Logger sLogger = new Logger("ImageUtils");

    public static boolean isBlurByOpenCV(Bitmap image) {
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY); // 图像灰度化

        Mat laplacianImage = new Mat();
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U); // 拉普拉斯变换

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stdDev = new MatOfDouble();
        Core.meanStdDev(laplacianImage, mean, stdDev);

        matImage.release();
        laplacianImage.release();

        float blur = (float) (stdDev.get(0, 0)[0] * stdDev.get(0, 0)[0]);
        sLogger.i("mean: " + mean.get(0, 0)[0] + " stdDev: " + stdDev.get(0, 0)[0] + " blur: " + blur);
        return blur < 250;
    }

    public static boolean isBlurByOpenCV_new(Bitmap image) {
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY); // 图像灰度化

        Mat laplacianImage = new Mat();
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U); // 拉普拉斯变换

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stdDev = new MatOfDouble();
        Core.meanStdDev(laplacianImage, mean, stdDev);

        matImage.release();
        laplacianImage.release();

        float blur = (float) (stdDev.get(0, 0)[0] * stdDev.get(0, 0)[0]);
//        sLogger.i("mean: " + mean.get(0, 0)[0] + " stdDev: " + stdDev.get(0, 0)[0] + " blur: " + blur);
        Log.d(TAG, "图像imgae-------blur test --set_checkcount----------blur = " + blur);
        return blur < 20;
    }

    //end add
    // TODO: 2018/9/15 By:LuoLu
    public static boolean isBlurredImage(Bitmap image) {
        try {
            if (image != null) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inDither = true;
                opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                int l = CvType.CV_8UC1;
                Mat matImage = new Mat();
                Utils.bitmapToMat(image, matImage);
                Mat matImageGrey = new Mat();
                Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

                Mat dst2 = new Mat();
                Utils.bitmapToMat(image, dst2);

                Mat laplacianImage = new Mat();
                dst2.convertTo(laplacianImage, l);
                Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
                Mat laplacianImage8bit = new Mat();
                laplacianImage.convertTo(laplacianImage8bit, l);
                System.gc();

                Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(),
                        laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(laplacianImage8bit, bmp);

                int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
                        bmp.getHeight());
                if (bmp != null)
                    if (!bmp.isRecycled()) {
                        bmp.recycle();

                    }
                int maxLap = -16777216;

                for (int i = 0; i < pixels.length; i++) {

                    if (pixels[i] > maxLap) {
                        maxLap = pixels[i];
                    }
                }
                int soglia = -6118750;

                if (maxLap < soglia || maxLap == soglia) {


                    return true;
                } else {


                    return false;
                }
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (OutOfMemoryError e) {
            return false;
        }
    }

    public static boolean isBlurByOpenCV(String picFilePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 通过path得到一个不超过2000*2000的Bitmap
        Bitmap image = decodeSampledBitmapFromFile(picFilePath, options, 2000, 2000);
        return isBlurByOpenCV(image);
    }

    private static Bitmap decodeSampledBitmapFromFile(String imgPath, BitmapFactory.Options options, int reqWidth, int reqHeight) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        // inSampleSize为缩放比例，举例：options.inSampleSize = 2表示缩小为原来的1/2，3则是1/3，以此类推
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
        sLogger.d("inSampleSize=" + inSampleSize);
        return inSampleSize;
    }

    public static void drawRecognitions(
            Canvas canvas, float left, float top, float right, float bottom) {
        Paint boxPaint = new Paint();
        Paint textPaint;
        float textSizePx;
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(6.0f);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        boxPaint.setStrokeJoin(Paint.Join.ROUND);
        boxPaint.setStrokeMiter(100);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, InnApplication.getAppContext().getResources().getDisplayMetrics());
        textPaint = new Paint((int) textSizePx);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(30);
        left = left * canvas.getWidth();
        right = right * canvas.getWidth();
        top = top * canvas.getHeight();
        bottom = bottom * canvas.getHeight();
        sLogger.i("drawRecognitions %f:" + canvas.getWidth() + "*" + canvas.getHeight() + ", "
                + "location = (" + left + "," + top + ")(" + right + "," + bottom + ")");
        canvas.drawRect(left, top, right, bottom, boxPaint);

//        canvas.drawText("luolu", canvas.getWidth() - 100,
//                canvas.getHeight() - 100, textPaint);

    }

    public static void drawText(
            Canvas canvas,
            String string1, int x1, int y1,
            String string2, int x2, int y2,
            String string3, int x3, int y3) {
        float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 6, InnApplication.getAppContext().getResources().getDisplayMetrics());
        BorderedText borderedText = new BorderedText(textSizePx);
        borderedText.setInteriorColor(Color.RED);

        borderedText.drawText(canvas, x1, y1, string1);
        borderedText.drawText(canvas, x2, y2, string2);
        borderedText.drawText(canvas, x3, y3, string3);

    }

    public static void drawKeypoints(Canvas canvas, PointFloat point, int size, String string) {
        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(8.0f);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        boxPaint.setStrokeJoin(Paint.Join.ROUND);
        boxPaint.setStrokeMiter(100);
        boxPaint.setColor(Color.YELLOW);

        float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8, InnApplication.getAppContext().getResources().getDisplayMetrics());
        BorderedText borderedText = new BorderedText(textSizePx);
        borderedText.setInteriorColor(Color.RED);

        if (point.getX() < 1
                && point.getY() < 1
                && point.getX() > 0
                && point.getY() > 0) {
            canvas.drawCircle(point.getX() * size, point.getY() * size, 3, boxPaint);
            borderedText.drawText(canvas, point.getX() * size, point.getY() * size, string);
        }
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.i(TAG, w + "---------------" + h);
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 360f;
        float ww = 320f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
        //return bitmap;
    }


    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * Creates and returns a new bitmap which is the same as the provided bitmap
     * but with horizontal or vertical padding (if necessary)
     * either side of the original bitmap
     * so that the resulting bitmap is a square.
     *
     * @param bitmap is the bitmap to pad.
     * @return the padded bitmap.
     */
    public static Bitmap padBitmap(Bitmap bitmap) {
        int paddingX;
        int paddingY;

        if (bitmap.getWidth() == bitmap.getHeight()) {
            paddingX = 0;
            paddingY = 0;
        } else if (bitmap.getWidth() > bitmap.getHeight()) {
            paddingX = 0;
            paddingY = bitmap.getWidth() - bitmap.getHeight();
        } else {
            paddingX = bitmap.getHeight() - bitmap.getWidth();
            paddingY = 0;
        }

        Bitmap paddedBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + paddingX,
                bitmap.getHeight() + paddingY,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0x00, 0x00, 0x00, 0x00); // this represents color
        canvas.drawBitmap(
                bitmap,
                paddingX / 2,
                paddingY / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));

        return paddedBitmap;
    }

    public static Bitmap padColorBitmap(Bitmap bitmap, float ratio) {
        int paddingW = 0;
        int paddingH = 0;
        float paddedWHRatio;
        float srcWHRatio;
        srcWHRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        sLogger.i("srcWHRatio %f=" + srcWHRatio);
        if (srcWHRatio == ratio) {
            return bitmap;
        }
        if (bitmap.getWidth() > bitmap.getHeight()) {
            paddingW = 0;
            paddingH = (int) (bitmap.getWidth() / ratio) - bitmap.getHeight();
            sLogger.i("paddingH=" + paddingH);
        } else if (bitmap.getHeight() > bitmap.getWidth()) {
            paddingH = 0;
            paddingW = (int) (bitmap.getHeight() * ratio) - bitmap.getWidth();
            sLogger.i("paddingW=" + paddingW);
        }

        Bitmap paddedBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + paddingW,
                bitmap.getHeight() + paddingH,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0x00, 0x00, 0x06, 0xff); // this represents color
        canvas.drawBitmap(
                bitmap,
                paddingW / 2,
                paddingH / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));


        paddedWHRatio = (float) paddedBitmap.getWidth() / (float) paddedBitmap.getHeight();
        sLogger.i("paddedWHRatio %f=" + paddedWHRatio);

        return paddedBitmap;
    }

    public static Bitmap padBitmap2SpRatio(Bitmap bitmap, float ratio) {
        int paddingW = 0;
        int paddingH = 0;
        float paddedWHRatio;
        float srcWHRatio;
        srcWHRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        sLogger.i("srcWHRatio %f=" + srcWHRatio);
        if (srcWHRatio == ratio) {
            return bitmap;
        }
        if (bitmap.getWidth() > bitmap.getHeight()) {
            paddingW = 0;
            paddingH = (int) (bitmap.getWidth() / ratio) - bitmap.getHeight();
            sLogger.i("paddingH=" + paddingH);
        } else if (bitmap.getHeight() > bitmap.getWidth()) {
            paddingH = 0;
            paddingW = (int) (bitmap.getHeight() * ratio) - bitmap.getWidth();
            sLogger.i("paddingW=" + paddingW);
        }

        Bitmap paddedBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + paddingW,
                bitmap.getHeight() + paddingH,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0x00, 0x00, 0x00, 0x00); // this represents color
        canvas.drawBitmap(
                bitmap,
                paddingW / 2,
                paddingH / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));


        paddedWHRatio = (float) paddedBitmap.getWidth() / (float) paddedBitmap.getHeight();
        sLogger.i("paddedWHRatio %f=" + paddedWHRatio);

        return paddedBitmap;
    }

    public static Bitmap clipBitmap(Bitmap bitmap, float X0, float Y0, float X1, float Y1, float multiple) {
        Bitmap clipBitmap = null;
        Canvas canvasClipRecognition = new Canvas(bitmap);
        int padSize = Math.max(canvasClipRecognition.getHeight(), canvasClipRecognition.getWidth());
        float left = X0 * canvasClipRecognition.getWidth();
        float top = Y0 * canvasClipRecognition.getHeight();
        float right = X1 * canvasClipRecognition.getWidth();
        float bottom = Y1 * canvasClipRecognition.getHeight();
        int multiX = (int) ((multiple + 1) * left - (multiple - 1) * right) / 2;
        int multiY = (int) ((multiple + 1) * top - (multiple - 1) * bottom) / 2;
        int multiX1 = (int) ((multiple + 1) * right - (multiple - 1) * left) / 2;
        int multiY1 = (int) ((multiple + 1) * bottom - (multiple - 1) * top) / 2;
        if (multiX < 0) {
            multiX = 0;
        }
        if (multiY < 0) {
            multiY = 0;
        }
        if (multiX1 > padSize) {
            multiX1 = padSize;
        }
        if (multiY1 > padSize) {
            multiY1 = padSize;
        }
        clipBitmap = Bitmap.createBitmap(bitmap,
                 multiX, multiY,
                multiX1 - multiX,
                multiY1 - multiY);

        return clipBitmap;
    }

    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {

        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);

        byte[] jdata = baos.toByteArray();

        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();

        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);

        return bmp;

    }


    public static Bitmap createBitmapThumbnail(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 360;
        int newHeight = 480;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        return newBitMap;
    }


    public static Bitmap rotateBitmap(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    public static File saveImage(Bitmap bmp) {
        File appDir = new File(new File(Environment.getExternalStorageDirectory(), "innovation"), "test1031");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        SimpleDateFormat tmpSimpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS", Locale.getDefault());
        String fileName = tmpSimpleDateFormat.format(new Date(System.currentTimeMillis())) + ".jpeg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sLogger.i("Lu,save to local,path: " + appDir.toString());
        return file;
    }


    /**
     * 缩放图片
     *
     * @param
     */
    public static Bitmap getPostScaleBitmap(Bitmap bitmap) {
        // Matrix类进行图片处理（缩小或者旋转）
        Matrix matrix = new Matrix();
        // 根据指定高度宽度缩放
        matrix.postScale(0.05f, 0.05f);
        // 生成新的图片
        try {
            Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            if (dstbmp != null) {
                return dstbmp;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            Log.d(TAG, "图像imgae----getPostScaleBitmap except===" + s);
            return null;
        }
        return null;
    }

    //获取图片亮度值
    public static int getImageBright(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int r, g, b;
        int count = 0;
        int bright = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count++;
                int localTemp = bm.getPixel(i, j);
                r = (localTemp | 0xff00ffff) >> 16 & 0x00ff;
                g = (localTemp | 0xffff00ff) >> 8 & 0x0000ff;
                b = (localTemp | 0xffffff00) & 0x0000ff;
                bright = (int) (bright + 0.299 * r + 0.587 * g + 0.114 * b);
            }
        }
        return bright / count;
    }

    //检测亮度
    public static int checkImageBright(Bitmap bitmap) {
        //对图像进行模糊度，明暗度判断
        //先缩放再获得亮度
        Bitmap checkBitmap = ImageUtils.getPostScaleBitmap(bitmap);
        long time0 = System.currentTimeMillis();
        int bitBright = ImageUtils.getImageBright(checkBitmap);
        long time1 = System.currentTimeMillis();
        Log.d(TAG, "图像imgae----bitBright===" + bitBright + "--spent time ====" + (time1 - time0));
        return bitBright;
    }

    /*
     *@bitmapFileName,save bitmap with specified name
     * @childFileName,file dir .../Test
     */
    public static File saveBitmap(Bitmap bmp, String childFileName, String bitmapFileName) {
        File appDir = new File(GlobalConfigure.mediaInsureItem.getmTestDir(), childFileName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, bitmapFileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sLogger.i("Lu,save to local,path: " + appDir.toString());
        return file;
    }

    public static Bitmap drawEmptyBitmapWithExcel() {
        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(2.0f);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        boxPaint.setStrokeJoin(Paint.Join.ROUND);
        boxPaint.setStrokeMiter(100);
        boxPaint.setColor(Color.WHITE);

        float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8, InnApplication.getAppContext().getResources().getDisplayMetrics());
        BorderedText borderedText = new BorderedText(textSizePx);
        borderedText.setInteriorColor(Color.RED);
        int w = 600, h = 800;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bitmap);
        float startX = 0, startY = 0;
        float startY1 = h / 5, startY2 = h * 2 / 5, startY3 = h * 3 / 5, startY4 = h * 4 / 5;
        float startX1 = w / 4, startX2 = w * 2 / 4, startX3 = w * 3 / 4;
        float stopY1 = w / 4, stopY2 = w * 2 / 4, stopY3 = w * 3 / 4;
        float[] lineX1 = {startX, startY1, w, startY1};
        float[] lineX2 = {startX, startY2, w, startY2};
        float[] lineX3 = {startX, startY3, w, startY3};
        float[] lineX4 = {startX, startY4, w, startY4};
        float[] lineY1 = {startX1, startY, stopY1, h};
        float[] lineY2 = {startX2, startY, stopY2, h};
        float[] lineY3 = {startX3, startY, stopY3, h};

        canvas.drawLines(lineX1, boxPaint);
        canvas.drawLines(lineX2, boxPaint);
        canvas.drawLines(lineX3, boxPaint);
        canvas.drawLines(lineX4, boxPaint);
        canvas.drawLines(lineY1, boxPaint);
        canvas.drawLines(lineY2, boxPaint);
        canvas.drawLines(lineY3, boxPaint);
        borderedText.drawText(canvas, 150, 160, "(150,160)");
        borderedText.drawText(canvas, 300, 160, "(300,160)");
        borderedText.drawText(canvas, 450, 160, "(450,160)");
        borderedText.drawText(canvas, 150, 320, "(150,320)");
        borderedText.drawText(canvas, 300, 320, "(300,320)");
        borderedText.drawText(canvas, 450, 320, "(450,320)");
        borderedText.drawText(canvas, 150, 480, "(150,480)");
        borderedText.drawText(canvas, 300, 480, "(300,480)");
        borderedText.drawText(canvas, 450, 480, "(450,480)");
        borderedText.drawText(canvas, 150, 640, "(150,640)");
        borderedText.drawText(canvas, 300, 640, "(300,640)");
        borderedText.drawText(canvas, 450, 640, "(450,640)");
        borderedText.drawText(canvas, 0, 160, "(0,160)");
        borderedText.drawText(canvas, 0, 320, "(0,320)");
        borderedText.drawText(canvas, 0, 480, "(0,480)");
        borderedText.drawText(canvas, 0, 640, "(0,640)");


        return bitmap;
    }
}
