package by.losik.systems.inventory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ArmorSlotRenderer {
    private final ShapeRenderer shapeRenderer;

    public ArmorSlotRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public void render(InventoryRenderContext context) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < context.armorSlotsCount; i++) {
            int renderIndex = context.getRenderIndexForArmor(i);
            int armorY = context.getArmorSlotY(i);

            shapeRenderer.setColor(InventoryUIConfig.ARMOR_SLOT_NORMAL_COLOR);
            shapeRenderer.rect(context.armorPanelX + InventoryUIConfig.ARMOR_SLOT_OFFSET_X,
                    armorY, context.armorSlotSize, context.armorSlotSize);

            if (context.state.inArmorSelection && renderIndex == context.state.selectedArmorSlot) {
                shapeRenderer.setColor(InventoryUIConfig.ARMOR_SLOT_SELECTED_COLOR);
                shapeRenderer.rect(context.armorPanelX + InventoryUIConfig.ARMOR_SLOT_OFFSET_X - InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS,
                        armorY - InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS,
                        context.armorSlotSize + InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS * 2,
                        context.armorSlotSize + InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS * 2);
            } else {
                shapeRenderer.setColor(InventoryUIConfig.ARMOR_SLOT_BORDER_COLOR);
                shapeRenderer.rect(context.armorPanelX + InventoryUIConfig.ARMOR_SLOT_OFFSET_X - InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS,
                        armorY - InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS,
                        context.armorSlotSize + InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS * 2,
                        context.armorSlotSize + InventoryUIConfig.ARMOR_SELECTION_BORDER_THICKNESS * 2);
            }
        }

        shapeRenderer.end();
    }
}