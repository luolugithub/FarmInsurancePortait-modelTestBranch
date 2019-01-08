package com.innovation.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.innovation.animalInsurance.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by lxr on 2018/4/27.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected ViewGroup title_bar = null;
    Unbinder unbinder;
    public ProgressDialog mProgressDialog;
    private boolean isPause = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            Window win = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //透明状态栏
                win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
                win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                // 部分机型的statusbar会有半透明的黑色背景
                win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // SDK21
                win.setStatusBarColor(Color.TRANSPARENT);
            }
        }*/
        this.setContentView(this.getLayoutId());//缺少这一行
        // setContentView(getLayoutId());
        initView();
        ButterKnife.bind(this);
        InnApplication.verifyStoragePermissions(this);
        showProgressDialog(this);
        initData();
    }

    public void initView() {

    }

    /**
     * 布局ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            initData();
        }
    }

    /**
     * 初始化数据
     */

    protected abstract void initData();


    /* *跳转*/

    public void goToActivity(Class activity, Bundle bundle) {
        Intent intent = new Intent(this, activity);
        //携带数据
        if (bundle != null && bundle.size() != 0) {
            intent.putExtra("data", bundle);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
        }
        super.onDestroy();
    }

    private void showProgressDialog(Activity activity) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle(R.string.dialog_title);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("开始处理......");
        Button positive = mProgressDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setVisibility(View.GONE);
        }
        Button negative = mProgressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setVisibility(View.GONE);
        }
    }

    public void showDialogError(String s) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View inflate = View.inflate(InnApplication.getAppContext(), R.layout.error_nomal, null);
        TextView error_msg = inflate.findViewById(R.id.error_msg);
        TextView error_sure = inflate.findViewById(R.id.error_sure);
        dialog.setView(inflate);
        error_msg.setText(s);
        AlertDialog dialogcreate = dialog.create();
        dialogcreate.setCanceledOnTouchOutside(false);
        dialogcreate.show();
        error_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcreate.dismiss();
            }
        });
    }
    public final Random random = new Random();
    public static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    public String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    /**
     * 将时间戳转换为时间
     */
    public String stampToDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }
   /* public void getCurrentLocationLatLng() {
        //初始化定位
        mLocationClient = new AMapLocationClient(EnterpriseBaodanActivity.this);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。默认连续定位 切最低时间间隔为1000ms
        mLocationOption.setInterval(3500);
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private double currentLat;
    private double currentLon;
    private String str_address = "";
    private final AMapLocationListener mLocationListener = amapLocation -> {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLat = amapLocation.getLatitude();//获取纬度
                currentLon = amapLocation.getLongitude();//获取经度
                //  str_address = amapLocation.getAddress();
                str_address = mLocationClient.getLastKnownLocation().getAddress();
                ;

                amapLocation.getAccuracy();//获取精度信息
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    };*/
}
