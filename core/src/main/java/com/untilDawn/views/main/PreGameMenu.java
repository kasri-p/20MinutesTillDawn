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
import com.untilDawn.models.utils.UIHelper;

public class PreGameMenu implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final PreGameMeuController controller;
    private final float FRAME_DURATION = 0.15f;
    private int NUM_SELECTORS = 5;

    private Table mainTable;
    private Image[] selectorBubbles;
    private Image[] selectorHighlights;

    private Texture selectorTexture;
    private Texture selectorHighlightTexture; // New texture for the highlighted selector
    private Texture panelTexture;

    private Animation<TextureRegion> ravenIdleAnimation;
    private float animationTime = 0f;

    private int selectedSelector = -1;

    public PreGameMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.controller = new PreGameMeuController();

        this.selectorTexture = new Texture(Gdx.files.internal("Images/selectorBubble/SelectorBubble1.png"));
        this.selectorHighlightTexture = new Texture(Gdx.files.internal("Images/selectorBubble/SelectorBubble1.png"));
        this.panelTexture = new Texture(Gdx.files.internal("Images/SelectScreenPanel.png"));
        createRavenIdleAnimation();
        createUI();
    }

    private void createRavenIdleAnimation() {
        Array<TextureRegion> frames = new Array<>();

        for (int i = 0; i < 5; i++) {
            Texture frameTex = new Texture(Gdx.files.internal("Images/avatars/idle/Raven/Raven_Idle_" + i + ".png"));
            frames.add(new TextureRegion(frameTex));
        }

        ravenIdleAnimation = new Animation<>(FRAME_DURATION, frames);
    }

    private void createUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(50);

        Label titleLabel = new Label("Game Setup", skin, "title");
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).expandX().pad(20).row();

        Table bubblesTable = new Table();

        NUM_SELECTORS = 6;
        selectorBubbles = new Image[NUM_SELECTORS];
        selectorHighlights = new Image[NUM_SELECTORS];

        for (int i = 0; i < NUM_SELECTORS; i++) {
            Stack selectorStack = new Stack();

            selectorHighlights[i] = new Image(selectorHighlightTexture);
            selectorHighlights[i].setSize(75, 75);
            selectorHighlights[i].setColor(new Color(1f, 0.8f, 0.2f, 1f));
            selectorHighlights[i].setVisible(false);

            selectorBubbles[i] = new Image(selectorTexture);
            selectorBubbles[i].setSize(75, 75);

            selectorStack.add(selectorHighlights[i]);
            selectorStack.add(selectorBubbles[i]);

            final int index = i;
            selectorStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.playClick();
                    selectBubble(index);
                }
            });


            bubblesTable.add(selectorStack).width(160).height(160).pad(15);
        }

        mainTable.add(bubblesTable).expandX().row();

        // TODO: make this button mach the game
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

    private void selectBubble(int index) {
        if (selectedSelector == index) {
            selectorHighlights[index].setVisible(false);
            selectedSelector = -1;
            System.out.println("Selector " + index + " deselected");
        } else {
            if (selectedSelector != -1) {
                selectorHighlights[selectedSelector].setVisible(false);
            }

            selectorHighlights[index].setVisible(true);
            selectedSelector = index;
            System.out.println("Selector " + index + " selected");
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

        TextureRegion currentFrame = ravenIdleAnimation.getKeyFrame(animationTime, true);

        selectorBubbles[0].setDrawable(new TextureRegionDrawable(currentFrame));

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

        if (ravenIdleAnimation != null) {
            for (TextureRegion region : ravenIdleAnimation.getKeyFrames()) {
                if (region.getTexture() != null) {
                    region.getTexture().dispose();
                }
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
}
