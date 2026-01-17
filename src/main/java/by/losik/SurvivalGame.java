package by.losik;

import by.losik.core.GameBootstrap;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class SurvivalGame extends Game {

    @Inject
    private Injector injector;

    @Inject
    private GameBootstrap gameBootstrap;

    @Override
    public void create() {
        Gdx.graphics.setForegroundFPS(60);
        Gdx.graphics.setVSync(true);

        gameBootstrap.initialize();

        Screen mainScreen = gameBootstrap.getMainScreen();
        setScreen(mainScreen);
    }

    @Override
    public void render() {
        gameBootstrap.update(Gdx.graphics.getDeltaTime());

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameBootstrap.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameBootstrap.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
        gameBootstrap.pause();
    }

    @Override
    public void resume() {
        super.resume();
        gameBootstrap.resume();
    }
}