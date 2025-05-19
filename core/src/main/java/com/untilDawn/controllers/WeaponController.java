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

        // Convert screen coordinates to world coordinates
        // This is necessary because the mouse coordinates are in screen space
        // but we need them in world space relative to the camera
        float worldX = x - screenCenterX;
        float worldY = screenCenterY - y; // Y is inverted in screen coordinates

        // Calculate angle between cursor and center (which is now at 0,0 in world coordinates)
        float angle = (float) Math.atan2(worldY, worldX);

        // Convert to degrees and rotate the weapon
        weaponSprite.setRotation((float) Math.toDegrees(angle) - 90);

        // Make sure weapon stays centered regardless of rotation
        ensureWeaponCentered();
    }

    private void ensureWeaponCentered() {
        // Position the weapon at the origin (0,0) which will be the center
        // of the camera's view since the camera is following the player
        weapon.getSprite().setPosition(
            -weapon.getSprite().getWidth() / 2,
            -weapon.getSprite().getHeight() / 2
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

            // Calculate direction vector from center (0,0 in world coordinates) to target
            Vector2 direction = new Vector2(
                bullet.getX() - screenCenterX,
                screenCenterY - bullet.getY()  // Y is inverted in screen coordinates
            ).nor();

            // Update bullet position
            bullet.getSprite().setX(bullet.getSprite().getX() + direction.x * 5);
            bullet.getSprite().setY(bullet.getSprite().getY() + direction.y * 5);

            // Remove bullets that have gone too far from the player
            if (isBulletTooFar(bullet)) {
                iterator.remove();
            }
        }
    }

    private boolean isBulletTooFar(Bullet bullet) {
        // Check if bullet has gone too far from the player
        float bulletX = bullet.getSprite().getX();
        float bulletY = bullet.getSprite().getY();

        // Calculate distance from origin (player position)
        float distanceSquared = bulletX * bulletX + bulletY * bulletY;

        // Set a maximum distance - bullets disappear after this range
        float maxDistanceSquared = 1000 * 1000; // 1000 pixels range

        return distanceSquared > maxDistanceSquared;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void handleResize(int width, int height) {
        updateScreenCenter();
        ensureWeaponCentered();
    }
}
