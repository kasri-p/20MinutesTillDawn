package com.untilDawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untilDawn.controllers.MainMenuController;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.MainMenu;


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

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/musics/PrettyDungeon.wav"));
        menuMusic.setLooping(true);
        menuMusic.play(); // Start playing the music

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        MainMenuController controller = new MainMenuController();
        MainMenu mainMenu = new MainMenu(controller, GameAssetManager.getGameAssetManager().getSkin());
        controller.setView(mainMenu);
        setScreen(mainMenu);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        App.save();
        batch.dispose();
    }

    public Sound getClickSound() {
        return clickSound;
    }
}
