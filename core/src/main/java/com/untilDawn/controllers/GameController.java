package com.untilDawn.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.untilDawn.Main;
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

        // Initialize player position to be at the center of the world coordinates
        this.playerController.getPlayer().setPosX(0);
        this.playerController.getPlayer().setPosY(0);
    }

    public void updateGame() {
        if (view != null) {
            // Update camera position to follow player
            OrthographicCamera camera = view.getCamera();

            // Move camera to player position
            camera.position.x = playerController.getPlayer().getPosX();
            camera.position.y = playerController.getPlayer().getPosY();
            camera.update();

            // Set the projection matrix of the batch to the camera
            Main.getBatch().setProjectionMatrix(camera.combined);

            worldController.update();
            playerController.update();
            weaponController.update();
        }
    }

    public WeaponController getWeaponController() {
        return weaponController;
    }
}
