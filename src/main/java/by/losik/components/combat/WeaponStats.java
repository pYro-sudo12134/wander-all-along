package by.losik.components.combat;

import by.losik.components.core.ID;
import com.artemis.Component;

public class WeaponStats extends Component {
    public WeaponType weaponType;
    public DamageType damageType;
    public float baseDamage;
    public float attackSpeed;
    public float range;

    public WeaponStats() {}

    public WeaponStats(WeaponType weaponType, DamageType damageType,
                       float baseDamage, float attackSpeed, float range) {
        this.weaponType = weaponType;
        this.damageType = damageType;
        this.baseDamage = baseDamage;
        this.attackSpeed = attackSpeed;
        this.range = range;
    }

    public WeaponStats(ID weaponTypeId, ID damageTypeId,
                       float baseDamage, float attackSpeed, float range) {
        this.weaponType = WeaponType.valueOf(weaponTypeId.id.toUpperCase());
        this.damageType = DamageType.valueOf(damageTypeId.id.toUpperCase());
        this.baseDamage = baseDamage;
        this.attackSpeed = attackSpeed;
        this.range = range;
    }
}