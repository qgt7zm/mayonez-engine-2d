package slavsquatsuperstar.demos.spacegame.objects;

import mayonez.*;
import slavsquatsuperstar.demos.spacegame.objects.asteroids.Asteroid;
import slavsquatsuperstar.demos.spacegame.objects.ships.EnemyShip;

/**
 * Automatically populates the scene with prefabs and respawns them when they are
 * destroyed.
 *
 * @author SlavSquatSuperstar
 */
public class SpaceObjectSpawner extends GameObject {

    // Constants
    private final static int NUM_ENEMIES = 6;
    private final static float ENEMY_RESPAWN_COOLDOWN = 5f;
    private final static int NUM_OBSTACLES = 5;
    private final static float OBSTACLE_RESPAWN_COOLDOWN = 20f;

    public SpaceObjectSpawner(String name) {
        super(name);
    }

    @Override
    protected void init() {
        addComponent(new PlayerSpawner());
        addComponent(new SpawnManager(NUM_ENEMIES, ENEMY_RESPAWN_COOLDOWN) {
            @Override
            public GameObject createSpawnedObject() {
                return new EnemyShip("Enemy Spaceship",
                        "assets/spacegame/textures/spaceship2.png", this);
            }
        });
        addComponent(new SpawnManager(NUM_OBSTACLES, OBSTACLE_RESPAWN_COOLDOWN) {
            @Override
            public GameObject createSpawnedObject() {
                return new Asteroid("Asteroid", this);
            }
        });
    }

}
