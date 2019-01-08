package com.innovation.biz.insurance;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.base.BaseActivity;
import com.innovation.base.GlobalConfigure;
import com.innovation.bean.BaoDanBeanNew;
import com.innovation.base.InnApplication;
import com.innovation.biz.insurance.adapter.YanBiaoAdapter;
import com.innovation.base.Model;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.OkHttp3Util;
import com.innovation.utils.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class YanBiaoDanActivity extends BaseActivity {

    public static String TAG = "YanBiaoDanActivity";
    @BindView(R.id.yanbiaodan_listview)
    ListView yanbiaodan_listview;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.btn_yanbiao_add)
    Button btn_yanbiao_add;
    private int deptId;
    List<BaoDanBeanNew> baoDanBeanNewList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_yan_biao_dan;
    }

    @Override
    protected void initData() {
        tv_title.setText("保单列表");
        deptId = PreferencesUtils.getIntValue(HttpUtils.deptId, InnApplication.getAppContext());
        GlobalConfigure.model = Model.BUILD.value();
        getDataFromNet();
        ivCancel.setVisibility(View.VISIBLE);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDataFromNet() {
        Map map = new HashMap();
        map.put(HttpUtils.AppKeyAuthorization, "hopen");
        Map mapbody = new HashMap();
        mapbody.put(HttpUtils.deptId, deptId + "");
        Log.i("deptId", deptId + "");
        OkHttp3Util.doPost(HttpUtils.BaoDanList, mapbody, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.i(TAG, string);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
//                                showDialogError(msg);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    baoDanBeanNewList.clear();
                                    JSONArray datas = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < datas.length(); i++) {
                                        JSONObject jsonObject1 = datas.getJSONObject(i);
                                        String bankName = jsonObject1.getString("baodanName");
                                        String createtime = jsonObject1.getString("createtime");
                                        int baodanType = jsonObject1.getInt("baodanType");
                                        int id = jsonObject1.getInt("id");
                                        BaoDanBeanNew baoDanBeanNew = new BaoDanBeanNew(bankName, baodanType, id, createtime);
                                        baoDanBeanNewList.add(baoDanBeanNew);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                yanbiaodan_listview.setAdapter(new YanBiaoAdapter(YanBiaoDanActivity.this, baoDanBeanNewList));
                                yanbiaodan_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        PreferencesUtils.saveKeyValue(HttpUtils.baodanType, baoDanBeanNewList.get(position).baodanType + "", InnApplication.getAppContext());
                                        PreferencesUtils.saveKeyValue(HttpUtils.id, baoDanBeanNewList.get(position).id + "", InnApplication.getAppContext());
                                        // activity.goToActivity(HomeActivity.class, null);
                                        goToActivity(CreateYanActivity.class, null);
                                    }
                                });
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @OnClick({R.id.iv_cancel, R.id.btn_yanbiao_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.btn_yanbiao_add:
                goToActivity(InsuranceAcitivity.class, null);
                break;
            default:
                break;
        }

    }
}
