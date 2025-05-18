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
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.StartMenu;

public class DeleteAccountWindow extends Window {
    private final TextField passwordField;
    private final TextButton confirmButton;
    private final TextButton cancelButton;
    private final Label messageLabel;
    private final User user;
    private Runnable onComplete;

    public DeleteAccountWindow(Skin skin, Stage stage) {
        super("Delete Account", skin);
        this.user = App.getLoggedInUser();

        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        confirmButton = new TextButton("Delete", skin);
        cancelButton = new TextButton("Cancel", skin);

        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        setColor(new Color(0.15f, 0.15f, 0.25f, 0.95f));

        defaults().pad(10).width(350);

        Label waringLabel = new Label("WARNING: This action cannot be undone.", skin);
        waringLabel.setColor(Color.RED);
        waringLabel.setAlignment(Align.center);

        add(waringLabel).colspan(2).padBottom(20).row();
        add(new Label("Please enter your password:", skin)).left().row();
        add(passwordField).fillX().row();
        add(messageLabel).colspan(2).width(350).padTop(5).row();

        Table buttonTable = new Table();
        buttonTable.add(confirmButton).width(150).padRight(20);
        buttonTable.add(cancelButton).width(120);
        add(buttonTable).colspan(2).padTop(20).row();

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                playClick();
                if (validateAndDeleteAccount()) {
                    remove();
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


        setSize(600, 400);

        float centerX = stage.getWidth() / 2 - getWidth() / 2;
        float centerY = stage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);
        setModal(true);
        setMovable(false);
    }

    private boolean validateAndDeleteAccount() {
        String password = passwordField.getText().trim();
        if (!App.verifyPassword(password, user.getPassword())) {
            messageLabel.setText("Password is incorrect.");
            return false;
        }

        App.removeUser(user);
        App.logout();
        App.save();

        StartMenu startMenu = new StartMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(startMenu);

        return true;
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
