package com.untilDawn.controllers;

import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.views.main.ProfileMenu;

public class ProfileMenuController {
    private final ProfileMenu view;

    ProfileMenuController(ProfileMenu view) {
        this.view = view;
        initializeButtonListeners();
    }

    private void initializeButtonListeners() {
        
    }

    public void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

}
