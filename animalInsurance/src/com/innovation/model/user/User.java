package com.innovation.model.user;

/**
 *1，用户名；
 *2，密码；
 */
public class User {
    /**
     * 账号
     */
    int uID;
    /**
     * 密码
     */
    String password;
    BasicInfo basicInfo;
    boolean isLogined = false;
}
