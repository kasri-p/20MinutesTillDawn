package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.LoginMenu;
import com.untilDawn.views.SignUpMenu;
import com.untilDawn.views.main.PreGameMenu;

public class LoginMenuController {
    private final LoginMenu view;

    public LoginMenuController(LoginMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        view.getLoginButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                String username = view.getUsernameField().getText().trim();
                String password = view.getPasswordField().getText().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    view.getErrorLabel().setText("Username and password cannot be empty.");
                    return;
                }

                User user = App.getUser(username);
                if (user == null) {
                    view.getErrorLabel().setText("User not found.");
                    return;
                }

                if (!App.verifyPassword(password, user.getPassword())) {
                    view.getErrorLabel().setText("Incorrect password.");
                    return;
                }

                App.setLoggedInUser(user);

                PreGameMenu preGameMenu = new PreGameMenu(GameAssetManager.getGameAssetManager().getSkin());
                Main.getMain().setScreen(preGameMenu);
            }
        });

        view.getRegisterButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                Main.getMain().setScreen(new SignUpMenu(GameAssetManager.getGameAssetManager().getSkin()));
            }
        });

        view.getForgotPasswordButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                // i'll implement this functionality later TODO
                // Main.getMain().setScreen(new ForgotPasswordScreen(GameAssetManager.getGameAssetManager().getSkin()));
                view.getErrorLabel().setText("This feature is coming soon!");
            }
        });
    }
}
