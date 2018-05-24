package pers.husen.highdsa.activity;

import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pers.husen.highdsa.CNiaoApplication;
import pers.husen.highdsa.R;
import pers.husen.highdsa.bean.User;
import pers.husen.highdsa.constants.HttpConstants;
import pers.husen.highdsa.msg.CustUserInfo;
import pers.husen.highdsa.msg.ResponseJson;
import pers.husen.highdsa.utils.LogUtil;
import pers.husen.highdsa.utils.OkHttp3Utils;
import pers.husen.highdsa.utils.ToastUtils;

public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.Image)
    CircleImageView mImage;
    @BindView(R.id.TextUsername)
    TextView mUsername;
    @BindView(R.id.TextSex)
    TextView mSex;
    @BindView(R.id.TextAddress)
    TextView mAddress;
    @BindView(R.id.TextAge)
    TextView mAge;
    @BindView(R.id.TextBirthday)
    TextView mBirthday;

    CustUserInfo userInfo;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void init() {
        initToolBar();
    }

    private void initToolBar() {
        getUserInfo();
    }

    class UserInfoThread extends Thread {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (userInfo != null) {
                        mUsername.setText(userInfo.getUserNickName());
                        mSex.setText((userInfo.getUserSex()) ? "男" : "女");
                        mAddress.setText(userInfo.getUserAddress());
                        mAge.setText(userInfo.getUserAge());
                        mBirthday.setText(userInfo.getUserBirthday());
                    }
                }
            });
        }
    }

    private void getUserInfo() {
        final User user = CNiaoApplication.getInstance().getUser();
        if (user == null) {
            ToastUtils.safeToastShow(UserInfoActivity.this, "用户未登录");
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("phone_number", user.getPhoneNumber());

            ToastUtils.showDebugSafeToast(UserInfoActivity.this, "开始获取用户" + user.getPhoneNumber() + "的用户信息");

            final PersistentCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

            String url = OkHttp3Utils.appendParams(HttpConstants.URL_USER_INFO, params);
            LogUtil.e("url", url);
            final Request request = new Request.Builder().get().url(url).build();

            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    ToastUtils.showDebugSafeToast(UserInfoActivity.this, "用户信息：" + string);
                    LogUtil.e("用户信息", string);

                    ObjectMapper objectMapper = new ObjectMapper();
                    ResponseJson responseJson = objectMapper.readValue(string, ResponseJson.class);

                    if (responseJson.getSuccess()) {
                        ToastUtils.showDebugSafeToast(UserInfoActivity.this, "用户信息获取成功");
                        userInfo = objectMapper.readValue(objectMapper.writeValueAsString(responseJson.getMessage()), CustUserInfo.class);

                        ToastUtils.showDebugSafeToast(UserInfoActivity.this, "用户信息：" + userInfo.toString());

                        //在UI线程中更新界面
                        new UserInfoThread().start();
                    } else {
                        ToastUtils.safeToastShow(UserInfoActivity.this, "用户信息有误：" + responseJson.getMessage());
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}