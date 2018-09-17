package com.example.administrator.suoji;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengwei on 2018/9/17.
 * 权限管理工具类
 */

public class ZwPermissions{
    /**权限数组，将所有所需要的权限放在这里**/
    private  String[] permissions;
    /**用来判断是否有权限未授予权限**/
    private  List<String> mPermissionList = new ArrayList<>();
    /**提示框**/
    private  AlertDialog mPermissionDialog;
    /**权限管理界面**/
    private  String mPackName;
    private  Activity activity;
    private ZwPermissionsListener zwPermissionsListener;

    /**
     *
     * @param activity
     * @param permissions   权限数组
     * @param mPackName    包名
     * @param zwPermissionsListener   回调方法
     */
        public ZwPermissions(Activity activity,String[] permissions,String mPackName,ZwPermissionsListener zwPermissionsListener){
        this.activity=activity;
        this.permissions=permissions;
        this.zwPermissionsListener=zwPermissionsListener;
        this.mPackName=mPackName;
    }

    /**权限判断和申请**/
    public void initPermission() {
        /**清空没有通过的权限**/
        mPermissionList.clear();
        /**逐个判断你要的权限是否已经通过**/
        for (int i = 0; i < permissions.length; i++) {
            /** 这一个方法主要是主要用于检测某个权限是否已经被授予，
             * 方法返回值为PackageManager.PERMISSION_DENIED或者PackageManager.PERMISSION_GRANTED。
             * 当返回DENIED就需要进行申请授权了。
             **/
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                /**添加还未授予的权限**/
                mPermissionList.add(permissions[i]);
            }
        }
        /**申请权限**/
        if (mPermissionList.size() > 0) {
            /**
             *有权限没有通过，需要申请*
             *该方法是异步的，第一个参数是Context；第二个参数是需要申请的权限的字符串数组；
             *第三个参数为requestCode，主要用于回调的时候检测。
             *可以从方法名requestPermissions以及第二个参数看出，是支持一次性申请多个权限的，系统会通过对话框逐一询问用户是否授权。
             **/
            ActivityCompat.requestPermissions(activity, permissions, 100);
        }else{
            /**说明权限都已经通过，可以做你想做的事情去**/
            zwPermissionsListener.ok();
        }
    }

    public  void ZwRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (100 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                //打开提示授予权限对话框，跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                showPermissionDialog();
            }else{
                //全部权限通过，可以进行下一步操作。。。
                zwPermissionsListener.ok();
            }
        }
    }
    private  void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(activity).setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /**这里是跳转到应用的权限授予页面**/
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            activity.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作，直接关闭可能太流氓了，可以判断一下未授予的权限是不是一定得要然后在做操作
                            cancelPermissionDialog();
                           zwPermissionsListener.no();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }
    //关闭对话框
    private  void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }
}
