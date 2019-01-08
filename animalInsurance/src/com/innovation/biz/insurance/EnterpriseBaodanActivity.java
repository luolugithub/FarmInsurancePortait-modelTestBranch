package com.innovation.biz.insurance;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.innovation.animalInsurance.BuildConfig;
import com.innovation.animalInsurance.R;
import com.innovation.base.BaseActivity;
import com.innovation.base.InnApplication;
import com.innovation.bean.UploadImageObject;
import com.innovation.base.Model;
import com.innovation.login.IDCardValidate;
import com.innovation.login.Utils;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.OkHttp3Util;
import com.innovation.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;
import com.innovation.base.GlobalConfigure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static com.innovation.utils.ValidatorUtils.isMobileNO;
import static com.innovation.utils.ValidatorUtils.isPhone;


/**
 * Created by Luolu on 2018/9/19.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class EnterpriseBaodanActivity extends BaseActivity {
    public static String TAG = "EnterpriseBaodanActivity";
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.editTextBaodanedPeople)
    EditText editTextBaodanedPeople;
    @BindView(R.id.radioButtonIDCard)
    RadioButton radioButtonIDCard;
    @BindView(R.id.radioBttonBusinessLicens)
    RadioButton radioBttonBusinessLicens;
    /*   @BindView(R.id.certificateTypeRadioGroup)
       RadioGroup certificateTypeRadioGroup;*/
    @BindView(R.id.editTextBaodanIDCard)
    EditText editTextBaodanIDCard;
    @BindView(R.id.imageIDCardZhengUpload)
    ImageView imageIDCardZhengUpload;
    @BindView(R.id.textViewIDPositive)
    TextView tvIdPositive;
    @BindView(R.id.textViewIDCardZhengPath)
    TextView textViewIDCardZhengPath;
    @BindView(R.id.imageIDCardFanUpload)
    ImageView imageIDCardFanUpload;
    @BindView(R.id.textViewIdCardNegative)
    TextView textViewIdCardNegative;
    @BindView(R.id.id_card_negative_photo_constraint_layout)
    ConstraintLayout idCardNegativePhotoConstraintLayout;
    @BindView(R.id.idCardFanPath)
    TextView idCardFanPath;
    @BindView(R.id.openBankCardName)
    EditText openBankCardName;
    @BindView(R.id.bankCardNumber)
    EditText bankCardNumber;
    @BindView(R.id.bankCard)
    ImageView bankCard;
    @BindView(R.id.bank_path)
    TextView bankPath;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.btnFinish)
    Button btnFinish;
    @BindView(R.id.btnnew)
    Button btnnew;


    private int certificateType = 1;
    private static final int REQUESTCODE_TAKE = 11111;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 22222;    // 图片裁切标记
    private static String IMAGE_FILE_NAME = "";// 头像文件名称
    private String imageType;
    private Uri uritempFile;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private String str_idcard_zheng = "";
    private String str_idcard_fan = "";
    private String str_bank = "";
    private String mTempToubaoNumber;
    private int mid;
    private int userId;
    private BitmapDrawable drawable;
    private File fileURLPath;
    private File tempFile;
    private File imageSystemRoot;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_baodan_enterprise;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initData() {
        XXPermissions.with(this)
                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.Group.STORAGE, Permission.Group.LOCATION) //不指定权限则自动获取清单中的危险权限
                .permission(Permission.CAMERA)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            // toastUtils.showLong(MyApplication.getAppContext(), "获取权限成功");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Toast.makeText(InnApplication.getAppContext(), "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(InnApplication.getAppContext());
                        } else {
                            Toast.makeText(InnApplication.getAppContext(), "获取权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        IMAGE_FILE_NAME = stampToDate(System.currentTimeMillis()) + ".jpg";

        GlobalConfigure.model = Model.BUILD.value();
        tvTitle.setText("新建企业临时保单");
        SharedPreferences pref = EnterpriseBaodanActivity.this.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userId = pref.getInt("uid", 0);

        ivCancel.setVisibility(View.VISIBLE);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getCurrentLocationLatLng();

        RadioGroup certificateTypeRadioGroup = findViewById(R.id.certificateTypeRadioGroup);
        certificateTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonIDCard:
                    certificateType = 1;
                    idCardNegativePhotoConstraintLayout.setVisibility(View.VISIBLE);
                    tvIdPositive.setText(getString(R.string.idPostive));
                    imageIDCardZhengUpload.setImageResource(R.color.video_bg);
                    imageIDCardFanUpload.setImageResource(R.color.video_bg);
                    str_idcard_zheng = "";
                    str_idcard_fan = "";
                    //更新照片中文字
                    break;
                case R.id.radioBttonBusinessLicens:
                    certificateType = 2;
                    idCardNegativePhotoConstraintLayout.setVisibility(View.INVISIBLE);
                    tvIdPositive.setText(R.string.businessLicense);
                    imageIDCardZhengUpload.setImageResource(R.color.video_bg);
                    str_idcard_fan = "";
                    // imageIDCardZhengUpload.setImageDrawable(null);
                    //更新照片中文字
                    break;
                default:
                    break;
            }
        });
    }

    public String str = "";

    @OnClick({R.id.imageIDCardZhengUpload, R.id.imageIDCardFanUpload, R.id.bankCard, R.id.btnNext, R.id.btnFinish, R.id.btnnew})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageIDCardZhengUpload:
                photograph("idcard_zheng");
                break;
            case R.id.imageIDCardFanUpload:
                photograph("idcard_fan");
                break;
            case R.id.bankCard:
                photograph("bank");
                break;
            case R.id.btnNext:
                break;
            case R.id.btnFinish:
                str = "btnFinish";
                btnFinish.setEnabled(false);
                if ("".equals(editTextBaodanedPeople.getText().toString())) {
                    Toast.makeText(EnterpriseBaodanActivity.this, "请输入被保险人名称！", Toast.LENGTH_LONG).show();
                    btnFinish.setEnabled(true);
                    return;
                }
                if (certificateType == 0) {
                    Toast.makeText(EnterpriseBaodanActivity.this, "请选择证件类型", Toast.LENGTH_LONG).show();
                    btnFinish.setEnabled(true);
                    return;
                }
                if (certificateType == 1) {
                    String strIDcard = IDCardValidate.validateIDcardNumber(editTextBaodanIDCard.getText().toString().trim(), true);
                    if (!(strIDcard.length() == 15 || strIDcard.length() == 18)) {
                        Toast.makeText(getApplicationContext(), strIDcard, Toast.LENGTH_SHORT).show();
                        btnFinish.setEnabled(true);
                        return;
                    }
                } else if (certificateType == 2) {
                    if (!isValid(editTextBaodanIDCard.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "请输入正确的统一社会信用代码", Toast.LENGTH_SHORT).show();
                        btnFinish.setEnabled(true);
                        return;
                    }

                }

                if (certificateType == 1) {
                    if ("".equals(str_idcard_zheng.trim())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "请上传身份证正面照片", Toast.LENGTH_LONG).show();
                        btnFinish.setEnabled(true);
                        return;
                    } else if ("".equals(str_idcard_fan.trim())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "请上传身份证反面照片", Toast.LENGTH_LONG).show();
                        btnFinish.setEnabled(true);
                        return;
                    }
                } else if (certificateType == 2) {
                    if ("".equals(str_idcard_zheng.trim())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "请上传营业执照正面照片", Toast.LENGTH_LONG).show();
                        btnFinish.setEnabled(true);
                        return;
                    }
                }
                if ("".equals(openBankCardName.getText().toString())) {
                    Toast.makeText(EnterpriseBaodanActivity.this, "开户行为空", Toast.LENGTH_LONG).show();
                    btnFinish.setEnabled(true);
                    return;
                }
                if ("".equals(bankCardNumber.getText().toString())) {
                    Toast.makeText(EnterpriseBaodanActivity.this, "银行账号为空", Toast.LENGTH_LONG).show();
                    btnFinish.setEnabled(true);
                    return;
                }
                if (str_bank.equals("")) {
                    Toast.makeText(getApplicationContext(), "未上传成功,请重新拍摄银行卡正面照片", Toast.LENGTH_SHORT).show();
                    btnFinish.setEnabled(true);
                    return;
                }

                if ("".equals(phoneNumber.getText().toString())) {
                    Toast.makeText(EnterpriseBaodanActivity.this, "联系方式为空", Toast.LENGTH_LONG).show();
                    btnFinish.setEnabled(true);
                    return;
                }

                if (!isMobileNO(phoneNumber.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(), "手机号格式有误", Toast.LENGTH_SHORT).show();
                    btnFinish.setEnabled(true);
                    return;
                } else if (isPhone(phoneNumber.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(), "电话号格式异常", Toast.LENGTH_SHORT).show();
                    btnFinish.setEnabled(true);
                    return;
                }
                    String str_random1 = createCode();
                    mTempToubaoNumber = stampToDate(System.currentTimeMillis()) + str_random1;
                    createDan();

                break;
            case R.id.btnnew:
                str = "xinjian";
                btnnew.setEnabled(false);
//                if (isOPen(EnterpriseBaodanActivity.this)) {
                    if ("".equals(editTextBaodanedPeople.getText().toString())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "被保险人不能为空！", Toast.LENGTH_LONG).show();
                        btnnew.setEnabled(true);
                        return;
                    }
                    if (certificateType == 0) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "请选择证件类型", Toast.LENGTH_LONG).show();
                        btnnew.setEnabled(true);
                        return;
                    }
                    if (certificateType == 1) {
                        String strIDcard = IDCardValidate.validateIDcardNumber(editTextBaodanIDCard.getText().toString().trim(), true);
                        if (!(strIDcard.length() == 15 || strIDcard.length() == 18)) {
                            Toast.makeText(getApplicationContext(), strIDcard, Toast.LENGTH_SHORT).show();
                            btnnew.setEnabled(true);
                            return;
                        }
                    } else if (certificateType == 2) {
                        if (!isValid(editTextBaodanIDCard.getText().toString().trim())) {
                            Toast.makeText(getApplicationContext(), "请输入正确的统一社会信用代码", Toast.LENGTH_SHORT).show();
                            btnnew.setEnabled(true);
                            return;
                        }

                    }

                    if (certificateType == 1) {
                        if ("".equals(str_idcard_zheng.trim())) {
                            Toast.makeText(EnterpriseBaodanActivity.this, "请上传身份证正面照片", Toast.LENGTH_LONG).show();
                            btnnew.setEnabled(true);
                            return;
                        } else if ("".equals(str_idcard_fan.trim())) {
                            Toast.makeText(EnterpriseBaodanActivity.this, "请上传身份证反面照片", Toast.LENGTH_LONG).show();
                            btnnew.setEnabled(true);
                            return;
                        }
                    } else if (certificateType == 2) {
                        if ("".equals(str_idcard_zheng.trim())) {
                            Toast.makeText(EnterpriseBaodanActivity.this, "请上传营业执照正面照片", Toast.LENGTH_LONG).show();
                            btnnew.setEnabled(true);
                            return;
                        }
                    }
                    if ("".equals(openBankCardName.getText().toString())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "开户行为空", Toast.LENGTH_LONG).show();
                        btnnew.setEnabled(true);
                        return;
                    }
                    if ("".equals(bankCardNumber.getText().toString())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "银行账号为空", Toast.LENGTH_LONG).show();
                        btnnew.setEnabled(true);
                        return;
                    }
                    if (str_bank.equals("")) {
                        Toast.makeText(getApplicationContext(), "未上传成功,请重新拍摄银行卡正面照片", Toast.LENGTH_SHORT).show();
                        btnnew.setEnabled(true);
                        return;
                    }
                    if ("".equals(phoneNumber.getText().toString())) {
                        Toast.makeText(EnterpriseBaodanActivity.this, "联系方式为空", Toast.LENGTH_LONG).show();
                        btnnew.setEnabled(true);
                        return;
                    }

                    if (!isMobileNO(phoneNumber.getText().toString().trim())){
                        Toast.makeText(getApplicationContext(), "手机号格式有误", Toast.LENGTH_SHORT).show();
                        btnnew.setEnabled(true);
                        return;
                    } else if (isPhone(phoneNumber.getText().toString().trim())){
                        Toast.makeText(getApplicationContext(), "电话号格式异常", Toast.LENGTH_SHORT).show();
                        btnnew.setEnabled(true);
                        return;
                    }

                        String str_random3 = createCode();
                        mTempToubaoNumber = stampToDate(System.currentTimeMillis()) + str_random3;
                        createDan();


//                }
//                else {
//                    AlertDialogManager.showMessageDialog(EnterpriseBaodanActivity.this, "提示", getString(R.string.locationwarning), new AlertDialogManager.DialogInterface() {
//                        @Override
//                        public void onPositive() {
//                            Intent intent = new Intent();
//                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivityForResult(intent, 1315);
//                        }
//
//                        @Override
//                        public void onNegative() {
//
//                        }
//                    });
//
//                }

                break;

        }
    }


    private void createDan() {
        Map map = new HashMap();
        map.put(HttpUtils.AppKeyAuthorization, "hopen");
        map.put(HttpUtils.id, PreferencesUtils.getStringValue(HttpUtils.id, InnApplication.getAppContext(), "0"));
        Map mapbody = new HashMap();
        mapbody.put("baodanNo", mTempToubaoNumber);
        mapbody.put("baodanName", PreferencesUtils.getStringValue(HttpUtils.baodanName, InnApplication.getAppContext()));
        mapbody.put("baodanType", PreferencesUtils.getStringValue(HttpUtils.baodanType, InnApplication.getAppContext()));
        mapbody.put("animalType", String.valueOf(ANIMAL_TYPE));
        mapbody.put("toubaoType", PreferencesUtils.getStringValue(HttpUtils.InsuranceType, InnApplication.getAppContext()));
        mapbody.put("baodanRate", PreferencesUtils.getStringValue(HttpUtils.insuranceRate, InnApplication.getAppContext()));
        mapbody.put("shiyangMethod", PreferencesUtils.getStringValue(HttpUtils.farmForm, InnApplication.getAppContext()));
        mapbody.put("toubaoCost", PreferencesUtils.getStringValue(HttpUtils.InsuranceCost, InnApplication.getAppContext()));
        mapbody.put("address", PreferencesUtils.getStringValue(HttpUtils.baodanApplyAddress, InnApplication.getAppContext()));
        mapbody.put("uid", PreferencesUtils.getStringValue(HttpUtils.id, InnApplication.getAppContext()));
        mapbody.put(HttpUtils.deptId, PreferencesUtils.getIntValue(HttpUtils.deptId, InnApplication.getAppContext()) + "");

        mapbody.put("longitude", String.valueOf(currentLat));
        mapbody.put("latitude", String.valueOf(currentLon));

        mapbody.put("cardType", String.valueOf(certificateType));
        mapbody.put("cardNo", editTextBaodanIDCard.getText().toString());
        mapbody.put("cardFront", str_idcard_zheng.trim());
        mapbody.put("cardBack", str_idcard_fan.trim());
        mapbody.put("name", editTextBaodanedPeople.getText().toString());
        mapbody.put("phone", phoneNumber.getText().toString());
        mapbody.put("bankNo", bankCardNumber.getText().toString());
        mapbody.put("bankName", openBankCardName.getText().toString());
        mapbody.put("bankFront", str_bank.trim());

        OkHttp3Util.doPost(HttpUtils.BaoDanadd, mapbody, map, new Callback() {
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
                                Toast.makeText(EnterpriseBaodanActivity.this, msg, Toast.LENGTH_LONG).show();
                            } else if (status == 1) {
                                Toast.makeText(EnterpriseBaodanActivity.this, msg, Toast.LENGTH_LONG).show();
                                if ("btnFinish".equals(str)) {
                                    if (fileURLPath != null) {
                                        File file = new File(fileURLPath.getAbsolutePath());
                                        boolean deleteFile = FileUtils.deleteFile(file);
                                        if (deleteFile){
                                            Log.i(TAG,"finish,调用系统拍照的fileURLPath删除成功");
                                        }
                                    }
                                    if ( tempFile != null) {
                                        File file = new File(tempFile.getAbsolutePath());
                                        boolean deleteFile = FileUtils.deleteFile(file);
                                        if (deleteFile){
                                            Log.i(TAG,"finish,调用系统拍照的tempFile删除成功");
                                        }
                                    }
                                    if ( imageSystemRoot != null) {
                                        File file = new File(imageSystemRoot.getAbsolutePath());
                                        boolean deleteFile = FileUtils.deleteFile(file);
                                        if (deleteFile){
                                            Log.i(TAG,"finish,调用系统拍照的imageSystemRoot删除成功");
                                        }
                                    }
                                    finish();
                                } else if ("xinjian".equals(str)) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    mid = jsonObject1.getInt("id");
                                    createYan();
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


    private void createYan() {
        Map map = new HashMap();
        map.put(HttpUtils.AppKeyAuthorization, "hopen");
        Map mapbody = new HashMap();
        mapbody.put("bdsId", String.valueOf(mid));
        mapbody.put("baodanNo", mTempToubaoNumber);
        mapbody.put("longitude", String.valueOf(currentLat));
        mapbody.put("latitude", String.valueOf(currentLon));

        mapbody.put("cardType", certificateType + "");
        mapbody.put("cardNo", editTextBaodanIDCard.getText().toString());
        mapbody.put("cardFront", str_idcard_zheng.trim());
        mapbody.put("cardBack", str_idcard_fan.trim());
        mapbody.put("name", editTextBaodanedPeople.getText().toString());
        mapbody.put("phone", phoneNumber.getText().toString());
        mapbody.put("bankNo", bankCardNumber.getText().toString());
        mapbody.put("bankName", openBankCardName.getText().toString());
        mapbody.put("bankFront", str_bank.trim());
        mapbody.put("uid", String.valueOf(userId));

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
                                Toast.makeText(EnterpriseBaodanActivity.this, msg, Toast.LENGTH_LONG).show();
                            } else if (status == 1) {
                                Toast.makeText(EnterpriseBaodanActivity.this, msg, Toast.LENGTH_LONG).show();
                                if (fileURLPath != null) {
                                    File file = new File(fileURLPath.getAbsolutePath());
                                    boolean deleteFile = FileUtils.deleteFile(file);
                                    if (deleteFile){
                                        Log.i(TAG,"finish,调用系统拍照的fileURLPath删除成功");
                                    }
                                }
                                if ( tempFile != null) {
                                    File file = new File(tempFile.getAbsolutePath());
                                    boolean deleteFile = FileUtils.deleteFile(file);
                                    if (deleteFile){
                                        Log.i(TAG,"finish,调用系统拍照的tempFile删除成功");
                                    }
                                }
                                if ( imageSystemRoot != null) {
                                    File file = new File(imageSystemRoot.getAbsolutePath());
                                    boolean deleteFile = FileUtils.deleteFile(file);
                                    if (deleteFile){
                                        Log.i(TAG,"finish,调用系统拍照的imageSystemRoot删除成功");
                                    }
                                }
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    /**
     * 拍照
     *
     * @param imageType 图片采集类型
     */
    private void photograph(String imageType) {
        this.imageType = imageType;
         tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //下面这句指定调用相机拍照后的照片存储的路径
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(EnterpriseBaodanActivity.this, BuildConfig.APPLICATION_ID + ".provider", tempFile));
        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_TAKE:// 调用相机拍照
                crop(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                 imageSystemRoot = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                Log.i(TAG,"imageSystemRoot:"+imageSystemRoot.getAbsolutePath());
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    try {
                        setPicToView();
                    } catch (Exception e) {
                        Toast.makeText(EnterpriseBaodanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                break;
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (fileURLPath != null) {
            boolean deleteFile = FileUtils.deleteFile(fileURLPath);
            if (deleteFile){
                Log.i(TAG,"onDestroy,调用系统拍照的fileURLPath删除成功");
            }
        }
        if ( tempFile != null) {
            boolean deleteFile = FileUtils.deleteFile(tempFile);
            if (deleteFile){
                Log.i(TAG,"onDestroy,调用系统拍照的tempFile删除成功");
            }
        }
        if ( imageSystemRoot != null) {
            boolean deleteFile = FileUtils.deleteFile(imageSystemRoot);
            if (deleteFile){
                Log.i(TAG,"onDestroy,调用系统拍照的imageSystemRoot删除成功");
            }
        }
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
                EnterpriseBaodanActivity.this.startActivityForResult(intent, REQUESTCODE_CUTTING);
            }
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
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

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView() throws Exception {
        // 取得SDCard图片路径做显示
        Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
        drawable = new BitmapDrawable(null, photo);
        String urlpath = FileUtils.saveFile(EnterpriseBaodanActivity.this, "temphead.jpg", photo);

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


         fileURLPath = new File(urlpath);
        upload_zipImage(fileURLPath, userId);
    }

    private UploadImageObject upload_zipImage(File zipFile_image, int uid) {
        UploadImageObject imgResp = HttpUtils.uploadImage(zipFile_image, uid);

        if (imgResp == null || imgResp.status != HttpRespObject.STATUS_OK) {
            Toast.makeText(EnterpriseBaodanActivity.this, imgResp.msg, Toast.LENGTH_SHORT).show();
            return imgResp;
        }
        if (imageType.indexOf("idcard_zheng") > -1) {
            str_idcard_zheng = imgResp.upload_imagePath;
        } else if (imageType.indexOf("idcard_fan") > -1) {
            str_idcard_fan = imgResp.upload_imagePath;
        } else if (imageType.indexOf("bank") > -1) {
            str_bank = imgResp.upload_imagePath;
        }
        if (imageType.contains("idcard_zheng")) {
            imageIDCardZhengUpload.setImageDrawable(drawable);
        } else if (imageType.contains("idcard_fan")) {
            imageIDCardFanUpload.setImageDrawable(drawable);
        } else if (imageType.contains("bank")) {
            bankCard.setImageDrawable(drawable);
        }
        return imgResp;
    }

    private void getCurrentLocationLatLng() {
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
    };

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
}
