package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.LoginMenu;
import com.untilDawn.views.MainMenu;
import com.untilDawn.views.SecurityQuestionWindow;
import com.untilDawn.views.SignUpMenu;

import java.util.Random;

public class SignUpMenuController {
    private SignUpMenu view;

    public SignUpMenuController(SignUpMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        view.getSignUpButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                String username = view.getUsernameField().getText().trim();
                String password = view.getPasswordField().getText().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    view.getErrorLabel().setText("All fields are required.");
                    return;
                }

                if (App.getUser(username) != null) {
                    view.getErrorLabel().setText("Username already exists.");
                    return;
                }

                if (!isPasswordStrong(password)) {
                    view.getErrorLabel().setText("Password must be at least 8 characters, include a special character, a number, and an uppercase letter.");
                    return;
                }

                String hashedPassword = App.hashPassword(password);
                User newUser = new User(username, hashedPassword, "", getRandomAvatar());
                App.addUser(newUser);
                App.setLoggedInUser(newUser);

                SecurityQuestionWindow securityWindow = new SecurityQuestionWindow(
                    GameAssetManager.getGameAssetManager().getSkin(), newUser);
                securityWindow.setOnCompleteCallback(() -> {
                    navigateToMainMenu();
                });
                view.getStage().addActor(securityWindow);
            }
        });

        view.getSkipButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                User guestUser = new User();
                App.setLoggedInUser(guestUser);
                navigateToMainMenu();
            }
        });

        view.getBackButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                navigateToLoginMenu();
            }
        });
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
        MainMenuController controller = new MainMenuController();
        MainMenu mainMenu = new MainMenu(controller, GameAssetManager.getGameAssetManager().getSkin());
        controller.setView(mainMenu);
        Main.getMain().setScreen(mainMenu);
    }

    private void navigateToLoginMenu() {
        Main.getMain().setScreen(new LoginMenu(GameAssetManager.getGameAssetManager().getSkin()));
    }
}
