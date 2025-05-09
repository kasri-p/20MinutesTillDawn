package com.untilDawn.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.untilDawn.Main;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.MainMenu;
import com.untilDawn.views.SignUpMenu;

public class MainMenuController {
    private MainMenu view;
    private boolean listenersInitialized = false;

    public MainMenuController() {
    }

    public void setView(MainMenu mainMenu) {
        this.view = mainMenu;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        if (view != null && !listenersInitialized) {
            view.getPlayButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Play button clicked");
                    Main.getMain().getClickSound().play();
                    Main.getMain().setScreen(new SignUpMenu(GameAssetManager.getGameAssetManager().getSkin()));
                }
            });

            view.getSettingsButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Settings button clicked");
                    Main.getMain().getClickSound().play();

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
