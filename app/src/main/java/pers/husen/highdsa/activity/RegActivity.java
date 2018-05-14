package pers.husen.highdsa.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pers.husen.highdsa.R;
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
        initSms();
    }

    private void initSms() {
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        afterVerificationCodeRequested((Boolean) data);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();

                    LogUtil.e("SMSDK执行", msg);

                    ToastUtils.showSafeToast(RegActivity.this, msg);
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 跳转到注册界面二
     *
     * @param data
     */
    private void afterVerificationCodeRequested(Boolean data) {
        LogUtil.e("注册界面", "代码是否执行", true);

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

    private void initToolBar() {
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();
            }
        });
    }

    /**
     * 获取手机号 密码等信息 发送验证码
     */
    private void getCode() {
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();

        //如果手机号无效,直接返回
        if (!checkPhoneNumValidate(phone, code)) {
            return;
        }

        LogUtil.e("手机号验证完毕", "123");
        //发送验证码
        sendValidateCode(phone);
        //SMSSDK.getVerificationCode(code, phone);
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
            ToastUtils.showSafeToast(RegActivity.this, "请输入手机号码");

            return false;
        }

        if ("86".equals(code)) {
            if (phone.length() != 11) {
                LogUtil.e("手机号长度不为11", phone);
                ToastUtils.showSafeToast(RegActivity.this, "手机号码长度不对");

                return false;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            LogUtil.e("手机号格式不正确", phone);
            ToastUtils.showSafeToast(RegActivity.this, "您输入的手机号码格式不正确");

            return false;
        }

        return true;
    }

    /**
     * 发送验证码
     */
    private void sendValidateCode(String phone) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);

        // 修改登录的请求地址
        OkHttpUtils.post().url(HttpConstants.URL_SEND_CODE).params(params).build().execute(new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                String string = response.body().string();

                //使用jackson
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> map = objectMapper.readValue(string, Map.class);

                LogUtil.d("测试结果", map.toString());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}