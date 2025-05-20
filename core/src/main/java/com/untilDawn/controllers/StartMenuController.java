package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.SignUpMenu;
import com.untilDawn.views.StartMenu;

public class StartMenuController {
    private static final float HOVER_SCALE = 1.2f;
    private static final float ANIMATION_DURATION = 0.15f;
    private static final float FADE_OUT_DURATION = 0.5f; // Duration for fade out effect
    private StartMenu view;
    private boolean listenersInitialized = false;

    public StartMenuController(StartMenu startMenu) {
        this.view = startMenu;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        if (view != null && !listenersInitialized) {
            view.getStartButton().setTransform(true);
            view.getQuitButton().setTransform(true);
            view.getLanguageButton().setTransform(true);

            view.getStartButton().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    view.getStartButton().setOrigin(Align.center);
                    view.getStartButton().clearActions();
                    view.getStartButton().addAction(
                        Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION, Interpolation.smooth)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    view.getStartButton().clearActions();
                    view.getStartButton().addAction(
                        Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION, Interpolation.smooth)
                    );
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    playClick();
                    System.out.println("Play button clicked");

                    view.getStartButton().setDisabled(true);

                    view.getStage().getRoot().addAction(
                        Actions.sequence(
                            Actions.fadeOut(FADE_OUT_DURATION),
                            Actions.run(() -> {
                                Main.getMain().setScreen(new SignUpMenu(GameAssetManager.getGameAssetManager().getSkin()));
                            })
                        )
                    );
                }
            });

            // Quit button listener
            view.getQuitButton().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    view.getQuitButton().setOrigin(Align.center);
                    view.getQuitButton().clearActions();
                    view.getQuitButton().addAction(
                        Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION, Interpolation.smooth)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    view.getQuitButton().clearActions();
                    view.getQuitButton().addAction(
                        Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION, Interpolation.smooth)
                    );
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    playClick();
                    System.out.println("Exit button clicked");
                    Main.getMain().getScreen().dispose();
                    Gdx.app.exit();
                }
            });

            if (view.getLanguageButton() != null) {
                view.getLanguageButton().addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        view.getLanguageButton().setOrigin(Align.center);
                        view.getLanguageButton().clearActions();
                        view.getLanguageButton().addAction(
                            Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION, Interpolation.smooth)
                        );
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        view.getLanguageButton().clearActions();
                        view.getLanguageButton().addAction(
                            Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION, Interpolation.smooth)
                        );
                    }
                });
            }

            listenersInitialized = true;
        }
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }
}
