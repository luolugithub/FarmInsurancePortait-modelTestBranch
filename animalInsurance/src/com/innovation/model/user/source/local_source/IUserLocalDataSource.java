package com.innovation.model.user.source.local_source;

import com.innovation.model.user.User;

public interface IUserLocalDataSource {
    void getUser(String UserId);

    void saveUser(User user);

    void dlelteUser(String userID);

}
