
package pers.husen.highdsa.bean;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;

import java.io.Serializable;

/**
 * Description 用户信息
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/22 01:03
 * <p>
 * Version 1.0.0
 */
public class User implements Serializable {
    private Long id;
    private String email;
    private String logo_url;
    private String username;
    private String mobi;
    private String phone_number;

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", logo_url=" + logo_url + ", username=" + username + ", mobi=" + mobi + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobi() {
        return mobi;
    }

    public void setMobi(String mobi) {
        this.mobi = mobi;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }
}