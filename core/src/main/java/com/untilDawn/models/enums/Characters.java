package com.untilDawn.models.enums;


public enum Characters {
    Shana("Shana", 4, 4),
    Diamond("Diamond", 1, 7),
    Scarlett("Scarlett", 5, 3),
    Lilith("Lilith", 5, 3),
    Dasher("Dasher", 10, 2),
    Abby("Abby", 3, 4),
    Raven("Raven", 6, 2),
    Hastur("Hastur", 7, 6);

    private final String name;
    private final int speed;
    private final int hp;

    Characters(String name, int speed, int hp) {
        this.name = name;
        this.speed = speed;
        this.hp = hp;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public String toString() {
        return " ( Speed: " + speed + " | HP: " + hp + " )";
    }

    public String getImagePath() {
        return "Images/characters/" + name.toLowerCase() + "/portrait.png";
    }
}
