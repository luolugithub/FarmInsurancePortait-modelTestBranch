package com.innovation.biz.Insured;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.bean.LiPeiLocalBean;
import com.innovation.bean.PayInfoCheckResultBean;
import com.innovation.bean.ResultBean;
import com.innovation.base.Model;
import com.innovation.biz.processor.PayDataProcessor;
import com.innovation.location.AlertDialogManager;
import com.innovation.location.LocationManager;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.IDCardValidate;
import com.innovation.login.Utils;
import com.innovation.login.view.HomeActivity;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.PreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import org.tensorflow.demo.DetectorActivity;
import com.innovation.base.GlobalConfigure;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static com.innovation.login.view.HomeActivity.isOPen;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;


/**
 * Created by Luolu on 2018/9/18.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class PayActivity extends AppCompatActivity {


    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.checkedBaodanNo)
    EditText checkedBaodanNo;
    @BindView(R.id.idCardRadioButton)
    RadioButton idCardRadioButton;
    @BindView(R.id.idBusinessLicens)
    RadioButton idBusinessLicens;
    @BindView(R.id.certificateTypeRadioGroup)
    RadioGroup certificateTypeRadioGroup;
    @BindView(R.id.etLipeiIdcard)
    EditText etLipeiIdcard;
    @BindView(R.id.payReasonSpinner)
    Spinner payReasonSpinner;
    @BindView(R.id.quSpinner)
    Spinner quSpinner;
    @BindView(R.id.sheSpinner)
    Spinner sheSpinner;
    @BindView(R.id.lanSpinner)
    Spinner lanSpinner;
    @BindView(R.id.animalEarsTagNo)
    EditText animalEarsTagNo;
    @BindView(R.id.payImageAcquisition)
    Button payImageAcquisition;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    private int userId;
    private PayAnimalApplyTask payAnimalApplyTask;
    private String TAG = "PayActivity";
    private String errString;
    private PayInfoCheckResultBean payInfoCheckResultBean;
    private Gson gson;
    private int certificateType = -1;
    private ResultBean bean;
    private String lipname;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        GlobalConfigure.model = Model.VERIFY.value();
        tvTitle.setText(R.string.pay_apply_animal);
        ivCancel.setVisibility(View.VISIBLE);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String[] strReason = new String[]{"火灾", "风灾", "暴雨", "洪水", "内涝", "冷冻/冻灾", "雷电", "冰雹/雹灾", "地震", "爆炸", "建筑物倒塌/空中运行物体坠落", "传染病（疫病）", "非传染病", "难产", "泥石流/山体滑坡", "扑杀", "疫病/疾病免疫副反应", "中毒", "其它意外事故"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strReason);
        payReasonSpinner.setAdapter(adapter);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.area));
        quSpinner.setAdapter(arrayAdapter);
        quSpinner.setSelection(0, true);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.area2));
        sheSpinner.setAdapter(arrayAdapter2);
        sheSpinner.setSelection(0, true);
        ArrayAdapter arrayAdapter3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.area3));
        lanSpinner.setAdapter(arrayAdapter3);
        lanSpinner.setSelection(0, true);
        SharedPreferences pref = getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userId = pref.getInt("uid", 0);

        gson = new Gson();
        payInfoCheckResultBean = new PayInfoCheckResultBean();

        LocationManager.getInstance(PayActivity.this).startLocation();
        databaseHelper = new DatabaseHelper(PayActivity.this);
    }

    private String strfleg = "";

    @OnClick({R.id.idCardRadioButton, R.id.idBusinessLicens, R.id.certificateTypeRadioGroup, R.id.payImageAcquisition, R.id.savepay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.idCardRadioButton:
                certificateType = 1;
                break;
            case R.id.idBusinessLicens:
                certificateType = 2;
                break;
            case R.id.certificateTypeRadioGroup:
                certificateTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.id_card_radio_button:
                            certificateType = 1;
                            break;
                        case R.id.id_business_licens:
                            certificateType = 2;
                            break;
                    }
                });
                break;
            case R.id.savepay:
                if (isOPen(PayActivity.this)) {
                    payImageAcquisition.setEnabled(false);

                    if (checkedBaodanNo.getText().toString().trim().matches("")) {
                        Toast.makeText(this, "保单号不能为空！", Toast.LENGTH_SHORT).show();
                        payImageAcquisition.setEnabled(true);
                        return;
                    }
                    if (certificateType == -1) {
                        Toast.makeText(getApplicationContext(), "请选择证件类型", Toast.LENGTH_SHORT).show();
                        payImageAcquisition.setEnabled(true);
                        return;
                    }
                    if (certificateType == 1) {
                        String strIDcard = IDCardValidate.validateIDcardNumber(etLipeiIdcard.getText().toString().trim(), true);
                        if (!(strIDcard.length() == 15 || strIDcard.length() == 18)) {
                            Toast.makeText(getApplicationContext(), strIDcard, Toast.LENGTH_SHORT).show();
                            payImageAcquisition.setEnabled(true);
                            return;
                        }
                    } else if (certificateType == 2) {
                        if (!isValid(etLipeiIdcard.getText().toString().trim())) {
                            Toast.makeText(getApplicationContext(), "请输入正确的统一社会信用代码", Toast.LENGTH_SHORT).show();
                            payImageAcquisition.setEnabled(true);
                            return;
                        }

                    }
                    if (payReasonSpinner.getSelectedItem().toString().matches("")) {
                        Toast.makeText(this, "请选择出险原因！", Toast.LENGTH_SHORT).show();
                        payImageAcquisition.setEnabled(true);
                        return;
                    }

                    TreeMap treeMapInfoCheck = new TreeMap<String, String>();
                    treeMapInfoCheck.put("baodanNoReal", checkedBaodanNo.getText().toString().trim());
                    treeMapInfoCheck.put("reason", payReasonSpinner.getSelectedItem().toString().trim());
                    treeMapInfoCheck.put("cardNo", etLipeiIdcard.getText().toString().trim());
                    treeMapInfoCheck.put("uid", String.valueOf(userId) == null ? "" : String.valueOf(userId));
                    treeMapInfoCheck.put("yiji", quSpinner.getSelectedItem().toString());
                    treeMapInfoCheck.put("erji", sheSpinner.getSelectedItem().toString());
                    treeMapInfoCheck.put("sanji", lanSpinner.getSelectedItem().toString());
                    treeMapInfoCheck.put("pigNo", animalEarsTagNo.getText().toString().trim());
                    PreferencesUtils.saveKeyValue("reason", payReasonSpinner.getSelectedItem().toString().trim(), PayActivity.this);
                    PreferencesUtils.saveKeyValue("cardnum", etLipeiIdcard.getText().toString().trim(), PayActivity.this);
                    PreferencesUtils.saveKeyValue("baodannum", checkedBaodanNo.getText().toString().trim(), PayActivity.this);
                    new PayDataProcessor(PayActivity.this).transerPayData(
                            checkedBaodanNo.getText().toString().trim(),
                            payReasonSpinner.getSelectedItem().toString().trim(),
                            quSpinner.getSelectedItem().toString(),
                            sheSpinner.getSelectedItem().toString(),
                            lanSpinner.getSelectedItem().toString(),
                            animalEarsTagNo.getText().toString().trim(),
                            etLipeiIdcard.getText().toString().trim());
                    strfleg = "save";
                    payAnimalApplyTask = new PayAnimalApplyTask(HttpUtils.ANIMAL_PAY_INFOCHECK, treeMapInfoCheck);
                    payAnimalApplyTask.execute((Void) null);
                } else {
                    AlertDialogManager.showMessageDialog(PayActivity.this, "提示", getString(R.string.locationwarning), new AlertDialogManager.DialogInterface() {
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
                break;


            case R.id.payImageAcquisition:
                if (isOPen(PayActivity.this)) {
                    payImageAcquisition.setEnabled(false);

                    if (checkedBaodanNo.getText().toString().trim().matches("")) {
                        Toast.makeText(this, "保单号不能为空！", Toast.LENGTH_SHORT).show();
                        payImageAcquisition.setEnabled(true);
                        return;
                    }
                    if (certificateType == -1) {
                        Toast.makeText(getApplicationContext(), "请选择证件类型", Toast.LENGTH_SHORT).show();
                        payImageAcquisition.setEnabled(true);
                        return;
                    }
                    if (certificateType == 1) {
                        String strIDcard = IDCardValidate.validateIDcardNumber(etLipeiIdcard.getText().toString().trim(), true);
                        if (!(strIDcard.length() == 15 || strIDcard.length() == 18)) {
                            Toast.makeText(getApplicationContext(), strIDcard, Toast.LENGTH_SHORT).show();
                            payImageAcquisition.setEnabled(true);
                            return;
                        }
                    } else if (certificateType == 2) {
                        if (!isValid(etLipeiIdcard.getText().toString().trim())) {
                            Toast.makeText(getApplicationContext(), "请输入正确的统一社会信用代码", Toast.LENGTH_SHORT).show();
                            payImageAcquisition.setEnabled(true);
                            return;
                        }

                    }
                    if (payReasonSpinner.getSelectedItem().toString().matches("")) {
                        Toast.makeText(this, "请选择出险原因！", Toast.LENGTH_SHORT).show();
                        payImageAcquisition.setEnabled(true);
                        return;
                    }

                    TreeMap treeMapInfoCheck = new TreeMap<String, String>();
                    treeMapInfoCheck.put("baodanNoReal", checkedBaodanNo.getText().toString().trim());
                    treeMapInfoCheck.put("reason", payReasonSpinner.getSelectedItem().toString().trim());
                    treeMapInfoCheck.put("cardNo", etLipeiIdcard.getText().toString().trim());
                    treeMapInfoCheck.put("uid", String.valueOf(userId) == null ? "" : String.valueOf(userId));
                    treeMapInfoCheck.put("yiji", quSpinner.getSelectedItem().toString());
                    treeMapInfoCheck.put("erji", sheSpinner.getSelectedItem().toString());
                    treeMapInfoCheck.put("sanji", lanSpinner.getSelectedItem().toString());
                    treeMapInfoCheck.put("pigNo", animalEarsTagNo.getText().toString().trim());

                    new PayDataProcessor(PayActivity.this).transerPayData(
                            checkedBaodanNo.getText().toString().trim(),
                            payReasonSpinner.getSelectedItem().toString().trim(),
                            quSpinner.getSelectedItem().toString(),
                            sheSpinner.getSelectedItem().toString(),
                            lanSpinner.getSelectedItem().toString(),
                            animalEarsTagNo.getText().toString().trim(),
                            etLipeiIdcard.getText().toString().trim());
                    strfleg = "collect";
                    PreferencesUtils.saveKeyValue("reason", payReasonSpinner.getSelectedItem().toString().trim(), PayActivity.this);
                    PreferencesUtils.saveKeyValue("cardnum", etLipeiIdcard.getText().toString().trim(), PayActivity.this);
                    PreferencesUtils.saveKeyValue("baodannum", checkedBaodanNo.getText().toString().trim(), PayActivity.this);
                    payAnimalApplyTask = new PayAnimalApplyTask(HttpUtils.ANIMAL_PAY_INFOCHECK, treeMapInfoCheck);
                    payAnimalApplyTask.execute((Void) null);
                } else {
                    AlertDialogManager.showMessageDialog(PayActivity.this, "提示", getString(R.string.locationwarning), new AlertDialogManager.DialogInterface() {
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
                break;
        }
    }


    class PayAnimalApplyTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUrl;
        private final Map<String, String> mQueryMap;

        PayAnimalApplyTask(String url, Map<String, String> map) {
            mUrl = url;
            mQueryMap = map;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                FormBody.Builder builder = new FormBody.Builder();
                for (TreeMap.Entry<String, String> entry : mQueryMap.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                Log.e(TAG, "理赔信息校验请求报文:" + mQueryMap.toString());
                RequestBody formBody = builder.build();
                String infoCheckResponse = HttpUtils.post(mUrl, formBody);
                if (null != infoCheckResponse) {
                    bean = gson.fromJson(infoCheckResponse, ResultBean.class);
                    Log.d(TAG, mUrl + "\ninfoCheckResponse:" + infoCheckResponse);
                    if (bean.getStatus() == 1) {
                        Log.d(TAG, "bean.getStatus():" + bean.getStatus());
                        if (null != bean.getData() && !bean.getData().equals("")) {
                            Map<String, String> data = (Map<String, String>) bean.getData();
                            lipname = data.get("name");
                        }
                        PreferencesUtils.saveKeyValue("insurename", lipname, PayActivity.this);
                        return true;
                    } else if (bean.getStatus() == 0) {
                        Log.d(TAG, "bean.getStatus():" + bean.getStatus());
                        payInfoCheckHandler.sendEmptyMessage(12);
                        return false;
                    } else {
                        Log.d(TAG, "bean.getStatus():" + bean.getStatus());
                        payInfoCheckHandler.sendEmptyMessage(13);
                        return false;
                    }
                } else {
                    payInfoCheckHandler.sendEmptyMessage(14);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errString = "服务器错误！";
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            payAnimalApplyTask = null;
            if (success & HttpUtils.ANIMAL_PAY_INFOCHECK.equalsIgnoreCase(mUrl)) {
                if (strfleg.equals("save")) {
                    String baodanno = checkedBaodanNo.getText().toString().trim();
                    String cardnum = etLipeiIdcard.getText().toString().trim();
                    String payreason = payReasonSpinner.getSelectedItem().toString();
                    String qu = quSpinner.getSelectedItem().toString();
                    String she = sheSpinner.getSelectedItem().toString();
                    String lan = lanSpinner.getSelectedItem().toString();
                    String Ears = animalEarsTagNo.getText().toString();

                    SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.getDefault());
                    String lipeidate = mDateFormat.format(new Date(System.currentTimeMillis()));
                    Log.i("===lipeidate===", lipeidate);
                    String s = "投保人：" + lipname + " ,请确认";
                    LiPeiLocalBean liPeiLocalBean = new LiPeiLocalBean(baodanno, lipname+"", cardnum, payreason,
                            qu + she + lan, lipeidate,
                            LocationManager.getInstance(PayActivity.this).currentLon + "",
                            LocationManager.getInstance(PayActivity.this).currentLat + "",
                            String.valueOf(ANIMAL_TYPE), Ears, "", "1", s,"");
                    databaseHelper.addLiPeiLocalData(liPeiLocalBean);
                    Log.d(TAG, "理赔信息校验接口，校验通过");
                    PreferencesUtils.saveBooleanValue("isli", true, PayActivity.this);
                    PreferencesUtils.saveKeyValue("lipeidate", lipeidate, PayActivity.this);

                    Intent infoCheckIntent = new Intent(PayActivity.this, HomeActivity.class);
                    startActivity(infoCheckIntent);
                    finish();
                } else if (strfleg.equals("collect")) {
                    PreferencesUtils.saveBooleanValue("isli", false, PayActivity.this);
                    Intent infoCheckIntent = new Intent(PayActivity.this, DetectorActivity.class);
                    startActivity(infoCheckIntent);
                    collectNumberHandler.sendEmptyMessage(2);
                    finish();
                }
            } else if (!success) {
                // 失败
                errString = "理赔单提交失败！";
                Log.d(TAG, errString);
                payImageAcquisition.setEnabled(true);
            }
        }

        @Override
        protected void onCancelled() {
            payAnimalApplyTask = null;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler payInfoCheckHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (msg.what) {
                case 12:
                case 13:
                    AlertDialog.Builder builder13 = new AlertDialog.Builder(PayActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(bean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder13.create();
                    builder13.setCancelable(false);
                    builder13.show();
                    break;
                case 14:
                    AlertDialog.Builder builder14 = new AlertDialog.Builder(PayActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("服务异常，请稍后再试！")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder14.create();
                    builder14.setCancelable(false);
                    builder14.show();
                    break;
                case 15:
                    AlertDialog.Builder builder15 = new AlertDialog.Builder(PayActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("无法连接服务器，请稍后再试！")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder15.create();
                    builder15.setCancelable(false);
                    builder15.show();
                    break;
                default:
                    break;
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
