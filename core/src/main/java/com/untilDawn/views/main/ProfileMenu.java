package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.ProfileMenuController;
import com.untilDawn.models.enums.Language;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.UIHelper;

public class ProfileMenu implements Screen {
    private final TextButton changeUsernameButton;
    private final TextButton changePasswordButton;
    private final TextButton deleteAccountButton;
    private final TextButton changeAvatarButton;

    public Table table;
    private Stage stage;
    private Image[] leavesDecorations;

    private Label title;

    public ProfileMenu(Skin skin) {
        title = new Label(Language.Profile.getText(), skin, "title");
        TextButton.TextButtonStyle textOnlyStyle = new TextButton.TextButtonStyle();
        textOnlyStyle.font = GameAssetManager.getGameAssetManager().getChevyRayFont();
        textOnlyStyle.fontColor = new Color(Color.SALMON);
        textOnlyStyle.overFontColor = new Color(Color.SALMON).mul(0.7f);
        textOnlyStyle.downFontColor = new Color(Color.SALMON).mul(0.5f);

        this.changeUsernameButton = new TextButton(Language.ChangeUsername.getText(), textOnlyStyle);
        this.changePasswordButton = new TextButton(Language.ChangePassword.getText(), textOnlyStyle);
        this.deleteAccountButton = new TextButton(Language.DeleteAccount.getText(), textOnlyStyle);
        this.changeAvatarButton = new TextButton(Language.ChangeAvatar.getText(), textOnlyStyle);

        ProfileMenuController controller = new ProfileMenuController(this);
        this.table = new Table();
    }


    @Override
    public void show() {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        leavesDecorations = UIHelper.addLeavesDecoration(stage);
        table.add(title).colspan(2).center().row();
        table.setFillParent(true);
        table.top();
        table.row().pad(100, 0, 20, 0);
        table.add(changeUsernameButton).colspan(2).center();
        table.row().pad(20, 0, 20, 0);
        table.add(changePasswordButton).colspan(2).center();
        table.row().pad(20, 0, 20, 0);
        table.add(deleteAccountButton).colspan(2).center();
        table.row().pad(20, 0, 20, 0);
        table.add(changeAvatarButton).colspan(2).center();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

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
    }


    public Stage getStage() {
        return stage;
    }

    public TextButton getChangeUsernameButton() {
        return changeUsernameButton;
    }

    public TextButton getChangePasswordButton() {
        return changePasswordButton;
    }

    public TextButton getDeleteAccountButton() {
        return deleteAccountButton;
    }

    public TextButton getChangeAvatarButton() {
        return changeAvatarButton;
    }
}
