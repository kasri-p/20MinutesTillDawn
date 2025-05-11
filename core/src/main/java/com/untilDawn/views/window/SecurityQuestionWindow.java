package com.untilDawn.views.window;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;

public class SecurityQuestionWindow extends Window {
    private final TextField securityAnswerField;
    private final SelectBox<String> securityQuestionBox;
    private final TextButton confirmButton;
    private final User user;
    private Runnable onComplete;
    private Image leftLeaves;
    private Image rightLeaves;
    private Texture leavesTexture;

    public SecurityQuestionWindow(Skin skin, User user, Stage stage) {
        super("Security Question", skin);
        this.user = user;

        leavesTexture = new Texture(Gdx.files.internal("images/TitleLeaves.png"));

        leftLeaves = new Image(leavesTexture);
        float scale = stage.getHeight() / leavesTexture.getHeight();
        float scaledWidth = leavesTexture.getWidth() * scale;
        leftLeaves.setSize(scaledWidth, stage.getHeight());
        leftLeaves.setPosition(0, 0);

        rightLeaves = new Image(leavesTexture);
        rightLeaves.setSize(scaledWidth, stage.getHeight());
        rightLeaves.setPosition(stage.getWidth() - scaledWidth, 0);
        rightLeaves.setScaleX(-1);

        stage.addActor(leftLeaves);
        stage.addActor(rightLeaves);
        leftLeaves.toBack();
        rightLeaves.toBack();

        String[] securityQuestions = {
            "What is your favorite music band?", // Guns n' Roses
            "When did you parents met each-other?",
            "What was your father's first car?",
            "What is your favorite restaurant?",// Fresco
            "How many times did you fail driving test?" // 6
        };

        setColor(new Color(0.15f, 0.15f, 0.25f, 0.95f));

        securityQuestionBox = new SelectBox<>(skin);
        securityQuestionBox.setItems(securityQuestions);
        securityAnswerField = new TextField("", skin);
        confirmButton = new TextButton("Confirm", skin);

        defaults().pad(10).width(200);
        add(new Label("Select a security question:", skin)).left().row();
        add(securityQuestionBox).fillX().row();
        add(new Label("Your answer:", skin)).left().row();
        add(securityAnswerField).fillX().row();
        add(confirmButton).width(100).align(Align.center).padTop(20).row();
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getMain().getClickSound().play();
                if (validateAndSave()) {
                    leftLeaves.remove();
                    rightLeaves.remove();
                    if (leavesTexture != null) {
                        leavesTexture.dispose();
                    }

                    remove();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        });

        setSize(600, 500);
        float centerX = stage.getWidth() / 2 - getWidth() / 2;
        float centerY = stage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);
        setModal(true);
        setMovable(false);
    }

    private boolean validateAndSave() {
        String answer = securityAnswerField.getText().trim();

        if (answer.isEmpty()) {
            return false;
        }

        user.setSecurityQuestionIndex(securityQuestionBox.getSelectedIndex());
        user.setSecurityAnswer(answer);

        App.save();
        return true;
    }

    public void setOnCompleteCallback(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}
