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

    public void onAutoReloadToggled(boolean enabled) {
        // Will be applied when user clicks "Apply"
    }

    public void onBlackAndWhiteToggled(boolean enabled) {
        // Will be applied when user clicks "Apply"
    }

    public void onBackClicked() {
        // Discard changes and return to main menu
        discardChanges();
        navigateToMainMenu();
    }

    public void onApplyClicked() {
        // Save all settings
        saveSettings();
        navigateToMainMenu();
    }

    private void saveSettings() {
        // Save music volume
        float volume = view.getMusicVolumeSlider().getValue();
        App.setMusicVolume(volume);

        // Save current music track
        String selectedTrack = view.getMusicSelectBox().getSelected();
        App.setCurrentMusicTrack(selectedTrack);

        // Save SFX setting
        boolean sfxEnabled = view.getSfxCheckBox().isChecked();
        App.setSFX(sfxEnabled);

        // Save keybinds
        App.setKeybinds(pendingKeybinds);

        // Save bonus features
        boolean autoReloadEnabled = view.getAutoReloadCheckBox().isChecked();
        App.setAutoReloadEnabled(autoReloadEnabled);

        boolean blackAndWhiteEnabled = view.getBlackAndWhiteCheckBox().isChecked();
        App.setBlackAndWhiteEnabled(blackAndWhiteEnabled);

        // If the music changed and it's not the preview track, keep it playing
        if (!originalMusicTrack.equals(selectedTrack)) {
            Main.getMain().setMenuMusic(currentMusic);
        }

        // Persist settings
        App.save();
    }

    private void discardChanges() {
        // Restore original music volume
        if (currentMusic != null) {
            currentMusic.setVolume(originalVolume);
        }

        // Restore original music if changed
        if (!originalMusicTrack.equals(view.getMusicSelectBox().getSelected())) {
            // Stop the preview music
            if (currentMusic != null && currentMusic != Main.getMain().getMenuMusic()) {
                currentMusic.stop();
                currentMusic.dispose();
            }

            // Restore original menu music
            Main.getMain().getMenuMusic().play();
        }

        // Restore SFX setting
        App.setSFX(originalSFX);

        // Keybinds are only applied when "Apply" is clicked, so no need to restore
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
