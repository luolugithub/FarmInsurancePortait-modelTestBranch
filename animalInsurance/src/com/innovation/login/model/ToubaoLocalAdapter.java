package com.innovation.login.model;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.bean.BaodanBean;
import com.innovation.base.InnApplication;
import com.innovation.bean.MultiBaodanBean;
import com.innovation.biz.Insured.LocaleInsuredSaveListener;
import com.innovation.biz.Insured.ResponseBean;
import com.innovation.base.Model;
import com.innovation.data.source.InsuredNos;
import com.innovation.location.AlertDialogManager;
import com.innovation.login.DatabaseHelper;
import com.innovation.login.Utils;
import com.innovation.login.view.HomeActivity;
import com.innovation.login.view.ISExist;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.UploadUtils;

import org.tensorflow.demo.DetectorActivity;
import com.innovation.base.GlobalConfigure;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;
import static com.innovation.base.InnApplication.OFFLINE_PATH;
import static com.innovation.login.model.MyUIUTILS.getString;
import static com.innovation.login.view.HomeActivity.isOPen;
import static com.innovation.utils.FileUtils.getMD5FromPath;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;
import static com.innovation.base.GlobalConfigure.waitUploadCount;

public class ToubaoLocalAdapter extends RecyclerView.Adapter<ToubaoLocalAdapter.ViewHolder> {

    private DatabaseHelper databaseHelper;
//    private List<LocalModel> localInsuredNos;
    private List<LocalModelNongxian> localModelNongxianList;
    private Context mContext;
    private RecyclerViewOnItemClickListener mOnItemClickListener;
    private RecyclerViewOnItemClickListener mOnItemClickListenerZiliao;
    //保单保存到本地的回调
    private LocaleInsuredSaveListener mLocaleInsuredSaveListener;

    private String result;
    private ToubaoTask mToubaoTask;
    private BaodanBean insurresp;
    private String errStr;
    Handler mHandler;
    String insuredNo;
    private String qurreyPid;
    private ResponseBean beanResponse;
    private int[] cnt;
//    private Gson gson= new Gson();

    // 上传文件用全局变量
    // zip文件List
    List<String> zipFilePaths;
    // 用户ID
    Integer userId;
    // 时间戳
    String batchId;
    // 处理文件对象Index
    int zipIndex = 0;
    private int status = -44;
    private int waitUploadCountTemp;
    private MultiBaodanBean offlineBuildResult;
    private ISExist isExist;

    private void callZipUpload() {
        if (zipFilePaths.size() > zipIndex) {
            String zipFilePath = zipFilePaths.get(zipIndex);
            String md5String = getMD5FromPath(zipFilePath);
            offlineZipUpLoad(zipFilePath, md5String, userId.toString(), batchId);
        } else {
            // 处理
            mHandlerLocal.sendEmptyMessage(9);
        }

    }


    public ToubaoLocalAdapter(List<LocalModelNongxian> localInsouredNos, Context mContext, Handler handler, DatabaseHelper databaseHelper) {
        this.localModelNongxianList = localInsouredNos;
        this.mContext = mContext;
        this.databaseHelper = databaseHelper;
        mHandler = handler;
//        waitUploadCount = waitUploadCountTemp;
        offlineBuildResult = new MultiBaodanBean();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.toubao_local_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

//        viewHolder.toubao_num.setText(localInsuredNos.get(position).getBaodanNo());
        viewHolder.toubao_num.setText(localModelNongxianList.get(position).getYanBiaoName().equals("")
                ? localModelNongxianList.get(position).getBaodanNo() :
                localModelNongxianList.get(position).getYanBiaoName());
        String uploadZipFilePathStr = Environment.getExternalStorageDirectory().getPath() +
                OFFLINE_PATH + localModelNongxianList.get(position).getBaodanNo() + "/";
        List<String> zipFilePathsTmp = FileUtils.getAllFile(uploadZipFilePathStr, ".zip");
        waitUploadCount = zipFilePathsTmp.size();
        Log.i("viewHolder待上传数量：", String.valueOf(waitUploadCount));
        viewHolder.cow_to_upload.setText(waitUploadCount + "头信息未上传");


        viewHolder.toubao_name.setText(localModelNongxianList.get(position).getName());
        if (!localModelNongxianList.get(position).getInsureDate().equals("")) {
            String mtoubao_date = localModelNongxianList.get(position).getInsureDate();
            viewHolder.toubao_date.setText(mtoubao_date);
        }

        if (localModelNongxianList.get(position).getCardNo() != null) {
            viewHolder.toubao_idcard.setText(localModelNongxianList.get(position).getCardNo());
        }

        viewHolder.toubao_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalModelNongxian qb = localModelNongxianList.get(position);
                insuredNo = qb.getBaodanNo();
                String uploadZipFilePathStr = Environment.getExternalStorageDirectory().getPath() + OFFLINE_PATH + insuredNo + "/";
                zipFilePaths = FileUtils.getAllFile(uploadZipFilePathStr, ".zip");
                SharedPreferences pref = mContext.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
                userId = pref.getInt("uid", 0);
                SimpleDateFormat tmpSimpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault());
                batchId = tmpSimpleDateFormat.format(new Date(System.currentTimeMillis()));
                if (zipFilePaths.size() > 0) {
                    // 显示：正在上传
                    mHandlerLocal.sendEmptyMessage(8);
                    // 调用上传方法
                    zipIndex = 0;
                    callZipUpload();

                } else {
                    // TODO: 2018/8/11 By:LuoLu  无需上传文件时反馈
                    mHandlerLocal.sendEmptyMessage(100);
                }

            }
        });


        viewHolder.toubao_continue.setOnClickListener(v -> {
            if (isOPen(mContext)) {
                // TODO: 2018/8/8   进入离线流程
                LocalModelNongxian qb = localModelNongxianList.get(position);
                insuredNo = qb.getBaodanNo();
                InnApplication.isOfflineMode = true;
                InnApplication.offLineInsuredNo = insuredNo;
                InnApplication.getStringTouboaExtra = insuredNo;
                Log.i("离线保单号:", InnApplication.getStringTouboaExtra);
                showNormalDialog();
            } else {
                openGPS1(mContext);
            }

        });
        viewHolder.deletedInsuredButon.setOnClickListener(v -> {
            insuredNo = localModelNongxianList.get(position).getBaodanNo();
            InnApplication.getStringTouboaExtra = insuredNo;
            showDialog();
            Log.i("Tbl保单号:", insuredNo);
        });


    }

    private void openGPS1(Context mContext) {
        AlertDialogManager.showMessageDialog(mContext, "提示", getString(R.string.locationwarning), new AlertDialogManager.DialogInterface() {
            @Override
            public void onPositive() {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                HomeActivity activity = (HomeActivity) mContext;
                activity.startActivityForResult(intent, 1315);
        }

            @Override
            public void onNegative() {

            }
        });

    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(waitUploadCount > 0 ? "还有" + waitUploadCount + "条数据未上传,確定刪除吗" : "确定删除数据吗?");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (databaseHelper.deleteLocalDataFromBaodanNo(insuredNo)) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                    isExist.isexist(true);
                } else {
                    isExist.isexist(false);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setCancelable(false);
        builder.show();
    }

    Gson gson = new Gson();
    InsuredNos insuredNos = new InsuredNos();

    //获取数据的数量
    @Override
    public int getItemCount() {
        return localModelNongxianList.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView toubao_num;
        public TextView toubao_name;
        public TextView isnot_lipei;
        public TextView toubao_date;
        public TextView cow_to_upload;
        public TextView toubao_upload;
        public TextView toubao_continue;
        public TextView toubao_idcard;
        public TextView deletedInsuredButon;


        public ViewHolder(View view) {
            super(view);
            toubao_num = (TextView) view.findViewById(R.id.toubao_num);
            toubao_name = (TextView) view.findViewById(R.id.toubao_name);
            toubao_date = (TextView) view.findViewById(R.id.toubao_date);
            toubao_idcard = (TextView) view.findViewById(R.id.toubao_idcard);
            isnot_lipei = (TextView) view.findViewById(R.id.isnot_lipei);
            cow_to_upload = (TextView) view.findViewById(R.id.cow_to_upload);
            toubao_upload = (TextView) view.findViewById(R.id.toubao_upload);
            toubao_continue = (TextView) view.findViewById(R.id.toubao_local_continue);
            deletedInsuredButon = view.findViewById(R.id.deleteInsured);
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

    /*
     * 设置接口回调更新数据
     * */
    public void setListner(ISExist isExist) {
        Log.i("setListner:", "localsetListner");
        this.isExist = isExist;
    }

    public class ToubaoTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;

        ToubaoTask(String url, TreeMap map) {
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


                if (!insurresp.ibaodanNoReal.equals("")) {
                    Toast.makeText(mContext, "保单已审核，不能继续录入", Toast.LENGTH_SHORT).show();
                } else {
                    GlobalConfigure.model = Model.BUILD.value();
                    Intent intent = new Intent();
                    intent.putExtra("ToubaoTempNumber", insurresp.ibaodanNo);
                    intent.setClass(mContext, DetectorActivity.class);
                    mContext.startActivity(intent);
                    collectNumberHandler.sendEmptyMessage(2);
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

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        //normalDialog.setIcon(R.drawable.icon_dialog);
        //      normalDialog.setTitle("我是一个普通Dialog")
        normalDialog.setIcon(R.drawable.cowface);
        normalDialog.setMessage("进入离线模式");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "进入离线模式", Toast.LENGTH_SHORT).show();
                        GlobalConfigure.model = Model.BUILD.value();
                        Intent intent = new Intent();
                        intent.putExtra("ToubaoTempNumber", insuredNo);
                        intent.setClass(mContext, DetectorActivity.class);
                        mContext.startActivity(intent);
                        collectNumberHandler.sendEmptyMessage(2);
                        HomeActivity mContext = (HomeActivity) ToubaoLocalAdapter.this.mContext;
                        mContext.finish();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.setCancelable(false);
        normalDialog.show();
    }

    private void offlineZipUpLoad(String file, String md5, String userId, String batchId) {
        UploadUtils.upload(file, md5, userId.toString(), batchId, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure:", "onFailure离线投保上传失败");
                mHandlerLocal.sendEmptyMessage(14);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("responseString:", responseString);
                offlineBuildResult = HttpUtils.processResp_new_detail_query(responseString);
                status = offlineBuildResult.status;
                Log.e("onResponse", response.toString());


                switch (status) {
                    case 1:
                        // 处理成功
                        Log.e("status == 1: ", file);
                        mHandlerLocal.sendEmptyMessage(1);
                        FileUtils.delFile(file);
                        break;
                    case 0:
                        // 处理失败
                        Log.e("status == 0: ", file);
                        mHandlerLocal.sendEmptyMessage(0);
//                        FileUtils.delFile(file);
                        break;
                    case -2:
                        // 处理失败
                        Log.e("status == -2: ", file);
                        mHandlerLocal.sendEmptyMessage(-2);
                        break;
                    case -4:
                        // 处理失败
                        Log.e("status == -4: ", file);
                        mHandlerLocal.sendEmptyMessage(-4);
                        break;
                }
            }
        });
    }

    // TODO: 2018/8/9 By:LuoLu
    @SuppressLint("HandlerLeak")
    private Handler mHandlerLocal = new Handler() {
        // 成功头数
        private int temp1 = 0;
        // 失败头数
        private int temp0 = 0;

        // 上传失败次数
        private int failCnt = 0;

        // 上传时显示用对话框
        ProgressDialog mProgressDialog = null;  //new ProgressDialog(mContext);

        @Override
        public void handleMessage(Message msg) {
            //  Auto-generated method stub
//            ProgressDialog mProgressDialog = null;
            int what = msg.what;
            switch (what) {
                case 1:
                    failCnt = 0;
                    temp1++;
                    mProgressDialog.setTitle(R.string.dialog_title);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("处理成功" + temp1 + "头\n失败" + temp0 + "头");
                    mProgressDialog.show();
                    mProgressDialog.closeOptionsMenu();
                    zipIndex++;
                    callZipUpload();
                    break;

                case 0:
                    failCnt = 0;
                    temp0++;
                    mProgressDialog.setTitle(R.string.dialog_title);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("处理成功" + temp1 + "头\n失败" + temp0 + "头");
                    mProgressDialog.show();
                    mProgressDialog.closeOptionsMenu();


                    zipIndex++;
                    callZipUpload();

                    break;
                case -2:

                    failCnt++;
                    if (failCnt > 3) {
                        zipIndex++;
                        failCnt = 0;
                    }
                    callZipUpload();
                    break;


                case 8:
                    // 点击上传按钮时
                    failCnt = 0;
                    mProgressDialog = new ProgressDialog(mContext);
                    mProgressDialog.setTitle(R.string.dialog_title);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("正在上传......");
                    mProgressDialog.show();
                    mProgressDialog.closeOptionsMenu();
                    break;

                case 9:
                    failCnt = 0;
                    mProgressDialog.dismiss();
                    Log.d("////", "zmzmmzm");
                    AlertDialog.Builder innerBuilder9 = new AlertDialog.Builder(mContext)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("上传成功" + temp1 + "头" + "\n失败" + temp0 + "头")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mContext.startActivity(new Intent(new Intent(mContext, HomeActivity.class)));
                                }
                            });
                    innerBuilder9.create();
                    innerBuilder9.setCancelable(false);
                    innerBuilder9.show();
                    break;

                case 14:
                case -4:
                    failCnt = 0;
                    mProgressDialog.dismiss();
                    AlertDialog.Builder innerBuilder14 = new AlertDialog.Builder(mContext)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("服务端异常！请稍后再试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    innerBuilder14.create();
                    innerBuilder14.setCancelable(false);
                    innerBuilder14.show();
                    break;

                case 100:
                    Toast.makeText(mContext, "没有需要上传的离线验标单！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    break;
            }
        }
    };


}
