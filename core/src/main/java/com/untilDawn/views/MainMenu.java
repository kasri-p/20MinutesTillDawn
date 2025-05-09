package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.MainMenuController;

public class MainMenu implements Screen {
    private final TextButton playButton;
    private final TextButton quitButton;
    private final TextButton settingsButton;
    private final Label gameTitle;
    private final Sound clickSound;
    public Table table;
    private Stage stage;

    public MainMenu(MainMenuController controller, Skin skin) {

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = new Color(Color.SALMON);
        textOnlyStyle.overFontColor = new Color(Color.SALMON).mul(0.7f);
        textOnlyStyle.downFontColor = new Color(Color.SALMON).mul(0.5f);

        this.playButton = new TextButton("Play", textOnlyStyle);
        this.quitButton = new TextButton("Quit", textOnlyStyle);
        this.settingsButton = new TextButton("Settings", textOnlyStyle);
        this.gameTitle = new Label("20 Minutes\n Till Dawn", skin);

        this.table = new Table();
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();
        table.add(gameTitle);
        table.row().pad(10, 0, 10, 0);
        table.add(playButton).width(150).height(70);
        table.row().pad(10, 0, 10, 0);
        table.add(settingsButton).width(150).height(70);
        table.row().pad(10, 0, 10, 0);
        table.add(quitButton).width(150).height(70);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }

    public TextButton getPlayButton() {
        return playButton;
    }

    public TextButton getQuitButton() {
        return quitButton;
    }

    public TextButton getSettingsButton() {
        return settingsButton;
    }

}
