package com.robindas.bloodbridge.DTO;

/**
 * DTO for notification details returned from the API.
 */
public class NotificationResponse {
    private int id;
    private String notTitle;
    private String notMessage;
    private boolean isRead;
    private String bloodgrp;
    private String usernameDonor;

    public NotificationResponse() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotTitle() {
        return notTitle;
    }

    public void setNotTitle(String notTitle) {
        this.notTitle = notTitle;
    }

    public String getNotMessage() {
        return notMessage;
    }

    public void setNotMessage(String notMessage) {
        this.notMessage = notMessage;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getBloodgrp() {
        return bloodgrp;
    }

    public void setBloodgrp(String bloodgrp) {
        this.bloodgrp = bloodgrp;
    }

    public String getUsernameDonor() {
        return usernameDonor;
    }

    public void setUsernameDonor(String usernameDonor) {
        this.usernameDonor = usernameDonor;
    }
}
