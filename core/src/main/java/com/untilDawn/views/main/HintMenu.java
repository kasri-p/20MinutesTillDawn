package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.models.App;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.utils.CheatCodeManager;

import java.util.Map;

public class HintMenu implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Runnable onBackPressed;

    public HintMenu(Skin skin, Runnable onBackPressed) {
        this.skin = skin;
        this.onBackPressed = onBackPressed;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        setupLayout();
    }

    private void setupLayout() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center().pad(20);

        Table contentTable = new Table();
        contentTable.center().pad(10).defaults().width(600).padBottom(20);

        // --- Hero Hints Section ---
        Label heroTitle = createWrappedLabel("Hero Hints:");
        contentTable.add(heroTitle).row();

        Table heroesTable = new Table().left().top();
        for (Characters character : Characters.values()) {
            Table row = new Table();

            Texture charTexture = new Texture(Gdx.files.internal(character.getImagePath()));
            Image charImage = new Image(charTexture);
            charImage.setSize(48, 48);

            Label nameLabel = new Label(character.name(), skin);
            nameLabel.setColor(Color.CYAN);

            Label toStringLabel = new Label(character.toString(), skin);
            toStringLabel.setColor(Color.WHITE);

            // Add image, then name, then toString, all in one row
            row.add(charImage).size(48).padRight(10);
            row.add(nameLabel).padRight(10);
            row.add(toStringLabel).left().expandX();

            heroesTable.add(row).left().row();
        }
        contentTable.add(heroesTable).row();

        // --- Cheat Codes Section ---
        Label cheatCodesTitle = createWrappedLabel("Cheat Codes:");
        contentTable.add(cheatCodesTitle).row();

        Table cheatCodesTable = new Table().left().top();
        for (CheatCodeManager.CheatCode cheatCode : CheatCodeManager.getInstance().getAllCheatCodes().values()) {
            Label codeLabel = new Label(cheatCode.getCode(), skin);
            codeLabel.setColor(Color.ORANGE);

            Label descLabel = new Label(cheatCode.getDescription(), skin);
            descLabel.setWrap(true);
            descLabel.setColor(Color.LIGHT_GRAY);

            Container<Label> descContainer = new Container<>(descLabel);
            descContainer.width(550).fill();

            Table row = new Table();
            row.add(codeLabel).width(150).left().padRight(25);
            row.add(descContainer).left().expandX();

            cheatCodesTable.add(row).left().row();
        }
        contentTable.add(cheatCodesTable).row();

        // --- Abilities Section ---
        Label abilitiesTitle = createWrappedLabel("Abilities:");
        contentTable.add(abilitiesTitle).row();

        Table abilitiesTable = new Table().left().top();
        for (Abilities ability : Abilities.values()) {
            Table row = new Table();

            Texture texture = new Texture(Gdx.files.internal(ability.getImagePath() + ".png"));
            Image image = new Image(texture);
            image.setSize(48, 48);

            Label nameLabel = new Label(ability.name(), skin);
            nameLabel.setColor(Color.YELLOW);

            Label descLabel = new Label(ability.getDescription(), skin);
            descLabel.setWrap(true);
            descLabel.setColor(Color.LIGHT_GRAY);

            Table textTable = new Table();
            textTable.add(nameLabel).left().row();
            textTable.add(descLabel).width(500).left().row();

            row.add(image).size(48).padRight(10);
            row.add(textTable).left().expandX();

            abilitiesTable.add(row).left().row();
        }
        contentTable.add(abilitiesTable).row();

        // --- Key Bindings Section ---
        Label keyBindingsTitle = createWrappedLabel("Key Bindings:");
        contentTable.add(keyBindingsTitle).row();

        Table keyBindingsTable = new Table().left().top();

        Map<String, String> keyBinds = App.getKeybinds();

        for (var entry : keyBinds.entrySet()) {
            Label actionLabel = new Label(entry.getKey(), skin);
            actionLabel.setColor(Color.GREEN);

            Label keyLabel = new Label(entry.getValue(), skin);
            keyLabel.setColor(Color.LIGHT_GRAY);

            Table row = new Table();
            row.add(actionLabel).width(200).left().padRight(20);
            row.add(keyLabel).left();

            keyBindingsTable.add(row).left().row();
        }
        contentTable.add(keyBindingsTable).row();

        // --- ScrollPane ---
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);

        // --- Back Button ---
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                onBackPressed.run();
                return true;
            }
            return false;
        });

        rootTable.add(scrollPane).expand().fill().row();
        rootTable.add(backButton).padTop(10).center();

        stage.addActor(rootTable);
    }

    private Label createWrappedLabel(String text) {
        Label label = new Label(text, skin);
        label.setWrap(true);
        label.setColor(Color.WHITE);
        label.setAlignment(Align.center);
        return label;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
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
        stage.dispose();
    }
}
