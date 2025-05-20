package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Bullet;
import com.untilDawn.models.Weapon;
import com.untilDawn.models.enums.Weapons;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.Iterator;

public class WeaponController {
    private Weapon weapon;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private float screenCenterX;
    private float screenCenterY;
    private PlayerController playerController;

    private boolean isReloading = false;
    private float reloadTimer = 0;
    private float reloadDuration = 1.0f; // 1 second reload time by default

    public WeaponController(Weapon weapon) {
        this.weapon = weapon;
        updateScreenCenter();

        if (weapon.getWeapon() != null) {
            this.reloadDuration = weapon.getWeapon().getReloadTime();
        }
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        updateScreenCenter();

        if (playerController != null) {
            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            weapon.getSprite().setPosition(
                playerX - weapon.getSprite().getWidth() / 2,
                playerY - weapon.getSprite().getHeight() / 2
            );
        }

        updateReloading(deltaTime);

        weapon.getSprite().draw(Main.getBatch());

        updateBullets();
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    private void updateReloading(float deltaTime) {
        if (isReloading) {
            reloadTimer += deltaTime;

            // Check if reload is complete
            if (reloadTimer >= reloadDuration) {
                completeReload();
            }
        }
    }

    private void completeReload() {
        isReloading = false;
        reloadTimer = 0;

        // Reset ammo to maximum
        if (weapon.getWeapon() != null) {
            weapon.setAmmo(weapon.getWeapon().getAmmoMax());
        } else {
            weapon.setAmmo(30); // Default ammo if weapon type is not set
        }
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
        if (isReloading || weapon.getAmmo() <= 0) {
            // If out of ammo, automatically start reloading
            if (weapon.getAmmo() <= 0 && !isReloading) {
                startReload();
            }
            return;
        }

        GameAssetManager.getGameAssetManager().playShot();

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        int projectileCount = 1;
        if (weapon.getWeapon() != null) {
            projectileCount = weapon.getWeapon().getProjectileCount();
        }

        for (int i = 0; i < projectileCount; i++) {
            Bullet newBullet = new Bullet((int) playerX, (int) playerY);

            Vector2 direction = new Vector2(x - playerX, y - playerY).nor();

            if (projectileCount > 1) {
                float spreadAngle = 15f;
                float angle = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));

                // Calculate spread based on projectile index
                float bulletAngle = angle + (i - (projectileCount - 1) / 2f) * (spreadAngle / (projectileCount - 1));

                // Convert back to radians and create new direction vector
                float radians = (float) Math.toRadians(bulletAngle);
                direction = new Vector2((float) Math.cos(radians), (float) Math.sin(radians));
            }

            newBullet.setDirection(direction);

            bullets.add(newBullet);

            newBullet.getSprite().setPosition(
                playerX - newBullet.getSprite().getWidth() / 2,
                playerY - newBullet.getSprite().getHeight() / 2
            );
        }

        // Reduce ammo
        weapon.setAmmo(weapon.getAmmo() - 1);
    }

    public void startReload() {
        if (!isReloading && weapon.getAmmo() < weapon.getWeapon().getAmmoMax()) {
            isReloading = true;
            reloadTimer = 0;

            GameAssetManager.getGameAssetManager().playReloadSound();
        }
    }

    public void cancelReload() {
        if (isReloading) {
            isReloading = false;
            reloadTimer = 0;
        }
    }

    public void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            // Skip or remove inactive bullets
            if (!bullet.isActive()) {
                iterator.remove();
                continue;
            }

            // Draw the bullet
            bullet.getSprite().draw(Main.getBatch());

            // Update bullet position
            float speed = 10.0f; // Base bullet speed

            // Adjust speed based on weapon type if needed
            if (weapon.getWeapon() == Weapons.Shotgun) {
                speed = 8.0f; // Shotgun bullets are slower
            } else if (weapon.getWeapon() == Weapons.Dual_Smg) {
                speed = 12.0f; // SMG bullets are faster
            }

            bullet.getSprite().setX(bullet.getSprite().getX() + bullet.getDirection().x * speed);
            bullet.getSprite().setY(bullet.getSprite().getY() + bullet.getDirection().y * speed);

            // Check if bullet is too far from player
            float playerX = 0;
            float playerY = 0;

            if (playerController != null) {
                playerX = playerController.getPlayer().getPosX();
                playerY = playerController.getPlayer().getPosY();
            }

            if (isBulletTooFar(bullet, playerX, playerY)) {
                bullet.setActive(false);
                iterator.remove();
            }
        }
    }

    //TODO: Check if we need to auto-reload
    public void checkAutoReload() {
        if (weapon.getAmmo() <= 0 && App.isAutoReloadEnabled() && !isReloading) {
            startReload();
        }
    }

    private boolean isBulletTooFar(Bullet bullet, float playerX, float playerY) {
        float bulletX = bullet.getSprite().getX();
        float bulletY = bullet.getSprite().getY();

        float distanceSquared = (bulletX - playerX) * (bulletX - playerX) +
            (bulletY - playerY) * (bulletY - playerY);

        float maxDistanceSquared = 1000 * 1000; // 1000 pixels max distance

        return distanceSquared > maxDistanceSquared;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;

        // Update reload duration
        if (weapon.getWeapon() != null) {
            this.reloadDuration = weapon.getWeapon().getReloadTime();
        }
    }

    public void handleResize(int width, int height) {
        updateScreenCenter();
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadProgress() {
        if (!isReloading) return 0;
        return reloadTimer / reloadDuration;
    }

    public void dispose() {
        // Clear all bullets
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();
    }
}
