package com.untilDawn.controllers;

import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.MainMenu;
import com.untilDawn.views.SignUpMenu;

import java.util.Random;

public class SignUpMenuController {
    private SignUpMenu view;

    public SignUpMenuController(SignUpMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        view.getSignUpButton().addListener(event -> {
            Main.getMain().getClickSound().play();
            String username = view.getUsernameField().getText();
            String password = view.getPasswordField().getText();
            String securityAnswer = view.getSecurityAnswerField().getText();

            if (username.isEmpty() || password.isEmpty() || securityAnswer.isEmpty()) {
                view.getErrorLabel().setText("All fields are required.");
                return false;
            }

            if (App.getUser(username) != null) {
                view.getErrorLabel().setText("Username already exists.");
                return false;
            }

            if (!isPasswordStrong(password)) {
                view.getErrorLabel().setText("Password must be at least 8 characters, include a special character, a number, and an uppercase letter.");
                return false;
            }

            User newUser = new User(username, password, securityAnswer, getRandomAvatar());
            App.addUser(newUser);
            App.save();
            view.getErrorLabel().setText("Registration successful!");
            return true;
        });

        view.getSkipButton().addListener(event -> {
            Main.getMain().getClickSound().play();
            User guestUser = new User();
            App.setLoggedInUser(guestUser);
            navigateToMainMenu();
            return true;
        });

//        view.getBackButton().addListener(event -> navigateToLoginMenu();
//        );
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 &&
            password.matches(".*[@#$%&*()_].*") &&
            password.matches(".*\\d.*") &&
            password.matches(".*[A-Z].*");
    }

    private String getRandomAvatar() {
        String[] avatars = {"avatar1.png", "avatar2.png", "avatar3.png"};
        return avatars[new Random().nextInt(avatars.length)];
    }

    private void navigateToMainMenu() {
//        Main.getMain().getClickSound().play();

        MainMenuController controller = new MainMenuController();
        MainMenu mainMenu = new MainMenu(controller, GameAssetManager.getGameAssetManager().getSkin());
        controller.setView(mainMenu);
        Main.getMain().setScreen(mainMenu);
    }

    private void navigateToLoginMenu() {
        Main.getMain().setScreen(new MainMenu(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }
}
