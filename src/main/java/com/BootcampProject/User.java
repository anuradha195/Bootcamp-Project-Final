package com.BootcampProject;

import java.io.IOException;

public class User {
    private String username;
    private String password;
    private String accessToken;
    DailySteps[] results = new DailySteps[7];

    public User(String login, String password) {
        this.username = login;
        this.password = password;
    }

    public DailySteps[] login() throws IOException {
        GoogleFitRequests googleFit = new GoogleFitRequests(this);
        results = googleFit.requestData();
        return results;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}