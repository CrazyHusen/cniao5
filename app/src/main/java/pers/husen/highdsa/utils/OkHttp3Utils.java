package pers.husen.highdsa.utils;

import android.net.Uri;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Description http请求工具类
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/22 02:26
 * <p>
 * Version 1.0.0
 */
public class OkHttp3Utils {
    public static String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }
}