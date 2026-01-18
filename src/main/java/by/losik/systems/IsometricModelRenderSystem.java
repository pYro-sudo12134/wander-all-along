package by.losik.systems;

import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.Model3D;
import by.losik.components.core.Position;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class IsometricModelRenderSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(IsometricModelRenderSystem.class);

    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Model3D> mModel3D;
    protected ComponentMapper<Creature> mCreature;

    private ModelBatch modelBatch;
    private Environment environment;
    private boolean initialized = false;

    private final ObjectMap<String, com.badlogic.gdx.graphics.g3d.Model> modelCache = new ObjectMap<>();
    private final ObjectMap<Integer, ModelInstance> instanceCache = new ObjectMap<>();

    private CameraSystem cameraSystem;
    private GroundSystem groundSystem;

    public IsometricModelRenderSystem() {
        super(Aspect.all(Position.class, Model3D.class));
    }

    @Override
    protected void initialize() {
        mPosition = world.getMapper(Position.class);
        mModel3D = world.getMapper(Model3D.class);
        mCreature = world.getMapper(Creature.class);

        cameraSystem = world.getSystem(CameraSystem.class);
        groundSystem = world.getSystem(GroundSystem.class);
    }

    private void initResources() {
        if (initialized) return;

        if (Gdx.gl20 == null) {
            logger.warn("Gdx.gl20 is null, cannot initialize graphics resources");
            return;
        }

        logger.info("Initializing IsometricModelRenderSystem resources...");

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        initialized = true;
        logger.info("IsometricModelRenderSystem resources initialized successfully");
    }

    @Override
    protected void begin() {
        initResources();

        if (!initialized || Gdx.gl20 == null) {
            return;
        }

        if (cameraSystem == null || !cameraSystem.isCameraInitialized()) {
            logger.warn("CameraSystem not ready, skipping render");
            return;
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    }

    @Override
    protected void process(int entityId) {
        if (!initialized) return;

        Position position = mPosition.get(entityId);
        Model3D model3D = mModel3D.get(entityId);

        if (position == null || model3D == null) {
            return;
        }

        if (mCreature.has(entityId)) {
            Creature creature = mCreature.get(entityId);
            if (creature.type == CreatureType.PLAYER) {
                logger.debug("Rendering player entity {} at position ({}, {}, {})",
                        entityId,
                        position.value.x, position.value.y, position.value.z);
            }
        }

        if (model3D.model == null && model3D.modelPath != null) {
            loadModel(model3D);
        }

        if (model3D.model != null) {
            ModelInstance instance = getOrCreateInstance(entityId, model3D);
            updateInstanceTransform(instance, position, model3D, entityId);
        }
    }

    private void updateInstanceTransform(ModelInstance instance, Position position, Model3D model3D, int entityId) {
        com.badlogic.gdx.math.Matrix4 transform = new com.badlogic.gdx.math.Matrix4();
        transform.idt();
        transform.translate(position.value.x, position.value.y, position.value.z);

        if (mCreature.has(entityId) && Math.abs(position.rotation) > 0.001f) {
            float degrees = (float) Math.toDegrees(position.rotation);
            transform.rotate(0, 1, 0, degrees);
        }

        float scale = model3D.scale > 0 ? model3D.scale : 1.0f;
        transform.scale(scale, scale, scale);

        instance.transform.set(transform);
    }

    @Override
    protected void end() {
        if (!initialized || modelBatch == null) {
            return;
        }

        PerspectiveCamera camera = cameraSystem.getPerspectiveCamera();
        if (camera == null) {
            logger.warn("No camera available for rendering");
            return;
        }

        modelBatch.begin(camera);

        if (groundSystem != null && groundSystem.isInitialized()) {
            modelBatch.render(groundSystem.getGroundInstance(), environment);
        }

        for (ObjectMap.Entry<Integer, ModelInstance> entry : instanceCache.entries()) {
            modelBatch.render(entry.value, environment);
        }

        modelBatch.end();
    }

    private void loadModel(Model3D model3D) {
        try {
            if (modelCache.containsKey(model3D.modelPath)) {
                model3D.model = modelCache.get(model3D.modelPath);
            } else {
                logger.info("Loading model: {}", model3D.modelPath);

                com.badlogic.gdx.assets.AssetManager assets = new com.badlogic.gdx.assets.AssetManager();
                assets.load(model3D.modelPath, com.badlogic.gdx.graphics.g3d.Model.class);
                assets.finishLoading();

                if (assets.isLoaded(model3D.modelPath)) {
                    model3D.model = assets.get(model3D.modelPath, com.badlogic.gdx.graphics.g3d.Model.class);
                    modelCache.put(model3D.modelPath, model3D.model);
                    logger.info("Model loaded successfully: {}", model3D.modelPath);
                } else {
                    logger.error("Failed to load model: {}", model3D.modelPath);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load model: {}", model3D.modelPath, e);
        }
    }

    private ModelInstance getOrCreateInstance(int entityId, Model3D model3D) {
        if (!instanceCache.containsKey(entityId)) {
            ModelInstance instance = new ModelInstance(model3D.model);
            instanceCache.put(entityId, instance);
            return instance;
        }
        return instanceCache.get(entityId);
    }

    @Override
    protected void dispose() {
        logger.info("Disposing IsometricModelRenderSystem resources");

        if (modelBatch != null) {
            modelBatch.dispose();
            modelBatch = null;
        }

        for (com.badlogic.gdx.graphics.g3d.Model model : modelCache.values()) {
            if (model != null) {
                model.dispose();
            }
        }
        modelCache.clear();
        instanceCache.clear();

        initialized = false;
    }

    public void resize(int width, int height) {
        if (cameraSystem != null) {
            cameraSystem.resize(width, height);
        }
    }
}