package by.losik.systems.inventory;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.core.Inventory;
import by.losik.components.core.InventoryState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryNavigationHandler {
    private static final Logger logger = LoggerFactory.getLogger(InventoryNavigationHandler.class);

    private boolean leftKeyWasPressed = false;
    private boolean rightKeyWasPressed = false;
    private boolean upKeyWasPressed = false;
    private boolean downKeyWasPressed = false;
    private boolean wKeyWasPressed = false;
    private boolean aKeyWasPressed = false;
    private boolean sKeyWasPressed = false;
    private boolean dKeyWasPressed = false;

    public void handleNavigation(InventoryState inventoryState, Inventory inventory) {
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
            handleBagNavigation(inventoryState, inventory, leftPressed, rightPressed,
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

    private void handleBagNavigation(InventoryState inventoryState, Inventory inventory,
                                     boolean leftPressed, boolean rightPressed,
                                     boolean upPressed, boolean downPressed,
                                     boolean wPressed, boolean aPressed,
                                     boolean sPressed, boolean dPressed) {
        int maxSlots = inventory.maxSlots;
        int slotsX = InventoryUIConfig.SLOTS_PER_ROW;
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
}