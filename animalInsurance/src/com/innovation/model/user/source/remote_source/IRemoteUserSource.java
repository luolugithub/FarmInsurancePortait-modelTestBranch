package com.innovation.model.user.source.remote_source;

public interface IRemoteUserSource {
    void getUser(String userId, String password);
}
