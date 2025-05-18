package com.untilDawn.views.window;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;

public class ChangePasswordWindow extends Window {
    private final TextField currentPasswordField;
    private final TextField newPasswordField;
    private final TextField confirmPasswordField;
    private final TextButton confirmButton;
    private final TextButton cancelButton;
    private final Label messageLabel;
    private final User user;
    private Runnable onComplete;

    public ChangePasswordWindow(Skin skin, User user, Stage stage) {
        super("Change Password", skin);
        this.user = user;

        currentPasswordField = new TextField("", skin);
        currentPasswordField.setPasswordMode(true);
        currentPasswordField.setPasswordCharacter('*');

        newPasswordField = new TextField("", skin);
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');

        confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');

        confirmButton = new TextButton("Confirm", skin);
        cancelButton = new TextButton("Cancel", skin);

        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        setColor(new Color(0.15f, 0.15f, 0.25f, 0.95f));

        // Configure layout
        defaults().pad(10).width(300);

        add(new Label("Current Password:", skin)).left().row();
        add(currentPasswordField).fillX().row();

        add(new Label("New Password:", skin)).left().row();
        add(newPasswordField).fillX().row();

        add(new Label("Confirm New Password:", skin)).left().row();
        add(confirmPasswordField).fillX().row();

        add(messageLabel).colspan(2).width(350).padTop(5).row();

        Table buttonTable = new Table();
        buttonTable.add(confirmButton).width(120).padRight(20);
        buttonTable.add(cancelButton).width(120);
        add(buttonTable).colspan(2).padTop(20).row();

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                if (validateAndSave()) {
                    remove();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                remove();
            }
        });

        setSize(400, 700);
        float centerX = stage.getWidth() / 2 - getWidth() / 2;
        float centerY = stage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);
        setModal(true);
        setMovable(false);
    }

    private boolean validateAndSave() {
        String currentPassword = currentPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (!App.verifyPassword(currentPassword, user.getPassword())) {
            messageLabel.setText("Current password is incorrect.");
            return false;
        }

        if (newPassword.isEmpty()) {
            messageLabel.setText("New password cannot be empty.");
            return false;
        }

        if (!isPasswordStrong(newPassword)) {
            messageLabel.setText("new password is weak!");
            return false;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("New passwords do not match.");
            return false;
        }

        // Save the new password
        String hashedPassword = App.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        App.save();

        return true;
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 &&
            password.matches(".*[@#$%&*()_].*") &&
            password.matches(".*\\d.*") &&
            password.matches(".*[A-Z].*");
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}
