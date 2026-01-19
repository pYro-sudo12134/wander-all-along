package by.losik.systems.inventory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BackgroundRenderer {
    private final ShapeRenderer shapeRenderer;

    public BackgroundRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public void render(InventoryRenderContext context) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(InventoryUIConfig.INVENTORY_BG_COLOR);
        shapeRenderer.rect(context.inventoryX, context.inventoryY,
                context.inventoryWidth, context.inventoryHeight);

        shapeRenderer.setColor(InventoryUIConfig.PANEL_BG_COLOR);
        shapeRenderer.rect(context.infoPanelX, context.inventoryY,
                InventoryUIConfig.INFO_PANEL_WIDTH, context.inventoryHeight);

        shapeRenderer.rect(context.armorPanelX, context.inventoryY,
                InventoryUIConfig.ARMOR_PANEL_WIDTH, context.inventoryHeight);

        shapeRenderer.end();
    }
}