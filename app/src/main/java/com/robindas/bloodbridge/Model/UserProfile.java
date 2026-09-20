package com.robindas.bloodbridge.Model;

/**
 * Model class representing a user's profile.
 */
public class UserProfile {

    private int userId;
    private String userName;
    private String passWord;
    private String userEmail;
    private String role; // Added role field for authorization

    public UserProfile(int userId, String userName, String passWord, String userEmail, String role) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.userEmail = userEmail;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
