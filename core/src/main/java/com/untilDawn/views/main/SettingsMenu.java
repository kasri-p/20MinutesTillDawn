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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.untilDawn.controllers.SettingsMenuController;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.UIHelper;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenu implements Screen {
    private Stage stage;
    private Skin skin;
    private Table mainTable;
    private SettingsMenuController controller;
    private Image[] leavesDecorations;

    // Music settings
    private Slider musicVolumeSlider;
    private SelectBox<String> musicSelectBox;
    private Label volumeValueLabel;

    // SFX settings
    private CheckBox sfxCheckBox;

    // Keyboard controls
    private Table keybindTable;
    private Map<String, TextButton> keybindButtons;

    // Bonus features
    private CheckBox autoReloadCheckBox;
    private CheckBox blackAndWhiteCheckBox;

    // Navigation buttons
    private TextButton backButton;
    private TextButton applyButton;

    // Current editing keybind
    private String currentEditingKeybind = null;

    // Available music tracks
    private Array<String> availableMusicTracks;
    private Map<String, String> musicFiles;

    public SettingsMenu(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.controller = new SettingsMenuController(this);

        // Initialize controls and table
        createUI();

        // Load settings from App/storage
        loadSettings();
    }

    private void createUI() {
        // Set up main table
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(30);

        // Add title
        Label titleLabel = new Label("Settings", skin, "title");
        mainTable.add(titleLabel).colspan(2).pad(20).row();

        // Create music controls
        createMusicControls();

        // Create SFX controls
        createSFXControls();

        // Create keyboard control settings
        createKeyboardControls();

        // Create bonus features
        createBonusFeatures();

        // Add navigation buttons
        createNavigationButtons();

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    private void createMusicControls() {
        // Music volume label and slider
        Label musicVolumeLabel = new Label("Music Volume:", skin);
        musicVolumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeValueLabel = new Label("50%", skin);

        Table volumeTable = new Table();
        volumeTable.add(musicVolumeSlider).width(300).pad(5);
        volumeTable.add(volumeValueLabel).width(60).pad(5);

        mainTable.add(musicVolumeLabel).right().pad(10);
        mainTable.add(volumeTable).left().pad(10).row();

        // Set up music tracks
        setupMusicTracks();

        // Music selection
        Label musicSelectLabel = new Label("Music Track:", skin);
        musicSelectBox = new SelectBox<>(skin);
        musicSelectBox.setItems(availableMusicTracks);

        mainTable.add(musicSelectLabel).right().pad(10);
        mainTable.add(musicSelectBox).left().width(300).pad(10).row();

        // Add listeners
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
    }

    private void setupMusicTracks() {
        availableMusicTracks = new Array<>();
        musicFiles = new HashMap<>();

        // Add the default music
        availableMusicTracks.add("Pretty Dungeon");
        musicFiles.put("Pretty Dungeon", "sounds/musics/PrettyDungeon.wav");

        // Scan the music directory for more tracks
        FileHandle dir = Gdx.files.internal("sounds/musics");
        if (dir.exists() && dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                if (file.extension().equals("wav") || file.extension().equals("mp3")) {
                    String trackName = file.nameWithoutExtension();
                    if (!trackName.equals("PrettyDungeon")) { // Skip the default one as we already added it
                        String formattedName = formatTrackName(trackName);
                        availableMusicTracks.add(formattedName);
                        musicFiles.put(formattedName, file.path());
                    }
                }
            }
        }
    }

    private String formatTrackName(String trackName) {
        // Convert camelCase or snake_case to Title Case
        String formatted = trackName.replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("_", " ");

        // Capitalize first letter of each word
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

    private void createSFXControls() {
        // SFX checkbox
        Label sfxLabel = new Label("Sound Effects:", skin);
        sfxCheckBox = new CheckBox("Enabled", skin);

        mainTable.add(sfxLabel).right().pad(10);
        mainTable.add(sfxCheckBox).left().pad(10).row();

        // Add listener
        sfxCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onSFXToggled(sfxCheckBox.isChecked());
            }
        });
    }

    private void createKeyboardControls() {
        // Keyboard controls section title
        Label keybindTitle = new Label("Keyboard Controls", skin);
        keybindTitle.setColor(Color.YELLOW);
        mainTable.add(keybindTitle).colspan(2).pad(20).row();

        // Create keybind table
        keybindTable = new Table();
        keybindButtons = new HashMap<>();

        // Add keybind rows
        addKeybindRow("Move Up", "W");
        addKeybindRow("Move Down", "S");
        addKeybindRow("Move Left", "A");
        addKeybindRow("Move Right", "D");
        addKeybindRow("Shoot", "SPACE");
        addKeybindRow("Reload", "R");
        addKeybindRow("Pause", "ESC");

        mainTable.add(keybindTable).colspan(2).pad(10).row();
    }

    private void addKeybindRow(String action, String defaultKey) {
        Label actionLabel = new Label(action + ":", skin);
        TextButton keyButton = new TextButton(defaultKey, skin);

        // Store in map for later access
        keybindButtons.put(action, keyButton);

        // Add button listener
        keyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                startKeyBindCapture(action);
            }
        });

        keybindTable.add(actionLabel).right().padRight(15).padBottom(5);
        keybindTable.add(keyButton).width(100).padBottom(5).row();
    }

    private void createBonusFeatures() {
        // Bonus features section title
        Label bonusTitle = new Label("Advanced Options", skin);
        bonusTitle.setColor(Color.YELLOW);
        mainTable.add(bonusTitle).colspan(2).pad(20).row();

        // Auto-reload checkbox
        Label autoReloadLabel = new Label("Auto-Reload:", skin);
        autoReloadCheckBox = new CheckBox("Enabled", skin);

        mainTable.add(autoReloadLabel).right().pad(10);
        mainTable.add(autoReloadCheckBox).left().pad(10).row();

        // Black and white mode checkbox
        Label blackAndWhiteLabel = new Label("Black & White Mode:", skin);
        blackAndWhiteCheckBox = new CheckBox("Enabled", skin);

        mainTable.add(blackAndWhiteLabel).right().pad(10);
        mainTable.add(blackAndWhiteCheckBox).left().pad(10).row();

        // Add listeners
        autoReloadCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onAutoReloadToggled(autoReloadCheckBox.isChecked());
            }
        });

        blackAndWhiteCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.onBlackAndWhiteToggled(blackAndWhiteCheckBox.isChecked());
            }
        });
    }

    private void createNavigationButtons() {
        Table buttonTable = new Table();

        // Back button
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                controller.onBackClicked();
            }
        });

        // Apply button
        applyButton = new TextButton("Apply Changes", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.playClick();
                controller.onApplyClicked();
            }
        });

        buttonTable.add(backButton).width(150).pad(10);
        buttonTable.add(applyButton).width(150).pad(10);

        mainTable.add(buttonTable).colspan(2).pad(20).row();
    }

    private void loadSettings() {
        // Set music volume from App settings
        float musicVolume = App.getMusicVolume();
        musicVolumeSlider.setValue(musicVolume);
        volumeValueLabel.setText(Math.round(musicVolume * 100) + "%");

        // Set selected music track
        String currentTrack = App.getCurrentMusicTrack();
        if (currentTrack != null && availableMusicTracks.contains(currentTrack, false)) {
            musicSelectBox.setSelected(currentTrack);
        }

        // Set SFX enabled/disabled
        sfxCheckBox.setChecked(App.isSFX());

        // Set keybinds from App settings
        Map<String, String> keybinds = App.getKeybinds();
        if (keybinds != null) {
            for (Map.Entry<String, String> entry : keybinds.entrySet()) {
                if (keybindButtons.containsKey(entry.getKey())) {
                    keybindButtons.get(entry.getKey()).setText(entry.getValue());
                }
            }
        }

        // Set bonus features
        autoReloadCheckBox.setChecked(App.isAutoReloadEnabled());
        blackAndWhiteCheckBox.setChecked(App.isBlackAndWhiteEnabled());
    }

    public void startKeyBindCapture(String action) {
        currentEditingKeybind = action;
        keybindButtons.get(action).setText("Press a key...");

        // Highlight the button being edited
        keybindButtons.get(action).setColor(Color.YELLOW);
    }

    public void setKeybind(String action, String key) {
        if (keybindButtons.containsKey(action)) {
            keybindButtons.get(action).setText(key);
            keybindButtons.get(action).setColor(Color.WHITE);
        }
        currentEditingKeybind = null;
    }

    public void cancelKeyBindCapture() {
        if (currentEditingKeybind != null && keybindButtons.containsKey(currentEditingKeybind)) {
            // Reset to previous key
            String previousKey = App.getKeybinds().getOrDefault(currentEditingKeybind, "UNDEFINED");
            keybindButtons.get(currentEditingKeybind).setText(previousKey);
            keybindButtons.get(currentEditingKeybind).setColor(Color.WHITE);
            currentEditingKeybind = null;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Add leaves decoration
        leavesDecorations = UIHelper.addLeavesDecoration(stage);

        // Make sure leaves are at the back
        for (Image leaf : leavesDecorations) {
            leaf.toBack();
        }

        // Bring the main table to front
        mainTable.toFront();
    }

    @Override
    public void render(float delta) {
        UIHelper.clearScreenWithBackgroundColor();

        // Process key input for keybinding
        if (currentEditingKeybind != null && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ANY_KEY)) {
            int keyCode = -1;

            // Find which key was pressed
            for (int i = 0; i < 256; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    keyCode = i;
                    break;
                }
            }

            if (keyCode != -1) {
                String keyName = com.badlogic.gdx.Input.Keys.toString(keyCode);
                controller.onKeybindChanged(currentEditingKeybind, keyName);
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        // Reposition leaves decoration
        if (leavesDecorations != null) {
            for (Image leaf : leavesDecorations) {
                leaf.remove();
            }
            leavesDecorations = UIHelper.addLeavesDecoration(stage);

            // Make sure leaves are at the back
            for (Image leaf : leavesDecorations) {
                leaf.toBack();
            }
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

    // Getters for controller access
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

    public Stage getStage() {
        return stage;
    }
}
