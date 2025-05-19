package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.untilDawn.models.enums.Characters;

public class Player {
    private Texture playerTexture = new Texture(Gdx.files.internal("Images/characters/Shana/run0.png"));
    private Sprite playerSprite = new Sprite(playerTexture);
    private float posX = 0;
    private float posY = 0;
    private float playerHealth = 100;
    private CollisionRect rect;
    private float time = 0;
    private float speed = 5;
    private boolean isPlayerIdle = true;
    private boolean isPlayerRunning = false;
    private Characters character;

    public Player(Characters character) {
        playerSprite.setSize(playerTexture.getWidth() * 3, playerTexture.getHeight() * 3);
        playerSprite.setOriginCenter(); // Set the origin to the center for proper rotation
        rect = new CollisionRect(0, 0, playerTexture.getWidth() * 3, playerTexture.getHeight() * 3);
        this.character = character;
    }

    public float getSpeed() {
        return speed;
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    public void setPlayerTexture(Texture playerTexture) {
        this.playerTexture = playerTexture;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(float playerHealth) {
        this.playerHealth = playerHealth;
    }

    public CollisionRect getRect() {
        return rect;
    }

    public void setRect(CollisionRect rect) {
        this.rect = rect;
    }


    public boolean isPlayerIdle() {
        return isPlayerIdle;
    }

    public void setPlayerIdle(boolean playerIdle) {
        isPlayerIdle = playerIdle;
    }

    public boolean isPlayerRunning() {
        return isPlayerRunning;
    }

    public void setPlayerRunning(boolean playerRunning) {
        isPlayerRunning = playerRunning;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public Characters getCharacter() {
        return character;
    }
}
