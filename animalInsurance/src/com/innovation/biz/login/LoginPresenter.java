package com.innovation.biz.login;


import com.innovation.model.user.User;

public class LoginPresenter implements ILoginPresenter {
    ILoginView loginView;

    public LoginPresenter(ILoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void login(User user) {

    }

    @Override
    public void showVersonNumber() {

    }

    @Override
    public void startRegister() {

    }


}
