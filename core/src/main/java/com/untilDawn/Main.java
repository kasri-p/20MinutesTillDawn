package com.untilDawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untilDawn.controllers.StartMenuController;
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

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/musics/PrettyDungeon.wav"));
        menuMusic.setLooping(true);
        menuMusic.play();

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        StartMenuController controller = new StartMenuController();
        StartMenu startMenu = new StartMenu(controller, GameAssetManager.getGameAssetManager().getSkin());
        controller.setView(startMenu);
        setScreen(startMenu);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        App.save();
        batch.dispose();
        UIHelper.dispose();
    }

    public Sound getClickSound() {
        if (App.isSFX()) {
            return clickSound;
        } else {
            return null;
        }
    }
}
