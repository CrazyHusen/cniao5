package pers.husen.highdsa.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pers.husen.highdsa.CNiaoApplication;
import pers.husen.highdsa.constants.DebugShowConstants;

/**
 * Description Toast的工具类
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/16 01:30
 * <p>
 * Version 1.0.0
 */
public class ToastUtils {
    private static Toast mToast;

    public static void showDebugSafeToast(final Context context, final String text) {
        if(DebugShowConstants.isToastShow){
            safeToastShow(context, text);
        }
    }

    /**
     * 安全弹出Toast。处理线程的问题。
     */
    public static void safeToastShow(final Context context, final String text) {
        //如果不是在主线程弹出吐司，那么抛到主线程弹
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (context == null) {
                                uiToastShow(CNiaoApplication.sContext, text);
                            } else {
                                uiToastShow(context, text);
                            }
                        }
                    }
            );
        } else {
            if (context == null) {
                uiToastShow(CNiaoApplication.sContext, text);
            } else {
                uiToastShow(context, text);
            }
        }
    }

    public static void showDebugUiToast(final Context context, final String text) {
        if(DebugShowConstants.isToastShow){
            uiToastShow(context, text);
        }
    }

    /**
     * 弹出Toast，处理单例的问题。----如果是在主线程,可以用这个,子线程就不要用这个,不安全
     */
    public static void uiToastShow(Context context, String text) {
        if (context == null) {
            context = CNiaoApplication.sContext;
        }

        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }

        if (text.length() < 10) {
            mToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_LONG);
        }

        View view = mToast.getView();
        view.setPadding(28, 12, 28, 12);
        TextView tv = view.findViewById(android.R.id.message);
        tv.setTextSize(16);
        tv.setTextColor(Color.WHITE);
        mToast.setText(text);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(view);
        mToast.show();
    }
}