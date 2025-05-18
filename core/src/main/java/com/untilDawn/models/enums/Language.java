package com.untilDawn.models.enums;

import com.untilDawn.models.App;

import java.util.Objects;

public enum Language {
    Start("Start", "Commence"),
    ;
//    Quit("Quit"),
//    SignUp("Sign Up"),
//    Login("Login"),
//    Language("Languge: English"),
//    Username("Username"),
//    Password("Password"),


    private final String english;
    private final String french;

    Language(String english, String french) {
        this.english = english;
        this.french = french;
    }

    public String getText() {
        return Objects.equals(App.getLanguage(), "en") ? english : french;
    }
}
