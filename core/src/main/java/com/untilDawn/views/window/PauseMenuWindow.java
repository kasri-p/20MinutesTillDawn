package com.untilDawn.views.window;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.App;
import com.untilDawn.models.Player;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.utils.CheatCodeManager;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.Map;

public class PauseMenuWindow extends Window {
    // Constants
    private static final float WINDOW_WIDTH = 1200f;
    private static final float WINDOW_HEIGHT = 800f;
    private static final float PADDING = 25f;
    private static final float SECTION_SPACING = 20f;
    private static final float BUTTON_WIDTH = 180f;
    private static final float BUTTON_HEIGHT = 50f;

    // Dark color scheme
    private static final Color TITLE_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f);
    private static final Color SECTION_HEADER_COLOR = new Color(0.7f, 0.8f, 0.9f, 1f);
    private static final Color ABILITY_ACTIVE_COLOR = new Color(0.3f, 0.8f, 0.3f, 1f);
    private static final Color ABILITY_COOLDOWN_COLOR = new Color(0.8f, 0.6f, 0.2f, 1f);
    private static final Color ABILITY_READY_COLOR = new Color(0.4f, 0.7f, 0.9f, 1f);
    private static final Color TEXT_COLOR = new Color(0.8f, 0.8f, 0.8f, 1f);
    private static final Color CHEAT_SUCCESS_COLOR = new Color(0.3f, 0.8f, 0.3f, 1f);
    private static final Color CHEAT_ERROR_COLOR = new Color(0.8f, 0.3f, 0.3f, 1f);
    private static final Color PANEL_COLOR = new Color(0.1f, 0.1f, 0.15f, 0.95f);

    // Core components
    private final Player player;
    private final GameController gameController;
    private final Runnable onResume;
    private final Runnable onGiveUp;
    private final Runnable onSaveAndExit;

    // UI Components
    private Table contentArea;
    private TextField cheatInputField;
    private Label cheatStatusLabel;
    private CheckBox blackWhiteCheckBox;
    private CheckBox soundEffectsCheckBox;
    private CheatCodeManager cheatManager;

    // Navigation state
    private MenuSection currentSection = MenuSection.MAIN;
    private TextButton[] navigationButtons;

    public PauseMenuWindow(Skin skin, Player player, GameController gameController, Stage stage,
                           Runnable onResume, Runnable onGiveUp, Runnable onSaveAndExit) {
        super("", skin);
        this.player = player;
        this.gameController = gameController;
        this.onResume = onResume;
        this.onGiveUp = onGiveUp;
        this.onSaveAndExit = onSaveAndExit;

        this.cheatManager = CheatCodeManager.getInstance();
        this.cheatManager.setGameController(gameController);

        setupWindow(stage);
        createContent(skin);
    }

    private void setupWindow(Stage stage) {
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        float centerX = (stage.getWidth() - getWidth()) / 2;
        float centerY = (stage.getHeight() - getHeight()) / 2;
        setPosition(centerX, centerY);

        setModal(true);
        setMovable(false);
        getTitleLabel().setText("");

        setColor(PANEL_COLOR);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(contentArea).center().expand();

        stage.addActor(root);
    }

    private void createContent(Skin skin) {
        clear();

        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.pad(PADDING);

        createMainTitle(mainContainer, skin);

        createNavigationBar(mainContainer, skin);

        // Create content area with dark panel background
        contentArea = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        contentArea.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        contentArea.setColor(new Color(0.05f, 0.05f, 0.1f, 0.9f));
        contentArea.pad(20);

        showMainSection(skin);

        mainContainer.add(contentArea).size(WINDOW_WIDTH - 80, 500).padBottom(SECTION_SPACING).row();

        createActionButtons(mainContainer, skin);

        add(mainContainer).expand().fill();
    }

    private void createMainTitle(Table container, Skin skin) {
        Label titleLabel = new Label("GAME PAUSED", skin, "title");
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(TITLE_COLOR);

        Label subtitleLabel = new Label("Game progress is automatically saved", skin);
        subtitleLabel.setAlignment(Align.center);
        subtitleLabel.setFontScale(0.9f);
        subtitleLabel.setColor(TEXT_COLOR);

        Table titleTable = new Table();
        titleTable.add(titleLabel).row();
        titleTable.add(subtitleLabel).padTop(5);

        container.add(titleTable).padBottom(SECTION_SPACING).row();
    }

    private void createNavigationBar(Table container, Skin skin) {
        Table navBar = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        navBar.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        navBar.setColor(new Color(0.08f, 0.08f, 0.12f, 0.95f));
        navBar.pad(10);

        navigationButtons = new TextButton[4];
        String[] buttonTexts = {"Main Menu", "Abilities", "Cheat Codes", "Settings"};
        MenuSection[] sections = {MenuSection.MAIN, MenuSection.ABILITIES, MenuSection.CHEATS, MenuSection.SETTINGS};

        for (int i = 0; i < buttonTexts.length; i++) {
            final MenuSection section = sections[i];
            TextButton navButton = new TextButton(buttonTexts[i], skin);
            navButton.getLabel().setFontScale(1.0f);

            navButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    playClick();
                    switchToSection(section, skin);
                }
            });

            navigationButtons[i] = navButton;
            navBar.add(navButton).width(180).height(40).padRight(10);
        }

        updateNavigationButtons();
        container.add(navBar).fillX().padBottom(15).row();
    }

    private void updateNavigationButtons() {
        for (int i = 0; i < navigationButtons.length; i++) {
            TextButton button = navigationButtons[i];
            if (MenuSection.values()[i] == currentSection) {
                button.setColor(SECTION_HEADER_COLOR);
                button.getLabel().setColor(Color.BLACK);
            } else {
                button.setColor(Color.GRAY);
                button.getLabel().setColor(Color.WHITE);
            }
        }
    }

    private void switchToSection(MenuSection section, Skin skin) {
        currentSection = section;
        updateNavigationButtons();

        switch (section) {
            case MAIN:
                showMainSection(skin);
                break;
            case ABILITIES:
                showAbilitiesSection(skin);
                break;
            case CHEATS:
                showCheatSection(skin);
                break;
            case SETTINGS:
                showSettingsSection(skin);
                break;
        }
    }

    private void showMainSection(Skin skin) {
        contentArea.clear();

        // Game Status Section
        Label statusTitle = new Label("GAME STATUS", skin);
        statusTitle.setFontScale(1.4f);
        statusTitle.setColor(SECTION_HEADER_COLOR);
        statusTitle.setAlignment(Align.center);
        contentArea.add(statusTitle).padBottom(20).row();

        // Create two-column layout for stats
        Table statsContainer = new Table();

        // Left column - Player Stats
        Table leftStats = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        leftStats.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        leftStats.setColor(new Color(0.08f, 0.08f, 0.12f, 0.8f));
        leftStats.pad(15);

        Label playerStatsTitle = new Label("Player Information", skin);
        playerStatsTitle.setFontScale(1.1f);
        playerStatsTitle.setColor(SECTION_HEADER_COLOR);
        leftStats.add(playerStatsTitle).padBottom(10).row();

        addStatRow(leftStats, skin, "Level:", String.valueOf(player.getLevel()));
        addStatRow(leftStats, skin, "Health:", player.getPlayerHealth() + "/" + player.getMaxHealth());
        addStatRow(leftStats, skin, "Experience:", String.valueOf(player.getXP()));
        addStatRow(leftStats, skin, "Kills:", String.valueOf(player.getKills()));

        Table rightStats = new Table();
        rightStats.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        rightStats.setColor(new Color(0.08f, 0.08f, 0.12f, 0.8f));
        rightStats.pad(15);

        Label gameStatsTitle = new Label("Game Information", skin);
        gameStatsTitle.setFontScale(1.1f);
        gameStatsTitle.setColor(SECTION_HEADER_COLOR);
        rightStats.add(gameStatsTitle).padBottom(10).row();

        addStatRow(rightStats, skin, "Time Remaining:", gameController.getFormattedRemainingTime());
        addStatRow(rightStats, skin, "Time Limit:", gameController.getTimeLimit() + " minutes");
        addStatRow(rightStats, skin, "Weapon:", gameController.getWeaponController().getWeapon().getWeapon().getName());
        addStatRow(rightStats, skin, "Ammo:", String.valueOf(gameController.getWeaponController().getWeapon().getAmmo()));

        statsContainer.add(leftStats).width(350).padRight(20);
        statsContainer.add(rightStats).width(350).padLeft(20);

        contentArea.add(statsContainer).padBottom(30).row();

        Label quickNavTitle = new Label("QUICK NAVIGATION", skin);
        quickNavTitle.setFontScale(1.2f);
        quickNavTitle.setColor(SECTION_HEADER_COLOR);
        quickNavTitle.setAlignment(Align.center);
        contentArea.add(quickNavTitle).padBottom(15).row();

        Table quickNavTable = new Table();
        quickNavTable.defaults().width(160).height(35).space(15);

        TextButton viewAbilitiesBtn = new TextButton("View Abilities", skin);
        viewAbilitiesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                switchToSection(MenuSection.ABILITIES, skin);
            }
        });

        TextButton openCheatsBtn = new TextButton("Cheat Codes", skin);
        openCheatsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                switchToSection(MenuSection.CHEATS, skin);
            }
        });

        TextButton settingsBtn = new TextButton("Game Settings", skin);
        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                switchToSection(MenuSection.SETTINGS, skin);
            }
        });

        quickNavTable.add(viewAbilitiesBtn);
        quickNavTable.add(openCheatsBtn);
        quickNavTable.add(settingsBtn);

        contentArea.add(quickNavTable);
    }

    private void showAbilitiesSection(Skin skin) {
        contentArea.clear();

        Label title = new Label("ACQUIRED ABILITIES", skin);
        title.setFontScale(1.4f);
        title.setColor(SECTION_HEADER_COLOR);
        title.setAlignment(Align.center);
        contentArea.add(title).padBottom(20).row();

        Table scrollContent = new Table();
        scrollContent.top();

        createAbilityTypeSection(scrollContent, skin, "PASSIVE ABILITIES", Abilities.AbilityType.PASSIVE);

        createAbilityTypeSection(scrollContent, skin, "ACTIVE ABILITIES", Abilities.AbilityType.ACTIVE);

        ScrollPane scrollPane = new ScrollPane(scrollContent, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollBarPositions(false, true);

        contentArea.add(scrollPane).expand().fill();
    }

    private void createAbilityTypeSection(Table container, Skin skin, String sectionName, Abilities.AbilityType type) {
        Label sectionHeader = new Label(sectionName, skin);
        sectionHeader.setFontScale(1.2f);
        sectionHeader.setColor(SECTION_HEADER_COLOR);
        container.add(sectionHeader).left().padTop(15).padBottom(10).row();

        boolean hasAbilities = false;

        for (Abilities ability : Abilities.values()) {
            if (ability.getType() == type && hasPlayerAcquiredAbility(ability)) {
                hasAbilities = true;
                Table abilityRow = createAbilityRow(ability, skin);
                container.add(abilityRow).fillX().padBottom(8).row();
            }
        }

        if (!hasAbilities) {
            Label noAbilitiesLabel = new Label("No " + type.toString().toLowerCase() + " abilities acquired yet", skin);
            noAbilitiesLabel.setColor(TEXT_COLOR);
            noAbilitiesLabel.setFontScale(0.9f);
            container.add(noAbilitiesLabel).left().padLeft(20).padBottom(15).row();
        }
    }

    private Table createAbilityRow(Abilities ability, Skin skin) {
        Table row = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        row.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        row.setColor(new Color(0.12f, 0.12f, 0.18f, 0.9f));
        row.pad(12);

        // Ability icon/name
        Label nameLabel = new Label(ability.getName(), skin);
        nameLabel.setFontScale(1.1f);
        nameLabel.setColor(Color.WHITE);
        row.add(nameLabel).width(150).left();

        // Ability description
        Label descLabel = new Label(getAbilityDescription(ability), skin);
        descLabel.setFontScale(0.85f);
        descLabel.setColor(TEXT_COLOR);
        descLabel.setWrap(true);
        row.add(descLabel).width(350).left().padLeft(15);

        // Status/Effect
        if (ability.getType() == Abilities.AbilityType.ACTIVE) {
            Label statusLabel = createAbilityStatusLabel(ability, skin);
            row.add(statusLabel).width(120).right();
        } else {
            Label effectLabel = new Label(getAbilityEffectText(ability), skin);
            effectLabel.setFontScale(0.9f);
            effectLabel.setColor(ABILITY_ACTIVE_COLOR);
            row.add(effectLabel).width(120).right();
        }

        return row;
    }

    private void showCheatSection(Skin skin) {
        contentArea.clear();

        Label title = new Label("CHEAT CODES", skin);
        title.setFontScale(1.4f);
        title.setColor(SECTION_HEADER_COLOR);
        title.setAlignment(Align.center);
        contentArea.add(title).padBottom(20).row();

        // Cheat input section
        Table inputSection = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        inputSection.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        inputSection.setColor(new Color(0.08f, 0.08f, 0.12f, 0.9f));
        inputSection.pad(15);

        Label inputLabel = new Label("Enter Cheat Code:", skin);
        inputLabel.setColor(Color.WHITE);
        inputLabel.setFontScale(1.1f);
        inputSection.add(inputLabel).padRight(15);

        cheatInputField = new TextField("", skin);
        cheatInputField.setMessageText("Type cheat code here...");
        inputSection.add(cheatInputField).width(250).padRight(15);

        TextButton executeBtn = new TextButton("Execute", skin);
        executeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                executeCheatCode();
            }
        });
        inputSection.add(executeBtn).width(100);

        contentArea.add(inputSection).fillX().padBottom(15).row();

        // Status label
        cheatStatusLabel = new Label("", skin);
        cheatStatusLabel.setAlignment(Align.center);
        cheatStatusLabel.setFontScale(1.0f);
        contentArea.add(cheatStatusLabel).padBottom(20).row();

        // Available cheats
        Label availableTitle = new Label("AVAILABLE CHEAT CODES", skin);
        availableTitle.setFontScale(1.2f);
        availableTitle.setColor(SECTION_HEADER_COLOR);
        availableTitle.setAlignment(Align.center);
        contentArea.add(availableTitle).padBottom(15).row();

        Table cheatsContainer = new Table();
        cheatsContainer.top();

        Map<String, CheatCodeManager.CheatCode> cheats = cheatManager.getAllCheatCodes();
        for (CheatCodeManager.CheatCode cheat : cheats.values()) {
            Table cheatRow = createCheatRow(cheat, skin);
            cheatsContainer.add(cheatRow).fillX().padBottom(8).row();
        }

        ScrollPane scrollPane = new ScrollPane(cheatsContainer, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        contentArea.add(scrollPane).expand().fill();
    }

    private Table createCheatRow(CheatCodeManager.CheatCode cheat, Skin skin) {
        Table row = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        row.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        row.setColor(new Color(0.12f, 0.12f, 0.18f, 0.9f));
        row.pad(10);

        Label codeLabel = new Label(cheat.getCode(), skin);
        codeLabel.setFontScale(1.0f);
        codeLabel.setColor(new Color(1f, 0.8f, 0.4f, 1f)); // Orange color for cheat codes
        row.add(codeLabel).width(140).left();

        Label nameLabel = new Label(cheat.getName(), skin);
        nameLabel.setFontScale(0.95f);
        nameLabel.setColor(Color.WHITE);
        row.add(nameLabel).width(120).left().padLeft(10);

        Label descLabel = new Label(cheat.getDescription(), skin);
        descLabel.setFontScale(0.8f);
        descLabel.setColor(TEXT_COLOR);
        descLabel.setWrap(true);
        row.add(descLabel).expandX().left().padLeft(10);

        return row;
    }

    private void showSettingsSection(Skin skin) {
        contentArea.clear();

        Label title = new Label("GAME SETTINGS", skin);
        title.setFontScale(1.4f);
        title.setColor(SECTION_HEADER_COLOR);
        title.setAlignment(Align.center);
        contentArea.add(title).padBottom(20).row();

        // Display Settings
        Table displaySection = createSettingsSection(skin, "DISPLAY SETTINGS");

        blackWhiteCheckBox = new CheckBox("Black & White Mode", skin);
        blackWhiteCheckBox.setChecked(App.isBlackAndWhiteEnabled());
        blackWhiteCheckBox.getLabel().setColor(Color.WHITE);
        blackWhiteCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.setBlackAndWhiteEnabled(blackWhiteCheckBox.isChecked());
                playClick();
            }
        });
        displaySection.add(blackWhiteCheckBox).left().padLeft(20).row();

        contentArea.add(displaySection).fillX().padBottom(20).row();

        // Audio Settings
        Table audioSection = createSettingsSection(skin, "AUDIO SETTINGS");

        soundEffectsCheckBox = new CheckBox("Sound Effects", skin);
        soundEffectsCheckBox.setChecked(App.isSFX());
        soundEffectsCheckBox.getLabel().setColor(Color.WHITE);
        soundEffectsCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                App.setSFX(soundEffectsCheckBox.isChecked());
                playClick();
            }
        });
        audioSection.add(soundEffectsCheckBox).left().padLeft(20).row();

        contentArea.add(audioSection).fillX();
    }

    private Table createSettingsSection(Skin skin, String sectionTitle) {
        Table section = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        section.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        section.setColor(new Color(0.08f, 0.08f, 0.12f, 0.9f));
        section.pad(15);

        Label titleLabel = new Label(sectionTitle, skin);
        titleLabel.setFontScale(1.1f);
        titleLabel.setColor(SECTION_HEADER_COLOR);
        section.add(titleLabel).left().padBottom(10).row();

        return section;
    }

    private void executeCheatCode() {
        String cheatCode = cheatInputField.getText().trim().toUpperCase();

        if (cheatCode.isEmpty()) {
            cheatStatusLabel.setText("Please enter a cheat code");
            cheatStatusLabel.setColor(CHEAT_ERROR_COLOR);
            return;
        }

        boolean success = cheatManager.executeCheatCode(cheatCode);

        if (success) {
            cheatStatusLabel.setText("Cheat code executed successfully: " + cheatCode);
            cheatStatusLabel.setColor(CHEAT_SUCCESS_COLOR);
            cheatInputField.setText("");
        } else {
            cheatStatusLabel.setText("Invalid cheat code: " + cheatCode);
            cheatStatusLabel.setColor(CHEAT_ERROR_COLOR);
        }
    }

    private void createActionButtons(Table container, Skin skin) {
        Table buttonContainer = new Table();
        Texture panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        buttonContainer.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        buttonContainer.setColor(new Color(0.08f, 0.08f, 0.12f, 0.9f));
        buttonContainer.pad(15);
        buttonContainer.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).space(20);

        // Resume Button
        TextButton resumeButton = new TextButton("Resume Game", skin);
        resumeButton.getLabel().setColor(ABILITY_ACTIVE_COLOR);
        resumeButton.getLabel().setFontScale(1.1f);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                handleResume();
            }
        });

        // Save & Exit Button
        TextButton saveExitButton = new TextButton("Save & Exit", skin);
        saveExitButton.getLabel().setColor(ABILITY_READY_COLOR);
        saveExitButton.getLabel().setFontScale(1.1f);
        saveExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                handleSaveAndExit();
            }
        });

        // Give Up Button
        TextButton giveUpButton = new TextButton("Give Up", skin);
        giveUpButton.getLabel().setColor(CHEAT_ERROR_COLOR);
        giveUpButton.getLabel().setFontScale(1.1f);
        giveUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                handleGiveUp();
            }
        });

        buttonContainer.add(resumeButton);
        buttonContainer.add(saveExitButton);
        buttonContainer.add(giveUpButton);

        container.add(buttonContainer).fillX();
    }

    // Helper methods
    private void addStatRow(Table table, Skin skin, String label, String value) {
        Label nameLabel = new Label(label, skin);
        nameLabel.setColor(TEXT_COLOR);
        nameLabel.setFontScale(0.9f);

        Label valueLabel = new Label(value, skin);
        valueLabel.setColor(Color.WHITE);
        valueLabel.setFontScale(0.9f);

        table.add(nameLabel).left().width(120).padBottom(5);
        table.add(valueLabel).left().expandX().padBottom(5).row();
    }

    private boolean hasPlayerAcquiredAbility(Abilities ability) {
        switch (ability) {
            case VITALITY:
                return player.getMaxHealth() > player.getCharacter().getHp();
            case PROCREASE:
                return player.getProjectileBonus() > 0;
            case AMOCREASE:
                return player.getAmmoBonus() > 0;
            case REGENERATION:
                return player.hasRegeneration();
            case DAMAGER:
            case SPEEDY:
            case SHIELD:
            case MULTISHOT:
                return ability.getCooldownProgress() > 0 || ability.isActive();
            default:
                return false;
        }
    }

    private Label createAbilityStatusLabel(Abilities ability, Skin skin) {
        Label statusLabel;
        if (ability.isActive()) {
            statusLabel = new Label("ACTIVE (" + String.format("%.1fs", ability.getRemainingDuration()) + ")", skin);
            statusLabel.setColor(ABILITY_ACTIVE_COLOR);
        } else if (ability.getRemainingCooldown() > 0) {
            statusLabel = new Label("COOLDOWN (" + String.format("%.1fs", ability.getRemainingCooldown()) + ")", skin);
            statusLabel.setColor(ABILITY_COOLDOWN_COLOR);
        } else {
            statusLabel = new Label("READY", skin);
            statusLabel.setColor(ABILITY_READY_COLOR);
        }
        statusLabel.setFontScale(0.8f);
        return statusLabel;
    }

    private String getAbilityDescription(Abilities ability) {
        switch (ability) {
            case VITALITY:
                return "Permanently increases maximum health";
            case PROCREASE:
                return "Adds extra projectiles to weapon attacks";
            case AMOCREASE:
                return "Increases maximum ammunition capacity";
            case REGENERATION:
                return "Slowly restores health over time";
            case DAMAGER:
                return "Temporarily increases damage output";
            case SPEEDY:
                return "Temporarily increases movement speed";
            case SHIELD:
                return "Provides temporary damage immunity";
            case MULTISHOT:
                return "Fires multiple projectiles simultaneously";
            default:
                return "Special ability effect";
        }
    }

    private String getAbilityEffectText(Abilities ability) {
        switch (ability) {
            case VITALITY:
                return "+" + (player.getMaxHealth() - player.getCharacter().getHp()) + " Max HP";
            case PROCREASE:
                return "+" + player.getProjectileBonus() + " Projectiles";
            case AMOCREASE:
                return "+" + player.getAmmoBonus() + " Max Ammo";
            case REGENERATION:
                return "Health Regen";
            default:
                return "Acquired";
        }
    }

    private void handleResume() {
        if (onResume != null) {
            onResume.run();
        }
        remove();
    }

    private void handleSaveAndExit() {
        if (onSaveAndExit != null) {
            onSaveAndExit.run();
        }
        remove();
    }

    private void handleGiveUp() {
        if (onGiveUp != null) {
            onGiveUp.run();
        }
        remove();
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Update abilities in real-time
        for (Abilities ability : Abilities.values()) {
            ability.update(delta);
        }
    }

    private enum MenuSection {
        MAIN, ABILITIES, CHEATS, SETTINGS
    }
}
