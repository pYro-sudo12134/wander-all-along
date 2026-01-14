package by.losik.components.survival;

import com.artemis.Component;

public class Disease extends Component {
    public boolean hasCold = false;
    public boolean hasFever = false;
    public boolean hasInfection = false;
    public boolean hasFoodPoisoning = false;
    public boolean hasBrokenBone = false;

    public float coldSeverity = 0f;    // 0-1
    public float feverTemperature = 36.6f;
    public float infectionProgress = 0f; // 0-1
    public float painLevel = 0f;        // 0-10

    public Disease() {}
}