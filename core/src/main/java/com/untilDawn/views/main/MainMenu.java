package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.MainMenuController;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.UIHelper;

public class MainMenu implements Screen {
    private final Stage stage;
    private final Table mainTable;
    private final Table userInfoTable;
    private final TextButton playButton;
    private final TextButton continueGameButton;
    private final TextButton settingsButton;
    private final TextButton profileButton;
    private final TextButton scoreboardButton;
    private final TextButton talentsButton;
    private final TextButton logoutButton;
    private final Label usernameLabel;
    private final Label scoreLabel;
    private final Skin skin;
    private final User currentUser;
    private Image[] leavesDecorations;
    private MainMenuController controller;
    private Image avatarImage;

    public MainMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        currentUser = App.getLoggedInUser();

        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);

        userInfoTable = new Table();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("font");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.LIGHT_GRAY;
        buttonStyle.downFontColor = Color.GRAY;

        playButton = new TextButton("Play New Game", buttonStyle);
        continueGameButton = new TextButton("Continue Game", buttonStyle);
        settingsButton = new TextButton("Settings", buttonStyle);
        profileButton = new TextButton("Profile", buttonStyle);
        scoreboardButton = new TextButton("Scoreboard", buttonStyle);
        talentsButton = new TextButton("Hints", buttonStyle);
        logoutButton = new TextButton("Logout", buttonStyle);

        String avatarPath = "images/avatars/avatar1.png"; // Default avatar
        if (currentUser != null && currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty()) {
            avatarPath = "images/avatars/" + currentUser.getAvatarPath();
        }

        if (Gdx.files.internal(avatarPath).exists()) {
            avatarImage = new Image(new Texture(Gdx.files.internal(avatarPath)));
        } else {
            // Fallback to default avatar if file doesn't exist
            avatarImage = new Image(new Texture(Gdx.files.internal("images/avatars/avatar1.png")));
        }

        avatarImage.setSize(80, 80);

        // Create user information labels
        String username = currentUser != null ? currentUser.getUsername() : "Guest";
        usernameLabel = new Label(username, skin);
        usernameLabel.setFontScale(1.5f);

        int score = 0;
        if (currentUser != null) {
            score = currentUser.getScore();
        }
        scoreLabel = new Label("Score: " + score, skin);

        // Set up the stage
        setupLayout();
//        setupListeners();
        this.controller = new MainMenuController(this);
        stage.addActor(mainTable);
    }

    private void setupLayout() {
        userInfoTable.add(avatarImage).size(80, 80).padRight(20);

        Table userTextInfo = new Table();
        userTextInfo.add(usernameLabel).left().row();
        userTextInfo.add(scoreLabel).left().padTop(5);

        userInfoTable.add(userTextInfo).left().padRight(50);
        userInfoTable.add(logoutButton).right().expandX();

        mainTable.top().padTop(30);
        mainTable.add(userInfoTable).fillX().padBottom(50).row();

        Table buttonTable = new Table();
        buttonTable.defaults().size(300, 60).space(15);

        buttonTable.add(playButton).row();
        buttonTable.add(continueGameButton).row();
        buttonTable.add(profileButton).row();
        buttonTable.add(scoreboardButton).row();
        buttonTable.add(settingsButton).row();
        buttonTable.add(talentsButton).row();

        mainTable.add(buttonTable);

        if (currentUser == null || currentUser.isGuest()) {
            continueGameButton.setDisabled(true);
            continueGameButton.getLabel().setColor(Color.GRAY);
        }
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        if (leavesDecorations != null) {
            leavesDecorations[0].remove();
            leavesDecorations[1].remove();
            leavesDecorations = UIHelper.addLeavesDecoration(stage);

            leavesDecorations[0].toBack();
            leavesDecorations[1].toBack();
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
        stage.dispose();
    }

    public TextButton getPlayButton() {
        return playButton;
    }

    public TextButton getContinueGameButton() {
        return continueGameButton;
    }

    public TextButton getSettingsButton() {
        return settingsButton;
    }

    public TextButton getProfileButton() {
        return profileButton;
    }

    public TextButton getScoreboardButton() {
        return scoreboardButton;
    }

    public TextButton getTalentsButton() {
        return talentsButton;
    }

    public TextButton getLogoutButton() {
        return logoutButton;
    }


    public Stage getStage() {
        return stage;
    }
}
