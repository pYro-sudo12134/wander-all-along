package by.losik.components.items;

import com.artemis.Component;

public class StackData extends Component {
    public int maxStackSize = 64;
    public int currentCount = 1;

    public StackData() {}

    public StackData(int maxStackSize, int currentCount) {
        this.maxStackSize = maxStackSize;
        this.currentCount = currentCount;
    }
}