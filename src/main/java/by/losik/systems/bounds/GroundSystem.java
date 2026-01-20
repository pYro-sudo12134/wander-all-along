package by.losik.systems.bounds;

import by.losik.components.core.Position;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.google.inject.Singleton;

@Singleton
public class GroundSystem extends IteratingSystem {

    protected ComponentMapper<Position> mPosition;

    private Model groundModel;
    private ModelInstance groundInstance;
    private boolean resourcesInitialized = false;

    private static final float GROUND_SIZE = 100f;
    private static final float GROUND_HEIGHT = -0.5f;
    private static final float GRID_SIZE = 10f;

    public GroundSystem() {
        super(Aspect.all(Position.class));
    }

    @Override
    protected void initialize() {
        mPosition = world.getMapper(Position.class);
    }

    private void initResources() {
        if (resourcesInitialized || Gdx.gl20 == null) {
            return;
        }

        try {
            ModelBuilder modelBuilder = new ModelBuilder();

            Material groundMaterial = new Material();
            groundMaterial.set(ColorAttribute.createDiffuse(0.3f, 0.5f, 0.2f, 1f));
            groundMaterial.set(ColorAttribute.createSpecular(0.1f, 0.1f, 0.1f, 1f));

            modelBuilder.begin();

            MeshPartBuilder meshBuilder = modelBuilder.part("ground", GL20.GL_TRIANGLES,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, groundMaterial);

            float halfSize = GROUND_SIZE / 2;

            meshBuilder.rect(
                    -halfSize, GROUND_HEIGHT, -halfSize,
                    halfSize, GROUND_HEIGHT, -halfSize,
                    halfSize, GROUND_HEIGHT, halfSize,
                    -halfSize, GROUND_HEIGHT, halfSize,
                    0, 1, 0
            );

            Material gridMaterial = new Material();
            gridMaterial.set(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 0.3f));

            MeshPartBuilder gridBuilder = modelBuilder.part("grid", GL20.GL_LINES,
                    VertexAttributes.Usage.Position, gridMaterial);

            for (float x = -halfSize; x <= halfSize; x += GRID_SIZE) {
                gridBuilder.line(x, GROUND_HEIGHT + 0.01f, -halfSize,
                        x, GROUND_HEIGHT + 0.01f, halfSize);
            }
            for (float z = -halfSize; z <= halfSize; z += GRID_SIZE) {
                gridBuilder.line(-halfSize, GROUND_HEIGHT + 0.01f, z,
                        halfSize, GROUND_HEIGHT + 0.01f, z);
            }

            groundModel = modelBuilder.end();
            groundInstance = new ModelInstance(groundModel);
            resourcesInitialized = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void begin() {
        initResources();
    }

    @Override
    protected void process(int entityId) {
    }

    public ModelInstance getGroundInstance() {
        return groundInstance;
    }

    public boolean isInitialized() {
        return resourcesInitialized;
    }

    @Override
    protected void dispose() {
        if (groundModel != null) {
            groundModel.dispose();
        }
        resourcesInitialized = false;
    }
}