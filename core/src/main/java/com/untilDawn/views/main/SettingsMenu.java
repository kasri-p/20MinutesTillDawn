package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.SettingsMenuController;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.UIHelper;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenu implements Screen {
    private final Color BUTTON_COLOR = Color.valueOf("EC2F7B");
    private Stage stage;
    private Skin skin;
    private Table mainTable;
    private SettingsMenuController controller;
    private Image[] leavesDecorations;
    private ScrollPane scrollPane;
    private Slider musicVolumeSlider;
    private SelectBox<String> musicSelectBox;
    private Label volumeValueLabel;

    private CheckBox sfxCheckBox;

    private Table keybindTable;
    private Map<String, TextButton> keybindButtons;

    private CheckBox autoReloadCheckBox;
    private CheckBox blackAndWhiteCheckBox;

    private TextButton backButton;
    private TextButton applyButton;

    private String currentEditingKeybind = null;

    private Array<String> availableMusicTracks;
    private Map<String, String> musicFiles;

    public SettingsMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createUI();

        loadSettings();

        this.controller = new SettingsMenuController(this);

        setupListeners();
    }

    private void createUI() {
        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        mainTable = new Table();

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        scrollPane = new ScrollPane(mainTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Label titleLabel = new Label("Settings", skin, "title");
        mainTable.add(titleLabel).colspan(2).pad(20).row();

        float screenWidth = Gdx.graphics.getWidth();
        float columnWidth = (screenWidth * 0.85f) / 2;

        Table leftColumn = new Table();
        Table rightColumn = new Table();

        createMusicControls(leftColumn);
        createSFXControls(leftColumn);
        createBonusFeatures(leftColumn);

        createKeyboardControls(rightColumn);

        mainTable.add(leftColumn).width(columnWidth).top().padRight(20);
        mainTable.add(rightColumn).width(columnWidth).top().padLeft(20).row();

        createNavigationButtons();

        mainTable.left().top();

        rootTable.add(scrollPane).width(screenWidth * 0.9f).height(Gdx.graphics.getHeight() * 0.9f);

        stage.addActor(rootTable);

        for (Image leaf : leavesDecorations) {
            leaf.toBack();
        }

        rootTable.toFront();
    }

    private void createMusicControls(Table targetTable) {
        setupMusicTracks();

        Label musicTitle = new Label("Audio Settings", skin);
        musicTitle.setColor(Color.YELLOW);
        targetTable.add(musicTitle).colspan(2).pad(15).row();

        Label musicVolumeLabel = new Label("Music Volume:", skin);
        musicVolumeSlider = new Slider(0, 1, 0.01f, false, skin);
        musicVolumeSlider.setColor(BUTTON_COLOR);
        volumeValueLabel = new Label("50%", skin);

        Table volumeTable = new Table();
        volumeTable.add(musicVolumeSlider).width(220).pad(5);
        volumeTable.add(volumeValueLabel).width(50).pad(5).left();

        targetTable.add(musicVolumeLabel).right().pad(10);
        targetTable.add(volumeTable).left().pad(10).row();

        Label musicSelectLabel = new Label("Music Track:", skin);
        musicSelectBox = new SelectBox<>(skin);
        musicSelectBox.setItems(availableMusicTracks);
        musicSelectBox.setColor(BUTTON_COLOR);

        targetTable.add(musicSelectLabel).right().pad(10);
        targetTable.add(musicSelectBox).left().width(220).pad(10).row();
    }

    private void setupMusicTracks() {
        availableMusicTracks = new Array<>();
        musicFiles = new HashMap<>();

        availableMusicTracks.add("Pretty Dungeon");
        musicFiles.put("Pretty Dungeon", "sounds/musics/PrettyDungeon.wav");

        FileHandle dir = Gdx.files.internal("sounds/musics");
        if (dir.exists() && dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                if (file.extension().equals("wav") || file.extension().equals("mp3")) {
                    String trackName = file.nameWithoutExtension();
                    if (!trackName.equals("PrettyDungeon")) {
                        String formattedName = formatTrackName(trackName);
                        availableMusicTracks.add(formattedName);
                        musicFiles.put(formattedName, file.path());
                    }
                }
            }
        }
    }

    private String formatTrackName(String trackName) {
        String formatted = trackName.replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("_", " ");

        StringBuilder result = new StringBuilder();
        for (String word : formatted.split("\\s")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }

        return result.toString().trim();
    }

    private void createSFXControls(Table targetTable) {
        Label sfxLabel = new Label("Sound Effects:", skin);
        sfxCheckBox = new CheckBox("Enabled", skin);
        sfxCheckBox.setColor(BUTTON_COLOR);

        targetTable.add(sfxLabel).right().pad(10);
        targetTable.add(sfxCheckBox).left().pad(10).row();

        targetTable.add().height(15).row();
    }

    private void createKeyboardControls(Table targetTable) {
        Label keybindTitle = new Label("Keyboard Controls", skin);
        keybindTitle.setColor(Color.YELLOW);
        targetTable.add(keybindTitle).colspan(2).pad(15).row();

        keybindTable = new Table();
        keybindButtons = new HashMap<>();

        addKeybindRow("Move Up", "W");
        addKeybindRow("Move Down", "S");
        addKeybindRow("Move Left", "A");
        addKeybindRow("Move Right", "D");
        addKeybindRow("Auto Shoot", "SPACE");
        addKeybindRow("Reload", "R");
        addKeybindRow("Pause", "ESC");

        targetTable.add(keybindTable).colspan(2).pad(10).row();
    }

    private void addKeybindRow(String action, String defaultKey) {
        Label actionLabel = new Label(action + ":", skin);
        TextButton keyButton = new TextButton(defaultKey, skin);
        keyButton.setColor(BUTTON_COLOR);

        actionLabel.setAlignment(Align.right);

        keybindButtons.put(action, keyButton);

        keyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startKeyBindCapture(action);
            }
        });

        Table rowTable = new Table();
        rowTable.add(actionLabel).width(150).right().padRight(15);
        rowTable.add(keyButton).width(100).left();

        keybindTable.add(rowTable).fillX().padBottom(10).row();
    }

    private void createBonusFeatures(Table targetTable) {
        Label bonusTitle = new Label("Advanced Options", skin);
        bonusTitle.setColor(Color.YELLOW);
        targetTable.add(bonusTitle).colspan(2).pad(15).row();

        Label autoReloadLabel = new Label("Auto-Reload:", skin);
        autoReloadCheckBox = new CheckBox("Enabled", skin);
        autoReloadCheckBox.setColor(BUTTON_COLOR);

        Table autoReloadTable = new Table();
        autoReloadTable.add(autoReloadLabel).width(150).right().pad(20);
        autoReloadTable.add(autoReloadCheckBox).left().pad(50).row();

        targetTable.add(autoReloadTable).colspan(2).padBottom(10).row();

        Label blackAndWhiteLabel = new Label("Black & White:", skin);
        blackAndWhiteCheckBox = new CheckBox("Enabled", skin);
        blackAndWhiteCheckBox.setColor(BUTTON_COLOR);

        Table bwTable = new Table();
        bwTable.add(blackAndWhiteLabel).width(150).right().pad(20);
        bwTable.add(blackAndWhiteCheckBox).left().pad(50);

        targetTable.add(bwTable).colspan(2).padBottom(15).row();
    }

    private void createNavigationButtons() {
        Table buttonTable = new Table();

        backButton = new TextButton("Back", skin);
        backButton.setColor(BUTTON_COLOR);

        applyButton = new TextButton("Apply Changes", skin);
        applyButton.setColor(BUTTON_COLOR);

        buttonTable.add(backButton).width(150).padRight(20).pad(10);
        buttonTable.add(applyButton).width(150).padLeft(20).pad(10);

        mainTable.add(buttonTable).colspan(2).pad(20).padBottom(40).row();
    }

    private void loadSettings() {
        float musicVolume = App.getMusicVolume();
        musicVolumeSlider.setValue(musicVolume);
        volumeValueLabel.setText(Math.round(musicVolume * 100) + "%");

        String currentTrack = App.getCurrentMusicTrack();
        if (currentTrack != null && availableMusicTracks.contains(currentTrack, false)) {
            musicSelectBox.setSelected(currentTrack);
        }

        sfxCheckBox.setChecked(App.isSFX());

        Map<String, String> keybinds = App.getKeybinds();
        if (keybinds != null) {
            for (Map.Entry<String, String> entry : keybinds.entrySet()) {
                if (keybindButtons.containsKey(entry.getKey())) {
                    keybindButtons.get(entry.getKey()).setText(entry.getValue());
                }
            }
        }

        autoReloadCheckBox.setChecked(App.isAutoReloadEnabled());
        blackAndWhiteCheckBox.setChecked(App.isBlackAndWhiteEnabled());
    }

    public void startKeyBindCapture(String action) {
        currentEditingKeybind = action;
        keybindButtons.get(action).setText("Press key...");

        keybindButtons.get(action).setColor(Color.YELLOW);
    }

    public void setKeybind(String action, String key) {
        if (keybindButtons.containsKey(action)) {
            keybindButtons.get(action).setText(key);
            keybindButtons.get(action).setColor(BUTTON_COLOR);
        }
        currentEditingKeybind = null;
    }

    public void cancelKeyBindCapture() {
        if (currentEditingKeybind != null && keybindButtons.containsKey(currentEditingKeybind)) {
            String previousKey = App.getKeybinds().getOrDefault(currentEditingKeybind, "UNDEFINED");
            keybindButtons.get(currentEditingKeybind).setText(previousKey);
            keybindButtons.get(currentEditingKeybind).setColor(BUTTON_COLOR);
            currentEditingKeybind = null;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        if (currentEditingKeybind != null && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ANY_KEY)) {
            int keyCode = -1;

            for (int i = 0; i < 256; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    keyCode = i;
                    break;
                }
            }

            if (keyCode != -1) {
                String keyName = com.badlogic.gdx.Input.Keys.toString(keyCode);
                if (com.badlogic.gdx.utils.SharedLibraryLoader.isMac) {
                    if (keyName.equals("META")) keyName = "CMD";
                    else if (keyName.equals("CONTROL")) keyName = "CTRL";
                    else if (keyName.equals("ALT")) keyName = "OPT";
                }

                if (controller != null) {
                    controller.onKeybindChanged(currentEditingKeybind, keyName);
                } else {
                    setKeybind(currentEditingKeybind, keyName);
                }
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        if (leavesDecorations != null) {
            for (Image leaf : leavesDecorations) {
                leaf.remove();
            }
            leavesDecorations = UIHelper.addLeavesDecoration(stage);

            for (Image leaf : leavesDecorations) {
                leaf.toBack();
            }
        }

        scrollPane.setWidth(width * 0.9f);
        scrollPane.setHeight(height * 0.9f);
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

    public Slider getMusicVolumeSlider() {
        return musicVolumeSlider;
    }

    public SelectBox<String> getMusicSelectBox() {
        return musicSelectBox;
    }

    public CheckBox getSfxCheckBox() {
        return sfxCheckBox;
    }

    public Map<String, TextButton> getKeybindButtons() {
        return keybindButtons;
    }

    public CheckBox getAutoReloadCheckBox() {
        return autoReloadCheckBox;
    }

    public CheckBox getBlackAndWhiteCheckBox() {
        return blackAndWhiteCheckBox;
    }

    private void setupListeners() {
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = musicVolumeSlider.getValue();
                volumeValueLabel.setText(Math.round(value * 100) + "%");
                controller.onVolumeChanged(value);
            }
        });

        musicSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = musicSelectBox.getSelected();
                controller.onMusicTrackChanged(selected, musicFiles.get(selected));
            }
        });

        sfxCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onSFXToggled(sfxCheckBox.isChecked());
            }
        });

        for (final Map.Entry<String, TextButton> entry : keybindButtons.entrySet()) {
            entry.getValue().getListeners().clear();

            entry.getValue().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.playClick();
                    startKeyBindCapture(entry.getKey());
                }
            });
        }

        autoReloadCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });

        blackAndWhiteCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                controller.onBackClicked();
            }
        });

        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                controller.onApplyClicked();
            }
        });
    }

    public Stage getStage() {
        return stage;
    }
}
