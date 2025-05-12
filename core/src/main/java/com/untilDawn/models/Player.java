package com.untilDawn.models;

import com.untilDawn.models.enums.Characters;

public class Player {
    private Characters character;

    public Player(Characters character) {
        this.character = character;
    }

    public String getName() {
        return character.getName();
    }

    
}
