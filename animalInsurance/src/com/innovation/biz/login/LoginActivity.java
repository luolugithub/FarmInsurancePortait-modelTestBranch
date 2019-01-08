package com.innovation.biz.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.base.InnApplication;
import com.innovation.bean.MultiBaodanBean;
import com.innovation.bean.ResultBean;
import com.innovation.login.InputValidation;
import com.innovation.login.RegisterActivity;
import com.innovation.login.RespObject;
import com.innovation.login.ResponseProcessor;
import com.innovation.login.TokenResp;
import com.innovation.login.Utils;
import com.innovation.login.view.HomeActivity;
import com.innovation.utils.ConstUtils;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.PreferencesUtils;

import org.tensorflow.demo.env.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    private static final String TAG = "LoginActivity";
    private final AppCompatActivity activity = LoginActivity.this;
    private final Logger mLogger = new Logger(LoginActivity.class.getSimpleName());

    @BindView(R.id.textInputEditPhoneNumber)
    TextInputEditText textInputEditPhoneNumber;
    @BindView(R.id.textInputLayoutPhoneNumber)
    TextInputLayout textInputLayoutPhoneNumber;
    @BindView(R.id.textInputEditPassword)
    TextInputEditText textInputEditPassword;
    @BindView(R.id.textInputLayoutPassword)
    TextInputLayout textInputLayoutPassword;
    @BindView(R.id.version_name)
    TextView versionName;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    private InputValidation inputValidation;


    private String errString = "";
    private UserLoginTask mAuthTask;

    private LoginPresenter loginPresenter;
    private MultiBaodanBean loginResult;

    private AlertDialog.Builder builder;
    private RadioGroup animalTypeRadioGroup;
    private View v;
    private LayoutInflater inflater;
    private Dialog dialog;
    private Button okButton;
    private Gson gson;
    private ResultBean resultBean;
    private String responseUserLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

//        mProgressHandler.sendEmptyMessage(55);

        SharedPreferences pref = this.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(pref.getString("token", ""))) {
            Intent add_intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(add_intent);
            finish();
        }
        inputValidation = new InputValidation(this.getBaseContext());
        loginPresenter = new LoginPresenter(this);
        versionName.setText(getString(R.string.version_name) + getVersionName());


    }

    private View.OnClickListener okButtonClickListener = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View v) {
            if (ANIMAL_TYPE == 0) {
                Toast.makeText(LoginActivity.this, "必须选择其中一个农险！！", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
        }
    };


    /**
     * get App versionName
     *
     * @return versionName
     */
    private String getVersionName() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    @Override
    public void onAuthenticate() {

    }

    @Override
    public int onLoginError(String message) {
        return 0;
    }

    @Override
    public void onLoginSuccess(String message) {

    }

    @Override
    public void onLogin() {

        if (!inputValidation.isMobile(textInputEditPhoneNumber, textInputLayoutPhoneNumber, getString(R.string.error_message_phone_number))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        mLogger.i("databaseHelper.checkUser: " + textInputEditPhoneNumber.getText().toString().trim());
        mLogger.i("databaseHelper.checkUser Password: " + textInputEditPassword.getText().toString().trim());
        Map<String, String> query = new HashMap<>();
        query.put("mobilephone", textInputEditPhoneNumber.getText().toString().trim());
        query.put("password", textInputEditPassword.getText().toString().trim());
        query.put("imageCode", "1234");
        mAuthTask = new UserLoginTask(HttpUtils.PIC_LOGIN_URL, query);
        mAuthTask.execute((Void) null);
    }

    @OnClick({R.id.appCompatButtonLogin, R.id.textViewLinkRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.appCompatButtonLogin:
                onLogin();
                break;
            case R.id.textViewLinkRegister:
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUrl;
        private final Map<String, String> mQueryMap;

        UserLoginTask(String url, Map<String, String> map) {
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
                RequestBody formBody = builder.build();
                 responseUserLoginTask = HttpUtils.post(mUrl, formBody);
                 if (responseUserLoginTask != null){
                     return true;
                 }else {
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
            mAuthTask = null;
            gson = new Gson();
            resultBean = gson.fromJson(responseUserLoginTask, ResultBean.class);
            if (resultBean != null) {
                Log.d(TAG, mUrl + "\nresponseUserLoginTask:" + responseUserLoginTask);
                if (resultBean.getStatus() == 1) {
                    if (HttpUtils.PIC_LOGIN_URL.equalsIgnoreCase(mUrl)) {
                        TokenResp tokenresp = (TokenResp) ResponseProcessor.processResp(responseUserLoginTask, mUrl);
                        if (tokenresp == null || TextUtils.isEmpty(tokenresp.token) || tokenresp.user_status != RespObject.USER_STATUS_1) {
                            return ;

                        }
                        //  存储用户信息
                        SharedPreferences userinfo = getApplicationContext().getSharedPreferences(
                                Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userinfo.edit();
                        editor.putString("token", tokenresp.token);
                        //  int 类型的可能需要修改
                        //  验证码的有效期，应该在获取验证码的时候返回才对
                        editor.putInt("tokendate", tokenresp.tokendate);
                        editor.putInt("uid", tokenresp.uid);
                        editor.putString("username", tokenresp.user_username);
                        editor.putString("fullname", tokenresp.user_fullname);
                        editor.putString("codedate", tokenresp.codedate);
                        //用户创建时间
                        editor.putString("createtime", tokenresp.createtime);
                        //  editor.putInt("deptid", tokenresp.deptid);
                        editor.apply();
                        int i = tokenresp.deptid;
                        PreferencesUtils.saveIntValue(HttpUtils.deptId, tokenresp.deptid, InnApplication.getAppContext());
                        PreferencesUtils.saveKeyValue(HttpUtils.id, tokenresp.uid + "", InnApplication.getAppContext());

                    }
                    Intent add_intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(add_intent);
                } else if (resultBean.getStatus() == 0) {
                    Snackbar.make(nestedScrollView, resultBean.getMsg(), Snackbar.LENGTH_SHORT).setText(resultBean.getMsg()).show();
//                        mProgressHandler.sendEmptyMessage(24);
                    return ;
                } else {
//                        mProgressHandler.sendEmptyMessage(44);
                    Snackbar.make(nestedScrollView, resultBean.getMsg(), Snackbar.LENGTH_SHORT).setText(resultBean.getMsg()).show();
                    return ;
                }

            } else {
//                Snackbar.make(nestedScrollView, "服务器错误，请稍后再试！", Snackbar.LENGTH_SHORT).show();
                    mProgressHandler.sendEmptyMessage(41);
                Log.e(TAG, mUrl + "\n请求失败！");
            }


            if (success & HttpUtils.PIC_LOGIN_URL.equalsIgnoreCase(mUrl)) {
                Intent add_intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(add_intent);
            } else if (!success) {
                // 显示失败
                Snackbar.make(nestedScrollView, "网络异常！", Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }


    // TODO: 2018/9/3 By:LuoLu
    @SuppressLint("HandlerLeak")
    private Handler mProgressHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 41:
                    Toast.makeText(LoginActivity.this, "网络请求异常！", Toast.LENGTH_SHORT).show();
                    break;
                case 44:
                    Toast.makeText(LoginActivity.this, resultBean.getMsg(), Toast.LENGTH_SHORT).show();
                    break;
                case 24:
                    Toast.makeText(LoginActivity.this, resultBean.getMsg(), Toast.LENGTH_SHORT).show();
                    break;
                case 55:
                    // TODO: 2018/8/28 By:LuoLu
                    if (ANIMAL_TYPE == ConstUtils.ANIMAL_TYPE_NONE) {
                        builder = new AlertDialog.Builder(LoginActivity.this);
                        inflater = LayoutInflater.from(LoginActivity.this);
                        v = inflater.inflate(R.layout.animal_type_dialog_layout, null);
                        dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setContentView(v);
                        animalTypeRadioGroup = v.findViewById(R.id.animalTypeRadioGroup);
                        okButton = v.findViewById(R.id.okButton);
                        okButton.setOnClickListener(okButtonClickListener);
                        animalTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                            switch (checkedId) {
                                case R.id.cowRadioButton:
                                    ANIMAL_TYPE = ConstUtils.ANIMAL_TYPE_CATTLE;
                                    break;
                                case R.id.donkeyRadioButton:
                                    ANIMAL_TYPE = ConstUtils.ANIMAL_TYPE_DONKEY;
                                    break;
                                case R.id.pigRadioButton:
                                    ANIMAL_TYPE = ConstUtils.ANIMAL_TYPE_PIG;
                                    break;
                            }
                        });
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.setCancelable(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // AppManager.getAppManager().finishActivity(this);
    }
}

