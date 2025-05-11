package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.main.ProfileMenu;
import com.untilDawn.views.window.ChangeUsernameWindow;

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
