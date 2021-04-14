package com.yfz.password;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import static android.view.View.GONE;

public class DisplayUtils {
    private static DisplayMetrics dm = new DisplayMetrics();
    private static  WindowManager windowManager;

    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / (context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }
    public static float dip2pxFloat(Context context, float dipValue) {
        return (float) (dipValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / ( context.getResources().getDisplayMetrics().scaledDensity)+0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * (context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }


    /**
     * 沉浸式全屏
     * @param activity
     */
    private void fullScreen(Activity activity){
        View vDecorView = activity.getWindow().getDecorView();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(params);
        }
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            vDecorView.setSystemUiVisibility(GONE);
        } else {
            vDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     * 获取屏幕缩放密度
     * @param context
     */
    public static float getScreenScale(Context context) {
        if(null !=context) {
            if (null == windowManager) {
                windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            }
            if (null != windowManager) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(outMetrics);
                float scaledDensity = outMetrics.scaledDensity;
                return scaledDensity;
            }
        }
        return 0;
    }
    /**
     * 获取屏幕密度
     * @param context
     */
    public static float getScreenDpi(Context context) {
        if(null !=context) {
            if (null == windowManager) {
                windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            }
            if (null != windowManager) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(outMetrics);
                float density = outMetrics.density;
                return density;
            }
        }
        return 0;
    }

    /**
     * 获取屏幕高度
     * @param context
     */
    public static int getScreenHeight(Context context) {
        if(null !=context) {
            if (null == windowManager) {
                windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            }
            if (null != windowManager) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(outMetrics);
                int widthPixels = outMetrics.widthPixels;
                int heightPixels = outMetrics.heightPixels;
                int densityDpi = outMetrics.densityDpi;
                float density = outMetrics.density;
                float scaledDensity = outMetrics.scaledDensity;
                //可用显示大小的绝对宽度（以像素为单位）。
                //可用显示大小的绝对高度（以像素为单位）。
                //屏幕密度表示为每英寸点数。
                //显示器的逻辑密度。
                //显示屏上显示的字体缩放系数。
                return heightPixels;
            }
        }
        return 0;
    }


    /**
     * 获取屏幕宽度
     * @param context
     */
    public static int getScreenWidth(Context context) {
        if(null !=context) {
            if (null == windowManager) {
                windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            }
            if (null != windowManager) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(outMetrics);
                int widthPixels = outMetrics.widthPixels;
                int heightPixels = outMetrics.heightPixels;
                int densityDpi = outMetrics.densityDpi;
                float density = outMetrics.density;
                float scaledDensity = outMetrics.scaledDensity;
                //可用显示大小的绝对宽度（以像素为单位）。
                //可用显示大小的绝对高度（以像素为单位）。
                //屏幕密度表示为每英寸点数。
                //显示器的逻辑密度。
                //显示屏上显示的字体缩放系数。
                return widthPixels;
            }
        }
        return 0;
    }
}
