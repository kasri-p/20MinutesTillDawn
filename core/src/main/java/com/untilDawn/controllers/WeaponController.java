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

    public WeaponController(Weapon weapon) {
        this.weapon = weapon;
        updateScreenCenter();
        ensureWeaponCentered();
    }

    public void update() {
        // Update screen center values in case of resize
        updateScreenCenter();

        // Draw the weapon sprite
        weapon.getSprite().draw(Main.getBatch());

        // Update and handle bullets
        updateBullets();
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    public void handleWeaponRotation(int x, int y) {
        Sprite weaponSprite = weapon.getSprite();

        // Calculate angle between cursor and center of screen
        float angle = (float) Math.atan2(y - screenCenterY, x - screenCenterX);

        // Convert to degrees and rotate the weapon
        weaponSprite.setRotation((float) (Math.toDegrees(angle) - 90));  // Adjusted for orientation

        // Make sure weapon stays centered regardless of rotation
        ensureWeaponCentered();
    }

    private void ensureWeaponCentered() {
        weapon.getSprite().setPosition(
            screenCenterX - weapon.getSprite().getWidth() / 2,
            screenCenterY - weapon.getSprite().getHeight() / 2
        );
    }

    public void handleWeaponShoot(int x, int y) {
        GameAssetManager.getGameAssetManager().playShot();
        bullets.add(new Bullet(x, y));
        weapon.setAmmo(weapon.getAmmo() - 1);
    }

    public void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            // Draw the bullet
            bullet.getSprite().draw(Main.getBatch());

            // Calculate direction vector from center of screen to target
            Vector2 direction = new Vector2(
                screenCenterX - bullet.getX(),
                screenCenterY - bullet.getY()
            ).nor();

            // Update bullet position
            bullet.getSprite().setX(bullet.getSprite().getX() - direction.x * 5);
            bullet.getSprite().setY(bullet.getSprite().getY() + direction.y * 5);

            // Remove bullets that have gone off screen
            if (isOffScreen(bullet)) {
                iterator.remove();
            }
        }
    }

    private boolean isOffScreen(Bullet bullet) {
        float bulletX = bullet.getSprite().getX();
        float bulletY = bullet.getSprite().getY();
        float bulletWidth = bullet.getSprite().getWidth();
        float bulletHeight = bullet.getSprite().getHeight();

        return bulletX + bulletWidth < 0 ||
            bulletX > Gdx.graphics.getWidth() ||
            bulletY + bulletHeight < 0 ||
            bulletY > Gdx.graphics.getHeight();
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void handleResize(int width, int height) {
        updateScreenCenter();
        ensureWeaponCentered();
    }
}
