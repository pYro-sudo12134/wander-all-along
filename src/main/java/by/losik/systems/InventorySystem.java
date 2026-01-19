package by.losik.systems;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.core.ID;
import by.losik.components.core.Inventory;
import by.losik.components.core.InventoryState;
import by.losik.components.core.Item;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
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
    private static final int INVENTORY_SLOT_SIZE = 64;
    private static final int INVENTORY_SLOTS_X = 10;
    private static final int INVENTORY_PADDING = 10;
    private static final int INFO_PANEL_WIDTH = 300;

    private final Int2BooleanArrayMap iKeyWasPressedMap = new Int2BooleanArrayMap();
    private final Int2BooleanArrayMap tabKeyWasPressedMap = new Int2BooleanArrayMap();
    private boolean graphicsInitialized = false;

    private boolean leftKeyWasPressed = false;
    private boolean rightKeyWasPressed = false;
    private boolean upKeyWasPressed = false;
    private boolean downKeyWasPressed = false;
    private boolean wKeyWasPressed = false;
    private boolean aKeyWasPressed = false;
    private boolean sKeyWasPressed = false;
    private boolean dKeyWasPressed = false;

    @Override
    protected void initialize() {
        logger.info("InventorySystem initialized (graphics resources deferred)");
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

        handleInventoryInput(inventoryState);

        if (inventoryState.isOpen) {
            handleInventoryNavigation(entityId, inventoryState, inventory);
            handleInventoryInteraction(entityId, inventoryState, inventory);
        }
    }

    private void handleInventoryInput(InventoryState inventoryState) {
        boolean iPressed = Gdx.input.isKeyPressed(Input.Keys.I);
        boolean escPressed = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
        boolean tabPressed = Gdx.input.isKeyPressed(Input.Keys.TAB);

        if (iPressed && !iKeyWasPressedMap.getOrDefault(Input.Keys.I, false)) {
            inventoryState.isOpen = !inventoryState.isOpen;
            if (inventoryState.isOpen) {
                inventoryState.selectedSlot = 0;
                inventoryState.selectedArmorSlot = 0; // Сбросить выбор брони
                inventoryState.inArmorSelection = false; // Начинаем с инвентаря
            }
            logger.info("Inventory toggled by I key: {}", inventoryState.isOpen);
            iKeyWasPressedMap.put(Input.Keys.I, true);
        } else if (!iPressed) {
            iKeyWasPressedMap.put(Input.Keys.I, false);
        }

        if (tabPressed && !tabKeyWasPressedMap.getOrDefault(Input.Keys.TAB, false)) {
            inventoryState.inArmorSelection = !inventoryState.inArmorSelection;
            if (inventoryState.inArmorSelection) {
                inventoryState.selectedArmorSlot = 0;
                logger.info("Switched to ARMOR selection (slot: {})", inventoryState.selectedArmorSlot);
            } else {
                logger.info("Switched to INVENTORY selection (slot: {})", inventoryState.selectedSlot);
            }
            tabKeyWasPressedMap.put(Input.Keys.TAB, true);
        } else if (!tabPressed) {
            tabKeyWasPressedMap.put(Input.Keys.TAB, false);
        }

        if (escPressed && inventoryState.isOpen) {
            inventoryState.isOpen = false;
            logger.info("Inventory closed by ESC");
        }
    }

    private void handleInventoryNavigation(int entityId, InventoryState inventoryState, Inventory inventory) {
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        boolean wPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean aPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean sPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.D);

        if (logger.isDebugEnabled()) {
            logger.debug("Navigation - inArmorSelection: {}, selectedArmorSlot: {}, selectedSlot: {}",
                    inventoryState.inArmorSelection,
                    inventoryState.selectedArmorSlot,
                    inventoryState.selectedSlot);
        }

        if (inventoryState.inArmorSelection) {
            handleArmorNavigation(inventoryState, upPressed, downPressed,
                    wPressed, sPressed, leftPressed, rightPressed);
        } else {
            handleStandardNavigation(inventoryState, inventory, leftPressed, rightPressed,
                    upPressed, downPressed, wPressed, aPressed,
                    sPressed, dPressed);
        }

        leftKeyWasPressed = leftPressed;
        rightKeyWasPressed = rightPressed;
        upKeyWasPressed = upPressed;
        downKeyWasPressed = downPressed;
        wKeyWasPressed = wPressed;
        aKeyWasPressed = aPressed;
        sKeyWasPressed = sPressed;
        dKeyWasPressed = dPressed;
    }

    private void handleStandardNavigation(InventoryState inventoryState, Inventory inventory,
                                          boolean leftPressed, boolean rightPressed,
                                          boolean upPressed, boolean downPressed,
                                          boolean wPressed, boolean aPressed,
                                          boolean sPressed, boolean dPressed) {
        int maxSlots = inventory.maxSlots;
        int slotsX = INVENTORY_SLOTS_X;
        int slotsY = Math.max(1, (int) Math.ceil(maxSlots / (float) slotsX));

        if ((leftPressed && !leftKeyWasPressed) || (aPressed && !aKeyWasPressed)) {
            if (inventoryState.selectedSlot % slotsX > 0) {
                inventoryState.selectedSlot--;
                logger.debug("Inventory navigation: left to slot {}", inventoryState.selectedSlot);
            }
        }

        if ((rightPressed && !rightKeyWasPressed) || (dPressed && !dKeyWasPressed)) {
            if (inventoryState.selectedSlot % slotsX < slotsX - 1 &&
                    inventoryState.selectedSlot < maxSlots - 1) {
                inventoryState.selectedSlot++;
                logger.debug("Inventory navigation: right to slot {}", inventoryState.selectedSlot);
            }
        }

        if ((upPressed && !upKeyWasPressed) || (wPressed && !wKeyWasPressed)) {
            if (inventoryState.selectedSlot >= slotsX) {
                inventoryState.selectedSlot -= slotsX;
                logger.debug("Inventory navigation: UP to slot {}", inventoryState.selectedSlot);
            }
        }

        if ((downPressed && !downKeyWasPressed) || (sPressed && !sKeyWasPressed)) {
            if (inventoryState.selectedSlot < slotsX * (slotsY - 1) &&
                    inventoryState.selectedSlot + slotsX < maxSlots) {
                inventoryState.selectedSlot += slotsX;
                logger.debug("Inventory navigation: DOWN to slot {}", inventoryState.selectedSlot);
            }
        }
    }

    private void handleArmorNavigation(InventoryState inventoryState,
                                       boolean upPressed, boolean downPressed,
                                       boolean wPressed, boolean sPressed,
                                       boolean leftPressed, boolean rightPressed) {
        int armorSlotsCount = ArmorSlot.values().length;

        if ((upPressed && !upKeyWasPressed) || (wPressed && !wKeyWasPressed)) {
            if (inventoryState.selectedArmorSlot > 0) {
                inventoryState.selectedArmorSlot--;
                logger.debug("Armor navigation: up to slot {} ({})",
                        inventoryState.selectedArmorSlot,
                        ArmorSlot.values()[inventoryState.selectedArmorSlot].name());
            }
        }

        if ((downPressed && !downKeyWasPressed) || (sPressed && !sKeyWasPressed)) {
            if (inventoryState.selectedArmorSlot < armorSlotsCount - 1) {
                inventoryState.selectedArmorSlot++;
                logger.debug("Armor navigation: down to slot {} ({})",
                        inventoryState.selectedArmorSlot,
                        ArmorSlot.values()[inventoryState.selectedArmorSlot].name());
            }
        }

        if ((leftPressed && !leftKeyWasPressed) || (rightPressed && !rightKeyWasPressed)) {
            inventoryState.inArmorSelection = false;
            logger.debug("Switched to inventory selection");
        }
    }

    private void handleInventoryInteraction(int entityId, InventoryState inventoryState, Inventory inventory) {
        if (inventoryState.inArmorSelection) {
            handleArmorInteraction(entityId, inventoryState);
        } else {
            handleStandardInteraction(entityId, inventoryState, inventory);
        }
    }

    private void handleStandardInteraction(int entityId, InventoryState inventoryState, Inventory inventory) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            useSelectedItem(entityId, inventoryState.selectedSlot, inventory);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            dropSelectedItem(entityId, inventoryState.selectedSlot, inventory);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            examineSelectedItem(entityId, inventoryState.selectedSlot, inventory);
        }

        for (int i = Input.Keys.NUM_1; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                int slot = i - Input.Keys.NUM_1;
                if (slot < inventory.maxSlots) {
                    inventoryState.selectedSlot = slot;
                    logger.debug("Quick select slot: {}", slot + 1);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            int slot = 9;
            if (slot < inventory.maxSlots) {
                inventoryState.selectedSlot = slot;
                logger.debug("Quick select slot: 10");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            equipItemToArmor(entityId, inventoryState.selectedSlot, inventory);
        }
    }

    private void handleArmorInteraction(int entityId, InventoryState inventoryState) {
        ArmorSlot selectedArmorSlot = ArmorSlot.values()[inventoryState.selectedArmorSlot];

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            equipArmorFromInventory(entityId, selectedArmorSlot);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            unequipArmor(entityId, selectedArmorSlot);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            examineEquippedArmor(entityId, selectedArmorSlot);
        }
    }

    private void equipArmorFromInventory(int entityId, ArmorSlot slot) {
        logger.info("Trying to equip armor to {} slot for entity {}", slot, entityId);
        // TODO: equip armor from inventory
    }

    private void unequipArmor(int entityId, ArmorSlot slot) {
        logger.info("Trying to unequip armor from {} slot for entity {}", slot, entityId);
        // TODO: unequip armor
    }

    private void examineEquippedArmor(int entityId, ArmorSlot slot) {
        logger.info("Examining armor in {} slot for entity {}", slot, entityId);
        // TODO: examine armor item
    }

    private void equipItemToArmor(int entityId, int slotIndex, Inventory inventory) {
        logger.info("Trying to equip item from slot {} to armor for entity {}",
                slotIndex + 1, entityId);
        // TODO: equip item to armor
    }

    private void useSelectedItem(int entityId, int slot, Inventory inventory) {
        logger.info("Using item in slot {}", slot + 1);
        // TODO: item use
    }

    private void dropSelectedItem(int entityId, int slot, Inventory inventory) {
        logger.info("Dropping item in slot {}", slot + 1);
        // TODO: item drop
    }

    private void examineSelectedItem(int entityId, int slot, Inventory inventory) {
        logger.info("Examining item in slot {}", slot + 1);
        // TODO: item examination
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

        int maxSlots = inventory.maxSlots;
        int slotsX = INVENTORY_SLOTS_X;
        int slotsY = Math.max(1, (int) Math.ceil(maxSlots / (float) slotsX));

        int inventoryWidth = slotsX * INVENTORY_SLOT_SIZE + (slotsX - 1) * INVENTORY_PADDING;
        int inventoryHeight = slotsY * INVENTORY_SLOT_SIZE + (slotsY - 1) * INVENTORY_PADDING;

        int armorPanelWidth = 200;
        int totalWidth = inventoryWidth + INFO_PANEL_WIDTH + armorPanelWidth + INVENTORY_PADDING * 3;

        int inventoryX = (screenWidth - totalWidth) / 2;
        int inventoryY = (screenHeight - inventoryHeight) / 2;

        try {
            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            spriteBatch.setProjectionMatrix(uiCamera.combined);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.1f, 0.15f, 0.95f);
            shapeRenderer.rect(inventoryX, inventoryY, inventoryWidth, inventoryHeight);

            int infoPanelX = inventoryX + inventoryWidth + INVENTORY_PADDING;
            shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 0.95f);
            shapeRenderer.rect(infoPanelX, inventoryY, INFO_PANEL_WIDTH, inventoryHeight);

            int armorPanelX = infoPanelX + INFO_PANEL_WIDTH + INVENTORY_PADDING;
            shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 0.95f);
            shapeRenderer.rect(armorPanelX, inventoryY, armorPanelWidth, inventoryHeight);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            for (int row = 0; row < slotsY; row++) {
                for (int col = 0; col < slotsX; col++) {
                    int slotIndex = row * slotsX + col;
                    if (slotIndex >= maxSlots) continue;

                    int slotX = inventoryX + col * (INVENTORY_SLOT_SIZE + INVENTORY_PADDING);
                    int slotY = inventoryY + (slotsY - 1 - row) * (INVENTORY_SLOT_SIZE + INVENTORY_PADDING);

                    shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
                    shapeRenderer.rect(slotX, slotY, INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE);

                    if (!inventoryState.inArmorSelection && slotIndex == inventoryState.selectedSlot) {
                        shapeRenderer.setColor(1f, 0.8f, 0f, 1f); // Желтый
                        shapeRenderer.rect(slotX - 4, slotY - 4,
                                INVENTORY_SLOT_SIZE + 8, INVENTORY_SLOT_SIZE + 8);
                    }
                }
            }

            ArmorSlot[] armorSlots = ArmorSlot.values();
            int armorSlotsCount = armorSlots.length;
            int armorSlotSize = Math.min(64, (inventoryHeight - 40 - (armorSlotsCount - 1) * 10) / armorSlotsCount);
            int armorSlotPadding = 10;
            int armorSlotsHeight = armorSlotsCount * armorSlotSize + (armorSlotsCount - 1) * armorSlotPadding;
            int armorStartY = inventoryY + (inventoryHeight - armorSlotsHeight) / 2;

            for (int i = 0; i < armorSlotsCount; i++) {
                int renderIndex = armorSlotsCount - 1 - i;
                int armorY = armorStartY + i * (armorSlotSize + armorSlotPadding);

                shapeRenderer.setColor(0.4f, 0.4f, 0.6f, 1f);
                shapeRenderer.rect(armorPanelX + 20, armorY, armorSlotSize, armorSlotSize);

                if (inventoryState.inArmorSelection && renderIndex == inventoryState.selectedArmorSlot) {
                    shapeRenderer.setColor(1f, 1f, 0f, 1f);
                    shapeRenderer.rect(armorPanelX + 20 - 2, armorY - 2,
                            armorSlotSize + 4, armorSlotSize + 4);
                } else {
                    shapeRenderer.setColor(0.6f, 0.6f, 0.8f, 1f);
                    shapeRenderer.rect(armorPanelX + 20 - 2, armorY - 2,
                            armorSlotSize + 4, armorSlotSize + 4);
                }
            }

            shapeRenderer.end();
            spriteBatch.begin();

            font.setColor(Color.WHITE);
            font.getData().setScale(1.5f);
            String title = mID.has(entityId) ? mID.get(entityId).name + "'s Inventory" : "Inventory";
            font.draw(spriteBatch, title, infoPanelX + 20, inventoryY + inventoryHeight - 20);

            font.getData().setScale(1.2f);
            if (inventoryState.inArmorSelection) {
                font.setColor(Color.YELLOW);
                font.draw(spriteBatch, "ARMOR SELECTION", infoPanelX + 20, inventoryY + inventoryHeight - 50);
            } else {
                font.setColor(Color.CYAN);
                font.draw(spriteBatch, "INVENTORY SELECTION",
                        infoPanelX + 20, inventoryY + inventoryHeight - 50);
                font.getData().setScale(1.0f);
            }

            font.getData().setScale(0.9f);
            for (int row = 0; row < slotsY; row++) {
                for (int col = 0; col < slotsX; col++) {
                    int slotIndex = row * slotsX + col;
                    if (slotIndex >= maxSlots) continue;

                    int slotX = inventoryX + col * (INVENTORY_SLOT_SIZE + INVENTORY_PADDING);
                    int slotY = inventoryY + (slotsY - 1 - row) * (INVENTORY_SLOT_SIZE + INVENTORY_PADDING);

                    font.setColor(Color.LIGHT_GRAY);
                    font.draw(spriteBatch, String.valueOf(slotIndex + 1),
                            slotX + 5, slotY + INVENTORY_SLOT_SIZE - 5);
                }
            }

            font.getData().setScale(0.9f);
            for (int i = 0; i < armorSlotsCount; i++) {
                int armorY = armorStartY + i * (armorSlotSize + armorSlotPadding);

                int renderIndex = armorSlotsCount - 1 - i;
                ArmorSlot slot = armorSlots[renderIndex];

                String armorName = slot.name();
                if (armorName.length() > 12) {
                    armorName = armorName.substring(0, 12) + ".";
                }

                if (inventoryState.inArmorSelection && renderIndex == inventoryState.selectedArmorSlot) {
                    font.setColor(Color.YELLOW);
                    font.getData().setScale(1.0f);
                    font.draw(spriteBatch, "▶ " + armorName,
                            armorPanelX + 40, armorY + armorSlotSize);
                } else {
                    font.setColor(Color.LIGHT_GRAY);
                    font.getData().setScale(0.9f);
                    font.draw(spriteBatch, armorName,
                            armorPanelX + 40, armorY + armorSlotSize);
                }

                font.getData().setScale(0.8f);
                font.setColor(Color.GRAY);
                font.draw(spriteBatch, "(" + slot.getBodyPart() + ")",
                        armorPanelX + 40, armorY + armorSlotSize - 15);
            }

            font.getData().setScale(0.9f);
            font.setColor(Color.LIGHT_GRAY);
            int controlsY = inventoryY + inventoryHeight - 70;
            font.draw(spriteBatch, "Controls:", infoPanelX + 20, controlsY);
            font.getData().setScale(0.8f);
            font.draw(spriteBatch, "TAB - Switch mode", infoPanelX + 40, controlsY - 20);
            font.draw(spriteBatch, "Arrows/WASD - Navigate", infoPanelX + 40, controlsY - 35);
            font.draw(spriteBatch, "E - Use/Equip", infoPanelX + 40, controlsY - 50);
            font.draw(spriteBatch, "Q - Drop/Unequip", infoPanelX + 40, controlsY - 65);
            font.draw(spriteBatch, "Enter - Examine", infoPanelX + 40, controlsY - 80);
            font.draw(spriteBatch, "I/Esc - Toggle/Close", infoPanelX + 40, controlsY - 95);
            if (inventoryState.inArmorSelection) {
                font.setColor(Color.YELLOW);
                font.draw(spriteBatch, "TAB - Back to inventory", infoPanelX + 40, controlsY - 110);
            }

            spriteBatch.end();

        } catch (Exception e) {
            logger.error("Error rendering inventory: {}", e.getMessage(), e);
        } finally {
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
        }
        catch (NullPointerException npe) {
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