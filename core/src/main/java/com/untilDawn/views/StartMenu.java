package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.StartMenuController;
import com.untilDawn.models.utils.UIHelper;

public class StartMenu implements Screen {
    private final TextButton startButton;
    private final TextButton quitButton;
    private final Label gameTitle;
    private final Sound clickSound;
    private final float ANIMATION_FRAME_DURATION = 0.15f; // Adjust timing as needed
    public Table table;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Stage stage;
    private Image[] leavesDecorations;
    // Eye blink animation properties
    private Animation<TextureRegion> eyeBlinkAnimation;
    private float animationTime = 0;
    private Image eyeImage;

    public StartMenu(StartMenuController controller, Skin skin) {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/effects/click.wav"));

        // Keep the background texture for transition effects if needed
        backgroundTexture = new Texture(Gdx.files.internal("Images/background.png"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = skin.getFont("font");
        textOnlyStyle.fontColor = new Color(Color.SALMON);
        textOnlyStyle.overFontColor = new Color(Color.SALMON).mul(0.7f);
        textOnlyStyle.downFontColor = new Color(Color.SALMON).mul(0.5f);

        this.startButton = new TextButton("Start", textOnlyStyle);
        this.quitButton = new TextButton("Quit", textOnlyStyle);
        this.gameTitle = new Label("20 Minutes Till Dawn", skin, "title");

        this.table = new Table();

        // Create the eye blink animation
        createEyeBlinkAnimation();
    }

    private void createEyeBlinkAnimation() {
        Texture eyeBlink1 = new Texture(Gdx.files.internal("Images/EyeBlink/EyeBlink_0.png"));
        Texture eyeBlink2 = new Texture(Gdx.files.internal("Images/EyeBlink/EyeBlink_1.png"));
        Texture eyeBlink3 = new Texture(Gdx.files.internal("Images/EyeBlink/EyeBlink_2.png"));

        Array<TextureRegion> eyeFrames = new Array<>();
        eyeFrames.add(new TextureRegion(eyeBlink1));
        eyeFrames.add(new TextureRegion(eyeBlink2));
        eyeFrames.add(new TextureRegion(eyeBlink3));
        eyeFrames.add(new TextureRegion(eyeBlink2));
        eyeFrames.add(new TextureRegion(eyeBlink1));

        eyeBlinkAnimation = new Animation<>(ANIMATION_FRAME_DURATION, eyeFrames);

        eyeImage = new Image(eyeFrames.first());
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        gameTitle.setFontScale(0.8f);
        table.setFillParent(true);
        table.top();

        table.add(gameTitle)
            .padTop(100)
            .colspan(2)
            .center()
            .row();


        table.add(startButton)
            .colspan(2)
            .center()
            .padTop(50)
            .row();

        table.add(eyeImage)
            .colspan(2)
            .center()
            .padTop(0)
            .size(300, 150) // Adjust size as needed
            .row();

        table.add(quitButton)
            .colspan(2)
            .center()
            .padTop(0)
            .row();


        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        animationTime += delta;

        TextureRegion currentFrame = eyeBlinkAnimation.getKeyFrame(animationTime, true);

        eyeImage.setDrawable(new TextureRegionDrawable(currentFrame));

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        // Reposition and resize the leaves decorations when the screen is resized
        if (leavesDecorations != null) {
            // Remove old decorations
            leavesDecorations[0].remove();
            leavesDecorations[1].remove();

            // Add new properly sized decorations
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        // Dispose of animation textures
        if (eyeBlinkAnimation != null && eyeBlinkAnimation.getKeyFrames().length > 0) {
            for (TextureRegion region : eyeBlinkAnimation.getKeyFrames()) {
                if (region.getTexture() != null) {
                    region.getTexture().dispose();
                }
            }
        }
    }

    public TextButton getStartButton() {
        return startButton;
    }

    public TextButton getQuitButton() {
        return quitButton;
    }

    public Stage getStage() {
        return stage;
    }
}
