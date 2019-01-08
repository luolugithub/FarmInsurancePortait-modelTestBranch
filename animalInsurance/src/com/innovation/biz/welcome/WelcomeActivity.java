package com.innovation.biz.welcome;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.Toast;

import com.innovation.animalInsurance.R;
import com.innovation.biz.login.LoginActivity;
import com.innovation.utils.HttpUtils;


public class WelcomeActivity extends Activity {
    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_VIDEO = Manifest.permission.RECORD_AUDIO;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_PHONE = Manifest.permission.READ_PHONE_STATE;
    private static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    private static final int REQUEST_READ_CONTACTS = 0;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        HttpUtils.DATABSAE_VERSION = 6;
//        ImageView imageView = (ImageView) findViewById(R.id.logo_id);
//        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.START_ON_FIRST_FRAME, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.RELATIVE_TO_PARENT);
//        anim.setDuration(700);

// Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.logo_id);
//        splash.startAnimation(anim);

// Later.. stop the animation


//        requestPermission();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hasPermission()) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
//        splash.setAnimation(null);
                } else {
                    requestPermission();
                }
                finish();
            }
        }, 3000);


        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }


    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED
                        ) {
                } else {
                    requestPermission();
                }
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(PERMISSION_VIDEO) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(PERMISSION_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(PERMISSION_PHONE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(PERMISSION_READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) || shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(WelcomeActivity.this, "Camera AND storage permission are required for this APP", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{PERMISSION_CAMERA, PERMISSION_VIDEO, PERMISSION_STORAGE, PERMISSION_PHONE,
                    PERMISSION_LOCATION, PERMISSION_READ_CONTACTS, PERMISSION_INTERNET}, PERMISSIONS_REQUEST);
        }
    }
}
