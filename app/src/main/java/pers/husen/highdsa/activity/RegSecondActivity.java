package pers.husen.highdsa.activity;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pers.husen.highdsa.CNiaoApplication;
import pers.husen.highdsa.R;
import pers.husen.highdsa.bean.User;
import pers.husen.highdsa.contants.Contants;
import pers.husen.highdsa.contants.HttpContants;
import pers.husen.highdsa.msg.LoginRespMsg;
import pers.husen.highdsa.utils.CountTimerView;
import pers.husen.highdsa.utils.DESUtil;

import okhttp3.Response;
import pers.husen.highdsa.constants.HttpConstants;
import pers.husen.highdsa.utils.LogUtil;
import pers.husen.highdsa.utils.ToastUtils;
import pers.husen.highdsa.utils.TripleDesEncrypt;
import pers.husen.highdsa.widget.CNiaoToolBar;
import pers.husen.highdsa.widget.ClearEditText;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import dmax.dialog.SpotsDialog;
import okhttp3.Call;

/**
 * Description 接收验证码的注册界面,即注册界面二
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/13 21:05
 * <p>
 * Version 1.0.0
 */
public class RegSecondActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    CNiaoToolBar mToolBar;

    @BindView(R.id.txtTip)
    TextView mTxtTip;

    @BindView(R.id.btn_reSend)
    Button mBtnResend;

    @BindView(R.id.edittxt_code)
    ClearEditText mEtCode;

    private String phone;
    private String pwd;
    private String countryCode;

    private EventHandler eventHandler;
    private SpotsDialog dialog;
    private Gson mGson = new Gson();

    @Override
    protected void init() {

        initToolBar();
        dialog = new SpotsDialog(this);

        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");

        String formatedPhone = "+" + countryCode + " " + splitPhoneNum(phone);
        String text = getString(R.string.smssdk_send_mobile_detail) + formatedPhone;
        mTxtTip.setText(Html.fromHtml(text));

        CountTimerView timerView = new CountTimerView(mBtnResend);    //倒计时
        timerView.start();

        initSms();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_reg_second;
    }

    private void initSms() {
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        try {
                            // 与后台交互
                            doReg();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //                        dialog.setMessage("正在提交注册信息");
                        //                        dialog.show();
                    }
                } else if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    ToastUtils.showSafeToast(RegSecondActivity.this, msg);
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 标题栏 完成
     */
    private void initToolBar() {
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });
    }

    /**
     * 提交验证码
     */
    private void submitCode() {
        //验证码
        String vCode = mEtCode.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            ToastUtils.showSafeToast(RegSecondActivity.this, "请填写验证码");

            return;
        }

        //校验验证码
        validateCode(phone, vCode);
        //SMSSDK.submitVerificationCode(countryCode, phone, vCode);
    }


    /**
     * 注册.与后台交互
     */
    private void doReg() throws Exception {
        String pwdEncode = TripleDesEncrypt.encrypt(pwd);

        Map<String, String> params = new HashMap<>();
        params.put("userPhone", phone);
        //TODO - 用户名不能为空
        params.put("userName", phone);
        params.put("userPassword", pwdEncode);

        OkHttpUtils.post().url(HttpConstants.URL_REGISTER_USER).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.e("注册", "失败", true);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.e("注册", "成功", true);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LoginRespMsg<User> loginRespMsg = mGson.fromJson(response, new
                        TypeToken<LoginRespMsg<User>>() {
                        }.getType());
                CNiaoApplication application = CNiaoApplication.getInstance();
                application.putUser(loginRespMsg.getData(), loginRespMsg.getToken());

                startActivity(new Intent(RegSecondActivity.this, MainActivity.class));
                finish();

            }
        });
    }

    /**
     * 分割电话号码
     */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }

    /**
     * 发送验证码
     */
    private void validateCode(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);

        // 修改登录的请求地址
        OkHttpUtils.post().url(HttpConstants.URL_VALIDATE_CODE).params(params).build().execute(new Callback<String>() {
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