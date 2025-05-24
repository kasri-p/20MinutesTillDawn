package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.LoginMenuController;
import com.untilDawn.models.enums.Language;
import com.untilDawn.models.utils.UIHelper;

public class LoginMenu implements Screen {
    private final Stage stage;
    private final TextField usernameField;
    private final TextField passwordField;
    private final Label errorLabel;
    private final TextButton loginButton;
    private final TextButton registerButton;
    private final TextButton forgotPasswordButton;
    private final Table table;
    private LoginMenuController controller;
    private Image[] leavesDecorations;

    public LoginMenu(Skin skin) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Add leaves decoration
        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = Color.WHITE;
        textOnlyStyle.overFontColor = Color.LIGHT_GRAY;
        textOnlyStyle.downFontColor = Color.GRAY;

        loginButton = new TextButton(Language.Login.getText(), textOnlyStyle);
        registerButton = new TextButton(Language.Register.getText(), textOnlyStyle);
        forgotPasswordButton = new TextButton(Language.ForgotPassword.getText(), textOnlyStyle);

        table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(new Label(Language.Login.getText(), skin, "title")).colspan(2).pad(20).row();
        table.add(new Label(Language.Username.getText(), skin)).pad(10);
        table.add(usernameField).width(200).pad(10).row();
        table.add(new Label(Language.Password.getText(), skin)).pad(10);
        table.add(passwordField).width(200).pad(10).row();
        table.add(errorLabel).colspan(2).pad(10).row();
        table.add(loginButton).width(100).pad(10);
        table.add(registerButton).width(100).pad(10).row();
        table.add(forgotPasswordButton).colspan(2).width(200).pad(10);

        // Make sure the table is on top of the leaves
        stage.addActor(table);
        table.toFront();

        controller = new LoginMenuController(this);
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

        // Reposition and resize the leaves decorations
        if (leavesDecorations != null) {
            leavesDecorations[0].remove();
            leavesDecorations[1].remove();
            leavesDecorations = UIHelper.addLeavesDecoration(stage);

            // Make sure the leaves are behind the table
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
    }

    // Getters for controller access
    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public TextButton getLoginButton() {
        return loginButton;
    }

    public TextButton getRegisterButton() {
        return registerButton;
    }

    public TextButton getForgotPasswordButton() {
        return forgotPasswordButton;
    }

    public Stage getStage() {
        return stage;
    }
}
