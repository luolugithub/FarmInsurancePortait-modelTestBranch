package com.innovation.biz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.innovation.animalInsurance.R;
import com.innovation.utils.ScreenUtil;

import java.io.File;

/**
 * Created by haojie on 2018/6/6.
 */

public class ShowVideoDialog extends Dialog {
    private String TAG = "ImageShowDialog";
    private Activity mActivity;
    private VideoView videoViewView = null;

    public ShowVideoDialog(Activity activity) {
        super(activity, R.style.Alert_Dialog_Style);
        setContentView(R.layout.show_video_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.height = (int) (ScreenUtil.getScreenHight() - 35 * ScreenUtil.getDensity());
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);

        mActivity = activity;
        videoViewView = (VideoView)findViewById(R.id.video_show);

    }

    public void updateView(String path)
    {
        File file = new File(path);
        videoViewView.setVideoPath(path);
        //用来设置起始播放位置，为0表示从开始播放
        videoViewView.seekTo(0);
        videoViewView.requestFocus();
        videoViewView.start();
    }
}
