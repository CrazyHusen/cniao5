package pers.husen.highdsa.utils;

import android.util.Log;

/**
 * Description 日志的工具类,通过变量统一管理日志
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/14 23:00
 * <p>
 * Version 1.0.0
 */
public class LogUtil {
    private static boolean bossLog = true;

    public static void d(String TAG, String msg) {
        if (bossLog)
            Log.d(TAG, msg);
    }

    public static void d(String TAG, String msg, boolean isLog) {
        if (bossLog && isLog)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (bossLog)
            Log.e(TAG, msg);
    }

    public static void e(String TAG, String msg, boolean isLog) {
        if (bossLog && isLog)
            Log.e(TAG, msg);
    }
}