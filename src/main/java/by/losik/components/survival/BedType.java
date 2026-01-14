package by.losik.components.survival;

public enum BedType {
    GROUND("ground"),
    BED("bed"),
    BAG("bag");

    private final String bedType;

    BedType(String bedType) {
        this.bedType = bedType;
    }

    public String getBedType() {
        return bedType;
    }
}
