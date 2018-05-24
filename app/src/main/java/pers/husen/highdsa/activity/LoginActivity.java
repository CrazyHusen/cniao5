package pers.husen.highdsa.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pers.husen.highdsa.CNiaoApplication;
import pers.husen.highdsa.R;
import pers.husen.highdsa.bean.User;
import pers.husen.highdsa.constants.HttpConstants;
import pers.husen.highdsa.msg.LoginRespMsg;
import pers.husen.highdsa.msg.ResponseJson;
import pers.husen.highdsa.utils.ToastUtils;
import pers.husen.highdsa.widget.CNiaoToolBar;
import pers.husen.highdsa.widget.ClearEditText;

/**
 * Description 登录界面
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/13 20:11
 * <p>
 * Version 1.0.1
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    CNiaoToolBar mToolBar;
    @BindView(R.id.etxt_phone)
    ClearEditText mEtxtPhone;
    @BindView(R.id.etxt_pwd)
    ClearEditText mEtxtPwd;
    @BindView(R.id.txt_toReg)
    TextView mTxtToReg;

    @Override
    protected void init() {
        initToolBar();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_login;
    }

    private void initToolBar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });
    }

    @OnClick({R.id.btn_login, R.id.txt_toReg, R.id.txt_forget})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //登录
                login();
                break;
            case R.id.txt_toReg:
                //注册
                Intent intent = new Intent(this, RegActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_forget:
                //忘记密码
                Intent forget_intent = new Intent(this, ForgetActivity.class);
                startActivity(forget_intent);
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        final String phone = mEtxtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.safeToastShow(LoginActivity.this, "请输入手机号码");
            return;
        }

        String pwd = mEtxtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.safeToastShow(LoginActivity.this, "请输入密码");
            return;
        }

        final PersistentCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        RequestBody formBody = new FormBody.Builder()
                .add("phone", phone)
                .add("password", pwd)
                .build();

        final Request request = new Request.Builder().url(HttpConstants.URL_LOGIN).post(formBody).build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                LoginRespMsg<User> loginRespMsg = new LoginRespMsg<>();

                ToastUtils.showDebugSafeToast(LoginActivity.this, "登录返回结果：" + string);

                ObjectMapper objectMapper = new ObjectMapper();
                ResponseJson responseJson = objectMapper.readValue(string, ResponseJson.class);

                boolean success = responseJson.getSuccess();
                if (success) {
                    Map<String, Object> message = (Map<String, Object>) responseJson.getMessage();

                    loginRespMsg.setToken((String) message.get("token"));
                    loginRespMsg.setData(objectMapper.readValue(objectMapper.writeValueAsString(message.get("data")), User.class));
                }

                CNiaoApplication application = CNiaoApplication.getInstance();
                User user = loginRespMsg.getData();
                user.setPhoneNumber(phone);
                application.putUser(user, loginRespMsg.getToken());
                ToastUtils.showDebugSafeToast(LoginActivity.this, "用户" + CNiaoApplication.getInstance().getUser().getPhoneNumber() + "登录");

                if (application.getIntent() == null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    application.jumpToTargetActivity(LoginActivity.this);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}