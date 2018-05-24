package pers.husen.highdsa.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pers.husen.highdsa.R;
import pers.husen.highdsa.msg.ResponseJson;
import pers.husen.highdsa.utils.ToastUtils;
import pers.husen.highdsa.widget.CNiaoToolBar;
import pers.husen.highdsa.widget.ClearEditText;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;
import pers.husen.highdsa.constants.HttpConstants;
import pers.husen.highdsa.utils.LogUtil;

/**
 * Description 注册activity
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/13 20:13
 * <p>
 * Version 1.0.0
 */
public class RegActivity extends BaseActivity {
    private EventHandler eventHandler;
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
    @BindView(R.id.edittxt_pwd)
    ClearEditText mEtxtPwd;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_reg;
    }

    @Override
    protected void init() {
        initToolBar();
    }

    private void initToolBar() {
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送验证码
                getCode();
                ////获取验证码成功,跳转到第二个界面
                afterVerificationCodeRequested();
            }
        });
    }

    /**
     * 获取手机号、密码等信息,并发送验证码
     */
    private void getCode() {
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();

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
            ToastUtils.showDebugSafeToast(RegActivity.this, "请输入手机号码");

            return false;
        }

        if ("86".equals(code)) {
            if (phone.length() != 11) {
                LogUtil.e("手机号长度不为11", phone);
                ToastUtils.showDebugSafeToast(RegActivity.this, "手机号码长度不对");

                return false;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            LogUtil.e("手机号格式不正确", phone);
            ToastUtils.showDebugSafeToast(RegActivity.this, "您输入的手机号码格式不正确");

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
        ToastUtils.showDebugSafeToast(RegActivity.this, "注册手机号：" + phone);

        OkHttpUtils.post().url(HttpConstants.URL_SEND_CODE).params(params).build().execute(new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                String string = response.body().string();

                //使用jackson
                ResponseJson responseJson = new ObjectMapper().readValue(string, ResponseJson.class);

                if (responseJson.getSuccess()) {
                    ToastUtils.showDebugSafeToast(RegActivity.this, "验证码发送成功：" + responseJson.getMessage());
                    ToastUtils.safeToastShow(RegActivity.this,"验证码："+responseJson.getMessage());
                } else {
                    ToastUtils.showDebugSafeToast(RegActivity.this, "验证码发送失败：" + responseJson.getMessage());
                    ToastUtils.safeToastShow(RegActivity.this,"验证码："+responseJson.getMessage());
                }

                return (String) responseJson.getMessage();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showDebugSafeToast(RegActivity.this, "发送验证码出错");
            }

            @Override
            public void onResponse(String response, int id) {
            }
        });
    }

    /**
     * 跳转到注册界面二
     */
    private void afterVerificationCodeRequested() {
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();

        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        Intent intent = new Intent(this, RegSecondActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pwd);
        intent.putExtra("countryCode", code);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}