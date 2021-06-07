package com.zhengsr.zlog;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
    }

    public void zipLog(View view) {
        String netLog = "/sdcard/tem.txt";
        ZLog.with(this)
                .logPaths(Arrays.asList(netLog))
                .zipPath("/sdcard/")
                .zipName("传屏No.1.zip")
                .isHasSystemPermission(true)
                .listener(new ZLogListener() {
                    @Override
                    void onStart() {
                        super.onStart();
                        Log.d(TAG, "zsr onStart: ");
                    }

                    @Override
                    void onSuccess(String path) {
                        Log.d(TAG, "zsr onSuccess: "+path);
                    }

                    @Override
                    void onFail(ZipError errorCode, String errorMsg) {
                        Log.d(TAG, "zsr onFail: "+errorCode+" "+errorMsg);
                    }
                }).zip();
    }
}