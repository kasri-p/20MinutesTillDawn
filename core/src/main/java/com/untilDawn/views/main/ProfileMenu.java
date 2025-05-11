package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.models.utils.UIHelper;

public class ProfileMenu implements Screen {
    private final TextButton changeUsernameButton;
    private final TextButton changePasswordButton;
    private final TextButton deleteAccountButton;
    private final TextButton changeAvatarButton;

    public Table table;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Stage stage;
    private Image[] leavesDecorations;

    private Label title;

    public ProfileMenu(Skin skin) {
        backgroundTexture = new Texture(Gdx.files.internal("Images/background.png"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        title = new Label("Profile Menu", skin, "title");
        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = new Color(Color.SALMON);
        textOnlyStyle.overFontColor = new Color(Color.SALMON).mul(0.7f);
        textOnlyStyle.downFontColor = new Color(Color.SALMON).mul(0.5f);

        this.changeUsernameButton = new TextButton("Change Username", textOnlyStyle);
        this.changePasswordButton = new TextButton("Change Password", textOnlyStyle);
        this.deleteAccountButton = new TextButton("Delete Account", textOnlyStyle);
        this.changeAvatarButton = new TextButton("Change Avatar", textOnlyStyle);


        this.table = new Table();
    }


    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        leavesDecorations = UIHelper.addLeavesDecoration(stage);
        table.add(title).colspan(2).center().row();
        table.setFillParent(true);
        table.top();
        table.row().pad(100, 0, 20, 0);
        table.add(changeUsernameButton).colspan(2).center();
        table.row().pad(20, 0, 20, 0);
        table.add(changePasswordButton).colspan(2).center();
        table.row().pad(20, 0, 20, 0);
        table.add(deleteAccountButton).colspan(2).center();
        table.row().pad(20, 0, 20, 0);
        table.add(changeAvatarButton).colspan(2).center();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        // Reposition and resize the leaves decorations when the screen is resized
        if (leavesDecorations != null) {
            // Remove old decorations
            leavesDecorations[0].remove();
            leavesDecorations[1].remove();

            // Add new properly sized decorations
            leavesDecorations = UIHelper.addLeavesDecoration(stage);

            leavesDecorations[0].toBack();
            leavesDecorations[1].toBack();
        }
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }


    public Stage getStage() {
        return stage;
    }
}
