package com.innovation.biz.login;

import com.innovation.login.User;

public interface ILoginView {

    void onAuthenticate();

    int onLoginError(String message);

    void onLoginSuccess(String message);

    void onLogin();
}
