package com.untilDawn.views.window;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.untilDawn.models.User;

public class ChangePasswordWindow extends Window {
    private Runnable onComplete;
    

    public ChangePasswordWindow(Skin skin, User user, Stage stage) {
        super("Reset Password", skin);
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}
