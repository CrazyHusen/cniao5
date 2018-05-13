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
    public static final String BASE_URL_LOGIN = "http://223.3.89.104:8080/highdsa-web-app/app/v1/";

    /**
     * 登录
     */
    public static final String URL_LOGIN = BASE_URL_LOGIN + "login/phone";
    /**
     * 退出登录
     */
    public static final String URL_LOGOUT = BASE_URL_LOGIN + "logout";
}