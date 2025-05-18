package com.untilDawn.views.window;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.models.App;
import com.untilDawn.models.User;

public class ForgotPasswordWindow extends Window {
    private final TextField usernameField;
    private final TextField securityAnswerField;
    private final TextField newPasswordField;
    private final TextField confirmPasswordField;
    private final Label messageLabel;
    private final TextButton resetButton;
    private final TextButton backButton;
    private final Table contentTable;
    private final Stage parentStage;

    String[] securityQuestions = {
        "What is your favorite music band?", // Guns n' Roses
        "When did you parents met each-other?",
        "What was your father's first car?",
        "What is your favorite restaurant?",// Fresco
        "How many times did you fail driving test?" // 6
    };
    private Label securityQuestionLabel;
    private User user;


    public ForgotPasswordWindow(Skin skin, final Stage parentStage) {
        super("Forgot Password", skin);
        this.parentStage = parentStage;

        // Set window properties
        this.setSize(parentStage.getWidth() / 2, 900);
        this.setPosition((Gdx.graphics.getWidth() - this.getWidth()) / 2,
            (Gdx.graphics.getHeight() - this.getHeight()) / 2 - 100);
        this.setMovable(true);

        // Create components
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
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        securityQuestionLabel = new Label("Enter your username to see your security question", skin);
        securityQuestionLabel.setWrap(true);
        securityQuestionLabel.setAlignment(Align.center);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(
            skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.LIGHT_GRAY;
        buttonStyle.downFontColor = Color.GRAY;

        if (buttonStyle.up == null) {
            buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = skin.getFont("default-font");
            buttonStyle.fontColor = Color.WHITE;
            buttonStyle.overFontColor = Color.LIGHT_GRAY;
            buttonStyle.downFontColor = Color.GRAY;
            if (skin.has("button", TextButton.TextButtonStyle.class)) {
                TextButton.TextButtonStyle defaultStyle = skin.get("button", TextButton.TextButtonStyle.class);
                buttonStyle.up = defaultStyle.up;
                buttonStyle.down = defaultStyle.down;
                buttonStyle.over = defaultStyle.over;
            }
        }

        resetButton = new TextButton("Reset", buttonStyle);
        backButton = new TextButton("Back", buttonStyle);
        TextButton findUserButton = new TextButton("Find User", buttonStyle);

        contentTable = new Table();
        contentTable.pad(30);
        contentTable.defaults().space(15);

        // Add header
        Label titleLabel = new Label("Reset Password", skin, "title");
        titleLabel.setAlignment(Align.center);
        contentTable.add(titleLabel).colspan(2).padBottom(10).fillX().row();

        // Username section
        contentTable.add(new Label("Username:", skin)).right().padRight(10);
        contentTable.add(usernameField).width(250).left().row();
        contentTable.add(findUserButton).colspan(2).padBottom(10).padTop(10).row();

        // Security question section
        contentTable.add(securityQuestionLabel).colspan(2).width(300).padBottom(10).row();
        contentTable.add(securityAnswerField).colspan(2).width(250).padBottom(10).row();

        // New password section
        contentTable.add(new Label("New Password:", skin)).right().padRight(15);
        contentTable.add(newPasswordField).width(250).left().row();
        contentTable.add(new Label("Confirm Password:", skin)).right().padRight(15);
        contentTable.add(confirmPasswordField).width(250).left().row();

        // Message and buttons
        contentTable.add(messageLabel).colspan(2).width(350).padTop(5).row();
        Table buttonTable = new Table();
        buttonTable.add(resetButton).width(180);
        buttonTable.add(backButton).width(180);
        contentTable.add(buttonTable).colspan(2).padTop(5).row();

        securityQuestionLabel.setVisible(false);
        securityAnswerField.setVisible(false);
        newPasswordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        resetButton.setVisible(false);

        this.add(contentTable).expand().fill();

        findUserButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                findUser();
            }
        });

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetPassword();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove(); // Remove this window from the stage
                parentStage.setKeyboardFocus(null);
            }
        });
    }

    private void findUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Please enter a username");
            return;
        }

        user = App.getUser(username);
        if (user == null) {
            messageLabel.setText("User not found");
            securityQuestionLabel.setVisible(false);
            securityAnswerField.setVisible(false);
            newPasswordField.setVisible(false);
            confirmPasswordField.setVisible(false);
            resetButton.setVisible(false);
            return;
        }

        int questionIndex = user.getSecurityQuestionIndex();
        if (questionIndex >= 0 && questionIndex < securityQuestions.length) {
            securityQuestionLabel.setText(securityQuestions[questionIndex]);
        } else {
            securityQuestionLabel.setText("Security question not available");
        }

        securityQuestionLabel.setVisible(true);
        securityAnswerField.setVisible(true);
        newPasswordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        resetButton.setVisible(true);

        messageLabel.setText("");
    }

    private void resetPassword() {
        if (user == null) {
            messageLabel.setText("Please find a user first");
            return;
        }

        String securityAnswer = securityAnswerField.getText().trim();
        if (securityAnswer.isEmpty()) {
            messageLabel.setText("Please answer the security question");
            return;
        }

        if (!securityAnswer.equalsIgnoreCase(user.getSecurityAnswer())) {
            messageLabel.setText("Incorrect security answer");
            return;
        }

        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty()) {
            messageLabel.setText("Please enter a new password");
            return;
        }

        if (newPassword.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            return;
        }
        String hashedPassword = App.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        messageLabel.setColor(Color.GREEN);
        messageLabel.setText("Password reset successful! You can now log in.");

        // Reset fields
        securityAnswerField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    // Add this window to the parent stage
    public void show() {
        parentStage.addActor(this);
        parentStage.setKeyboardFocus(usernameField);

        // Make sure all necessary fields are visible initially
        usernameField.setVisible(true);
        messageLabel.setVisible(true);

        // Force layout update
        invalidate();
        pack();

        // Re-center the window after packing
        this.setPosition((Gdx.graphics.getWidth() - this.getWidth()) / 2,
            (Gdx.graphics.getHeight() - this.getHeight()) / 2);
    }
}
