package by.losik.ui;

import by.losik.core.GameBootstrap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainGameScreen implements Screen {
    private static final Logger logger = LoggerFactory.getLogger(MainGameScreen.class);

    private final GameBootstrap gameBootstrap;
    private Stage stage;
    private Skin skin;
    private Table rootTable;
    private Label fpsLabel;
    private float uiUpdateTimer = 0;
    private static final float UI_UPDATE_INTERVAL = 0.1f;

    public MainGameScreen(GameBootstrap gameBootstrap) {
        this.gameBootstrap = gameBootstrap;
        initializeUI();
    }

    private void initializeUI() {
        logger.info("Initializing game UI...");
        stage = new Stage(new ScreenViewport());

        skin = createBasicSkin();
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        rootTable.top().left().pad(10);
        fpsLabel = new Label("FPS: --", skin);

        Table infoTable = new Table();
        infoTable.add(fpsLabel).left().padBottom(5).row();

        rootTable.add(infoTable).expand().top().left();
        rootTable.row();

        logger.info("Game UI initialized");
    }

    private Skin createBasicSkin() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        return skin;
    }

    @Override
    public void show() {
        logger.info("Main game screen shown");
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameBootstrap.update(delta);
        gameBootstrap.render();
        updateUI(delta);

        stage.act(delta);
        stage.draw();
    }

    private void updateUI(float delta) {
        uiUpdateTimer += delta;

        if (uiUpdateTimer >= UI_UPDATE_INTERVAL) {
            int fps = Gdx.graphics.getFramesPerSecond();
            fpsLabel.setText(String.format("FPS: %d", fps));

            uiUpdateTimer = 0;
        }
    }

    @Override
    public void resize(int width, int height) {
        logger.debug("Resizing screen to {}x{}", width, height);

        stage.getViewport().update(width, height, true);

        gameBootstrap.resize(width, height);
    }

    @Override
    public void pause() {
        logger.info("Game screen paused");
        gameBootstrap.pause();
    }

    @Override
    public void resume() {
        logger.info("Game screen resumed");
        gameBootstrap.resume();
    }

    @Override
    public void hide() {
        logger.info("Main game screen hidden");
    }

    @Override
    public void dispose() {
        logger.info("Disposing game screen...");

        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        if (skin != null) {
            skin.dispose();
            skin = null;
        }

        logger.info("Game screen disposed");
    }
}