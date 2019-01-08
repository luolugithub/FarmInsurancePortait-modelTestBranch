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
 * Author by luolu, Date on 2018/10/8.
 * COMPANY：InnovationAI
 */

public class ToubaoResultDialog extends Dialog {

    private TextView tvToubaoResultmessage;
    public ImageView imageMaySimilarity;
    private Button btnReCollect, btnContinueToubao;


    public ToubaoResultDialog(Context context) {
        super(context, R.style.Alert_Dialog_Style);
        setContentView(R.layout.toubao_upload_result_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);

        imageMaySimilarity = (ImageView) findViewById(R.id.imageMaySimilarity);
        tvToubaoResultmessage = (TextView) findViewById(R.id.tvToubaoResultmessage);

        btnReCollect = findViewById(R.id.btnReCollect);
        btnContinueToubao = findViewById(R.id.btnContinueToubao);
    }


    public void setBtnReCollect(String text, View.OnClickListener listener) {
        btnReCollect.setText(text);
        btnReCollect.setOnClickListener(listener);
    }

    public void setBtnContinueToubao(String text, View.OnClickListener listener) {
        btnContinueToubao.setText(text);
        btnContinueToubao.setOnClickListener(listener);
    }

    public void setImage(String text) {
        ImageLoader.getInstance().displayImage(text,imageMaySimilarity);
    }

    public void setToubaoResultmessage(String text) {
        tvToubaoResultmessage.setText(text);
    }

}
