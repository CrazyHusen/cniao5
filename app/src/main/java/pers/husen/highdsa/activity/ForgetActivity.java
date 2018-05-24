package pers.husen.highdsa.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import dmax.dialog.SpotsDialog;
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
import pers.husen.highdsa.utils.CountTimerView;
import pers.husen.highdsa.utils.LogUtil;
import pers.husen.highdsa.utils.OkHttp3Utils;
import pers.husen.highdsa.utils.ToastUtils;
import pers.husen.highdsa.widget.CNiaoToolBar;
import pers.husen.highdsa.widget.ClearEditText;

/**
 * Description 忘记密码activity
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/13 20:13
 * <p>
 * Version 1.0.0
 */
public class ForgetActivity extends BaseActivity {
    //右上角继续按钮
    @BindView(R.id.toolbar)
    CNiaoToolBar mToolBar;
    @BindView(R.id.txtCountry)
    TextView mTxtCountry;
    //国家编码,如+86
    @BindView(R.id.txtCountryCode)
    TextView mTxtCountryCode;
    @BindView(R.id.edittxt_phone)
    ClearEditText mEtxtPhone;
    @BindView(R.id.edittxt_verification)
    ClearEditText mEtxtVerification;
    @BindView(R.id.btn_reSend)
    Button mVerificationButton;
    @BindView(R.id.edittxt_pwd)
    ClearEditText mEtxtPwd;

    private SpotsDialog dialog;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget;
    }

    @Override
    protected void init() {
        dialog = new SpotsDialog(this);
        initToolBar();
    }

    private void initToolBar() {
        mVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVerificationButton.getText().equals("获取验证码") || mVerificationButton.getText().equals("重新获取验证码")) {
                    getCode();

                    //倒计时
                    CountTimerView timerView = new CountTimerView(mVerificationButton);
                    timerView.start();
                }
            }
        });
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //验证
                checkVerification();
            }
        });
    }

    /**
     * 获取手机号、密码等信息,并发送验证码
     */
    private void getCode() {
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();

        //如果手机号无效,直接返回
        if (!checkPhoneNumValidate(phone, code)) {
            return;
        }

        //发送验证码
        sendValidateCode(phone);
    }

    /**
     * 对手机号有效性进行验证
     */
    private boolean checkPhoneNumValidate(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            LogUtil.e("手机号为空", "123");
            ToastUtils.safeToastShow(ForgetActivity.this, "请输入手机号码");

            return false;
        }

        if ("86".equals(code)) {
            if (phone.length() != 11) {
                LogUtil.e("手机号长度不为11", phone);
                ToastUtils.safeToastShow(ForgetActivity.this, "手机号码长度不对");

                return false;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            LogUtil.e("手机号格式不正确", phone);
            ToastUtils.safeToastShow(ForgetActivity.this, "您输入的手机号码格式不正确");

            return false;
        }

        return true;
    }

    /**
     * 发送验证码
     */
    private void sendValidateCode(String phone) {
        Map<String, String> params = new HashMap<>();
        params.put("phone_number", phone);
        ToastUtils.showDebugSafeToast(ForgetActivity.this, "手机号：" + phone);

        ToastUtils.showDebugSafeToast(ForgetActivity.this, "发送验证码");
        OkHttpUtils.post().url(HttpConstants.URL_SEND_CODE).params(params).build().execute(new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                String string = response.body().string();

                //使用jackson
                ResponseJson responseJson = new ObjectMapper().readValue(string, ResponseJson.class);

                if (responseJson.getSuccess()) {
                    ToastUtils.showDebugSafeToast(ForgetActivity.this, "验证码发送成功：" + responseJson.getMessage());
                    ToastUtils.safeToastShow(ForgetActivity.this, "验证码：" + responseJson.getMessage());
                    mEtxtVerification.setText(responseJson.getMessage().toString());
                } else {
                    ToastUtils.showDebugSafeToast(ForgetActivity.this, "验证码发送失败：" + responseJson.getMessage());
                    ToastUtils.safeToastShow(ForgetActivity.this, "验证码发送失败：" + responseJson.getMessage());
                }

                return (String) responseJson.getMessage();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showDebugSafeToast(ForgetActivity.this, "发送验证码出错");
            }

            @Override
            public void onResponse(String response, int id) {
            }
        });
    }

    /**
     * 进行验证码校验
     */
    private void checkVerification() {
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String pwd = mEtxtPwd.getText().toString().trim();

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showDebugSafeToast(ForgetActivity.this, "请填写新密码");

            return;
        }

        //验证码
        String vCode = mEtxtVerification.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            ToastUtils.showDebugSafeToast(ForgetActivity.this, "请填写验证码");

            return;
        }

        dialog.setMessage("正在提交信息...");
        dialog.show();

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();

        RequestBody formBody = new FormBody.Builder()
                .add("phone_number", phone)
                .add("captcha", vCode)
                .add("new_password", pwd)
                .build();

        Map<String, String> params = new HashMap<>();
        params.put("phone_number", phone);
        params.put("captcha", vCode);
        params.put("new_password", pwd);

        final Request request = new Request.Builder().url(OkHttp3Utils.appendParams(HttpConstants.URL_FORGET_PASSWORD, params)).put(formBody).build();
        Call call = mOkHttpClient.newCall(request);

        //验证验证码并修改密码
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();

                ToastUtils.safeToastShow(ForgetActivity.this, "返回结果：" + string);
                LogUtil.e("修改密码", string);

                ResponseJson responseJson = new ObjectMapper().readValue(string, ResponseJson.class);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (responseJson.getSuccess()) {
                    ToastUtils.safeToastShow(ForgetActivity.this, "" + responseJson.getMessage());

                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtils.safeToastShow(ForgetActivity.this, "密码修改失败：" + responseJson.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}