package by.losik.systems.collisions;

import by.losik.components.core.Bounds;
import com.artemis.utils.IntBag;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpatialGrid {
    private float cellSize;
    private final Map<String, Set<Integer>> grid = new HashMap<>();

    public SpatialGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    public void clear() {
        grid.clear();
    }

    public void insert(int entityId, Vector3f position, float width, float height, float depth) {
        // Рассчитываем bounding box сущности в координатах grid (все три измерения)
        int minX = (int) Math.floor((position.x - width/2) / cellSize);
        int maxX = (int) Math.floor((position.x + width/2) / cellSize);
        int minY = (int) Math.floor(position.y / cellSize);
        int maxY = (int) Math.floor((position.y + height) / cellSize);
        int minZ = (int) Math.floor((position.z - depth/2) / cellSize);
        int maxZ = (int) Math.floor((position.z + depth/2) / cellSize);

        // Добавляем сущность во все ячейки, которые она занимает
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    String cellKey = x + "," + y + "," + z;
                    grid.computeIfAbsent(cellKey, k -> new HashSet<>()).add(entityId);
                }
            }
        }
    }

    public IntBag getPotentialCollisions(int entityId, Vector3f position, Bounds bounds) {
        IntBag result = new IntBag();
        Set<Integer> potential = new HashSet<>();

        // Получаем ячейки, которые занимает эта сущность
        int minX = (int) Math.floor((position.x - bounds.getHalfWidth()) / cellSize);
        int maxX = (int) Math.floor((position.x + bounds.getHalfWidth()) / cellSize);
        int minY = (int) Math.floor(position.y / cellSize);
        int maxY = (int) Math.floor((position.y + bounds.getHeight()) / cellSize);
        int minZ = (int) Math.floor((position.z - bounds.getHalfDepth()) / cellSize);
        int maxZ = (int) Math.floor((position.z + bounds.getHalfDepth()) / cellSize);

        // Собираем все сущности из соседних ячеек (включая вертикальные)
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int y = minY - 1; y <= maxY + 1; y++) {
                for (int z = minZ - 1; z <= maxZ + 1; z++) {
                    String cellKey = x + "," + y + "," + z;
                    Set<Integer> cellEntities = grid.get(cellKey);
                    if (cellEntities != null) {
                        potential.addAll(cellEntities);
                    }
                }
            }
        }

        // Конвертируем в IntBag
        for (int id : potential) {
            result.add(id);
        }

        return result;
    }

    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
        clear();
    }
}