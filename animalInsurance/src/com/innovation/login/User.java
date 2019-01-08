package com.innovation.login;

/**
 * Created by luolu on 08/01/2018.
 */

public class User {
    private int id;
    private String name;
    private String phoneNumber;
    private String password;
    private String idNumber;



    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String name) {
        this.phoneNumber = phoneNumber;
    }

    public String getIDNumber() {
        return idNumber;
    }

    public void setIDNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

