package com.untilDawn.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Weapon;
import com.untilDawn.views.main.GameView;

public class GameController {
    private GameView view;
    private PlayerController playerController;
    private WeaponController weaponController;
    private WorldController worldController;

    public GameController(GameView view) {
        this.view = view;
        this.playerController = new PlayerController(App.getGame().getPlayer());
        this.weaponController = new WeaponController(new Weapon());
        this.weaponController.setPlayerController(playerController);
        this.worldController = new WorldController(playerController);

        this.playerController.getPlayer().setPosX(0);
        this.playerController.getPlayer().setPosY(0);
    }

    public void updateGame() {
        if (view != null) {
            OrthographicCamera camera = view.getCamera();
            Main.getBatch().setProjectionMatrix(camera.combined);
            
            worldController.update();
            playerController.update();
            weaponController.update();
        }
    }

    public WeaponController getWeaponController() {
        return weaponController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }
}
