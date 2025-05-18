package com.untilDawn.views.main;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SettingsMenu implements Screen {
    private final Slider slider;

    private final TextButton SFXButton;

    public SettingsMenu(Skin skin) {
        slider = new Slider(0, 100, 1, false, skin);
        SFXButton = new TextButton("SFX", skin);
        slider.setValue(0);
        
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

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

    }
}
