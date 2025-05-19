package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.untilDawn.models.App;

import java.util.ArrayList;
import java.util.List;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    private final String bullet = "Images/bullet.png";
    private Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
    private Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/single_shot.wav"));
    private int footstepsCounter = 1;

    private List<Sound> footSteps = new ArrayList<>();

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
        return bullet;
    }

    public Animation<Texture> getPlayerRunAnimation() {
        Array<Texture> frames = new Array<>();

        for (int i = 0; i < 4; i++) {
            String framePath = "Images/characters/" + App.getGame().getPlayer().getCharacter().getName() + "/run" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            }
        }

        float FRAME_DURATION = 0.13f;
        return new Animation<>(FRAME_DURATION, frames);
    }

    public Animation<Texture> getPlayerIdleAnimation() {
        Array<Texture> frames = new Array<>();

        for (int i = 0; i < 6; i++) {
            String framePath = "Images/characters/" + App.getGame().getPlayer().getCharacter().getName() + "/idle" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            }
        }

        float FRAME_DURATION = 0.13f;
        return new Animation<>(FRAME_DURATION, frames);
    }

    public Animation<Texture> getPlayerWalkAnimation() {
        String framePath = "Images/characters/" + App.getGame().getPlayer().getCharacter().getName() + "/walk";

        return null;
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
}
