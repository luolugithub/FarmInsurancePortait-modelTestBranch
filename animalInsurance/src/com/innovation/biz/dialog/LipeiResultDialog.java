package com.innovation.biz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.utils.ScreenUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Modified by luolu on 2018/6/17.
 */

public class LipeiResultDialog extends Dialog {

    private TextView tv_lipeiResultmessage;
    public ImageView image1,image2,image3;
    private Button btnReCollect, btnGoApplication;


    public LipeiResultDialog(Context context) {
        super(context, R.style.Alert_Dialog_Style);
        setContentView(R.layout.lipei_result_dialog_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);

        tv_lipeiResultmessage = (TextView) findViewById(R.id.tv_lipeiResultmessage);

        btnReCollect = findViewById(R.id.btnReCollect);
        btnGoApplication = findViewById(R.id.btnGoApplication);
//        btnReturn = findViewById(R.id.btnReturn);


    }




    private void initView()
    {
//        image1.setBackgroundResource(R.drawable.cow_angle2no);
//        image2.setBackgroundResource(R.drawable.cow_angle1no);
//        image3.setBackgroundResource(R.drawable.cow_angle0no);

    }

    public void setBtnReCollect(String text, View.OnClickListener listener) {
        btnReCollect.setText(text);
        btnReCollect.setOnClickListener(listener);
    }

    public void setBtnGoApplication(String text, View.OnClickListener listener) {
        btnGoApplication.setText(text);
        btnGoApplication.setOnClickListener(listener);
    }

    public void setBtnGoApplicationVisible() {
        btnGoApplication.setVisibility(View.VISIBLE);
    }
    public void setBtnGoApplicationGone() {
        btnGoApplication.setVisibility(View.GONE);
    }

    public void setBtnReCollectVisible() {
        btnReCollect.setVisibility(View.VISIBLE);
    }


    public void setImage1(String text) {
        ImageLoader.getInstance().displayImage(text,image1);
    }

    public void setImage2(String text) {
        ImageLoader.getInstance().displayImage(text,image2);
    }
    public void setImage3(String text) {
        ImageLoader.getInstance().displayImage(text,image3);
    }

    public void setLipeiResultmessage(String text) {
        tv_lipeiResultmessage.setText(text);
    }

}
