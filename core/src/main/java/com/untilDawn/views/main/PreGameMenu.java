package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.PreGameMeuController;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.enums.Weapons;
import com.untilDawn.models.utils.UIHelper;

public class PreGameMenu implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final PreGameMeuController controller;
    private int NUM_CHARACTER_SELECTORS = 5;
    private int NUM_WEAPON_SELECTORS = 3;

    private Table mainTable;
    private Image[] characterBubbles;
    private Image[] characterHighlights;
    private Image[] weaponBubbles;
    private Image[] weaponHighlights;
    private Image characterPortrait;
    private Image weaponPortrait;
    private Label characterInfoLabel;
    private Label weaponInfoLabel;
    private TextButton playButton;

    private Texture selectorTexture;
    private Texture selectorHighlightTexture;
    private Texture panelTexture;

    private Animation<TextureRegion>[] characterAnimations;
    private float animationTime = 0f;

    private float widthFactor;
    private float heightFactor;

    private int selectedCharacter = -1;
    private int selectedWeapon = -1;
    private Characters[] availableCharacters = {
        Characters.Shana,
        Characters.Diamond,
        Characters.Scarlett,
        Characters.Lilith,
        Characters.Dasher,
        Characters.Raven
    };
    private Weapons[] availableWeapons = {
        Weapons.Revolver,
        Weapons.Shotgun,
        Weapons.Dual_Smg
    };

    public PreGameMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        widthFactor = Gdx.graphics.getWidth() / 1920f;
        heightFactor = Gdx.graphics.getHeight() / 1080f;

        this.controller = new PreGameMeuController();
        controller.setView(this);

        this.selectorTexture = new Texture(Gdx.files.internal("Images/selectorBubble/SelectorBubble1.png"));
        this.selectorHighlightTexture = new Texture(Gdx.files.internal("Images/selectorBubble/SelectorBubble1.png"));
        this.panelTexture = new Texture(Gdx.files.internal("Images/SelectScreenPanel.png"));

        createCharacterAnimations();
        createUI();
    }

    @SuppressWarnings("unchecked")
    private void createCharacterAnimations() {
        characterAnimations = new Animation[availableCharacters.length];

        for (int i = 0; i < availableCharacters.length; i++) {
            Characters character = availableCharacters[i];
            characterAnimations[i] = createCharacterIdleAnimation(character.getName());
        }
    }

    private Animation<TextureRegion> createCharacterIdleAnimation(String characterName) {
        Array<TextureRegion> frames = new Array<>();

        for (int i = 0; i < 6; i++) {
            String framePath = "Images/characters/" + characterName + "/idle" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frameTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                frames.add(new TextureRegion(frameTex));
            }
        }

        float FRAME_DURATION = 0.13f;
        return new Animation<>(FRAME_DURATION, frames);
    }

    private void createUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(20 * heightFactor);

        Label titleLabel = new Label("Game Setup", skin, "title");
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(Math.min(widthFactor, heightFactor) * 0.9f);
        mainTable.add(titleLabel).expandX().pad(10 * heightFactor).row();

        Table contentTable = new Table();

        Table selectorsTable = new Table();

        // Character selection section
        Label charactersLabel = new Label("Select Character", skin);
        charactersLabel.setAlignment(Align.center);
        charactersLabel.setFontScale(Math.min(widthFactor, heightFactor) * 0.9f);
        selectorsTable.add(charactersLabel).colspan(3).pad(5 * heightFactor).row();

        Table characterBubblesTable = new Table();
        createCharacterBubbles(characterBubblesTable);
        selectorsTable.add(characterBubblesTable).colspan(3).pad(5 * heightFactor).row();

        // Weapon selection section
        Label weaponsLabel = new Label("Select Weapon", skin);
        weaponsLabel.setAlignment(Align.center);
        weaponsLabel.setFontScale(Math.min(widthFactor, heightFactor) * 0.9f);
        selectorsTable.add(weaponsLabel).colspan(3).pad(5 * heightFactor).row();

        Table weaponBubblesTable = new Table();
        createWeaponBubbles(weaponBubblesTable);
        selectorsTable.add(weaponBubblesTable).colspan(3).pad(5 * heightFactor).row();

        TextButton.TextButtonStyle playButtonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        playButtonStyle.fontColor = Color.SALMON;
        playButtonStyle.overFontColor = Color.valueOf("#f1cedb");
        playButtonStyle.downFontColor = Color.LIGHT_GRAY;

        playButton = new TextButton("PLAY", playButtonStyle);
        playButton.getLabel().setFontScale(1.3f * Math.min(widthFactor, heightFactor));
        playButton.setDisabled(true); // Initially disabled until both selections are made

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!playButton.isDisabled()) {
                    controller.playClick();
                    controller.startGame();
                }
            }
        });

        selectorsTable.add(playButton).colspan(3).width(180 * widthFactor).height(50 * heightFactor).pad(15 * heightFactor).row();

        contentTable.add(selectorsTable).width(stage.getWidth() * 0.55f).padRight(10 * widthFactor);

        Table infoTable = new Table();
        createInfoSection(infoTable);
        contentTable.add(infoTable).width(stage.getWidth() * 0.35f).top();

        mainTable.add(contentTable).expandX().row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.getLabel().setFontScale(1.1f * Math.min(widthFactor, heightFactor));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                Main.getMain().setScreen(new MainMenu(skin));
            }
        });

        float panelHeight = 150 * heightFactor;

        Image panel = new Image(panelTexture);
        panel.setScale(1, -1);
        panel.setOrigin(panel.getWidth() / 2, panel.getHeight() / 2);

        Table panelContainer = new Table();
        panelContainer.setFillParent(true);
        panelContainer.padTop(stage.getHeight() - panelHeight - 20);
        panelContainer.add(panel).expandX().fillX().height(panelHeight);

        mainTable.add(backButton).pad(10 * heightFactor);

        stage.addActor(panelContainer);
        stage.addActor(mainTable);
    }

    private void createCharacterBubbles(Table bubblesTable) {
        NUM_CHARACTER_SELECTORS = availableCharacters.length;
        characterBubbles = new Image[NUM_CHARACTER_SELECTORS];
        characterHighlights = new Image[NUM_CHARACTER_SELECTORS];

        // Calculate the bubble size based on screen dimensions
        float bubbleSize = 50 * Math.min(widthFactor, heightFactor);
        float stackSize = 80 * Math.min(widthFactor, heightFactor);
        float padding = 5 * Math.min(widthFactor, heightFactor);

        for (int i = 0; i < NUM_CHARACTER_SELECTORS; i++) {
            final int index = i;
            Stack selectorStack = new Stack();

            characterHighlights[i] = new Image(selectorHighlightTexture);
            characterHighlights[i].setSize(bubbleSize, bubbleSize);
            characterHighlights[i].setColor(new Color(1f, 0.8f, 0.2f, 1f));
            characterHighlights[i].setVisible(false);

            characterBubbles[i] = new Image();
            characterBubbles[i].setSize(bubbleSize, bubbleSize);

            selectorStack.add(characterHighlights[i]);
            selectorStack.add(characterBubbles[i]);

            selectorStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.playClick();
                    selectCharacter(index);
                    controller.onCharacterSelected(availableCharacters[index].getName());
                    updatePlayButtonState();
                }
            });

            if (i % 3 == 0 && i > 0) {
                bubblesTable.row();
            }
            bubblesTable.add(selectorStack).width(stackSize).height(stackSize).pad(padding);
        }
    }

    private void createWeaponBubbles(Table bubblesTable) {
        NUM_WEAPON_SELECTORS = availableWeapons.length;
        weaponBubbles = new Image[NUM_WEAPON_SELECTORS];
        weaponHighlights = new Image[NUM_WEAPON_SELECTORS];

        // Calculate the bubble size based on screen dimensions
        float bubbleSize = 50 * Math.min(widthFactor, heightFactor);
        float stackSize = 80 * Math.min(widthFactor, heightFactor);
        float padding = 5 * Math.min(widthFactor, heightFactor);

        for (int i = 0; i < NUM_WEAPON_SELECTORS; i++) {
            final int index = i;
            Stack selectorStack = new Stack();

            weaponHighlights[i] = new Image(selectorHighlightTexture);
            weaponHighlights[i].setSize(bubbleSize, bubbleSize);
            weaponHighlights[i].setColor(new Color(1f, 0.2f, 0.2f, 1f));
            weaponHighlights[i].setVisible(false);

            weaponBubbles[i] = new Image();
            weaponBubbles[i].setSize(bubbleSize, bubbleSize);

            String weaponIconPath = "Images/weapons/" + availableWeapons[i].getName() + "/still.png";
            if (Gdx.files.internal(weaponIconPath).exists()) {
                Texture weaponTex = new Texture(Gdx.files.internal(weaponIconPath));
                weaponBubbles[i].setDrawable(new TextureRegionDrawable(new TextureRegion(weaponTex)));
            } else {
                Texture placeholderTex = new Texture(Gdx.files.internal("Images/selectorBubble/SelectorBubble1.png"));
                weaponBubbles[i].setDrawable(new TextureRegionDrawable(new TextureRegion(placeholderTex)));
                System.out.println("Warning: Weapon icon not found: " + weaponIconPath);
            }

            selectorStack.add(weaponHighlights[i]);
            selectorStack.add(weaponBubbles[i]);

            selectorStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.playClick();
                    selectWeapon(index);
                    controller.onWeaponSelected(availableWeapons[index].getName());
                    updatePlayButtonState();
                }
            });

            bubblesTable.add(selectorStack).width(stackSize).height(stackSize).pad(padding);
        }
    }

    private void createInfoSection(Table infoTable) {
        float characterPortraitSize = 150 * Math.min(widthFactor, heightFactor);
        float weaponPortraitWidth = 150 * widthFactor;
        float weaponPortraitHeight = 75 * heightFactor;
        float infoWidth = 180 * widthFactor;
        float padding = 5 * Math.min(widthFactor, heightFactor);

        Table characterPortraitTable = new Table();
        characterPortrait = new Image();
        characterPortrait.setVisible(false);

        characterInfoLabel = new Label("", skin);
        characterInfoLabel.setWrap(true);
        characterInfoLabel.setAlignment(Align.center);
        characterInfoLabel.setFontScale(0.9f * Math.min(widthFactor, heightFactor));
        characterInfoLabel.setVisible(false);

        Label characterHeader = new Label("Character", skin, "title");
        characterHeader.setAlignment(Align.center);
        characterHeader.setFontScale(0.9f * Math.min(widthFactor, heightFactor));

        characterPortraitTable.add(characterHeader).padBottom(padding).row();
        characterPortraitTable.add(characterPortrait).size(characterPortraitSize, characterPortraitSize).padBottom(padding).row();
        characterPortraitTable.add(characterInfoLabel).width(infoWidth).row();

        Table weaponPortraitTable = new Table();
        weaponPortrait = new Image();
        weaponPortrait.setVisible(false);

        weaponInfoLabel = new Label("", skin);
        weaponInfoLabel.setWrap(true);
        weaponInfoLabel.setAlignment(Align.center);
        weaponInfoLabel.setFontScale(0.9f * Math.min(widthFactor, heightFactor));
        weaponInfoLabel.setVisible(false);

        Label weaponHeader = new Label("Weapon", skin, "title");
        weaponHeader.setAlignment(Align.center);
        weaponHeader.setFontScale(0.9f * Math.min(widthFactor, heightFactor));

        weaponPortraitTable.add(weaponHeader).padBottom(padding).row();
        weaponPortraitTable.add(weaponPortrait).size(weaponPortraitWidth, weaponPortraitHeight).padBottom(padding).row();
        weaponPortraitTable.add(weaponInfoLabel).width(infoWidth).row();

        infoTable.add(characterPortraitTable).padBottom(10 * heightFactor).row();
        infoTable.add(weaponPortraitTable).row();
    }

    private void selectCharacter(int index) {
        if (selectedCharacter == index) {
            characterHighlights[index].setVisible(false);
            selectedCharacter = -1;
            characterPortrait.setVisible(false);
            characterInfoLabel.setVisible(false);
        } else {
            if (selectedCharacter != -1) {
                characterHighlights[selectedCharacter].setVisible(false);
            }

            characterHighlights[index].setVisible(true);
            selectedCharacter = index;
            updateCharacterPortrait(index);
        }
    }

    private void selectWeapon(int index) {
        if (selectedWeapon == index) {
            weaponHighlights[index].setVisible(false);
            selectedWeapon = -1;
            weaponPortrait.setVisible(false);
            weaponInfoLabel.setVisible(false);
        } else {
            if (selectedWeapon != -1) {
                weaponHighlights[selectedWeapon].setVisible(false);
            }

            weaponHighlights[index].setVisible(true);
            selectedWeapon = index;
            updateWeaponPortrait(index);
        }
    }

    private void updateCharacterPortrait(int index) {
        Characters character = availableCharacters[index];
        String portraitPath = "Images/characters/" + character.getName() + "/portrait.png";

        if (Gdx.files.internal(portraitPath).exists()) {
            Texture portraitTexture = new Texture(Gdx.files.internal(portraitPath));
            characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(portraitTexture)));
        } else {
            Texture placeholderTex = new Texture(Gdx.files.internal("Images/characters/placeholder.png"));
            characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(placeholderTex)));
            System.out.println("Warning: Portrait not found for " + character.getName() + ": " + portraitPath);
        }

        characterPortrait.setVisible(true);

        String info = character.getName() + "\n" +
            "HP: " + character.getHp() + "\n" +
            "Speed: " + character.getSpeed();
        characterInfoLabel.setText(info);
        characterInfoLabel.setVisible(true);
    }

    private void updateWeaponPortrait(int index) {
        Weapons weapon = availableWeapons[index];
        String portraitPath = "Images/weapons/" + weapon.getName().replace(" ", "") + "_large.png";

        if (Gdx.files.internal(portraitPath).exists()) {
            Texture portraitTexture = new Texture(Gdx.files.internal(portraitPath));
            weaponPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(portraitTexture)));
        } else {
            Texture placeholderTex = new Texture(Gdx.files.internal("Images/selectorBubble/SelectorBubble.png"));
            weaponPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(placeholderTex)));
            System.out.println("Warning: Portrait not found for " + weapon.getName() + ": " + portraitPath);
        }

        weaponPortrait.setVisible(true);

        String info = weapon.getName() + "\n" +
            "Damage: " + weapon.getDamage() + "\n" +
            "Ammo: " + weapon.getAmmoMax() + "\n" +
            "Reload Time: " + weapon.getReloadTime() + "s";
        weaponInfoLabel.setText(info);
        weaponInfoLabel.setVisible(true);
    }

    private void updatePlayButtonState() {
        if (selectedCharacter != -1 && selectedWeapon != -1) {
            playButton.setDisabled(false);
            playButton.getLabel().setColor(Color.WHITE);
        } else {
            playButton.setDisabled(true);
            playButton.getLabel().setColor(Color.GRAY);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        animationTime += delta;

        for (int i = 0; i < NUM_CHARACTER_SELECTORS; i++) {
            if (i < characterAnimations.length && characterAnimations[i] != null) {
                TextureRegion currentFrame = characterAnimations[i].getKeyFrame(animationTime, true);
                characterBubbles[i].setDrawable(new TextureRegionDrawable(currentFrame));
            }
        }

        if (selectedCharacter != -1) {
            float pulse = 0.7f + 0.3f * (float) Math.sin(animationTime * 3);
            characterHighlights[selectedCharacter].setColor(1f, 1f, 0f, pulse);
        }

        if (selectedWeapon != -1) {
            float pulse = 0.7f + 0.3f * (float) Math.sin(animationTime * 3);
            weaponHighlights[selectedWeapon].setColor(1f, 0f, 0f, pulse);
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        widthFactor = width / 1920f;
        heightFactor = height / 1080f;
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
        if (selectorTexture != null) {
            selectorTexture.dispose();
        }
        if (selectorHighlightTexture != null) {
            selectorHighlightTexture.dispose();
        }
        if (panelTexture != null) {
            panelTexture.dispose();
        }

        if (characterAnimations != null) {
            for (Animation<TextureRegion> animation : characterAnimations) {
                if (animation != null) {
                    for (TextureRegion region : animation.getKeyFrames()) {
                        if (region != null && region.getTexture() != null) {
                            region.getTexture().dispose();
                        }
                    }
                }
            }
        }

        if (characterPortrait != null && characterPortrait.getDrawable() instanceof TextureRegionDrawable) {
            Texture texture = ((TextureRegionDrawable) characterPortrait.getDrawable()).getRegion().getTexture();
            if (texture != null) {
                texture.dispose();
            }
        }

        if (weaponPortrait != null && weaponPortrait.getDrawable() instanceof TextureRegionDrawable) {
            Texture texture = ((TextureRegionDrawable) weaponPortrait.getDrawable()).getRegion().getTexture();
            if (texture != null) {
                texture.dispose();
            }
        }

        if (weaponBubbles != null) {
            for (Image bubble : weaponBubbles) {
                if (bubble != null && bubble.getDrawable() instanceof TextureRegionDrawable) {
                    Texture texture = ((TextureRegionDrawable) bubble.getDrawable()).getRegion().getTexture();
                    if (texture != null) {
                        texture.dispose();
                    }
                }
            }
        }
    }

    public Image[] getCharacterBubbles() {
        return characterBubbles;
    }

    public Image[] getWeaponBubbles() {
        return weaponBubbles;
    }

    public Stage getStage() {
        return stage;
    }

    public int getSelectedCharacter() {
        return selectedCharacter;
    }

    public int getSelectedWeapon() {
        return selectedWeapon;
    }

    public Characters getSelectedCharacterObject() {
        if (selectedCharacter >= 0 && selectedCharacter < availableCharacters.length) {
            return availableCharacters[selectedCharacter];
        }
        return null;
    }

    public Weapons getSelectedWeaponObject() {
        if (selectedWeapon >= 0 && selectedWeapon < availableWeapons.length) {
            return availableWeapons[selectedWeapon];
        }
        return null;
    }
}
