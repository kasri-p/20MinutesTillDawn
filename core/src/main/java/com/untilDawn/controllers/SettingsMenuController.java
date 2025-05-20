package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.main.MainMenu;
import com.untilDawn.views.main.SettingsMenu;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenuController {
    private final SettingsMenu view;
    private Music currentMusic;
    private String currentMusicPath;

    // Cache the original values for comparison
    private float originalVolume;
    private boolean originalSFX;
    private String originalMusicTrack;
    private Map<String, String> originalKeybinds;
    private boolean originalAutoReload;
    private boolean originalBlackAndWhite;

    // Cache the new values
    private Map<String, String> pendingKeybinds;

    public SettingsMenuController(SettingsMenu view) {
        this.view = view;
        this.currentMusic = Main.getMain().getMenuMusic();

        // Initialize caches
        cacheOriginalSettings();
        pendingKeybinds = new HashMap<>(originalKeybinds);
    }

    private void cacheOriginalSettings() {
        originalVolume = App.getMusicVolume();
        originalSFX = App.isSFX();
        originalMusicTrack = App.getCurrentMusicTrack();
        originalKeybinds = new HashMap<>(App.getKeybinds());
        originalAutoReload = App.isAutoReloadEnabled();
        originalBlackAndWhite = App.isBlackAndWhiteEnabled();
    }

    public void onVolumeChanged(float volume) {
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }

    public void onMusicTrackChanged(String trackName, String filePath) {
        if (filePath == null || filePath.equals(currentMusicPath)) {
            return;
        }

        // Stop current music if playing
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        // Load and play new music
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        currentMusicPath = filePath;

        // Apply current volume and settings
        currentMusic.setVolume(view.getMusicVolumeSlider().getValue());
        currentMusic.setLooping(true);
        currentMusic.play();
    }

    public void onSFXToggled(boolean enabled) {
        // This is applied immediately to preview the setting
        App.setSFX(enabled);

        // Play a test sound if enabled
        if (enabled) {
            playClick();
        }
    }

    public void onKeybindChanged(String action, String key) {
        // Store the pending keybind change
        pendingKeybinds.put(action, key);

        // Update UI
        view.setKeybind(action, key);
    }
    

    public void onBackClicked() {
        discardChanges();
        navigateToMainMenu();
    }

    public void onApplyClicked() {
        saveSettings();
        navigateToMainMenu();
    }

    private void saveSettings() {
        float volume = view.getMusicVolumeSlider().getValue();
        App.setMusicVolume(volume);

        String selectedTrack = view.getMusicSelectBox().getSelected();
        App.setCurrentMusicTrack(selectedTrack);

        boolean sfxEnabled = view.getSfxCheckBox().isChecked();
        App.setSFX(sfxEnabled);

        App.setKeybinds(pendingKeybinds);

        boolean autoReloadEnabled = view.getAutoReloadCheckBox().isChecked();
        App.setAutoReloadEnabled(autoReloadEnabled);

        boolean blackAndWhiteEnabled = view.getBlackAndWhiteCheckBox().isChecked();
        App.setBlackAndWhiteEnabled(blackAndWhiteEnabled);

        if (!originalMusicTrack.equals(selectedTrack)) {
            Main.getMain().setMenuMusic(currentMusic);
        }

        App.save();
    }

    private void discardChanges() {
        if (currentMusic != null) {
            currentMusic.setVolume(originalVolume);
        }

        if (!originalMusicTrack.equals(view.getMusicSelectBox().getSelected())) {
            if (currentMusic != null && currentMusic != Main.getMain().getMenuMusic()) {
                currentMusic.stop();
                currentMusic.dispose();
            }

            Main.getMain().getMenuMusic().play();
        }

        App.setSFX(originalSFX);
    }

    private void navigateToMainMenu() {
        MainMenu mainMenu = new MainMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(mainMenu);
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }
}
