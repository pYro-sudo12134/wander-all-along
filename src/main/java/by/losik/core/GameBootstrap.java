package by.losik.core;

import by.losik.components.core.Camera;
import by.losik.components.core.FollowTarget;
import by.losik.components.core.Position;
import by.losik.providers.factories.CreatureFactory;
import by.losik.systems.BoundsSystem;
import by.losik.systems.CameraSystem;
import by.losik.systems.MovementSystem;
import by.losik.systems.PlayerInputSystem;
import by.losik.ui.MainGameScreen;
import com.artemis.World;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class GameBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(GameBootstrap.class);

    private final World world;
    private final CreatureFactory creatureFactory;
    private SpriteBatch spriteBatch;
    private OrthographicCamera gameCamera;
    private OrthographicCamera uiCamera;
    private Viewport gameViewport;
    private Viewport uiViewport;
    private MainGameScreen mainGameScreen;
    private MovementSystem movementSystem;
    private PlayerInputSystem playerInputSystem;
    private BoundsSystem boundsSystem;
    private CameraSystem cameraSystem;
    private boolean initialized = false;
    private boolean paused = false;
    private int playerEntityId = -1;
    private Texture whitePixel;
    private TextureRegion whiteRegion;

    @Inject
    public GameBootstrap(World world, CreatureFactory creatureFactory) {
        this.world = world;
        this.creatureFactory = creatureFactory;
    }

    public void initialize() {
        if (initialized) {
            logger.warn("Game already initialized!");
            return;
        }

        logger.info("Initializing game...");
        initializeGraphics();
        setupWorld();
        createInitialEntities();
        setupSystems();
        mainGameScreen = new MainGameScreen(this);

        initialized = true;
        logger.info("Game initialized successfully!");
    }

    private void initializeGraphics() {
        logger.info("Initializing graphics...");
        spriteBatch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        whiteRegion = new TextureRegion(whitePixel);
        pixmap.dispose();

        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(1920, 1080, gameCamera);
        gameViewport.apply();

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(1920, 1080, uiCamera);
        uiViewport.apply();

        logger.info("Graphics initialized: {}x{}",
                (int)gameViewport.getWorldWidth(),
                (int)gameViewport.getWorldHeight());
    }

    private void setupWorld() {
        logger.info("Setting up Artemis world...");

        injectSystems();

        logger.info("World setup complete with {} systems",
                world.getSystems().size());
    }

    private void injectSystems() {
        movementSystem = world.getSystem(MovementSystem.class);
        playerInputSystem = world.getSystem(PlayerInputSystem.class);
        boundsSystem = world.getSystem(BoundsSystem.class);
        cameraSystem = world.getSystem(CameraSystem.class);

        if (playerInputSystem != null) {
            playerInputSystem.setMovementSpeed(5.0f);
        }

        if (boundsSystem != null) {
            boundsSystem.setWorldBounds(-100, 100, -100, 100);
            boundsSystem.setEnforceWorldBounds(true);
        }
    }

    private void createInitialEntities() {
        logger.info("Creating initial entities...");

        playerEntityId = creatureFactory.createPlayer("Hero", 0, 0);
        logger.info("Player created with entity ID: {}", playerEntityId);

        createCameraForPlayer(playerEntityId);

        createDemoNPCs();

        createTestObjects();

        logger.info("Created initial entities");
    }

    private void createCameraForPlayer(int playerId) {
        int cameraEntity = world.create();

        Camera camera = new Camera();
        camera.position.set(0, 30, 50);

        Position playerPos = world.getMapper(Position.class).get(playerId);
        if (playerPos != null) {
            FollowTarget followTarget = new FollowTarget(
                    playerPos.value.x,
                    playerPos.value.y,
                    playerPos.value.z
            );

            world.edit(cameraEntity)
                    .add(camera)
                    .add(followTarget);

            logger.info("Camera created to follow player at ({}, {}, {})",
                    followTarget.targetX, followTarget.targetY, followTarget.targetZ);
        }
    }

    private void createDemoNPCs() {
        String[] npcNames = {"Guard", "Merchant", "Farmer", "Hunter"};

        for (int i = 0; i < npcNames.length; i++) {
            float x = (i - 2) * 5;
            float y = -5;

            int npcId = creatureFactory.createNPC(npcNames[i], x, y);
            logger.info("Created NPC '{}' at ({}, {}) with entity ID: {}",
                    npcNames[i], x, y, npcId);
        }
    }

    private void createTestObjects() {
        logger.info("Test objects would be created here");
    }

    private void setupSystems() {
        logger.info("Systems could be set up here");
    }

    public void update(float deltaTime) {
        if (!initialized || paused) {
            return;
        }

        world.setDelta(deltaTime);
        world.process();

        if (gameCamera != null) {
            gameCamera.update();
        }

        if (uiCamera != null) {
            uiCamera.update();
        }
    }

    public void render() {
        if (!initialized) {
            return;
        }

        // Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1);
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        gameViewport.apply();

        spriteBatch.setProjectionMatrix(gameCamera.combined);
        spriteBatch.begin();

        // TODO: rendering

        spriteBatch.setColor(Color.DARK_GRAY);
        spriteBatch.draw(whiteRegion,
                gameCamera.position.x - 250f,
                gameCamera.position.y - 250f,
                500f,
                500f
        );
        spriteBatch.setColor(Color.WHITE);

        spriteBatch.end();

        uiViewport.apply();
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();

        // TODO: UI

        spriteBatch.end();
    }

    public void resize(int width, int height) {
        if (!initialized) {
            return;
        }

        logger.info("Resizing to {}x{}", width, height);
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, false);
        uiCamera.position.set(uiCamera.viewportWidth / 2, uiCamera.viewportHeight / 2, 0);
    }

    public void pause() {
        logger.info("Game paused");
        paused = true;
    }

    public void resume() {
        logger.info("Game resumed");
        paused = false;
    }

    public void dispose() {
        logger.info("Disposing game resources...");

        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }

        if (world != null) {
            world.dispose();
        }

        initialized = false;
        logger.info("Game disposed successfully");
    }

    public World getWorld() {
        return world;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public OrthographicCamera getUiCamera() {
        return uiCamera;
    }

    public Viewport getGameViewport() {
        return gameViewport;
    }

    public Viewport getUiViewport() {
        return uiViewport;
    }

    public MainGameScreen getMainScreen() {
        return mainGameScreen;
    }

    public Screen getMainGameScreen() {
        return mainGameScreen;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getPlayerEntityId() {
        return playerEntityId;
    }

    public MovementSystem getMovementSystem() {
        return movementSystem;
    }

    public PlayerInputSystem getPlayerInputSystem() {
        return playerInputSystem;
    }

    public BoundsSystem getBoundsSystem() {
        return boundsSystem;
    }

    public CameraSystem getCameraSystem() {
        return cameraSystem;
    }

    public void spawnCreature(String name, float x, float y) {
        if (!initialized) {
            logger.warn("Cannot spawn creature: game not initialized");
            return;
        }

        int creatureId = creatureFactory.createNPC(name, x, y);
        logger.info("Spawned creature '{}' at ({}, {}) with ID: {}", name, x, y, creatureId);
    }

    public List<Integer> getAllCreatureIds() {
        List<Integer> creatureIds = new ArrayList<>();
        // TODO: get IDs for all creatures
        return creatureIds;
    }
}