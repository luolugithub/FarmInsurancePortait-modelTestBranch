package com.innovation.biz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.utils.ScreenUtil;

import java.util.Map;

import static org.tensorflow.demo.DetectorActivity.type1Count;
import static org.tensorflow.demo.DetectorActivity.type2Count;
import static org.tensorflow.demo.DetectorActivity.type3Count;


/**
 * Author by luolu, Date on 2018/8/25.
 * COMPANY：InnovationAI
 */

public class InsureDialog extends Dialog {


    private ImageView mImage2, mImage7;
    private ImageView mImage6, mImage8, mImage3, mImage1;
    private Button mAbortBtn;
    private Button mAddBtn;
    private Button mNextBtn, mUpOneBtn, mUpAllBtn, caijiRetry,insure_cowInfo;
    ;
    private TextView mTips;
    public static EditText mNumberEdit;
    public static TextView mLipeiNumber;
    public static TextView mcowear_number;
    public static TextView mcowType;
    private String missInfo = "";
    private boolean angleOK = true; // 每头猪所需要的必要角度是否完整
    //    private final static int minCount = 5;
    private Button mSeeImage, mSeeVideo;


    public InsureDialog(Context context, View view) {
        super(context, R.style.Alert_Dialog_Style);
        //setContentView(R.layout.insure_dialog_layout);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);

        mImage2 = (ImageView) findViewById(R.id.insure_image2);
        mImage3 = (ImageView) findViewById(R.id.insure_image3);
        mImage1 = (ImageView) findViewById(R.id.insure_image1);
        mImage6 = (ImageView) findViewById(R.id.insure_image6);
        mImage7 = (ImageView) findViewById(R.id.insure_image7);
        mImage8 = (ImageView) findViewById(R.id.insure_image8);
        mNumberEdit = (EditText) findViewById(R.id.insure_number);
        mLipeiNumber = (TextView) findViewById(R.id.lipei_number);
        mcowear_number = (TextView) findViewById(R.id.cowear_number);
        mcowType = (TextView) findViewById(R.id.cow_type);
        mTips = (TextView) findViewById(R.id.tv_tips);
        mAbortBtn = (Button) findViewById(R.id.insure_abort);
        mAddBtn = (Button) findViewById(R.id.insure_add);
        mNextBtn = (Button) findViewById(R.id.btn_next);
        mUpOneBtn = (Button) findViewById(R.id.btn_uploadone);
        mUpAllBtn = (Button) findViewById(R.id.btn_uploadall);
        mSeeImage = (Button) findViewById(R.id.insure_seeimage);
        mSeeVideo = (Button) findViewById(R.id.insure_seevideo);
//        caijiRetry = (Button) findViewById(R.id.caijiRetry);
//        insure_cowInfo  = (Button) findViewById(R.id.insure_cowInfo);
    }

    //判断是否获得必要角度
    public boolean getAngleOk(Map<String, String> map) {
        boolean angleok = true;
        int count2 = 0;
        int count7 = 0;
        for (String v : map.keySet()) {
            int count = Integer.parseInt(map.get(v));
            switch (v) {
                case "2":
                    count2 += count;
                    break;
                case "7":
                    count7 += count;
                    break;
                default:
                    break;
            }
        }
        if ((count2 == 0) && (count7 == 0)) {
            angleok = false;
        }

        return angleok;
    }

    private boolean getAngleMissData(Map<String, String> map) {
        boolean angleok = true;

        mImage3.setBackgroundResource(R.drawable.cow_angle0);
        mImage2.setBackgroundResource(R.drawable.cow_angle1);
        mImage1.setBackgroundResource(R.drawable.cow_angle2);
        return angleok;
    }

    private void initView() {
        mImage1.setBackgroundResource(R.drawable.cow_angle2no);
        mImage2.setBackgroundResource(R.drawable.cow_angle1no);
        mImage3.setBackgroundResource(R.drawable.cow_angle0no);
        mNumberEdit.setText("");
        mNumberEdit.clearFocus();
        mTips.setText("");
    }

    public void setAbortButton(String text, View.OnClickListener listener) {
        mAbortBtn.setText(text);
        mAbortBtn.setOnClickListener(listener);
    }

    public void setAddeButton(String text, View.OnClickListener listener) {
        //mAddBtn.setText(text);
        mAddBtn.setOnClickListener(listener);
    }

    public void setUploadOneButton(String text, View.OnClickListener listener) {
        mUpOneBtn.setText(text);
        mUpOneBtn.setOnClickListener(listener);
    }

    public void setUploadAllButton(String text, View.OnClickListener listener) {
        //mAddBtn.setText(text);
        mUpAllBtn.setOnClickListener(listener);
    }

    public void setNexteButton(String text, View.OnClickListener listener) {
        //mAddBtn.setText(text);
        mNextBtn.setOnClickListener(listener);
    }

    public void setSeeImageButton(String text, View.OnClickListener listener) {
        mSeeImage.setOnClickListener(listener);
    }

    public void setSeeVideoButton(String text, View.OnClickListener listener) {
        mSeeVideo.setOnClickListener(listener);
    }

    public void setCaijiRetryButton(String text, View.OnClickListener listener) {
        caijiRetry.setOnClickListener(listener);
    }

    public void updateView(Map<String, String> showMap, boolean haveimage, boolean havezip, String libid, boolean havevideo) {

        initView();
        angleOK = getAngleMissData(showMap);

        if (angleOK)//必要角度齐全
        {
            mUpOneBtn.setVisibility(View.VISIBLE);
            mNextBtn.setVisibility(View.GONE);
            mAddBtn.setVisibility(View.GONE);
        } else {
            mUpOneBtn.setVisibility(View.GONE);
            mNextBtn.setVisibility(View.GONE);
        }

//        if (haveimage)// 图片文件存在，补充按钮可见，回看图片按钮可见
//        {
//            mTips.setText(missInfo);
////            mAddBtn.setVisibility(View.VISIBLE);
//            mSeeImage.setVisibility(View.GONE);
////            insure_cowInfo.setVisibility(View.VISIBLE);
//        } else {
//            mAddBtn.setVisibility(View.GONE);
//            mSeeImage.setVisibility(View.GONE);
////            insure_cowInfo.setVisibility(View.GONE);
//        }
//
//        if (havezip)// zip文件存在，全部上传按钮可见
//        {
////            mUpAllBtn.setVisibility(View.VISIBLE);
//        } else {
//            mUpAllBtn.setVisibility(View.GONE);
//        }
//
//        if (havevideo) {// zip文件存在，全部上传按钮可见
//            mSeeVideo.setVisibility(View.GONE);
//        } else {
//            mSeeVideo.setVisibility(View.GONE);
//        }
//        mNumberEdit.setText(libid);

    }

    public void setTextTips(String text) {
        mTips.setText(text);
    }

}
