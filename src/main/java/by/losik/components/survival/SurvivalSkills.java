package by.losik.components.survival;

import com.artemis.Component;

public class SurvivalSkills extends Component {

    public static final float[] EXP_TABLE = {
        0f,
        100f,
        250f,
        500f,
        900f,
        1400f,
        2000f,
        2700f,
        3500f,
        4400f,
        5400f,
        6500f,
        7700f,
        9000f,
        10400f,
        11900f,
        13500f,
        15200f,
        17000f,
        18900f,
        20900f
    };
    public SurvivalSkillType survivalSkill;
    public float progress;

    public SurvivalSkills() {}
    public SurvivalSkills(SurvivalSkillType type, float progress) {
        this.survivalSkill = type;
        this.progress = progress;
    }
}