package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.enums.Language;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.UIHelper;

import java.util.ArrayList;

public class ScoreBoardMenu implements Screen {
    // Colors for visual distinction
    private static final Color GOLD_COLOR = new Color(1.0f, 0.843f, 0.0f, 1.0f);
    private static final Color SILVER_COLOR = new Color(0.753f, 0.753f, 0.753f, 1.0f);
    private static final Color BRONZE_COLOR = new Color(0.804f, 0.498f, 0.196f, 1.0f);
    private static final Color CURRENT_USER_COLOR = new Color(0.4f, 0.8f, 1.0f, 1.0f);
    private static final Color HEADER_COLOR = new Color(0.2f, 0.6f, 0.9f, 1.0f);
    private static final Color PANEL_BG_COLOR = new Color(0.1f, 0.1f, 0.15f, 0.95f);
    private Stage stage;
    private Skin skin;
    private Image[] leavesDecorations;
    // UI Components
    private Table mainTable;
    private Table leaderboardTable;
    private ScrollPane scrollPane;
    // Sorting buttons
    private TextButton sortByScoreButton;
    private TextButton sortByUsernameButton;
    private TextButton sortByKillsButton;
    private TextButton sortBySurvivalTimeButton;
    private TextButton backButton;
    // Current sort state
    private SortType currentSort = SortType.SCORE;
    private boolean ascending = false;

    public ScoreBoardMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        // Add leaves decoration
        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        // Main container
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Title with enhanced styling
        Label titleLabel = new Label(Language.Leaderboard.getText(), skin, "title");
        titleLabel.setColor(HEADER_COLOR);
        titleLabel.setAlignment(Align.center);

        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        TextureRegionDrawable panelDrawable = new TextureRegionDrawable(new TextureRegion(panelTexture));

        // Create sorting buttons container
        Table sortButtonsTable = new Table();
        sortButtonsTable.setBackground(panelDrawable);
        sortButtonsTable.pad(15);
        createSortingButtons(sortButtonsTable);

        // Create leaderboard container
        leaderboardTable = new Table();
        leaderboardTable.top();
        populateLeaderboard();

        // Create scroll pane for leaderboard
        scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Container for scroll pane with background
        Table scrollContainer = new Table();
        scrollContainer.setBackground(panelDrawable);
        scrollContainer.add(scrollPane).expand().fill().pad(20);

        // Create back button
        createBackButton();

        // Layout
        mainTable.add(titleLabel).padBottom(30).row();
        mainTable.add(sortButtonsTable).fillX().padBottom(20).row();
        mainTable.add(scrollContainer).size(1000, 600).padBottom(20).row();
        mainTable.add(backButton).width(200).height(60).pad(10);

        stage.addActor(mainTable);
    }

    private void createSortingButtons(Table container) {
        // Style for sort buttons
        TextButton.TextButtonStyle sortButtonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        sortButtonStyle.fontColor = Color.WHITE;
        sortButtonStyle.overFontColor = Color.LIGHT_GRAY;
        sortButtonStyle.downFontColor = Color.GRAY;

        // Create buttons
        sortByScoreButton = new TextButton(Language.SortByScore.getText(), sortButtonStyle);
        sortByUsernameButton = new TextButton(Language.SortByUsername.getText(), sortButtonStyle);
        sortByKillsButton = new TextButton(Language.SortByKills.getText(), sortButtonStyle);
        sortBySurvivalTimeButton = new TextButton(Language.SortBySurvivalTime.getText(), sortButtonStyle);

        // Add listeners
        sortByScoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                sortBy(SortType.SCORE);
            }
        });

        sortByUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                sortBy(SortType.USERNAME);
            }
        });

        sortByKillsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                sortBy(SortType.KILLS);
            }
        });

        sortBySurvivalTimeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                sortBy(SortType.SURVIVAL_TIME);
            }
        });

        // Layout buttons
        container.add(new Label("Sort By: ", skin)).padRight(20);
        container.add(sortByScoreButton).width(150).padRight(10);
        container.add(sortByUsernameButton).width(150).padRight(10);
        container.add(sortByKillsButton).width(150).padRight(10);
        container.add(sortBySurvivalTimeButton).width(200);

        // Highlight current sort
        updateSortButtonStyles();
    }

    private void createBackButton() {
        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.font = skin.getFont("font");
        backButtonStyle.fontColor = Color.WHITE;
        backButtonStyle.overFontColor = Color.LIGHT_GRAY;
        backButtonStyle.downFontColor = Color.GRAY;

        backButton = new TextButton("Back", backButtonStyle);
        backButton.getLabel().setFontScale(1.2f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                Main.getMain().setScreen(new MainMenu(skin));
            }
        });
    }

    private void populateLeaderboard() {
        leaderboardTable.clear();

        // Create header
        createLeaderboardHeader();

        // Get all users
        ArrayList<User> users = new ArrayList<User>(App.getUsers().values());

        // Filter out guest users
        users.removeIf(User::isGuest);

        // Sort users based on current sort type
        sortUsers(users);

        // Display top 10 users
        User currentUser = App.getLoggedInUser();
        int displayCount = Math.min(10, users.size());

        for (int i = 0; i < displayCount; i++) {
            User user = users.get(i);
            createUserRow(user, i + 1, user.equals(currentUser));
        }

        // If current user is not in top 10, show them separately
        if (currentUser != null && !currentUser.isGuest()) {
            int currentUserRank = users.indexOf(currentUser) + 1;
            if (currentUserRank > 10) {
                // Add separator
                leaderboardTable.add(new Label("...", skin)).colspan(6).padTop(20).padBottom(20).row();
                createUserRow(currentUser, currentUserRank, true);
            }
        }
    }

    private void createLeaderboardHeader() {
        Table headerTable = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        headerTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        headerTable.pad(15);

        Label rankLabel = new Label("Rank", skin);
        Label usernameLabel = new Label("Username", skin);
        Label scoreLabel = new Label("Score", skin);
        Label killsLabel = new Label("Kills", skin);
        Label survivalLabel = new Label("Survival Time", skin);

        // Style headers
        rankLabel.setColor(HEADER_COLOR);
        usernameLabel.setColor(HEADER_COLOR);
        scoreLabel.setColor(HEADER_COLOR);
        killsLabel.setColor(HEADER_COLOR);
        survivalLabel.setColor(HEADER_COLOR);

        rankLabel.setFontScale(1.2f);
        usernameLabel.setFontScale(1.2f);
        scoreLabel.setFontScale(1.2f);
        killsLabel.setFontScale(1.2f);
        survivalLabel.setFontScale(1.2f);

        headerTable.add(rankLabel).width(80).center();
        headerTable.add(usernameLabel).width(250).center();
        headerTable.add(scoreLabel).width(150).center();
        headerTable.add(killsLabel).width(150).center();
        headerTable.add(survivalLabel).width(200).center();

        leaderboardTable.add(headerTable).fillX().padBottom(10).row();
    }

    private void createUserRow(User user, int rank, boolean isCurrentUser) {
        Table rowTable = new Table();

        // Background panel
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        rowTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));

        // Different background opacity for current user
        if (isCurrentUser) {
            rowTable.setColor(CURRENT_USER_COLOR.r, CURRENT_USER_COLOR.g, CURRENT_USER_COLOR.b, 0.3f);
        } else {
            rowTable.setColor(PANEL_BG_COLOR);
        }

        rowTable.pad(10);

        // Rank with special colors for top 3
        Label rankLabel = new Label(String.valueOf(rank), skin);
        rankLabel.setFontScale(1.1f);

        if (rank == 1) {
            rankLabel.setColor(GOLD_COLOR);
            rankLabel.setText("🥇 " + rank);
        } else if (rank == 2) {
            rankLabel.setColor(SILVER_COLOR);
            rankLabel.setText("🥈 " + rank);
        } else if (rank == 3) {
            rankLabel.setColor(BRONZE_COLOR);
            rankLabel.setText("🥉 " + rank);
        } else {
            rankLabel.setColor(Color.WHITE);
        }

        // Username
        Label usernameLabel = new Label(user.getUsername(), skin);
        usernameLabel.setFontScale(1.0f);
        if (isCurrentUser) {
            usernameLabel.setColor(CURRENT_USER_COLOR);
            usernameLabel.setText("▶ " + user.getUsername());
        } else {
            usernameLabel.setColor(Color.WHITE);
        }

        // Score
        Label scoreLabel = new Label(String.valueOf(user.getScore()), skin);
        scoreLabel.setColor(Color.YELLOW);
        scoreLabel.setFontScale(1.0f);

        // Kills
        Label killsLabel = new Label(String.valueOf(user.getKills()), skin);
        killsLabel.setColor(new Color(1f, 0.4f, 0.4f, 1f));
        killsLabel.setFontScale(1.0f);

        // Survival time
        String survivalTimeStr = formatSurvivalTime(user.getSurvivalTime());
        Label survivalLabel = new Label(survivalTimeStr, skin);
        survivalLabel.setColor(new Color(0.4f, 1f, 0.4f, 1f));
        survivalLabel.setFontScale(1.0f);

        // Add to row
        rowTable.add(rankLabel).width(80).center();
        rowTable.add(usernameLabel).width(250).left().padLeft(20);
        rowTable.add(scoreLabel).width(150).center();
        rowTable.add(killsLabel).width(150).center();
        rowTable.add(survivalLabel).width(200).center();

        leaderboardTable.add(rowTable).fillX().padBottom(5).row();
    }

    private String formatSurvivalTime(float totalSeconds) {
        int hours = (int) (totalSeconds / 3600);
        int minutes = (int) ((totalSeconds % 3600) / 60);
        int seconds = (int) (totalSeconds % 60);

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    private void sortUsers(ArrayList<User> users) {
        switch (currentSort) {
            case SCORE:
                users.sort((a, b) -> ascending ?
                    Integer.compare(a.getScore(), b.getScore()) :
                    Integer.compare(b.getScore(), a.getScore()));
                break;
            case USERNAME:
                users.sort((a, b) -> ascending ?
                    a.getUsername().compareToIgnoreCase(b.getUsername()) :
                    b.getUsername().compareToIgnoreCase(a.getUsername()));
                break;
            case KILLS:
                users.sort((a, b) -> ascending ?
                    Integer.compare(a.getKills(), b.getKills()) :
                    Integer.compare(b.getKills(), a.getKills()));
                break;
            case SURVIVAL_TIME:
                users.sort((a, b) -> ascending ?
                    Float.compare(a.getSurvivalTime(), b.getSurvivalTime()) :
                    Float.compare(b.getSurvivalTime(), a.getSurvivalTime()));
                break;
        }
    }

    private void sortBy(SortType sortType) {
        if (currentSort == sortType) {
            ascending = !ascending;
        } else {
            currentSort = sortType;
            ascending = false;
        }

        updateSortButtonStyles();
        populateLeaderboard();
    }

    private void updateSortButtonStyles() {
        // Reset all button colors
        sortByScoreButton.getLabel().setColor(Color.WHITE);
        sortByUsernameButton.getLabel().setColor(Color.WHITE);
        sortByKillsButton.getLabel().setColor(Color.WHITE);
        sortBySurvivalTimeButton.getLabel().setColor(Color.WHITE);

        // Highlight active sort button
        TextButton activeButton = null;
        switch (currentSort) {
            case SCORE:
                activeButton = sortByScoreButton;
                break;
            case USERNAME:
                activeButton = sortByUsernameButton;
                break;
            case KILLS:
                activeButton = sortByKillsButton;
                break;
            case SURVIVAL_TIME:
                activeButton = sortBySurvivalTimeButton;
                break;
        }

        if (activeButton != null) {
            activeButton.getLabel().setColor(HEADER_COLOR);
            String arrow = ascending ? " ▲" : " ▼";
            activeButton.setText(activeButton.getText().toString().replaceAll(" [▲▼]", "") + arrow);
        }
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
        if (stage != null) {
            stage.dispose();
        }
    }

    // Enum for sort types
    private enum SortType {
        SCORE, USERNAME, KILLS, SURVIVAL_TIME
    }
}
