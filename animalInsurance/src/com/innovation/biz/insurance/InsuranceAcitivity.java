package com.innovation.biz.insurance;

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

import com.amap.api.location.AMapLocationClient;
import com.innovation.animalInsurance.R;
import com.innovation.base.BaseActivity;
import com.innovation.base.GlobalConfigure;
import com.innovation.base.InnApplication;
import com.innovation.base.Model;
import com.innovation.location.LocationManager;
import com.innovation.utils.ConstUtils;
import com.innovation.utils.EditTextJudgeNumberWatcher;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.OkHttp3Util;
import com.innovation.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;


/**
 * Created by Luolu on 2018/9/19.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class InsuranceAcitivity extends BaseActivity {
    public static String TAG = "InsuranceAcitivity";
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.baodanName)
    EditText baodanName;
    @BindView(R.id.radioButtonEnterprise)
    RadioButton radioButtonEnterprise;
    @BindView(R.id.radioButtonOrganization)
    RadioButton radioButtonOrganization;
    @BindView(R.id.radioGroupBaodanType)
    RadioGroup radioGroupBaodanType;
    @BindView(R.id.spinnerInsuranceType)
    Spinner spinnerInsuranceType;
    @BindView(R.id.insuranceRate)
    EditText insuranceRate;
    @BindView(R.id.scaleFarming)
    RadioButton scaleFarming;
    @BindView(R.id.freeRangeFarming)
    RadioButton freeRangeFarming;
    @BindView(R.id.farmFormRadioGroup)
    RadioGroup farmFormRadioGroup;
    @BindView(R.id.unitInsuranceCost)
    EditText unitInsuranceCost;
    @BindView(R.id.baodanApplyAddress)
    EditText baodanApplyAddress;
    @BindView(R.id.baodanApplyName)
    EditText baodanApplyName;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.btnFinish)
    Button btnFinish;
    @BindView(R.id.lin_yan)
    LinearLayout lin_yan;
    private int baodanType;
    private AMapLocationClient mLocationClient;
    private int farmForm = -1;
    private String mTempToubaoNumber;
    private String strtype;
    private int insuredType;
    private String sbaodanName;
    private String sinsuranceRate;
    private String sunitInsuranceCost;
    private String sbaodanApplyAddress;
    private String sbaodanApplyName;
    private boolean addressFirst = true;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_insurance;
    }

    @Override
    protected void initData() {
        GlobalConfigure.model = Model.BUILD.value();
        tvTitle.setText("新建临时保单");
        ivCancel.setVisibility(View.VISIBLE);
        GlobalConfigure.model = Model.BUILD.value();
        radioGroupBaodanType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonEnterprise:
                    baodanType = 1;
                    btnNext.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.GONE);
                    //lin_yan.setVisibility(View.VISIBLE);
                    break;
                case R.id.radioButtonOrganization:
                    baodanType = 2;
                    btnFinish.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    // lin_yan.setVisibility(View.GONE);
                    break;
            }
        });

        farmFormRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.scaleFarming:
                    farmForm = 1;
                    strtype = "规模化养殖";
                    break;
                case R.id.freeRangeFarming:
                    farmForm = 2;
                    strtype = "散养";
                    break;
            }
        });
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ConstUtils.geInsureTypeCaptions(ANIMAL_TYPE));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsuranceType.setAdapter(arrayAdapter);
        //getCurrentLocationLatLng();
        LocationManager instance = LocationManager.getInstance(InsuranceAcitivity.this);
        instance.startLocation();
        instance.setAddress(new LocationManager.GetAddress() {
            @Override
            public void getaddress(String address) {
                if (addressFirst) {
                    baodanApplyAddress.setText(address);
                    addressFirst = false;
                }

            }
        });
        String str_random1 = createCode();
        mTempToubaoNumber = stampToDate(System.currentTimeMillis()) + str_random1;
        insuranceRate.addTextChangedListener(new EditTextJudgeNumberWatcher(insuranceRate));
    }

    public String str = "";

    @OnClick({R.id.btnNext, R.id.btnFinish, R.id.iv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                str = "qiye";
                //新建企业验标单
                saveMeaasge();
                if ("".equals(sbaodanName)) {
                    Toast.makeText(InsuranceAcitivity.this, "临时保单为空", Toast.LENGTH_LONG).show();
                } else if ("".equals(sinsuranceRate)) {
                    Toast.makeText(InsuranceAcitivity.this, "保险费率为空", Toast.LENGTH_LONG).show();
                }else if (Integer.parseInt(insuranceRate.getText().toString().trim()) > 10){
                    Toast.makeText(InsuranceAcitivity.this, "非法的费率格式", Toast.LENGTH_LONG).show();
                } else if (farmForm == -1) {
                    Toast.makeText(getApplicationContext(), "请选择饲养方式", Toast.LENGTH_SHORT).show();
                } else if ("".equals(sunitInsuranceCost)) {
                    Toast.makeText(InsuranceAcitivity.this, "保险金额为空", Toast.LENGTH_LONG).show();
                } else if ("".equals(sbaodanApplyAddress)) {
                    Toast.makeText(InsuranceAcitivity.this, "地址为空", Toast.LENGTH_LONG).show();
                } else {
                    chackName();

                }
                break;
            case R.id.btnFinish:
                str = "zuzhi";
                //组织列表界面跳转
                saveMeaasge();
                if ("".equals(sbaodanName)) {
                    Toast.makeText(InsuranceAcitivity.this, "临时保单为空", Toast.LENGTH_LONG).show();
                } else if ("".equals(sinsuranceRate)) {
                    Toast.makeText(InsuranceAcitivity.this, "保险费率为空", Toast.LENGTH_LONG).show();
                } else if (Integer.parseInt(insuranceRate.getText().toString().trim()) > 10){
                    Toast.makeText(InsuranceAcitivity.this, "非法的费率格式", Toast.LENGTH_LONG).show();
                } else if (farmForm == -1) {
                    Toast.makeText(getApplicationContext(), "请选择饲养方式", Toast.LENGTH_SHORT).show();
                } else if ("".equals(sunitInsuranceCost)) {
                    Toast.makeText(InsuranceAcitivity.this, "保险金额为空", Toast.LENGTH_LONG).show();
                } else if ("".equals(sbaodanApplyAddress)) {
                    Toast.makeText(InsuranceAcitivity.this, "地址为空", Toast.LENGTH_LONG).show();
                } else {
                    chackName();
                }
                break;
            case R.id.iv_cancel:
                finish();
                break;
        }
    }

    private void chackName() {
        Map map = new HashMap();
        map.put(HttpUtils.AppKeyAuthorization, "hopen");
        Map mapbody = new HashMap();
        mapbody.put(HttpUtils.deptId, PreferencesUtils.getIntValue(HttpUtils.deptId, InnApplication.getAppContext()) + "");
        mapbody.put("baodanName", PreferencesUtils.getStringValue(HttpUtils.baodanName, InnApplication.getAppContext()));
        OkHttp3Util.doPost(HttpUtils.BaoDannametest, mapbody, map, new Callback() {
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
                                Toast.makeText(InsuranceAcitivity.this, msg, Toast.LENGTH_LONG).show();
                            } else if (status == 1) {
                                // Toast.makeText(InsuranceAcitivity.this, msg, Toast.LENGTH_LONG).show();
                                if ("qiye".equals(str)) {
                                    goToActivity(EnterpriseBaodanActivity.class, null);
                                    finish();
                                } else if ("zuzhi".equals(str)) {
                                    createDan();
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
        mapbody.put("address", baodanApplyAddress.getText().toString().trim());
        mapbody.put("uid", PreferencesUtils.getStringValue(HttpUtils.id, InnApplication.getAppContext()));
        mapbody.put(HttpUtils.deptId, PreferencesUtils.getIntValue(HttpUtils.deptId, InnApplication.getAppContext()) + "");

        mapbody.put("longitude", String.valueOf(LocationManager.getInstance(InsuranceAcitivity.this).currentLat));
        mapbody.put("latitude", String.valueOf(LocationManager.getInstance(InsuranceAcitivity.this).currentLon));

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
                                Toast.makeText(InsuranceAcitivity.this, msg, Toast.LENGTH_LONG).show();
                            } else if (status == 1) {
                                Toast.makeText(InsuranceAcitivity.this, msg, Toast.LENGTH_LONG).show();
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

    private void saveMeaasge() {
        sbaodanName = baodanName.getText().toString();
        PreferencesUtils.saveKeyValue(HttpUtils.baodanName, sbaodanName, InnApplication.getAppContext());
        PreferencesUtils.saveKeyValue(HttpUtils.baodanType, baodanType + "", InnApplication.getAppContext());
        String sXZ = spinnerInsuranceType.getSelectedItem().toString();
        insuredType = ConstUtils.getInsureTypeCodeIntByCaption(sXZ);
        PreferencesUtils.saveKeyValue(HttpUtils.InsuranceType, insuredType + "", InnApplication.getAppContext());
        sinsuranceRate = insuranceRate.getText().toString();
        PreferencesUtils.saveKeyValue(HttpUtils.insuranceRate, sinsuranceRate, InnApplication.getAppContext());
        PreferencesUtils.saveKeyValue(HttpUtils.farmForm, strtype + "", InnApplication.getAppContext());
        sunitInsuranceCost = unitInsuranceCost.getText().toString();
        PreferencesUtils.saveKeyValue(HttpUtils.InsuranceCost, sunitInsuranceCost, InnApplication.getAppContext());
        sbaodanApplyAddress = baodanApplyAddress.getText().toString();
        PreferencesUtils.saveKeyValue(HttpUtils.baodanApplyAddress, sbaodanApplyAddress, InnApplication.getAppContext());
        sbaodanApplyName = baodanApplyName.getText().toString();
        PreferencesUtils.saveKeyValue(HttpUtils.baodanApplyName, sbaodanApplyName, InnApplication.getAppContext());

    }

}
