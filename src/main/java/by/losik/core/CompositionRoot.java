package by.losik.core;

import by.losik.providers.flyweight.TextureFlyweight;
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
import by.losik.providers.factories.FoodItemFactory;
import by.losik.providers.factories.ItemFactory;
import by.losik.providers.factories.MaterialItemFactory;
import by.losik.providers.factories.ResourceItemFactory;
import by.losik.providers.factories.StoneFactory;
import by.losik.providers.factories.ToolItemFactory;
import by.losik.providers.factories.TreeFactory;
import by.losik.providers.factories.WeaponFactory;
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
    }

    @Provides
    @Singleton
    public World provideWorld() {
        WorldConfigurationBuilder config = new WorldConfigurationBuilder();
        // Системы можно добавлять постепенно
        // .with(new MovementSystem())
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

    @Provides
    @Singleton
    public CreatureFactory provideCreatureFactory(World world, CreatureConfig config) {
        return new CreatureFactory(world, config);
    }

    @Provides
    @Singleton
    public ItemFactory provideItemFactory(World world) {
        return new ItemFactory(world);
    }

    @Provides
    @Singleton
    public ResourceItemFactory provideResourceItemFactory(World world, ItemConfig itemConfig) {
        return new ResourceItemFactory(world, itemConfig);
    }

    @Provides
    @Singleton
    public FoodItemFactory provideFoodItemFactory(World world, ItemConfig itemConfig) {
        return new FoodItemFactory(world, itemConfig);
    }

    @Provides
    @Singleton
    public ConsumableItemFactory provideConsumableItemFactory(World world, ItemConfig itemConfig) {
        return new ConsumableItemFactory(world, itemConfig);
    }

    @Provides
    @Singleton
    public MaterialItemFactory provideMaterialItemFactory(World world, ItemConfig itemConfig) {
        return new MaterialItemFactory(world, itemConfig);
    }

    @Provides
    @Singleton
    public ToolItemFactory provideToolItemFactory(World world, ItemConfig itemConfig) {
        return new ToolItemFactory(world, itemConfig);
    }

    @Provides
    @Singleton
    public WeaponFactory provideWeaponFactory(World world, WeaponConfig weaponConfig) {
        return new WeaponFactory(world, weaponConfig);
    }

    @Provides
    @Singleton
    public ArmorFactory provideArmorFactory(World world, ArmorConfig armorConfig) {
        return new ArmorFactory(world, armorConfig);
    }

    @Provides
    @Singleton
    public ContainerItemFactory provideContainerFactory(World world) {
        return new ContainerItemFactory(world);
    }

    // =========== ФАБРИКИ МИРА ===========

    @Provides
    @Singleton
    public TreeFactory provideTreeFactory(World world) {
        return new TreeFactory(world);
    }

    @Provides
    @Singleton
    public StoneFactory provideStoneFactory(World world) {
        return new StoneFactory(world);
    }

    @Provides
    @Singleton
    public CropFactory provideCropFactory(World world) {
        return new CropFactory(world);
    }
}