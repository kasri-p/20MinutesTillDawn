package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.main.ProfileMenu;
import com.untilDawn.views.window.ChangeAvatarWindow;
import com.untilDawn.views.window.ChangePasswordWindow;
import com.untilDawn.views.window.ChangeUsernameWindow;
import com.untilDawn.views.window.DeleteAccountWindow;

public class ProfileMenuController {
    private final ProfileMenu view;

    public ProfileMenuController(ProfileMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        view.getChangeUsernameButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                ChangeUsernameWindow changeUsernameWindow = new ChangeUsernameWindow(GameAssetManager.getGameAssetManager().getSkin(), view.getStage());
                changeUsernameWindow.setOnComplete(() -> {
                    navigateToProfileMenu();
                });
                view.getStage().addActor(changeUsernameWindow);
            }
        });

        view.getChangePasswordButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(
                    GameAssetManager.getGameAssetManager().getSkin(),
                    App.getLoggedInUser(),
                    view.getStage()
                );
                changePasswordWindow.setOnComplete(() -> {
                    navigateToProfileMenu();
                });
                view.getStage().addActor(changePasswordWindow);
            }
        });

        view.getDeleteAccountButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                DeleteAccountWindow deleteAccountWindow = new DeleteAccountWindow(
                    GameAssetManager.getGameAssetManager().getSkin(),
                    view.getStage()
                );
                view.getStage().addActor(deleteAccountWindow);
            }
        });

        view.getChangeAvatarButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                view.getStage().addActor(new ChangeAvatarWindow(
                    GameAssetManager.getGameAssetManager().getSkin(),
                    view.getStage(),
                    "Change Avatar feature is coming soon!"
                ));
            }
        });
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    public void navigateToProfileMenu() {
        ProfileMenu profileMenu = new ProfileMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(profileMenu);
    }
}
