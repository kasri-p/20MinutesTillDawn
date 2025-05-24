package com.untilDawn.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Game;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.GameSaveSystem;
import com.untilDawn.views.StartMenu;
import com.untilDawn.views.main.*;

public class MainMenuController {
    private static final float HOVER_SCALE = 1.2f;
    private static final float ANIMATION_DURATION = 0.15f;
    private static final float TRANSITION_DURATION = 0.5f;

    private MainMenu view;

    public MainMenuController(MainMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    public void initializeButtonListeners() {
        view.getLogoutButton().setTransform(true);
        view.getProfileButton().setTransform(true);
        view.getPlayButton().setTransform(true);
        view.getSettingsButton().setTransform(true);
        if (view.getScoreboardButton() != null) view.getScoreboardButton().setTransform(true);
        if (view.getTalentsButton() != null) view.getTalentsButton().setTransform(true);

        view.getLogoutButton().addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                view.getLogoutButton().setOrigin(Align.center);
                view.getLogoutButton().clearActions();
                view.getLogoutButton().addAction(
                    Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                view.getLogoutButton().clearActions();
                view.getLogoutButton().addAction(
                    Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                disableAllButtons();

                view.getStage().getRoot().addAction(
                    Actions.sequence(
                        Actions.fadeOut(TRANSITION_DURATION),
                        Actions.run(() -> {
                            StartMenu startMenu = new StartMenu(GameAssetManager.getGameAssetManager().getSkin());
                            Main.getMain().setScreen(startMenu);
                            startMenu.getStage().getRoot().getColor().a = 0;
                            startMenu.getStage().getRoot().addAction(Actions.fadeIn(TRANSITION_DURATION));
                        })
                    )
                );
            }
        });

        // Profile Button
        view.getProfileButton().addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                view.getProfileButton().setOrigin(Align.center);
                view.getProfileButton().clearActions();
                view.getProfileButton().addAction(
                    Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                view.getProfileButton().clearActions();
                view.getProfileButton().addAction(
                    Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                disableAllButtons();

                view.getStage().getRoot().addAction(
                    Actions.sequence(
                        Actions.fadeOut(TRANSITION_DURATION),
                        Actions.run(() -> {
                            ProfileMenu profileMenu = new ProfileMenu(GameAssetManager.getGameAssetManager().getSkin());
                            Main.getMain().setScreen(profileMenu);
                            profileMenu.getStage().getRoot().getColor().a = 0;
                            profileMenu.getStage().getRoot().addAction(Actions.fadeIn(TRANSITION_DURATION));
                        })
                    )
                );
            }
        });

        // Play Button
        view.getPlayButton().addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                view.getPlayButton().setOrigin(Align.center);
                view.getPlayButton().clearActions();
                view.getPlayButton().addAction(
                    Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                view.getPlayButton().clearActions();
                view.getPlayButton().addAction(
                    Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                disableAllButtons();

                view.getStage().getRoot().addAction(
                    Actions.sequence(
                        Actions.fadeOut(TRANSITION_DURATION),
                        Actions.run(() -> {
                            PreGameMenu preGameMenu = new PreGameMenu(GameAssetManager.getGameAssetManager().getSkin());
                            Main.getMain().setScreen(preGameMenu);
                            preGameMenu.getStage().getRoot().getColor().a = 0;
                            preGameMenu.getStage().getRoot().addAction(Actions.fadeIn(TRANSITION_DURATION));
                        })
                    )
                );
            }
        });

        if (view.getScoreboardButton() != null) {
            view.getScoreboardButton().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    view.getScoreboardButton().setOrigin(Align.center);
                    view.getScoreboardButton().clearActions();
                    view.getScoreboardButton().addAction(
                        Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    view.getScoreboardButton().clearActions();
                    view.getScoreboardButton().addAction(
                        Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                    );
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    playClick();
                    disableAllButtons();

                    view.getStage().getRoot().addAction(
                        Actions.sequence(
                            Actions.fadeOut(TRANSITION_DURATION),
                            Actions.run(() -> {
                                ScoreBoardMenu scoreboardMenu = new ScoreBoardMenu(GameAssetManager.getGameAssetManager().getSkin());
                                Main.getMain().setScreen(scoreboardMenu);
                            })
                        )
                    );
                }
            });
        }

        // Settings Button
        view.getSettingsButton().addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                view.getSettingsButton().setOrigin(Align.center);
                view.getSettingsButton().clearActions();
                view.getSettingsButton().addAction(
                    Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                view.getSettingsButton().clearActions();
                view.getSettingsButton().addAction(
                    Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                disableAllButtons();

                view.getStage().getRoot().addAction(
                    Actions.sequence(
                        Actions.fadeOut(TRANSITION_DURATION),
                        Actions.run(() -> {
                            SettingsMenu settingsMenu = new SettingsMenu(GameAssetManager.getGameAssetManager().getSkin());
                            Main.getMain().setScreen(settingsMenu);
                            settingsMenu.getStage().getRoot().getColor().a = 0;
                            settingsMenu.getStage().getRoot().addAction(Actions.fadeIn(TRANSITION_DURATION));
                        })
                    )
                );
            }
        });

        if (view.getScoreboardButton() != null) {
            view.getScoreboardButton().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    view.getScoreboardButton().setOrigin(Align.center);
                    view.getScoreboardButton().clearActions();
                    view.getScoreboardButton().addAction(
                        Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    view.getScoreboardButton().clearActions();
                    view.getScoreboardButton().addAction(
                        Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                    );
                }
            });
        }

        if (view.getTalentsButton() != null) {
            view.getTalentsButton().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    view.getTalentsButton().setOrigin(Align.center);
                    view.getTalentsButton().clearActions();
                    view.getTalentsButton().addAction(
                        Actions.scaleTo(HOVER_SCALE, HOVER_SCALE, ANIMATION_DURATION)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    view.getTalentsButton().clearActions();
                    view.getTalentsButton().addAction(
                        Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION)
                    );
                }
            });
        }

        view.getContinueGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                if (App.getLoggedInUser().isGuest()) {
                    return;
                }
                // TODO: show the game info
                GameSaveSystem.GameSaveData saveData = GameSaveSystem.loadGame(App.getLoggedInUser());
                if (saveData == null) {
                    return;
                }
                Game game = GameSaveSystem.restoreGameFromSave(saveData);
                App.setGame(game);
                Main.getMain().setScreen(new GameView(GameAssetManager.getGameAssetManager().getSkin()));
            }
        });

        view.getTalentsButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                Main.getMain().setScreen(new HintMenu(GameAssetManager.getGameAssetManager().getSkin(), () -> Main.getMain().setScreen(new MainMenu(GameAssetManager.getGameAssetManager().getSkin()))));
            }
        });
    }

    private void disableAllButtons() {
        view.getLogoutButton().setDisabled(true);
        view.getProfileButton().setDisabled(true);
        view.getPlayButton().setDisabled(true);
        view.getSettingsButton().setDisabled(true);
        if (view.getScoreboardButton() != null) view.getScoreboardButton().setDisabled(true);
        if (view.getTalentsButton() != null) view.getTalentsButton().setDisabled(true);
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }
}
