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
    private final List<Sound> footSteps = new ArrayList<>();

    private final Texture ammoIcon = new Texture(Gdx.files.internal("Images/AmmoIcon.png"));
    private final Texture reloadBarBg = new Texture(Gdx.files.internal("Images/reload/ReloadBar_0.png"));
    private final Texture reloadBarFill = new Texture(Gdx.files.internal("Images/reload/ReloadBar_1.png"));

    private final ObjectMap<String, Animation<Texture>> enemyAnimationCache = new ObjectMap<>();

    private final ObjectMap<String, Animation<Texture>> playerRunAnimationCache = new ObjectMap<>();
    private final ObjectMap<String, Animation<Texture>> playerIdleAnimationCache = new ObjectMap<>();
    private final ObjectMap<String, Animation<Texture>> weaponReloadAnimationCache = new ObjectMap<>();

    private final Sound bloodSplash = Gdx.audio.newSound(Gdx.files.internal("Sounds/effects/bloodSplash.wav"));
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/single_shot.wav"));
    private final Sound obtainSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/ObtainPoints.wav"));
    private final Sound batDeathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/batDeath.wav"));
    private final Sound levelUpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/LevelUp.wav"));

    private final Texture heartAnimation0 = new Texture(Gdx.files.internal("Images/HeartAnimation/HeartAnimation_0.png"));
    private final Texture heartAnimation1 = new Texture(Gdx.files.internal("Images/HeartAnimation/HeartAnimation_1.png"));
    private final Texture heartAnimation2 = new Texture(Gdx.files.internal("Images/HeartAnimation/HeartAnimation_2.png"));

    private final Texture levelUpAnimation1 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp1.png"));
    private final Texture levelUpAnimation2 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp2.png"));
    private final Texture levelUpAnimation3 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp3.png"));
    private final Texture levelUpAnimation4 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp4.png"));
    private final Texture levelUpAnimation5 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp5.png"));
    private final Texture levelUpAnimation6 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp6.png"));
    private final Texture levelUpAnimation7 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp7.png"));
    private final Texture levelUpAnimation8 = new Texture(Gdx.files.internal("Images/LevelUpAnimation/LevelUp8.png"));

    private final Animation<Texture> levelUpAnimation = new Animation<>(0.25f, levelUpAnimation1, levelUpAnimation2, levelUpAnimation3, levelUpAnimation4, levelUpAnimation5, levelUpAnimation6, levelUpAnimation7, levelUpAnimation8);

    private int footstepsCounter = 1;
    private Texture muzzleFlash = new Texture(Gdx.files.internal("Images/muzzleFlash.png"));
    private Animation<Texture> heartAnimation = new Animation<>(0.5f, heartAnimation0, heartAnimation1, heartAnimation2);
    private Texture panel = new Texture(Gdx.files.internal("Images/panel.png"));

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

    public Texture getPanel() {
        return panel;
    }

    public Texture getAmmoIcon() {
        return ammoIcon;
    }

    public Animation<Texture> getLevelUpAnimation() {
        return levelUpAnimation;
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
        String weaponName = weapon.getName().replaceAll("\\s+", "").toLowerCase();
        String cacheKey = weaponName + "_reload";

        if (weaponReloadAnimationCache.containsKey(cacheKey)) {
            return weaponReloadAnimationCache.get(cacheKey);
        }

        Array<Texture> frames = new Array<>();

        for (int i = 0; i < 4; i++) {
            String framePath = "Images/weapons/" + weaponName + "/reload" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                try {
                    Texture frameTex = new Texture(Gdx.files.internal(framePath));
                    frames.add(frameTex);
                } catch (Exception e) {
                    Gdx.app.error("GameAssetManager", "Error loading weapon reload texture: " + framePath + ", " + e.getMessage());
                }
            } else {
                Gdx.app.log("GameAssetManager", "Weapon reload texture not found: " + framePath);
            }
        }

        if (frames.size == 0) {
            return null;
        }

        float frameDuration = weapon.getReloadTime() / (float) frames.size;
        Animation<Texture> animation = new Animation<>(frameDuration, frames);
        weaponReloadAnimationCache.put(cacheKey, animation);
        return animation;
    }

    public Animation<Texture> getHeartAnimation() {
        return heartAnimation;
    }

    public void playShot() {
        if (App.isSFX()) {
            shootSound.play();
        }
    }

    public void playReloadSound() {
        if (App.isSFX()) {
            reloadSound.play();
        }
    }

    public void playSplash() {
        if (App.isSFX()) {
            bloodSplash.play();
        }
    }

    public void playObtain() {
        if (App.isSFX()) {
            obtainSound.play();
        }
    }

    public void playBatDeath() {
        if (App.isSFX()) {
            batDeathSound.play();
        }
    }

    public void playLevelUp() {
        if (App.isSFX()) {
            levelUpSound.play();
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


        enemyAnimationCache.clear();
        playerRunAnimationCache.clear();
        playerIdleAnimationCache.clear();
        weaponReloadAnimationCache.clear();

        if (muzzleFlash != null) {
            muzzleFlash.dispose();
            muzzleFlash = null;
        }

        if (reloadBarBg != null) {
            reloadBarBg.dispose();
        }

        if (reloadBarFill != null) {
            reloadBarFill.dispose();
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
