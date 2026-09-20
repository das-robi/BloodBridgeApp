package com.robindas.bloodbridge.Model;

public class UserProfile {

    private int userId;
    private String userName;
    private String passWord;
    private String userEmail;

    public UserProfile(int userId, String userName, String passWord, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
