package by.losik.components.core;

import com.artemis.Component;

public class CarryingCapacity extends Component {
    public float maxWeight = 50f;
    public float currentWeight = 0f;

    public boolean canCarry(float additionalWeight) {
        return currentWeight + additionalWeight <= maxWeight;
    }

    public void addWeight(float weight) {
        currentWeight += weight;
        currentWeight = Math.max(0, currentWeight);
    }

    public CarryingCapacity() {}
    public CarryingCapacity(float maxWeight, float currentWeight) {
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
    }
}