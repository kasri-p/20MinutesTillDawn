package com.untilDawn.controllers;

import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.views.main.PreGameMenu;

public class PreGameMeuController {
    private PreGameMenu view;

    public PreGameMeuController() {
    }

    public void setView(PreGameMenu view) {
        this.view = view;
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    public void onCharacterSelected(String characterName) {
        playClick();
    }

    public void onWeaponSelected(String weaponName) {
        playClick();
    }

    public void onTimeSelected(int minutes) {
        playClick();
    }

    public void onDifficultySelected(int level) {
        playClick();
    }

    public void startGame() {
        playClick();
    }
}
