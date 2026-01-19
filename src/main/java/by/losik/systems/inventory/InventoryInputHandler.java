package by.losik.systems.inventory;

import by.losik.components.core.InventoryState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryInputHandler {
    private static final Logger logger = LoggerFactory.getLogger(InventoryInputHandler.class);

    private final Int2BooleanArrayMap iKeyWasPressedMap = new Int2BooleanArrayMap();
    private final Int2BooleanArrayMap tabKeyWasPressedMap = new Int2BooleanArrayMap();
    public InventoryInputHandler() {
    }

    public void handleInput(InventoryState inventoryState) {
        boolean iPressed = Gdx.input.isKeyPressed(Input.Keys.I);
        boolean escPressed = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
        boolean tabPressed = Gdx.input.isKeyPressed(Input.Keys.TAB);

        if (iPressed && !iKeyWasPressedMap.getOrDefault(Input.Keys.I, false)) {
            inventoryState.isOpen = !inventoryState.isOpen;
            if (inventoryState.isOpen) {
                inventoryState.selectedSlot = 0;
                inventoryState.selectedArmorSlot = 0;
                inventoryState.inArmorSelection = false;
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
}