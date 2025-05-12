package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.StartMenu;
import com.untilDawn.views.main.MainMenu;
import com.untilDawn.views.main.PreGameMenu;
import com.untilDawn.views.main.ProfileMenu;

public class MainMenuController {
    MainMenu view;

    public MainMenuController(MainMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    public void initializeButtonListeners() {
        view.getLogoutButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                Main.getMain().setScreen(new StartMenu(new StartMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
            }
        });
        view.getProfileButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                navigateToProfileMenu();
            }
        });
        view.getPlayButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                Main.getMain().setScreen(new PreGameMenu(GameAssetManager.getGameAssetManager().getSkin()));
            }
        });
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    private void navigateToProfileMenu() {
        ProfileMenu profileMenu = new ProfileMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(profileMenu);
    }
}
