package by.losik.core;

import by.losik.providers.config.ArmorConfig;
import by.losik.providers.config.ConfigManager;
import by.losik.providers.config.CreatureConfig;
import by.losik.providers.config.ItemConfig;
import by.losik.providers.config.WeaponConfig;
import by.losik.providers.factories.ArmorFactory;
import by.losik.providers.factories.ConsumableItemFactory;
import by.losik.providers.factories.ContainerItemFactory;
import by.losik.providers.factories.CreatureFactory;
import by.losik.providers.factories.CropFactory;
import by.losik.providers.factories.DiseaseFactory;
import by.losik.providers.factories.FoodItemFactory;
import by.losik.providers.factories.ItemFactory;
import by.losik.providers.factories.MaterialItemFactory;
import by.losik.providers.factories.ResourceItemFactory;
import by.losik.providers.factories.StoneFactory;
import by.losik.providers.factories.ToolItemFactory;
import by.losik.providers.factories.TreeFactory;
import by.losik.providers.factories.WeaponFactory;
import by.losik.providers.flyweight.ModelFlyweight;
import by.losik.providers.flyweight.SpriteFlyweight;
import by.losik.providers.flyweight.TextureFlyweight;
import by.losik.systems.bounds.BoundsSystem;
import by.losik.systems.camera.CameraSystem;
import by.losik.systems.camera.FollowTargetSystem;
import by.losik.systems.collisions.CollisionSystem;
import by.losik.systems.movement.GravitySystem;
import by.losik.systems.bounds.GroundSystem;
import by.losik.systems.inventory.InventorySystem;
import by.losik.systems.render.IsometricModelRenderSystem;
import by.losik.systems.movement.MovementSystem;
import by.losik.systems.movement.PlayerInputSystem;
import by.losik.systems.time.TimeSystem;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CompositionRoot extends AbstractModule {

    @Override
    public void configure() {
        bind(CreatureConfig.class).in(Singleton.class);
        bind(ArmorConfig.class).in(Singleton.class);
        bind(WeaponConfig.class).in(Singleton.class);
        bind(ItemConfig.class).in(Singleton.class);
        bind(ConfigManager.class).in(Singleton.class);
        bind(TextureFlyweight.class).in(Singleton.class);
        bind(SpriteFlyweight.class).in(Singleton.class);
        bind(MovementSystem.class).in(Singleton.class);
        bind(PlayerInputSystem.class).in(Singleton.class);
        bind(BoundsSystem.class).in(Singleton.class);
        bind(CameraSystem.class).in(Singleton.class);
        bind(IsometricModelRenderSystem.class).in(Singleton.class);
        bind(ArmorFactory.class).in(Singleton.class);
        bind(ConsumableItemFactory.class).in(Singleton.class);
        bind(ContainerItemFactory.class).in(Singleton.class);
        bind(CreatureFactory.class).in(Singleton.class);
        bind(CropFactory.class).in(Singleton.class);
        bind(FoodItemFactory.class).in(Singleton.class);
        bind(ItemFactory.class).in(Singleton.class);
        bind(MaterialItemFactory.class).in(Singleton.class);
        bind(ResourceItemFactory.class).in(Singleton.class);
        bind(ToolItemFactory.class).in(Singleton.class);
        bind(WeaponFactory.class).in(Singleton.class);
        bind(StoneFactory.class).in(Singleton.class);
        bind(TreeFactory.class).in(Singleton.class);
        bind(DiseaseFactory.class).in(Singleton.class);
        bind(GameBootstrap.class).in(Singleton.class);
        bind(ModelFlyweight.class).in(Singleton.class);
        bind(GroundSystem.class).in(Singleton.class);
        bind(FollowTargetSystem.class).in(Singleton.class);
        bind(GravitySystem.class).in(Singleton.class);
        bind(InventorySystem.class).in(Singleton.class);
        bind(TimeSystem.class).in(Singleton.class);
        bind(CollisionSystem.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public World provideWorld(
            MovementSystem movementSystem,
            PlayerInputSystem playerInputSystem,
            BoundsSystem boundsSystem,
            CameraSystem cameraSystem,
            IsometricModelRenderSystem isometricRenderSystem,
            GroundSystem groundSystem,
            FollowTargetSystem followTargetSystem,
            GravitySystem gravitySystem,
            InventorySystem inventorySystem,
            TimeSystem timeSystem,
            CollisionSystem collisionSystem) {

        WorldConfigurationBuilder config = new WorldConfigurationBuilder();
        config.with(timeSystem)
                .with(playerInputSystem)
                .with(movementSystem)
                .with(gravitySystem)
                .with(boundsSystem)
                .with(collisionSystem)
                .with(followTargetSystem)
                .with(cameraSystem)
                .with(isometricRenderSystem)
                .with(groundSystem)
                .with(inventorySystem);

        // .with(new CombatSystem())
        // .with(new HealthSystem())
        // .with(new NeedsSystem())
        // .with(new InventorySystem())
        // .with(new TimeSystem())

        return new World(config.build());
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }
}