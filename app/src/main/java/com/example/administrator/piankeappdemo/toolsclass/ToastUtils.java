package com.example.administrator.piankeappdemo.toolsclass;

import android.widget.Toast;

import com.example.administrator.piankeappdemo.baseclass.AppApplication;


/**
 * Created by 董梦娇 on 2015/10/12.
 */
public class ToastUtils {
    public static void showToast(String str){
        Toast.makeText(AppApplication.getApplication(),str,Toast.LENGTH_LONG).show();

    }
}
