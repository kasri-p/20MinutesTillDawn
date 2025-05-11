package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;


public class UIHelper {
    // The background color to use for all menus
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.2f, 1f); // Dark blue

    // Cached textures to avoid reloading
    private static Texture leavesTexture;

    /**
     * Clears the screen with the standard background color
     */
    public static void clearScreenWithBackgroundColor() {
        ScreenUtils.clear(BACKGROUND_COLOR);
    }

    /**
     * Adds the leaves decoration to both sides of the stage
     * The right leaves will be mirrored
     *
     * @param stage The stage to add the leaves to
     * @return Array containing the left and right leaf images
     */
    public static Image[] addLeavesDecoration(Stage stage) {
        // Load the texture if not already loaded
        if (leavesTexture == null) {
            leavesTexture = new Texture(Gdx.files.internal("images/TitleLeaves.png"));
        }

        Image[] leaves = new Image[2];

        // Left leaves
        leaves[0] = new Image(leavesTexture);

        float scale = stage.getHeight() / leavesTexture.getHeight();
        float scaledWidth = leavesTexture.getWidth() * scale;

        leaves[0].setSize(scaledWidth, stage.getHeight());
        leaves[0].setPosition(0, 0);

        // Right leaves (mirrored)
        leaves[1] = new Image(leavesTexture);
        leaves[1].setSize(scaledWidth, stage.getHeight());
        leaves[1].setPosition(stage.getWidth(), 0);
        leaves[1].setScaleX(-1); // Mirror horizontally

        // Add to stage
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
