package com.innovation.login.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.base.BaseFragment;
import com.innovation.bean.BaoDanNetBean;
import com.innovation.base.InnApplication;
import com.innovation.bean.QueryBaodanBean;
import com.innovation.biz.insurance.YanBiaoDanActivity;
import com.innovation.base.Model;
import com.innovation.biz.login.LoginActivity;
import com.innovation.location.AlertDialogManager;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.Utils;
import com.innovation.login.model.LocalModelNongxian;
import com.innovation.login.model.MyUIUTILS;
import com.innovation.login.model.ToubaoAdapter;
import com.innovation.login.model.ToubaoLocalAdapter;
import com.innovation.login.model.ToubaoNetAdapter;
import com.innovation.login.presenter.TouBaoPresenter;
import com.innovation.utils.GsonUtils;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.OkHttp3Util;
import com.innovation.utils.PreferencesUtils;
import com.innovation.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.innovation.base.GlobalConfigure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.prefs.Prefs;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;

public class ToubaoFragment extends BaseFragment implements ITouBao {

    private static String TAG = "ToubaoFragment";
    private TextView tv_title;
    private TextView search_input_edit;
    private Button btn_toubao_add;
    private TextView tv_exit;
    private static Context mContext;
    private Unbinder unbinder;
    ArrayList<QueryBaodanBean> localInsureList = new ArrayList<>();
    private RecyclerView localInsuredRecyclerView;
    private RecyclerView mRecyclerView;
    static Handler mHandler;
    private ToubaoAdapter mAdapter;
    private ToubaoLocalAdapter localInsureAdapter;
    ArrayList<QueryBaodanBean> userList;
    ArrayList<QueryBaodanBean> showuserList;
    private TouBaoPresenter presenter;
    private DatabaseHelper databaseHelper;
    private CharSequence mcharSequence = "";
    private static int type;
    private Button keywordSearchButton;
    private int userid;
    private List<QueryBaodanBean> beanList = new ArrayList<>();
    private List<QueryBaodanBean> beanList_item;
    private List<BaoDanNetBean> baoDanNetBeans;

    @SuppressLint("HandlerLeak")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter = new TouBaoPresenter(this);
        mContext = context;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0://接收消息
                        String insuredNo = (String) msg.obj;
                        QueryBaodanBean insured = null;
                        for (QueryBaodanBean baodanBean : userList) {
                            if (baodanBean.baodanNo.equals(insuredNo)) {
                                insured = baodanBean;
                            }
                        }
                        if (insured != null) {
                            localInsureList.add(insured);
                        }
                        break;

                }
            }
        };
        prefs = Prefs.with(mContext, "insured_nos");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View createSuccessView() {
        View view = MyUIUTILS.inflate(R.layout.fragment_toubao);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO: 2018/8/8 加载数据
        String jsonString = prefs.read("insured_nos");
        QueryBaodanBean[] queryBaodanBeans = gson.fromJson(jsonString, QueryBaodanBean[].class);
        //localInsureList.addAll(list);
        // TODO: 2018/8/8   获取数据
        if (queryBaodanBeans != null) {
            for (QueryBaodanBean queryBaodanBean : queryBaodanBeans) {
                localInsureList.add(queryBaodanBean);
            }
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        InnApplication.isOfflineMode = false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = new DatabaseHelper(getContext());
        GlobalConfigure.model = Model.BUILD.value();
        SharedPreferences pref = mContext.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userid = pref.getInt("uid", 0);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_exit = (TextView) view.findViewById(R.id.tv_exit);

        tv_exit.setVisibility(View.VISIBLE);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.cowface).setTitle("提示")
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

        btn_toubao_add = (Button) view.findViewById(R.id.btn_toubao_add);
        btn_toubao_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_intent = new Intent(getActivity(), YanBiaoDanActivity.class);
                startActivity(add_intent);
            }
        });
        localInsuredRecyclerView = view.findViewById(R.id.local_insured_recyclerView);
        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setHasFixedSize(true);
        //创建默认的线性LayoutManager
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        search_input_edit = (EditText) view.findViewById(R.id.search_tag_input_edit);
        keywordSearchButton = view.findViewById(R.id.keywordSearchButton);
        keywordSearchButton.setOnClickListener(keywordSearchButtonClickListener);

        localInsuredRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    private void doPostQuery() {
        Map map = new HashMap();
        map.put(HttpUtils.AppKeyAuthorization, "hopen");
        map.put("Content-Type", "application/x-www-form-urlencoded");
        map.put("animalType", String.valueOf(ANIMAL_TYPE));
        Map mapbody = new HashMap();
        mapbody.put("uid", String.valueOf(userid));
        OkHttp3Util.doPost(HttpUtils.SEARCH_YANBIAO, mapbody, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TouBaoFra", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        int status = jsonObject.getInt("status");
                        String msg = jsonObject.optString("msg");
                        if (status == 1) {
                            baoDanNetBeans = new ArrayList<>();
                            try {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject1 = data.getJSONObject(i);
                                    String baodanNo = jsonObject1.optString("baodanNo");
                                    String yanBiaoName = jsonObject1.optString("yanBiaoName");
                                    String name = jsonObject1.optString("name");
                                    String cardNo = jsonObject1.optString("cardNo");
                                    String animalType = jsonObject1.optString("animalType");
                                    String createtime = jsonObject1.optString("createtime");
                                    int id = jsonObject1.getInt("id");
                                    PreferencesUtils.saveIntValue("baodan_id", id, getContext());
                                    PreferencesUtils.saveKeyValue("animalType", animalType, getContext());
                                    BaoDanNetBean baoDanNetBean = new BaoDanNetBean(baodanNo, yanBiaoName, name, cardNo, createtime);
                                    baoDanNetBeans.add(baoDanNetBean);
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("====", baoDanNetBeans.size() + "");

                                        ToubaoNetAdapter toubaoNetAdapter = new ToubaoNetAdapter(baoDanNetBeans, mContext, databaseHelper);
                                        mRecyclerView.setAdapter(toubaoNetAdapter);
                                        toubaoNetAdapter.notifyDataSetChanged();
                                        toubaoNetAdapter.setListner(new ISExist() {
                                            @Override
                                            public void isexist(boolean exist) {
                                                if (exist) {
                                                    Toast.makeText(mContext, "保单信息已添加到离线", Toast.LENGTH_SHORT).show();
                                                    List<LocalModelNongxian> localModels = databaseHelper.queryLocalDatas(PreferencesUtils.getStringValue(HttpUtils.user_id,getContext()));
                                                    if (null != localModels) {
                                                        showLocalDataBase(localModels);
                                                    }

                                                } else {
                                                    Toast.makeText(mContext, "查询到" + ToubaoFragment.this.userList.size() + "条数据", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            AlertDialogManager.showMessageDialog(getContext(), "tishi", msg, new AlertDialogManager.DialogInterface() {
                                @Override
                                public void onPositive() {

                                }

                                @Override
                                public void onNegative() {

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private View.OnClickListener keywordSearchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            keywordSearchButton.setEnabled(false);
            if (
                    search_input_edit.getText().toString().trim().equals("")) {
                TreeMap query = new TreeMap<String, String>();
                query.put("uid", String.valueOf(userid));
                presenter.setparameter(HttpUtils.SEARCH_YANBIAO, query);
            } else {
                TreeMap treeMapKeywordSearch = new TreeMap<String, String>();
                treeMapKeywordSearch.put("keyword", search_input_edit.getText().toString().trim());
                treeMapKeywordSearch.put("uid", String.valueOf(userid));
                Log.i("keywordSearch:", search_input_edit.getText().toString().trim() + "");
                presenter.setparameter(HttpUtils.INSUR_DETAIL_QUERY_URL, treeMapKeywordSearch);
            }
        }
    };

    @Override
    public void onPause() {
        Log.i("onPause:", "ToubaoFragment");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("onResume:", "ToubaoFragment");
        if (ANIMAL_TYPE == 2) {
            tv_title.setText("牛险投保");
        } else if (ANIMAL_TYPE == 3) {
            tv_title.setText("驴险投保");
        } else if (ANIMAL_TYPE == 1) {
            tv_title.setText("猪险投保");
        } else {
            tv_title.setText("投保");
        }

        //显示离线保单
        List<LocalModelNongxian> localModels = databaseHelper.queryLocalDatas(PreferencesUtils.getStringValue(HttpUtils.user_id,getContext()));
        showLocalDataBase(localModels);

       /* TreeMap query = new TreeMap<String, String>();
        query.put("uid", String.valueOf(userid));
        presenter.setparameter(HttpUtils.SEARCH_YANBIAO, query);*/
        doPostQuery();

        super.onResume();

    }

    //展示本地缓存的数据
    private void showLocalDataBase(List<LocalModelNongxian> localModels) {
        if (localModels != null && localModels.size() > 0) {
            localInsuredRecyclerView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = localInsuredRecyclerView.getLayoutParams();
            lp.height = UIUtils.dp2px(mContext, 130 * 1);
            localInsuredRecyclerView.setLayoutParams(lp);
            localInsureAdapter = new ToubaoLocalAdapter(localModels, mContext, mHandler, databaseHelper);
            localInsuredRecyclerView.setAdapter(localInsureAdapter);
            //删除后刷新本地数据和网络查询的数据
            localInsureAdapter.setListner(new ISExist() {
                @Override
                public void isexist(boolean exist) {
                    if (exist) {
                        List<LocalModelNongxian> localModels1 = databaseHelper.queryLocalDatas(PreferencesUtils.getStringValue(HttpUtils.user_id,getContext()));
                        Log.i("====deleteanimalAfter", localModels1.size() + "");
                        showLocalDataBase(localModels1);
                        //showNetDataBase(beanList);
                        doPostQuery();
                    }

                }
            });
        } else {
            localInsuredRecyclerView.setVisibility(View.GONE);
        }
    }

    Prefs prefs;
    Gson gson = new Gson();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    @Override
    public void onDestroy() {
        String jsonString = gson.toJson(localInsureList);
        prefs.write("insured_nos", jsonString);
        super.onDestroy();
    }


    @Override
    public void success(String string) {
        keywordSearchButton.setEnabled(true);
        beanList.clear();
        beanList_item = GsonUtils.getBeanList(string, QueryBaodanBean.class);
        beanList.addAll(beanList_item);
        Log.i("===beanList===", beanList.size() + "");
        showNetDataBase(beanList);
        List<LocalModelNongxian> localModels = databaseHelper.queryLocalDatas(PreferencesUtils.getStringValue(HttpUtils.user_id,getContext()));
        showLocalDataBase(localModels);
    }

    @Override
    public void error(String string) {
        keywordSearchButton.setEnabled(true);
        Toast.makeText(mContext, "保单查询失败 ：" + string, Toast.LENGTH_SHORT).show();
    }

    private void showNetDataBase(List<QueryBaodanBean> userList) {
        showuserList = new ArrayList<>();
        for (QueryBaodanBean queryBaodanBean : userList) {
            if (queryBaodanBean.animalType == ANIMAL_TYPE) {
                showuserList.add(queryBaodanBean);
            }
        }
        mAdapter = new ToubaoAdapter(showuserList, mContext, mHandler, databaseHelper);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setListner(new ISExist() {
            @Override
            public void isexist(boolean exist) {
                if (exist) {
                    Toast.makeText(mContext, "保单信息已添加到离线", Toast.LENGTH_SHORT).show();
                    List<LocalModelNongxian> localModels = databaseHelper.queryLocalDatas(PreferencesUtils.getStringValue(HttpUtils.user_id,getContext()));
                    showLocalDataBase(localModels);
                } else {
                    Toast.makeText(mContext, "查询到" + ToubaoFragment.this.userList.size() + "条数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



