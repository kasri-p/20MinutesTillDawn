package com.untilDawn.models.enums;

public enum EnemyType {
    TREE("Tree", 1000, 0, "Images/Enemies/Tree/tree0.png", false, false),
    EYEBAT("EyeBat", 50, 2, "Images/Enemies/eyebat/eyebat0.png", true, true),
    TENTACLE("TentacleMonster", 25, 1, "Images/Enemies/tentaclemonster/tentaclemonster0.png", true, false),
    ;

    private final String name;
    private final int health;
    private final float speed;
    private final String texturePath;
    private final boolean canMove;
    private final boolean canShoot;

    EnemyType(String name, int health, float speed, String texturePath, boolean canMove, boolean canShoot) {
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.texturePath = texturePath;
        this.canMove = canMove;
        this.canShoot = canShoot;
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

    public String getTexturePath() {
        return texturePath;
    }

    public boolean canMove() {
        return canMove;
    }

    public boolean canShoot() {
        return canShoot;
    }
}
