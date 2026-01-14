package by.losik.components.world;

public enum GrowthStage {
    SEED("seed"),
    SPROUT("sprout"),
    GROWING("growing"),
    MATURE("mature"),
    ROTTEN("rotten");

    private final String growthStage;
    GrowthStage(String growthStage) {
        this.growthStage = growthStage;
    }

    public String getGrowthStage() {
        return growthStage;
    }
}
