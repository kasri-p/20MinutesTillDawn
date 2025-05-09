package com.untilDawn.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.untilDawn.Main;
import com.untilDawn.views.MainMenu;

public class MainMenuController {
    private MainMenu view;
    private boolean listenersInitialized = false;

    public MainMenuController() {
    }

    public void setView(MainMenu preGameMenu) {
        this.view = preGameMenu;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        if (view != null && !listenersInitialized) {
            view.getPlayButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Play button clicked");
                    // Add logic to start the game
                    // For example: Main.getMain().setScreen(new GameScreen());
                }
            });

            view.getSettingsButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Settings button clicked");
                    // Add logic to open settings menu
                    // For example: Main.getMain().setScreen(new SettingsScreen());
                }
            });

            view.getQuitButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Exit button clicked");
                    Main.getMain().getScreen().dispose();
                    Gdx.app.exit();
                }
            });

            listenersInitialized = true;
        }
    }
}
