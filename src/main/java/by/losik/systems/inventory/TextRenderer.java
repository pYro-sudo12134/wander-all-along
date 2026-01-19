package by.losik.systems.inventory;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.core.ID;
import by.losik.systems.inventory.InventoryRenderContext;
import by.losik.systems.inventory.InventoryUIConfig;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextRenderer {
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private ComponentMapper<ID> mID;

    public TextRenderer(SpriteBatch spriteBatch, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.font = font;
    }

    public void setIDMapper(ComponentMapper<ID> mID) {
        this.mID = mID;
    }

    public void render(InventoryRenderContext context) {
        spriteBatch.begin();

        renderTitle(context);
        renderSelectionHeader(context);
        renderInventorySlotNumbers(context);
        renderArmorSlotTexts(context);
        renderControls(context);

        spriteBatch.end();
    }

    private void renderTitle(InventoryRenderContext context) {
        font.setColor(InventoryUIConfig.TEXT_TITLE_COLOR);
        font.getData().setScale(InventoryUIConfig.FONT_TITLE_SCALE);

        String title = mID.has(context.entityId)
                ? mID.get(context.entityId).name + "'s Inventory"
                : "Inventory";

        font.draw(spriteBatch, title,
                context.infoPanelX + InventoryUIConfig.PADDING * 2,
                context.inventoryY + context.inventoryHeight - InventoryUIConfig.TITLE_OFFSET_Y);
    }

    private void renderSelectionHeader(InventoryRenderContext context) {
        font.getData().setScale(InventoryUIConfig.FONT_HEADER_SCALE);

        if (context.state.inArmorSelection) {
            font.setColor(InventoryUIConfig.TEXT_ARMOR_SELECTED_COLOR);
            font.draw(spriteBatch, "ARMOR SELECTION",
                    context.infoPanelX + InventoryUIConfig.PADDING * 2,
                    context.inventoryY + context.inventoryHeight - InventoryUIConfig.HEADER_OFFSET_Y);
        } else {
            font.setColor(InventoryUIConfig.TEXT_INVENTORY_SELECTED_COLOR);
            font.draw(spriteBatch, "INVENTORY SELECTION",
                    context.infoPanelX + InventoryUIConfig.PADDING * 2,
                    context.inventoryY + context.inventoryHeight - InventoryUIConfig.HEADER_OFFSET_Y);
            font.getData().setScale(InventoryUIConfig.FONT_NORMAL_SCALE);
        }
    }

    private void renderInventorySlotNumbers(InventoryRenderContext context) {
        font.getData().setScale(InventoryUIConfig.FONT_SMALL_SCALE);
        font.setColor(InventoryUIConfig.TEXT_SLOT_NUMBER_COLOR);

        for (int row = 0; row < context.slotsY; row++) {
            for (int col = 0; col < context.slotsX; col++) {
                int slotIndex = row * context.slotsX + col;
                if (slotIndex >= context.maxSlots) continue;

                int slotX = context.getSlotX(col);
                int slotY = context.getSlotY(row);

                font.draw(spriteBatch, String.valueOf(slotIndex + 1),
                        slotX + (float) InventoryUIConfig.PADDING / 2,
                        slotY + (float) (InventoryUIConfig.SLOT_SIZE - InventoryUIConfig.PADDING / 2));
            }
        }
    }

    private void renderArmorSlotTexts(InventoryRenderContext context) {
        for (int i = 0; i < context.armorSlotsCount; i++) {
            int renderIndex = context.getRenderIndexForArmor(i);
            int armorY = context.getArmorSlotY(i);
            ArmorSlot slot = context.getArmorSlotForDisplayIndex(i);

            String armorName = slot.name();
            if (armorName.length() > InventoryUIConfig.ARMOR_NAME_MAX_LENGTH) {
                armorName = armorName.substring(0, InventoryUIConfig.ARMOR_NAME_MAX_LENGTH) + ".";
            }

            if (context.state.inArmorSelection && renderIndex == context.state.selectedArmorSlot) {
                font.setColor(InventoryUIConfig.TEXT_ARMOR_SELECTED_COLOR);
                font.getData().setScale(InventoryUIConfig.FONT_NORMAL_SCALE);
                font.draw(spriteBatch, "> " + armorName,
                        context.armorPanelX + InventoryUIConfig.ARMOR_TEXT_OFFSET_X,
                        armorY + context.armorSlotSize);
            } else {
                font.setColor(InventoryUIConfig.TEXT_NORMAL_COLOR);
                font.getData().setScale(InventoryUIConfig.FONT_SMALL_SCALE);
                font.draw(spriteBatch, armorName,
                        context.armorPanelX + InventoryUIConfig.ARMOR_TEXT_OFFSET_X,
                        armorY + context.armorSlotSize);
            }

            font.getData().setScale(InventoryUIConfig.FONT_CONTROLS_SCALE);
            font.setColor(InventoryUIConfig.TEXT_DESCRIPTION_COLOR);
            font.draw(spriteBatch, "(" + slot.getBodyPart() + ")",
                    context.armorPanelX + InventoryUIConfig.ARMOR_TEXT_OFFSET_X,
                    armorY + context.armorSlotSize - InventoryUIConfig.ARMOR_TEXT_VERTICAL_SPACING);
        }
    }

    private void renderControls(InventoryRenderContext context) {
        font.getData().setScale(InventoryUIConfig.FONT_SMALL_SCALE);
        font.setColor(InventoryUIConfig.TEXT_CONTROLS_COLOR);

        int controlsY = context.inventoryY + context.inventoryHeight - InventoryUIConfig.CONTROL_START_OFFSET_Y;

        font.draw(spriteBatch, "Controls:",
                context.infoPanelX + InventoryUIConfig.PADDING * 2, controlsY);

        font.getData().setScale(InventoryUIConfig.FONT_CONTROLS_SCALE);

        String[] controls = {
                "TAB - Switch mode",
                "Arrows/WASD - Navigate",
                "E - Use/Equip",
                "Q - Drop/Unequip",
                "Enter - Examine",
                "I/Esc - Toggle/Close"
        };

        for (int i = 0; i < controls.length; i++) {
            font.draw(spriteBatch, controls[i],
                    context.infoPanelX + InventoryUIConfig.PADDING * 2 + InventoryUIConfig.CONTROL_INDENT,
                    controlsY - (i + 1) * InventoryUIConfig.CONTROL_LINE_SPACING);
        }

        if (context.state.inArmorSelection) {
            font.setColor(InventoryUIConfig.TEXT_CONTROLS_HIGHLIGHT_COLOR);
            font.draw(spriteBatch, "TAB - Back to inventory",
                    context.infoPanelX + InventoryUIConfig.PADDING * 2 + InventoryUIConfig.CONTROL_INDENT,
                    controlsY - (controls.length + 1) * InventoryUIConfig.CONTROL_LINE_SPACING);
        }
    }
}