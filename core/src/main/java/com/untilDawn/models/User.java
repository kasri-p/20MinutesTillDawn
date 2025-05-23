package com.untilDawn.models;

public class User {
    private String username;
    private String password;
    private int SecurityQuestionIndex;
    private String SecurityAnswer;
    private Game lastGame;
    private boolean isGuest;
    private int score;
    private String avatarPath;
    private int deaths;
    private int kills;
    private float survivalTime;

    public User(String username, String password, String avatarPath) {
        this.username = username;
        this.password = password;
        this.avatarPath = avatarPath;
        this.isGuest = false;
        this.SecurityQuestionIndex = 0;
        this.SecurityAnswer = "";
        this.survivalTime = 0;
    }


    public User() {
        this.username = "guest";
        this.password = "Guest#";
        isGuest = true;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public float getSurvivalTime() {
        return survivalTime;
    }

    public void setSurvivalTime(float survivalTime) {
        this.survivalTime = survivalTime;
    }

    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", SecurityQuestionIndex=" + SecurityQuestionIndex +
            ", SecurityAnswer='" + SecurityAnswer + '\'' +
            ", lastGame=" + lastGame +
            ", isGuest=" + isGuest +
            ", score=" + score +
            ", avatarPath='" + avatarPath + '\'' +
            ", deaths=" + deaths +
            ", kills=" + kills +
            '}';
    }
}
