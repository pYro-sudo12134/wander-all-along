package by.losik.systems.inventory;

import by.losik.components.core.ID;
import by.losik.components.core.Inventory;
import by.losik.components.core.InventoryState;
import by.losik.components.core.Item;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Singleton
@All({Inventory.class, InventoryState.class})
public class InventorySystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(InventorySystem.class);

    protected ComponentMapper<Inventory> mInventory;
    protected ComponentMapper<InventoryState> mInventoryState;
    protected ComponentMapper<ID> mID;
    protected ComponentMapper<Item> mItem;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera uiCamera;

    private boolean graphicsInitialized = false;

    private InventoryRenderer inventoryRenderer;
    private InventoryInputHandler inputHandler;
    private InventoryNavigationHandler navigationHandler;
    private InventoryInteractionHandler interactionHandler;

    @Override
    protected void initialize() {
        logger.info("InventorySystem initialized (graphics resources deferred)");
        inputHandler = new InventoryInputHandler();
        navigationHandler = new InventoryNavigationHandler();
        interactionHandler = new InventoryInteractionHandler();
    }

    private void initGraphicsResources() {
        if (graphicsInitialized) {
            return;
        }

        try {
            logger.info("Initializing InventorySystem graphics resources...");

            if (Gdx.gl == null || Gdx.gl20 == null) {
                logger.warn("OpenGL not initialized yet, deferring graphics initialization");
                return;
            }

            spriteBatch = new SpriteBatch();
            shapeRenderer = new ShapeRenderer();
            font = new BitmapFont();
            uiCamera = new OrthographicCamera();
            uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            inventoryRenderer = new InventoryRenderer(spriteBatch, shapeRenderer, font);
            inventoryRenderer.setIDMapper(mID);

            graphicsInitialized = true;
            logger.info("InventorySystem graphics resources initialized successfully");

        } catch (UnsatisfiedLinkError e) {
            logger.error("Failed to create graphics resources: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create graphics resources: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize inventory graphics", e);
        }
    }

    @Override
    protected void begin() {
        if (!graphicsInitialized) {
            initGraphicsResources();
        }
    }

    @Override
    protected void process(int entityId) {
        if (!graphicsInitialized) {
            return;
        }

        InventoryState inventoryState = mInventoryState.get(entityId);
        Inventory inventory = mInventory.get(entityId);

        if (inventoryState == null || inventory == null) {
            return;
        }

        inputHandler.handleInput(inventoryState);

        if (inventoryState.isOpen) {
            navigationHandler.handleNavigation(inventoryState, inventory);
            interactionHandler.handleInteraction(entityId, inventoryState, inventory);
        }
    }

    @Override
    protected void end() {
        if (!graphicsInitialized || spriteBatch == null || shapeRenderer == null || uiCamera == null) {
            return;
        }

        try {
            uiCamera.update();

            com.artemis.utils.IntBag entities = subscription.getEntities();
            int openInventories = 0;

            for (int i = 0; i < entities.size(); i++) {
                int entityId = entities.get(i);
                InventoryState inventoryState = mInventoryState.get(entityId);
                Inventory inventory = mInventory.get(entityId);

                if (inventoryState != null && inventoryState.isOpen && inventory != null) {
                    openInventories++;

                    if (logger.isDebugEnabled()) {
                        logger.debug("Rendering inventory for entity {}: inArmorSelection={}, selectedArmorSlot={}, selectedSlot={}",
                                entityId, inventoryState.inArmorSelection,
                                inventoryState.selectedArmorSlot, inventoryState.selectedSlot);
                    }

                    renderInventory(entityId, inventoryState, inventory);
                }
            }

            if (logger.isDebugEnabled() && openInventories > 0) {
                logger.debug("Rendered {} open inventories", openInventories);
            }

        } catch (Exception e) {
            logger.error("Error during inventory rendering: {}", e.getMessage(), e);
        }
    }

    private void renderInventory(int entityId, InventoryState inventoryState, Inventory inventory) {
        boolean wasDepthTestEnabled = Gdx.gl.glIsEnabled(GL20.GL_DEPTH_TEST);
        boolean wasBlendEnabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND);

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        try {
            setupRenderProjection();
            inventoryRenderer.render(entityId, inventory, inventoryState, screenWidth, screenHeight);
        } catch (Exception e) {
            logger.error("Error rendering inventory: {}", e.getMessage(), e);
        } finally {
            restoreOpenGLState(wasDepthTestEnabled, wasBlendEnabled);
        }
    }

    private void setupRenderProjection() {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        spriteBatch.setProjectionMatrix(uiCamera.combined);
    }

    private void restoreOpenGLState(boolean wasDepthTestEnabled, boolean wasBlendEnabled) {
        if (wasDepthTestEnabled) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        } else {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }

        if (wasBlendEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    protected void dispose() {
        logger.info("Disposing InventorySystem resources");

        try {
            if (spriteBatch != null) {
                spriteBatch.dispose();
                spriteBatch = null;
            }
            if (shapeRenderer != null) {
                shapeRenderer.dispose();
                shapeRenderer = null;
            }
            if (font != null) {
                font.dispose();
                font = null;
            }
        } catch (Exception e) {
            logger.error("Error disposing inventory resources: {}", e.getMessage(), e);
        }

        graphicsInitialized = false;
    }

    public void resize(int width, int height) {
        if (uiCamera != null) {
            uiCamera.setToOrtho(false, width, height);
            uiCamera.update();
        }
        try {
            if (graphicsInitialized) {
                Objects.requireNonNull(uiCamera).setToOrtho(false, width, height);
                uiCamera.update();
            }
        } catch (NullPointerException npe) {
            logger.error("NPE", npe);
        }
    }

    public boolean addItem(int entityId, ID itemId, int quantity) {
        if (!mInventory.has(entityId)) {
            return false;
        }

        Inventory inventory = mInventory.get(entityId);

        if (inventory.items == null) {
            inventory.items = new it.unimi.dsi.fastutil.objects.Object2IntArrayMap<>();
        }

        if (inventory.items.size() >= inventory.maxSlots &&
                !inventory.items.containsKey(itemId)) {
            return false;
        }

        int currentQuantity = inventory.items.getOrDefault(itemId, 0);
        inventory.items.put(itemId, currentQuantity + quantity);

        logger.info("Added {}x {} to entity {} inventory",
                quantity, itemId.name, entityId);

        return true;
    }

    public boolean removeItem(int entityId, ID itemId, int quantity) {
        if (!mInventory.has(entityId)) {
            return false;
        }

        Inventory inventory = mInventory.get(entityId);

        if (inventory.items == null || !inventory.items.containsKey(itemId)) {
            return false;
        }

        int currentQuantity = inventory.items.getInt(itemId);
        if (currentQuantity < quantity) {
            return false;
        }

        if (currentQuantity == quantity) {
            inventory.items.removeInt(itemId);
        } else {
            inventory.items.put(itemId, currentQuantity - quantity);
        }

        logger.info("Removed {}x {} from entity {} inventory",
                quantity, itemId.name, entityId);

        return true;
    }

    public boolean isInventoryOpen(int entityId) {
        if (mInventoryState.has(entityId)) {
            InventoryState state = mInventoryState.get(entityId);
            return state.isOpen;
        }
        return false;
    }
}