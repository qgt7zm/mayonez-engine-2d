package slavsquatsuperstar.demos.physics;

import mayonez.GameObject;
import mayonez.Script;
import mayonez.graphics.Colors;
import mayonez.graphics.sprites.ShapeSprite;
import mayonez.input.KeyInput;
import mayonez.math.Vec2;
import mayonez.physics.Rigidbody;
import mayonez.physics.colliders.BallCollider;
import mayonez.physics.colliders.Collider;
import mayonez.scripts.KeepInScene;
import mayonez.scripts.movement.DragAndDrop;
import mayonez.scripts.movement.MouseFlick;
import mayonez.scripts.movement.MoveMode;

public class FrictionTest extends PhysicsTestScene {

    public FrictionTest(String name) {
        super(name, 2);
    }

    @Override
    protected void init() {
        super.init();
        addObject(createStaticBox("Ground", new Vec2(getWidth() / 2f, 1), new Vec2(getWidth(), 2), 0));
        addObject(createStaticBox("Sloped Ramp", new Vec2(30, 50), new Vec2(40, 5), -25));

        addObject(createBall(new Vec2(4), new Vec2(25, 60), NORMAL_MATERIAL));
        addObject(createBox(new Vec2(4), new Vec2(15, 65), 15, BOUNCY_MATERIAL));

        addObject(new GameObject("Player Circle", new Vec2(25, 8)) {
            @Override
            protected void init() {
                float speed = 2f;
//                Collider2D collider = new BoxCollider2D(new Vec2(6, 6));
                Collider collider = new BallCollider(3f);
                addComponent(collider);
                addComponent(new ShapeSprite(Colors.BLUE, false));
                addComponent(new Rigidbody(collider.getMass(DENSITY)));
                addComponent(new MouseFlick(MoveMode.VELOCITY, "right mouse", 15, false));
                addComponent(new DragAndDrop("left mouse"));
                addComponent(new KeepInScene(new Vec2(), new Vec2(getScene().getSize()), KeepInScene.Mode.BOUNCE));
                addComponent(new Script() {
                    private Rigidbody rb;

                    @Override
                    public void start() {
                        rb = getRigidbody().setMaterial(PhysicsTestScene.NORMAL_MATERIAL);
                    }

                    @Override
                    public void update(float dt) {
                        if (KeyInput.keyDown("q"))
                            rb.applyAngularImpulse(speed);
                        else if (KeyInput.keyDown("e"))
                            rb.applyAngularImpulse(-speed);

                        rb.applyImpulse(new Vec2(KeyInput.getAxis("horizontal") * speed,
                                KeyInput.getAxis("vertical") * speed));
                    }
                });
            }
        });
    }

    @Override
    protected void onUserUpdate(float dt) {
        super.onUserUpdate(dt);
        getCamera().setPosition(getSize().mul(0.5f));
    }

}
