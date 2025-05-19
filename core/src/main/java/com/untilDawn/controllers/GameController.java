package com.untilDawn.controllers;

import com.untilDawn.models.Player;
import com.untilDawn.models.Weapon;
import com.untilDawn.views.main.GameView;

public class GameController {
    private GameView view;
    private PlayerController playerController;
    private WeaponController weaponController;
    private WorldController worldController;

    public GameController(GameView view) {
        this.view = view;
        this.weaponController = new WeaponController(new Weapon());
        this.playerController = new PlayerController(new Player());
        this.worldController = new WorldController(playerController);
    }

    public void updateGame() {
        if (view != null) {
            worldController.update();
            playerController.update();
            weaponController.update();
        }
    }

    public void handleResize(int width, int height) {
        if (worldController != null) {
            worldController.handleResize(width, height);
        }

        if (playerController != null) {
            playerController.handleResize(width, height);
        }

        if (weaponController != null) {
            weaponController.handleResize(width, height);
        }
    }

    public WeaponController getWeaponController() {
        return weaponController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public WorldController getWorldController() {
        return worldController;
    }
}
