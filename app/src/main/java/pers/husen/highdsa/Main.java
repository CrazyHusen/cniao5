package pers.husen.highdsa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import pers.husen.highdsa.utils.DESUtil;

/**
 * Description 测试专用类
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/13 00:32
 * <p>
 * Version 1.0.0
 */
public class Main {
    public void okHttpTest() throws JsonProcessingException {
        String phone = "18626422426";
        String url = "http://localhost:8081/highdsa-restful-test/test/v1/go";

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);

        // 修改登录的请求地址
        OkHttpUtils.post().url(url).params(params).build().execute(new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                String string = response.body().string();

                //使用jackson
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> map = objectMapper.readValue(string, Map.class);

                System.out.println("测试结果" + map.toString());

                return "";
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
    }

    public static void main(String[] args) {
        System.out.println("OK");
       /* try {
            new Main().okHttpTest();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        String result = DESUtil.encode("IwL1EOba", "highdsa");
        System.out.println(result);
    }
}