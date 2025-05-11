package com.untilDawn.views.window;

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

    public SecurityQuestionWindow(Skin skin, User user, Stage stage) {
        super("Security Question", skin);
        this.user = user;

        String[] securityQuestions = {
            "What is your favorite music band?", // Guns n' Roses
            "When did you parents met each-other?",
            "What was your father's first car?",
            "What is your favorite restaurant?",// Fresco
            "How many times did you fail driving test?" // 6
        };

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
