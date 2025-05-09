package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.MainMenuController;
import com.untilDawn.controllers.PreGameMeuController;

public class MainMenu implements Screen {
    private Stage stage;
    private final TextButton playButton;
    private final TextButton quitButton;
    private final TextButton settingsButton;
    private final Label gameTitle;
    public Table table;
    private final MainMenuController controller;

    public MainMenu(MainMenuController controller, Skin skin) {
        this.controller = controller;
        this.playButton = new TextButton("Play", skin);
        this.quitButton = new TextButton("Quit", skin);
        this.settingsButton = new TextButton("Settings", skin);
        this.gameTitle = new Label("20 Minutes\nTill Dawn", skin);

        this.table = new Table();
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();
        table.row().pad(10, 0, 10, 0);
        table.add(gameTitle);
        table.row().pad(10, 0, 10, 0);
        table.add(playButton);
        table.row().pad(10, 0, 10, 0);
        table.add(quitButton);
        table.row().pad(10, 0, 10, 0);
        table.add(settingsButton);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        controller.handleMainMenuButtons();
    }

    @Override
    public void resize(int width, int height) {

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
