package com.untilDawn.controllers;


import com.untilDawn.Main;
import com.untilDawn.models.App;

public class PreGameMeuController {


    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

}
