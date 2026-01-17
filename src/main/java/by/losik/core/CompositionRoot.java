package by.losik.core;

import by.losik.providers.factories.*;
import by.losik.providers.flyweight.SpriteFlyweight;
import by.losik.providers.flyweight.TextureFlyweight;
import by.losik.providers.config.*;
import by.losik.systems.*;
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
    }

    @Provides
    @Singleton
    public World provideWorld(
            MovementSystem movementSystem,
            PlayerInputSystem playerInputSystem,
            BoundsSystem boundsSystem,
            CameraSystem cameraSystem) {

        WorldConfigurationBuilder config = new WorldConfigurationBuilder();
        config.with(movementSystem)
                .with(playerInputSystem)
                .with(boundsSystem)
                .with(cameraSystem);

        // Можно добавить другие системы позже
        // .with(new CombatSystem())
        // .with(new HealthSystem())
        // .with(new NeedsSystem())
        // .with(new InventorySystem())
        // .with(new TimeSystem())
        // .with(new UIRenderSystem());

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