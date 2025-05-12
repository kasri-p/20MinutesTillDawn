package com.untilDawn.models;


public class Game {
    private int score;
    private int level;
    private long duration;
    private Player player;

    public Game() {
        this.score = 0;
        this.level = 1;
        this.duration = 0;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Player getPlayer() {
        return player;
    }
}
