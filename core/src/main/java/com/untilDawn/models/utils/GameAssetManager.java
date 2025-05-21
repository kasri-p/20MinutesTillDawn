package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.untilDawn.models.App;
import com.untilDawn.models.enums.Weapons;

import java.util.ArrayList;
import java.util.List;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    private final Sound reloadSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/reload.wav"));
    private final Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/single_shot.wav"));
    private final List<Sound> footSteps = new ArrayList<>();

    private final Texture reloadBarBg = new Texture(Gdx.files.internal("Images/reload/ReloadBar_0.png"));
    private final Texture reloadBarFill = new Texture(Gdx.files.internal("Images/reload/ReloadBar_1.png"));

    private final ObjectMap<String, Animation<Texture>> enemyAnimationCache = new ObjectMap<>();

    private final ObjectMap<String, Animation<Texture>> playerRunAnimationCache = new ObjectMap<>();
    private final ObjectMap<String, Animation<Texture>> playerIdleAnimationCache = new ObjectMap<>();

    private final ObjectMap<String, Animation<Texture>> weaponReloadAnimationCache = new ObjectMap<>();

    private int footstepsCounter = 1;

    private Texture muzzleFlash = new Texture(Gdx.files.internal("Images/muzzleFlash.png"));


    GameAssetManager() {
//        footSteps.add(Gdx.audio.newSound(Gdx.files.internal("sounds/effects/footstep1.wav")));
//        footSteps.add(Gdx.audio.newSound(Gdx.files.internal("sounds/effects/footstep2.wav")));
//        footSteps.add(Gdx.audio.newSound(Gdx.files.internal("sounds/effects/footstep3.wav")));
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }
        return gameAssetManager;
    }

    public Skin getSkin() {
        return skin;
    }

    public String getBullet() {
        return "Images/bullet.png";
    }

    public Animation<Texture> getPlayerRunAnimation() {
        String characterName = App.getGame().getPlayer().getCharacter().getName();
        String cacheKey = characterName + "_run";

        if (playerRunAnimationCache.containsKey(cacheKey)) {
            return playerRunAnimationCache.get(cacheKey);
        }

        Array<Texture> frames = new Array<>();
        for (int i = 0; i < 4; i++) {
            String framePath = "Images/characters/" + characterName + "/run" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            }
        }

        float FRAME_DURATION = 0.13f;
        Animation<Texture> animation = new Animation<>(FRAME_DURATION, frames);
        playerRunAnimationCache.put(cacheKey, animation);
        return animation;
    }

    public Animation<Texture> getPlayerIdleAnimation() {
        String characterName = App.getGame().getPlayer().getCharacter().getName();
        String cacheKey = characterName + "_idle";

        if (playerIdleAnimationCache.containsKey(cacheKey)) {
            return playerIdleAnimationCache.get(cacheKey);
        }

        Array<Texture> frames = new Array<>();
        for (int i = 0; i < 6; i++) {
            String framePath = "Images/characters/" + characterName + "/idle" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            }
        }

        float FRAME_DURATION = 0.13f;
        Animation<Texture> animation = new Animation<>(FRAME_DURATION, frames);
        playerIdleAnimationCache.put(cacheKey, animation);
        return animation;
    }

    public Animation<Texture> getWeaponReloadAnimation(Weapons weapon) {
        String weaponName = weapon.getName().replaceAll("\\s+", "");
        String cacheKey = weaponName + "_reload";

        if (weaponReloadAnimationCache.containsKey(cacheKey)) {
            return weaponReloadAnimationCache.get(cacheKey);
        }

        Array<Texture> frames = new Array<>();
        for (int i = 0; i < 3; i++) {
            String framePath = "Images/weapons/" + weaponName + "/reload" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            } else {
                Gdx.app.log("GameAssetManager", "Weapon reload texture not found: " + framePath);
            }
        }

        if (frames.size == 0) {
            return null;
        }

        // Adjust duration based on weapon reload time for smoother animations
        float frameDuration = weapon.getReloadTime() / frames.size;
        Animation<Texture> animation = new Animation<>(frameDuration, frames);
        weaponReloadAnimationCache.put(cacheKey, animation);
        return animation;
    }

    public void playShot() {
        if (App.isSFX()) {
            shootSound.play();
        }
    }

    public void playFootStep() {
        if (App.isSFX()) {
            footSteps.get(footstepsCounter).play();
            footstepsCounter++;
            if (footstepsCounter == footSteps.size()) {
                footstepsCounter = 0;
            }
        }
    }

    public void playReloadSound() {
        if (App.isSFX()) {
            reloadSound.play();
        }
    }

    public Animation<Texture> getEnemyAnimation(String enemyName) {
        if (enemyAnimationCache.containsKey(enemyName)) {
            return enemyAnimationCache.get(enemyName);
        }

        Array<Texture> frames = new Array<>();
        for (int i = 0; i < 4; i++) {
            String framePath = "Images/Enemies/" + enemyName.toLowerCase() + "/" + enemyName.toLowerCase() + i + ".png";
            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            } else {
                Gdx.app.log("GameAssetManager", "Enemy texture not found: " + framePath);
            }
        }

        if (frames.size == 0) {
            return null;
        }

        float animationDuration = enemyName.equalsIgnoreCase("tree") ? 5f : 0.3f;
        Animation<Texture> animation = new Animation<>(animationDuration, frames);
        enemyAnimationCache.put(enemyName, animation);
        return animation;
    }

    public void dispose() {
        if (skin != null) {
            skin.dispose();
        }

        if (reloadSound != null) {
            reloadSound.dispose();
        }

        if (shootSound != null) {
            shootSound.dispose();
        }

        for (Sound sound : footSteps) {
            if (sound != null) {
                sound.dispose();
            }
        }

        for (Animation<Texture> animation : enemyAnimationCache.values()) {
            if (animation != null) {
                for (Texture texture : animation.getKeyFrames()) {
                    if (texture != null) {
                        texture.dispose();
                    }
                }
            }
        }
        enemyAnimationCache.clear();

        for (Animation<Texture> animation : playerRunAnimationCache.values()) {
            if (animation != null) {
                for (Texture texture : animation.getKeyFrames()) {
                    if (texture != null) {
                        texture.dispose();
                    }
                }
            }
        }
        playerRunAnimationCache.clear();

        // Dispose player idle animations
        for (Animation<Texture> animation : playerIdleAnimationCache.values()) {
            if (animation != null) {
                for (Texture texture : animation.getKeyFrames()) {
                    if (texture != null) {
                        texture.dispose();
                    }
                }
            }
        }
        playerIdleAnimationCache.clear();

        for (Animation<Texture> animation : weaponReloadAnimationCache.values()) {
            if (animation != null) {
                for (Texture texture : animation.getKeyFrames()) {
                    if (texture != null) {
                        texture.dispose();
                    }
                }
            }
        }
        weaponReloadAnimationCache.clear();

        if (muzzleFlash != null) {
            muzzleFlash.dispose();
            muzzleFlash = null;
        }

    }

    public Texture getReloadBarBg() {
        return reloadBarBg;
    }

    public Texture getReloadBarFill() {
        return reloadBarFill;
    }

    public Texture getMuzzleFlash() {
        return muzzleFlash;
    }
}
