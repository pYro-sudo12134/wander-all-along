package by.losik.core;

import by.losik.components.core.Camera;
import by.losik.components.core.FollowTarget;
import by.losik.components.core.Position;
import by.losik.providers.factories.CreatureFactory;
import by.losik.systems.BoundsSystem;
import by.losik.systems.CameraSystem;
import by.losik.systems.InventorySystem;
import by.losik.systems.IsometricModelRenderSystem;
import by.losik.systems.MovementSystem;
import by.losik.systems.PlayerInputSystem;
import by.losik.ui.MainGameScreen;
import com.artemis.World;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GameBootstrap extends ApplicationAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GameBootstrap.class);

    private final World world;
    private final CreatureFactory creatureFactory;
    private MainGameScreen mainGameScreen;
    private MovementSystem movementSystem;
    private PlayerInputSystem playerInputSystem;
    private BoundsSystem boundsSystem;
    private CameraSystem cameraSystem;
    private IsometricModelRenderSystem renderSystem;
    private InventorySystem inventorySystem;
    private boolean initialized = false;
    private boolean paused = false;
    private int playerEntityId = -1;

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

        setupWorld();
        setupSystems();
        createInitialEntities();

        mainGameScreen = new MainGameScreen(this);
        initialized = true;
        logger.info("Game initialized successfully!");
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
        renderSystem = world.getSystem(IsometricModelRenderSystem.class);
        inventorySystem = world.getSystem(InventorySystem.class);

        if (playerInputSystem != null) {
            playerInputSystem.setMovementSpeed(5.0f);
        }

        if (boundsSystem != null) {
            boundsSystem.setAllBounds(-50, 50, -10, 100, -50, 50);
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
        logger.info("Creating camera entity: {} for player: {}", cameraEntity, playerId);

        Camera camera = new Camera();
        camera.isIsometric = true;

        Position playerPos = world.getMapper(Position.class).get(playerId);
        if (playerPos != null) {
            FollowTarget followTarget = new FollowTarget(playerId);
            followTarget.targetX = playerPos.value.x;
            followTarget.targetY = playerPos.value.y;
            followTarget.targetZ = playerPos.value.z;

            followTarget.followSpeed = 2.0f;

            camera.target = new com.badlogic.gdx.math.Vector3(
                    playerPos.value.x,
                    playerPos.value.y,
                    playerPos.value.z
            );

            camera.lookAt(playerPos.value.x, playerPos.value.y, playerPos.value.z);

            camera.position.set(
                    playerPos.value.x,
                    playerPos.value.y,
                    playerPos.value.z
            );

            world.edit(cameraEntity)
                    .add(camera)
                    .add(followTarget);

            logger.info("Camera created with target: ({}, {}, {})",
                    camera.target.x, camera.target.y, camera.target.z);
            logger.info("Camera will use 120 isometric projection");
        } else {
            logger.error("Player position is null! Cannot create camera.");
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
        logger.info("Setting up systems...");

        if (cameraSystem != null) {
            logger.info("CameraSystem found and ready");

            if (cameraSystem.isCameraInitialized()) {
                com.badlogic.gdx.graphics.PerspectiveCamera cam = cameraSystem.getPerspectiveCamera();
                if (cam != null) {
                    logger.info("Isometric camera initialized: pos=({}, {}, {}), fov={}, viewport={}x{}",
                            cam.position.x, cam.position.y, cam.position.z,
                            cam.fieldOfView,
                            cam.viewportWidth, cam.viewportHeight);
                }
            } else {
                logger.info("CameraSystem is initializing camera...");
            }
        } else {
            logger.error("CameraSystem not found in world!");
        }

        if (renderSystem != null) {
            logger.info("IsometricModelRenderSystem found and ready");
        } else {
            logger.error("IsometricModelRenderSystem not found in world!");
        }

        com.artemis.utils.ImmutableBag<com.artemis.BaseSystem> systems = world.getSystems();
        logger.info("Available systems in world ({} total):", systems.size());
        for (int i = 0; i < systems.size(); i++) {
            com.artemis.BaseSystem system = systems.get(i);
            logger.info("  {}. {}", i + 1, system.getClass().getSimpleName());
        }
    }

    public void update(float deltaTime) {
        if (!initialized || paused) {
            return;
        }

        world.setDelta(deltaTime);
        world.process();
    }

    public void render() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void resize(int width, int height) {
        if (!initialized) {
            return;
        }

        logger.info("Resizing to {}x{}", width, height);

        if (cameraSystem != null) {
            cameraSystem.resize(width, height);
            inventorySystem.resize(width, height);
        }
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

        if (world != null) {
            world.dispose();
        }

        initialized = false;
        logger.info("Game disposed successfully");
    }

    public World getWorld() {
        return world;
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

    public IsometricModelRenderSystem getRenderSystem() {
        return renderSystem;
    }

}