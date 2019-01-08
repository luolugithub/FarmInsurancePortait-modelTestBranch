package com.innovation.biz.Insured;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.innovation.animalInsurance.BuildConfig;
import com.innovation.animalInsurance.R;
import com.innovation.bean.BaodanBean;
import com.innovation.bean.MultiBaodanBean;
import com.innovation.bean.UploadImageObject;
import com.innovation.base.Model;
import com.innovation.login.IDCardValidate;
import com.innovation.login.Utils;
import com.innovation.utils.ConstUtils;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.ValidatorUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.tensorflow.demo.DetectorActivity;
import com.innovation.base.GlobalConfigure;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static com.innovation.base.InnApplication.getStringTouboaExtra;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;


/**
 * Author by luolu, Date on 2018/8/16.
 * COMPANY：InnovationAI
 */

public class InsuredActivity extends AppCompatActivity {
    private static final String mTag = "InsuredActivity";
    private static final int PERMISSIONS_REQUEST = 1;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_baodan_num)
    EditText tvBaodanNum;
    @BindView(R.id.tv_baodan_pay)
    EditText tvBaodanPay;
    @BindView(R.id.tv_baodan_people)
    EditText tvBaodanPeople;
    @BindView(R.id.id_card_radio_button)
    RadioButton idCardRadioButton;
    @BindView(R.id.id_business_licens)
    RadioButton idBusinessLicens;
    @BindView(R.id.certificate_type_radioGroup)
    RadioGroup certificateTypeRadioGroup;
    @BindView(R.id.tv_baodan_idcard)
    EditText tvBaodanIdcard;
    @BindView(R.id.btn_idcard_zheng_upload)
    ImageView btnIdcardZhengUpload;
    @BindView(R.id.tv_id_positive)
    TextView tvIdPositive;
    @BindView(R.id.idcard_zheng_path)
    TextView idcardZhengPath;
    @BindView(R.id.btn_idcard_fan_upload)
    ImageView btnIdcardFanUpload;
    @BindView(R.id.tv_id_negative)
    TextView tvIdNegative;
    @BindView(R.id.id_card_negative_photo_constraint_layout)
    ConstraintLayout idCardNegativePhotoConstraintLayout;
    @BindView(R.id.idcard_fan_path)
    TextView idcardFanPath;
    @BindView(R.id.tv_baodan_openbank)
    EditText tvBaodanOpenbank;
    @BindView(R.id.tv_baodan_bank_num)
    EditText tvBaodanBankNum;
    @BindView(R.id.btn_bank_upload)
    ImageView btnBankUpload;
    @BindView(R.id.bank_path)
    TextView bankPath;
    @BindView(R.id.tv_baodan_tel)
    EditText tvBaodanTel;
    @BindView(R.id.rate)
    EditText rate;
    @BindView(R.id.spinnerInsureType)
    Spinner spinnerInsureType;
    @BindView(R.id.scale)
    RadioButton scale;
    @BindView(R.id.free_range)
    RadioButton freeRange;
    @BindView(R.id.shiyangMethodRadioGroup)
    RadioGroup shiyangMethodRadioGroup;
    @BindView(R.id.editTextToubaoCost)
    EditText editTextToubaoCost;
    @BindView(R.id.tv_baodan_date)
    TextView tvBaodanDate;
    @BindView(R.id.tv_baodan_address)
    EditText tvBaodanAddress;
    @BindView(R.id.btn_toubaoImageAcquisition)
    Button btnToubaoImageAcquisition;
    // TODO: 2018/8/3
    private String pigType;
    private int mYear;
    private int mMonth;
    private int mDay;
    private AddToubaoTask mAddToubaoTask = null;
    private String errStr = "";
    //声明高德AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    private final Random random = new Random();
    private int userId;
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private String mTempToubaoNumber;
    private static final int REQUESTCODE_TAKE = 11111;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 22222;    // 图片裁切标记
    private static String IMAGE_FILE_NAME = "";// 头像文件名称
    private String imageType;
    private Uri uritempFile;
    private static String expression;
    //证件类型
    private int certificateType = -1;

    private String str_idcard_zheng_path = "";
    private String str_idcard_fan_path = "";
    private String str_bank_path = "";
    //投保类型
    private int insuredType = -1;
    //    饲养类型
    private int feedType = -1;
    private final DatePickerDialog.OnDateSetListener onDateSetListener;
    private MultiBaodanBean newBaodanResponse;
    private Bitmap head;

    public InsuredActivity() {
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                if (mMonth + 1 < 10) {
                    if (mDay < 10) {
                        days = String.valueOf(mYear) + "-" + "0" +
                                (mMonth + 1) + "-" + "0" + mDay;
                    } else {
                        days = String.valueOf(mYear) + "-" + "0" +
                                (mMonth + 1) + "-" + mDay;
                    }

                } else {
                    if (mDay < 10) {
                        days = String.valueOf(mYear) + "-" +
                                (mMonth + 1) + "-" + "0" + mDay;
                    } else {
                        days = String.valueOf(mYear) + "-" +
                                (mMonth + 1) + "-" + mDay;
                    }
                }
                tvBaodanDate.setText(days.trim());
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insured);
        ButterKnife.bind(this);
        expression = getString(R.string.expression);
        GlobalConfigure.model = Model.BUILD.value();
        String str_random = createCode();
        mTempToubaoNumber = stampToDate(System.currentTimeMillis()) + str_random;
        requestPermission();
        SharedPreferences pref = InsuredActivity.this.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userId = pref.getInt("uid", 0);
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tvBaodanDate.setText(sdf.format(new Date(System.currentTimeMillis())));
        getCurrentLocationLatLng();
        IMAGE_FILE_NAME = stampToDate(System.currentTimeMillis()) + ".jpg";
        tvTitle.setText(R.string.build_insure);
        ivCancel.setVisibility(View.VISIBLE);
        certificateTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.id_card_radio_button:
                    certificateType = 1;
                    idCardNegativePhotoConstraintLayout.setVisibility(View.VISIBLE);
                    tvIdPositive.setText(getString(R.string.idPostive));
                    //更新照片中文字
                    break;
                case R.id.id_business_licens:
                    certificateType = 2;
                    idCardNegativePhotoConstraintLayout.setVisibility(View.INVISIBLE);
                    tvIdPositive.setText(R.string.businessLicense);
                    //更新照片中文字
                    break;
            }
        });
// TODO: 2018/8/16 By:LuoLu  spinnerInsureType 动态适配
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ConstUtils.geInsureTypeCaptions(ANIMAL_TYPE));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsureType.setAdapter(arrayAdapter);
        spinnerInsureType.setSelection(0, true);


        shiyangMethodRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.scale:
                    feedType = 1;
                    break;
                case R.id.free_range:
                    feedType = 2;
                    break;
            }
        });
        certificateTypeRadioGroup.check(R.id.id_card_radio_button);

    }

    /**
     * 拍照
     *
     * @param imageType 图片采集类型
     */
    private void photograph(String imageType) {
        this.imageType = imageType;
        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //下面这句指定调用相机拍照后的照片存储的路径
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(InsuredActivity.this, BuildConfig.APPLICATION_ID + ".provider", tempFile));
        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_TAKE:// 调用相机拍照
                crop(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    try {
                        setPicToView();
                    } catch (Exception e) {
                        Toast.makeText(InsuredActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                break;
        }
    }


    @Override
    protected void onDestroy() {
//        if(mProgressDialog.isShowing())
//        {
//            mProgressDialog.dismiss();
//        }
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

    // TODO: 2018/9/6 By:LuoLu
    @Override
    public void onBackPressed() {
        // your code.
    }

    /**
     * 裁剪图片方法实现
     */
    private void crop(String imagePath) {
        // TODO: 2018/8/24 By:LuoLu  "No Activity found to handle Intent"
        try {
            if (imagePath != null) {
                //call the standard crop action intent (the user device may not support it)
                Intent intent = new Intent("com.android.camera.action.CROP");
                //indicate image type and Uri
                intent.setDataAndType(getImageContentUri(new File(imagePath)), "image/*");
                intent.putExtra("crop", "false");
                intent.putExtra("scale", true);
                intent.putExtra("return-data", false);
                uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                InsuredActivity.this.startActivityForResult(intent, REQUESTCODE_CUTTING);
            }
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView() throws Exception {
        // 取得SDCard图片路径做显示
        Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
        Drawable drawable = new BitmapDrawable(null, photo);
        String urlpath = FileUtils.saveFile(InsuredActivity.this, "temphead.jpg", photo);

        // TODO: 2018/8/21 By:LuoLu
        File file = new File(urlpath);
        Log.e("file", file.getPath());
        file.mkdirs();
        long i = System.currentTimeMillis();
        file = new File(file.getPath());
        Log.e("fileNew：", file.getPath());
        OutputStream out = new FileOutputStream(file.getPath());
        boolean flag = photo.compress(Bitmap.CompressFormat.JPEG, 30, out);
        Log.e("flag:", "图片压缩成功" + flag);

        if (imageType.contains("idcard_zheng")) {
            btnIdcardZhengUpload.setImageDrawable(drawable);
        } else if (imageType.contains("idcard_fan")) {
            btnIdcardFanUpload.setImageDrawable(drawable);
        } else if (imageType.contains("bank")) {
            btnBankUpload.setImageDrawable(drawable);
        }
        File fileURLPath = new File(urlpath);
        upload_zipImage(fileURLPath, userId);
    }

    private UploadImageObject upload_zipImage(File zipFile_image, int uid) {
        UploadImageObject imgResp = HttpUtils.uploadImage(zipFile_image, uid);

        if (imgResp == null || imgResp.status != HttpRespObject.STATUS_OK) {
            Toast.makeText(InsuredActivity.this, imgResp.msg, Toast.LENGTH_SHORT).show();
            return imgResp;
        }
        if (imageType.indexOf("idcard_zheng") > -1) {
            str_idcard_zheng_path = imgResp.upload_imagePath;
        } else if (imageType.indexOf("idcard_fan") > -1) {
            str_idcard_fan_path = imgResp.upload_imagePath;
        } else if (imageType.indexOf("bank") > -1) {
            str_bank_path = imgResp.upload_imagePath;
        }
        return imgResp;
    }

    /**
     * 转换 content:// uri
     *
     * @param imageFile 图片文件
     * @return url
     */
    private Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        try (Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }
    }

    private void getCurrentLocationLatLng() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        // 同时使用网络定位和GPS定位,优先返回最高精度的定位结果,以及对应的地址描述信息
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。默认连续定位 切最低时间间隔为1000ms
        mLocationOption.setInterval(3500);
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    AMapLocation amapLocation;
    private final AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    InsuredActivity.this.amapLocation = amapLocation;
                    String str_address = amapLocation.getAddress();
                    tvBaodanAddress.setText(str_address);
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

    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    private static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private static String days;

    /**
     * 日期选择器对话框监听
     */


    @OnClick({R.id.iv_cancel, R.id.btn_idcard_zheng_upload, R.id.btn_idcard_fan_upload, R.id.btn_bank_upload, R.id.tv_baodan_date, R.id.btn_toubaoImageAcquisition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.btn_idcard_zheng_upload:
                photograph("idcard_zheng");
                break;
            case R.id.btn_idcard_fan_upload:
                photograph("idcard_fan");
                break;
            case R.id.btn_bank_upload:
                photograph("bank");
                break;
            case R.id.tv_baodan_date:
                new DatePickerDialog(InsuredActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
                break;
            case R.id.btn_toubaoImageAcquisition: {
                btnToubaoImageAcquisition.setEnabled(false);
                String str_baodan_number = mTempToubaoNumber;
                getStringTouboaExtra = str_baodan_number;

                String str_baodan_date = tvBaodanDate.getText().toString();
                String str_baodan_people = tvBaodanPeople.getText().toString();
                String str_baodan_tel = tvBaodanTel.getText().toString();
                String str_baodan_address = tvBaodanAddress.getText().toString();
                String str_baodan_idcard = tvBaodanIdcard.getText().toString();

                insuredType = ConstUtils.getInsureTypeCodeIntByCaption(spinnerInsureType.getSelectedItem().toString());
                String str_baodan_openbank = tvBaodanOpenbank.getText().toString();
                String str_baodan_bank_num = tvBaodanBankNum.getText().toString();
                String strIDcard = IDCardValidate.validateIDcardNumber(str_baodan_idcard, true);
                if (rate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入保险费率", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                }
                if (str_baodan_number.equals("")) {
                    Toast.makeText(getApplicationContext(), "请重新获取验标单号", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                if (str_baodan_people.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入被保险人名称", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                if (certificateType == -1) {
                    Toast.makeText(getApplicationContext(), "请选择证件类型", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                if (certificateType == 1) {
                    if (!(strIDcard.length() == 15 || strIDcard.length() == 18)) {
                        Toast.makeText(getApplicationContext(), strIDcard, Toast.LENGTH_SHORT).show();
                        btnToubaoImageAcquisition.setEnabled(true);
                        return;
                    }
                    // TODO: 2018/8/10 By:LuoLu
                    if (str_idcard_zheng_path.equals("")) {
                        Toast.makeText(getApplicationContext(), "未上传成功,请重新拍摄身份证正面照片", Toast.LENGTH_SHORT).show();
                        btnToubaoImageAcquisition.setEnabled(true);
                        return;
                    }
                    if (str_idcard_fan_path.equals("")) {
                        Toast.makeText(getApplicationContext(), "未上传成功,请重新拍摄身份证反面照片", Toast.LENGTH_SHORT).show();
                        btnToubaoImageAcquisition.setEnabled(true);
                        return;
                    }
                } else if (certificateType == 2) {
                    if (!ValidatorUtils.isLicense18(str_baodan_idcard)) {
                        Toast.makeText(getApplicationContext(), "请输入正确的统一社会信用代码", Toast.LENGTH_SHORT).show();
                        btnToubaoImageAcquisition.setEnabled(true);
                        return;
                    }
                    // TODO: 2018/8/10 By:LuoLu
                    if (str_idcard_zheng_path.equals("")) {
                        Toast.makeText(getApplicationContext(), "未上传成功,请重新拍摄营业执照的照片", Toast.LENGTH_SHORT).show();
                        btnToubaoImageAcquisition.setEnabled(true);
                        return;
                    }

                }

                if (str_baodan_openbank.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入开户行名称", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }

                if (str_baodan_bank_num.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入银行账号", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }

                if (str_bank_path.equals("")) {
                    Toast.makeText(getApplicationContext(), "未上传成功,请重新拍摄银行卡正面照片", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }

                if (str_baodan_tel.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }

                if (!isPhoneNumberValid(str_baodan_tel.trim())) {
                    Toast.makeText(getApplicationContext(), "手机号格式有误", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                // TODO: 2018/8/4
                //投保险种
                if (insuredType == -1) {
                    Toast.makeText(getApplicationContext(), "请选择投保险种", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }

                //饲养方式
                if (feedType == -1) {
                    Toast.makeText(getApplicationContext(), "请选择饲养方式", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                //金额
                if (editTextToubaoCost.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写金额", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                if (str_baodan_date.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入验标日期", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                if (amapLocation == null) {
                    Toast.makeText(getApplicationContext(), "获取地址失败", Toast.LENGTH_SHORT).show();
                    btnToubaoImageAcquisition.setEnabled(true);
                    return;
                }
                TreeMap<String, Object> query = new TreeMap<>();
                query.put("name", str_baodan_people.trim());
                query.put("cardType", certificateType);
                query.put("cardNo", str_baodan_idcard.trim());
                query.put("baodanNo", str_baodan_number);
                query.put("baodanType", "1");
                query.put("toubaoKind", "1");
                query.put("amount", "1000");
                query.put("money", "1");
                query.put("proxyName", "投保");
                query.put("phone", str_baodan_tel.trim());
                query.put("address", str_baodan_address.trim());
                query.put("longitude", amapLocation.getLongitude() + "");
                query.put("latitude", amapLocation.getLatitude() + "");
                query.put("baodanTime", str_baodan_date.trim() + " 00:00:00");
                query.put("animalType", String.valueOf(ANIMAL_TYPE));
                query.put("baodanRate", rate.getText().toString());
                query.put("cardFront", str_idcard_zheng_path.trim());
                query.put("cardBack", str_idcard_fan_path.trim());
                query.put("bankNo", str_baodan_bank_num.trim());
                query.put("bankName", str_baodan_openbank.trim());
                query.put("bankFront", str_bank_path.trim());
                query.put("uid", String.valueOf(userId));
                // TODO: 2018/8/3 insuredType   feedType
                query.put("toubaoType", insuredType);
                query.put("pigType", insuredType);
                if (feedType == 1) {
                    query.put("shiyangMethod", "规模化养殖");
                } else {
                    query.put("shiyangMethod", "散养");
                }
                query.put("toubaoCost", editTextToubaoCost.getText().toString());
                mAddToubaoTask = new AddToubaoTask(HttpUtils.INSUR_NEW_URL, query);
                mAddToubaoTask.execute((Void) null);
            }
            break;
        }
    }

    class AddToubaoTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUrl;
        private final TreeMap<String, Object> mQueryMap;

        AddToubaoTask(String url, TreeMap<String, Object> map) {
            mUrl = url;
            mQueryMap = map;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                FormBody.Builder builder = new FormBody.Builder();
                for (TreeMap.Entry<String, Object> entry : mQueryMap.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue().toString());
                }
                RequestBody formBody = builder.build();
                String response = HttpUtils.post(mUrl, formBody);
                Log.d(mTag, mUrl + "\nresponse:" + response);
                newBaodanResponse = HttpUtils.processResp_new_detail_query(response);
                if (newBaodanResponse == null) {
                    mProgressHandler.sendEmptyMessage(40);
                    return false;
                }
                if (newBaodanResponse.status == -4) {
                    mProgressHandler.sendEmptyMessage(-44);
                    return false;
                }
                if (newBaodanResponse.status == 0) {
                    mProgressHandler.sendEmptyMessage(-440);
                    return false;
                }
                if (HttpUtils.INSUR_NEW_URL.equalsIgnoreCase(mUrl)) {
                    BaodanBean insurresp = (BaodanBean) HttpUtils.processResp_insurInfo(response, mUrl);
                    if (insurresp == null) {
                        errStr = "请求错误！";
                        return false;
                    }
                    if (insurresp.status != HttpRespObject.STATUS_OK) {
                        errStr = insurresp.msg;
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                errStr = "服务器错误！";
                mProgressHandler.sendEmptyMessage(441);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddToubaoTask = null;
            if (success & HttpUtils.INSUR_NEW_URL.equalsIgnoreCase(mUrl)) {
                Intent intent = new Intent(InsuredActivity.this, DetectorActivity.class);
                intent.putExtra("ToubaoTempNumber", mTempToubaoNumber);
                startActivity(intent);
                collectNumberHandler.sendEmptyMessage(2);
                finish();

            } else if (!success) {
                //  显示失败
                Log.d(mTag, errStr);
                btnToubaoImageAcquisition.setEnabled(true);
                mProgressHandler.sendEmptyMessage(441);
            }
        }

        @Override
        protected void onCancelled() {
            mAddToubaoTask = null;
        }
    }

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (hasPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                }
            }
        }
    }

    /**
     * 将时间戳转换为时间
     */
    private String stampToDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    private boolean isValid(String businessCode) {
        if ((businessCode.equals("")) || businessCode.length() != 18) {
            return false;
        }
        String baseCode = "0123456789ABCDEFGHJKLMNPQRTUWXY";
        char[] baseCodeArray = baseCode.toCharArray();
        Map<Character, Integer> codes = new HashMap<>();
        for (int i = 0; i < baseCode.length(); i++) {
            codes.put(baseCodeArray[i], i);
        }
        char[] businessCodeArray = businessCode.toCharArray();
        Character check = businessCodeArray[17];
        if (baseCode.indexOf(check) == -1) {
            return false;
        }
        int[] wi = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            Character key = businessCodeArray[i];
            if (baseCode.indexOf(key) == -1) {
                return false;
            }
            sum += (codes.get(key) * wi[i]);
        }
        int value = 31 - sum % 31;
        return value == codes.get(check);
    }


    @SuppressLint("HandlerLeak")
    private Handler mProgressHandler = new Handler() {
        ProgressDialog mProgressDialog = null;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 51:
//                    mProgressDialog.setTitle(R.string.dialog_title);
                    mProgressDialog = new ProgressDialog(InsuredActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("正在处理......");
                    mProgressDialog.show();
                    break;
                case 61:
                    mProgressDialog = new ProgressDialog(InsuredActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("处理成功......");
                    mProgressDialog.show();
                case 41:
                    mProgressDialog.dismiss();
                    android.support.v7.app.AlertDialog.Builder innerBuilder14 = new android.support.v7.app.AlertDialog.Builder(InsuredActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("网络异常！请重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    innerBuilder14.setCancelable(false);
                    innerBuilder14.show();
                    break;
                case 44:
                    mProgressDialog.dismiss();
                    break;
                case 40:
                    AlertDialog.Builder tcaBuilder = new AlertDialog.Builder(InsuredActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("网络异常！请重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    tcaBuilder.create();
                    tcaBuilder.setCancelable(false);
                    tcaBuilder.show();

                    break;
                case -44:
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsuredActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(newBaodanResponse.msg)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    builder.create();
                    builder.setCancelable(false);
                    builder.show();

                    break;
                case -440:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(InsuredActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(newBaodanResponse.msg)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    builder1.create();
                    builder1.setCancelable(false);
                    builder1.show();

                    break;
                case 2222:
                    AlertDialog.Builder builder22 = new AlertDialog.Builder(InsuredActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("正在调用相机拍照，确定返回吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i = new Intent(InsuredActivity.this, InsuredActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    builder22.create();
                    builder22.setCancelable(false);
                    builder22.show();
                    break;
                case 441:
                    AlertDialog.Builder builder441 = new AlertDialog.Builder(InsuredActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("服务端异常，验标单提交失败！")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder441.create();
                    builder441.setCancelable(false);
                    builder441.show();
                    break;
                default:
                    break;
            }
        }
    };

}
