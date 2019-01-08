package com.innovation.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.innovation.animalInsurance.R;

import java.io.File;


public class AppUpgradeService extends Service {
    private NotificationManager mNotificationManager = null;
    private Notification mNotification = null;
    private PendingIntent mPendingIntent = null;

    private String mDownloadUrl;
    private String mAppname;
    private File destDir = null;
    private File destFile = null;

    public static final String downloadPath = "/cowface";
    public static final int mNotificationId = 111;
    private static final int DOWNLOAD_FAIL = -1;
    private static final int DOWNLOAD_SUCCESS = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_LONG).show();
                    install(destFile);
                    mNotificationManager.cancel(mNotificationId);
                    break;
                case DOWNLOAD_FAIL:
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    mNotificationManager.cancel(mNotificationId);
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null){
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        mDownloadUrl = intent.getStringExtra("mDownloadUrl");
        mAppname = intent.getStringExtra("appname");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            destDir = new File(Environment.getExternalStorageDirectory().getPath() + downloadPath);
            if (destDir.exists()) {
                File destFile = new File(destDir.getPath() + "/" + mAppname+".apk");
                if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
                    install(destFile);
                    stopSelf();
                    return super.onStartCommand(intent, flags, startId);
                }
            }
        } else {
            return super.onStartCommand(intent, flags, startId);
        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();
        Intent completingIntent = new Intent();
        completingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        completingIntent.setClass(getApplicationContext(), AppUpgradeService.class);
        // 创建Notifcation对象，设置图标，提示文字,策略
        mPendingIntent = PendingIntent.getActivity(AppUpgradeService.this, R.string.app_name, completingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.icon = R.drawable.ic_launcher;
        mNotification.tickerText = "开始下载";
        mNotification.contentIntent = mPendingIntent;

        mNotification.contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
        mNotification.contentView.setProgressBar(R.id.app_upgrade_progressbar, 100, 0, false);
        mNotification.contentView.setTextViewText(R.id.download_notice_name_tv, "正在下载...");
        mNotification.contentView.setTextViewText(R.id.download_notice_speed_tv, "当前进度：0%");
        mNotificationManager.cancel(mNotificationId);
        mNotificationManager.notify(mNotificationId, mNotification);
        new AppUpgradeThread().start();
        return  super.onStartCommand(intent, flags, startId);

    }


    /**
     * 用于监听文件下载
     */
    private DownloadUtils.DownloadListener downloadListener = new DownloadUtils.DownloadListener() {
        @Override
        public void downloading(int progress) {
            System.out.println(progress);
            mNotification.contentView.setProgressBar(R.id.app_upgrade_progressbar, 100, progress, false);
            mNotification.contentView.setTextViewText(R.id.download_notice_speed_tv, "当前进度：" + progress + "%");
            mNotificationManager.notify(mNotificationId, mNotification);
        }

        @Override
        public void downloaded() {
            mNotification.contentView.setViewVisibility(R.id.app_upgrade_progressbar, View.GONE);
            mNotification.defaults = Notification.DEFAULT_SOUND;
            mNotification.contentIntent = mPendingIntent;
            mNotification.contentView.setTextViewText(R.id.download_notice_name_tv, "下载完成");
            mNotificationManager.notify(mNotificationId, mNotification);
            if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_SUCCESS;
                mHandler.sendMessage(msg);
            }
            mNotificationManager.cancel(mNotificationId);
        }
    };

    /**
     * 用于文件下载线程
     * @author yingjun10627
     *
     */
    class AppUpgradeThread extends Thread {
        @Override
        public void run() {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (destDir == null) {
                    destDir = new File(Environment.getExternalStorageDirectory().getPath() + downloadPath);
                }
                if (destDir.exists() || destDir.mkdirs()) {
                    destFile = new File(destDir.getPath() + "/" + mAppname+".apk");
                    if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
                        install(destFile);
                    } else {
                        try {
                            DownloadUtils.download(mDownloadUrl, destFile, false, downloadListener);
                        } catch (Exception e) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = DOWNLOAD_FAIL;
                            mHandler.sendMessage(msg);
                            e.printStackTrace();
                        }
                    }
                }
            }
            stopSelf();
        }
    }

    /**
     * apk文件安装
     *
     * @param apkFile
     */
    public void install(File apkFile) {
//        Uri uri = Uri.fromFile(apkFile);
        if(Build.VERSION.SDK_INT>=24) {
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        }
        else {
            Uri uri = Uri.fromFile(apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        }

    }

    /**
     * 判断文件是否完整
     *
     * @param apkFilePath
     * @return
     */
    public boolean checkApkFile(String apkFilePath) {
        boolean result = false;
        try {
            PackageManager pManager = getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pInfo == null) {
                result = false;
            } else {
                result = true;
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
