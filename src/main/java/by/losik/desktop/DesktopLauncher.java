package by.losik.desktop;

import by.losik.SurvivalGame;
import by.losik.core.CompositionRoot;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowIcon("textures/default.png");
        config.setTitle("Survival Game");
        config.setWindowedMode(1280, 720);
        config.setResizable(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(30);
        config.useVsync(true);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);

        Injector injector = Guice.createInjector(new CompositionRoot());
        SurvivalGame game = injector.getInstance(SurvivalGame.class);

        new Lwjgl3Application(game, config);
    }
}