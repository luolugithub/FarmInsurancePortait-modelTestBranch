package com.innovation.data.source;

import com.innovation.model.user.User;

public interface IUserDataSource {

    void saveUser(User user);

    User getUser(int uId);
}
