package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.ProfileMenuController;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.enums.Language;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.UIHelper;

public class ProfileMenu implements Screen {
    private static final float BUTTON_WIDTH = 350f;
    private static final float BUTTON_HEIGHT = 60f;
    private static final float PADDING = 20f;
    private static final float SECTION_SPACING = 30f;

    private static final Color TITLE_COLOR = new Color(0.9f, 0.7f, 0.2f, 1f); // Gold
    private static final Color BUTTON_TEXT_COLOR = Color.SALMON;
    private static final Color BUTTON_HOVER_COLOR = new Color(1f, 0.4f, 0.6f, 1f);
    private static final Color INFO_TEXT_COLOR = new Color(0.8f, 0.8f, 0.8f, 1f);
    private static final Color PANEL_COLOR = new Color(0.1f, 0.1f, 0.15f, 0.95f);

    private final TextButton changeUsernameButton;
    private final TextButton changePasswordButton;
    private final TextButton changeAvatarButton;
    private final TextButton deleteAccountButton;
    private final TextButton backButton;

    private Stage stage;
    private Skin skin;
    private Image[] leavesDecorations;
    private ProfileMenuController controller;

    private Table mainTable;
    private Table userInfoPanel;
    private Table buttonsPanel;

    private Image avatarImage;
    private Label usernameLabel;
    private Label userScoreLabel;
    private Label userStatsLabel;

    private Texture panelTexture;

    public ProfileMenu(Skin skin) {
        this.skin = skin;

        this.panelTexture = GameAssetManager.getGameAssetManager().getPanel();

        TextButton.TextButtonStyle buttonStyle = createButtonStyle();

        this.changeUsernameButton = new TextButton(Language.ChangeUsername.getText(), buttonStyle);
        this.changePasswordButton = new TextButton(Language.ChangePassword.getText(), buttonStyle);
        this.changeAvatarButton = new TextButton(Language.ChangeAvatar.getText(), buttonStyle);
        this.deleteAccountButton = new TextButton(Language.DeleteAccount.getText(), buttonStyle);
        this.backButton = new TextButton("Back to Main Menu", buttonStyle);

        setupButtonAnimations();

        this.controller = new ProfileMenuController(this);
    }

    private TextButton.TextButtonStyle createButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = GameAssetManager.getGameAssetManager().getChevyRayFont();
        style.fontColor = BUTTON_TEXT_COLOR;
        style.overFontColor = BUTTON_HOVER_COLOR;
        style.downFontColor = BUTTON_TEXT_COLOR.cpy().mul(0.7f);
        return style;
    }

    private void setupButtonAnimations() {
        setupButtonHover(changeUsernameButton);
        setupButtonHover(changePasswordButton);
        setupButtonHover(changeAvatarButton);
        setupButtonHover(deleteAccountButton);
        setupButtonHover(backButton);
    }

    private void setupButtonHover(final TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                button.getLabel().addAction(Actions.scaleTo(1.1f, 1.1f, 0.15f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                button.getLabel().addAction(Actions.scaleTo(1.0f, 1.0f, 0.15f));
            }
        });
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Add leaves decoration
        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        // Create main layout
        createMainLayout();

        // Add animations
        animateIn();
    }

    private void createMainLayout() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(PADDING);

        // Title
        createTitle();

        // User info panel
        createUserInfoPanel();

        // Buttons panel
        createButtonsPanel();

        // Back button
        createBackButton();

        stage.addActor(mainTable);

        // Ensure leaves are in background
        for (Image leaf : leavesDecorations) {
            leaf.toBack();
        }
    }

    private void createTitle() {
        Label titleLabel = new Label(Language.Profile.getText(), skin, "title");
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(TITLE_COLOR);

        Table titleContainer = new Table();
        titleContainer.add(titleLabel).padBottom(10).row();

        Label subtitleLabel = new Label("Manage your account settings", skin);
        subtitleLabel.setAlignment(Align.center);
        subtitleLabel.setFontScale(1.0f);
        subtitleLabel.setColor(INFO_TEXT_COLOR);

        titleContainer.add(subtitleLabel);

        mainTable.add(titleContainer).padBottom(SECTION_SPACING).row();
    }

    private void createUserInfoPanel() {
        userInfoPanel = new Table();
        userInfoPanel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        userInfoPanel.pad(PADDING);

        User currentUser = App.getLoggedInUser();

        // Avatar section
        createAvatarSection(currentUser);

        // User info section
        createUserInfoSection(currentUser);

        mainTable.add(userInfoPanel).width(800).padBottom(SECTION_SPACING).row();
    }

    private void createAvatarSection(User user) {
        Table avatarContainer = new Table();

        // Load avatar
        String avatarPath = "images/avatars/avatar1.png"; // Default
        if (user != null && user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            avatarPath = "images/avatars/" + user.getAvatarPath();
        }

        if (Gdx.files.internal(avatarPath).exists()) {
            avatarImage = new Image(new Texture(Gdx.files.internal(avatarPath)));
        } else {
            avatarImage = new Image(new Texture(Gdx.files.internal("images/avatars/avatar1.png")));
        }

        // Style avatar with border
        Table avatarBorder = new Table();
        avatarBorder.setBackground(new TextureRegionDrawable(createBorderTexture()));
        avatarBorder.add(avatarImage).size(120, 120).pad(5);

        avatarContainer.add(avatarBorder).padRight(PADDING * 2);

        userInfoPanel.add(avatarContainer);
    }

    private void createUserInfoSection(User user) {
        Table infoContainer = new Table();
        infoContainer.left();

        // Username
        String username = user != null ? user.getUsername() : "Guest";
        usernameLabel = new Label(username, skin);
        usernameLabel.setFontScale(2.0f);
        usernameLabel.setColor(Color.WHITE);
        infoContainer.add(usernameLabel).left().row();

        // Score
        int score = user != null ? user.getScore() : 0;
        userScoreLabel = new Label("Score: " + score, skin);
        userScoreLabel.setFontScale(1.3f);
        userScoreLabel.setColor(TITLE_COLOR);
        infoContainer.add(userScoreLabel).left().padTop(10).row();

        // Stats
        if (user != null && !user.isGuest()) {
            String stats = String.format("Kills: %d | Deaths: %d | Survival Time: %.1f min",
                user.getKills(), user.getDeaths(), user.getSurvivalTime() / 60f);
            userStatsLabel = new Label(stats, skin);
            userStatsLabel.setFontScale(1.0f);
            userStatsLabel.setColor(INFO_TEXT_COLOR);
            infoContainer.add(userStatsLabel).left().padTop(5).row();
        }

        userInfoPanel.add(infoContainer).expandX().left();
    }

    private void createButtonsPanel() {
        buttonsPanel = new Table();
        buttonsPanel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        buttonsPanel.pad(PADDING * 2);

        // Create 2x2 grid of buttons
        Table buttonGrid = new Table();
        buttonGrid.defaults().size(BUTTON_WIDTH, BUTTON_HEIGHT).pad(10);

        // Add button containers with icons
        buttonGrid.add(createButtonContainer(changeUsernameButton, "👤")).padRight(20);
        buttonGrid.add(createButtonContainer(changePasswordButton, "🔒")).row();

        buttonGrid.add(createButtonContainer(changeAvatarButton, "🖼️")).padRight(20);
        buttonGrid.add(createButtonContainer(deleteAccountButton, "⚠️"));

        buttonsPanel.add(buttonGrid);

        mainTable.add(buttonsPanel).width(800).padBottom(SECTION_SPACING).row();
    }

    private Table createButtonContainer(TextButton button, String icon) {
        Table container = new Table();
        container.setBackground(createButtonBackground());
        container.pad(15);

        Label iconLabel = new Label(icon, skin);
        iconLabel.setFontScale(1.5f);
        container.add(iconLabel).padRight(10);
        container.add(button).expandX().fillX();

        return container;
    }

    private void createBackButton() {
        Table backContainer = new Table();
        backContainer.add(backButton).size(200, 50);

        mainTable.add(backContainer).padTop(PADDING);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                animateOut(() -> {
                    Main.getMain().setScreen(new MainMenu(skin));
                });
            }
        });
    }

    private TextureRegionDrawable createBorderTexture() {
        return new TextureRegionDrawable(new TextureRegion(panelTexture));
    }

    private TextureRegionDrawable createButtonBackground() {
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(panelTexture));
        drawable.setMinWidth(BUTTON_WIDTH);
        drawable.setMinHeight(BUTTON_HEIGHT);
        return drawable;
    }

    private void animateIn() {
        mainTable.getColor().a = 0f;
        mainTable.addAction(Actions.fadeIn(0.3f));

        if (userInfoPanel != null) {
            userInfoPanel.setScale(0.9f);
            userInfoPanel.addAction(Actions.scaleTo(1f, 1f, 0.3f));
        }

        if (buttonsPanel != null) {
            buttonsPanel.setScale(0.9f);
            buttonsPanel.addAction(Actions.sequence(
                Actions.delay(0.1f),
                Actions.scaleTo(1f, 1f, 0.3f)
            ));
        }
    }

    private void animateOut(Runnable onComplete) {
        mainTable.addAction(Actions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run(onComplete)
        ));
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        if (leavesDecorations != null) {
            leavesDecorations[0].remove();
            leavesDecorations[1].remove();
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

    // Getters for controller
    public Stage getStage() {
        return stage;
    }

    public TextButton getChangeUsernameButton() {
        return changeUsernameButton;
    }

    public TextButton getChangePasswordButton() {
        return changePasswordButton;
    }

    public TextButton getDeleteAccountButton() {
        return deleteAccountButton;
    }

    public TextButton getChangeAvatarButton() {
        return changeAvatarButton;
    }
}
