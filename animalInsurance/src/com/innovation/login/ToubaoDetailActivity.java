package com.innovation.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.innovation.animalInsurance.R;
import com.innovation.bean.BaodanBean;
import com.innovation.base.Model;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tensorflow.demo.DetectorActivity;
import com.innovation.base.GlobalConfigure;

import java.io.IOException;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.getStringTouboaExtra;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;

public class ToubaoDetailActivity extends AppCompatActivity {

    private static String TAG = "ToubaoDetailActivity";
    private TextView tv_title;
    private ImageView iv_cancel;
    private String baodanNumber;
    private TextView baodan_detail_number;
    private ToubaoTask mToubaoTask;

    private String errStr = "";
    private BaodanBean insurresp;
    private TextView baodan_detail_date;
    private TextView baodan_detail_name;
    private TextView baodan_detail_address;
    private Button update_toubao;
    private TextView baodan_detail_idcard;
    private String baodanId;
    private TextView baodan_detail_rate;
    private TextView baodan_detail_idcard_type;
    private ImageView btn_idcard_detail_zheng;
    private ImageView btn_idcard_detail_fan;
    private TextView baodan_detail_openbank;
    private TextView baodan_detail_banknum;
    private ImageView btn_bankimg_detail;
    private TextView baodan_detail_phone;
    private Button continue_toubao;
    private TextView baodan_detail_realno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toubao_detail);
        GlobalConfigure.model = Model.BUILD.value();
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("验标单详情");

        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        iv_cancel.setVisibility(View.VISIBLE);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        baodan_detail_realno = (TextView) findViewById(R.id.baodan_detail_realno);

        baodanNumber = getIntent().getStringExtra("baodanNumber");
        baodanId = getIntent().getStringExtra("baodanId");

        baodan_detail_number = (TextView) findViewById(R.id.baodan_detail_number);
        baodan_detail_rate = (TextView) findViewById(R.id.baodan_detail_rate);
        baodan_detail_name = (TextView) findViewById(R.id.baodan_detail_name);
        baodan_detail_idcard_type = (TextView) findViewById(R.id.baodan_detail_idcard_type);

        baodan_detail_idcard = (TextView) findViewById(R.id.baodan_detail_idcard);

        btn_idcard_detail_zheng = (ImageView) findViewById(R.id.btn_idcard_detail_zheng);
        btn_idcard_detail_fan = (ImageView) findViewById(R.id.btn_idcard_detail_fan);

        baodan_detail_openbank = (TextView) findViewById(R.id.baodan_detail_openbank);
        baodan_detail_banknum = (TextView) findViewById(R.id.baodan_detail_banknum);

        btn_bankimg_detail = (ImageView) findViewById(R.id.btn_bankimg_detail);

        baodan_detail_phone = (TextView) findViewById(R.id.baodan_detail_phone);
        baodan_detail_date = (TextView) findViewById(R.id.baodan_detail_date);
        baodan_detail_address = (TextView) findViewById(R.id.baodan_detail_address);

        baodan_detail_number.setText(baodanNumber);

        update_toubao = (Button) findViewById(R.id.update_toubao);
        update_toubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("baodanNumber", baodanNumber);
                intent.putExtra("baodanId", baodanId);
                getStringTouboaExtra = baodanNumber;
                intent.setClass(getApplicationContext(), UpdateToubaoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continue_toubao = (Button) findViewById(R.id.continue_toubao);
        continue_toubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TreeMap query = new TreeMap<String, String>();
                query.put("baodanNo", baodanNumber.trim());
                getStringTouboaExtra = baodanNumber.trim();
                if (baodanNumber.trim() != null) {
                    mToubaoTask = new ToubaoTask(HttpUtils.INSUR_QUERY_URL, query, "continue");
                    mToubaoTask.execute((Void) null);
                }


            }
        });


        TreeMap query = new TreeMap<String, String>();
        query.put("baodanNo", baodanNumber);
        mToubaoTask = new ToubaoTask(HttpUtils.INSUR_QUERY_URL, query, "query");
        mToubaoTask.execute((Void) null);


    }


    public class ToubaoTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;
        private final String mtype;

        ToubaoTask(String url, TreeMap map, String type) {
            mUrl = url;
            mQueryMap = map;
            mtype = type;
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

                if (mtype.equals("continue")) {
                    baodan_detail_realno.setText(insurresp.ibaodanNoReal);
                    String str_realno = baodan_detail_realno.getText().toString().trim();

                    if (!str_realno.equals("")) {
                        Toast.makeText(ToubaoDetailActivity.this, "保单已审核，不能继续录入", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("ToubaoTempNumber", baodanNumber);
                        getStringTouboaExtra = baodanNumber;
                        intent.setClass(getApplicationContext(), DetectorActivity.class);
                        startActivity(intent);
                        collectNumberHandler.sendEmptyMessage(2);
                        finish();
                    }
                } else {
                    baodan_detail_realno.setText(insurresp.ibaodanNoReal);

                    baodan_detail_rate.setText(String.valueOf(insurresp.baodanRate) + "%");
                    baodan_detail_name.setText(String.valueOf(insurresp.iname));

                    baodan_detail_idcard_type.setText(String.valueOf(insurresp.icardType));
                    baodan_detail_idcard.setText(insurresp.icardNo);

                    baodan_detail_openbank.setText(insurresp.bankName);
                    baodan_detail_banknum.setText(insurresp.bankNo);

                    baodan_detail_date.setText(String.valueOf(insurresp.ibaodanTime));

                    baodan_detail_phone.setText(String.valueOf(insurresp.ibaodanPhone));
                    baodan_detail_address.setText(String.valueOf(insurresp.iaddress));

                    ImageLoader.getInstance().displayImage(insurresp.cardFrontShow, btn_idcard_detail_zheng);
                    ImageLoader.getInstance().displayImage(insurresp.cardBackShow, btn_idcard_detail_fan);
                    ImageLoader.getInstance().displayImage(insurresp.bankFrontShow, btn_bankimg_detail);
                }


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
}
