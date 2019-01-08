package com.innovation.biz.insurance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.innovation.animalInsurance.R;
import com.innovation.base.BaseActivity;
import com.innovation.base.GlobalConfigure;
import com.innovation.base.InnApplication;
import com.innovation.base.Model;
import com.innovation.location.AlertDialogManager;
import com.innovation.login.Utils;
import com.innovation.login.view.HomeActivity;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.OkHttp3Util;
import com.innovation.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.DetectorActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.innovation.login.view.HomeActivity.isOPen;

public class CreateYanActivity extends BaseActivity {
    public static String TAG = "CreateYanActivity";
    @BindView(R.id.baodanApplyName)
    EditText baodanApplyName;
    @BindView(R.id.btnyanNext)
    Button btnyanNext;
    @BindView(R.id.btncaiji)
    Button btncaiji;
    @BindView(R.id.btnyanWan)
    Button btnyanWan;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_cancel)
    ImageView iv_cancel;
    private String mTempToubaoNumber;
    private AMapLocationClient mLocationClient;
    private int userid;
    private String str="";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_yan;
    }

    @Override
    protected void initData() {
        tv_title.setText("新建验标单");
        SharedPreferences pref = CreateYanActivity.this.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userid = pref.getInt("uid", 0);
        GlobalConfigure.model = Model.BUILD.value();
        iv_cancel.setVisibility(View.VISIBLE);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String stringValue = PreferencesUtils.getStringValue(HttpUtils.baodanType, InnApplication.getAppContext());
        if ("1".equals(stringValue)) {
            btnyanWan.setVisibility(View.VISIBLE);
            btncaiji.setVisibility(View.VISIBLE);
            btnyanNext.setVisibility(View.GONE);
        } else if ("2".equals(stringValue)) {
            btnyanWan.setVisibility(View.GONE);
            btncaiji.setVisibility(View.GONE);
            btnyanNext.setVisibility(View.VISIBLE);
        }
        btnyanWan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(baodanApplyName.getText().toString())) {
                    Toast.makeText(CreateYanActivity.this, "验标单为空", Toast.LENGTH_LONG).show();
                } else {
                    String str_random1 = createCode();
                    mTempToubaoNumber = stampToDate(System.currentTimeMillis()) + str_random1;
                    str="wancheng";
                    createYan();
                }

            }
        });
        btnyanNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(baodanApplyName.getText().toString())) {
                    Toast.makeText(CreateYanActivity.this, "验标单为空", Toast.LENGTH_LONG).show();
                } else {
                    PreferencesUtils.saveKeyValue("zuYan", baodanApplyName.getText().toString(), CreateYanActivity.this);
                    goToActivity(OrganizationBaodanActivity.class, null);
                    finish();
                }

            }
        });
        btncaiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOPen(CreateYanActivity.this)) {
                    if ("".equals(baodanApplyName.getText().toString())) {
                        Toast.makeText(CreateYanActivity.this, "验标单为空", Toast.LENGTH_LONG).show();
                    }else {
                        String str_random1 = createCode();
                        mTempToubaoNumber = stampToDate(System.currentTimeMillis()) + str_random1;
                        str="caiji";
                        createYan();
                    }
                } else {
                    openGPS1(CreateYanActivity.this);
                }

            }
        });
        getCurrentLocationLatLng();
    }

    private void openGPS1(Context mContext) {
        AlertDialogManager.showMessageDialog(mContext, "提示", getString(R.string.locationwarning), new AlertDialogManager.DialogInterface() {
            @Override
            public void onPositive() {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1315);
            }

            @Override
            public void onNegative() {

            }
        });

    }

    private void createYan() {
        Map map = new HashMap();
        map.put(HttpUtils.AppKeyAuthorization, "hopen");
        Map mapbody = new HashMap();
        mapbody.put("baodanNo", mTempToubaoNumber);
        mapbody.put("bdsId", PreferencesUtils.getStringValue(HttpUtils.id, InnApplication.getAppContext()));
        mapbody.put("longitude", String.valueOf(currentLat));
        mapbody.put("latitude", String.valueOf(currentLon));
        mapbody.put("yanBiaoName", baodanApplyName.getText().toString());
        mapbody.put("uid", String.valueOf(userid));
        InnApplication.getStringTouboaExtra = String.valueOf(mTempToubaoNumber);

                OkHttp3Util.doPost(HttpUtils.BaoDanaddyan, mapbody, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                Log.i(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.i(TAG, string);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            int status = jsonObject.getInt("status");
                            String msg = jsonObject.getString("msg");
                            if (status == -1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        showDialogError(msg);
                                    }
                                });
                            } else if (status == 0) {
                                Toast.makeText(CreateYanActivity.this, msg, Toast.LENGTH_LONG).show();
                            } else if (status == 1) {
                                Toast.makeText(CreateYanActivity.this, msg, Toast.LENGTH_LONG).show();
                                if ("wancheng".equals(str)) {
                                    Intent intent = new Intent(CreateYanActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if ("caiji".equals(str)){
                                    Intent intent = new Intent(CreateYanActivity.this, DetectorActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    private void getCurrentLocationLatLng() {
        //初始化定位
        mLocationClient = new AMapLocationClient(CreateYanActivity.this);
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
    };

}
