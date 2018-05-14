package pers.husen.highdsa.constants;

/**
 * Description http请求常量
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/13 16:09
 * <p>
 * Version 1.0.0
 */
public class HttpConstants {
    /**
     * 登录基础url
     */
    public static final String BASE_URL_LOGIN = "http://223.3.89.104:8080/highdsa-web-app/";

    /** 登录 */
    public static final String URL_LOGIN = BASE_URL_LOGIN + "app/v1/login/phone";
    /** 退出登录 */
    public static final String URL_LOGOUT = BASE_URL_LOGIN + "app/v1/logout";

    /** 发送验证码 */
    public static final String URL_SEND_CODE = BASE_URL_LOGIN + "sms/v1/captcha";
    /** 校验验证码 */
    public static final String URL_VALIDATE_CODE = BASE_URL_LOGIN + "app/v1/validate";

    /** 校验验证码 */
    public static final String URL_REGISTER_USER = BASE_URL_LOGIN + "/register/v1/user";
}