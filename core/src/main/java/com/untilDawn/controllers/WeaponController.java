package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.Main;
import com.untilDawn.models.Bullet;
import com.untilDawn.models.Weapon;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.Iterator;

public class WeaponController {
    private Weapon weapon;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private float screenCenterX;
    private float screenCenterY;
    private PlayerController playerController;

    public WeaponController(Weapon weapon) {
        this.weapon = weapon;
        updateScreenCenter();
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void update() {
        // Update screen center values in case of resize
        updateScreenCenter();

        // Position the weapon at the player's position
        if (playerController != null) {
            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            weapon.getSprite().setPosition(
                playerX - weapon.getSprite().getWidth() / 2,
                playerY - weapon.getSprite().getHeight() / 2
            );
        }

        weapon.getSprite().draw(Main.getBatch());

        updateBullets();
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    public void handleWeaponRotation(int x, int y) {
        Sprite weaponSprite = weapon.getSprite();

        float playerX = 0;
        float playerY = 0;

        if (playerController != null) {
            playerX = playerController.getPlayer().getPosX();
            playerY = playerController.getPlayer().getPosY();
        }

        float angle = (float) Math.atan2(y - playerY, x - playerX);
        float degrees = (float) Math.toDegrees(angle);

        boolean shouldFlip = (degrees > 90 && degrees < 270) || (degrees < -90 && degrees > -270);

        if (weaponSprite.isFlipY() != shouldFlip) {
            weaponSprite.setFlip(false, shouldFlip);
        }

        weaponSprite.setRotation(degrees);
    }

    public void handleWeaponShoot(int x, int y) {
        GameAssetManager.getGameAssetManager().playShot();

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        Bullet newBullet = new Bullet((int) playerX, (int) playerY);

        Vector2 direction = new Vector2(
            x - playerX,
            y - playerY
        ).nor();

        newBullet.setDirection(direction);
        bullets.add(newBullet);
        weapon.setAmmo(weapon.getAmmo() - 1);


        if (!bullets.isEmpty()) {
            Bullet bullet = bullets.get(bullets.size() - 1);
            bullet.getSprite().setPosition(
                playerX - bullet.getSprite().getWidth() / 2,
                playerY - bullet.getSprite().getHeight() / 2
            );
        }
    }

    public void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            bullet.getSprite().draw(Main.getBatch());

            bullet.getSprite().setX(bullet.getSprite().getX() + bullet.getDirection().x * 5);
            bullet.getSprite().setY(bullet.getSprite().getY() + bullet.getDirection().y * 5);

            float playerX = 0;
            float playerY = 0;

            if (playerController != null) {
                playerX = playerController.getPlayer().getPosX();
                playerY = playerController.getPlayer().getPosY();
            }

            if (isBulletTooFar(bullet, playerX, playerY)) {
                iterator.remove();
            }
        }
    }

    private boolean isBulletTooFar(Bullet bullet, float playerX, float playerY) {
        float bulletX = bullet.getSprite().getX();
        float bulletY = bullet.getSprite().getY();

        float distanceSquared = (bulletX - playerX) * (bulletX - playerX) +
            (bulletY - playerY) * (bulletY - playerY);

        float maxDistanceSquared = 1000 * 1000;

        return distanceSquared > maxDistanceSquared;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void handleResize(int width, int height) {
        updateScreenCenter();
    }
}
