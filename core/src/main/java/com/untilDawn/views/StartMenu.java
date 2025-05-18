package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.StartMenuController;
import com.untilDawn.models.App;
import com.untilDawn.models.enums.Language;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.UIHelper;

public class StartMenu implements Screen {
    private final TextButton startButton;
    private final TextButton quitButton;
    private final TextButton languageButton;
    private final float ANIMATION_FRAME_DURATION = 0.17f;
    private final float PAUSE_DURATION = 6f;
    private final Image logoImage;
    public Table table;
    private Stage stage;
    private Image[] leavesDecorations;
    private Animation<TextureRegion> eyeBlinkAnimation;
    private float animationTime = 0f;
    private float timeSinceLastAnimation = 0f;
    private boolean isAnimating = false;
    private Image eyeImage;
    private boolean isEnglish = true;
    private StartMenuController controller;
    private TextureRegion firstFrame;

    public StartMenu(Skin skin) {

        TextButton.TextButtonStyle boldButtonStyle = new TextButton.TextButtonStyle();
        boldButtonStyle.font = skin.getFont("font");
        boldButtonStyle.font.getData().setScale(1.1f);
        boldButtonStyle.fontColor = new Color(Color.SALMON);
        boldButtonStyle.overFontColor = Color.valueOf("#f1cedb");
        boldButtonStyle.downFontColor = new Color(Color.LIGHT_GRAY);

        this.startButton = new TextButton(Language.Start.getText(), boldButtonStyle);
        this.quitButton = new TextButton("Quit", boldButtonStyle);
        this.languageButton = new TextButton("Language: English", boldButtonStyle);
        Texture logoTexture = new Texture(Gdx.files.internal("Images/logo.png"));
        this.logoImage = new Image(logoTexture);

        this.table = new Table();
        createEyeBlinkAnimation();
        this.controller = new StartMenuController(this);

        animationTime = 0f;
        isAnimating = false;
        timeSinceLastAnimation = 0f;
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

        firstFrame = eyeFrames.first();
        eyeImage = new Image(firstFrame);
    }

    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        table.setFillParent(true);
        table.top();

        eyeImage.setSize(500, 150);
        eyeImage.setPosition(
            (stage.getWidth() - eyeImage.getWidth()) / 2,
            (stage.getHeight() - eyeImage.getHeight()) / 2
        );
        stage.addActor(eyeImage);

        table.add(logoImage)
            .padTop(150)
            .colspan(2)
            .center()
            .width(500)
            .height(200)
            .row();

        table.add(startButton)
            .colspan(2)
            .center()
            .padTop(50)
            .width(250)
            .height(60)
            .row();

        table.add(languageButton)
            .colspan(2)
            .center()
            .padTop(20)
            .width(250)
            .height(60)
            .row();

        table.add(quitButton)
            .colspan(2)
            .center()
            .padTop(20)
            .width(250)
            .row();

        stage.addActor(table);

        table.toFront();

        languageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleLanguage();
            }
        });

        animationTime = 0f;
        isAnimating = false;
        timeSinceLastAnimation = 0f;
    }

    private void toggleLanguage() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
        App.changeLanguage();
        Main.getMain().setScreen(new StartMenu(GameAssetManager.getGameAssetManager().getSkin()));
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        TextureRegion currentFrame;

        if (isAnimating) {
            animationTime += delta;

            if (animationTime >= eyeBlinkAnimation.getAnimationDuration()) {
                animationTime = 0;
                isAnimating = false;
                timeSinceLastAnimation = 0;
                currentFrame = firstFrame;
            } else {
                currentFrame = eyeBlinkAnimation.getKeyFrame(animationTime, false);
            }
        } else {
            timeSinceLastAnimation += delta;
            if (timeSinceLastAnimation >= PAUSE_DURATION) {
                isAnimating = true;
                animationTime = 0;
            }
            currentFrame = firstFrame;
        }

        eyeImage.setDrawable(new TextureRegionDrawable(currentFrame));

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        eyeImage.setPosition(
            (width - eyeImage.getWidth()) / 2,
            (height - eyeImage.getHeight()) / 2
        );

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

        if (logoImage != null && logoImage.getDrawable() instanceof TextureRegionDrawable) {
            ((TextureRegionDrawable) logoImage.getDrawable()).getRegion().getTexture().dispose();
        }

        if (eyeBlinkAnimation != null) {
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

    public TextButton getLanguageButton() {
        return languageButton;
    }

    public boolean isEnglish() {
        return isEnglish;
    }

    public Stage getStage() {
        return stage;
    }
}
