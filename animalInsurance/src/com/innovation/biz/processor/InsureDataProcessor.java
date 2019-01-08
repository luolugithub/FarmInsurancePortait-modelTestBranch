package com.innovation.biz.processor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.innovation.bean.ResultBean;
import com.innovation.bean.ToubaoUploadBean;
import com.innovation.biz.Insured.AddAnimalActivity;
import com.innovation.biz.classifier.CowFaceBoxDetector;
import com.innovation.biz.classifier.DonkeyFaceBoxDetector;
import com.innovation.biz.classifier.PigFaceBoxDetector;
import com.innovation.biz.dialog.InsureDialog;
import com.innovation.biz.dialog.ReviewImageDialog;
import com.innovation.biz.dialog.ReviewVideoDialog;
import com.innovation.biz.dialog.ToubaoResultDialog;
import com.innovation.base.Model;
import com.innovation.biz.iterm.MediaInsureItem;
import com.innovation.location.LocationManager;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.RespObject;
import com.innovation.login.Utils;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.JsonHelper;
import com.innovation.utils.PreferencesUtils;
import com.innovation.utils.UploadObject;
import com.innovation.utils.ZipUtil;
import com.innvocation.upload.UploadHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static com.innovation.base.InnApplication.getStringTouboaExtra;
import static com.innovation.biz.Insured.AddAnimalActivity.addAnimalLibID;
import static com.innovation.utils.HttpUtils.INSURE_IMAGE_UPLOAD;
import static com.innovation.utils.HttpUtils.getEnvInfo;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;
import static org.tensorflow.demo.DetectorActivity.type1Count;
import static org.tensorflow.demo.DetectorActivity.type2Count;
import static org.tensorflow.demo.DetectorActivity.type3Count;

/**
 * Author by luolu, Date on 2018/8/16.
 * COMPANY：InnovationAI
 */

public class InsureDataProcessor {

    private final Logger mLogger = new Logger(InsureDataProcessor.class.getSimpleName());
    private static InsureDataProcessor sInstance;
    private Activity mActivity = null;
    private ProgressDialog mProgressDialog;
    private InsureDialog mInsureDialog = null;
    private ReviewImageDialog mReviewDialogImage = null;
    private ReviewVideoDialog mReviewDialogVideo = null;

    public static InsureDataProcessor getInstance(Context context) {
        if (sInstance == null) {
            synchronized (InsureDataProcessor.class) {
                if (sInstance == null) {
                    sInstance = new InsureDataProcessor(context);
                }
            }
        }
        return sInstance;
    }

    public InsureDataProcessor(Context context) {
        HandlerThread mProcessorThread = new HandlerThread("processor-thread");
        mProcessorThread.start();
    }

    public void handleMediaResource_build(final Activity activity) {
        mActivity = activity;
//        initDialogs(activity);
        mProgressDialog = new ProgressDialog(activity);
    }

    public void handleMediaResource_destroy() {
        destroyDialogs();
    }

//    private void initDialogs(final Activity activity) {
//        updateInsureDialog(activity);
//    }

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

    private void initInsureDialog(final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View conView = layoutInflater.inflate(R.layout.insure_dialog_layout, null); //3个角度加载
        mInsureDialog = new InsureDialog(activity, conView);

        mInsureDialog.setTitle(R.string.dialog_title);

        View.OnClickListener listener_abort = v -> {
            Log.i("initInsureDialog:", "listener_abort");

            mInsureDialog.dismiss();
            reInitCurrentDir();
            collectNumberHandler.sendEmptyMessage(2);
        };

        View.OnClickListener listener_next = v -> {
            //回录像界面继续捕捉下一头图片
            mInsureDialog.dismiss();
//            showProgressDialog(activity);
        };

            mInsureDialog.setUploadOneButton("完成", finish);
            mInsureDialog.setAbortButton("放弃", listener_abort);
        mInsureDialog.setNexteButton("下一头", listener_next);
        updateInsureDialog(activity);
    }
    View.OnClickListener finish = v -> {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle(R.string.dialog_title);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIcon(R.drawable.cowface);
        mProgressDialog.setMessage("正在处理，请等待......");
        mProgressDialog.show();
        mInsureDialog.dismiss();
        SimpleDateFormat tmpSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        String fname = tmpSimpleDateFormat.format(new Date(System.currentTimeMillis()));
        File file = new File("/sdcard/Android/data/com.innovation.animal_model_test/cache/innovation/animal/投保/");
        File fileNew = new File("/sdcard/Android/data/com.innovation.animal_model_test/cache/innovation/animal/");


        boolean zipResult = ZipUtil.zipFile(file.getAbsolutePath(),
                fileNew.getPath() + "/" + fname + ".zip");
        if (zipResult == true){
            FileUtils.deleteFile(file);
            GlobalConfigure.mediaInsureItem = new MediaInsureItem(mActivity);
            GlobalConfigure.mediaInsureItem.currentDel();
            GlobalConfigure.mediaInsureItem.currentInit();
            GlobalConfigure.mediaInsureItem.getmTestDir();
            GlobalConfigure.mediaInsureItem.getVideoDir();
            GlobalConfigure.mediaInsureItem.getmTestVideoSuccessDir();
            GlobalConfigure.mediaInsureItem.getmTestVideoFailedDir();
            collectNumberHandler.sendEmptyMessage(2);
        }
        if (mProgressDialog != null){
            if (mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
        Intent intent = new Intent(mActivity, DetectorActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
    };

    private void updateInsureDialog(final Activity activity) {
        if (mInsureDialog == null) {
            initInsureDialog(activity);
        }
        Map<String, String> showMap = getCaptureAngles();
        mInsureDialog.updateView(showMap, true, true, "111", true);
        if (type1Count == 0 && type2Count == 0 && type3Count == 0) {
            Log.i("updateInsureDialog:", "type1Count = " + type1Count);
            Log.i("updateInsureDialog:", "type2Count = " + type2Count);
            Log.i("updateInsureDialog:", "type3Count = " + type3Count);
            mInsureDialog.dismiss();
        } else {
            mInsureDialog.show();
        }
    }

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
}
