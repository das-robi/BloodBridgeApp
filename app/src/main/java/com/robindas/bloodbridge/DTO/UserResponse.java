package com.robindas.bloodbridge.DTO;

/**
 * DTO for user information returned in admin lists.
 */
public class UserResponse {

    private int userId;
    private String userName;
    private String userEmail;
    private String role;

    public UserResponse() {

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
