package com.untilDawn.controllers;

import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Game;
import com.untilDawn.models.Player;
import com.untilDawn.models.User;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.enums.Weapons;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.main.GameView;
import com.untilDawn.views.main.PreGameMenu;

public class PreGameMeuController {
    private PreGameMenu view;

    private Characters selectedCharacter;
    private Weapons selectedWeapon;
    private int selectedTime = 5;
    private int selectedDifficulty = 1;

    public PreGameMeuController() {
    }

    public void setView(PreGameMenu view) {
        this.view = view;
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    public void onCharacterSelected(String characterName) {
        playClick();
        for (Characters character : Characters.values()) {
            if (character.getName().equals(characterName)) {
                selectedCharacter = character;
                System.out.println("Selected character: " + characterName);
                break;
            }
        }
    }

    public void onWeaponSelected(String weaponName) {
        playClick();
        for (Weapons weapon : Weapons.values()) {
            if (weapon.getName().equals(weaponName)) {
                selectedWeapon = weapon;
                System.out.println("Selected weapon: " + weaponName);
                break;
            }
        }
    }

    public void onTimeSelected(int minutes) {
        playClick();
        selectedTime = minutes;
        System.out.println("Selected time: " + minutes + " minutes");
    }

    public void onDifficultySelected(int level) {
        playClick();
        selectedDifficulty = level;
        System.out.println("Selected difficulty: " + level);
    }

    public void startGame() {
        playClick();

        if (selectedCharacter == null) {
            System.out.println("No character selected!");
            return;
        }

        if (selectedWeapon == null) {
            System.out.println("No weapon selected!");
            return;
        }

        Player player = new Player(selectedCharacter);

        Game game = new Game();
        game.setPlayer(player);
        game.setSelectedWeapon(selectedWeapon);

        User currentUser = App.getLoggedInUser();
        if (currentUser != null) {
            App.addGame(game, currentUser);
        }
        App.setGame(game);

        System.out.println("Starting game with character: " + selectedCharacter.getName() +
            ", weapon: " + selectedWeapon.getName() +
            ", time: " + selectedTime + " minutes, difficulty: " + selectedDifficulty);
        showGameStartMessage();
    }

    private void showGameStartMessage() {
        Main.getMain().setScreen(new GameView(GameAssetManager.getGameAssetManager().getSkin()));
    }

    public Characters getSelectedCharacter() {
        return selectedCharacter;
    }

    public Weapons getSelectedWeapon() {
        return selectedWeapon;
    }

    public int getSelectedTime() {
        return selectedTime;
    }

    public int getSelectedDifficulty() {
        return selectedDifficulty;
    }
}
