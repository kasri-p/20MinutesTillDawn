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
    private final Label messageLabel;
    private Runnable onComplete;

    public ChangeUsernameWindow(Skin skin, Stage stage) {
        super("Change Username", skin);
        newUserTextField = new TextField("", skin);
        confirmButton = new TextButton("Change", skin);
        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        setColor(new Color(0.15f, 0.15f, 0.25f, 0.95f));

        defaults().pad(10).width(200);

        add(newUserTextField).fillX().row();

        add(confirmButton).width(100).align(Align.center).padTop(20).row();

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                remove();
                if (!checkUser()) {
                    return;
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        setSize(600, 500);
        float centerX = stage.getWidth() / 2 - getWidth() / 2;
        float centerY = stage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);
        setModal(true);
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    private boolean checkUser() {
        String username = newUserTextField.getText();
        User user = App.getUser(username);
        if (user != null) {
            messageLabel.setText("Username already exists.");
            return false;
        }
        return true;
    }
}
