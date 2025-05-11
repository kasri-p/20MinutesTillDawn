package com.untilDawn.models.enums;


public enum Abilities {
    VITALITY("Vitality", "Increases maximum HP by one unit", AbilityType.PASSIVE),
    DAMAGER("Damager", "Increases weapon damage by 25% for 10 seconds", AbilityType.ACTIVE),
    PROCREASE("Procrease", "Increases weapon Projectile by one unit", AbilityType.PASSIVE),
    AMOCREASE("Amocrease", "Increases maximum weapon ammunition by 5 units", AbilityType.PASSIVE),
    SPEEDY("Speedy", "Doubles player movement speed for 10 seconds", AbilityType.ACTIVE);

    private final String name;
    private final String description;
    private final AbilityType type;

    private boolean active = false;
    private float duration = 0;
    private float cooldown = 0;

    Abilities(String name, String description, AbilityType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public AbilityType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public float getDuration() {
        return duration;
    }

    public float getCooldown() {
        return cooldown;
    }

    public enum AbilityType {
        PASSIVE,
        ACTIVE
    }
}
