package com.innovation.login.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.bean.LiPeiLocalBean;
import com.innovation.bean.MultiBaodanBean;
import com.innovation.bean.QueryBaodanBean;
import com.innovation.biz.Insured.PayActivity;
import com.innovation.base.Model;
import com.innovation.biz.login.LoginActivity;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.LipeiAdapter;
import com.innovation.login.Utils;
import com.innovation.login.model.LipeiLocalAdapter;
import com.innovation.utils.ConstUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.PreferencesUtils;

import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;


/**
 * luolu
 */
public class LipeiFragment extends Fragment {

    private static String TAG = "LipeiFragment";
    private static final Logger logger = new Logger();
    private TextView tv_title;
    private TextView btn_lipei_add;
    private RecyclerView mylipei_recycler_view;
    public final ArrayList<QueryBaodanBean> newsBeanArrayList;
    private LipeiAdapter mAdapter;
    private EditText search_lipei_input_edit;
    private LinearLayoutManager mLayoutManager;

    private String errStr = "";
    private MultiBaodanBean insurresp;
    private LipeiTask mLipeiTask;
    private TextView tv_exit;
    private static int type;
    private ArrayList<QueryBaodanBean> showuserList;
    private ArrayList<QueryBaodanBean> userList;
    private boolean isFleg = false;
    private List<LiPeiLocalBean> liPeiLocalBeans = new ArrayList<>();
    private LipeiLocalAdapter lipeiLocalAdapter;

    public LipeiFragment() {
        newsBeanArrayList = new ArrayList<QueryBaodanBean>();
    }

    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lipei, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        databaseHelper = new DatabaseHelper(getContext());
        GlobalConfigure.model = Model.VERIFY.value();

        tv_exit = (TextView) view.findViewById(R.id.tv_exit);
        tv_exit.setVisibility(View.VISIBLE);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.cowface)
                        .setTitle("提示")
                        .setMessage("退出登录")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences pref = getActivity().getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.clear();
                                editor.commit();
                                Intent add_intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(add_intent);
                                ANIMAL_TYPE = 0;
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.setCancelable(false);
                builder.show();


            }
        });

        btn_lipei_add = (TextView) view.findViewById(R.id.btn_lipei_add);

        btn_lipei_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_intent = new Intent(getActivity(), PayActivity.class);
                startActivity(add_intent);
            }
        });

        mylipei_recycler_view = (RecyclerView) view.findViewById(R.id.mylipei_recycler_view);
//创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mylipei_recycler_view.setLayoutManager(mLayoutManager);
//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mylipei_recycler_view.setHasFixedSize(true);

        /*liPeiLocalBeans = databaseHelper.queryLocalDataFromLiPei();
        if (null != liPeiLocalBeans) {
            lipeiLocalAdapter = new LipeiLocalAdapter(liPeiLocalBeans, getContext());
            mylipei_recycler_view.setAdapter(lipeiLocalAdapter);
            lipeiLocalAdapter.notifyDataSetChanged();//更新RecycleView
        }*/

        search_lipei_input_edit = (EditText) view.findViewById(R.id.search_lipei_input_edit);
        search_lipei_input_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //当EditText内容发生变化时会调用此方法
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                TreeMap query = new TreeMap<String, String>();
                query.put("baodanNo", charSequence.toString());
                if (charSequence.toString() != null) {
                    mLipeiTask = new LipeiTask(HttpUtils.INSUR_QUERY_URL, query);
                    mLipeiTask.execute((Void) null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        isFleg = true;
        Log.i("==resume===", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        isFleg = true;
        Log.i("==resume===", "onStop");
    }

    @Override
    public void onResume() {
        if (ANIMAL_TYPE == ConstUtils.ANIMAL_TYPE_CATTLE) {
            tv_title.setText("牛险理赔");
        } else if (ANIMAL_TYPE == ConstUtils.ANIMAL_TYPE_DONKEY) {
            tv_title.setText("驴险理赔");
        } else if (ANIMAL_TYPE == ConstUtils.ANIMAL_TYPE_PIG) {
            tv_title.setText("猪险理赔");
        } else {
            tv_title.setText("理赔");
        }
        Log.i("==resume===", "resume");
        Log.i("==resume===", "isFleg" + isFleg);
        liPeiLocalBeans.clear();
        if (null != databaseHelper.queryLocalDataFromLiPei(PreferencesUtils.getStringValue(HttpUtils.user_id, getContext()))) {
            liPeiLocalBeans.addAll(databaseHelper.queryLocalDataFromLiPei(PreferencesUtils.getStringValue(HttpUtils.user_id, getContext())));
        }
        if (null != liPeiLocalBeans) {
            if (lipeiLocalAdapter == null) {
                lipeiLocalAdapter = new LipeiLocalAdapter(liPeiLocalBeans, getContext());
                lipeiLocalAdapter.setOnUpdateClickListener(new LipeiLocalAdapter.OnUpdateClickListener() {
                    @Override
                    public void onUpdateClick(File uploadFile, int model, int userId, String pbaodanNo) {

                    }
                });
                mylipei_recycler_view.setAdapter(lipeiLocalAdapter);
            } else {
                lipeiLocalAdapter.notifyDataSetChanged();//更新RecycleView
            }
        }

        super.onResume();
    }

    public class LipeiTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;

        LipeiTask(String url, TreeMap map) {
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
                    insurresp = (MultiBaodanBean) HttpUtils.processResp_new_detail_query(response);
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
            } catch (IOException e) {
                e.printStackTrace();
                errStr = "服务器错误！";
                return false;
            }
            //  register the new account here.

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLipeiTask = null;
            newsBeanArrayList.clear();
            if (success & HttpUtils.INSUR_QUERY_URL.equalsIgnoreCase(mUrl)) {
                showuserList = new ArrayList<>();
//                for (QueryBaodanBean queryBaodanBean : newsBeanArrayList) {
//                    if (queryBaodanBean.animalType == ANIMAL_TYPE) {
//                        showuserList.add(queryBaodanBean);
//                    }
//                }

                mAdapter = new LipeiAdapter(newsBeanArrayList, getContext());
                mylipei_recycler_view.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();//更新RecycleView

            } else if (!success) {
                //  显示失败
                Log.d(TAG, errStr);
            }
        }

        @Override
        protected void onCancelled() {
            mLipeiTask = null;
        }
    }

}
