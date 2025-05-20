package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.untilDawn.models.enums.Weapons;

public class Weapon {
    private Texture texture;
    private Weapons weapon;
    private Sprite sprite;
    private int ammo;

    public Weapon(Weapons weapon) {
        this.weapon = weapon;
        texture = new Texture(Gdx.files.internal("Images/weapons/" + weapon.getName().toLowerCase() + "/still.png"));
        this.sprite = new Sprite(new Sprite(texture));
        sprite.setSize(50, 50);
        sprite.setOriginCenter();
        ammo = weapon.getAmmoMax();
    }


    public Sprite getSprite() {
        return sprite;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public Weapons getWeapon() {
        return weapon;
    }

}
