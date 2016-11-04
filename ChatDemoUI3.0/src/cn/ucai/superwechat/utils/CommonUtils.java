package cn.ucai.superwechat.utils;

import android.widget.Toast;

import cn.ucai.superwechat.DemoApplication;

public class CommonUtils {
    public static void showLongToast(String msg){
        Toast.makeText(DemoApplication.getInstance(),msg,Toast.LENGTH_LONG).show();
    }
    public static void showShortToast(String msg){
        Toast.makeText(DemoApplication.getInstance(),msg,Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(int rId){
        showLongToast(DemoApplication.getInstance().getString(rId));
    }
    public static void showShortToast(int rId){
        showShortToast(DemoApplication.getInstance().getString(rId));
    }
}
