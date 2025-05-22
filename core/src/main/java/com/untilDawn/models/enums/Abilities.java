package com.untilDawn.models.enums;

public enum Abilities {
    VITALITY("Vitality", "Increases maximum HP by one unit, providing more survivability in combat", AbilityType.PASSIVE, "💙", 0f, 0f, "Images/Abilities/Vitality"),
    DAMAGER("Damager", "Increases weapon damage by 25% for 10 seconds, dealing devastating blows to enemies", AbilityType.ACTIVE, "⚔️", 10f, 30f, "Images/Abilities/AmoDamage"),
    PROCREASE("Procrease", "Increases weapon projectile count by one unit, allowing for wider area coverage", AbilityType.PASSIVE, "🎯", 0f, 0f, "Images/Abilities/doubleShot"),
    AMOCREASE("Amocrease", "Increases maximum weapon ammunition by 5 units, reducing reload frequency", AbilityType.PASSIVE, "🔫", 0f, 0f, "Images/Abilities/Amo"),
    SPEEDY("Speedy", "Doubles player movement speed for 10 seconds, enabling quick escapes and positioning", AbilityType.ACTIVE, "💨", 10f, 25f, "Images/Abilities/Speedy"),
    REGENERATION("Regeneration", "Slowly regenerates health over time, providing continuous healing", AbilityType.PASSIVE, "💚", 0f, 0f, "Images/Abilities/Regeneration"),
    SHIELD("Shield", "Provides temporary invincibility for 3 seconds when activated", AbilityType.ACTIVE, "🛡️", 3f, 45f, "Images/Abilities/HolyShield"),
    MULTISHOT("Multishot", "Fire 3 bullets in a spread pattern for 15 seconds", AbilityType.ACTIVE, "🌟", 15f, 20f, "Images/Abilities/MultiShot");

    private final String name;
    private final String description;
    private final AbilityType type;
    private final String icon;
    private final float duration;
    private final float cooldown;
    private final String imagePath;

    private boolean active = false;
    private float remainingDuration = 0;
    private float remainingCooldown = 0;

    Abilities(String name, String description, AbilityType type, String icon, float duration, float cooldown, String imagePath) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.icon = icon;
        this.duration = duration;
        this.cooldown = cooldown;
        this.imagePath = imagePath;
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

    public String getIcon() {
        return icon;
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

    public float getRemainingDuration() {
        return remainingDuration;
    }

    public float getRemainingCooldown() {
        return remainingCooldown;
    }

    public void activate() {
        if (type == AbilityType.ACTIVE && remainingCooldown <= 0) {
            active = true;
            remainingDuration = duration;
            remainingCooldown = cooldown;
        }
    }

    public void update(float delta) {
        if (type == AbilityType.ACTIVE) {
            if (active && remainingDuration > 0) {
                remainingDuration -= delta;
                if (remainingDuration <= 0) {
                    active = false;
                    remainingDuration = 0;
                }
            }

            if (remainingCooldown > 0) {
                remainingCooldown -= delta;
                if (remainingCooldown < 0) {
                    remainingCooldown = 0;
                }
            }
        }
    }

    public boolean canActivate() {
        return type == AbilityType.ACTIVE && remainingCooldown <= 0 && !active;
    }

    public void reset() {
        active = false;
        remainingDuration = 0;
        remainingCooldown = 0;
    }

    public String getImagePath() {
        return imagePath;
    }

    public enum AbilityType {
        PASSIVE("Passive - Always Active"),
        ACTIVE("Active - Must Be Triggered");

        private final String description;

        AbilityType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
