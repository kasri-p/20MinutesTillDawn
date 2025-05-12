package com.untilDawn.models.enums;


public enum Weapons {
    Revolver("Revolver", 6, 1, 1, 20),
    Shotgun("Shotgun", 2, 1, 4, 10),
    Dual_Smg("Dual SMGs", 24, 2, 1, 8);

    private final String name;
    private final int ammoMax;
    private final int reloadTime;
    private final int projectileCount;
    private final int damage;

    private int currentAmmo;

    private boolean reloading;

    Weapons(String name, int ammoMax, int reloadTime, int projectileCount, int damage) {
        this.name = name;
        this.ammoMax = ammoMax;
        this.reloadTime = reloadTime;
        this.projectileCount = projectileCount;
        this.damage = damage;
        this.currentAmmo = ammoMax;
        this.reloading = false;
    }

    public String getName() {
        return name;
    }

    public int getAmmoMax() {
        return ammoMax;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getProjectileCount() {
        return projectileCount;
    }

    public int getDamage() {
        return damage;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = Math.min(currentAmmo, ammoMax);
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }


    public boolean fire() {
        if (currentAmmo > 0 && !reloading) {
            currentAmmo--;
            return true;
        }
        return false;
    }


    public void reload() {
        currentAmmo = ammoMax;
    }

    public Weapons reset() {
        Weapons newWeapon = valueOf(this.name());
        newWeapon.currentAmmo = newWeapon.ammoMax;
        newWeapon.reloading = false;
        return newWeapon;
    }
}
