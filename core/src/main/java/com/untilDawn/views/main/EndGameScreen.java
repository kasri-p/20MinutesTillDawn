package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import com.untilDawn.models.utils.UIHelper;

public class EndGameScreen implements Screen {
    // Constants
    private static final float PANEL_WIDTH = 800f;
    private static final float PANEL_HEIGHT = 600f;
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 60f;
    private static final float PADDING = 30f;

    // Colors
    private static final Color VICTORY_COLOR = new Color(0.2f, 0.8f, 0.2f, 1f);
    private static final Color DEFEAT_COLOR = new Color(0.8f, 0.2f, 0.2f, 1f);
    private static final Color GOLD_COLOR = new Color(1f, 0.84f, 0f, 1f);
    private static final Color PANEL_COLOR = new Color(0.1f, 0.1f, 0.15f, 0.95f);
    private static final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f);
    private static final Color LABEL_COLOR = new Color(0.7f, 0.7f, 0.7f, 1f);
    private final Skin skin;
    private final User user;
    private final float survivalTime;
    private final int kills;
    private final EndGameStatus status;
    private final int score;
    // Core components
    private Stage stage;
    // UI Components
    private Table mainPanel;
    private Label statusLabel;
    private Label scoreLabel;
    private Container<Table> panelContainer;

    // Textures
    private Texture panelTexture;
    private Texture buttonTexture;
    private Image[] leavesDecorations;

    // Animation
    private float animationTime = 0f;

    public EndGameScreen(Skin skin, User user, float survivalTime, int kills, EndGameStatus status) {
        this.skin = skin;
        this.user = user;
        this.survivalTime = survivalTime;
        this.kills = kills;
        this.status = status;
        this.score = calculateScore();

        // Update user statistics
        updateUserStats();
    }

    private int calculateScore() {
        return (int) (survivalTime * kills);
    }

    private void updateUserStats() {
        if (user != null && !user.isGuest()) {
            // Update user's total score
            user.setScore(user.getScore() + score);

            App.save();
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Add decorative leaves
        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        // Load textures
        loadTextures();

        // Create the main content
        createContent();

        // Add animations
        setupAnimations();

        // Play appropriate sound
        playEndGameSound();
    }

    private void loadTextures() {
        panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        buttonTexture = createButtonTexture();
    }

    private Texture createButtonTexture() {
        // Create a simple button texture (this is a placeholder)
        // In production, you'd load an actual texture
        return panelTexture;
    }

    private void createContent() {
        // Create main panel
        mainPanel = new Table();
        mainPanel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        mainPanel.setColor(PANEL_COLOR);
        mainPanel.pad(PADDING);

        // Add content to panel
        createStatusSection();
        createStatsSection();
        createScoreSection();
        createButtonsSection();

        // Wrap panel in container for centering
        panelContainer = new Container<>(mainPanel);
        panelContainer.setFillParent(true);
        panelContainer.center();

        stage.addActor(panelContainer);

        // Ensure leaves are in background
        for (Image leaf : leavesDecorations) {
            leaf.toBack();
        }
    }

    private void createStatusSection() {
        // Victory/Defeat label
        statusLabel = new Label(status.title, skin, "title");
        statusLabel.setAlignment(Align.center);
        statusLabel.setFontScale(3f);
        statusLabel.setColor(status.color);

        // Subtitle
        Label subtitleLabel = new Label(status.subtitle, skin);
        subtitleLabel.setAlignment(Align.center);
        subtitleLabel.setFontScale(1.2f);
        subtitleLabel.setColor(TEXT_COLOR);

        mainPanel.add(statusLabel).padBottom(10).row();
        mainPanel.add(subtitleLabel).padBottom(40).row();
    }

    private void createStatsSection() {
        Table statsTable = new Table();
        statsTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        statsTable.setColor(new Color(0.08f, 0.08f, 0.12f, 0.8f));
        statsTable.pad(20);

        // Username
        addStatRow(statsTable, "Player:", user != null ? user.getUsername() : "Guest");

        // Survival time
        String timeString = formatTime(survivalTime);
        addStatRow(statsTable, "Survival Time:", timeString);

        // Kill count
        addStatRow(statsTable, "Enemies Killed:", String.valueOf(kills));

        mainPanel.add(statsTable).width(PANEL_WIDTH - 60).padBottom(30).row();
        mainPanel.pack();
    }

    private void createScoreSection() {
        Table scoreTable = new Table();
        scoreTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        scoreTable.setColor(new Color(0.15f, 0.12f, 0.08f, 0.9f));
        scoreTable.pad(20);

        Label scoreTextLabel = new Label("FINAL SCORE", skin);
        scoreTextLabel.setAlignment(Align.center);
        scoreTextLabel.setFontScale(1.5f);
        scoreTextLabel.setColor(GOLD_COLOR);

        scoreLabel = new Label(String.format("%,d", score), skin);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setFontScale(2.5f);
        scoreLabel.setColor(GOLD_COLOR);

        // Score calculation breakdown
        Label calculationLabel = new Label(
            String.format("(%s × %d kills)", formatTime(survivalTime), kills),
            skin
        );
        calculationLabel.setAlignment(Align.center);
        calculationLabel.setFontScale(0.9f);
        calculationLabel.setColor(LABEL_COLOR);

        scoreTable.add(scoreTextLabel).row();
        scoreTable.add(scoreLabel).padTop(10).row();
        scoreTable.add(calculationLabel).padTop(5);

        mainPanel.add(scoreTable).width(PANEL_WIDTH - 100).padBottom(40).row();
    }

    private void createButtonsSection() {
        Table buttonTable = new Table();
        buttonTable.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).space(20);

        // Main Menu button
        TextButton mainMenuButton = createStyledButton("Main Menu", VICTORY_COLOR);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                transitionToMainMenu();
            }
        });

        // Play Again button
        TextButton playAgainButton = createStyledButton("Play Again", new Color(0.4f, 0.6f, 0.9f, 1f));
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                transitionToPreGame();
            }
        });

        buttonTable.add(mainMenuButton);
        buttonTable.add(playAgainButton);

        mainPanel.add(buttonTable);
    }

    private TextButton createStyledButton(String text, Color color) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        style.fontColor = color;
        style.overFontColor = color.cpy().mul(0.8f);
        style.downFontColor = color.cpy().mul(0.6f);

        TextButton button = new TextButton(text, style);
        button.getLabel().setFontScale(1.2f);

        if (buttonTexture != null) {
            button.setBackground(new TextureRegionDrawable(new TextureRegion(buttonTexture)));
        }

        return button;
    }

    private void addStatRow(Table table, String label, String value) {
        Label labelText = new Label(label, skin);
        labelText.setFontScale(1.2f);
        labelText.setColor(LABEL_COLOR);

        Label valueText = new Label(value, skin);
        valueText.setFontScale(1.2f);
        valueText.setColor(TEXT_COLOR);

        table.add(labelText).left().padRight(40).padBottom(10);
        table.add(valueText).right().padBottom(10).row();
    }

    private String formatTime(float totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void setupAnimations() {
        // Panel fade in and scale animation
        mainPanel.setScale(0.8f);
        mainPanel.getColor().a = 0f;
        mainPanel.addAction(Actions.parallel(
            Actions.fadeIn(0.5f),
            Actions.scaleTo(1f, 1f, 0.5f, Interpolation.bounceOut)
        ));

        // Delayed animations for individual elements
        statusLabel.setScale(0f);
        statusLabel.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.scaleTo(1f, 1f, 0.5f, Interpolation.elastic)
        ));

        scoreLabel.getColor().a = 0f;
        scoreLabel.addAction(Actions.sequence(
            Actions.delay(0.6f),
            Actions.fadeIn(0.5f)
        ));
    }

    private void playEndGameSound() {
        if (App.isSFX()) {
            if (status == EndGameStatus.VICTORY) {
                // Play victory sound
                GameAssetManager.getGameAssetManager().playWin();
            } else {
                // Play defeat sound
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
            Actions.fadeOut(0.5f),
            Actions.run(() -> {
                Main.getMain().setScreen(new MainMenu(skin));
            })
        ));
    }

    private void transitionToPreGame() {
        stage.getRoot().addAction(Actions.sequence(
            Actions.fadeOut(0.5f),
            Actions.run(() -> {
                Main.getMain().setScreen(new PreGameMenu(skin));
            })
        ));
    }

    @Override
    public void render(float delta) {
        // Clear screen with dark background
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update animations
        animationTime += delta;
        updateAnimations();

        stage.act(delta);
        stage.draw();
    }

    private void updateAnimations() {
        if (status == EndGameStatus.VICTORY) {
            float pulse = 1f + 0.1f * (float) Math.sin(animationTime * 3);
            statusLabel.setFontScale(3f * pulse);
        }

        float glow = 0.8f + 0.2f * (float) Math.sin(animationTime * 2);
        scoreLabel.setColor(GOLD_COLOR.r, GOLD_COLOR.g, GOLD_COLOR.b, glow);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        // Reposition leaves decorations
        if (leavesDecorations != null) {
            for (Image leaf : leavesDecorations) {
                leaf.remove();
            }
            leavesDecorations = UIHelper.addLeavesDecoration(stage);
            for (Image leaf : leavesDecorations) {
                leaf.toBack();
            }
        }
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
        VICTORY("VICTORY!", "You survived until dawn!", VICTORY_COLOR),
        DEFEAT("DEFEATED", "You couldn't survive the night...", DEFEAT_COLOR),
        GIVE_UP("GAVE UP", "You abandoned the fight...", DEFEAT_COLOR);

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
