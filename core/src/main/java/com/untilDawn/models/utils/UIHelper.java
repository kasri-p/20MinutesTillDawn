package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;


public class UIHelper {
    private static final Color BACKGROUND_COLOR = Color.valueOf("301934");

    private static Texture leavesTexture;

    public static void clearScreenWithBackgroundColor() {
        ScreenUtils.clear(BACKGROUND_COLOR);
    }

    public static Image[] addLeavesDecoration(Stage stage) {
        if (leavesTexture == null) {
            leavesTexture = new Texture(Gdx.files.internal("images/TitleLeaves.png"));
        }

        Image[] leaves = new Image[2];

        leaves[0] = new Image(leavesTexture);

        float scale = stage.getHeight() / leavesTexture.getHeight();
        float scaledWidth = leavesTexture.getWidth() * scale;

        leaves[0].setSize(scaledWidth, stage.getHeight());
        leaves[0].setPosition(0, 0);

        leaves[1] = new Image(leavesTexture);
        leaves[1].setSize(scaledWidth, stage.getHeight());
        leaves[1].setPosition(stage.getWidth(), 0);
        leaves[1].setScaleX(-1); // Mirror horizontally

        stage.addActor(leaves[0]);
        stage.addActor(leaves[1]);

        return leaves;
    }

    public static void dispose() {
        if (leavesTexture != null) {
            leavesTexture.dispose();
            leavesTexture = null;
        }
    }
}
