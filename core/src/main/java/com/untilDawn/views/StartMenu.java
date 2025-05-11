package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.StartMenuController;

public class StartMenu implements Screen {
    private final TextButton startButton;
    private final TextButton quitButton;
    private final Label gameTitle;
    private final Sound clickSound;
    public Table table;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Stage stage;

    public StartMenu(StartMenuController controller, Skin skin) {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        backgroundTexture = new Texture(Gdx.files.internal("Images/background.png"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = new Color(Color.SALMON);
        textOnlyStyle.overFontColor = new Color(Color.SALMON).mul(0.7f);
        textOnlyStyle.downFontColor = new Color(Color.SALMON).mul(0.5f);

        this.startButton = new TextButton("Start", textOnlyStyle);
        this.quitButton = new TextButton("Quit", textOnlyStyle);
        this.gameTitle = new Label("20 Minutes Till Dawn", skin, "title");

        this.table = new Table();
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        gameTitle.setFontScale(0.8f);
        table.setFillParent(true);
        table.top();

        table.add(gameTitle)
            .padTop(100)
            .colspan(2)
            .center()
            .row();
        table.row();

        table.add(startButton)
            .colspan(2)
            .center()
            .padTop(200)
            .row();

        table.add(quitButton)
            .colspan(2)
            .center()
            .padTop(50)
            .row();

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().draw(backgroundTexture, 0, 0, stage.getWidth(), stage.getHeight());
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
//        stage.getViewport().update(width, height, true);
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

    public TextButton getStartButton() {
        return startButton;
    }

    public TextButton getQuitButton() {
        return quitButton;
    }

    public Stage getStage() {
        return stage;
    }
}
