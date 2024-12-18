package slavsquatsuperstar.demos.spacegame.combat.projectiles;

import mayonez.*;
import mayonez.assets.*;
import mayonez.assets.text.*;
import mayonez.graphics.sprites.*;
import mayonez.math.*;
import mayonez.math.Random;
import mayonez.physics.colliders.*;
import mayonez.physics.dynamics.*;
import slavsquatsuperstar.demos.spacegame.objects.SpaceGameLayer;
import slavsquatsuperstar.demos.spacegame.objects.SpaceGameZIndex;

import java.util.*;

/**
 * Creates prefab projectiles that spaceships can fire.
 *
 * @author SlavSquatSuperstar
 */
public final class ProjectilePrefabs {

    // Constants
    private static final CSVFile PROJECTILE_DATA = Assets.getAsset(
            "assets/spacegame/data/projectiles.csv", CSVFile.class);
    private static final List<ProjectileType> PROJECTILE_TYPES;
    public static final int NUM_PROJECTILES;
    public static final SpriteSheet PROJECTILE_SPRITES;

    static {
        PROJECTILE_TYPES = readProjectileTypes();
        NUM_PROJECTILES = PROJECTILE_TYPES.size();
        PROJECTILE_SPRITES = Sprites.createSpriteSheet(
                "assets/spacegame/textures/combat/projectiles.png",
                16, 16, NUM_PROJECTILES, 0);
    }

    private ProjectilePrefabs() {
    }

    // Projectile Data Methods

    /**
     * Get the number of projectile types.
     *
     * @return the count
     */
    public static int count() {
        return Math.min(NUM_PROJECTILES, PROJECTILE_TYPES.size());
    }

    /**
     * Get the {@link ProjectileType} stored at the specified index.
     *
     * @param projectileIndex the index of the {@link ProjectileType}
     * @return the projectile type, or null if the index is invalid
     */
    public static ProjectileType getProjectileType(int projectileIndex) {
        if (projectileIndex < 0 || projectileIndex >= NUM_PROJECTILES) return null;
        return PROJECTILE_TYPES.get(projectileIndex);
    }

    private static List<ProjectileType> readProjectileTypes() {
        if (PROJECTILE_DATA == null) return Collections.emptyList();
        return PROJECTILE_DATA.readCSV().stream()
                .map(ProjectileType::new).toList();
    }

    // Create Prefab Methods

    /**
     * Creates a prefab {@link Projectile} object with the specified projectile type.
     *
     * @param projectileIndex the index of the {@link ProjectileType}
     * @param source          the object that fired the projectile
     * @param offsetPos       the projectile spawn position in relation to the source
     * @param offsetAngle     the projectile spawn angle in relation to the source
     * @return the projectile object, or null if the index is invalid
     */
    public static GameObject createPrefab(
            int projectileIndex, GameObject source, Vec2 offsetPos, float offsetAngle
    ) {
        var type = getProjectileType(projectileIndex);
        if (type == null) return null;

        var projXf = getProjectileTransform(type, source.transform, offsetPos, offsetAngle);
        return createProjectileObject(type, source, projXf);
    }

    /**
     * Create a projectile object to be fired.
     *
     * @param type   the projectile type
     * @param source the object that fired the projectile
     * @param projXf the projectile transform
     * @return the projectile object
     */
    private static GameObject createProjectileObject(
            ProjectileType type, GameObject source, Transform projXf
    ) {
        return new GameObject(type.name(), projXf, SpaceGameZIndex.PROJECTILE) {
            @Override
            protected void init() {
                setLayer(getScene().getLayer(SpaceGameLayer.PROJECTILES));
                addComponent(new Projectile(source, type.damage(), type.speed(), type.lifetime()));
                addComponent(PROJECTILE_SPRITES.getSprite(type.spriteIndex()));

                addComponent(new BallCollider(type.colliderSize()).setTrigger(true));
                addComponent(new Rigidbody(0.001f));
            }
        };
    }

    /**
     * Get the projectile transform in world space.
     *
     * @param type        the projectile type
     * @param sourceXf    the transform of the source object
     * @param offsetPos   the projectile spawn position in relation to the source
     * @param offsetAngle the projectile spawn angle in relation to the source
     * @return the projectile transform
     */
    private static Transform getProjectileTransform(
            ProjectileType type, Transform sourceXf, Vec2 offsetPos, float offsetAngle
    ) {
        var weaponSpreadAngle = Random.randomFloat(-type.weaponSpread(), type.weaponSpread());
        return new Transform(
//                sourceXf.getPosition().add(offsetPos.rotate(sourceXf.getRotation())),
                sourceXf.toWorld(offsetPos),
                sourceXf.getRotation() + offsetAngle + weaponSpreadAngle,
                type.scale()
        );
    }

}
