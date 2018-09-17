package com.example.administrator.suoji;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ZwPermissions zwPermissions;
    private  String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zwPermissions=new ZwPermissions(this, permissions,getPackageName(), new ZwPermissionsListener() {
            @Override
            public void ok() {
                Toast.makeText(MainActivity.this, "权限获取成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void no() {
                finish();
            }
        });
        zwPermissions.initPermission();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        zwPermissions.ZwRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
