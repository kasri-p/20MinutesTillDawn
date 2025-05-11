package com.untilDawn.views.window;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;

public class ChangeAvatarWindow extends Window {
    private final TextButton okButton;

    public ChangeAvatarWindow(Skin skin, Stage stage, String message) {
        super("Coming Soon", skin);

        Label messageLabel = new Label(message, skin);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        okButton = new TextButton("OK", skin);

        setColor(new Color(0.15f, 0.15f, 0.25f, 0.95f));

        defaults().pad(10).width(300);

        add(messageLabel).width(300).padBottom(20).row();
        add(okButton).width(100).align(Align.center).padTop(10).row();

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                remove();
            }
        });

        setSize(350, 200);
        float centerX = stage.getWidth() / 2 - getWidth() / 2;
        float centerY = stage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);
        setModal(true);
        setMovable(false);
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }
}
