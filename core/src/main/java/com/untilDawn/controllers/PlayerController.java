package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.untilDawn.Main;
import com.untilDawn.models.Player;
import com.untilDawn.models.utils.GameAssetManager;

public class PlayerController {
    private Player player;
    private boolean recentlyFilipped = false;
    private float screenCenterX;
    private float screenCenterY;

    public PlayerController(Player player) {
        this.player = player;
        updateScreenCenter();
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    public void update() {
        player.getPlayerSprite().setPosition(
            player.getPosX() - player.getPlayerSprite().getWidth() / 2,
            player.getPosY() - player.getPlayerSprite().getHeight() / 2
        );

        player.getPlayerSprite().draw(Main.getBatch());

        if (player.isPlayerIdle()) {
            idleAnimation();
        }

        handlePlayerInput();
    }

    public void handlePlayerInput() {
//        App.getGame().getPlayer().setPlayerRunning(true);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setPosY(player.getPosY() + player.getSpeed());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setPosX(player.getPosX() + player.getSpeed());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setPosY(player.getPosY() - player.getSpeed());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setPosX(player.getPosX() - player.getSpeed());
            if (!recentlyFilipped) {
                player.getPlayerSprite().flip(true, false);
            }
            recentlyFilipped = true;
        }
    }

    public void idleAnimation() {
        GameAssetManager.getGameAssetManager().getPlayerIdleAnimation().setPlayMode(Animation.PlayMode.LOOP);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
