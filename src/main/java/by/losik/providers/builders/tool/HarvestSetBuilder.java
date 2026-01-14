package by.losik.providers.builders.tool;

import by.losik.components.world.Building;
import by.losik.components.world.Crop;
import by.losik.components.world.OreComponent;
import by.losik.components.world.ResourceComponent;
import by.losik.components.world.StoneComponent;
import by.losik.components.world.WoodComponent;
import com.artemis.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HarvestSetBuilder {

    public static Set<Class<? extends Component>> createWoodHarvestSet() {
        return new HashSet<>(Arrays.asList(
                WoodComponent.class,
                Crop.class
        ));
    }

    public static Set<Class<? extends Component>> createMiningHarvestSet() {
        return new HashSet<>(Arrays.asList(
                OreComponent.class,
                StoneComponent.class
        ));
    }

    public static Set<Class<? extends Component>> createUniversalHarvestSet() {
        Set<Class<? extends Component>> set = new HashSet<>();
        set.addAll(createWoodHarvestSet());
        set.addAll(createMiningHarvestSet());
        set.add(Building.class);
        set.add(ResourceComponent.class);
        return set;
    }

    @SafeVarargs
    public static Set<Class<? extends Component>> fromComponentClasses(Class<? extends Component>... components) {
        return new HashSet<>(Arrays.asList(components));
    }
}