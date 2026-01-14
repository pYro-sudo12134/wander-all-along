package by.losik.components.core;

import com.artemis.Component;

public class ID extends Component {
    public String id;
    public String name;

    public ID() {}

    public ID(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ID objID)) return false;
        return objID.id.equals(this.id) && objID.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return (31 * id.hashCode()) ^ name.hashCode();
    }

    public static ID of(String id, String name) {
        return new ID(id, name);
    }

    public static ID of(String id) {
        return new ID(id, id);
    }

    public boolean matches(String id) {
        return this.id.equals(id);
    }

    public String toString() {
        return name + " (" + id + ")";
    }
}