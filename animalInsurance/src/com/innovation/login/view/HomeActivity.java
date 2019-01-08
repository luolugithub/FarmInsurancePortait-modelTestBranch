package com.innovation.login.view;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.base.InnApplication;
import com.innovation.bean.QueryVideoFlagDataBean;
import com.innovation.bean.ResultBean;
import com.innovation.bean.UpdateBean;
import com.innovation.login.FixedSpeedScroller;
import com.innovation.login.MyFragmentPagerAdapter;
import com.innovation.update.UpdateReceiver;
import com.innovation.utils.ConstUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.PreferencesUtils;
import com.innovation.view.CustomViewPager;

import org.tensorflow.demo.env.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;


//add by xuly 2018-06-12
public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static String TAG = "HomeActivity";
    //    private SearchView searchView;
    private CustomViewPager myviewpager;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragments;
    //选项卡中的按钮
    private RadioButton btn_first;
    private RadioButton btn_second;

//    private TextView tv_title;
//    private ImageView iv_cancel;

    //作为指示标签的按钮
    private ImageView cursor;
    //    标志指示标签的横坐标
    float cursorX = 0;
    //所有按钮的宽度的集合
    private int[] widthArgs;
    //所有按钮的集合
    private Button[] btnArgs;

    private UpdateReceiver mUpdateReceiver;
    private IntentFilter mIntentFilter;
    private GETUPDATETASK mUpdateTask;
    private UpdateBean insurresp_company;
    private String errStr_company;
    private Button select1Button;
    private Button select2Button;
    private Button select3Button;
    private TextView titleTextView;
    private AlertDialog.Builder builder;
    private RadioGroup animalTypeRadioGroup;
    private View v;
    private LayoutInflater inflater;
    private Dialog dialog;
    private Button okButton;


    private TextView tv_title;
    private ImageView iv_cancel;
    private static final Logger logger = new Logger();
    private Gson gson;
    private ResultBean queryVideoFlagResultBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskselect);
        gson = new Gson();
        queryVideoFlag();
        if (ANIMAL_TYPE == ConstUtils.ANIMAL_TYPE_NONE) {
            builder = new AlertDialog.Builder(HomeActivity.this);
            inflater = LayoutInflater.from(HomeActivity.this);
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
        } else {
            initView();
        }
        registerBroadcast();
//        initView();

        mUpdateTask = new GETUPDATETASK(HttpUtils.GET_UPDATE_URL, null);
        mUpdateTask.execute((Void) null);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    private void queryVideoFlag() {
        try {
            TreeMap<String, String> treeMapQueryVideoFlag = new TreeMap();
            treeMapQueryVideoFlag.put("", "");
            FormBody.Builder builder = new FormBody.Builder();
            for (TreeMap.Entry<String, String> entry : treeMapQueryVideoFlag.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            RequestBody formBody = builder.build();
            String queryVideoFlagResponse = HttpUtils.post(HttpUtils.QUERY_VIDEOFLAG, formBody);
            if (queryVideoFlagResponse != null) {
                logger.e(HttpUtils.QUERY_VIDEOFLAG + "\nqueryVideoFlagResponse:\n" + queryVideoFlagResponse);
                queryVideoFlagResultBean = gson.fromJson(queryVideoFlagResponse, ResultBean.class);
                if (queryVideoFlagResultBean.getStatus() == 1) {
                    QueryVideoFlagDataBean queryVideoFlagData = gson.fromJson(queryVideoFlagResponse, QueryVideoFlagDataBean.class);
                    PreferencesUtils.saveKeyValue(InnApplication.touBaoVieoFlag, queryVideoFlagData.getData().getToubaoVideoFlag(), HomeActivity.this);
                    PreferencesUtils.saveKeyValue(InnApplication.liPeiVieoFlag, queryVideoFlagData.getData().getLipeiVideoFlag(), HomeActivity.this);
                    /*InnApplication.touBaoVieoFlag = queryVideoFlagData.getData().getToubaoVideoFlag();
                    InnApplication.liPeiVieoFlag = queryVideoFlagData.getData().getLipeiVideoFlag();*/
                    Log.i("==touBaoVieoFlaghome===", queryVideoFlagData.getData().getToubaoVideoFlag());
                    Log.i("==liPeiVieoFlaghome===", queryVideoFlagData.getData().getLipeiVideoFlag());
                  /*  logger.e("\ntouBaoVieoFlag:\n" + queryVideoFlagData.getData().getToubaoVideoFlag());
                    logger.e("\nliPeiVieoFlag:\n" + queryVideoFlagData.getData().getLipeiVideoFlag());*/
                    if (null != queryVideoFlagData.getData() && !"".equals(queryVideoFlagData.getData())) {
                        String left = (queryVideoFlagData.getData().getLeftNum() == null) ? "8" : queryVideoFlagData.getData().getLeftNum();
                        String middleNum = (queryVideoFlagData.getData().getLeftNum() == null) ? "8" : queryVideoFlagData.getData().getMiddleNum();
                        String rightNum = (queryVideoFlagData.getData().getLeftNum() == null) ? "8" : queryVideoFlagData.getData().getRightNum();
                        logger.e("\nleft:\n" + left);
                        logger.e("\nmiddleNum:\n" + middleNum);
                        logger.e("\nrightNum:\n" + rightNum);
                        PreferencesUtils.saveKeyValue(PreferencesUtils.FACE_ANGLE_MAX_LEFT, left, HomeActivity.this);
                        PreferencesUtils.saveKeyValue(PreferencesUtils.FACE_ANGLE_MAX_MIDDLE, middleNum, HomeActivity.this);
                        PreferencesUtils.saveKeyValue(PreferencesUtils.FACE_ANGLE_MAX_RIGHT, rightNum, HomeActivity.this);

                    }
                } else if (queryVideoFlagResultBean.getStatus() == 0) {
                    homeActivityHandler.sendEmptyMessage(14);
                } else {
                    homeActivityHandler.sendEmptyMessage(15);
                }

            } else {
                // homeActivityHandler.sendEmptyMessage(16);
            }
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "查看是否录制视频接口异常！", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener okButtonClickListener = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View v) {
            if (ANIMAL_TYPE == 0) {
                Toast.makeText(HomeActivity.this, "必须选择其中一个农险！！", Toast.LENGTH_SHORT).show();
                return;
            }
            initView();
            dialog.dismiss();
        }
    };

    public void initView() {
        myviewpager = (CustomViewPager) this.findViewById(R.id.myviewpager);
        //禁止滑动
        myviewpager.setScanScroll(true);
        btn_first = (RadioButton) this.findViewById(R.id.btn_first);
        btn_second = (RadioButton) this.findViewById(R.id.btn_second);

        btnArgs = new Button[]{btn_first, btn_second};

        cursor = (ImageView) this.findViewById(R.id.cursor_btn);

        cursor.setBackgroundColor(Color.YELLOW);

        myviewpager.setOnPageChangeListener(this);

        btn_first.setOnCheckedChangeListener(new InnerOnCheckedChangeListener());
        btn_second.setOnCheckedChangeListener(new InnerOnCheckedChangeListener());

        Field mScroller = null;
        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(myviewpager.getContext());
            mScroller.set(myviewpager, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }

        fragments = new ArrayList<Fragment>();
        fragments.add(new ToubaoFragment());
        fragments.add(new LipeiFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        myviewpager.setAdapter(adapter);

        //重置所有按钮颜色
        resetButtonColor();
        //把第一个按钮的颜色设置为红色
        btn_first.setTextColor(Color.YELLOW);
        btn_first.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cursor.getLayoutParams();
                //减去边距*2，以对齐标题栏文字
                lp.width = btn_first.getWidth() - btn_first.getPaddingLeft() * 2;
                cursor.setLayoutParams(lp);
                cursor.setX(btn_first.getPaddingLeft());
            }
        });


    }

    //把事件的内部类定义出来
    private class InnerOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        //单选按钮选中事件方法
        //buttonView表示谁的状态被改变
        //isChecked上面的参数代表的状态是否选中
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.btn_first:
                    //单选按钮通过参数isChecked去得到当前到底是选中还是未选中
                    if (isChecked) {
                        myviewpager.setCurrentItem(0);
                        cursorAnim(0);
                    }

                    break;
                case R.id.btn_second:
                    //单选按钮通过参数isChecked去得到当前到底是选中还是未选中
                    if (isChecked) {
                        myviewpager.setCurrentItem(1);
                        cursorAnim(1);
                    }

                    break;

                default:
                    break;
            }

        }


    }

    //重置所有按钮的颜色
    public void resetButtonColor() {
        btn_first.setBackgroundColor(Color.parseColor("#ff0099cc"));
        btn_second.setBackgroundColor(Color.parseColor("#ff0099cc"));

        btn_first.setTextColor(Color.WHITE);
        btn_second.setTextColor(Color.WHITE);

    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        //  Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        //  Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        //  Auto-generated method stub
        if (widthArgs == null) {
            widthArgs = new int[]{btn_first.getWidth(),
                    btn_second.getWidth()};
        }
        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();

        //将滑动到的当前按钮颜色设置为红色
        btnArgs[arg0].setTextColor(Color.YELLOW);
        cursorAnim(arg0);

        //把当前页面的单选按钮设置为选中状态
        ((CompoundButton) btnArgs[arg0]).setChecked(true);


    }

    //指示器的跳转，传入当前所处的页面的下标
    public void cursorAnim(int curItem) {
        //每次调用，就将指示器的横坐标设置0，即开始的位置
        cursorX = 0;
        //再根据当前的curItem来设置指示器的宽度
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cursor.getLayoutParams();
        //首先获得当前按钮的宽度，再减去按钮左右边距距，以对齐标题栏文本
        lp.width = widthArgs[curItem] - btnArgs[0].getPaddingLeft() * 2;
        //通过指示标签对象，将标签设置到父容器中
        cursor.setLayoutParams(lp);
        //循环获取当前页之前的所有页面的宽度
        for (int i = 0; i < curItem; i++) {
            cursorX = cursorX + btnArgs[i].getWidth();
        }
        //再加上当前页面的左边距，即为指示器当前应处的位置
        cursor.setX(cursorX + btnArgs[curItem].getPaddingLeft());
    }

    private void registerBroadcast() {
        mUpdateReceiver = new UpdateReceiver(false);
        mIntentFilter = new IntentFilter(UpdateReceiver.UPDATE_ACTION);
        this.registerReceiver(mUpdateReceiver, mIntentFilter);
    }

    private void unRegisterBroadcast() {
        try {
            this.unregisterReceiver(mUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    public class GETUPDATETASK extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;

        GETUPDATETASK(String url, TreeMap map) {
            mUrl = url;
            mQueryMap = map;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                FormBody.Builder builder = new FormBody.Builder();
                RequestBody formBody = builder.build();

                String response = HttpUtils.post(mUrl, formBody);
                if (response == null) {
                    return false;
                }
                Log.d(TAG, mUrl + "\nresponse:\n" + response);

                if (HttpUtils.GET_UPDATE_URL.equalsIgnoreCase(mUrl)) {
                    insurresp_company = (UpdateBean) HttpUtils.processResp_update(response);
                    if (insurresp_company == null) {
                        errStr_company = "请求错误！";
                        return false;
                    }
                    if (insurresp_company.status != HttpRespObject.STATUS_OK) {
                        errStr_company = insurresp_company.msg;
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                errStr_company = "服务器错误！";
                return false;
            }
            //  register the new account here.

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUpdateTask = null;

            if (success & HttpUtils.GET_UPDATE_URL.equalsIgnoreCase(mUrl)) {
                Intent intent = new Intent();
                intent.setAction(UpdateReceiver.UPDATE_ACTION);
                intent.putExtra("result_json", String.valueOf(insurresp_company.data));

                //发送广播
                sendBroadcast(intent);


            } else if (!success) {
                Toast.makeText(HomeActivity.this, "网络接口请求异常！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateTask = null;
        }
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    @SuppressLint("HandlerLeak")
    private final Handler homeActivityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 14:
                    AlertDialog.Builder builder14 = new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(queryVideoFlagResultBean.getMsg())
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    HomeActivity.this.finish();
                                }
                            });
                    builder14.setCancelable(false);
                    builder14.show();
                    break;

                case 15:
                    AlertDialog.Builder builder15 = new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(queryVideoFlagResultBean.getMsg())
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    HomeActivity.this.finish();
                                }
                            });
                    builder15.setCancelable(false);
                    builder15.show();
                    break;
                case 16:
                    AlertDialog.Builder builder16 = new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("网络异常！")
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    HomeActivity.this.finish();
                                }
                            });
                    builder16.setCancelable(false);
                    builder16.show();
                    break;

                case 400:
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("获取用户ID失败！")
                            .setPositiveButton("确认", (dialog, which) -> HomeActivity.this.finish());
                    builder.setCancelable(false);
                    builder.show();
                    break;
                case 94:
                    break;
                default:
                    break;
            }

        }
    };

}


