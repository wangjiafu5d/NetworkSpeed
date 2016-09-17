package com.test.chuan.networkspeed;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by chuan on 2016/9/16 0016.
 */
public class MyApplication extends Application{
    public static int[] getScreenHW(Context context) {
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int[] HW = new int[] { width, height };
//        Toast.makeText(context, "当前手机的屏幕宽高：" + dm.widthPixels + "*" +
//                dm.heightPixels, Toast.LENGTH_SHORT).show();
        return HW;
    }
}
