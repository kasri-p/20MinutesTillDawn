package com.untilDawn.views.window;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Player;
import com.untilDawn.models.enums.Abilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelUpWindow extends Window {
    private final Player player;
    private final Stage gameStage;
    private final Runnable onAbilitySelected;

    private Table upgradeIconsTable;
    private Table descriptionTable;
    private Table buttonsTable;
    private Label titleLabel;
    private Label upgradeNameLabel;
    private Label upgradeDescriptionLabel;
    private Label unlocksLabel;
    private Table unlocksIconsTable;

    // Selected abilities for this level up
    private List<Abilities> selectedAbilities;
    private int currentSelectedIndex = 0;

    // Textures
    private Texture windowBackgroundTexture;
    private Texture upgradeCardTexture;
    private Texture selectedUpgradeTexture;
    private Texture buttonTexture;
    private Texture upgradeIconTexture;

    // Animation
    private float animationTime = 0f;

    public LevelUpWindow(Skin skin, Player player, Stage gameStage, Runnable onAbilitySelected) {
        super("", skin);
        this.player = player;
        this.gameStage = gameStage;
        this.onAbilitySelected = onAbilitySelected;

        // Load textures
        loadTextures();

        // Select 5 random abilities (like the reference image)
        selectRandomAbilities();

        // Setup window appearance
        setupWindow();

        // Create content
        createContent();

        // Setup animations
        setupAnimations();
    }

    private void loadTextures() {
        try {
            // Create main window background - dark with cyan border
            windowBackgroundTexture = createMainWindowTexture(1000, 700);

            // Create upgrade card background
            upgradeCardTexture = createUpgradeCardTexture(100, 100);

            // Create selected upgrade background (highlighted)
            selectedUpgradeTexture = createSelectedUpgradeTexture(100, 100);

            // Create button texture
            buttonTexture = createButtonTexture(300, 60);

            // Create upgrade icon texture
            upgradeIconTexture = createUpgradeIconTexture(60, 60);

        } catch (Exception e) {
            Gdx.app.error("LevelUpWindow", "Error loading textures: " + e.getMessage());
        }
    }

    private Texture createMainWindowTexture(int width, int height) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Very dark background
        pixmap.setColor(new Color(0.05f, 0.08f, 0.12f, 0.98f));
        pixmap.fill();

        // Cyan border (multiple layers for glow effect)
        for (int i = 0; i < 4; i++) {
            float alpha = 0.8f - (i * 0.15f);
            pixmap.setColor(new Color(0.2f, 0.6f, 0.8f, alpha));
            pixmap.drawRectangle(i, i, width - 2 * i, height - 2 * i);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createUpgradeCardTexture(int width, int height) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Dark background
        pixmap.setColor(new Color(0.1f, 0.15f, 0.2f, 0.9f));
        pixmap.fill();

        // Hexagonal border effect
        pixmap.setColor(new Color(0.3f, 0.4f, 0.5f, 0.8f));
        pixmap.drawRectangle(0, 0, width, height);
        pixmap.drawRectangle(1, 1, width - 2, height - 2);

        // Inner glow
        pixmap.setColor(new Color(0.2f, 0.3f, 0.4f, 0.4f));
        for (int i = 2; i < 8; i++) {
            pixmap.drawRectangle(i, i, width - 2 * i, height - 2 * i);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createSelectedUpgradeTexture(int width, int height) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Slightly brighter background
        pixmap.setColor(new Color(0.15f, 0.2f, 0.25f, 0.95f));
        pixmap.fill();

        // Bright cyan border for selection
        pixmap.setColor(new Color(0.4f, 0.8f, 1.0f, 1.0f));
        for (int i = 0; i < 3; i++) {
            pixmap.drawRectangle(i, i, width - 2 * i, height - 2 * i);
        }

        // Glowing inner border
        pixmap.setColor(new Color(0.6f, 0.9f, 1.0f, 0.6f));
        for (int i = 3; i < 8; i++) {
            pixmap.drawRectangle(i, i, width - 2 * i, height - 2 * i);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createButtonTexture(int width, int height) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Dark button background
        pixmap.setColor(new Color(0.08f, 0.12f, 0.16f, 0.9f));
        pixmap.fill();

        // Cyan border
        pixmap.setColor(new Color(0.3f, 0.6f, 0.8f, 0.9f));
        for (int i = 0; i < 2; i++) {
            pixmap.drawRectangle(i, i, width - 2 * i, height - 2 * i);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createUpgradeIconTexture(int width, int height) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Transparent background
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        // Simple icon shape (can be customized per ability)
        pixmap.setColor(new Color(0.8f, 0.9f, 1.0f, 0.9f));
        pixmap.fillCircle(width / 2, height / 2, width / 3);

        // Inner detail
        pixmap.setColor(new Color(0.4f, 0.6f, 0.8f, 1.0f));
        pixmap.fillCircle(width / 2, height / 2, width / 5);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void selectRandomAbilities() {
        selectedAbilities = new ArrayList<>();

        // Get all available abilities
        List<Abilities> allAbilities = new ArrayList<>();
        allAbilities.add(Abilities.VITALITY);
        allAbilities.add(Abilities.DAMAGER);
        allAbilities.add(Abilities.PROCREASE);
        allAbilities.add(Abilities.AMOCREASE);
        allAbilities.add(Abilities.SPEEDY);

        // Shuffle and select 5 (like the reference image)
        Collections.shuffle(allAbilities);
        for (int i = 0; i < Math.min(5, allAbilities.size()); i++) {
            selectedAbilities.add(allAbilities.get(i));
        }

        // Ensure we have at least one ability
        if (selectedAbilities.isEmpty()) {
            selectedAbilities.add(Abilities.VITALITY);
        }
    }

    private void setupWindow() {
        // Remove default title bar
        getTitleLabel().setVisible(false);
        getTitleTable().setVisible(false);

        // Set window properties to fill most of the screen
        setSize(1000, 700);
        setPosition(
            (gameStage.getWidth() - getWidth()) / 2,
            (gameStage.getHeight() - getHeight()) / 2
        );

        // Set background
        if (windowBackgroundTexture != null) {
            setBackground(new TextureRegionDrawable(new TextureRegion(windowBackgroundTexture)));
        }

        setModal(true);
        setMovable(false);
    }

    private void createContent() {
        clear();

        // Main container
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(60);

        // Title section
        createTitleSection(mainTable);

        // Upgrade icons section (horizontal row of 5 icons)
        createUpgradeIconsSection(mainTable);

        // Description section (large center area)
        createDescriptionSection(mainTable);

        // Buttons section
        createButtonsSection(mainTable);

        add(mainTable).expand().fill();

        // Initially select the first upgrade
        selectUpgrade(0);
    }

    private void createTitleSection(Table mainTable) {
        titleLabel = new Label("Choose an Upgrade", getSkin());
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(new Color(0.9f, 0.4f, 0.4f, 1.0f)); // Reddish color like the reference

        mainTable.add(titleLabel).padBottom(40).row();
    }

    private void createUpgradeIconsSection(Table mainTable) {
        upgradeIconsTable = new Table();
        upgradeIconsTable.defaults().space(15);

        for (int i = 0; i < selectedAbilities.size(); i++) {
            final int index = i;
            Abilities ability = selectedAbilities.get(i);

            Table iconContainer = createUpgradeIcon(ability, i);

            // Add click listener
            iconContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectUpgrade(index);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (index != currentSelectedIndex) {
                        iconContainer.clearActions();
                        iconContainer.addAction(Actions.scaleTo(1.1f, 1.1f, 0.1f));
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (index != currentSelectedIndex) {
                        iconContainer.clearActions();
                        iconContainer.addAction(Actions.scaleTo(1.0f, 1.0f, 0.1f));
                    }
                }
            });

            upgradeIconsTable.add(iconContainer).size(100, 100);
        }

        mainTable.add(upgradeIconsTable).padBottom(40).row();
    }

    private Table createUpgradeIcon(Abilities ability, int index) {
        Table container = new Table();
        container.setTransform(true);
        container.setOrigin(Align.center);

        // Background
        if (upgradeCardTexture != null) {
            container.setBackground(new TextureRegionDrawable(new TextureRegion(upgradeCardTexture)));
        }

        // Icon
        Image icon = new Image(createAbilitySpecificIcon(ability));
        container.add(icon).expand().fill().pad(15);

        return container;
    }

    private void createDescriptionSection(Table mainTable) {
        descriptionTable = new Table();
        if (selectedUpgradeTexture != null) {
            descriptionTable.setBackground(new TextureRegionDrawable(new TextureRegion(selectedUpgradeTexture)));
        }
        descriptionTable.pad(40);

        // Upgrade name
        upgradeNameLabel = new Label("", getSkin());
        upgradeNameLabel.setAlignment(Align.left);
        upgradeNameLabel.setFontScale(1.8f);
        upgradeNameLabel.setColor(Color.WHITE);

        // Upgrade description
        upgradeDescriptionLabel = new Label("", getSkin());
        upgradeDescriptionLabel.setAlignment(Align.left);
        upgradeDescriptionLabel.setWrap(true);
        upgradeDescriptionLabel.setFontScale(1.2f);
        upgradeDescriptionLabel.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));

        // Unlocks section
        unlocksLabel = new Label("Unlocks", getSkin());
        unlocksLabel.setAlignment(Align.right);
        unlocksLabel.setFontScale(1.0f);
        unlocksLabel.setColor(new Color(0.6f, 0.6f, 0.6f, 1.0f));

        unlocksIconsTable = new Table();

        // Layout
        Table leftSide = new Table();
        leftSide.add(upgradeNameLabel).left().padBottom(20).row();
        leftSide.add(upgradeDescriptionLabel).left().width(400).expand().fill();

        Table rightSide = new Table();
        rightSide.add(unlocksLabel).right().padBottom(10).row();
        rightSide.add(unlocksIconsTable).right();

        descriptionTable.add(leftSide).expand().fill().left();
        descriptionTable.add(rightSide).right().top().width(200);

        mainTable.add(descriptionTable).size(800, 300).padBottom(40).row();
    }

    private void createButtonsSection(Table mainTable) {
        buttonsTable = new Table();
        buttonsTable.defaults().space(30);

        // Choose button
        TextButton chooseButton = new TextButton("Choose", getSkin());
        if (buttonTexture != null) {
            chooseButton.setBackground(new TextureRegionDrawable(new TextureRegion(buttonTexture)));
        }
        chooseButton.getLabel().setFontScale(1.4f);
        chooseButton.getLabel().setColor(new Color(0.9f, 0.4f, 0.4f, 1.0f));

        // Reroll button
        TextButton rerollButton = new TextButton("Reroll Upgrades", getSkin());
        if (buttonTexture != null) {
            rerollButton.setBackground(new TextureRegionDrawable(new TextureRegion(buttonTexture)));
        }
        rerollButton.getLabel().setFontScale(1.4f);
        rerollButton.getLabel().setColor(new Color(0.9f, 0.4f, 0.4f, 1.0f));

        // Add listeners
        chooseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectCurrentAbility();
            }
        });

        rerollButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rerollUpgrades();
            }
        });

        buttonsTable.add(chooseButton).size(280, 60);
        buttonsTable.add(rerollButton).size(280, 60);

        mainTable.add(buttonsTable);
    }

    private void selectUpgrade(int index) {
        if (index < 0 || index >= selectedAbilities.size()) return;

        currentSelectedIndex = index;
        Abilities selectedAbility = selectedAbilities.get(index);

        // Update visual selection
        updateUpgradeSelection();

        // Update description
        updateDescription(selectedAbility);
    }

    private void updateUpgradeSelection() {
        // Update all upgrade icons to show/hide selection
        for (int i = 0; i < upgradeIconsTable.getChildren().size; i++) {
            Actor child = upgradeIconsTable.getChildren().get(i);
            if (child instanceof Table) {
                Table iconContainer = (Table) child;
                if (i == currentSelectedIndex) {
                    // Selected state
                    if (selectedUpgradeTexture != null) {
                        iconContainer.setBackground(new TextureRegionDrawable(new TextureRegion(selectedUpgradeTexture)));
                    }
                    iconContainer.setScale(1.2f);
                } else {
                    // Normal state
                    if (upgradeCardTexture != null) {
                        iconContainer.setBackground(new TextureRegionDrawable(new TextureRegion(upgradeCardTexture)));
                    }
                    iconContainer.setScale(1.0f);
                }
            }
        }
    }

    private void updateDescription(Abilities ability) {
        upgradeNameLabel.setText(ability.getName());
        upgradeDescriptionLabel.setText(getDetailedDescription(ability));

        // Update unlocks icons (simplified for now)
        updateUnlocksSection(ability);
    }

    private String getDetailedDescription(Abilities ability) {
        switch (ability) {
            case VITALITY:
                return "Increases your maximum health by 1 heart. This bonus is permanent and helps you survive longer against the endless hordes.";
            case DAMAGER:
                return "Summon a Magic Lens that gives all bullets that pass through it +30% damage and size. This bonus is increased by your Summon Damage.";
            case PROCREASE:
                return "Increases the number of projectiles fired by your weapon by 1. More bullets mean more damage and better crowd control.";
            case AMOCREASE:
                return "Increases your maximum ammunition capacity by 5 rounds. Never run out of bullets when you need them most.";
            case SPEEDY:
                return "Doubles your movement speed for 10 seconds when activated. Perfect for escaping dangerous situations or repositioning quickly.";
            default:
                return "A mysterious upgrade with unknown effects. Choose wisely.";
        }
    }

    private void updateUnlocksSection(Abilities ability) {
        unlocksIconsTable.clear();

        // Add some unlock icons (simplified)
        for (int i = 0; i < 6; i++) {
            Image unlockIcon = new Image(upgradeIconTexture);
            unlockIcon.setColor(new Color(0.4f, 0.4f, 0.4f, 0.8f));

            if (i < 2) {
                // First two are "unlocked"
                unlockIcon.setColor(new Color(0.6f, 0.8f, 1.0f, 1.0f));
            }

            unlocksIconsTable.add(unlockIcon).size(30, 30).pad(2);
            if (i % 3 == 2) unlocksIconsTable.row();
        }
    }

    private Texture createAbilitySpecificIcon(Abilities ability) {
        Color iconColor = getAbilityColor(ability);
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(60, 60, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Clear background
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        // Create ability-specific shapes
        pixmap.setColor(iconColor);

        switch (ability) {
            case VITALITY:
                // Heart shape
                pixmap.fillCircle(20, 35, 12);
                pixmap.fillCircle(40, 35, 12);
                pixmap.fillTriangle(15, 25, 45, 25, 30, 15);
                break;
            case DAMAGER:
                // Sword/blade shape
                pixmap.fillRectangle(28, 10, 4, 40);
                pixmap.fillRectangle(20, 45, 20, 8);
                pixmap.fillTriangle(25, 10, 35, 10, 30, 5);
                break;
            case PROCREASE:
                // Multiple projectiles
                for (int i = 0; i < 3; i++) {
                    pixmap.fillCircle(15 + i * 15, 30, 4);
                    pixmap.fillTriangle(11 + i * 15, 30, 19 + i * 15, 30, 15 + i * 15, 20);
                }
                break;
            case AMOCREASE:
                // Ammo/clip shape
                pixmap.fillRectangle(25, 15, 10, 30);
                pixmap.fillRectangle(23, 12, 14, 6);
                break;
            case SPEEDY:
                // Speed/wind lines
                for (int i = 0; i < 4; i++) {
                    pixmap.fillRectangle(15, 20 + i * 5, 20 - i * 3, 2);
                    pixmap.fillRectangle(20, 22 + i * 5, 25 - i * 3, 2);
                }
                break;
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Color getAbilityColor(Abilities ability) {
        switch (ability) {
            case VITALITY:
                return new Color(0.8f, 0.3f, 0.3f, 1f); // Red for health
            case DAMAGER:
                return new Color(0.9f, 0.6f, 0.2f, 1f); // Orange for damage
            case PROCREASE:
                return new Color(0.3f, 0.6f, 0.9f, 1f); // Blue for projectiles
            case AMOCREASE:
                return new Color(0.6f, 0.8f, 0.3f, 1f); // Green for ammo
            case SPEEDY:
                return new Color(0.9f, 0.9f, 0.3f, 1f); // Yellow for speed
            default:
                return new Color(0.7f, 0.7f, 0.7f, 1f); // Gray default
        }
    }

    private void selectCurrentAbility() {
        if (currentSelectedIndex >= 0 && currentSelectedIndex < selectedAbilities.size()) {
            Abilities selectedAbility = selectedAbilities.get(currentSelectedIndex);
            playClickSound();
            applyAbilityToPlayer(selectedAbility);
            closeWindow();
        }
    }

    private void rerollUpgrades() {
        playClickSound();

        // Animate icons out
        for (Actor child : upgradeIconsTable.getChildren()) {
            child.addAction(Actions.sequence(
                Actions.scaleTo(0f, 0f, 0.2f),
                Actions.run(() -> {
                    // After animation, reroll abilities
                    selectRandomAbilities();
                    recreateUpgradeIcons();
                    selectUpgrade(0);
                })
            ));
        }
    }

    private void recreateUpgradeIcons() {
        upgradeIconsTable.clear();

        for (int i = 0; i < selectedAbilities.size(); i++) {
            final int index = i;
            Abilities ability = selectedAbilities.get(i);

            Table iconContainer = createUpgradeIcon(ability, i);
            iconContainer.setScale(0f);
            iconContainer.addAction(Actions.scaleTo(1.0f, 1.0f, 0.3f, Interpolation.bounceOut));

            iconContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectUpgrade(index);
                }
            });

            upgradeIconsTable.add(iconContainer).size(100, 100);
        }
    }

    private void applyAbilityToPlayer(Abilities ability) {
        // Apply the selected ability's effects to the player
        switch (ability) {
            case VITALITY:
                player.setPlayerHealth(player.getPlayerHealth() + 1);
                Gdx.app.log("LevelUp", "Vitality selected: +1 HP");
                break;
            case DAMAGER:
                // Apply damage boost logic
                Gdx.app.log("LevelUp", "Damager selected: Magic Lens summoned");
                break;
            case PROCREASE:
                // Apply projectile increase logic
                Gdx.app.log("LevelUp", "Procrease selected: +1 projectile");
                break;
            case AMOCREASE:
                // Apply ammo increase logic
                Gdx.app.log("LevelUp", "Amocrease selected: +5 max ammo");
                break;
            case SPEEDY:
                // Apply speed boost logic
                Gdx.app.log("LevelUp", "Speedy selected: Speed boost activated");
                break;
        }
    }

    private void closeWindow() {
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.8f, 0.8f, 0.3f),
                Actions.fadeOut(0.3f)
            ),
            Actions.run(() -> {
                if (onAbilitySelected != null) {
                    onAbilitySelected.run();
                }
                dispose();
                remove();
            })
        ));
    }

    private void setupAnimations() {
        // Initial window animation
        setScale(0.7f);
        setColor(1f, 1f, 1f, 0f);
        addAction(Actions.parallel(
            Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.bounceOut),
            Actions.fadeIn(0.5f)
        ));
    }

    public void act(float delta) {
        super.act(delta);
        animationTime += delta;

        // Add subtle pulsing to selected upgrade
        if (upgradeIconsTable.getChildren().size > currentSelectedIndex) {
            Actor selectedIcon = upgradeIconsTable.getChildren().get(currentSelectedIndex);
            float pulse = 1.2f + 0.05f * (float) Math.sin(animationTime * 4);
            selectedIcon.setScale(pulse);
        }
    }

    private void playClickSound() {
        if (App.isSFX() && Main.getMain().getClickSound() != null) {
            Main.getMain().getClickSound().play();
        }
    }

    public void dispose() {
        if (windowBackgroundTexture != null) windowBackgroundTexture.dispose();
        if (upgradeCardTexture != null) upgradeCardTexture.dispose();
        if (selectedUpgradeTexture != null) selectedUpgradeTexture.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (upgradeIconTexture != null) upgradeIconTexture.dispose();
    }
}
