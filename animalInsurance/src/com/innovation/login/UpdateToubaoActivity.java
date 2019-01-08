package com.innovation.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.innovation.animalInsurance.R;
import com.innovation.base.InnApplication;
import com.innovation.bean.BaodanBean;
import com.innovation.biz.insurance.InsuranceAcitivity;
import com.innovation.utils.EditTextJudgeNumberWatcher;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class UpdateToubaoActivity extends AppCompatActivity {

    private static String TAG = "UpdateToubaoActivity";
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private TextView tv_title;
    private ImageView iv_cancel;

    private TextView tv_baodan_number;
    private TextView tv_baodan_tel;
    private TextView tv_baodan_people;
    private EditText tv_baodan_date;
    private TextView tv_baodan_address;

    private ToubaoTask mToubaoTask;

    private String errStr = "";
    private BaodanBean insurresp;

    private int mYear;
    private int mMonth;
    private int mDay;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private String baodanNumber;
    private Button update_complete;
    private Button update_cancel;
    private UpdateToubaoTask mUpdateToubaoTask;
    private TextView tv_baodan_idcard;
    private TextView tv_baodan_jing;
    private TextView tv_baodan_wei;
    private TextView tv_baodan_cardtype;
    private String baodanId;

    private EditText insuranceRate;
    private ArrayAdapter<String> adapter;
    private TextView tv_baodan_openbank;
    private TextView tv_baodan_bank_num;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_toubao);

        if (hasPermission()) {

        } else {
            requestPermission();
        }

        baodanNumber = getIntent().getStringExtra("baodanNumber");
        baodanId = getIntent().getStringExtra("baodanId");

        SharedPreferences pref = getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userId = pref.getInt("uid", 0);

        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        tv_baodan_number = (TextView) findViewById(R.id.tv_baodan_number);

        //spinnerInsuranceRate = (Spinner) findViewById(R.id.spinnerInsuranceRate);
        tv_baodan_tel = (TextView) findViewById(R.id.tv_baodan_tel);
        tv_baodan_people = (TextView) findViewById(R.id.tv_baodan_people);
        tv_baodan_idcard = (TextView) findViewById(R.id.tv_baodan_idcard);
        tv_baodan_date = (EditText) findViewById(R.id.tv_baodan_date);
        tv_baodan_address = (TextView) findViewById(R.id.tv_baodan_address);
        tv_baodan_jing = (TextView) findViewById(R.id.tv_baodan_jing);
        tv_baodan_wei = (TextView) findViewById(R.id.tv_baodan_wei);
        tv_baodan_cardtype = (TextView) findViewById(R.id.tv_baodan_cardtype);

        tv_baodan_openbank = (TextView) findViewById(R.id.tv_baodan_openbank);
        tv_baodan_bank_num = (TextView) findViewById(R.id.tv_baodan_bank_num);

        update_complete = (Button) findViewById(R.id.update_complete);
        update_cancel = (Button) findViewById(R.id.update_cancel);
        insuranceRate = (EditText) findViewById(R.id.insuranceRate);
        insuranceRate.addTextChangedListener(new EditTextJudgeNumberWatcher(insuranceRate));
        tv_baodan_number.setText(baodanNumber);

        // insuranceRate = new String[]{"贫困户 4.5%", "非贫困户 5%"};
        // adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, insuranceRate);
        // spinnerInsuranceRate.setAdapter(adapter);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修改验标单");

        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        iv_cancel.setVisibility(View.VISIBLE);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String double_insuranceRate = insuranceRate.getText().toString();
                String str_baodan_number = tv_baodan_number.getText().toString();
                String str_baodan_people = tv_baodan_people.getText().toString();
                String str_baodan_tel = tv_baodan_tel.getText().toString();
                String str_baodan_date = tv_baodan_date.getText().toString();
                String str_baodan_address = tv_baodan_address.getText().toString();
                String str_baodan_jing = tv_baodan_jing.getText().toString();
                String str_baodan_wei = tv_baodan_wei.getText().toString();

                String str_baodan_idcard = tv_baodan_idcard.getText().toString();


               /* String insuranceRate = spinnerInsuranceRate.getSelectedItem().toString();
                String double_insuranceRate = "";

                if (insuranceRate.indexOf("贫困户 4.5%") > -1) {
                    double_insuranceRate = "4.5";
                } else if (insuranceRate.indexOf("非贫困户 5%") > -1) {
                    double_insuranceRate = "5.0";
                }*/

//                //读取用户信息
//                SharedPreferences pref_user = UpdateToubaoActivity.this.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
//                String preUserId = String.valueOf(pref_user.getInt("uid", 0));

                if (str_baodan_number.equals("")) {
                    Toast.makeText(getApplicationContext(), "请重新获取验标单号", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*if (double_insuranceRate.equals("")) {
                    Toast.makeText(getApplicationContext(), "请选择保险费率", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                if ("".equals(double_insuranceRate)) {
                    Toast.makeText(UpdateToubaoActivity.this, "保险费率为空", Toast.LENGTH_LONG).show();
                    return;
                } else if (Double.valueOf(double_insuranceRate) > 10) {
                    Toast.makeText(UpdateToubaoActivity.this, "非法的费率格式", Toast.LENGTH_LONG).show();
                    return;
                }

                if (str_baodan_people.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入被保险人名称", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (str_baodan_tel.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPhoneNumberValid(str_baodan_tel.trim())) {
                    Toast.makeText(getApplicationContext(), "手机号格式有误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (str_baodan_date.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入验标日期", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (str_baodan_address.equals("") || str_baodan_jing.equals("")) {
                    str_baodan_address = "未知位置";
                    str_baodan_jing = "未知位置";
//                    Toast.makeText(getApplicationContext(), "请重新获取地址信息", Toast.LENGTH_SHORT).show();
//                    return;
                }

                TreeMap query = new TreeMap<String, String>();
                query.put("name", str_baodan_people.trim());
                query.put("cardType", tv_baodan_cardtype.getText().toString().trim());
                query.put("cardNo", str_baodan_idcard.trim());
                query.put("baodanNo", baodanNumber);
                query.put("baodanType", "1");
                query.put("toubaoKind", "1");
                query.put("amount", "1000");
                query.put("money", "1");
                query.put("proxyName", "投保修改");
                query.put("phone", str_baodan_tel.trim());
                query.put("address", str_baodan_address.trim());
                query.put("longitude", str_baodan_jing.trim());
                query.put("latitude", str_baodan_wei.trim());
                query.put("baodanTime", str_baodan_date.trim().length() > 12 ? str_baodan_date.trim() : str_baodan_date.trim() + " 00:00:00");
                query.put("animalType", "2");
                query.put("id", baodanId);
                query.put("baodanRate", double_insuranceRate);
                query.put("uid", String.valueOf(userId));

                mUpdateToubaoTask = new UpdateToubaoTask(HttpUtils.INSUR_UPDATE_URL, query);
                mUpdateToubaoTask.execute((Void) null);
            }
        });

        tv_baodan_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateToubaoActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });

        getCurrentLocationLatLng();

        TreeMap query = new TreeMap<String, String>();
        query.put("baodanNo", baodanNumber.trim());
        InnApplication.getStringTouboaExtra = baodanNumber.trim();
        if (baodanNumber.trim() != null) {
            mToubaoTask = new ToubaoTask(HttpUtils.INSUR_QUERY_URL, query);
            mToubaoTask.execute((Void) null);
        }
    }

    @Override
    protected void onDestroy() {
        //  Auto-generated method stub
        super.onDestroy();

        mLocationClient.stopLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationClient != null) {
            mLocationClient.startLocation(); // 启动定位
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
        }
    }


    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            }
            tv_baodan_date.setText(days.trim());
        }
    };


    private void getCurrentLocationLatLng() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //只会使用网络定位
        /* mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);*/
        //只使用GPS进行定位
        /*mLocationOption.setLocationMode(AMapLocationMode.Device_Sensors);*/
        // 设置为单次定位 默认为false
        /*mLocationOption.setOnceLocation(true);*/
        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。默认连续定位 切最低时间间隔为1000ms
        mLocationOption.setInterval(3500);
        //设置是否返回地址信息（默认返回地址信息）
        /*mLocationOption.setNeedAddress(true);*/
        //关闭缓存机制 默认开启 ，在高精度模式和低功耗模式下进行的网络定位结果均会生成本地缓存,不区分单次定位还是连续定位。GPS定位结果不会被缓存。
        /*mLocationOption.setLocationCacheEnable(false);*/
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
//            if (!IsGpsWork.isGpsEnabled(getApplicationContext())) {
////                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.hasNotOpenGps), Toast.LENGTH_SHORT);
////                toast.setGravity(Gravity.CENTER, 0, 0);
////                toast.show();
//            }

            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double currentLat = amapLocation.getLatitude();//获取纬度
                    double currentLon = amapLocation.getLongitude();//获取经度
                    String str_address = amapLocation.getAddress();
//                        latLonPoint = new LatLonPoint(currentLat, currentLon);  // latlng形式的
                    /*currentLatLng = new LatLng(currentLat, currentLon);*/   //latlng形式的
//                        Log.i("currentLocation", "currentLat : " + currentLat + " currentLon : " + currentLon);

//                    tv_baodan_address.setText(str_address);
//                    tv_baodan_jing.setText(String.valueOf(currentLon));
//                    tv_baodan_wei.setText(String.valueOf(currentLat));

                    amapLocation.getAccuracy();//获取精度信息
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "((^(13|15|17|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;

    }


    public class ToubaoTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;

        ToubaoTask(String url, TreeMap map) {
            mUrl = url;
            mQueryMap = map;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //  attempt authentication against a network service.
            try {
                FormBody.Builder builder = new FormBody.Builder();
                // Add Params to Builder
                for (TreeMap.Entry<String, String> entry : mQueryMap.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                // Create RequestBody
                RequestBody formBody = builder.build();

                String response = HttpUtils.post(mUrl, formBody);
                Log.d(TAG, "response:" + response);

                if (HttpUtils.INSUR_QUERY_URL.equalsIgnoreCase(mUrl)) {
                    insurresp = (BaodanBean) HttpUtils.processResp_insurInfo(response, mUrl);
                    if (insurresp == null) {
//                        errStr = getString(R.string.error_newwork);
                        errStr = "请求错误！";
                        return false;
                    }
                    if (insurresp.status != HttpRespObject.STATUS_OK) {
                        errStr = insurresp.msg;
                        return false;
                    }
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                errStr = "服务器错误！";
                return false;
            }
            //  register the new account here.

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mToubaoTask = null;
            if (success & HttpUtils.INSUR_QUERY_URL.equalsIgnoreCase(mUrl)) {
//                InfoUtils.saveInsurInfo(InsuranceNewActivity.this, insurresp);
//                startDetectActity();
//                Intent add_intent = new Intent(getActivity(),ToubaoDetailActivity.class);
//                startActivity(add_intent);
//                getActivity().finish();

              /*  if (insurresp.baodanRate == 4.5) {
                    spinnerInsuranceRate.setSelection(0);
                } else if (insurresp.baodanRate == 5.0) {
                    spinnerInsuranceRate.setSelection(1);
                }
*/
                tv_baodan_people.setText(String.valueOf(insurresp.iname));
                tv_baodan_idcard.setText(String.valueOf(insurresp.icardNo));

                tv_baodan_openbank.setText(insurresp.bankName);
                tv_baodan_bank_num.setText(insurresp.bankNo);

                tv_baodan_date.setText(String.valueOf(insurresp.createtime));

                tv_baodan_tel.setText(String.valueOf(insurresp.ibaodanPhone));
                tv_baodan_address.setText(String.valueOf(insurresp.iaddress));
                tv_baodan_jing.setText(String.valueOf(insurresp.ilongitude));
                tv_baodan_wei.setText(String.valueOf(insurresp.ilatitude));
                tv_baodan_cardtype.setText(String.valueOf(insurresp.icardType));


            } else if (!success) {
                //  显示失败
                Log.d(TAG, errStr);
//                tv_info.setText(errStr);
            }
        }

        @Override
        protected void onCancelled() {
            mToubaoTask = null;
        }
    }


    public class UpdateToubaoTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;

        UpdateToubaoTask(String url, TreeMap map) {
            mUrl = url;
            mQueryMap = map;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //  attempt authentication against a network service.
            try {
                FormBody.Builder builder = new FormBody.Builder();
                // Add Params to Builder
                for (TreeMap.Entry<String, String> entry : mQueryMap.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                // Create RequestBody
                RequestBody formBody = builder.build();

                String response = HttpUtils.post(mUrl, formBody);
                Log.d(TAG, "response:" + response);

                if (HttpUtils.INSUR_UPDATE_URL.equalsIgnoreCase(mUrl)) {
                    insurresp = (BaodanBean) HttpUtils.processResp_insurInfo(response, mUrl);
                    if (insurresp == null) {
//                        errStr = getString(R.string.error_newwork);
                        errStr = "请求错误！";
                        return false;
                    }
                    if (insurresp.status != HttpRespObject.STATUS_OK) {
                        errStr = insurresp.msg;
                        return false;
                    }
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                errStr = "服务器错误！";
                return false;
            }
            //  register the new account here.

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mToubaoTask = null;
            if (success & HttpUtils.INSUR_UPDATE_URL.equalsIgnoreCase(mUrl)) {
//                InfoUtils.saveInsurInfo(InsuranceNewActivity.this, insurresp);
//                startDetectActity();
//                Intent add_intent = new Intent(getActivity(),ToubaoDetailActivity.class);
//                startActivity(add_intent);
//                getActivity().finish();
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateToubaoActivity.this)
                        .setMessage("更新成功")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                finish();

                            }
                        });
                builder.setCancelable(false);
                builder.show();


            } else if (!success) {
                //  显示失败
                Log.d(TAG, errStr);
//                tv_info.setText(errStr);
                Toast.makeText(getApplicationContext(), errStr, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateToubaoTask = null;
        }
    }


    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    requestPermission();
                }
            }
        }
    }
}


