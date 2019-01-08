package com.innovation.biz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.innovation.animalInsurance.R;
import com.innovation.utils.ScreenUtil;

public class LipeiDialog extends Dialog {

    private Button btnLipeiNext;


    public LipeiDialog(Context context) {
        super(context, R.style.Alert_Dialog_Style);
        setContentView(R.layout.lipei_dialog_layout);
//        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);


        btnLipeiNext = findViewById(R.id.btnLipeiNext);


    }



    public void setBtnLipeiNext(String text, View.OnClickListener listener) {
        btnLipeiNext.setOnClickListener(listener);
    }


}
