package com.cniao.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cniao.CNiaoApplication;
import com.cniao.R;
import com.cniao.bean.User;
import com.cniao.contants.Contants;
import com.cniao.msg.LoginRespMsg;
import com.cniao.utils.DESUtil;
import com.cniao.utils.ToastUtils;
import com.cniao.widget.CNiaoToolBar;
import com.cniao.widget.ClearEditText;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import pers.husen.highdsa.constants.HttpConstants;

/**
 * Description 登录界面
 *
 * Author 何明胜
 *
 * Created at 2018/05/13 20:11
 * 
 * Version 1.0.0
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

    @OnClick({R.id.btn_login, R.id.txt_toReg})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();   //登录
                break;
            case R.id.txt_toReg:
                Intent intent = new Intent(this, RegActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {

        String phone = mEtxtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请输入手机号码");
            return;
        }

        String pwd = mEtxtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请输入密码");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));
        Log.d("用户输入", params.toString());

        // 修改登录的请求地址
        OkHttpUtils.post().url(HttpConstants.URL_LOGIN).params(params).build().execute(new Callback<LoginRespMsg<User>>() {
            @Override
            public LoginRespMsg<User> parseNetworkResponse(Response response, int id) throws Exception {
                String string = response.body().string();

                Log.d("登录", string);

                //LoginRespMsg loginRespMsg = new Gson().fromJson(string, LoginRespMsg.class);
                //使用jackson
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> map = objectMapper.readValue(string, Map.class);

                LoginRespMsg<User> loginRespMsg = new LoginRespMsg<>();
                loginRespMsg.setToken(map.get("token"));
                loginRespMsg.setData(objectMapper.readValue(objectMapper.writeValueAsString(map.get("data")), User.class));
                Log.d("登录1", loginRespMsg.getToken());
                Log.d("登录2", loginRespMsg.getData().getClass().getName());

                return loginRespMsg;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(LoginRespMsg<User> response, int id) {
                CNiaoApplication application = CNiaoApplication.getInstance();
                application.putUser(response.getData(), response.getToken());
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