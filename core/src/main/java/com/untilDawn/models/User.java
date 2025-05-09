package com.untilDawn.models;

public class User {
    private String username;
    private String password;
    private String email;
    private int SecurityQuestionIndex;
    private String SecurityAnswer;
    private Game lastGame;
    private boolean isGuest;

    private int score;
    private String avatarPath;
    private int deaths;
    private int kills;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        isGuest = false;
    }

    public User() {
        this.username = "guest";
        this.password = "Guest#";
        isGuest = true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSecurityAnswer() {
        return SecurityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        SecurityAnswer = securityAnswer;
    }

    public int getSecurityQuestionIndex() {
        return SecurityQuestionIndex;
    }

    public void setSecurityQuestionIndex(int securityQuestionIndex) {
        SecurityQuestionIndex = securityQuestionIndex;
    }
}
