package com.innovation.biz.processor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.base.InnApplication;
import com.innovation.bean.PayApplyResultBean;
import com.innovation.bean.PayImageUploadResultBean;
import com.innovation.bean.PayInfoContrastResultBean;
import com.innovation.bean.ResultBean;
import com.innovation.biz.dialog.InsureDialog;
import com.innovation.biz.dialog.LipeiResultDialog;
import com.innovation.biz.dialog.ReviewImageDialog;
import com.innovation.biz.dialog.ReviewVideoDialog;
import com.innovation.base.Model;
import com.innovation.location.LocationManager;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.Utils;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.PreferencesUtils;
import com.innovation.utils.ZipUtil;

import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.getStringTouboaExtra;
import static com.innovation.base.InnApplication.getlipeiTempNumber;
import static com.innovation.utils.HttpUtils.PAY_LIBUPLOAD;
import static com.innvocation.upload.UploadHelper.getEnvInfo;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;
import static org.tensorflow.demo.DetectorActivity.type1Count;
import static org.tensorflow.demo.DetectorActivity.type2Count;
import static org.tensorflow.demo.DetectorActivity.type3Count;

/**
 * Author by luolu, Date on 2018/9/18.
 * COMPANY：InnovationAI
 */

public class PayDataProcessor {
    private static final int MSG_PROCESSOR_ZIP = 101;
    private static final int MSG_PROCESSOR_UPLOAD_PAY_ONE = 103;
    private static final int MSG_PROCESSOR_TEST = 105;
    private static final int MSG_PROCESSOR_UPLOAD_IMAGEONE = 106;

    private static final int MSG_UI_PROGRESS_ZIP_IMG = 3;
    private static final int MSG_UI_PROGRESS_ZIP_VIDEO = 33;
    private static final int MSG_UI_PROGRESS_ZIP_VIDEO_UPLOAD = 47;
    private static final int MSG_UI_PROGRESS_UPLOAD_IMG = 4;
    private static final int MSG_UI_FINISH_ZIP_IMG_FAILED = 7;
    private static final int MSG_UI_FINISH_ZIP_VIDEO_FAILED = 8;
    private static final int MSG_UI_FINISH_UPLOAD_IMG_ONE_FAILED = 9;
    private static final int MSG_UI_FINISH_UPLOAD_IMG_ONE_SUCCESS = 10;
    private static final int MSG_UI_PROGRESS_UPLOAD_IMG_ONE = 12;
    private static final int MSG_UI_PROGRESS_UPLOAD_ALL = 14;
    private static final int MSG_UI_FINISH_UPLOAD_ALL = 15;
    private static final int MSG_UI_FINISH_NOZIP = 16;
    private static final int MSG_UI_FINISH_ZIP_PAY = 18;
    private static final int MSG_UI_FINISH_ZIP_FILE_NULL = 22;
    private static final int MSG_UI_PROGRESS_IMAGE_CONTRAST = 23;
    private static final int MSG_UI_FINISH_UPLOAD_ZIPVIDEO_SUCCESS = 24;
    private static final int MSG_UI_PAY_ZIPIMAGE_UPLOAD_FAILED = 25;
    private final Logger mLogger = new Logger(PayDataProcessor.class.getSimpleName());
    private static PayDataProcessor sInstance;
    private final Context mContext;
    private Context context;
    private Activity mActivity = null;
    private ProgressDialog mProgressDialog;
    private InsureDialog mInsureDialog = null;
    public final Handler mProcessorHandler_new;
    public final Handler mUiHandler_new;
    private ReviewImageDialog mReviewDialogImage = null;
    private ReviewVideoDialog mReviewDialogVideo = null;

    public static String getCheckedBaodanNo;
    public static String getPayReason;
    public static String getPayYiji;
    public static String getPayErji;
    public static String getPaySanji;
    public static String getAnimalEarsTagNo;
    public static String getCardNo;

    private PayImageUploadResultBean payImageUploadResultBean;
    private Gson gson;
    private SharedPreferences pref_user;

    private PayInfoContrastResultBean payInfoContrastResultBean;
    private PayApplyResultBean payApplyResultBean;
    private int lipeiUploadGetLibId;
    private double currentLat;
    private double currentLon;
    private ResultBean resultBean;
    private ResultBean resultPayZipImageBean;
    private String strfleg = "";
    private String liPeiVieoFlag;

    public static PayDataProcessor getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PayDataProcessor.class) {
                if (sInstance == null) {
                    sInstance = new PayDataProcessor(context);
                }
            }
        }
        return sInstance;
    }

    public PayDataProcessor(Context context) {
        this.context = context;
        mActivity = (Activity) context;
        mContext = context.getApplicationContext();
        HandlerThread mProcessorThread = new HandlerThread("processor-thread");
        mProcessorThread.start();
        mProcessorHandler_new = new ProcessorHandler_new(mProcessorThread.getLooper());
        mUiHandler_new = new UiHandler_new(Looper.getMainLooper());
        payImageUploadResultBean = new PayImageUploadResultBean();
        payInfoContrastResultBean = new PayInfoContrastResultBean();
        payApplyResultBean = new PayApplyResultBean();
        gson = new Gson();
    }

    public void handleMediaResource_build(final Activity activity) {
        LocationManager.getInstance(activity).startLocation();
        mActivity = activity;
        initDialogs(activity);
        LocationManager locationManager = LocationManager.getInstance(mActivity);
        locationManager.setAddress(new LocationManager.GetAddress() {
            @Override
            public void getaddress(String address) {
            }
        });
        currentLat = locationManager.currentLat;
        currentLon = locationManager.currentLon;
    }

    public void handleMediaResource_destroy() {
        destroyDialogs();
    }

    public void showProgressDialog(Activity activity) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle(R.string.dialog_title);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIcon(R.drawable.cowface);
        mProgressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "确定", mPOSITIVEClickListener);
        mProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", mNEGATIVEClickListener);
        mProgressDialog.setMessage("开始处理......");
        mProgressDialog.show();
        Button positive = mProgressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setVisibility(View.GONE);
        }
        Button negative = mProgressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setVisibility(View.GONE);
        }
    }

    private String getLibId() {
        //读取用户信息
        SharedPreferences pref_user = mActivity.getSharedPreferences(Utils.LIBIDINFO_SHAREFILE, Context.MODE_PRIVATE);
        return pref_user.getString("libid", "");
    }

    private void initDialogs(final Activity activity) {
        updateInsureDialog(activity);
    }

    private void destroyDialogs() {
        if (mInsureDialog != null) {
            mInsureDialog.dismiss();
        }
        mInsureDialog = null;
        if (mReviewDialogImage != null) {
            mReviewDialogImage.dismiss();
        }
        mReviewDialogImage = null;
        if (mReviewDialogVideo != null) {
            mReviewDialogVideo.dismiss();
        }
        mReviewDialogVideo = null;
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        }
    }

    private void initReviewDialog_image(final Activity activity) {
        String imageDri = "";
        if (GlobalConfigure.model == Model.VERIFY.value()) {
            imageDri = GlobalConfigure.mediaPayItem.getImageDir();///storage/emulated/0/innovation/animal/理赔/Current/图片
        }
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View conView = layoutInflater.inflate(R.layout.review_dialog_layout, null);
        mReviewDialogImage = new ReviewImageDialog(activity, conView, imageDri);
        mReviewDialogImage.setTitle(R.string.dialog_title);
        mReviewDialogImage.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mReviewDialogImage.dismiss();
                updateInsureDialog(activity);
            }
            return false;
        });

    }

    private void initReviewDialog_video(final Activity activity) {
        String imageDri = "";
        if (GlobalConfigure.model == Model.BUILD.value()) {
            imageDri = GlobalConfigure.mediaInsureItem.getVideoDir();///storage/emulated/0/innovation/animal/投保/Current/视频
        } else if (GlobalConfigure.model == Model.VERIFY.value()) {
            imageDri = GlobalConfigure.mediaPayItem.getVideoDir();///storage/emulated/0/innovation/animal/理赔/Current/视频
        }
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View conView = layoutInflater.inflate(R.layout.review_dialog_layout, null);
        mReviewDialogVideo = new ReviewVideoDialog(activity, conView, imageDri);
        mReviewDialogVideo.setTitle(R.string.dialog_title);
        mReviewDialogVideo.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mReviewDialogVideo.dismiss();
                updateInsureDialog(activity);
            }
            return false;
        });
    }

    private void initInsureDialog(final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View conView = layoutInflater.inflate(R.layout.insure_dialog_layout, null); //3个角度加载
        mInsureDialog = new InsureDialog(activity, conView);
        final EditText meditText;
        meditText = conView.findViewById(R.id.insure_number);
        mInsureDialog.setTitle(R.string.dialog_title);

        View.OnClickListener listener_abort = v -> {
            mInsureDialog.dismiss();
            reInitCurrentDir();
            collectNumberHandler.sendEmptyMessage(2);
            Log.i("initInsureDialog:", "listener_abort");
        };
        View.OnClickListener listener_add = v -> {
            mInsureDialog.dismiss();
        };
        View.OnClickListener listener_upload_one = v -> {
            // TODO: 2018/8/7  本次上传按钮
            Log.i("isOfflineMode:", "listener_abort" + InnApplication.isOfflineMode);
            strfleg = "liUpload";
            if (InnApplication.isOfflineMode) {
                showProgressDialog(activity);
                dialogProcessUploadOneImage();
            } else {
                mInsureDialog.dismiss();
                showProgressDialog(activity);
                dialogProcessUploadOneImage();
            }

        };

        View.OnClickListener listener_upload_all = v -> {
            mInsureDialog.dismiss();
            //执行上传操作(不包括当前目录)
        };

        View.OnClickListener listener_next = v -> {
            //回录像界面继续捕捉下一头图片
            String pignum = meditText.getText().toString().trim();
            if (pignum.length() == 0) {
                mInsureDialog.setTextTips("请输入编号！！！");
                return;
            }
            mInsureDialog.dismiss();
            showProgressDialog(activity);
            dialogProcessZip();
        };
        View.OnClickListener listener_seeimage = v -> {
            mInsureDialog.dismiss();
            updateReviewDialog_image(activity);
        };

        View.OnClickListener listener_seevideo = v -> {
            mInsureDialog.dismiss();
            updateReviewDialog_video(activity);
        };
        View.OnClickListener listener_chongxin = v -> {
            new DetectorActivity().reInitCurrentCounter(0, 0, 0);
            Log.i("===chongxin===", "wancheng");
            mInsureDialog.dismiss();
        };
        View.OnClickListener listener_wancheng = v -> {
            new DetectorActivity().reInitCurrentCounter(0, 0, 0);
            Log.i("===wancheng===", "wancheng");
            mInsureDialog.dismiss();
        };

        mInsureDialog.setAddeButton("补充", listener_add);
        boolean isli = PreferencesUtils.getBooleanValue("isli", mContext);
        Log.i("==lipeiisli====", "" + isli);
        if (isli) {
            mInsureDialog.setUploadOneButton("完成", listener_upload_one);
            mInsureDialog.setAbortButton("重新拍摄", listener_abort);
        } else {
            mInsureDialog.setUploadOneButton("本次上传", listener_upload_one);
            mInsureDialog.setAbortButton("新开始", listener_abort);
        }
        mInsureDialog.setUploadAllButton("全部上传", listener_upload_all);
        mInsureDialog.setNexteButton("下一头", listener_next);
        mInsureDialog.setSeeImageButton("回看图片", listener_seeimage);
        mInsureDialog.setSeeVideoButton("回看视频", listener_seevideo);
       /* mInsureDialog.setCaijiRetryButton("重新拍摄", listener_chongxin);
        mInsureDialog.setwanchengButton("完成", listener_wancheng);*/
        updateInsureDialog(activity);
    }

    private void updateReviewDialog_image(final Activity activity) {
        if (mReviewDialogImage == null) {
            initReviewDialog_image(activity);
        }
        mReviewDialogImage.updateView();
        mReviewDialogImage.show();
    }

    private void updateReviewDialog_video(final Activity activity) {
        if (mReviewDialogVideo == null) {
            initReviewDialog_video(activity);
        }
        mReviewDialogVideo.updateView();
        mReviewDialogVideo.show();

    }

    private void updateInsureDialog(final Activity activity) {
        if (mInsureDialog == null) {
            initInsureDialog(activity);
        }
        boolean imagehave = testImageHave(); //图片目录是否存在图片文件
        Map<String, String> showMap = getCaptureAngles();//存在角度图片
        String libid = getLibId();
        boolean ziphave = testZipHave(); //是否存在待上传文件
        mInsureDialog.updateView(showMap, imagehave, ziphave, libid, false);
        if (type1Count == 0 && type2Count == 0 && type3Count == 0) {
            Log.i("updateInsureDialog:", "type1Count = " + type1Count);
            Log.i("updateInsureDialog:", "type2Count = " + type2Count);
            Log.i("updateInsureDialog:", "type3Count = " + type3Count);
            mInsureDialog.dismiss();
        } else {
            mInsureDialog.show();
        }
    }

    //判断图片目录下是否已经存在图片文件
    private boolean testImageHave() {
        boolean ifhave = false;
        //获取图片文件
        String imageDri = "";
        if (GlobalConfigure.model == Model.BUILD.value()) {
            imageDri = GlobalConfigure.mediaInsureItem.getImageDir();///storage/emulated/0/innovation/animal/投保/Current/图片
        } else if (GlobalConfigure.model == Model.VERIFY.value()) {
            imageDri = GlobalConfigure.mediaPayItem.getImageDir();///storage/emulated/0/innovation/animal/理赔/Current/图片
        }
        File imageDir_new = new File(imageDri);//图片目录下的文件
        File[] files_image = imageDir_new.listFiles();
        if (!imageDir_new.exists() || files_image.length == 0) {
            return false;
        }

        File tmpFile;
        boolean imagehave;
        for (File aFiles_image : files_image) {
            tmpFile = aFiles_image;//tmpFile===/storage/emulated/0/innovation/animal/Current/图片/1
            String abspath = tmpFile.getAbsolutePath();
            imagehave = testFileHave(abspath);
            if (!imagehave) {
                continue;
            } else {
                return true;
            }
        }
        return ifhave;

    }

    //判断图片目录下是否已经存在Zip文件
    private boolean testZipHave() {
        //投保目录
        String zipimageDri = GlobalConfigure.mediaInsureItem.getZipImageDir();///storage/emulated/0/innovation/animal/投保/ZipImge
        List<String> list_image = FileUtils.GetFiles(zipimageDri, "zip", true);
        if ((list_image != null) && (list_image.size() > 0)) {
            return true;
        }

        String zipvideoDri = GlobalConfigure.mediaInsureItem.getZipVideoDir();///storage/emulated/0/innovation/animal/投保/ZipVideo
        List<String> list_video = FileUtils.GetFiles(zipvideoDri, "zip", true);
        return (list_video != null) && (list_video.size() > 0);

    }

    //判断指定目录下是否已经存在指定类型的文件
    private boolean testFileHave(String filePath) {
        File file_parent = new File(filePath);
        List<String> list_all = FileUtils.GetFiles(filePath, GlobalConfigure.IMAGE_JPEG, true);
        return list_all != null && (!file_parent.exists() || list_all.size() == 0);
    }

    //判断指定目录下是否已经存在指定类型的文件
    private int testFileHaveCount(String filePath) {
        File file_parent = new File(filePath);
        List<String> list_all = FileUtils.GetFiles(filePath, GlobalConfigure.IMAGE_JPEG, true);
        if (list_all == null)
            return 0;
        if (!file_parent.exists()) {
            return 0;
        }
        return list_all.size();
    }

    //获得Dialog显示框中需要显示的角度图(已经捕获的角度图片)
    private Map<String, String> getCaptureAngles() {
        //ArrayList<HashMap<String,String>> missArray = new ArrayList<HashMap<String,String>>();
        Map<String, String> showMap = new TreeMap<>();//TreeMap方式创建可以对map进行升序排序
        //获取图片文件
        String imageDri = "";
        if (GlobalConfigure.model == Model.BUILD.value())
            imageDri = GlobalConfigure.mediaInsureItem.getImageDir();///storage/emulated/0/innovation/animal/投保/Current/图片
        else if (GlobalConfigure.model == Model.VERIFY.value())
            imageDri = GlobalConfigure.mediaPayItem.getImageDir();///storage/emulated/0/innovation/animal/理赔/Current/图片
        File imageDir_new = new File(imageDri);//图片目录下的文件
        File[] files_image = imageDir_new.listFiles();
        if (!imageDir_new.exists() || files_image.length == 0) {
            return showMap;
        }
        //角度类型图片不完整，提示 ，需加上，测试阶段暂不加（防止角度缺失，始终不上传图像）
        ArrayList<Integer> typelist = new ArrayList<>();
        typelist.add(1);
        typelist.add(2);
        typelist.add(3);
        typelist.add(4);
        File tmpFile;
        String tmptype;
        boolean ifneed;
        int imagecount;
        for (int i = 0; i < files_image.length; i++) {
            tmptype = i + "";
            ifneed = typelist.contains(i);
            if (!ifneed) {//不是要显示的角度
                continue;
            }
            tmpFile = files_image[i];//tmpFile===/storage/emulated/0/innovation/animal/Current/图片/1
            String abspath = tmpFile.getAbsolutePath();
            imagecount = testFileHaveCount(abspath);
            showMap.put(tmptype, imagecount + "");
        }
        return showMap;
    }

    //重新初始化Current文件
    private void reInitCurrentDir() {
        Log.i("reInitCurrentDir:", "重新初始化Current文件");
        if (GlobalConfigure.model == Model.BUILD.value()) {
            GlobalConfigure.mediaInsureItem.currentDel();
            GlobalConfigure.mediaInsureItem.currentInit();
        } else if (GlobalConfigure.model == Model.VERIFY.value()) {
            GlobalConfigure.mediaPayItem.currentDel();
            GlobalConfigure.mediaPayItem.currentInit();
        }
    }

    private void UploadOnePay() {
        String zipimageDir = GlobalConfigure.mediaPayItem.getZipImageDir();//storage/emulated/0/innovation/animal/理赔/ZipImage
        String zipVideoDir = GlobalConfigure.mediaPayItem.getZipVideoDir();//storage/emulated/0/innovation/animal/理赔/zipVideoDir
        File file_current = new File(zipimageDir);
        File file_currentVideo = new File(zipVideoDir);
        File zipFile_image = new File(file_current.getParentFile(), GlobalConfigure.ZipFileName + ".zip");
        File zipFile_video = new File(file_currentVideo.getParentFile(), GlobalConfigure.ZipFileName + ".zip");
        dialogProcessUploadOnePay(zipFile_image);
        dialogProcessUploadOnePay(zipFile_video);
        Log.d("UploadOnePay", "processUpload_zipImage_one file name = " + zipFile_image.getAbsolutePath());
        Log.d("UploadOnePay", "processUpload_zipVideo_one video file name = " + zipFile_video.getAbsolutePath());
    }

    //压缩图片和视频为zip文件
    private void dialogProcessZip() {
        Message msg = Message.obtain(mProcessorHandler_new, MSG_PROCESSOR_ZIP);
        mProcessorHandler_new.sendMessage(msg);
    }

    //上传图片zip文件
    private void dialogProcessUploadOneImage() {
        Message msg = Message.obtain(mProcessorHandler_new, MSG_PROCESSOR_UPLOAD_IMAGEONE);
        mProcessorHandler_new.sendMessage(msg);
    }

    //上传理赔图片zip文件
    private void dialogProcessUploadOnePay(File file) {
        Message msg = Message.obtain(mProcessorHandler_new, MSG_PROCESSOR_UPLOAD_PAY_ONE, file);
        mProcessorHandler_new.sendMessage(msg);
    }

    public class ProcessorHandler_new extends Handler {
        private ComparerListener comparerListener;

        ProcessorHandler_new(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            mLogger.i("ProcessorHandler message: %d, obj: %s", msg.what, msg.obj);
            switch (msg.what) {
                case MSG_PROCESSOR_ZIP:
                    processZip(true);
                    break;
                case MSG_PROCESSOR_UPLOAD_PAY_ONE:
                    processUploadOne_Pay();
                    break;
                case MSG_PROCESSOR_UPLOAD_IMAGEONE:
                    processZip(false);
                    processUploadOne_Pay();
                    break;
                case MSG_PROCESSOR_TEST:
                    break;
            }
        }

        private void publishProgress(int what) {
            mUiHandler_new.sendEmptyMessage(what);
        }

        private void publishProgress(int model, int status) {
            Message msg = Message.obtain(mUiHandler_new, MSG_UI_FINISH_UPLOAD_IMG_ONE_FAILED, model, status);
            mUiHandler_new.sendMessage(msg);
        }


        //压缩图片文件
        private void processZip(boolean ifCloseDialog) {
            File file_num = null;
            String namepre = "";
            String imageDri = "";
            String zipimageDri = "";
            String videoDri = "";
            String zipvideoDri = "";
            publishProgress(MSG_UI_PROGRESS_ZIP_IMG);//"压缩图片请等待";
            // if (GlobalConfigure.model == Model.VERIFY.value()) {
            //获取编号文件
            file_num = GlobalConfigure.mediaPayItem.getNumberFile();
            namepre = GlobalConfigure.mediaPayItem.getZipFileName();
            imageDri = GlobalConfigure.mediaPayItem.getImageDir();//storage/emulated/0/innovation/animal/20180227//File detectDir = new File(item.getDetectedDir(mContext));
            zipimageDri = GlobalConfigure.mediaPayItem.getZipImageDir();//storage/emulated/0/innovation/animal/ZipImage
            videoDri = GlobalConfigure.mediaPayItem.getVideoDir();
            GlobalConfigure.mediaPayItem.getZipVideoDir();//storage/emulated/0/innovation/animal/ZipVideo
            zipvideoDri = GlobalConfigure.mediaPayItem.getZipVideoDir();
            //   }
            //获取图片文件
            File imageDir_new = new File(imageDri);//图片目录下的文件
            File[] files_image = imageDir_new.listFiles();
            if (files_image == null) {
                publishProgress(MSG_UI_FINISH_ZIP_FILE_NULL);//"文件不存在
                return;
            }
            if (files_image.length == 0) {
                publishProgress(MSG_UI_FINISH_ZIP_FILE_NULL);//"文件不存在
                return;
            }

            // 4. zip recognized image
            //加入编号文件
            File[] fs_image = new File[files_image.length + 1];
            for (int i = 0; i < files_image.length; i++) {
                fs_image[i] = files_image[i];
            }
            fs_image[files_image.length] = file_num;

            //打包图片文件
            File file_current = new File(zipimageDri);
            File zipFile_image = new File(file_current, namepre + ".zip");
            ZipUtil.zipFiles(fs_image, zipFile_image);
            Log.i("========zipFile_image", zipFile_image.getAbsolutePath());
            if (!zipFile_image.exists()) {
                publishProgress(MSG_UI_FINISH_ZIP_IMG_FAILED);//"压缩图片出错，请重新录制";
                reInitCurrentDir();
                return;
            }

            //if (UPLOAD_VIDEO_FLAG == true) {
            liPeiVieoFlag = PreferencesUtils.getStringValue(InnApplication.liPeiVieoFlag, mContext);
            if ("1".equals(liPeiVieoFlag)) {
                publishProgress(MSG_UI_PROGRESS_ZIP_VIDEO);
                File videoDir_new = new File(videoDri);//视频目录下的文件
                File[] files_video = videoDir_new.listFiles();

                //20180425
                if (files_video == null) {
                    publishProgress(MSG_UI_FINISH_ZIP_FILE_NULL);
                    return;
                }
                if (files_video.length == 0) {
                    publishProgress(MSG_UI_FINISH_ZIP_FILE_NULL);
                    return;
                }

                File[] fs_video = new File[files_video.length + 1];
                for (int i = 0; i < files_video.length; i++) {
                    fs_video[i] = files_video[i];
                }
                fs_video[files_video.length] = file_num;
                Log.i("zipvideoDri", zipvideoDri);
                Log.i("zipgetvideoDri", GlobalConfigure.mediaPayItem.getVideoDir());
                file_current = new File(zipvideoDri);
                File zipFile_video = new File(file_current, GlobalConfigure.ZipFileName + ".zip");
                ZipUtil.zipFiles(fs_video, zipFile_video);
                if (!zipFile_video.exists()) {
                    publishProgress(MSG_UI_FINISH_ZIP_VIDEO_FAILED);
                    reInitCurrentDir();
                    return;
                }
            }

            File videoDir_new = new File(videoDri);//当前视频目录下的文件
            File imageDri_new = new File(imageDri);//当前图片目录下的文件
            boolean deleteCurrentVideoResult = FileUtils.deleteFile(videoDir_new);
            boolean deleteCurrentImageResult = FileUtils.deleteFile(imageDri_new);
            if (deleteCurrentVideoResult == true) {
                mLogger.i("当前视频文件夹删除成功！");
            }
            if (deleteCurrentImageResult == true) {
                mLogger.i("当前图片文件夹删除成功！");
            }

            reInitCurrentDir();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (GlobalConfigure.model == Model.BUILD.value()) {
                if (ifCloseDialog)
                    mProgressDialog.dismiss();
            }
        }

        private void processUploadOne_Pay() {
            // TODO: 2018/8/18 By:LuoLu  清空当前图片文件夹
            String imageDri = "";
            String videoDri = "";
            if (GlobalConfigure.model == Model.VERIFY.value()) {
                //获取编号文件
                imageDri = GlobalConfigure.mediaPayItem.getImageDir();
                videoDri = GlobalConfigure.mediaPayItem.getVideoDir();
                GlobalConfigure.mediaPayItem.getZipVideoDir();//storage/emulated/0/innovation/animal/ZipVideo
            }
            File videoDir_new = new File(videoDri);//当前视频目录下的文件
            File imageDri_new = new File(imageDri);//当前图片目录下的文件
            boolean deleteCurrentVideoResult = FileUtils.deleteFile(videoDir_new);
            boolean deleteCurrentImageResult = FileUtils.deleteFile(imageDri_new);
            if (deleteCurrentVideoResult == true) {
                mLogger.i("当前理赔视频文件夹删除成功！");
            }
            if (deleteCurrentImageResult == true) {
                mLogger.i("当前理赔图片文件夹删除成功！");
            }
            int model = Model.VERIFY.value();
            publishProgress(MSG_UI_PROGRESS_UPLOAD_IMG_ONE);
            // if (UPLOAD_VIDEO_FLAG == true) {
//            Log.i("=liPeiVieoFlag===", InnApplication.liPeiVieoFlag.trim());
            //  if (InnApplication.liPeiVieoFlag.trim().equals("1")) {
            // TODO: 视频包
            String zipVideoDir = GlobalConfigure.mediaPayItem.getZipVideoDir();
            Log.i("zipVideoDir:", zipVideoDir);
            File file_zipVideo = new File(zipVideoDir);
            String fname_video = GlobalConfigure.ZipFileName + ".zip";
            File zipFile_video2 = new File(file_zipVideo, fname_video); //要上传的文件
            Log.i("zipVideo=====:", zipFile_video2.getAbsolutePath());
            liPeiVieoFlag = PreferencesUtils.getStringValue(InnApplication.liPeiVieoFlag, mContext);
            if ("1".equals(liPeiVieoFlag)) {
                // 上传图片包
                String zipImageDir = GlobalConfigure.mediaPayItem.getZipImageDir();
                File file_zip = new File(zipImageDir);
                String fname_image = GlobalConfigure.ZipFileName + ".zip";
                Log.i("toubao fname_image:", fname_image);
                File zipFile_image2 = new File(file_zip, fname_image); //要上传的文件
                if (!zipFile_image2.exists()) {
                    Log.i("zipFile_image2:", "压缩图片文件夹不存在！！");
                    return;
                }
                //读取用户信息
                SharedPreferences pref_user = mActivity.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                int userId = 0;
                // TODO: 2018/8/20 By:LuoLu
                if (pref_user == null) {
                    payInfoHandler.sendEmptyMessage(400);
                }
                userId = pref_user.getInt("uid", 0);
                mLogger.i("baodanNO: " + getCheckedBaodanNo);

                if (!zipFile_video2.exists()) {
                    Log.i("zipVideo:", "压缩视频文件夹不存在！！");
                    Toast.makeText(mActivity, "压缩视频文件夹为空！", Toast.LENGTH_SHORT).show();
                }
                upload_zipVideo(model, zipFile_video2, userId, getCheckedBaodanNo);
                Log.i("lipei fname_image:", zipFile_image2.getAbsolutePath());
                uploadZipImage(model, zipFile_image2, userId, getCheckedBaodanNo, new OnUploadListener() {
                    @Override
                    public void showDialog() {

                    }

                    @Override
                    public void dismissDialog() {

                    }

                    @Override
                    public void onUploadResult(int status, String msg, OnUploadFailedRetryListener retryListener) {

                    }
                });
            } else {
                // TODO: 2018/8/16 By:LuoLu  建库，上传图片包
                String zipImageDir = GlobalConfigure.mediaPayItem.getZipImageDir();
                File file_zip = new File(zipImageDir);
                String fname_image = GlobalConfigure.ZipFileName + ".zip";
                Log.i("toubao fname_image:", fname_image);
                File zipFile_image2 = new File(file_zip, fname_image); //要上传的文件
                if (!zipFile_image2.exists()) {
                    Log.i("zipFile_image2:", "压缩图片文件夹不存在！！");
                    return;
                }
                //读取用户信息
                SharedPreferences pref_user = mActivity.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                int userId = 0;
                // TODO: 2018/8/20 By:LuoLu
                if (pref_user == null) {
                    payInfoHandler.sendEmptyMessage(400);
                }
                userId = pref_user.getInt("uid", 0);
                mLogger.i("baodanNO: " + getCheckedBaodanNo);
                Log.i("========zipFileImage", zipFile_image2.getAbsolutePath());
                Log.i("========zipisli", PreferencesUtils.getBooleanValue("isli", mContext) + "");
                if (!PreferencesUtils.getBooleanValue("isli", mContext)) {
                    uploadZipImage(model, zipFile_image2, userId, getCheckedBaodanNo, new OnUploadListener() {
                        @Override
                        public void showDialog() {

                        }

                        @Override
                        public void dismissDialog() {

                        }

                        @Override
                        public void onUploadResult(int status, String msg, OnUploadFailedRetryListener retryListener) {

                        }
                    });
                } else {
                    String lipeidate = PreferencesUtils.getStringValue("lipeidate", mContext);
//                    int cardno = databaseHelper.updateLiPeiLocalFromzipPath(zipFile_image2.getAbsolutePath(), lipeidate);
//                    Log.i("updatecount", cardno + "条");

                   /* Log.i("videopath", zipFile_video2.getAbsolutePath());
                    int videocount = databaseHelper.updateLiPeiLocalFromVideozipPath(zipFile_video2.getAbsolutePath(), lipeidate);
                    Log.i("upvideocount", videocount + "条");*/
                    mInsureDialog.dismiss();
                    DetectorActivity detectorActivity = (DetectorActivity) context;
                    detectorActivity.finish();
                }
            }
        }

        public Handler UploadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                PayDataProcessor.this.listener.showDialog();
            }
        };

        public void uploadZipImage(int model, File zipFileImage, int uid, String libNum, OnUploadListener listener) {
            PayDataProcessor.this.listener = listener;
            new Thread() {
                @Override
                public void run() {
                    UploadHandler.sendEmptyMessage(0);
                }
            }.start();
            //publishProgress(MSG_UI_PROGRESS_UPLOAD_IMG);
            int source = 1;
            String gps = null;
            try {
                TreeMap<String, String> treeMap = new TreeMap<>();
                treeMap.put(Utils.UploadNew.USERID, uid + "");
                treeMap.put(Utils.UploadNew.LIB_NUM, libNum);
                treeMap.put(Utils.UploadNew.TYPE, model + "");
                treeMap.put(Utils.UploadNew.LIBD_SOURCE, source + "");
                //    treeMap.put(Utils.UploadNew.LIB_ENVINFO, getEnvInfo(mActivity, gps));
                treeMap.put(Utils.UploadNew.LIB_ENVINFO, "");
                treeMap.put("collectTimes", String.valueOf(99));
                treeMap.put("timesFlag", "");
                Log.e("理赔图片包上传接口请求报文：", treeMap.toString() + "\n请求地址：" + PAY_LIBUPLOAD);
                MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
                FormBody.Builder builder = new FormBody.Builder();
                for (TreeMap.Entry<String, String> entry : treeMap.entrySet()) {
                    requestBody.addFormDataPart(entry.getKey(), entry.getValue());
                }
                Log.i("===zipFile==", zipFileImage.getName());
                requestBody.addFormDataPart("zipFile", zipFileImage.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), zipFileImage));
                String responsePayZipImageUpload = HttpUtils.post(PAY_LIBUPLOAD, requestBody.build());
                if (responsePayZipImageUpload != null) {
                    Log.e("理赔图片包上传接口返回：\n", PAY_LIBUPLOAD + "\nresponsePayZipImageUpload:\n" + responsePayZipImageUpload);
                    resultPayZipImageBean = gson.fromJson(responsePayZipImageUpload, ResultBean.class);
                    if (resultPayZipImageBean.getStatus() == 1) {
                        PayImageUploadResultBean payImageUploadResultBean = gson.fromJson(responsePayZipImageUpload, PayImageUploadResultBean.class);
                        //获取ib_id
                        lipeiUploadGetLibId = payImageUploadResultBean.getData().getLibId();
                        //publishProgress(MSG_UI_PROGRESS_IMAGE_CONTRAST);
                        // payInfoHandler.sendEmptyMessage(17);
                        if (null != comparerListener) {
                            comparerListener.onComparer(lipeiUploadGetLibId);
                        }
                        if (strfleg.equals("liUpload")) {
                            publishProgress(MSG_UI_PROGRESS_IMAGE_CONTRAST);
                            payInfoHandler.sendEmptyMessage(17);
                        }
                    } else if (resultPayZipImageBean.getStatus() == 0) {
                        //                    image quality bad
                        AlertDialog.Builder builder34 = new AlertDialog.Builder(mActivity)
                                .setIcon(R.drawable.cowface)
                                .setTitle("提示")
                                .setMessage(resultPayZipImageBean.getMsg())
                                .setPositiveButton("重新采集", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(mActivity, DetectorActivity.class);
                                        intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                        mActivity.startActivity(intent);
                                        reInitCurrentDir();
                                        collectNumberHandler.sendEmptyMessage(2);
                                        mActivity.finish();
                                    }
                                });
                        builder34.setCancelable(false);
                        builder34.show();
                    } else {
                        // upload failed
                        publishProgress(MSG_UI_PAY_ZIPIMAGE_UPLOAD_FAILED);
                    }
                } else {
                    //                server down
                    payInfoHandler.sendEmptyMessage(42);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TODO: 2018/8/15 By:LuoLu  upload video
        private void upload_zipVideo(int model, File zipFile_image, int uid, String libNum) {
            publishProgress(MSG_UI_PROGRESS_ZIP_VIDEO_UPLOAD);
            int source = 2;
            String gps = null;
            try {
                TreeMap<String, String> treeMap = new TreeMap<>();
                treeMap.put(Utils.UploadNew.USERID, uid + "");
                treeMap.put(Utils.UploadNew.LIB_NUM, libNum);
                treeMap.put(Utils.UploadNew.TYPE, model + "");
                treeMap.put(Utils.UploadNew.LIBD_SOURCE, source + "");
                treeMap.put(Utils.UploadNew.LIB_ENVINFO, getEnvInfo(mActivity, gps));

                MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
                FormBody.Builder builder = new FormBody.Builder();
                for (TreeMap.Entry<String, String> entry : treeMap.entrySet()) {
                    requestBody.addFormDataPart(entry.getKey(), entry.getValue());
                }
                requestBody.addFormDataPart("zipFile", zipFile_image.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), zipFile_image));
                // TODO: 2018/8/4
                Log.e("理赔视频上传接口请求报文：", treeMap.toString() + "\n请求地址：" + PAY_LIBUPLOAD);
                String responsePayVideoUpload = HttpUtils.post(PAY_LIBUPLOAD, requestBody.build());
                if (responsePayVideoUpload != null) {
                    mLogger.e("理赔视频文件上传接口返回：\n" + PAY_LIBUPLOAD + "\nresponsePayVideoUpload:" + responsePayVideoUpload);
                    resultBean = gson.fromJson(responsePayVideoUpload, ResultBean.class);
                    if (resultBean.getStatus() == 1) {
//                        upload success
                        publishProgress(MSG_UI_FINISH_UPLOAD_ZIPVIDEO_SUCCESS);
                        mLogger.i("responsePayImageUpload data:" + resultBean.getData().toString());
//                        toubaoUploadBean = gson.fromJson(responsePayImageUpload, ToubaoUploadBean.class);
//                        mLogger.i("理赔视频 libID:" + toubaoUploadBean.getData().getLibId());
//                        addAnimalLibID = String.valueOf(toubaoUploadBean.getData().getLibId());

//                        insuranceDataHandler.sendEmptyMessage(18);

                    } else if (resultBean.getStatus() == 0) {
//                        image bad
                        payInfoHandler.sendEmptyMessage(199);
                    } else {
//                server down
                        payInfoHandler.sendEmptyMessage(422);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setcomparerListener(ComparerListener comparerListener) {
            this.comparerListener = comparerListener;
        }


    }

    public interface ComparerListener {
        void onComparer(int lipeiUploadGetLibId);
    }

    public class UiHandler_new extends Handler {
        public UiHandler_new(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {
            mLogger.i("UiHandler message: %d", msg.what);
            final String showMessage;
            switch (msg.what) {
                case MSG_UI_PROGRESS_ZIP_IMG:
                    showMessage = "压缩图片，请等待......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_PROGRESS_ZIP_VIDEO:
                    showMessage = "压缩视频，请等待......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_PROGRESS_ZIP_VIDEO_UPLOAD:
                    showMessage = "正在上传视频，请等待......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_FINISH_ZIP_PAY:
                    UploadOnePay();
                    break;
                case MSG_UI_PROGRESS_UPLOAD_IMG:
                    showMessage = "正在上传理赔建库图片...";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_FINISH_ZIP_IMG_FAILED:
                    showMessage = "压缩图片出错，请重新录制";
                    updateProgressDialogOneButton(showMessage);
                    break;
                case MSG_UI_FINISH_ZIP_VIDEO_FAILED:
                    showMessage = "压缩视频出错，请重新录制";
                    updateProgressDialogOneButton(showMessage);
                    break;
                case MSG_UI_FINISH_ZIP_FILE_NULL:
                    showMessage = "待压缩的文件不存在，请重新录制";
                    updateProgressDialogOneButton(showMessage);
                    break;
                case MSG_UI_PROGRESS_UPLOAD_IMG_ONE:
                    showMessage = "正在上传建库的图片......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_FINISH_NOZIP:
                    showMessage = "没有需要上传的文件......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialogOneButton(showMessage);
                    break;
                case MSG_UI_FINISH_UPLOAD_IMG_ONE_SUCCESS:
                    showMessage = "本次图片上传成功......";
                    mProgressDialog.setMessage(showMessage);
                    break;
                case MSG_UI_FINISH_UPLOAD_ZIPVIDEO_SUCCESS:
                    showMessage = "视频包上传成功......";
                    mProgressDialog.setMessage(showMessage);
                    break;
                case MSG_UI_PROGRESS_UPLOAD_ALL:
                    showMessage = "开始上传全部文件......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_FINISH_UPLOAD_ALL:
                    showMessage = "全部文件上传完成......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialogOneButton(showMessage);
                    break;
                case MSG_UI_PROGRESS_IMAGE_CONTRAST:
                    showMessage = "正在比对图片......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialog(showMessage);
                    break;
                case MSG_UI_PAY_ZIPIMAGE_UPLOAD_FAILED:
                    showMessage = "上传失败......";
                    mProgressDialog.setMessage(showMessage);
                    updateProgressDialogTwoButton(showMessage);
                    break;
                default:
                    showMessage = ".....";
                    mProgressDialog.setMessage(showMessage);
            }
        }

        private void updateProgressDialog(String showMessage) {
            Button positive = mProgressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
            if (positive != null) {
                positive.setVisibility(View.GONE);
            }
            Button negative = mProgressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
            if (negative != null) {
                negative.setVisibility(View.GONE);
            }
            mProgressDialog.setMessage(showMessage);
        }

        private void updateProgressDialogOneButton(String showMessage) {
            Button positive = mProgressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
            if (positive != null) {
                positive.setVisibility(View.VISIBLE);
            }
            mProgressDialog.setMessage(showMessage);
        }

        private void updateProgressDialogTwoButton(String showMessage) {
            Button positive = mProgressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
            positive.setText("重试");
            positive.setOnClickListener(view -> {
                if (GlobalConfigure.model == Model.VERIFY.value()) {
                    UploadOnePay();
                }
            });
            if (positive != null) {
                positive.setVisibility(View.VISIBLE);
            }
            Button negative = mProgressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
            negative.setText("退出");
            negative.setOnClickListener(view -> {
                mProgressDialog.dismiss();
                mActivity.finish();
            });
            if (negative != null) {
                negative.setVisibility(View.VISIBLE);
            }
            mProgressDialog.setMessage(showMessage);
        }

    }

    private final DialogInterface.OnClickListener mPOSITIVEClickListener = (dialog, which) -> {
        dialog.dismiss();
        initDialogs(mActivity);
    };

    private final DialogInterface.OnClickListener mNEGATIVEClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mProgressDialog = null;
        }
    };

    public void transerPayData(String s, String s1, String s2, String s3, String s4, String s5, String s6) {

        getCheckedBaodanNo = s;
        getPayReason = s1;
        getPayYiji = s2;
        getPayErji = s3;
        getPaySanji = s4;
        getAnimalEarsTagNo = s5;
        getCardNo = s6;
        Log.d("Media getCBaodanNo", getCheckedBaodanNo);
        Log.d("Media getPayReason", getPayReason);
        Log.d("Media yiji", s2);
        Log.d("Media erji", s3);
        Log.d("Media sanji", s4);
        Log.d("Media AnimalEarsTagNo", getAnimalEarsTagNo);
        Log.d("Media getCardNo", getCardNo);

        getStringTouboaExtra = s;
        getlipeiTempNumber = s;

    }

    private String responsePayInfoContrast = null;
    private int payInfoContrastResultLipeiId;
    @SuppressLint("HandlerLeak")
    private final Handler payInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 17:
                    try {
                        pref_user = mActivity.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                        int userId = pref_user.getInt("uid", 0);
                        TreeMap<String, String> treeMapContrast = new TreeMap();
                        treeMapContrast.put("baodanNoReal", PreferencesUtils.getStringValue("baodannum", context));
                        treeMapContrast.put("reason", PreferencesUtils.getStringValue(HttpUtils.reason, context));
                        treeMapContrast.put("cardNo", PreferencesUtils.getStringValue("cardnum", context));
                        treeMapContrast.put("yiji", getPayYiji == null ? "" : getPayYiji);
                        treeMapContrast.put("erji", getPayErji == null ? "" : getPayErji);
                        treeMapContrast.put("sanji", getPaySanji == null ? "" : getPaySanji);
                        treeMapContrast.put("pigNo", getAnimalEarsTagNo == null ? "" : getAnimalEarsTagNo);
                        treeMapContrast.put("userId", String.valueOf(userId) == null ? "" : String.valueOf(userId));
                        treeMapContrast.put("libId", String.valueOf(lipeiUploadGetLibId));
                        treeMapContrast.put("longitude", currentLon + "");
                        treeMapContrast.put("latitude", currentLat + "");
                        treeMapContrast.put("collectTimes", String.valueOf(99));

                        Log.e("理赔信息比对接口请求报文：", treeMapContrast.toString());
                        FormBody.Builder builder = new FormBody.Builder();
                        for (TreeMap.Entry<String, String> entry : treeMapContrast.entrySet()) {
                            builder.add(entry.getKey(), entry.getValue());
                        }
                        RequestBody formBody = builder.build();

                        responsePayInfoContrast = HttpUtils.post(HttpUtils.PAY_INFO_CONTRAST, formBody);
                        Log.i("payInfoHandler:", HttpUtils.PAY_INFO_CONTRAST + "\n理赔信息比对接口responsePayInfoContrast:\n" + responsePayInfoContrast);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("理赔", "理赔信息比对接口异常");
                        Toast.makeText(mActivity, "理赔信息比对接口异常！", Toast.LENGTH_SHORT).show();
                        payInfoHandler.sendEmptyMessage(900);
                    }

                    if (null != responsePayInfoContrast) {
                        mProgressDialog.dismiss();
                        strfleg = "";
                        ResultBean resultBeanPayInfoContrast = gson.fromJson(responsePayInfoContrast, ResultBean.class);
                        if (resultBeanPayInfoContrast.getStatus() == 1) {
                            //   展示比对结果
                            payInfoHandler.sendEmptyMessage(18);
                        } else if (resultBeanPayInfoContrast.getStatus() == 0) {
                            Log.e("理赔", resultBeanPayInfoContrast.getMsg());
                            AlertDialog.Builder builder22 = new AlertDialog.Builder(mActivity)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayInfoContrast.getMsg())
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(mContext, DetectorActivity.class);
                                            intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                            intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                            mContext.startActivity(intent);
                                            reInitCurrentDir();
                                            collectNumberHandler.sendEmptyMessage(2);
                                            mActivity.finish();
                                        }
                                    });
                            builder22.setCancelable(false);
                            builder22.show();
                           /* listener.onUploadResult(0, resultBeanPayInfoContrast.getMsg(), new OnUploadFailedRetryListener() {
                                @Override
                                public void onRetry() {
                                    collectNumberHandler.sendEmptyMessage(2);
                                }
                            });*/
                        } else {

                            AlertDialog.Builder builder22 = new AlertDialog.Builder(mActivity)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayInfoContrast.getMsg())
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(mContext, DetectorActivity.class);
                                            intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                            intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                            mContext.startActivity(intent);
                                            reInitCurrentDir();
                                            collectNumberHandler.sendEmptyMessage(2);
                                            mActivity.finish();
                                        }
                                    });
                            builder22.setCancelable(false);
                            builder22.show();
                        }
                    }
                    break;

                case 18:
                    PayInfoContrastResultBean payInfoContrastResultBean = gson.fromJson(responsePayInfoContrast, PayInfoContrastResultBean.class);
                    payInfoContrastResultLipeiId = payInfoContrastResultBean.getData().getLipeiId();
                    final LipeiResultDialog dialogLipeiResult = new LipeiResultDialog(mActivity);
                    switch (payInfoContrastResultBean.getData().getResultPic().size()) {
                        case 1:
                            dialogLipeiResult.setImage2(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(0).getPic()));
                            dialogLipeiResult.setLipeiResultmessage(payInfoContrastResultBean.getData().getResultMsg()
                                    + "\n"
                                    + payInfoContrastResultBean.getData().getResultPic().get(0).getDetail() + "\n");
                            break;
                        case 2:
                            dialogLipeiResult.setImage1(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(0).getPic()));
                            dialogLipeiResult.setImage3(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(1).getPic()));
                            dialogLipeiResult.setLipeiResultmessage(payInfoContrastResultBean.getData().getResultMsg()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(0).getDetail()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(1).getDetail() + "\n");
                            break;
                        case 3:
                            dialogLipeiResult.setImage1(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(0).getPic()));
                            dialogLipeiResult.setImage2(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(1).getPic()));
                            dialogLipeiResult.setImage3(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(2).getPic()));
                            dialogLipeiResult.setLipeiResultmessage(payInfoContrastResultBean.getData().getResultMsg()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(0).getDetail()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(1).getDetail()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(2).getDetail() + "\n");
                            break;
                    }
                    View.OnClickListener listener_new = v -> {
                        dialogLipeiResult.dismiss();
                        //    1.4	理赔申请处理接口
                        payInfoHandler.sendEmptyMessage(19);
                    };
                    View.OnClickListener listener_ReCollect = v -> {
                        if (PreferencesUtils.getBooleanValue(HttpUtils.offlineupdate, context)) {
                            dialogLipeiResult.dismiss();
                            PreferencesUtils.saveBooleanValue(HttpUtils.offlineupdate, false, context);
//                            int cordeTextcount = databaseHelper.updateLiPeiLocalFromrecordeText("3", PreferencesUtils.getStringValue("lipeidate", mContext));
//                            Log.i("cordeTextcount", cordeTextcount + "");
                        } else {
                            dialogLipeiResult.dismiss();
                            Intent intent = new Intent(mActivity, DetectorActivity.class);
                            intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                            intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                            mActivity.startActivity(intent);
                            reInitCurrentDir();
                            collectNumberHandler.sendEmptyMessage(2);
                            mActivity.finish();
                        }
                    };

                    dialogLipeiResult.setTitle("验证结果");
                    dialogLipeiResult.setBtnGoApplication("直接申请", listener_new);
                    if (PreferencesUtils.getBooleanValue(HttpUtils.offlineupdate, context)) {
                        dialogLipeiResult.setBtnReCollect("放弃", listener_ReCollect);
                    } else {
                        dialogLipeiResult.setBtnReCollect("重新拍摄", listener_ReCollect);
                    }
                    dialogLipeiResult.show();

                    break;

                case 19:
                    String responsePayApplyResult = null;
                    try {
                        TreeMap<String, String> treeMapPayApply = new TreeMap();
                        treeMapPayApply.put("lipeiId", String.valueOf(payInfoContrastResultLipeiId));
                        Log.e("理赔申请处理接口", treeMapPayApply.toString());
                        FormBody.Builder builderPayApply = new FormBody.Builder();
                        for (TreeMap.Entry<String, String> entry : treeMapPayApply.entrySet()) {
                            builderPayApply.add(entry.getKey(), entry.getValue());
                        }
                        RequestBody formBodyPayApply = builderPayApply.build();
                        responsePayApplyResult = HttpUtils.post(HttpUtils.PAY_APPLY, formBodyPayApply);
                        Log.i("payInfoHandler:", HttpUtils.PAY_APPLY + "\n理赔申请处理接口responsePayApplyResult:\n"
                                + responsePayApplyResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mActivity, "理赔申请处理接口异常！", Toast.LENGTH_SHORT).show();
                    }
                    ResultBean resultBeanPayApply;
                    if (null != responsePayApplyResult) {
                        resultBeanPayApply = gson.fromJson(responsePayApplyResult, ResultBean.class);
                        if (resultBeanPayApply.getStatus() == 1) {
                            AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(mActivity)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayApply.getMsg())
                                    .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            mActivity.finish();
                                        }
                                    });

                            builderApplyFinish.setCancelable(false);
                            builderApplyFinish.show();

                        } else if (resultBeanPayApply.getStatus() == 0) {
                            AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(mActivity)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayApply.getMsg())
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            payInfoHandler.sendEmptyMessage(19);
                                        }
                                    });

                            builderApplyFinish.setCancelable(false);
                            builderApplyFinish.show();
                        } else if (resultBeanPayApply.getStatus() == -1) {
                            AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(mActivity)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayApply.getMsg())
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            payInfoHandler.sendEmptyMessage(19);
                                        }
                                    });

                            builderApplyFinish.setCancelable(false);
                            builderApplyFinish.show();
                        }
                    } else {
                        AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(mActivity)
                                .setIcon(R.drawable.cowface)
                                .setTitle("提示")
                                .setMessage("服务端异常，请稍后再试！")
                                .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        mProgressDialog.dismiss();
                                        //mActivity.finish();
                                    }
                                });

                        builderApplyFinish.setCancelable(false);
                        builderApplyFinish.show();
                    }


                    break;

                case 22:
                    AlertDialog.Builder builder22 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(payImageUploadResultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mActivity, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                    mActivity.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    mActivity.finish();
                                }
                            });
                    builder22.setCancelable(false);
                    builder22.show();

                    break;
                case 24:
                    AlertDialog.Builder builder24 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(payImageUploadResultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mActivity, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                    mActivity.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    mActivity.finish();
                                }
                            });
                    builder24.setCancelable(false);
                    builder24.show();
                    break;
                case 25:
                    AlertDialog.Builder builder25 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(payImageUploadResultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mActivity, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                    mActivity.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    mActivity.finish();
                                }
                            });
                    builder25.setCancelable(false);
                    builder25.show();
                    break;
                case 34:
                    AlertDialog.Builder builder34 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(payImageUploadResultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mActivity, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                    mActivity.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    mActivity.finish();
                                }
                            });
                    builder34.setCancelable(false);
                    builder34.show();
                    break;

                case 42:
                    mProgressDialog.dismiss();
                    AlertDialog.Builder builder42 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("服务端异常，请稍后再试！")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    // mActivity.finish();
                                }
                            });
                    builder42.setCancelable(false);
                    builder42.show();

                    break;

                case 400:
                    AlertDialog.Builder builder400 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("获取用户ID失败！")
                            .setPositiveButton("确认", (dialog, which) -> mActivity.finish());
                    builder400.setCancelable(false);
                    builder400.show();
                    break;
                case 900:
                    AlertDialog.Builder builder900 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("请求超时，请稍后重试！")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mActivity, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                                    mActivity.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    // mActivity.finish();
                                }
                            });
                    builder900.setCancelable(false);
                    builder900.show();
                    break;
                case 199:
                    AlertDialog.Builder builder199 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mActivity, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    mActivity.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    mActivity.finish();
                                }
                            });
                    builder199.setCancelable(false);
                    builder199.show();
                    break;
                case 422:
                    AlertDialog.Builder builder422 = new AlertDialog.Builder(mActivity)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mActivity.finish();
                                }
                            });
                    builder422.setCancelable(false);
                    builder422.show();
                    break;
                default:
                    break;
            }

        }
    };

    public interface OnUploadListener {
        void showDialog();

        void dismissDialog();

        void onUploadResult(int status, String msg, OnUploadFailedRetryListener retryListener);
    }

    private OnUploadListener listener;

    public interface OnUploadFailedRetryListener {
        void onRetry();
    }

}
