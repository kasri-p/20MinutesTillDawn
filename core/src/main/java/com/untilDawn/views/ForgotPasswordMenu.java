package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.models.User;

public class ForgotPasswordMenu implements Screen {
    private final Stage stage;
    private final TextField usernameField;
    private final TextField securityAnswerField;
    private final TextField newPasswordField;
    private final TextField confirmPasswordField;
    private final Label messageLabel;
    private final TextButton resetButton;
    private final TextButton backButton;
    private final Table table;
    private Label securityQuestionLabel;
    private User user;
    private String[] securityQuestions = {
        "What was your first pet's name?",
        "What was the name of your first school?",
        "What is your mother's maiden name?",
        "What city were you born in?",
        "What was your childhood nickname?"
    };

    public ForgotPasswordMenu(Skin skin) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        usernameField = new TextField("", skin);
        securityAnswerField = new TextField("", skin);
        newPasswordField = new TextField("", skin);
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');
        confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');

        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);

        securityQuestionLabel = new Label("Enter your username to see your security question", skin);

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = Color.WHITE;
        textOnlyStyle.overFontColor = Color.LIGHT_GRAY;
        textOnlyStyle.downFontColor = Color.GRAY;

        resetButton = new TextButton("Reset Password", textOnlyStyle);
        backButton = new TextButton("Back", textOnlyStyle);

        TextButton findUserButton = new TextButton("Find User", textOnlyStyle);

        table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(new Label("Reset Password", skin, "title")).colspan(2).padBottom(20).row();
        table.add(new Label("Username:", skin)).padRight(10);
        table.add(usernameField).width(200).padBottom(10).row();
        table.add(findUserButton).colspan(2).padBottom(20).row();

        table.add(securityQuestionLabel).colspan(2).padBottom(10).row();
        table.add(securityAnswerField).colspan(2).width(200).padBottom(20).row();

        table.add(new Label("New Password:", skin)).padRight(10);
        table.add(newPasswordField).width(200).padBottom(10).row();
        table.add(new Label("Confirm Password:", skin)).padRight(10);
        table.add(confirmPasswordField).width(200).padBottom(20).row();

        table.add(messageLabel).colspan(2).padBottom(20).row();
        table.add(resetButton).width(150).padRight(10);
        table.add(backButton).width(150).padBottom(20);

        securityQuestionLabel.setVisible(false);
        securityAnswerField.setVisible(false);
        newPasswordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        resetButton.setVisible(false);


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
}
