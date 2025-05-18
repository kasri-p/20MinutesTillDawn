package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.LoginMenu;
import com.untilDawn.views.SignUpMenu;
import com.untilDawn.views.main.MainMenu;
import com.untilDawn.views.window.ForgotPasswordWindow;

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
                playClick();
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

                MainMenu mainMenu = new MainMenu(GameAssetManager.getGameAssetManager().getSkin());
                Main.getMain().setScreen(mainMenu);
            }
        });

        view.getRegisterButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                Main.getMain().setScreen(new SignUpMenu(GameAssetManager.getGameAssetManager().getSkin()));
            }
        });

        view.getForgotPasswordButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                ForgotPasswordWindow forgotPasswordWindow = new ForgotPasswordWindow(GameAssetManager.getGameAssetManager().getSkin(), view.getStage());
                view.getStage().addActor(forgotPasswordWindow);
            }
        });
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

}
