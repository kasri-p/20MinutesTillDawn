package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.SignUpMenuController;
import com.untilDawn.models.enums.Language;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.UIHelper;

public class SignUpMenu implements Screen {
    private final Stage stage;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField securityAnswerField;
    private final Label errorLabel;
    private final TextButton signUpButton;
    private final TextButton skipButton;
    private final TextButton backButton;
    private final Table table;
    private final Sound clickSound;

    private Image[] leavesDecorations;

    public SignUpMenu(Skin skin) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        // Create minimal text field style with just an underline
        TextField.TextFieldStyle underlineStyle = new TextField.TextFieldStyle();
        underlineStyle.font = GameAssetManager.getGameAssetManager().getChevyRayFont();
        underlineStyle.fontColor = Color.WHITE;
        underlineStyle.cursor = skin.newDrawable("white", Color.SALMON);

        underlineStyle.background = null;
        underlineStyle.focusedBackground = null;
        underlineStyle.disabledBackground = null;

        usernameField = new TextField("", underlineStyle);
        passwordField = new TextField("", underlineStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        securityAnswerField = new TextField("", underlineStyle);

        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = GameAssetManager.getGameAssetManager().getChevyRayFont();
        textOnlyStyle.fontColor = Color.SALMON;
        textOnlyStyle.overFontColor = Color.LIGHT_GRAY;
        textOnlyStyle.downFontColor = Color.GRAY;

        signUpButton = new TextButton(Language.SignUp.getText(), textOnlyStyle);
        skipButton = new TextButton(Language.GuestLogin.getText(), textOnlyStyle);
        backButton = new TextButton(Language.Login.getText(), textOnlyStyle);

        table = new Table();
        table.setFillParent(true);
        table.center();

        Label.LabelStyle labelStyle = GameAssetManager.getGameAssetManager().getChevyRayLabelStyle();
        table.add(new Label(Language.SignUp.getText(), skin, "title")).colspan(2).pad(20).row();
        table.add(new Label(Language.Username.getText(), labelStyle)).pad(10);
        table.add(usernameField).prefWidth(300).pad(10);
        table.row();
        table.add(new Label(Language.Password.getText(), labelStyle)).pad(10);
        table.add(passwordField).prefWidth(300).pad(10);
        table.row();
        table.add(errorLabel).colspan(2).pad(10);
        table.row();
        table.add(signUpButton).width(100).pad(10);
        table.add(skipButton).width(150).pad(10);
        table.row();
        table.add(backButton).colspan(2).width(100).pad(10);

        stage.addActor(table);
        table.toFront();

        SignUpMenuController controller = new SignUpMenuController(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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

        if (leavesDecorations != null) {
            leavesDecorations[0].remove();
            leavesDecorations[1].remove();
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
        stage.dispose();
        clickSound.dispose();
    }

    public TextButton getSignUpButton() {
        return signUpButton;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    public TextField getSecurityAnswerField() {
        return securityAnswerField;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public TextButton getSkipButton() {
        return skipButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Stage getStage() {
        return stage;
    }
}
