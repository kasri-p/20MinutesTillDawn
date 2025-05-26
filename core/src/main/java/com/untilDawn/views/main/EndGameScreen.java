package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;

public class EndGameScreen implements Screen {
    // Constants
    private static final float PANEL_WIDTH = 800f;
    private static final float PANEL_HEIGHT = 600f;
    private static final float PADDING = 40f;

    // Colors matching the dark theme
    private static final Color BACKGROUND_COLOR = new Color(0.157f, 0.098f, 0.204f, 1f); // #28193A
    private static final Color PANEL_COLOR = new Color(0.1f, 0.05f, 0.15f, 0.98f);
    private static final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f);
    private static final Color DEFEAT_COLOR = new Color(0.9f, 0.2f, 0.2f, 1f);
    private static final Color VICTORY_COLOR = new Color(0.2f, 0.9f, 0.2f, 1f);
    private static final Color BUTTON_COLOR = Color.SALMON;
    private static final Color BUTTON_HOVER_COLOR = new Color(1f, 0.4f, 0.6f, 1f);

    private final Skin skin;
    private final User user;
    private final float survivalTime;
    private final int kills;
    private final EndGameStatus status;
    private final int score;

    // Core components
    private Stage stage;
    private BitmapFont chevyRayFont;

    // UI Components
    private Table mainPanel;
    private Label titleLabel;
    private Label scoreLabel;

    // Progress bar
    private float progressBarWidth = 600f;
    private float progressBarHeight = 8f;
    private Texture progressBarBg;
    private Texture progressBarFill;

    public EndGameScreen(Skin skin, User user, float survivalTime, int kills, EndGameStatus status) {
        this.skin = skin;
        this.user = user;
        this.survivalTime = survivalTime;
        this.kills = kills;
        this.status = status;
        this.score = calculateScore();

        updateUserStats();
    }

    private int calculateScore() {
        // Simple score calculation based on survival time and kills
        return (int) (survivalTime * 10 + kills * 100);
    }

    private void updateUserStats() {
        if (user != null && !user.isGuest()) {
            user.setScore(user.getScore() + score);
            App.save();
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        chevyRayFont = GameAssetManager.getGameAssetManager().getChevyRayFont();

        createContent();
        setupAnimations();
        playEndGameSound();
    }

    private void createContent() {
        Table container = new Table();
        container.setFillParent(true);

        mainPanel = new Table();
//        mainPanel.setBackground(createPanelBackground());
        mainPanel.pad(PADDING);

        // Title section
        createTitleSection();

        // Stats section
        createStatsSection();

        // Score section
        createScoreSection();

        // Progress bar
        createProgressBar();

        // Buttons section
        createButtonsSection();

        container.add(mainPanel).size(PANEL_WIDTH, PANEL_HEIGHT);
        stage.addActor(container);
    }

    private TextureRegionDrawable createPanelBackground() {
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(panelTexture));
        return drawable;
    }

    private void createTitleSection() {
        String titleText = "You Died";
        Color titleColor = DEFEAT_COLOR;

        if (status == EndGameStatus.VICTORY) {
            titleText = "Victory!";
            titleColor = VICTORY_COLOR;
        } else if (status == EndGameStatus.GIVE_UP) {
            titleText = "Gave Up";
            titleColor = DEFEAT_COLOR;
        }

        titleLabel = new Label(titleText, new Label.LabelStyle(chevyRayFont, titleColor));
        titleLabel.setFontScale(3.0f);
        titleLabel.setAlignment(Align.center);

        mainPanel.add(titleLabel).padBottom(60).row();
    }

    private void createStatsSection() {
        Table statsTable = new Table();
        statsTable.defaults().padBottom(15);

        // Time Survived
        Table timeRow = createStatRow("Time Survived", formatTime(survivalTime));
        statsTable.add(timeRow).fillX().row();

        // Enemies Killed
        Table killsRow = createStatRow("Enemies Killed", String.valueOf(kills));
        statsTable.add(killsRow).fillX().row();

        // Levels Earned
        int levelsEarned = calculateLevelsEarned();
        Table levelsRow = createStatRow("Levels Earned", String.valueOf(levelsEarned));
        statsTable.add(levelsRow).fillX().row();

        mainPanel.add(statsTable).width(PANEL_WIDTH - 100).padBottom(40).row();
    }

    private Table createStatRow(String label, String value) {
        Table row = new Table();

        String fullText = label + " (" + value + ")";
        Label statLabel = new Label(fullText, new Label.LabelStyle(chevyRayFont, TEXT_COLOR));
        statLabel.setFontScale(1.3f);

        Label valueLabel = new Label(value, new Label.LabelStyle(chevyRayFont, TEXT_COLOR));
        valueLabel.setFontScale(1.3f);
        valueLabel.setAlignment(Align.right);

        row.add(statLabel).expandX().left();
        row.add(valueLabel).right();

        return row;
    }

    private void createScoreSection() {
        Table scoreTable = new Table();

        Label earnedLabel = new Label("Earned", new Label.LabelStyle(chevyRayFont, BUTTON_COLOR));
        earnedLabel.setFontScale(1.5f);

        scoreLabel = new Label("1", new Label.LabelStyle(chevyRayFont, TEXT_COLOR));
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setAlignment(Align.right);

        scoreTable.add(earnedLabel).expandX().left();
        scoreTable.add(scoreLabel).right();

        mainPanel.add(scoreTable).width(PANEL_WIDTH - 100).padBottom(40).row();
    }

    private void createProgressBar() {
        progressBarBg = GameAssetManager.getGameAssetManager().getReloadBarBg();
        progressBarFill = GameAssetManager.getGameAssetManager().getReloadBarFill();

        Table progressTable = new Table();

        Image bgImage = new Image(progressBarBg);
        bgImage.setColor(0.2f, 0.2f, 0.2f, 1f);

        Image fillImage = new Image(progressBarFill);
        fillImage.setColor(BUTTON_COLOR);

        float progress = Math.min(survivalTime / App.getGame().getTimeLimit(), 1f);

        progressTable.add(bgImage).size(progressBarWidth, progressBarHeight);

        mainPanel.add(progressTable).padBottom(60).row();
    }

    private void createButtonsSection() {
        Table buttonTable = new Table();
        buttonTable.defaults().padBottom(20);

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = chevyRayFont;
        textOnlyStyle.fontColor = BUTTON_COLOR;
//        textOnlyStyle.overFontColor = BUTTON_HOVER_COLOR;
        textOnlyStyle.downFontColor = BUTTON_COLOR.cpy().mul(0.8f);

        TextButton tryAgainButton = new TextButton("Try Again", textOnlyStyle);
        tryAgainButton.getLabel().setFontScale(1.8f);
        tryAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                transitionToPreGame();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                tryAgainButton.getLabel().setColor(BUTTON_HOVER_COLOR);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                tryAgainButton.getLabel().setColor(BUTTON_COLOR);
            }
        });

        // Quit To Menu button
        TextButton quitButton = new TextButton("Quit To Menu", textOnlyStyle);
        quitButton.getLabel().setFontScale(1.8f);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                transitionToMainMenu();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                quitButton.getLabel().setColor(BUTTON_HOVER_COLOR);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                quitButton.getLabel().setColor(BUTTON_COLOR);
            }
        });

        buttonTable.add(tryAgainButton).row();
        buttonTable.add(quitButton).row();

        mainPanel.add(buttonTable);
    }

    private String formatTime(float totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private int calculateLevelsEarned() {
        // Calculate based on XP/kills
        return Math.max(0, kills / 10); // Example: 1 level per 10 kills
    }

    private void setupAnimations() {
        // Fade in animation
        mainPanel.getColor().a = 0f;
        mainPanel.addAction(Actions.sequence(
            Actions.fadeIn(0.5f, Interpolation.fade)
        ));

        // Scale animation for title
        titleLabel.setScale(0.8f);
        titleLabel.addAction(Actions.sequence(
            Actions.delay(0.2f),
            Actions.scaleTo(1f, 1f, 0.3f, Interpolation.bounceOut)
        ));
    }

    private void playEndGameSound() {
        if (App.isSFX()) {
            if (status == EndGameStatus.VICTORY) {
                GameAssetManager.getGameAssetManager().playWin();
            } else {
                GameAssetManager.getGameAssetManager().playLose();
            }
        }
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    private void transitionToMainMenu() {
        stage.getRoot().addAction(Actions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run(() -> {
                Main.getMain().setScreen(new MainMenu(skin));
            })
        ));
    }

    private void transitionToPreGame() {
        stage.getRoot().addAction(Actions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run(() -> {
                Main.getMain().setScreen(new PreGameMenu(skin));
            })
        ));
    }

    @Override
    public void render(float delta) {
        // Clear with dark purple background
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        if (stage != null) {
            stage.dispose();
        }
    }

    public enum EndGameStatus {
        VICTORY("Survived!", "You survived until dawn!", VICTORY_COLOR),
        DEFEAT("You Died", "You couldn't survive the night...", DEFEAT_COLOR),
        GIVE_UP("Gave Up", "You abandoned the fight...", DEFEAT_COLOR);

        private final String title;
        private final String subtitle;
        private final Color color;

        EndGameStatus(String title, String subtitle, Color color) {
            this.title = title;
            this.subtitle = subtitle;
            this.color = color;
        }
    }
}
