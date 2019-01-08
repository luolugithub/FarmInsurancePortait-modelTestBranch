package com.innovation.biz.Insured;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.BuildConfig;
import com.innovation.animalInsurance.R;
import com.innovation.base.InnApplication;
import com.innovation.bean.InsureAddAnimalBean;
import com.innovation.bean.ResultBean;
import com.innovation.bean.UploadImageObject;
import com.innovation.base.Model;
import com.innovation.location.LocationManager;
import com.innovation.login.Utils;
import com.innovation.login.view.HomeActivity;
import com.innovation.utils.FileUtils;
import com.innovation.utils.HttpRespObject;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.ZipUtil;

import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.innovation.base.InnApplication.getStringTouboaExtra;
import static org.tensorflow.demo.CameraConnectionFragment.collectNumberHandler;

/**
 * Author by luolu, Date on 2018/9/27.
 * COMPANY：InnovationAI
 */

public class AddAnimalActivity extends AppCompatActivity {

    private static String TAG = "AddAnimalActivity";
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.etAnimalAge)
    EditText etAnimalAge;
    @BindView(R.id.btnPersonAndAnimal)
    ImageView btnPersonAndAnimal;
    @BindView(R.id.tvPersonAndAnimalpath)
    TextView tvPersonAndAnimalpath;
    @BindView(R.id.etAnimalEarsTagNo)
    EditText etAnimalEarsTagNo;
    @BindView(R.id.quSpinner)
    Spinner quSpinner;
    @BindView(R.id.sheSpinner)
    Spinner sheSpinner;
    @BindView(R.id.lanSpinner)
    Spinner lanSpinner;
    @BindView(R.id.btnCommit)
    Button btnCommit;
    @BindView(R.id.addAmimalLinearLayout)
    LinearLayout addAmimalLinearLayout;
    private Logger mLogger = new Logger(AddAnimalActivity.class.getSimpleName());
    private String stringTouboaExtra;
    public static String addAnimalLibID;
    private AddAnimalTask addAnimalTask;
    private String errStr;
    private int userid;
    private static final int DECIMAL_DIGITS = 1;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记

    private static String IMAGE_FILE_NAME = "";// 头像文件名称
    private String urlpath;
    private Uri uritempFile;

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 10000;
    private static long lastClickTime;
    private Gson gson;
    private String responseAddAnimal;
    private ResultBean resultBeanAddAnimal;
    private String addressAddAnimal;
    private double currentLat;
    private double currentLon;
    private String username;
    private UploadImageObject imgResp;
    private UploadImageObject uploadImageObject;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);
        ButterKnife.bind(this);
        LocationManager locationManager = LocationManager.getInstance(this);
        locationManager.startLocation();
        locationManager.setAddress(new LocationManager.GetAddress() {
            @Override
            public void getaddress(String address) {
                addressAddAnimal = address;
            }
        });
        currentLat = locationManager.currentLat;
        currentLon = locationManager.currentLon;
        //读取用户信息
        SharedPreferences pref_user = AddAnimalActivity.this.getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        username = pref_user.getString("fullname", "");

        tvTitle.setText("资料采集");
        parentView = getWindow().getDecorView();
        SharedPreferences pref = getSharedPreferences(Utils.USERINFO_SHAREFILE, Context.MODE_PRIVATE);
        userid = pref.getInt("uid", 0);
        stringTouboaExtra = getIntent().getStringExtra("ToubaoTempNumber");
        setPoint(etAnimalAge);
        //选择图片
        pop = new PopupWindow(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        IMAGE_FILE_NAME = stampToDate(System.currentTimeMillis()) + ".jpg";
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", tempFile));
                startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                ;
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ll_popup.clearAnimation();
            }
        });
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.area));
        ArrayAdapter array2Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.area2));
        ArrayAdapter array3Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.area3));
        quSpinner.setAdapter(arrayAdapter);
        sheSpinner.setAdapter(array2Adapter);
        lanSpinner.setAdapter(array3Adapter);
        quSpinner.setSelection(0, true);
        sheSpinner.setSelection(0, true);
        lanSpinner.setSelection(0, true);
        gson = new Gson();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                crop(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    try {
                        setPicToView();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     */
    public void crop(String imagePath) {
        File file = new File(imagePath);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(new File(imagePath)), "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 9998);
        intent.putExtra("aspectY", 9999);
       /* if (android.os.Build.MODEL.contains("HUAWEI")) {//华为特殊处理 不然会显示圆

        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }*/
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
//        intent.putExtra("return-data", true);

        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    String zipFilePaht = "";

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView() throws Exception {
        // 取得SDCard图片路径做显示
        Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
        Drawable drawable = new BitmapDrawable(null, photo);
        urlpath = FileUtils.saveFile(getApplicationContext(), "temphead.jpg", photo);
        btnPersonAndAnimal.setImageDrawable(drawable);
        File file_zip = new File(urlpath);
        if (InnApplication.isOfflineMode) {
            // 将合影图片存入temp文件夹
            FileUtils.moveFile(urlpath, Environment.getExternalStorageDirectory().getPath() + InnApplication.OFFLINE_TEMP_PATH + "heyingPic.jpg");
        } else {
            uploadImageObject = upload_zipImage(file_zip, userid);
            int status;
            if (uploadImageObject == null || uploadImageObject.status != HttpRespObject.STATUS_OK) {
                status = uploadImageObject == null ? -1 : uploadImageObject.status;
                return;
            }
        }

    }

    private UploadImageObject upload_zipImage(File zipFile_image, int uid) {
        UploadImageObject imgResp = HttpUtils.uploadImage(zipFile_image, uid);
        if (imgResp == null || imgResp.status != HttpRespObject.STATUS_OK) {
            int status = imgResp == null ? -1 : imgResp.status;
            Toast.makeText(getApplicationContext(), imgResp.msg, Toast.LENGTH_SHORT).show();
            return imgResp;
        }
        tvPersonAndAnimalpath.setText(imgResp.upload_imagePath);
        return imgResp;
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    /**
     * 将map转为json
     *
     * @param map
     * @param sb
     * @return
     */
    public static StringBuilder mapToJson(Map<?, ?> map, StringBuilder sb) {
        if (sb == null) sb = new StringBuilder();
        sb.append("{");
        Iterator<?> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
            String key = entry.getKey() != null ? entry.getKey().toString() : "";
            sb.append("\"" + stringToJson(key) + "\":");
            Object o = entry.getValue();
            if (o instanceof List<?>) {
                List<?> l = (List<?>) o;
                listToJson(l, sb);
            } else if (o instanceof Map<?, ?>) {
                Map<?, ?> m = (Map<?, ?>) o;
                mapToJson(m, sb);
            } else {
                String val = entry.getValue() != null ? entry.getValue().toString() : "";
                sb.append("\"" + stringToJson(val) + "\"");
            }
            if (iter.hasNext()) sb.append(",");
        }
        sb.append("}");
        return sb;
    }

    public static StringBuilder listToJson(List<?> lists, StringBuilder sb) {
        if (sb == null) sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < lists.size(); i++) {
            Object o = lists.get(i);
            if (o instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) o;
                mapToJson(map, sb);
            } else if (o instanceof List<?>) {
                List<?> l = (List<?>) o;
                listToJson(l, sb);
            } else {
                sb.append("\"" + o + "\"");
            }
            if (i != lists.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb;
    }

    /**
     * 将字符串转为json数据
     *
     * @param str 数据字符串
     * @return json字符串
     */
    private static String stringToJson(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    @OnClick({R.id.btnPersonAndAnimal, R.id.btnCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPersonAndAnimal:
                ll_popup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.activity_translate_in));
                pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.btnCommit:
                if (SystemClock.elapsedRealtime() - lastClickTime < MIN_CLICK_DELAY_TIME) {
                    Toast.makeText(AddAnimalActivity.this, "正在处理，请勿连续多次点击！", Toast.LENGTH_SHORT).show();
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                String stringPersonAndAnimalpath = tvPersonAndAnimalpath.getText().toString();
                mLogger.i("保单号：" + getStringTouboaExtra);
                mLogger.i("libId：" + addAnimalLibID);
                if (etAnimalAge.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入畜龄", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getStringTouboaExtra.equals("")) {
                    Toast.makeText(getApplicationContext(), "保单号获取失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (addAnimalLibID.equals("")) {
//                    Toast.makeText(getApplicationContext(), "libId获取异常！", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (InnApplication.isOfflineMode) {
                    TreeMap treeMapOfflineAddAnimal = new TreeMap<String, String>();
                    treeMapOfflineAddAnimal.put("baodanNo", getStringTouboaExtra);
                    treeMapOfflineAddAnimal.put("xuling", etAnimalAge.getText().toString().trim());
                    treeMapOfflineAddAnimal.put("pigNo", etAnimalEarsTagNo.getText().toString().trim());
                    treeMapOfflineAddAnimal.put("address", addressAddAnimal + "");
                    treeMapOfflineAddAnimal.put("person", username);

                    treeMapOfflineAddAnimal.put("level1", quSpinner.getSelectedItem().toString());
                    treeMapOfflineAddAnimal.put("level2", sheSpinner.getSelectedItem().toString());
                    treeMapOfflineAddAnimal.put("level3", lanSpinner.getSelectedItem().toString());
                    treeMapOfflineAddAnimal.put("level4", "");
                    treeMapOfflineAddAnimal.put("level5", "");
                    treeMapOfflineAddAnimal.put("type", Model.BUILD.value());
                    treeMapOfflineAddAnimal.put("toubaoType", "");
                    treeMapOfflineAddAnimal.put("pigType", "");

                    treeMapOfflineAddAnimal.put("longitude", currentLon + "");
                    treeMapOfflineAddAnimal.put("latitude", currentLat + "");

                    StringBuilder builder = new StringBuilder();
                    StringBuilder stringBuilder = mapToJson(treeMapOfflineAddAnimal, builder);
                    String offLineTempFile = Environment.getExternalStorageDirectory().getPath() + InnApplication.OFFLINE_TEMP_PATH;
                    File newDir = new File(offLineTempFile);
                    if (!newDir.exists()) {
                        newDir.mkdirs();
                    }
                    String jsonFileName = newDir.getAbsolutePath() + "/info.json";
                    File jsonFile = new File(jsonFileName);
                    try {
                        jsonFile.createNewFile();
                        /* 保存人畜合影   */
                        if (!stringPersonAndAnimalpath.trim().equals("")) {
                            String picFileName = newDir.getAbsolutePath() + "/heyingPic.jpg";
                            File picFile = new File(picFileName);
                            picFile.createNewFile();
                            //移动文件
                            FileUtils.moveFile(offLineTempFile, picFileName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //文件生成
                    String jsonString = stringBuilder.toString();
                    com.innovation.tuil2.FileUtils.writeDatesToSDCard(jsonFile, jsonString);
                    // TODO: 2018/8/9 压缩大文件
                    String uploadZipFilePathStr = Environment.getExternalStorageDirectory().getPath() + InnApplication.OFFLINE_PATH + InnApplication.offLineInsuredNo + "/";
                    File uploadZipFilePath = new File(uploadZipFilePathStr);
                    if (!uploadZipFilePath.exists()) {
                        uploadZipFilePath.mkdirs();
                    }
                    mLogger.i("离线压缩后的文件路径：" + uploadZipFilePathStr);
                    String uuidFileName = uploadZipFilePathStr + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
                    File insuredNoFile = new File(offLineTempFile);
                    File[] files = insuredNoFile.listFiles();
                    boolean zipResultOK = ZipUtil.zipFiles(files, new File(uuidFileName));
                    String resultStr;
                    if (zipResultOK) {
                        resultStr = "离线验标成功";
                    } else {
                        resultStr = "离线验标失败，请再试";
                    }
                    mProgressHandler.sendEmptyMessage(44);
                    AlertDialog.Builder innerBuilder = new AlertDialog.Builder(AddAnimalActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultStr)
                            .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(AddAnimalActivity.this, HomeActivity.class);
                                    intent.putExtra("ToubaoTempNumber", InnApplication.offLineInsuredNo);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("继续录入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(AddAnimalActivity.this, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", InnApplication.offLineInsuredNo);
                                    startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                    finish();
                                }
                            });
                    innerBuilder.create();
                    innerBuilder.setCancelable(false);
                    innerBuilder.show();
                } else {

                    mProgressHandler.sendEmptyMessage(51);
                    TreeMap treeMapAddAnimal = new TreeMap<String, String>();
                    treeMapAddAnimal.put("baodanNo", getStringTouboaExtra);
                    treeMapAddAnimal.put("xuling", etAnimalAge.getText().toString().trim());
                    treeMapAddAnimal.put("libId", addAnimalLibID);
                    treeMapAddAnimal.put("pigNo", etAnimalEarsTagNo.getText().toString().trim());
                    treeMapAddAnimal.put("address", addressAddAnimal + "");
                    treeMapAddAnimal.put("longitude", currentLon + "");
                    treeMapAddAnimal.put("latitude", currentLat + "");
                    treeMapAddAnimal.put("yiji", quSpinner.getSelectedItem().toString());
                    treeMapAddAnimal.put("erji", sheSpinner.getSelectedItem().toString());
                    treeMapAddAnimal.put("sanji", lanSpinner.getSelectedItem().toString());
                    treeMapAddAnimal.put("siji", "");
                    if (null != uploadImageObject &&! "".equals(uploadImageObject.upload_imagePath)) {
                        Log.i("imagepath", uploadImageObject.upload_imagePath);
                        treeMapAddAnimal.put("heyingPic", uploadImageObject.upload_imagePath);
                    }
                    addAnimalTask = new AddAnimalTask(HttpUtils.ADD_ANIMAL, treeMapAddAnimal);
                    addAnimalTask.execute((Void) null);
                }
                break;
        }
    }

    public class AddAnimalTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final TreeMap<String, String> mQueryMap;

        AddAnimalTask(String url, TreeMap map) {
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
                responseAddAnimal = HttpUtils.post(mUrl, formBody);
                if (null != responseAddAnimal) {
                    Log.d(TAG, mUrl + ":\nresponseAddAnimal:" + responseAddAnimal);
                    resultBeanAddAnimal = gson.fromJson(responseAddAnimal, ResultBean.class);
                    if (resultBeanAddAnimal.getStatus() == 1) {
                        addAnimalResultHandler.sendEmptyMessage(18);
                    } else if (resultBeanAddAnimal.getStatus() == 0) {
                        addAnimalResultHandler.sendEmptyMessage(22);
                    } else {
                        addAnimalResultHandler.sendEmptyMessage(24);
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                errStr = "服务器错误！";
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            addAnimalTask = null;
            if (success & HttpUtils.INSUR_ADDPIG_URL.equalsIgnoreCase(mUrl)) {

            } else if (!success) {
                //  显示失败
                Log.d(TAG, errStr);
                Toast.makeText(getApplicationContext(), "onPostExecute:" + errStr, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            addAnimalTask = null;
        }
    }

    public static void setPoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public String stampToDate(long timeMillis) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    @SuppressLint("HandlerLeak")
    private Handler addAnimalResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (msg.what) {
                case 18:

                    InsureAddAnimalBean insureAddAnimalBean = gson.fromJson(responseAddAnimal, InsureAddAnimalBean.class);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddAnimalActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(insureAddAnimalBean.getMsg())
                            .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();

                                }
                            })
                            .setNegativeButton("继续录入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(AddAnimalActivity.this, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", stringTouboaExtra.trim());
                                    startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                    finish();
                                }
                            });
                    builder.create();
                    builder.setCancelable(false);
                    builder.show();

                    break;
                case 19:

                    break;
                case 22:
                    AlertDialog.Builder builder22 = new AlertDialog.Builder(AddAnimalActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultBeanAddAnimal.getMsg())
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(AddAnimalActivity.this, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", getStringTouboaExtra);
                                    startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                    finish();
                                }
                            });
                    builder22.setCancelable(false);
                    builder22.show();
                    break;

                case 24:
                    AlertDialog.Builder builder24 = new AlertDialog.Builder(AddAnimalActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage(resultBeanAddAnimal.getMsg())
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();

                                }
                            })
                            .setNegativeButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(AddAnimalActivity.this, DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", stringTouboaExtra.trim());
                                    startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                    finish();
                                }
                            });
                    builder24.create();
                    builder24.setCancelable(false);
                    builder24.show();
                    break;

                case 37:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        addAnimalResultHandler.removeMessages(18);
        mProgressHandler.removeMessages(51);
        mProgressHandler.removeMessages(61);
        mProgressHandler.removeMessages(41);
        mProgressHandler.removeMessages(44);

        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
        super.onDestroy();

    }

    private ProgressDialog mProgressDialog = null;
    @SuppressLint("HandlerLeak")
    private Handler mProgressHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 51:
                    mProgressDialog = new ProgressDialog(AddAnimalActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("正在处理......");
                    mProgressDialog.show();
                    break;
                case 61:
                    mProgressDialog = new ProgressDialog(AddAnimalActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setIcon(R.drawable.cowface);
                    mProgressDialog.setMessage("处理成功......");
                    mProgressDialog.show();
                case 41:
                    mProgressDialog.dismiss();
                    AlertDialog.Builder tcaBuilder = new AlertDialog.Builder(AddAnimalActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("网络异常！请重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", stringTouboaExtra.trim());
                                    startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                    finish();
                                }
                            });
                    tcaBuilder.create();
                    tcaBuilder.setCancelable(false);
                    tcaBuilder.show();
                    break;
                case 44:
                    if (null != mProgressDialog) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }

                    break;
                case 24:
                    if (null != mProgressDialog) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddAnimalActivity.this)
                            .setIcon(R.drawable.cowface)
                            .setTitle("提示")
                            .setMessage("网络超时，请稍后再试！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("重新投保", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                                    intent.putExtra("ToubaoTempNumber", stringTouboaExtra.trim());
                                    startActivity(intent);
                                    collectNumberHandler.sendEmptyMessage(2);
                                    finish();
                                }
                            });
                    builder.create();
                    builder.setCancelable(false);
                    builder.show();
                    break;
                default:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
            }
        }
    };
}
