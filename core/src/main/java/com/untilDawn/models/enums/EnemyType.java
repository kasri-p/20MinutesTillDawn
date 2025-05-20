package com.untilDawn.models.enums;

public enum EnemyType {
    TREE("Tree", 1000, 0, 4, "Images/Enemies/Tree/tree0.png", false);

    private final String name;
    private final int health;
    private final float speed;
    private final int damage;
    private final String texturePath;
    private final boolean canMove;

    EnemyType(String name, int health, float speed, int damage, String texturePath, boolean canMove) {
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.damage = damage;
        this.texturePath = texturePath;
        this.canMove = canMove;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public boolean canMove() {
        return canMove;
    }
}
