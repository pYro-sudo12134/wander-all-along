package by.losik.components.world;

import by.losik.components.core.ID;
import com.artemis.Component;

public class Container extends Component {
    public ID containerId;
    public int capacity = 10;
    public boolean isLocked = false;
    public String lockCode = "";

    public Container() {}
    public Container(ID containerId, int capacity, String lockCode) {
        this.containerId = containerId;
        this.capacity = capacity;
        this.lockCode = lockCode != null ? lockCode : "";
    }
}