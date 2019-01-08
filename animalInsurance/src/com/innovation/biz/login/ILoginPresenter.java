package com.innovation.biz.login;

import com.innovation.model.user.User;

public interface ILoginPresenter {
    void login(User user);
    void showVersonNumber();
    void startRegister();
}
