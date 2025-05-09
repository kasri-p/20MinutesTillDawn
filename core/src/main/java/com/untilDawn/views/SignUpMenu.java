package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    public SignUpMenu(Skin skin) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        securityAnswerField = new TextField("", skin);
        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);

        // Define a text-only style for the buttons
        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = Color.WHITE;
        textOnlyStyle.overFontColor = Color.LIGHT_GRAY;
        textOnlyStyle.downFontColor = Color.GRAY;

        // Create buttons with the text-only style
        signUpButton = new TextButton("Sign Up", textOnlyStyle);
        skipButton = new TextButton("Skip", textOnlyStyle);
        backButton = new TextButton("Back", textOnlyStyle);

        // Add a click listener to the sign-up button
        signUpButton.addListener(event -> {
            if (validateForm()) {
                // Proceed with sign-up logic
                errorLabel.setText("Sign-up successful!");
                errorLabel.setColor(Color.GREEN);
            }
            return true;
        });

        table = new Table();
        table.setFillParent(true);
        table.center();

        // Add components to the table
        table.add(new Label("Username:", skin)).pad(10);
        table.add(usernameField).width(200).pad(10);
        table.row();
        table.add(new Label("Password:", skin)).pad(10);
        table.add(passwordField).width(200).pad(10);
        table.row();
        table.add(new Label("Security Answer:", skin)).pad(10);
        table.add(securityAnswerField).width(200).pad(10);
        table.row();
        table.add(errorLabel).colspan(2).pad(10);
        table.row();
        table.add(signUpButton).width(100).pad(10);
        table.add(skipButton).width(100).pad(10);
        table.row();
        table.add(backButton).colspan(2).width(100).pad(10);

        stage.addActor(table);
    }

    private boolean validateForm() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String securityAnswer = securityAnswerField.getText().trim();

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty.");
            return false;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Password cannot be empty.");
            return false;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return false;
        }
        if (securityAnswer.isEmpty()) {
            errorLabel.setText("Security answer cannot be empty.");
            return false;
        }

        errorLabel.setText(""); // Clear error if validation passes
        return true;
    }

    @Override
    public void show() {
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
}
