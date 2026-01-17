package by.losik.providers.factories;

import by.losik.components.survival.Disease;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DiseaseFactory {

    private final World world;

    @Inject
    public DiseaseFactory(World world) {
        this.world = world;
    }

    public Disease createDisease() {
        return new Disease();
    }
    public Disease commonCold() {
        Disease disease = new Disease();
        disease.hasCold = true;
        disease.coldSeverity = 0.3f;
        disease.hasFever = true;
        disease.feverTemperature = 37.5f;
        return disease;
    }

    public Disease influenza() {
        Disease disease = new Disease();
        disease.hasCold = true;
        disease.coldSeverity = 0.7f;
        disease.hasFever = true;
        disease.feverTemperature = 39.0f;
        disease.painLevel = 4.0f;
        return disease;
    }

    public Disease pneumonia() {
        Disease disease = new Disease();
        disease.hasCold = true;
        disease.coldSeverity = 0.8f;
        disease.hasFever = true;
        disease.feverTemperature = 39.5f;
        disease.hasInfection = true;
        disease.infectionProgress = 0.6f;
        disease.painLevel = 6.0f;
        return disease;
    }

    public Disease foodPoisoning() {
        Disease disease = new Disease();
        disease.hasFoodPoisoning = true;
        disease.painLevel = 7.0f;
        return disease;
    }

    public Disease brokenArm() {
        Disease disease = new Disease();
        disease.hasBrokenBone = true;
        disease.painLevel = 8.5f;
        return disease;
    }

    public Disease infectedWound() {
        Disease disease = new Disease();
        disease.hasInfection = true;
        disease.infectionProgress = 0.4f;
        disease.hasFever = true;
        disease.feverTemperature = 38.0f;
        disease.painLevel = 5.0f;
        return disease;
    }

    public void applyDisease(int entityId, Disease disease) {
        world.edit(entityId).add(disease);
    }

    public void giveCommonCold(int entityId) {
        world.edit(entityId).add(commonCold());
    }

    public void giveFoodPoisoning(int entityId) {
        world.edit(entityId).add(foodPoisoning());
    }
}