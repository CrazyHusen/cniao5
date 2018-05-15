package pers.husen.highdsa.utils;

import android.util.Log;

import pers.husen.highdsa.constants.DebugShowConstants;

/**
 * Description 日志的工具类,通过变量统一管理日志
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/14 23:00
 * <p>
 * Version 1.0.1
 */
public class LogUtil {
    public static void d(String TAG, String msg) {
        if (DebugShowConstants.isLogShow)
            Log.d(TAG, msg);
    }

    public static void d(String TAG, String msg, boolean isLog) {
        if (DebugShowConstants.isLogShow && isLog)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (DebugShowConstants.isLogShow)
            Log.e(TAG, msg);
    }

    public static void e(String TAG, String msg, boolean isLog) {
        if (DebugShowConstants.isLogShow && isLog)
            Log.e(TAG, msg);
    }
}