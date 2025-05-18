package com.untilDawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.UIHelper;
import com.untilDawn.views.StartMenu;


public class Main extends Game {
    private static Main main;
    private static SpriteBatch batch;
    private Music menuMusic;
    private Sound clickSound;

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static Main getMain() {
        return main;
    }

    @Override
    public void create() {
        main = this;
        batch = new SpriteBatch();
        App.load();

        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("images/cursor.png"));
        Cursor customCursor = Gdx.graphics.newCursor(cursorPixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        cursorPixmap.dispose();

        // Load menu music based on saved settings
        loadMenuMusic();

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        StartMenu startMenu = new StartMenu(GameAssetManager.getGameAssetManager().getSkin());
        setScreen(startMenu);
    }

    private void loadMenuMusic() {
        String musicPath = "sounds/musics/PrettyDungeon.wav";
        String currentTrack = App.getCurrentMusicTrack();

        // Check if we have a custom track saved
        if (currentTrack != null && !currentTrack.equals("Pretty Dungeon")) {
            String customPath = "sounds/musics/" + currentTrack.replace(" ", "") + ".wav";
            if (Gdx.files.internal(customPath).exists()) {
                musicPath = customPath;
            }
        }

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        menuMusic.setLooping(true);
        menuMusic.setVolume(App.getMusicVolume());
        menuMusic.play();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        App.save();
        batch.dispose();
        if (menuMusic != null) {
            menuMusic.dispose();
        }
        if (clickSound != null) {
            clickSound.dispose();
        }
        UIHelper.dispose();
    }

    public Sound getClickSound() {
        if (App.isSFX()) {
            return clickSound;
        } else {
            return null;
        }
    }

    public Music getMenuMusic() {
        return menuMusic;
    }

    public void setMenuMusic(Music music) {
        if (menuMusic != null && menuMusic != music) {
            menuMusic.stop();
            menuMusic.dispose();
        }

        menuMusic = music;

        if (menuMusic != null) {
            if (!menuMusic.isPlaying()) {
                menuMusic.play();
            }
            menuMusic.setLooping(true);
        }
    }
}
