package slavsquatsuperstar.demos.spacegame.combat;

import mayonez.*;
import mayonez.math.*;

/**
 * Allows enemy ships to fire different weapons.
 *
 * @author SlavSquatSuperstar
 */
public class EnemyFireController extends FireProjectile {

    private int weaponChoice;
    private boolean isFiring;
    private int shotsLeft;

    public EnemyFireController(float cooldown) {
        super(cooldown);
    }

    @Override
    public void start() {
        isFiring = false;
        setRandomWeaponChoice();
        var type = ProjectilePrefabs.getProjectileType(weaponChoice);
        if (type != null) setCooldown(type.getFireCooldown());
    }

    @Override
    protected boolean readyToFire() {
        if (isReloaded() && isFiring) {
            // Decide to stop shooting
            if (--shotsLeft > 0) isFiring = false;
        } else {
            // Decide to start shooting
            isFiring = Random.randomPercent(0.01f);
            if (isFiring) shotsLeft = Random.randomInt(1, 5);
            // Switch weapon types
            if (Random.randomBoolean()) setRandomWeaponChoice();
        }
        return isFiring;
    }

    @Override
    protected GameObject spawnProjectile() {
        return ProjectilePrefabs.createPrefab(weaponChoice, gameObject);
    }

    private void setRandomWeaponChoice() {
        weaponChoice = Random.randomInt(0, ProjectilePrefabs.count() - 1);
    }

}
