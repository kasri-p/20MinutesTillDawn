package com.untilDawn.views.window;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Player;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.utils.GameAssetManager;

public class PauseMenuWindow extends Window {
    // Constants
    private static final float WINDOW_WIDTH = 1200f;
    private static final float WINDOW_HEIGHT = 800f;
    private static final float PADDING = 25f;
    private static final float SECTION_SPACING = 30f;
    private static final float BUTTON_WIDTH = 180f;
    private static final float BUTTON_HEIGHT = 60f;

    // Color scheme
    private static final Color TITLE_COLOR = Color.YELLOW;
    private static final Color SECTION_HEADER_COLOR = Color.CYAN;
    private static final Color CHEAT_COLOR = Color.ORANGE;
    private static final Color ABILITY_ACTIVE_COLOR = Color.GREEN;
    private static final Color ABILITY_COOLDOWN_COLOR = Color.YELLOW;
    private static final Color ABILITY_READY_COLOR = Color.CYAN;
    private static final Color STATS_COLOR = Color.LIGHT_GRAY;

    // Cheat codes organized by category
    private static final String[][] CHEAT_CATEGORIES = {
        {"COMBAT CHEATS",
            "🗡️ GODMODE - Infinite Health",
            "💥 DAMAGE2X - Double Damage",
            "🛡️ INVINCIBLE - Temporary Invincibility",
            "⚡ LEVELUP - Instant Level Up"
        },
        {"EQUIPMENT CHEATS",
            "🔫 AMMOMAX - Infinite Ammo",
            "⚡ NORELOAD - No Reload Time",
            "🎯 ALLABILITIES - Unlock All Abilities"
        },
        {"MOVEMENT CHEATS",
            "🏃 SPEEDUP - Double Movement Speed",
            "👻 NOCLIP - Walk Through Walls",
            "🦘 JUMPBOOST - Super Jump"
        }
    };

    // Core components
    private final Player player;
    private final Runnable onResume;
    private final Runnable onGiveUp;
    private final Runnable onSaveAndExit;

    // UI Components
    private ScrollPane abilitiesScrollPane;
    private ScrollPane cheatScrollPane;
    private ScrollPane statsScrollPane;
    private Table abilitiesTable;
    private Table statsTable;
    private CheckBox blackWhiteCheckBox;
    private CheckBox soundEffectsCheckBox;
    private CheckBox musicCheckBox;
    private Slider volumeSlider;
    private Label volumeLabel;
    private boolean originalBlackWhiteState;
    private boolean originalSfxState;
    private boolean originalMusicState;
    private Tab currentTab = Tab.ABILITIES;

    public PauseMenuWindow(Skin skin, Player player, Stage stage, Runnable onResume, Runnable onGiveUp, Runnable onSaveAndExit) {
        super("", skin); // Empty title, we'll create custom
        this.player = player;
        this.onResume = onResume;
        this.onGiveUp = onGiveUp;
        this.onSaveAndExit = onSaveAndExit;

        // Store original states
        this.originalBlackWhiteState = App.isBlackAndWhiteEnabled();
        this.originalSfxState = App.isSFX();

        setupWindow(stage);
        createContent(skin);
    }

    private void setupWindow(Stage stage) {
        // Enhanced background with border effect
        setBackground(new TextureRegionDrawable(
            new TextureRegion(GameAssetManager.getGameAssetManager().getPanel())
        ));

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        float centerX = (stage.getWidth() - getWidth()) / 2;
        float centerY = (stage.getHeight() - getHeight()) / 2;
        setPosition(centerX, centerY);

        setModal(true);
        setMovable(false);

        // Remove default title
        getTitleLabel().setText("");
    }

    private void createContent(Skin skin) {
        clear();

        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.pad(PADDING);

        // Create main title
        createMainTitle(mainContainer, skin);

        // Create tab navigation
        createTabNavigation(mainContainer, skin);

        // Create content area
        createContentArea(mainContainer, skin);

        // Create bottom action buttons
        createActionButtons(mainContainer, skin);

        add(mainContainer).expand().fill();
    }

    private void createMainTitle(Table container, Skin skin) {
        Table titleTable = new Table();

        Label mainTitle = new Label("⏸️ GAME PAUSED", skin, "title");
        mainTitle.setAlignment(Align.center);
        mainTitle.setFontScale(2.2f);
        mainTitle.setColor(TITLE_COLOR);

        Label subtitle = new Label("Game Progress Saved Automatically", skin);
        subtitle.setAlignment(Align.center);
        subtitle.setFontScale(0.9f);
        subtitle.setColor(STATS_COLOR);

        titleTable.add(mainTitle).row();
        titleTable.add(subtitle).padTop(5);

        container.add(titleTable).padBottom(SECTION_SPACING).row();
    }

    private void createTabNavigation(Table container, Skin skin) {
        Table tabTable = new Table();
        tabTable.defaults().width(180).height(50).space(10);

        for (Tab tab : Tab.values()) {
            TextButton tabButton = new TextButton(tab.getDisplayName(), skin);

            // Highlight current tab
            if (tab == currentTab) {
                tabButton.setColor(SECTION_HEADER_COLOR);
                tabButton.getLabel().setColor(Color.BLACK);
            } else {
                tabButton.setColor(Color.GRAY);
                tabButton.getLabel().setColor(Color.WHITE);
            }

            tabButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    playClick();
                    switchTab(tab, skin);
                }
            });

            tabTable.add(tabButton);
        }

        container.add(tabTable).padBottom(20).row();
    }

    private void createContentArea(Table container, Skin skin) {
        Table contentArea = new Table();
        contentArea.setBackground(new TextureRegionDrawable(
            new TextureRegion(GameAssetManager.getGameAssetManager().getPanel())
        ));
        contentArea.pad(20);

        refreshContentArea(contentArea, skin);

        container.add(contentArea).size(WINDOW_WIDTH - 100, 450).row();
    }

    private void refreshContentArea(Table contentArea, Skin skin) {
        contentArea.clear();

        switch (currentTab) {
            case ABILITIES:
                createAbilitiesContent(contentArea, skin);
                break;
            case STATS:
                createStatsContent(contentArea, skin);
                break;
            case SETTINGS:
                createSettingsContent(contentArea, skin);
                break;
            case CHEATS:
                createCheatsContent(contentArea, skin);
                break;
        }
    }

    private void createAbilitiesContent(Table container, Skin skin) {
        // Section header
        Label sectionTitle = new Label("🎯 ACQUIRED ABILITIES", skin);
        sectionTitle.setFontScale(1.4f);
        sectionTitle.setColor(SECTION_HEADER_COLOR);
        sectionTitle.setAlignment(Align.center);
        container.add(sectionTitle).padBottom(20).row();

        // Abilities organized by type
        Table abilitiesContainer = new Table();
        abilitiesContainer.top();

        // Create sections for different ability types
        createAbilityTypeSection(abilitiesContainer, skin, "⚔️ PASSIVE ABILITIES", Abilities.AbilityType.PASSIVE);
        createAbilityTypeSection(abilitiesContainer, skin, "⚡ ACTIVE ABILITIES", Abilities.AbilityType.ACTIVE);

        abilitiesScrollPane = new ScrollPane(abilitiesContainer, skin);
        abilitiesScrollPane.setFadeScrollBars(false);
        abilitiesScrollPane.setScrollingDisabled(true, false);
        abilitiesScrollPane.setScrollBarPositions(false, true);

        container.add(abilitiesScrollPane).expand().fill();
    }

    private void createAbilityTypeSection(Table container, Skin skin, String sectionName, Abilities.AbilityType type) {
        Label typeHeader = new Label(sectionName, skin);
        typeHeader.setFontScale(1.1f);
        typeHeader.setColor(Color.WHITE);
        container.add(typeHeader).left().padTop(15).padBottom(10).row();

        boolean hasAbilitiesOfType = false;

        for (Abilities ability : Abilities.values()) {
            if (ability.getType() == type && hasPlayerAcquiredAbility(ability)) {
                hasAbilitiesOfType = true;
                Table abilityRow = createEnhancedAbilityRow(ability, skin);
                container.add(abilityRow).fillX().padBottom(8).row();
            }
        }

        if (!hasAbilitiesOfType) {
            Label noAbilitiesLabel = new Label("No " + type.toString().toLowerCase() + " abilities acquired", skin);
            noAbilitiesLabel.setColor(Color.GRAY);
            noAbilitiesLabel.setFontScale(0.9f);
            container.add(noAbilitiesLabel).left().padLeft(20).padBottom(10).row();
        }
    }

    private Table createEnhancedAbilityRow(Abilities ability, Skin skin) {
        Table row = new Table();
        row.setBackground(new TextureRegionDrawable(
            new TextureRegion(GameAssetManager.getGameAssetManager().getPanel())
        ));
        row.pad(12);

        // Ability icon
        Label iconLabel = new Label(ability.getIcon(), skin);
        iconLabel.setFontScale(1.5f);
        row.add(iconLabel).width(50).left();

        // Main info column
        Table infoTable = new Table();

        // Ability name
        Label nameLabel = new Label(ability.getName(), skin);
        nameLabel.setFontScale(1.1f);
        nameLabel.setColor(Color.WHITE);

        // Ability description (if available)
        Label descLabel = new Label(getAbilityDescription(ability), skin);
        descLabel.setFontScale(0.8f);
        descLabel.setColor(STATS_COLOR);
        descLabel.setWrap(true);

        infoTable.add(nameLabel).left().row();
        infoTable.add(descLabel).width(400).left().padTop(3);

        row.add(infoTable).expandX().left().padLeft(15);

        // Status/Progress column
        Table statusTable = new Table();

        if (ability.getType() == Abilities.AbilityType.ACTIVE) {
            Label statusLabel = createAbilityStatusLabel(ability, skin);
            statusTable.add(statusLabel).row();


        } else {
            // Show passive effect level/bonus
            Label effectLabel = new Label(getAbilityEffectText(ability), skin);
            effectLabel.setFontScale(0.9f);
            effectLabel.setColor(ABILITY_ACTIVE_COLOR);
            statusTable.add(effectLabel);
        }

        row.add(statusTable).right().width(120);

        return row;
    }

    private void createStatsContent(Table container, Skin skin) {
        Label sectionTitle = new Label("📊 PLAYER STATISTICS", skin);
        sectionTitle.setFontScale(1.4f);
        sectionTitle.setColor(SECTION_HEADER_COLOR);
        sectionTitle.setAlignment(Align.center);
        container.add(sectionTitle).padBottom(20).row();

        statsTable = new Table();
        statsTable.top();

        // Create organized stats sections
        createStatsSection(statsTable, skin, "⚔️ COMBAT STATS", new String[][]{
            {"Health", player.getPlayerHealth() + " / " + player.getMaxHealth()},
            {"Critical Hit Chance", "15%"}, // Example stat
        });

        createStatsSection(statsTable, skin, "🎒 EQUIPMENT STATS", new String[][]{
            {"Current Weapon", "Assault Rifle"}, // Example
        });

        createStatsSection(statsTable, skin, "🏆 GAME PROGRESS", new String[][]{
            {"Current Level", String.valueOf(player.getLevel())},
            {"Enemies Defeated", "147"}, // Example stat
            {"Time Survived", "12:34"}, // Example stat
            {"Score", "2,450"}, // Example stat
        });

        statsScrollPane = new ScrollPane(statsTable, skin);
        statsScrollPane.setFadeScrollBars(false);
        statsScrollPane.setScrollingDisabled(true, false);

        container.add(statsScrollPane).expand().fill();
    }

    private void createStatsSection(Table container, Skin skin, String sectionName, String[][] stats) {
        // Section header
        Label sectionHeader = new Label(sectionName, skin);
        sectionHeader.setFontScale(1.1f);
        sectionHeader.setColor(Color.WHITE);
        container.add(sectionHeader).left().padTop(15).padBottom(10).colspan(2).row();

        // Stats rows
        for (String[] stat : stats) {
            Label statName = new Label(stat[0] + ":", skin);
            statName.setColor(STATS_COLOR);
            statName.setFontScale(0.9f);

            Label statValue = new Label(stat[1], skin);
            statValue.setColor(Color.WHITE);
            statValue.setFontScale(0.9f);

            container.add(statName).left().width(200).padLeft(20);
            container.add(statValue).left().expandX().row();
        }
    }

    private void createSettingsContent(Table container, Skin skin) {
        Label sectionTitle = new Label("⚙️ GAME SETTINGS", skin);
        sectionTitle.setFontScale(1.4f);
        sectionTitle.setColor(SECTION_HEADER_COLOR);
        sectionTitle.setAlignment(Align.center);
        container.add(sectionTitle).padBottom(20).row();

        Table settingsTable = new Table();
        settingsTable.defaults().space(15);

        // Display Settings Section
        createSettingsSection(settingsTable, skin, "🖥️ DISPLAY SETTINGS");

        blackWhiteCheckBox = new CheckBox("Black & White Mode", skin);
        blackWhiteCheckBox.setChecked(App.isBlackAndWhiteEnabled());
        blackWhiteCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                App.setBlackAndWhiteEnabled(blackWhiteCheckBox.isChecked());
            }
        });
        settingsTable.add(blackWhiteCheckBox).left().padLeft(20).row();

        // Audio Settings Section
        createSettingsSection(settingsTable, skin, "🔊 AUDIO SETTINGS");

        soundEffectsCheckBox = new CheckBox("Sound Effects", skin);
        soundEffectsCheckBox.setChecked(App.isSFX());
        soundEffectsCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                App.setSFX(soundEffectsCheckBox.isChecked());
            }
        });
        settingsTable.add(soundEffectsCheckBox).left().padLeft(20).row();

        musicCheckBox = new CheckBox("Background Music", skin);
        musicCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
            }
        });
        settingsTable.add(musicCheckBox).left().padLeft(20).row();

        // Volume slider
        Table volumeTable = new Table();
        volumeLabel = new Label("Master Volume: 75%", skin);
        volumeLabel.setColor(STATS_COLOR);
        volumeSlider = new Slider(0, 100, 1, false, skin);
        volumeSlider.setValue(75);
        volumeSlider.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                volumeLabel.setText("Master Volume: " + (int) volumeSlider.getValue() + "%");
            }
        });

        volumeTable.add(volumeLabel).width(150).left();
        volumeTable.add(volumeSlider).width(200).padLeft(10);
        settingsTable.add(volumeTable).left().padLeft(20).row();

        container.add(settingsTable).expand().fill().top();
    }

    private void createSettingsSection(Table container, Skin skin, String sectionName) {
        Label sectionHeader = new Label(sectionName, skin);
        sectionHeader.setFontScale(1.1f);
        sectionHeader.setColor(Color.WHITE);
        container.add(sectionHeader).left().padTop(20).padBottom(10).row();
    }

    private void createCheatsContent(Table container, Skin skin) {
        Label sectionTitle = new Label("🎮 CHEAT CODES", skin);
        sectionTitle.setFontScale(1.4f);
        sectionTitle.setColor(SECTION_HEADER_COLOR);
        sectionTitle.setAlignment(Align.center);
        container.add(sectionTitle).padBottom(20).row();

        Table cheatTable = new Table();
        cheatTable.top();

        for (String[] category : CHEAT_CATEGORIES) {
            // Category header
            Label categoryHeader = new Label(category[0], skin);
            categoryHeader.setFontScale(1.1f);
            categoryHeader.setColor(Color.WHITE);
            cheatTable.add(categoryHeader).left().padTop(15).padBottom(10).row();

            // Cheat codes in category
            for (int i = 1; i < category.length; i++) {
                Table cheatRow = new Table();
                cheatRow.setBackground(new TextureRegionDrawable(
                    new TextureRegion(GameAssetManager.getGameAssetManager().getPanel())
                ));
                cheatRow.pad(8);

                Label cheatLabel = new Label(category[i], skin);
                cheatLabel.setFontScale(0.9f);
                cheatLabel.setColor(CHEAT_COLOR);
                cheatLabel.setWrap(true);

                cheatRow.add(cheatLabel).width(450).left();
                cheatTable.add(cheatRow).fillX().padLeft(20).padBottom(5).row();
            }
        }

        // Instructions
        Label instructionLabel = new Label("💡 Note: These cheat codes are for reference only.\nActivation system not implemented in this demo.", skin);
        instructionLabel.setFontScale(0.8f);
        instructionLabel.setColor(Color.GRAY);
        instructionLabel.setAlignment(Align.center);
        instructionLabel.setWrap(true);
        cheatTable.add(instructionLabel).width(500).padTop(20).row();

        cheatScrollPane = new ScrollPane(cheatTable, skin);
        cheatScrollPane.setFadeScrollBars(false);
        cheatScrollPane.setScrollingDisabled(true, false);

        container.add(cheatScrollPane).expand().fill();
    }

    private void createActionButtons(Table container, Skin skin) {
        Table buttonContainer = new Table();
        buttonContainer.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).space(20);

        // Resume Button
        TextButton resumeButton = new TextButton("▶️ Resume Game", skin);
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
        TextButton saveExitButton = new TextButton("💾 Save & Exit", skin);
        saveExitButton.getLabel().setColor(Color.CYAN);
        saveExitButton.getLabel().setFontScale(1.1f);
        saveExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                handleSaveAndExit();
            }
        });

        // Settings Reset Button
        TextButton resetButton = new TextButton("🔄 Reset Settings", skin);
        resetButton.getLabel().setColor(ABILITY_COOLDOWN_COLOR);
        resetButton.getLabel().setFontScale(1.1f);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                resetSettings();
            }
        });

        // Give Up Button
        TextButton giveUpButton = new TextButton("❌ Give Up", skin);
        giveUpButton.getLabel().setColor(Color.RED);
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
        buttonContainer.add(resetButton);
        buttonContainer.add(giveUpButton);

        container.add(buttonContainer).padTop(SECTION_SPACING);
    }

    // Helper methods
    private void switchTab(Tab newTab, Skin skin) {
        if (currentTab != newTab) {
            currentTab = newTab;
            // Recreate the entire content to refresh tab buttons and content
            createContent(skin);
        }
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
            statusLabel = new Label("ACTIVE", skin);
            statusLabel.setColor(ABILITY_ACTIVE_COLOR);
        } else if (ability.getRemainingCooldown() > 0) {
            statusLabel = new Label(String.format("%.1fs", ability.getRemainingCooldown()), skin);
            statusLabel.setColor(ABILITY_COOLDOWN_COLOR);
        } else {
            statusLabel = new Label("READY", skin);
            statusLabel.setColor(ABILITY_READY_COLOR);
        }
        statusLabel.setFontScale(0.9f);
        return statusLabel;
    }

    private String getAbilityDescription(Abilities ability) {
        switch (ability) {
            case VITALITY:
                return "Increases maximum health permanently";
            case PROCREASE:
                return "Adds extra projectiles to attacks";
            case AMOCREASE:
                return "Increases maximum ammunition capacity";
            case REGENERATION:
                return "Slowly restores health over time";
            case DAMAGER:
                return "Temporarily doubles damage output";
            case SPEEDY:
                return "Increases movement speed temporarily";
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
                return "+" + (player.getMaxHealth() - player.getCharacter().getHp()) + " HP";
            case PROCREASE:
                return "+" + player.getProjectileBonus() + " shots";
            case AMOCREASE:
                return "+" + player.getAmmoBonus() + " ammo";
            case REGENERATION:
                return "+2 HP/sec";
            default:
                return "Active";
        }
    }

    private void handleResume() {
        confirmSettingsChanges();
        if (onResume != null) {
            onResume.run();
        }
        remove();
    }

    private void handleSaveAndExit() {
        confirmSettingsChanges();
        if (onSaveAndExit != null) {
            onSaveAndExit.run();
        }
        remove();
    }

    private void handleGiveUp() {
        restoreOriginalSettings();
        if (onGiveUp != null) {
            onGiveUp.run();
        }
        remove();
    }

    private void resetSettings() {
        App.setBlackAndWhiteEnabled(false);
        App.setSFX(true);

        if (blackWhiteCheckBox != null) blackWhiteCheckBox.setChecked(false);
        if (soundEffectsCheckBox != null) soundEffectsCheckBox.setChecked(true);
        if (musicCheckBox != null) musicCheckBox.setChecked(true);
        if (volumeSlider != null) {
            volumeSlider.setValue(75);
            volumeLabel.setText("Master Volume: 75%");
        }
    }

    private void confirmSettingsChanges() {
        // Settings are applied in real-time, so no need to do anything special
    }

    private void restoreOriginalSettings() {
        App.setBlackAndWhiteEnabled(originalBlackWhiteState);
        App.setSFX(originalSfxState);
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Update ability statuses in real-time
        for (Abilities ability : Abilities.values()) {
            ability.update(delta);
        }
    }

    // Tabs for better organization
    private enum Tab {
        ABILITIES("🎯 Abilities"),
        STATS("📊 Statistics"),
        SETTINGS("⚙️ Settings"),
        CHEATS("🎮 Cheats");

        private final String displayName;

        Tab(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
