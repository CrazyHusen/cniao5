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
    /** 登录基础url */
    public static final String BASE_URL = "http://223.3.89.104:8080/highdsa-restful-app/";
    //public static final String BASE_URL = "http:// 192.168.42.88:8081/highdsa-restful-app/";

    /** 登录 */
    public static final String URL_LOGIN = BASE_URL + "app/v1/login/phone";
    /** 退出登录 */
    public static final String URL_LOGOUT = BASE_URL + "app/v1/logout";

    /** 发送验证码 */
    public static final String URL_SEND_CODE = BASE_URL + "sms/v1/captcha";
    /** 校验验证码 */
    public static final String URL_VALIDATE_CODE = BASE_URL + "sms/v1/validate";

    /** 校验验证码 */
    public static final String URL_REGISTER_USER = BASE_URL + "register/v1/user";

    /**  找回密码 */
    public static final String URL_FORGET_PASSWORD = BASE_URL + "app/v1/password";

    /**  获取个人信息 */
    public static final String URL_USER_INFO = BASE_URL + "app/v1/user/info/phone";

}