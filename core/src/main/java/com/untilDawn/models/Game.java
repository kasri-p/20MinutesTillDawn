package com.untilDawn.models;

import com.untilDawn.models.enums.Weapons;

import java.util.ArrayList;

public class Game {
    private int score;
    private int level;
    private Player player;
    private Weapon selectedWeapon;
    private int difficulty;
    private int timeLimit; // in minutes
    private ArrayList<Enemy> enemies;
    private float gameTime; // current game time in seconds

    public Game(int timeLimit) {
        this.score = 0;
        this.level = 1;
        this.difficulty = 1;
        this.timeLimit = timeLimit;
        this.enemies = new ArrayList<>();
        this.gameTime = 0;
    }

    public Game() {
        this.score = 0;
        this.level = 1;
        this.difficulty = 1;
        this.timeLimit = 5;
        this.enemies = new ArrayList<>();
        this.gameTime = 0;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Weapon getSelectedWeapon() {
        return selectedWeapon;
    }

    public void setSelectedWeapon(Weapons selectedWeapon) {
        this.selectedWeapon = new Weapon(selectedWeapon);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    public float getGameTime() {
        return gameTime;
    }

    public void setGameTime(float gameTime) {
        this.gameTime = gameTime;
    }
}
