package com.untilDawn.models.enums;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

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
    private Texture texture;
    private boolean textureLoaded = false;

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

    public String getImagePath() {
        return imagePath;
    }


    public Texture getTexture() {
        if (!textureLoaded) {
            loadTexture();
        }
        return texture;
    }

    private void loadTexture() {
        try {
            String texturePath = imagePath + ".png";
            if (Gdx.files.internal(texturePath).exists()) {
                texture = new Texture(Gdx.files.internal(texturePath));
                Gdx.app.log("Abilities", "Loaded texture for " + name + ": " + texturePath);
            } else {
                Gdx.app.error("Abilities", "Texture not found for " + name + ": " + texturePath);
                String altPath = "Images/abilities/" + name.toLowerCase() + ".png";
                if (Gdx.files.internal(altPath).exists()) {
                    texture = new Texture(Gdx.files.internal(altPath));
                    Gdx.app.log("Abilities", "Loaded alternative texture for " + name + ": " + altPath);
                } else {
                    Gdx.app.log("Abilities", "No texture available for " + name);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("Abilities", "Error loading texture for " + name + ": " + e.getMessage());
        }
        textureLoaded = true;
    }

    public void activate() {
        if (type == AbilityType.ACTIVE && remainingCooldown <= 0) {
            active = true;
            remainingDuration = duration;
            remainingCooldown = cooldown;
            Gdx.app.log("Abilities", name + " activated for " + duration + " seconds");
        } else if (type == AbilityType.PASSIVE) {
            Gdx.app.log("Abilities", name + " (passive) applied");
        }
    }

    public void update(float delta) {
        if (type == AbilityType.ACTIVE) {
            if (active && remainingDuration > 0) {
                remainingDuration -= delta;
                if (remainingDuration <= 0) {
                    active = false;
                    remainingDuration = 0;
                    Gdx.app.log("Abilities", name + " effect ended");
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

    public void disposeTexture() {
        if (texture != null) {
            texture.dispose();
            texture = null;
            textureLoaded = false;
        }
    }

    public float getProgress() {
        if (type == AbilityType.PASSIVE) {
            return 1.0f;
        }

        if (active && duration > 0) {
            return 1.0f - (remainingDuration / duration);
        }

        return 0f;
    }

    public float getCooldownProgress() {
        if (type == AbilityType.PASSIVE || cooldown <= 0) {
            return 1.0f;
        }

        return 1.0f - (remainingCooldown / cooldown);
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
