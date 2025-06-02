package com.untilDawn.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.untilDawn.Main;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {
    private static Lwjgl3Application app;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // macOS and Windows compatibility
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        app = new Lwjgl3Application(new Main(), getDefaultConfiguration());
        return app;
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("20MinutesTillDawn");

        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }

    public static Lwjgl3Application getApplication() {
        return app;
    }
}
