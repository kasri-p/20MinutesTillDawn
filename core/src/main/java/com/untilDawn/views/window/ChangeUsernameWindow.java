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

public class ChangeUsernameWindow extends Window {
    private final TextField newUserTextField;
    private final TextButton confirmButton;
    private final TextButton cancelButton;
    private final Label messageLabel;
    private Runnable onComplete;

    public ChangeUsernameWindow(Skin skin, Stage stage) {
        super("Change Username", skin);
        newUserTextField = new TextField("", skin);
        confirmButton = new TextButton("Change", skin);
        cancelButton = new TextButton("Cancel", skin);
        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        setColor(new Color(0.15f, 0.15f, 0.25f, 0.95f));

        defaults().pad(10).width(200);

        Label instructionLabel = new Label("Enter new username:", skin);
        instructionLabel.setAlignment(Align.left);

        add(instructionLabel).left().row();
        add(newUserTextField).fillX().row();
        add(messageLabel).fillX().row();

        // Create a table for buttons to place them side by side
        Table buttonTable = new Table();
        buttonTable.add(confirmButton).width(100).padRight(20);
        buttonTable.add(cancelButton).width(100);

        add(buttonTable).padTop(20).row();

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                if (!checkUser()) {
                    return;
                }
                remove();
                if (onComplete != null) {
                    onComplete.run();
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

        setSize(600, 500);
        float centerX = stage.getWidth() / 2 - getWidth() / 2;
        float centerY = stage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);
        setModal(true);
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    private boolean checkUser() {
        String username = newUserTextField.getText();
        if (App.getLoggedInUser().isGuest()) {
            messageLabel.setText("Guest accounts cannot change usernames.");
            return false;
        }

        if (username.isEmpty()) {
            messageLabel.setText("Username cannot be empty.");
            return false;
        }

        User user = App.getUser(username);
        if (user != null) {
            messageLabel.setText("Username already exists.");
            return false;
        }

        User currentUser = App.getLoggedInUser();
        if (currentUser != null) {
            App.addUser(null);

            App.getUser(username).setUsername(newUserTextField.getText());
            App.save();
        }

        return true;
    }
}
