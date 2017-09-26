package com.pengge.musicplayer.tools;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by pengge on 16/9/7.
 */
public class Displaymetrics {
    private static Displaymetrics instance = null;
    public  static Displaymetrics getInstance() {
        if (instance == null) {
            instance    = new Displaymetrics();
        }

        return instance;
    }
    //获取运行屏幕宽度
    public int getScreenWidth(WindowManager windowManager){
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        //宽度 dm.widthPixels
        //高度 dm.heightPixels
        Log.d("widthPixels", String.valueOf(dm.widthPixels));
        return  dm.widthPixels;
    }
    //获取运行屏幕宽度
    public int getScreenHeight(WindowManager windowManager){
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        //宽度 dm.widthPixels
        //高度 dm.heightPixels
        Log.d("widthPixels", String.valueOf(dm.widthPixels));
        return  dm.heightPixels;
    }
    //DP转PX
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //PX转DP
    private static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
