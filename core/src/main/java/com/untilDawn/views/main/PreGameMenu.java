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
import com.untilDawn.models.utils.UIHelper;

public class PreGameMenu implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final PreGameMeuController controller;
    private int NUM_SELECTORS = 5;

    private Table mainTable;
    private Image[] selectorBubbles;
    private Image[] selectorHighlights;
    private Image characterPortrait;
    private Label characterInfoLabel;

    private Texture selectorTexture;
    private Texture selectorHighlightTexture;
    private Texture panelTexture;

    private Animation<TextureRegion>[] characterAnimations;
    private float animationTime = 0f;

    private int selectedSelector = -1;
    private Characters[] availableCharacters = {
        Characters.Shana,
        Characters.Diamond,
        Characters.Scarlett,
        Characters.Lilith,
        Characters.Dasher
    };

    public PreGameMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.controller = new PreGameMeuController();

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

        for (int i = 0; i < 5; i++) {
            String framePath = "Images/avatars/" + characterName + "/idle" + i + ".png";

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
        mainTable.top().padTop(50);

        Label titleLabel = new Label("Game Setup", skin, "title");
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).expandX().pad(20).row();

        Table contentTable = new Table();

        Table bubblesTable = new Table();
        createSelectorBubbles(bubblesTable);
        contentTable.add(bubblesTable).width(stage.getWidth() * 0.6f).padRight(20);

        Table portraitTable = new Table();
        createPortraitSection(portraitTable);
        contentTable.add(portraitTable).width(stage.getWidth() * 0.4f).top();

        mainTable.add(contentTable).expandX().row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.getLabel().setFontScale(1.2f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                Main.getMain().setScreen(new MainMenu(skin));
            }
        });

        Image panel = new Image(panelTexture);
        panel.setScale(1, -1);
        panel.setOrigin(panel.getWidth() / 2, panel.getHeight() / 2);
        mainTable.add(panel).expandX().fillX().row();
        mainTable.add(backButton);

        Table panelContainer = new Table();
        panelContainer.setFillParent(true);
        panelContainer.padTop(stage.getHeight() - 220);
        panelContainer.add(panel).expandX().fillX().height(200);

        stage.addActor(panelContainer);
        stage.addActor(mainTable);
    }

    private void createSelectorBubbles(Table bubblesTable) {
        NUM_SELECTORS = availableCharacters.length;
        selectorBubbles = new Image[NUM_SELECTORS];
        selectorHighlights = new Image[NUM_SELECTORS];

        for (int i = 0; i < NUM_SELECTORS; i++) {
            final int index = i;
            Stack selectorStack = new Stack();

            selectorHighlights[i] = new Image(selectorHighlightTexture);
            selectorHighlights[i].setSize(60, 60); // Make them smaller
            selectorHighlights[i].setColor(new Color(1f, 0.8f, 0.2f, 1f));
            selectorHighlights[i].setVisible(false);

            selectorBubbles[i] = new Image();
            selectorBubbles[i].setSize(60, 60); // Make them smaller

            selectorStack.add(selectorHighlights[i]);
            selectorStack.add(selectorBubbles[i]);

            selectorStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.playClick();
                    selectBubble(index);

                    controller.onCharacterSelected(availableCharacters[index].getName());
                }
            });

            if (i % 3 == 0 && i > 0) {
                bubblesTable.row();
            }
            bubblesTable.add(selectorStack).width(120).height(120).pad(10);
        }
    }

    private void createPortraitSection(Table portraitTable) {
        characterPortrait = new Image();
        characterPortrait.setVisible(false);

        characterInfoLabel = new Label("", skin);
        characterInfoLabel.setWrap(true);
        characterInfoLabel.setAlignment(Align.center);
        characterInfoLabel.setVisible(false);

        portraitTable.add(characterPortrait).size(250, 300).padBottom(20).row();
        portraitTable.add(characterInfoLabel).width(250).row();
    }

    private void selectBubble(int index) {
        if (selectedSelector == index) {
            selectorHighlights[index].setVisible(false);
            selectedSelector = -1;

            characterPortrait.setVisible(false);
            characterInfoLabel.setVisible(false);

            System.out.println("Selector " + index + " deselected");
        } else {
            if (selectedSelector != -1) {
                selectorHighlights[selectedSelector].setVisible(false);
            }

            selectorHighlights[index].setVisible(true);
            selectedSelector = index;

            updateCharacterPortrait(index);

            System.out.println("Selector " + index + " selected: " + availableCharacters[index].getName());
        }
    }

    private void updateCharacterPortrait(int index) {
        Characters character = availableCharacters[index];
        String portraitPath = "Images/avatars/" + character.getName() + "/portrait.png";

        if (Gdx.files.internal(portraitPath).exists()) {
            Texture portraitTexture = new Texture(Gdx.files.internal(portraitPath));
            characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(portraitTexture)));
        } else {
            Texture placeholderTex = new Texture(Gdx.files.internal("Images/avatars/placeholder.png"));
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        animationTime += delta;

        for (int i = 0; i < NUM_SELECTORS; i++) {
            if (i < characterAnimations.length && characterAnimations[i] != null) {
                TextureRegion currentFrame = characterAnimations[i].getKeyFrame(animationTime, true);
                selectorBubbles[i].setDrawable(new TextureRegionDrawable(currentFrame));
            }
        }

        if (selectedSelector != -1) {
            float pulse = 0.7f + 0.3f * (float) Math.sin(animationTime * 3);
            selectorHighlights[selectedSelector].setColor(1f, 1f, 0f, pulse);
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
    }

    public Image[] getSelectorBubbles() {
        return selectorBubbles;
    }

    public Stage getStage() {
        return stage;
    }

    public int getSelectedSelector() {
        return selectedSelector;
    }

    public Characters getSelectedCharacter() {
        if (selectedSelector >= 0 && selectedSelector < availableCharacters.length) {
            return availableCharacters[selectedSelector];
        }
        return null;
    }
}
