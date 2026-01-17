package by.losik.providers.builders.survival;

import by.losik.providers.builders.base.EntityBuilder;
import com.artemis.World;
import by.losik.components.survival.Disease;

public class DiseaseBuilder extends EntityBuilder {

    private final Disease disease;

    public DiseaseBuilder(World world) {
        super(world);
        this.disease = new Disease();
    }

    public DiseaseBuilder withCold(float severity) {
        disease.hasCold = true;
        disease.coldSeverity = Math.max(0, Math.min(1, severity));
        return this;
    }

    public DiseaseBuilder withFever(float temperature) {
        disease.hasFever = true;
        disease.feverTemperature = Math.max(36.0f, Math.min(42.0f, temperature));
        return this;
    }

    public DiseaseBuilder withInfection(float progress) {
        disease.hasInfection = true;
        disease.infectionProgress = Math.max(0, Math.min(1, progress));
        return this;
    }

    public DiseaseBuilder withFoodPoisoning() {
        disease.hasFoodPoisoning = true;
        return this;
    }

    public DiseaseBuilder withBrokenBone(float painLevel) {
        disease.hasBrokenBone = true;
        disease.painLevel = Math.max(0, Math.min(10, painLevel));
        return this;
    }

    public DiseaseBuilder withCommonCold() {
        return withCold(0.3f)
                .withFever(37.5f);
    }

    public DiseaseBuilder withPneumonia() {
        return withCold(0.8f)
                .withFever(39.0f)
                .withInfection(0.6f);
    }

    public DiseaseBuilder withFoodPoisoningSevere() {
        return withFoodPoisoning()
                .withPain(7.0f);
    }

    public DiseaseBuilder withInfectedWound() {
        return withInfection(0.4f)
                .withPain(5.0f)
                .withFever(38.0f);
    }

    public DiseaseBuilder withPain(float painLevel) {
        disease.painLevel = Math.max(0, Math.min(10, painLevel));
        return this;
    }

    @Override
    public int build() {
        and().edit(super.build()).add(disease);
        return super.build();
    }

    public void applyTo(int entityId) {
        and().edit(entityId).add(disease);
    }

    public Disease getDisease() {
        return disease;
    }
}