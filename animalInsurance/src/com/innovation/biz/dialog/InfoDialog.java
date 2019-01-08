package com.innovation.biz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.login.Utils;
import com.innovation.base.Model;
import com.innovation.utils.ImageLoaderUtils;
import com.innovation.utils.JsonHelper;
import com.innovation.utils.ScreenUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author wbs on 12/16/17.
 */

public class InfoDialog extends Dialog {

    private TextView mTitle;
    private TextView mMessage;
    private TextView mFullName;
    private TextView mId;
    private TextView mTime;
    private TextView mLocation;
    private ImageView mImage;
    private Button mPositiveBtn;
    private Button mNegativeBtn;
    private Button mContactBtn;

    public InfoDialog(Context context, int model, String info) {
        super(context, R.style.Alert_Dialog_Style);
        setContentView(R.layout.info_dialog_layout);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mMessage = (TextView) findViewById(R.id.tv_message);
        mFullName = (TextView) findViewById(R.id.full_name);
        mId = (TextView) findViewById(R.id.num_id);
        mTime = (TextView) findViewById(R.id.time);
        mLocation = (TextView) findViewById(R.id.location);
        mImage = (ImageView) findViewById(R.id.image);
        mPositiveBtn = (Button) findViewById(R.id.positive);
        mNegativeBtn = (Button) findViewById(R.id.negative);
        mContactBtn = (Button) findViewById(R.id.contactservice);
        initView(model, info);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setMessage(String msg) {
        setInfoViewVisible(false);
        mMessage.setText(msg);
    }

    public void setInfo(int model, String info) {
        setInfoViewVisible(true);
        initView(model, info);
    }

    private void initView(int model, String info) {
        JSONObject jo = null;
        JSONObject jo_env = null;
        try {
            jo = new JSONObject(info);
            jo_env = JsonHelper.getJsonObj(jo, Utils.Upload.LIB_ENVINFO);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jo == null) {
            mTitle.setText(model == Model.BUILD.value() ? "投保失败" : "校验失败");
            mMessage.setText("错误信息: " + info);
            setInfoViewVisible(false);
            return;
        }

        // TODO: 2018/8/7 By:LuoLu
        //读取用户信息
        SharedPreferences pref_user = getContext().getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        String username = pref_user.getString("fullname", "");

        String fullName = JsonHelper.getString(jo, Utils.Upload.FULL_NAME);
        int id = JsonHelper.getInt(jo, Utils.Upload.LIB_ID);
        String time = JsonHelper.getString(jo, Utils.Upload.LIB_CREATE_TIME);
        String location = JsonHelper.getString(jo_env, Utils.Upload.GPS);
        String image = JsonHelper.getString(jo, Utils.Upload.IMAGE_URL);
        mFullName.setText("采集人： " + username);
        mId.setText("序号： " + id);
        mTime.setText("采集时间： " + time);
        mLocation.setText((model == Model.BUILD.value() ? "养殖场地点： " : "采集地点： ") + location);
        ImageLoader.getInstance().displayImage(image, mImage, ImageLoaderUtils.getDefaultOptions());

        mImage.setVisibility(View.GONE);
    }

    private void setInfoViewVisible(boolean visible) {
        mMessage.setVisibility(visible ? View.GONE : View.VISIBLE);
        mFullName.setVisibility(visible ? View.VISIBLE : View.GONE);
        mId.setVisibility(visible ? View.VISIBLE : View.GONE);
        mTime.setVisibility(visible ? View.VISIBLE : View.GONE);
        mLocation.setVisibility(visible ? View.VISIBLE : View.GONE);
        mImage.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setPositiveButton(String text, View.OnClickListener listener) {
        mPositiveBtn.setText(text);
        mPositiveBtn.setVisibility(View.VISIBLE);
        mPositiveBtn.setOnClickListener(listener);
    }

    public void setNegativeButton(String text, View.OnClickListener listener) {
        mNegativeBtn.setText(text);
        mNegativeBtn.setVisibility(View.VISIBLE);
        mNegativeBtn.setOnClickListener(listener);
    }


    public void setContactButton(String text, View.OnClickListener listener) {
        mContactBtn.setText(text);
        mContactBtn.setVisibility(View.VISIBLE);
        mContactBtn.setOnClickListener(listener);
    }


}
