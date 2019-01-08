package com.innovation.login.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.bean.LiPeiLocalBean;
import com.innovation.bean.PayImageUploadResultBean;
import com.innovation.bean.PayInfoContrastResultBean;
import com.innovation.bean.ResultBean;
import com.innovation.biz.dialog.LipeiResultDialog;
import com.innovation.base.Model;
import com.innovation.biz.processor.PayDataProcessor;
import com.innovation.location.AlertDialogManager;
import com.innovation.location.LocationManager;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.Utils;
import com.innovation.login.view.HomeActivity;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.OkHttp3Util;
import com.innovation.utils.PreferencesUtils;

import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;
import static com.innovation.base.InnApplication.getStringTouboaExtra;
import static com.innovation.base.InnApplication.getlipeiTempNumber;
import static com.innovation.biz.processor.PayDataProcessor.getAnimalEarsTagNo;
import static com.innovation.biz.processor.PayDataProcessor.getPayErji;
import static com.innovation.biz.processor.PayDataProcessor.getPaySanji;
import static com.innovation.biz.processor.PayDataProcessor.getPayYiji;
import static com.innovation.login.model.MyUIUTILS.getString;
import static com.innovation.login.view.HomeActivity.isOPen;
import static com.innovation.utils.HttpUtils.PAY_LIBUPLOAD;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;

public class LipeiLocalAdapter extends RecyclerView.Adapter<LipeiLocalAdapter.ViewHolder> {

    private final DatabaseHelper databaseHelper;
    private Gson gson;
    private ProgressDialog uploadDialog;
    private final LocationManager instance;
    private String responsePayInfoContrast;
    private ProgressDialog progressDialog;
    private View view;
    private LipeiResultDialog dialogLipeiResult;
    private ResultBean resultBean;
    private int userId;
    private String pbaodanNo;

    public void setLiPeiLocalBeans(List<LiPeiLocalBean> liPeiLocalBeans) {
        this.liPeiLocalBeans = liPeiLocalBeans;
    }

    private List<LiPeiLocalBean> liPeiLocalBeans;
    private Context context;
    private RecyclerViewOnItemClickListener mOnItemClickListener;
    private RecyclerViewOnItemClickListener mOnItemClickListenerZiliao;

    private String result;
    private String TAG = "LipeiAdapter";
    private String strfleg = "";
    private OnUpdateClickListener listener;
    public int lipeiUploadLibId = 0;

    private final Logger mLogger = new Logger(PayDataProcessor.class.getSimpleName());

    public LipeiLocalAdapter(List<LiPeiLocalBean> liPeiLocalBeans, Context mContext) {
        this.liPeiLocalBeans = liPeiLocalBeans;
        this.context = mContext;
        databaseHelper = new DatabaseHelper(context);
        instance = LocationManager.getInstance(context);
        instance.startLocation();
        gson = new Gson();
        dialogLipeiResult = new LipeiResultDialog(context);
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lipeilocal_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.lipei_num.setText(liPeiLocalBeans.get(position).pbaodanNo);
        viewHolder.lipei_name.setText(liPeiLocalBeans.get(position).pinsurename);
        String mtoubao_date;
        mtoubao_date = liPeiLocalBeans.get(position).pinsureDate;
        viewHolder.lipei_date.setText(mtoubao_date);
        if (liPeiLocalBeans.get(position).pcardNo != null) {
            viewHolder.lipei_idcard.setText(liPeiLocalBeans.get(position).pcardNo);
        }

        Log.i("==pzippath===", liPeiLocalBeans.get(position).pzippath);
        if (null != liPeiLocalBeans.get(position).pzippath && !"".equals(liPeiLocalBeans.get(position).pzippath)) {
            viewHolder.toubaocontinue.setText("重新录入");
        } else {
            viewHolder.toubaocontinue.setText("继续录入");
        }
        String precordeText = liPeiLocalBeans.get(position).precordeText;
        Log.i("=precordeText===", precordeText + "");
        //未录入
        if ("1".equals(liPeiLocalBeans.get(position).precordeText)) {
            viewHolder.uploadtext.setVisibility(View.GONE);
            viewHolder.toubao_continue_no.setVisibility(View.GONE);
            viewHolder.toubaocontinue.setVisibility(View.VISIBLE);
            viewHolder.lipei_update.setVisibility(View.GONE);
            //已录入
        } else if ("2".equals(liPeiLocalBeans.get(position).precordeText)) {
            viewHolder.uploadtext.setVisibility(View.VISIBLE);
            viewHolder.uploadtext.setText("已录入");
            viewHolder.toubao_continue_no.setVisibility(View.GONE);
            viewHolder.toubaocontinue.setVisibility(View.VISIBLE);
            viewHolder.lipei_update.setVisibility(View.VISIBLE);
            //已上传
        } else if ("3".equals(liPeiLocalBeans.get(position).precordeText)) {
            viewHolder.uploadtext.setVisibility(View.VISIBLE);
            viewHolder.uploadtext.setText("已上传");
            viewHolder.lipei_update.setVisibility(View.GONE);
            viewHolder.toubao_continue_no.setVisibility(View.VISIBLE);
            viewHolder.toubaocontinue.setVisibility(View.GONE);
        }
        //删除理赔单
        viewHolder.deletelocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != liPeiLocalBeans.get(position).pzippath && !"".equals(liPeiLocalBeans.get(position).pzippath)) {
                    strfleg = "投保人：" + liPeiLocalBeans.get(position).pinsurename + " ,该离线理赔单的理赔牲畜信息已完成，删除后将无法恢复";
                } else {
                    strfleg = "投保人：" + liPeiLocalBeans.get(position).pinsurename + " ,请确认";
                }
                AlertDialogManager.showMessageDialog(context, "提示", strfleg, new AlertDialogManager.DialogInterface() {
                    @Override
                    public void onPositive() {
                        //删除数据库并删除本地文件
                        String pinsureDate = liPeiLocalBeans.get(position).pinsureDate;
                        Log.i("===pzippath===", pinsureDate);
                        boolean b = databaseHelper.deleteLocalDataFromdate(pinsureDate);
                        Toast.makeText(context, "删除" + b, Toast.LENGTH_LONG).show();
                        FileUtils.deleteFile(liPeiLocalBeans.get(position).pzippath);
                        liPeiLocalBeans.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onNegative() {

                    }
                });
            }
        });

        //继续录入
        viewHolder.toubaocontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOPen(context)) {
                    if ("继续录入".equals(viewHolder.toubaocontinue.getText().toString())) {
                        GlobalConfigure.model = Model.BUILD.value();
                        Intent intent = new Intent();
                        intent.putExtra("ToubaoTempNumber", liPeiLocalBeans.get(position).pbaodanNo);
                        intent.setClass(context, DetectorActivity.class);
                        PreferencesUtils.saveBooleanValue("isli", true, context);
                        PreferencesUtils.saveKeyValue("lipeidate", liPeiLocalBeans.get(position).pinsureDate, context);
                        context.startActivity(intent);
                        collectNumberHandler.sendEmptyMessage(2);
                    } else {

                        AlertDialogManager.showMessageDialog(context, "提示", liPeiLocalBeans.get(position).precordeMsg, new AlertDialogManager.DialogInterface() {
                            @Override
                            public void onPositive() {
                                GlobalConfigure.model = Model.BUILD.value();
                                Intent intent = new Intent();
                                intent.putExtra("ToubaoTempNumber", liPeiLocalBeans.get(position).pbaodanNo);
                                intent.setClass(context, DetectorActivity.class);
                                PreferencesUtils.saveBooleanValue("isli", true, context);
                                PreferencesUtils.saveKeyValue("lipeidate", liPeiLocalBeans.get(position).pinsureDate, context);
                                context.startActivity(intent);
                                collectNumberHandler.sendEmptyMessage(2);
                            }

                            @Override
                            public void onNegative() {

                            }
                        });
                    }


                } else {
//                    openGPS(mContext);
                    AlertDialogManager.showMessageDialog(context, "提示", getString(R.string.locationwarning), new AlertDialogManager.DialogInterface() {
                        @Override
                        public void onPositive() {
                            openGPS1(context);
                        }

                        @Override
                        public void onNegative() {

                        }
                    });

                }
            }
        });
        //读取用户信息
        SharedPreferences pref_user = context.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userId = pref_user.getInt("uid", 0);
        //理赔单上传
        viewHolder.lipei_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int netWorkStates = com.innovation.network_status.NetworkUtil.getNetWorkStates(context);
                Log.i("netWorkStates", netWorkStates + "");
                if (-1 == netWorkStates) {
                    Toast.makeText(context, "无网络", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialogManager.showDialog(context, "提示", "确定上传理赔单吗？", new AlertDialogManager.DiaInterface() {
                        @Override
                        public void onPositive() {
                            Log.i("==pzippath===", liPeiLocalBeans.get(position).pzippath);
                            Log.i("==panimalType===", liPeiLocalBeans.get(position).panimalType);
                            Log.i("==pVideozippath===", liPeiLocalBeans.get(position).pVideozippath);
                            PreferencesUtils.saveKeyValue(HttpUtils.reason, liPeiLocalBeans.get(position).pinsureReason, context);
                            PreferencesUtils.saveBooleanValue(HttpUtils.offlineupdate, true, context);
                            File zipFile_video2 = new File(liPeiLocalBeans.get(position).pVideozippath);
                            int model = Model.VERIFY.value();

                            pbaodanNo = liPeiLocalBeans.get(position).pbaodanNo;
                            upload_zipVideo(model, zipFile_video2, userId, pbaodanNo, position);

                            dialogLipeiResult.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    Log.i("==DismissListener===", "刷新");
                                    viewHolder.uploadtext.setVisibility(View.VISIBLE);
                                    viewHolder.lipei_update.setVisibility(View.GONE);
                                    viewHolder.uploadtext.setText("已录入");
                                    liPeiLocalBeans.get(position).setPrecordeText("3");
                                    notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public void onNegative() {

                        }

                        @Override
                        public void showPop() {
                            HomeActivity activity = (HomeActivity) context;
                            progressDialog = showUploadDialog(activity);
                            progressDialog.show();
                        }
                    });

                }
            }
        });
    }

    private void comparer(int lipeiUploadGetLibId, int pot) {
        try {

            SharedPreferences pref_user = context.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
            int userId = pref_user.getInt("uid", 0);
            TreeMap<String, String> treeMapContrast = new TreeMap();
            treeMapContrast.put("baodanNoReal", PreferencesUtils.getStringValue("baodannum", context));
            treeMapContrast.put("reason", PreferencesUtils.getStringValue(HttpUtils.reason, context));
            treeMapContrast.put("cardNo", PreferencesUtils.getStringValue("cardnum", context));
            treeMapContrast.put("yiji", getPayYiji == null ? "" : getPayYiji);
            treeMapContrast.put("erji", getPayErji == null ? "" : getPayErji);
            treeMapContrast.put("sanji", getPaySanji == null ? "" : getPaySanji);
            treeMapContrast.put("pigNo", getAnimalEarsTagNo == null ? "" : getAnimalEarsTagNo);
            treeMapContrast.put("userId", String.valueOf(userId) == null ? "" : String.valueOf(userId));
            treeMapContrast.put("libId", String.valueOf(lipeiUploadGetLibId));
            treeMapContrast.put("longitude", instance.currentLon + "");
            treeMapContrast.put("latitude", instance.currentLat + "");
            treeMapContrast.put("collectTimes", String.valueOf(99));

            Log.e("理赔信息比对接口请求报文：", treeMapContrast.toString());
            FormBody.Builder builder = new FormBody.Builder();
            for (TreeMap.Entry<String, String> entry : treeMapContrast.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            RequestBody formBody = builder.build();

            responsePayInfoContrast = HttpUtils.post(HttpUtils.PAY_INFO_CONTRAST, formBody);
            Log.i("payInfoHandler:", HttpUtils.PAY_INFO_CONTRAST + "\n理赔信息比对接口responsePayInfoContrast:\n" + responsePayInfoContrast);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("理赔", "理赔信息比对接口异常");
            Toast.makeText(context, "理赔信息比对接口异常！", Toast.LENGTH_SHORT).show();
        }

        if (null != responsePayInfoContrast) {
            progressDialog.dismiss();
            ResultBean resultBeanPayInfoContrast = gson.fromJson(responsePayInfoContrast, ResultBean.class);
            if (resultBeanPayInfoContrast.getStatus() == 1) {
                //   展示比对结果
                payInfoHandler.sendEmptyMessage(18);
                Message obtain = Message.obtain();
                obtain.arg1 = pot;
                payInfoHandler.sendMessage(obtain);
            } else if (resultBeanPayInfoContrast.getStatus() == 0) {
                Log.e("理赔", resultBeanPayInfoContrast.getMsg());
                collectNumberHandler.sendEmptyMessage(2);
//            } else {

                AlertDialog.Builder builder22 = new AlertDialog.Builder(context)
                        .setIcon(R.drawable.cowface)
                        .setTitle("提示")
                        .setMessage(resultBeanPayInfoContrast.getMsg())
                        .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                liPeiLocalBeans.get(pot).setPrecordeText("3");
                                notifyDataSetChanged();
                                dialog.dismiss();
                                //  mActivity.finish();
                            }
                        });
                builder22.setCancelable(false);
                builder22.show();
            }
        }


    }

    private void reInitCurrentDir() {
        Log.i("reInitCurrentDir:", "重新初始化Current文件");
        if (GlobalConfigure.model == Model.BUILD.value()) {
            GlobalConfigure.mediaInsureItem.currentDel();
            GlobalConfigure.mediaInsureItem.currentInit();
        } else if (GlobalConfigure.model == Model.VERIFY.value()) {
            GlobalConfigure.mediaPayItem.currentDel();
            GlobalConfigure.mediaPayItem.currentInit();
        }
    }

    public ProgressDialog showUploadDialog(Activity activity) {
        ProgressDialog mUploadDialog = new ProgressDialog(activity);
        mUploadDialog.setTitle(R.string.dialog_title);
        mUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mUploadDialog.setCancelable(false);
        mUploadDialog.setCanceledOnTouchOutside(false);
        mUploadDialog.setIcon(R.drawable.cowface);
        mUploadDialog.setMessage("开始处理......");
        Log.i("====show==", "show" + System.currentTimeMillis()
        );
        return mUploadDialog;
    }

    private void openGPS1(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        HomeActivity activity = (HomeActivity) mContext;
        activity.startActivityForResult(intent, 1315);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return liPeiLocalBeans.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lipei_num;
        public TextView lipei_name;
        public TextView isnot_lipei;
        public TextView lipei_date;
        public TextView lipei_idcard;
        public TextView lipei_update;
        public TextView deletelocal;
        public TextView uploadtext;
        public TextView toubaocontinue, toubao_continue_no;
        public RelativeLayout relative;

        public ViewHolder(View view) {
            super(view);
            lipei_num = (TextView) view.findViewById(R.id.lipei_num);
            lipei_name = (TextView) view.findViewById(R.id.lipei_name);
            lipei_date = (TextView) view.findViewById(R.id.lipei_date);
            lipei_idcard = (TextView) view.findViewById(R.id.lipei_idcard);
            isnot_lipei = (TextView) view.findViewById(R.id.isnot_lipei);
            lipei_update = (TextView) view.findViewById(R.id.lipei_update);
            deletelocal = (TextView) view.findViewById(R.id.deletelocal);
            uploadtext = (TextView) view.findViewById(R.id.uploadtext);
            toubaocontinue = (TextView) view.findViewById(R.id.toubao_continue);
            toubao_continue_no = (TextView) view.findViewById(R.id.toubao_continue_no);
            relative = (RelativeLayout) view.findViewById(R.id.relative);
        }
    }


    /**
     * 设置点击事件
     */
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 设置点击事件
     */
    public void setRecyclerViewOnItemClickListenerZiliao(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListenerZiliao = onItemClickListener;
    }


    /**
     * 点击事件接口
     */
    public interface RecyclerViewOnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(File uploadFile, int model, int userId, String pbaodanNo);
    }


    public void setOnUpdateClickListener(OnUpdateClickListener listener) {
        this.listener = listener;
    }

    public void uploadZipImage(int model, File zipFileImage, int uid, String libNum, int position) {
        //publishProgress(MSG_UI_PROGRESS_UPLOAD_IMG);
        int source = 1;
        String gps = null;
        try {
            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put(Utils.UploadNew.USERID, uid + "");
            treeMap.put(Utils.UploadNew.LIB_NUM, libNum);
            treeMap.put(Utils.UploadNew.TYPE, model + "");
            treeMap.put(Utils.UploadNew.LIBD_SOURCE, source + "");
            //    treeMap.put(Utils.UploadNew.LIB_ENVINFO, getEnvInfo(mActivity, gps));
            treeMap.put(Utils.UploadNew.LIB_ENVINFO, "");
            treeMap.put("collectTimes", String.valueOf(99));
            treeMap.put("timesFlag", "");
            Log.e("理赔图片包上传接口请求报文：", treeMap.toString() + "\n请求地址：" + PAY_LIBUPLOAD);

            Map<String, String> header = new HashMap<>();
            header.put("AppKeyAuthorization", "hopen");
            header.put("Content-Type", "application/x-www-form-urlencoded");
            // TODO: 2018/8/16 By:LuoLu  添加请求头 animalType GlobalConfigure.ANIMAL_TYPE)  String.valueOf(GlobalConfigure.ANIMAL_TYPE)
            header.put("animalType", String.valueOf(ANIMAL_TYPE));
            OkHttp3Util.uploadPreFile(PAY_LIBUPLOAD, zipFileImage, zipFileImage.getName(), treeMap, header, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("===e==", e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //  if (response.isSuccessful()) {
                    String responsePayZipImageUpload = response.body().string();
                    if (responsePayZipImageUpload != null) {
                        Log.e("理赔图片包上传接口返回：\n", PAY_LIBUPLOAD + "\nresponsePayZipImageUpload:\n" + responsePayZipImageUpload);
                        ResultBean resultPayZipImageBean = gson.fromJson(responsePayZipImageUpload, ResultBean.class);
                        if (resultPayZipImageBean.getStatus() == 1) {
                            PayImageUploadResultBean payImageUploadResultBean = gson.fromJson(responsePayZipImageUpload, PayImageUploadResultBean.class);
                            int lipeirecordernum = databaseHelper.updateLiPeiLocalFromrecordeText("3", PreferencesUtils.getStringValue("lipeidate", context));
                            Log.i("=lipeirecordernum===", lipeirecordernum + "");
                            //获取ib_id
                            int lipeiUploadGetLibId = payImageUploadResultBean.getData().getLibId();
                            //publishProgress(MSG_UI_PROGRESS_IMAGE_CONTRAST);
                            // payInfoHandler.sendEmptyMessage(17);
                            comparer(lipeiUploadGetLibId, position);
                        } else {
                            //                    image quality bad
                            AlertDialog.Builder builder34 = new AlertDialog.Builder(context)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultPayZipImageBean.getMsg())
                                    .setPositiveButton("重新采集", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            reInitCurrentDir();
                                            collectNumberHandler.sendEmptyMessage(2);
                                        }
                                    });
                            builder34.setCancelable(false);
                            builder34.show();
                        }
                    } else {
                        //                server down
                        payInfoHandler.sendEmptyMessage(42);
                    }
                }
                // }
            });


          /*  MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            FormBody.Builder builder = new FormBody.Builder();
            for (TreeMap.Entry<String, String> entry : treeMap.entrySet()) {
                requestBody.addFormDataPart(entry.getKey(), entry.getValue());
            }
            Log.i("===zipFile==", zipFileImage.getName());
            requestBody.addFormDataPart("zipFile", zipFileImage.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"), zipFileImage));
            String responsePayZipImageUpload = HttpUtils.post(PAY_LIBUPLOAD, requestBody.build());*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upload_zipVideo(int model, File zipFile_image, int uid, String libNum, int position) {
        int source = 2;
        String gps = null;
        try {
            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put(Utils.UploadNew.USERID, uid + "");
            treeMap.put(Utils.UploadNew.LIB_NUM, libNum);
            treeMap.put(Utils.UploadNew.TYPE, model + "");
            treeMap.put(Utils.UploadNew.LIBD_SOURCE, source + "");
//            treeMap.put(Utils.UploadNew.LIB_ENVINFO, getEnvInfo(context, gps));
            Map<String, String> header = new HashMap<>();
            header.put("AppKeyAuthorization", "hopen");
            header.put("Content-Type", "application/x-www-form-urlencoded");
            // TODO: 2018/8/16 By:LuoLu  添加请求头 animalType GlobalConfigure.ANIMAL_TYPE)  String.valueOf(GlobalConfigure.ANIMAL_TYPE)
            header.put("animalType", String.valueOf(ANIMAL_TYPE));
            Log.e("离线理赔视频上传接口请求报文：", treeMap.toString() + "\n请求地址：" + PAY_LIBUPLOAD);
            OkHttp3Util.uploadPreFile(PAY_LIBUPLOAD, zipFile_image, "video.zip", treeMap, header, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("离线理赔视频上传接口eee：", e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responsePayVideoUpload = response.body().string();
                        mLogger.e("离线理赔视频文件上传接口返回：\n" + PAY_LIBUPLOAD + "\nresponsePayVideoUpload:" + responsePayVideoUpload);
                        if (responsePayVideoUpload != null) {
                            resultBean = gson.fromJson(responsePayVideoUpload, ResultBean.class);
                            if (resultBean.getStatus() == 1) {
//                        upload success
                                mLogger.i("responsePayImageUpload data:" + resultBean.getData().toString());
//                        toubaoUploadBean = gson.fromJson(responsePayImageUpload, ToubaoUploadBean.class);
//                        mLogger.i("理赔视频 libID:" + toubaoUploadBean.getData().getLibId());
//                        addAnimalLibID = String.valueOf(toubaoUploadBean.getData().getLibId());

//                        insuranceDataHandler.sendEmptyMessage(18);
                                File zipFile_image2 = new File(liPeiLocalBeans.get(position).pzippath);
                                uploadZipImage(model, zipFile_image2, userId, pbaodanNo, position);
                            } else if (resultBean.getStatus() == 0) {
//                        image bad
                                payInfoHandler.sendEmptyMessage(199);
                            } else {
//                server down
                                payInfoHandler.sendEmptyMessage(422);
                            }

                        }
                    }
                }
            });


           /* MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            FormBody.Builder builder = new FormBody.Builder();
            for (TreeMap.Entry<String, String> entry : treeMap.entrySet()) {
                requestBody.addFormDataPart(entry.getKey(), entry.getValue());
            }
            requestBody.addFormDataPart("zipFile", zipFile_image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"), zipFile_image));
            // TODO: 2018/8/4*/
            // String responsePayVideoUpload = HttpUtils.post(PAY_LIBUPLOAD, requestBody.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences pref_user;
    int payInfoContrastResultLipeiId;
    @SuppressLint("HandlerLeak")
    private final Handler payInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 18:
                    PayInfoContrastResultBean payInfoContrastResultBean = gson.fromJson(responsePayInfoContrast, PayInfoContrastResultBean.class);
                    payInfoContrastResultLipeiId = payInfoContrastResultBean.getData().getLipeiId();

                    switch (payInfoContrastResultBean.getData().getResultPic().size()) {
                        case 1:
                            dialogLipeiResult.setImage2(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(0).getPic()));
                            dialogLipeiResult.setLipeiResultmessage(payInfoContrastResultBean.getData().getResultMsg()
                                    + "\n"
                                    + payInfoContrastResultBean.getData().getResultPic().get(0).getDetail() + "\n");
                            break;
                        case 2:
                            dialogLipeiResult.setImage1(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(0).getPic()));
                            dialogLipeiResult.setImage3(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(1).getPic()));
                            dialogLipeiResult.setLipeiResultmessage(payInfoContrastResultBean.getData().getResultMsg()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(0).getDetail()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(1).getDetail() + "\n");
                            break;
                        case 3:
                            dialogLipeiResult.setImage1(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(0).getPic()));
                            dialogLipeiResult.setImage2(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(1).getPic()));
                            dialogLipeiResult.setImage3(String.valueOf(payInfoContrastResultBean.getData().getResultPic().get(2).getPic()));
                            dialogLipeiResult.setLipeiResultmessage(payInfoContrastResultBean.getData().getResultMsg()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(0).getDetail()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(1).getDetail()
                                    + "\n" + payInfoContrastResultBean.getData().getResultPic().get(2).getDetail() + "\n");
                            break;
                    }
                    View.OnClickListener listener_new = v -> {
                        dialogLipeiResult.dismiss();
                        //    1.4	理赔申请处理接口
                        payInfoHandler.sendEmptyMessage(19);
                    };
                    View.OnClickListener listener_ReCollect = v -> {
                        if (PreferencesUtils.getBooleanValue(HttpUtils.offlineupdate, context)) {
                            PreferencesUtils.saveBooleanValue(HttpUtils.offlineupdate, false, context);
                            dialogLipeiResult.dismiss();
                        } else {
                            dialogLipeiResult.dismiss();
                            Intent intent = new Intent(context, DetectorActivity.class);
                            intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                            intent.putExtra("LipeiTempNumber", getlipeiTempNumber);
                            context.startActivity(intent);
                            reInitCurrentDir();
                            collectNumberHandler.sendEmptyMessage(2);

                        }
                    };

                    dialogLipeiResult.setTitle("验证结果");
                    dialogLipeiResult.setBtnGoApplication("直接申请", listener_new);
                    if (PreferencesUtils.getBooleanValue(HttpUtils.offlineupdate, context)) {
                        dialogLipeiResult.setBtnReCollect("放弃", listener_ReCollect);
                    } else {
                        dialogLipeiResult.setBtnReCollect("重新拍摄", listener_ReCollect);
                    }
                    dialogLipeiResult.show();

                    break;


                case 19:
                    String responsePayApplyResult = null;
                    try {
                        TreeMap<String, String> treeMapPayApply = new TreeMap();
                        treeMapPayApply.put("lipeiId", String.valueOf(payInfoContrastResultLipeiId));
                        Log.e("理赔申请处理接口", treeMapPayApply.toString());
                        FormBody.Builder builderPayApply = new FormBody.Builder();
                        for (TreeMap.Entry<String, String> entry : treeMapPayApply.entrySet()) {
                            builderPayApply.add(entry.getKey(), entry.getValue());
                        }
                        RequestBody formBodyPayApply = builderPayApply.build();
                        responsePayApplyResult = HttpUtils.post(HttpUtils.PAY_APPLY, formBodyPayApply);
                        Log.i("payInfoHandler:", HttpUtils.PAY_APPLY + "\n理赔申请处理接口responsePayApplyResult:\n"
                                + responsePayApplyResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "理赔申请处理接口异常！", Toast.LENGTH_SHORT).show();
                    }
                    ResultBean resultBeanPayApply;
                    if (null != responsePayApplyResult) {
                        resultBeanPayApply = gson.fromJson(responsePayApplyResult, ResultBean.class);
                        if (resultBeanPayApply.getStatus() == 1) {
                            AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(context)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayApply.getMsg())
                                    .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builderApplyFinish.setCancelable(false);
                            builderApplyFinish.show();

                        } else if (resultBeanPayApply.getStatus() == 0) {
                            AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(context)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayApply.getMsg())
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            payInfoHandler.sendEmptyMessage(19);
                                        }
                                    });

                            builderApplyFinish.setCancelable(false);
                            builderApplyFinish.show();
                        } else if (resultBeanPayApply.getStatus() == -1) {
                            AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(context)
                                    .setIcon(R.drawable.cowface)
                                    .setTitle("提示")
                                    .setMessage(resultBeanPayApply.getMsg())
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            payInfoHandler.sendEmptyMessage(19);
                                        }
                                    });

                            builderApplyFinish.setCancelable(false);
                            builderApplyFinish.show();
                        }
                    } else {
                        AlertDialog.Builder builderApplyFinish = new AlertDialog.Builder(context)
                                .setIcon(R.drawable.cowface)
                                .setTitle("提示")
                                .setMessage("服务端异常，请稍后再试！")
                                .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builderApplyFinish.setCancelable(false);
                        builderApplyFinish.show();
                    }


                    break;


                case 199:
                    AlertDialog.Builder builder199 = new AlertDialog.Builder(context)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(context, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    context.startActivity(intent);
                                    reInitCurrentDir();
                                    collectNumberHandler.sendEmptyMessage(2);
                                    // context.finish();
                                }
                            });
                    builder199.setCancelable(false);
                    builder199.show();
                    break;
                case 422:
                    AlertDialog.Builder builder422 = new AlertDialog.Builder(context)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultBean.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //mActivity.finish();
                                }
                            });
                    builder422.setCancelable(false);
                    builder422.show();
                    break;


                default:
                    break;
            }

        }
    };


}
