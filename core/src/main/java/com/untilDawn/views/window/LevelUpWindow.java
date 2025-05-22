package com.untilDawn.views.window;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
import com.untilDawn.models.utils.GameAssetManager;

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

    private List<Abilities> selectedAbilities;
    private int currentSelectedIndex = 0;

    private Texture windowBackgroundTexture;
    private Texture upgradeCardTexture;
    private Texture selectedUpgradeTexture;
    private Texture buttonTexture;

    private float animationTime = 0f;

    public LevelUpWindow(Skin skin, Player player, Stage gameStage, Runnable onAbilitySelected) {
        super("", skin);
        this.player = player;
        this.gameStage = gameStage;
        this.onAbilitySelected = onAbilitySelected;

        loadTextures();
        selectRandomAbilities();
        setupWindow();
        createContent();
        setupAnimations();
    }

    private void loadTextures() {
        try {
            // Create main window background - dark with cyan border
            windowBackgroundTexture = GameAssetManager.getGameAssetManager().getPanel();
            windowBackgroundTexture = resizeTexture(windowBackgroundTexture, 900, 600);

            // Create upgrade card background
            upgradeCardTexture = createUpgradeCardTexture(120, 120);

            // Create selected upgrade background (highlighted)
            selectedUpgradeTexture = createSelectedUpgradeTexture(120, 120);

            // Create button texture
            buttonTexture = createButtonTexture(280, 60);

        } catch (Exception e) {
            Gdx.app.error("LevelUpWindow", "Error loading textures: " + e.getMessage());
        }
    }

    private Texture resizeTexture(Texture originalTexture, int width, int height) {
        if (!originalTexture.getTextureData().isPrepared()) {
            originalTexture.getTextureData().prepare();
        }

        Pixmap originalPixmap = originalTexture.getTextureData().consumePixmap();
        try {
            Pixmap resizedPixmap = highQualityResizePixmap(originalPixmap, width, height);
            Texture resizedTexture = new Texture(resizedPixmap);
            resizedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return resizedTexture;
        } finally {
            originalPixmap.dispose();
        }
    }

    private Pixmap highQualityResizePixmap(Pixmap original, int targetWidth, int targetHeight) {
        if (targetWidth < original.getWidth() || targetHeight < original.getHeight()) {
            return downscalePixmap(original, targetWidth, targetHeight);
        } else {
            Pixmap resized = new Pixmap(targetWidth, targetHeight, original.getFormat());
            resized.setFilter(Pixmap.Filter.BiLinear);
            resized.drawPixmap(original,
                0, 0, original.getWidth(), original.getHeight(),
                0, 0, targetWidth, targetHeight);
            return resized;
        }
    }

    private Pixmap downscalePixmap(Pixmap original, int targetWidth, int targetHeight) {
        int currentWidth = original.getWidth();
        int currentHeight = original.getHeight();

        Pixmap current = new Pixmap(currentWidth, currentHeight, original.getFormat());
        current.drawPixmap(original, 0, 0);

        try {
            while (currentWidth / 2 >= targetWidth && currentHeight / 2 >= targetHeight) {
                int newWidth = Math.max(currentWidth / 2, targetWidth);
                int newHeight = Math.max(currentHeight / 2, targetHeight);

                Pixmap smaller = new Pixmap(newWidth, newHeight, current.getFormat());
                smaller.setFilter(Pixmap.Filter.BiLinear);
                smaller.drawPixmap(current,
                    0, 0, currentWidth, currentHeight,
                    0, 0, newWidth, newHeight);

                current.dispose();
                current = smaller;
                currentWidth = newWidth;
                currentHeight = newHeight;
            }

            if (currentWidth != targetWidth || currentHeight != targetHeight) {
                Pixmap finalPixmap = new Pixmap(targetWidth, targetHeight, current.getFormat());
                finalPixmap.setFilter(Pixmap.Filter.BiLinear);
                finalPixmap.drawPixmap(current,
                    0, 0, currentWidth, currentHeight,
                    0, 0, targetWidth, targetHeight);

                current.dispose();
                current = finalPixmap;
            }

            return current;
        } catch (Exception e) {
            current.dispose();
            throw e;
        }
    }

    private Texture createUpgradeCardTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Dark background
        pixmap.setColor(new Color(0.1f, 0.15f, 0.2f, 0.9f));
        pixmap.fill();

        // Border effect
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
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

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
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(new Color(0.08f, 0.12f, 0.16f, 0.9f));
        pixmap.fill();

        pixmap.setColor(new Color(0.3f, 0.6f, 0.8f, 0.9f));
        for (int i = 0; i < 2; i++) {
            pixmap.drawRectangle(i, i, width - 2 * i, height - 2 * i);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void selectRandomAbilities() {
        selectedAbilities = new ArrayList<>();

        List<Abilities> allAbilities = new ArrayList<>();
        for (Abilities ability : Abilities.values()) {
            allAbilities.add(ability);
        }

        Collections.shuffle(allAbilities);
        for (int i = 0; i < Math.min(3, allAbilities.size()); i++) {
            selectedAbilities.add(allAbilities.get(i));
        }

        if (selectedAbilities.isEmpty()) {
            selectedAbilities.add(Abilities.VITALITY);
        }
    }

    private void setupWindow() {
        getTitleLabel().setVisible(false);
        getTitleTable().setVisible(false);

        setSize(900, 600);

        float centerX = (Gdx.graphics.getWidth() - getWidth()) / 2;
        float centerY = (Gdx.graphics.getHeight() - getHeight()) / 2;
        setPosition(centerX, centerY);

        if (windowBackgroundTexture != null) {
            setBackground(new TextureRegionDrawable(new TextureRegion(windowBackgroundTexture)));
        }

        setModal(true);
        setMovable(false);
    }

    private void createContent() {
        clear();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(40);

        createTitleSection(mainTable);
        createUpgradeIconsSection(mainTable);
        createDescriptionSection(mainTable);
        createButtonsSection(mainTable);

        add(mainTable).expand().fill();

        selectUpgrade(0);
    }

    private void createTitleSection(Table mainTable) {
        titleLabel = new Label("Level Up! Choose an Upgrade", getSkin());
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(new Color(0.9f, 0.7f, 0.2f, 1.0f)); // Gold color

        mainTable.add(titleLabel).padBottom(30).row();
    }

    private void createUpgradeIconsSection(Table mainTable) {
        upgradeIconsTable = new Table();
        upgradeIconsTable.defaults().space(20);

        for (int i = 0; i < selectedAbilities.size(); i++) {
            final int index = i;
            Abilities ability = selectedAbilities.get(i);

            Table iconContainer = createUpgradeIcon(ability, i);

            iconContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectUpgrade(index);
                    playClickSound();
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

            upgradeIconsTable.add(iconContainer).size(120, 120);
        }

        mainTable.add(upgradeIconsTable).padBottom(30).row();
    }

    private Table createUpgradeIcon(Abilities ability, int index) {
        Table container = new Table();
        container.setTransform(true);
        container.setOrigin(Align.center);

        if (upgradeCardTexture != null) {
            container.setBackground(new TextureRegionDrawable(new TextureRegion(upgradeCardTexture)));
        }

        Table contentTable = new Table();

        // Try to use the ability's texture first, fallback to icon
        Image icon;
        Texture abilityTexture = ability.getTexture();
        if (abilityTexture != null) {
            icon = new Image(abilityTexture);
            contentTable.add(icon).size(60, 60).pad(5).row();
        } else {
            // Fallback to emoji icon
            Label iconLabel = new Label(ability.getIcon(), getSkin());
            iconLabel.setAlignment(Align.center);
            iconLabel.setFontScale(2.0f);
            contentTable.add(iconLabel).pad(5).row();
        }

        // Add ability name below icon
        Label nameLabel = new Label(ability.getName(), getSkin());
        nameLabel.setAlignment(Align.center);
        nameLabel.setFontScale(0.7f);
        nameLabel.setWrap(true);
        contentTable.add(nameLabel).width(100).padTop(5);

        container.add(contentTable).expand().fill();

        return container;
    }

    private void createDescriptionSection(Table mainTable) {
        descriptionTable = new Table();
        if (upgradeCardTexture != null) {
            descriptionTable.setBackground(new TextureRegionDrawable(new TextureRegion(upgradeCardTexture)));
        }
        descriptionTable.pad(30);

        // Upgrade name
        upgradeNameLabel = new Label("", getSkin());
        upgradeNameLabel.setAlignment(Align.center);
        upgradeNameLabel.setFontScale(1.5f);
        upgradeNameLabel.setColor(new Color(0.9f, 0.7f, 0.2f, 1.0f));

        // Upgrade description
        upgradeDescriptionLabel = new Label("", getSkin());
        upgradeDescriptionLabel.setAlignment(Align.center);
        upgradeDescriptionLabel.setWrap(true);
        upgradeDescriptionLabel.setFontScale(1.1f);
        upgradeDescriptionLabel.setColor(new Color(0.9f, 0.9f, 0.9f, 1.0f));

        // Ability type
        unlocksLabel = new Label("", getSkin());
        unlocksLabel.setAlignment(Align.center);
        unlocksLabel.setFontScale(0.9f);
        unlocksLabel.setColor(new Color(0.7f, 0.7f, 0.7f, 1.0f));

        descriptionTable.add(upgradeNameLabel).padBottom(15).row();
        descriptionTable.add(upgradeDescriptionLabel).width(600).padBottom(15).row();
        descriptionTable.add(unlocksLabel);

        mainTable.add(descriptionTable).size(700, 200).padBottom(30).row();
    }

    private void createButtonsSection(Table mainTable) {
        buttonsTable = new Table();
        buttonsTable.defaults().space(30);

        TextButton chooseButton = new TextButton("Choose", getSkin());
        if (buttonTexture != null) {
            chooseButton.setBackground(new TextureRegionDrawable(new TextureRegion(buttonTexture)));
        }
        chooseButton.getLabel().setFontScale(1.2f);
        chooseButton.getLabel().setColor(new Color(0.2f, 0.8f, 0.2f, 1.0f));

        TextButton rerollButton = new TextButton("Reroll Options", getSkin());
        if (buttonTexture != null) {
            rerollButton.setBackground(new TextureRegionDrawable(new TextureRegion(buttonTexture)));
        }
        rerollButton.getLabel().setFontScale(1.2f);
        rerollButton.getLabel().setColor(new Color(0.8f, 0.6f, 0.2f, 1.0f));

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

        buttonsTable.add(chooseButton).size(250, 50);
        buttonsTable.add(rerollButton).size(250, 50);

        mainTable.add(buttonsTable);
    }

    private void selectUpgrade(int index) {
        if (index < 0 || index >= selectedAbilities.size()) return;

        currentSelectedIndex = index;
        Abilities selectedAbility = selectedAbilities.get(index);

        updateUpgradeSelection();
        updateDescription(selectedAbility);
    }

    private void updateUpgradeSelection() {
        for (int i = 0; i < upgradeIconsTable.getChildren().size; i++) {
            Actor child = upgradeIconsTable.getChildren().get(i);
            if (child instanceof Table) {
                Table iconContainer = (Table) child;
                if (i == currentSelectedIndex) {
                    if (selectedUpgradeTexture != null) {
                        iconContainer.setBackground(new TextureRegionDrawable(new TextureRegion(selectedUpgradeTexture)));
                    }
                    iconContainer.setScale(1.15f);
                } else {
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
        upgradeDescriptionLabel.setText(ability.getDescription());

        String typeDescription = ability.getType().getDescription();
        if (ability.getType() == Abilities.AbilityType.ACTIVE) {
            typeDescription += "\nDuration: " + ability.getDuration() + "s, Cooldown: " + ability.getCooldown() + "s";
        }
        unlocksLabel.setText(typeDescription);
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
                    playClickSound();
                }
            });

            upgradeIconsTable.add(iconContainer).size(120, 120);
        }
    }

    private void applyAbilityToPlayer(Abilities ability) {
        Gdx.app.log("LevelUpWindow", "Applying ability: " + ability.getName());

        switch (ability) {
            case VITALITY:
                player.applyVitality();
                break;

            case DAMAGER:
                ability.activate();
                player.activateDamager();
                break;

            case PROCREASE:
                player.applyProcrease();
                break;

            case AMOCREASE:
                player.applyAmocrease();
                break;

            case SPEEDY:
                ability.activate();
                player.activateSpeedy();
                break;

            case REGENERATION:
                player.enableRegeneration();
                break;

            case SHIELD:
                ability.activate();
                player.activateShield();
                break;

            case MULTISHOT:
                ability.activate();
                player.activateMultishot();
                break;
        }
    }

    private void closeWindow() {
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.8f, 0.8f, 0.2f),
                Actions.fadeOut(0.2f)
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
        setScale(0.8f);
        setColor(1f, 1f, 1f, 0f);
        addAction(Actions.parallel(
            Actions.scaleTo(1.0f, 1.0f, 0.4f, Interpolation.bounceOut),
            Actions.fadeIn(0.4f)
        ));
    }

    public void act(float delta) {
        super.act(delta);
        animationTime += delta;

        if (upgradeIconsTable.getChildren().size > currentSelectedIndex) {
            Actor selectedIcon = upgradeIconsTable.getChildren().get(currentSelectedIndex);
            float pulse = 1.15f + 0.03f * (float) Math.sin(animationTime * 4);
            selectedIcon.setScale(pulse);
        }

        // Update all abilities
        for (Abilities ability : Abilities.values()) {
            ability.update(delta);
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
    }
}
