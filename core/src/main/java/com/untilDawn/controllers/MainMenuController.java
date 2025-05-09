package com.untilDawn.controllers;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.untilDawn.Main;
import com.untilDawn.views.MainMenu;

public class MainMenuController {
    private MainMenu view;

    public MainMenuController() {
    }

    public void setView(MainMenu preGameMenu) {
        this.view = preGameMenu;
    }

    public void handleMainMenuButtons() {
        if (view != null) {
            view.getPlayButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Play button clicked");
//                    Main.getMain().getScreen().dispose();
                }
            });

            view.getSettingsButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Settings button clicked");
                    // Add logic to open settings menu
                }
            });

            view.getQuitButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Handle exit button click
                    System.out.println("Exit button clicked");
                    Main.getMain().getScreen().dispose();
                }
            });
        }
    }

}
