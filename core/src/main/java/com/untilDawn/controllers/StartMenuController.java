package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.untilDawn.Main;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.SignUpMenu;
import com.untilDawn.views.StartMenu;

public class StartMenuController {
    private StartMenu view;
    private boolean listenersInitialized = false;

    public StartMenuController() {
    }

    public void setView(StartMenu startMenu) {
        this.view = startMenu;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        if (view != null && !listenersInitialized) {
            view.getStartButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Play button clicked");
                    Main.getMain().getClickSound().play();
                    Main.getMain().setScreen(new SignUpMenu(GameAssetManager.getGameAssetManager().getSkin()));
                }
            });

            view.getQuitButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Main.getMain().getClickSound().play();
                    System.out.println("Exit button clicked");
                    Main.getMain().getScreen().dispose();
                    Gdx.app.exit();
                }
            });

            listenersInitialized = true;
        }
    }
}
