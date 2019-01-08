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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.base.InnApplication;
import com.innovation.biz.Insured.AddAnimalActivity;
import com.innovation.biz.classifier.CowFaceBoxDetector;
import com.innovation.biz.classifier.DonkeyFaceBoxDetector;
import com.innovation.biz.classifier.PigFaceBoxDetector;
import com.innovation.biz.dialog.NameVideoDialog;
import com.innovation.biz.iterm.MediaInsureItem;
import com.innovation.biz.iterm.MediaPayItem;
import com.innovation.base.Model;
import com.innovation.biz.processor.InsureDataProcessor;
import com.innovation.biz.processor.PayDataProcessor;
import com.innovation.utils.FileUtils;
import com.innovation.utils.ZipUtil;
import com.innovation.view.SendView;

import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;
import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static org.tensorflow.demo.DetectorActivity.trackingOverlay;
import static com.innovation.base.GlobalConfigure.mediaPayItem;

@SuppressLint("ValidFragment")
public class CameraConnectionFragment extends Fragment implements View.OnClickListener {
    private static final Logger LOGGER = new Logger();

    /**
     * The camera preview size will be chosen to be the smallest frame by pixel size capable of
     * containing a DESIRED_SIZE x DESIRED_SIZE square.
     */
    private static final int MINIMUM_PREVIEW_SIZE = 320;

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    private static final String FRAGMENT_DIALOG = "dialog";

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(
                        final SurfaceTexture texture, final int width, final int height) {
                    GlobalConfigure.FrameWidth = width;
                    GlobalConfigure.FrameHeight = height;
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(
                        final SurfaceTexture texture, final int width, final int height) {
                    configureTransform(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(final SurfaceTexture texture) {
                }
            };
    private boolean mManualFocusEngaged;
    private static Activity activity;
    private  CameraCharacteristics characteristics;
    public static String collectTimeStamp;
    public static TextView textAngelX;
    public static TextView textAngelY;
    private static TextView collectCostTime;
    private static long baseTimer;
    public static long videoStartTime;
    public static long caijiCostTime;
    public static long start2PauseTime;
    public static int videoTime;
    private static NameVideoDialog nameVideoDialog;
    private static String caijiName;
    private static boolean videoPaused;


    /**
     * Callback for Activities to use to initialize their data once the
     * selected preview size is known.
     */
    public interface ConnectionCallback {
        void onPreviewSizeChosen(Size size, int cameraRotation);
    }

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String cameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private static AutoFitTextureView textureView;

    private static RelativeLayout mReCordLayout;
    /**
     * Button to record video
     */
    private static TextView mRecordControl;

    private static SendView mSendView;

    //haojie add
    private RelativeLayout mToolLayout;
    private long tmieVideoStart;
    private long tmieVideoEnd;


    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession captureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice cameraDevice;

    /**
     * The rotation in degrees of the camera sensor from the display.
     */
    private Integer sensorOrientation;

    /**
     * The {@link Size} of video recording.
     */
    private Size mVideoSize;
    private Size mVideoSize_midia;
    int mVideoSize_midia_width = 0;
    int mVideoSize_midia_height = 0;
    /**
     * MediaRecorder
     */
    private static MediaRecorder mMediaRecorder;

    /**
     * Whether the app is recording video now
     */
    private static boolean mIsRecordingVideo;

    /**
     * The {@link Size} of camera preview.
     */
    private Size previewSize;

    // private Model mModel = Model.BUILD;

    /**
     * {@link CameraDevice.StateCallback}
     * is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback stateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(final CameraDevice cd) {
                    // This method is called when the camera is opened.  We start camera preview here.
                    cameraOpenCloseLock.release();
                    cameraDevice = cd;
                    startPreview();
                    if (null != textureView) {
                        configureTransform(textureView.getWidth(), textureView.getHeight());
                    }
                }

                @Override
                public void onDisconnected(final CameraDevice cd) {
                    cameraOpenCloseLock.release();
                    cd.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(final CameraDevice cd, final int error) {
                    cameraOpenCloseLock.release();
                    cd.close();
                    cameraDevice = null;
                    final Activity activity = getActivity();
                    if (null != activity) {
                        activity.finish();
                    }
                }
            };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread backgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler backgroundHandler;

    /**
     * An {@link ImageReader} that handles preview frame capture.
     */
    private ImageReader previewReader;

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder previewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #previewRequestBuilder}
     */
    private CaptureRequest previewRequest;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private final Semaphore cameraOpenCloseLock = new Semaphore(1);

    /**
     * A {@link OnImageAvailableListener} to receive frames as they are available.
     */
    private final OnImageAvailableListener imageListener;

    /**
     * The input size in pixels desired by TensorFlow (width and height of a square bitmap).
     */
    private final Size inputSize;

    /**
     * The layout identifier to inflate for this Fragment.
     */
    private final int layout;


    private final ConnectionCallback cameraConnectionCallback;

    CameraConnectionFragment(
            final ConnectionCallback connectionCallback,
            final OnImageAvailableListener imageListener,
            final int layout, final Size inputSize) {
        this.cameraConnectionCallback = connectionCallback;
        this.imageListener = imageListener;
        this.layout = layout;
        this.inputSize = inputSize;
    }

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    public static void showToast(final String text) {
//        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
//            if (size.getWidth() == size.getWidth() && size.getWidth() == size.getHeight()) {
                LOGGER.e("suitable video size：" + size);
                return size;
            }
                LOGGER.e("size：" + size);
        }
        LOGGER.e("Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    protected static Size chooseOptimalSize(final Size[] choices, final int width, final int height) {
        final int minSize = Math.max(Math.min(width, height), MINIMUM_PREVIEW_SIZE);
        final Size desiredSize = new Size(width, height);

        // Collect the supported resolutions that are at least as big as the preview Surface
        boolean exactSizeFound = false;
        final List<Size> bigEnough = new ArrayList<Size>();
        final List<Size> tooSmall = new ArrayList<Size>();
        for (final Size option : choices) {
            if (option.equals(desiredSize)) {
                // Set the size but don't return yet so that remaining sizes will still be logged.
                exactSizeFound = true;
            }

            if (option.getHeight() >= minSize && option.getWidth() >= minSize) {
                bigEnough.add(option);
            } else {
                tooSmall.add(option);
            }
        }

        LOGGER.i("Desired size: " + desiredSize + ", min size: " + minSize + "x" + minSize);
        LOGGER.i("Valid preview sizes: [" + TextUtils.join(", ", bigEnough) + "]");
        LOGGER.i("Rejected preview sizes: [" + TextUtils.join(", ", tooSmall) + "]");

        if (exactSizeFound) {
            LOGGER.i("Exact size match found.");
            return desiredSize;
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            final Size chosenSize = Collections.min(bigEnough, new CompareSizesByArea());
            LOGGER.i("Chosen size: " + chosenSize.getWidth() + "x" + chosenSize.getHeight());
            return chosenSize;
        } else {
            LOGGER.e("Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static CameraConnectionFragment newInstance(
            final ConnectionCallback callback,
            final OnImageAvailableListener imageListener, final int layout, final Size inputSize) {
        return new CameraConnectionFragment(callback, imageListener, layout, inputSize);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CameraConntFragment:", "CameraConnectionFragment onDestroy()!");
        Activity activity = getActivity();
        InsureDataProcessor.getInstance(activity).handleMediaResource_destroy();
        if (null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        textAngelX = view.findViewById(R.id.textAngelX);
        textAngelY = view.findViewById(R.id.textAngelY);
        collectCostTime = view.findViewById(R.id.collectCostTime);
        baseTimer = SystemClock.elapsedRealtime();
        new Timer("Timer").scheduleAtFixedRate(new TimerTask() {
       @Override
       public void run() {
                        int time = (int)((SystemClock.elapsedRealtime() - baseTimer) / 1000);
                        String hh = new DecimalFormat("00").format(time / 3600);
                        String mm = new DecimalFormat("00").format(time % 3600 / 60);
                        String ss = new DecimalFormat("00").format(time % 60);
           videoTime = Integer.parseInt(mm);
                        String timeFormat = new String(mm + ":" + ss);
                        Message msg = new Message();
                        msg.obj = timeFormat;
                        startTimehandler.sendMessage(msg);
                    }

              }, 0, 1000L);

        mReCordLayout = (RelativeLayout) view.findViewById(R.id.record_layout);
        mRecordControl = (TextView) view.findViewById(R.id.record_control);
        mRecordControl.setOnClickListener(this);

        activity = getActivity();
        if (GlobalConfigure.mediaInsureItem == null) {
            GlobalConfigure.mediaInsureItem = new MediaInsureItem(activity);
        }

        if (GlobalConfigure.model == Model.BUILD.value()) {
            GlobalConfigure.UPLOAD_VIDEO_FLAG = false;
//            InsureDataProcessor.getInstance(activity).handleMediaResource_build(activity);
            GlobalConfigure.mediaInsureItem.currentDel();
            GlobalConfigure.mediaInsureItem.currentInit();
        }
        nameVideoDialog = new NameVideoDialog(getActivity());
        videoPaused = false;
    }

    final Handler startTimehandler = new Handler(){
        public void handleMessage(Message msg) {
            if (null != collectCostTime) {
                collectCostTime.setText((String) msg.obj);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_control:
                if (mIsRecordingVideo) {
                    tmieVideoEnd = System.currentTimeMillis();
                    long during = tmieVideoEnd - tmieVideoStart;
                    stopRecordingVideo(false);
                    GlobalConfigure.VIDEO_PROCESS = false;
                } else {
                    try {
                        GlobalConfigure.VIDEO_PROCESS = true;
                        startRecordingVideo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tmieVideoStart = System.currentTimeMillis();
                }
                break;
            case R.id.record_switch:
                if (GlobalConfigure.model != Model.BUILD.value()) {
                    GlobalConfigure.model = Model.BUILD.value();
                }
                break;
            case R.id.record_verify:
                if (GlobalConfigure.model != Model.VERIFY.value()) {
                    GlobalConfigure.model = Model.VERIFY.value();
                }
                break;
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(final int width, final int height) {
        final Activity activity = getActivity();
        final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (final String cameraId : manager.getCameraIdList()) {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//                characteristics = manager.getCameraCharacteristics(cameraId);

                final StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // We don't use a front facing camera in this sample.
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
//                LOGGER.i("map："+ map.toString());
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                final Size largest =
                        Collections.max(
                                Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                                new CompareSizesByArea());

                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);//获取摄像头的旋转角度

                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));//map.getOutputSizes(MediaRecorder.class)--获取图片输出的尺寸
                previewSize =
                        chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                                inputSize.getWidth(),
                                inputSize.getHeight());

                mVideoSize_midia = mVideoSize;

                mVideoSize_midia_width = mVideoSize.getWidth();
                mVideoSize_midia_height = mVideoSize.getHeight();

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                final int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    textureView.setAspectRatio(width, height);
                    textureView.setAspectRatio(3, 4);
                } else {
                    textureView.setAspectRatio(width, height);
//                    textureView.setAspectRatio(3, 4);
                }

                CameraConnectionFragment.this.cameraId = cameraId;
                cameraConnectionCallback.onPreviewSizeChosen(previewSize, 0);
                return;
            }

        } catch (final CameraAccessException e) {
            LOGGER.e(e, "Exception!");
        } catch (final NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }

    }

    /**
     * Opens the camera specified by {@link CameraConnectionFragment#cameraId}.
     */
    @SuppressLint("MissingPermission")
    private void openCamera(final int width, final int height) {
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        mMediaRecorder = new MediaRecorder();
        final Activity activity = getActivity();
        final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            manager.openCamera(cameraId, stateCallback, null);

        } catch (final CameraAccessException e) {
            LOGGER.e(e, "Exception!");
        } catch (final InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }

    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != previewReader) {
                previewReader.close();
                previewReader = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (final InterruptedException e) {
            showToast("lock camera failed!");
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("ImageListener");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (final InterruptedException e) {
            LOGGER.e(e, "Exception!");
        }
    }

    private final CameraCaptureSession.CaptureCallback captureCallback =
            new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureProgressed(
                        final CameraCaptureSession session,
                        final CaptureRequest request,
                        final CaptureResult partialResult) {
                }

                @Override
                public void onCaptureCompleted(
                        final CameraCaptureSession session,
                        final CaptureRequest request,
                        final TotalCaptureResult result) {
                }
            };

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void startPreview() {
        if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
            return;
        }
        try {
            closePreviewSession();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            final Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            LOGGER.i("Opening camera preview: " + previewSize.getWidth() + "x" + previewSize.getHeight());

            // Create the reader for the preview frames.
            previewReader =
                    ImageReader.newInstance(
                            previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);

            previewReader.setOnImageAvailableListener(imageListener, backgroundHandler);
            previewRequestBuilder.addTarget(previewReader.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDevice.createCaptureSession(
                    Arrays.asList(surface, previewReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(final CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == cameraDevice) {
                                return;
                            }
                            // When the session is ready, we start displaying the preview.
                            captureSession = cameraCaptureSession;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(final CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed----------");
                        }
                    },
                    null);
        } catch (final CameraAccessException e) {
            LOGGER.e(e, "Exception!");
        }
    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == cameraDevice) {
            return;
        }

        try {
            // Auto focus should be continuous for camera preview.
            previewRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
            // Finally, we start displaying the camera preview.
            previewRequest = previewRequestBuilder.build();
            captureSession.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(final int viewWidth, final int viewHeight) {
        final Activity activity = getActivity();
        if (null == textureView || null == previewSize || null == activity) {
            return;
        }
        final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        final Matrix matrix = new Matrix();
        final RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        final RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        final float centerX = viewRect.centerX();
        final float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            final float scale =
                    Math.max(
                            (float) viewHeight / previewSize.getHeight(),
                            (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (GlobalConfigure.model == Model.BUILD.value()) {
            GlobalConfigure.VideoFileName = GlobalConfigure.mediaInsureItem.getVideoFileName();
        } else if (GlobalConfigure.model == Model.VERIFY.value()) {
            GlobalConfigure.VideoFileName = mediaPayItem.getVideoFileName();
        }
        mMediaRecorder.setOutputFile(GlobalConfigure.VideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(1300000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(1280, 960);

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            mMediaRecorder.release();
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            mMediaRecorder.release();
        }


    }

    private void startRecordingVideo(){
        if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
            return;
        }
        GlobalConfigure.VIDEO_PROCESS = true;
        videoStartTime = SystemClock.uptimeMillis();

        try {
            closePreviewSession();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setUpMediaRecorder();
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            List<Surface> surfaces = new ArrayList<>();
            // Set up Surface for the camera preview
            Surface textureSurface = new Surface(texture);
            previewReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
            previewReader.setOnImageAvailableListener(imageListener, backgroundHandler);
            Surface imageSurface = previewReader.getSurface();

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            previewRequestBuilder.addTarget(textureSurface);
            previewRequestBuilder.addTarget(recorderSurface);
            previewRequestBuilder.addTarget(imageSurface);
            List<Surface> surfaceList = Arrays.asList(textureSurface, recorderSurface, imageSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            cameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == cameraDevice) {
                        return;
                    }
                    captureSession = cameraCaptureSession;
                    updatePreview();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (InnApplication.touBaoVieoFlag.trim().equals("1") || InnApplication.liPeiVieoFlag.trim().equals("1")) {
                                try {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    // UI
                                    mRecordControl.setText(R.string.stop);
                                    mIsRecordingVideo = true;
                                    // Start recording
                                    mMediaRecorder.start();
                                    // disable switch action

                                }catch (Exception e){
                                    e.printStackTrace();
                                    showToast("视频录制异常！");
                                }

                            } else {
                                // UI
                                mRecordControl.setText(R.string.stop);
                                mIsRecordingVideo = true;
                                try {
                                    mMediaRecorder.prepare();
                                } catch (IllegalStateException e) {
                                    Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
                                } catch (IOException e) {
                                    Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
                                }

                            }


                        }
                    });
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Camera Failed!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("视频录制异常！");
        }

    }

    private void stopRecordingVideo(boolean save) {
        // UI
        mMediaRecorder = new MediaRecorder();
        mIsRecordingVideo = false;
        mRecordControl.setText(R.string.record);

        mMediaRecorder.reset();
        GlobalConfigure.VIDEO_PROCESS = false;
        startPreview();
        videoPaused = true;
        start2PauseTime = SystemClock.uptimeMillis() - videoStartTime;
    }

    private void closePreviewSession() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(final Size lhs, final Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(final String message) {
            final ErrorDialog dialog = new ErrorDialog();
            final Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, final int i) {
                                    activity.finish();
                                }
                            })
                    .create();
        }
    }


    // TODO: 2018/8/7 By:LuoLu
    @SuppressLint("HandlerLeak")
    public static Handler collectNumberHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        baseTimer = SystemClock.elapsedRealtime();
                        if (mMediaRecorder == null) {
                            mMediaRecorder = new MediaRecorder();
                        }
                        GlobalConfigure.VIDEO_PROCESS = false;
                        mReCordLayout.setVisibility(View.GONE);
                        mIsRecordingVideo = false;
                        mRecordControl.setText(R.string.record);
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                        if (InnApplication.touBaoVieoFlag.trim().equals("0") || InnApplication.liPeiVieoFlag.trim().equals("0")) {
                            if (!TextUtils.isEmpty(GlobalConfigure.VideoFileName)) {
                                boolean deleteResult = FileUtils.deleteFile(new File(GlobalConfigure.VideoFileName));
                                if (deleteResult == true) {
                                    LOGGER.i("collectNumberHandler录制视频删除成功！");
                                }
                            }
                        }

                        if (videoPaused){
                            caijiCostTime = SystemClock.uptimeMillis() - videoStartTime + start2PauseTime;
                        }else {
                            caijiCostTime = SystemClock.uptimeMillis() - videoStartTime;
                        }
                        LOGGER.i("caijiCostTime %f：" + caijiCostTime);
                        FileUtils.generateCostTimeTxt("采集时间：" + String.valueOf(caijiCostTime) + "ms");
                        View.OnClickListener nextClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameVideoDialog.dismiss();
                                caijiName = NameVideoDialog.nameVideo.getText().toString();
                                collectNumberHandler.sendEmptyMessage(5);
                            }
                        };
                        nameVideoDialog.setPositiveButton("确认",nextClickListener);
                        nameVideoDialog.show();
                    } catch (Exception e) {
                        showToast("相机状态异常！");
                    }
                    break;

                case 2:
                    baseTimer = SystemClock.elapsedRealtime();
                    if (mReCordLayout != null) {
                        mReCordLayout.setVisibility(View.VISIBLE);
                    }
                    new DetectorActivity().reInitCurrentCounter(0, 0, 0);
                    if (activity != null) {
                        new MultiBoxTracker(activity).reInitCounter(0, 0, 0);
                    }
                    if (trackingOverlay != null) {
                        trackingOverlay.refreshDrawableState();
                        trackingOverlay.invalidate();
                    }
                    if (textureView != null) {
                        textureView.refreshDrawableState();
                    }
                    LOGGER.i("collectNumberHandler Message 2！");

                    break;
                case 3:
                    showToast("采集时间过长，已自动保存该视频");
                    if (mMediaRecorder == null) {
                        mMediaRecorder = new MediaRecorder();
                    }
                    GlobalConfigure.VIDEO_PROCESS = false;
                    mReCordLayout.setVisibility(View.VISIBLE);
                    mIsRecordingVideo = false;
                    mRecordControl.setText(R.string.record);
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    SimpleDateFormat tmpSimpleDateFormat = new SimpleDateFormat("yyyyMMddhhmm", Locale.getDefault());
                    String tempName =  tmpSimpleDateFormat.format(new Date(System.currentTimeMillis()));
                    String videoDri = GlobalConfigure.mediaInsureItem.getVideoDir();
                    String zipFailVideoDir  = GlobalConfigure.mediaInsureItem.getmTestVideoFailedDir();
                    File videoDir_new = new File(videoDri);//视频目录下的文件
                    File[] files_video = videoDir_new.listFiles();

                    if (files_video == null) {
                        return;
                    }
                    if (files_video.length == 0) {
                        return;
                    }

                    File[] fs_video = new File[files_video.length + 1];
                    for (int i = 0; i < files_video.length; i++) {
                        fs_video[i] = files_video[i];
                    }
                    File file_current = new File(zipFailVideoDir);
                    File zipVideo = new File(file_current, tempName + ".zip");
                    ZipUtil.zipFiles(fs_video, zipVideo);
                    baseTimer = SystemClock.elapsedRealtime();

                    break;
                case 4:
                    SimpleDateFormat tmpSimpleDateFormat1 = new SimpleDateFormat("yyyyMMddhhmm", Locale.getDefault());
                    String tempName1 =  tmpSimpleDateFormat1.format(new Date(System.currentTimeMillis()));
                    String videoDri1 = GlobalConfigure.mediaInsureItem.getVideoDir();
                    String zipSucessVideoDir = GlobalConfigure.mediaInsureItem.getmTestVideoSuccessDir();
                    File videoDir_new1 = new File(videoDri1);//视频目录下的文件
                    File[] srcVideo = videoDir_new1.listFiles();

                    if (srcVideo == null) {
                        return;
                    }
                    if (srcVideo.length == 0) {
                        return;
                    }

                    File[] sVideo = new File[srcVideo.length + 1];
                    for (int i = 0; i < srcVideo.length; i++) {
                        sVideo[i] = srcVideo[i];
                    }
                    File current = new File(zipSucessVideoDir);
                    File zipVideo1 = new File(current, tempName1 + ".zip");
                    ZipUtil.zipFiles(sVideo, zipVideo1);
                    baseTimer = SystemClock.elapsedRealtime();
                    break;
                case 5:
                    new Thread(new Task()).start();
                    collectNumberHandler.sendEmptyMessage(6);
                    break;

                case 6:
                    AlertDialog.Builder builder24 = new AlertDialog.Builder(activity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("保存成功！")
                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    activity.finish();

                                }
                            })
                            .setPositiveButton("下一头", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(activity, DetectorActivity.class);
                                    activity.startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                }
                            });
                    builder24.create();
                    builder24.setCancelable(false);
                    builder24.show();
                    break;

                default:
                    break;
            }
        }

    };

    static class Task implements Runnable {
        @Override
        public void run() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            String fname = simpleDateFormat.format(new Date(System.currentTimeMillis()));
            File file = new File("/sdcard/Android/data/com.innovation.animal_model_test/cache/innovation/animal/投保/");
            File fileNew = new File("/sdcard/Android/data/com.innovation.animal_model_test/cache/innovation/animal/");

            boolean zipResult = ZipUtil.zipFile(file.getAbsolutePath(),
                    fileNew.getPath() + "/" + caijiName + "_" + fname + ".zip");
            if (zipResult == true){
                FileUtils.deleteFile(file);
                GlobalConfigure.mediaInsureItem = new MediaInsureItem(activity);
                GlobalConfigure.mediaInsureItem.currentDel();
                GlobalConfigure.mediaInsureItem.currentInit();
                GlobalConfigure.mediaInsureItem.getmTestDir();
                GlobalConfigure.mediaInsureItem.getVideoDir();
                GlobalConfigure.mediaInsureItem.getmTestVideoSuccessDir();
                GlobalConfigure.mediaInsureItem.getmTestVideoFailedDir();
            }
        }
    }

}
