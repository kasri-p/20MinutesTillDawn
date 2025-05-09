package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.LoginMenuController;

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

    public LoginMenu(Skin skin) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

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

        loginButton = new TextButton("Login", textOnlyStyle);
        registerButton = new TextButton("Register", textOnlyStyle);
        forgotPasswordButton = new TextButton("Forgot Password?", textOnlyStyle);

        table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(new Label("Login", skin, "title")).colspan(2).pad(20).row();
        table.add(new Label("Username:", skin)).pad(10);
        table.add(usernameField).width(200).pad(10).row();
        table.add(new Label("Password:", skin)).pad(10);
        table.add(passwordField).width(200).pad(10).row();
        table.add(errorLabel).colspan(2).pad(10).row();
        table.add(loginButton).width(100).pad(10);
        table.add(registerButton).width(100).pad(10).row();
        table.add(forgotPasswordButton).colspan(2).width(200).pad(10);

        stage.addActor(table);

        controller = new LoginMenuController(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
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
