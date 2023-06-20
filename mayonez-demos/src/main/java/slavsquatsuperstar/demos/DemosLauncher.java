package slavsquatsuperstar.demos;

import mayonez.*;
import mayonez.init.*;
import mayonez.input.*;
import slavsquatsuperstar.demos.mario.MarioScene;
import slavsquatsuperstar.demos.physics.PoolBallsScene;
import slavsquatsuperstar.demos.physics.SandboxScene;
import slavsquatsuperstar.demos.spacegame.SpaceGameScene;

/**
 * A class with a main method that can be used to launch and switch between the different demo scenes.
 *
 * @author SlavSquatSuperstar
 */
// TODO maybe fix up other demos and include them
public class DemosLauncher {

    private final static String[] sceneNames = {
            "Space Game", "Mario Scene", "Collisions Test", "Pool Balls Test"
    };

    public static void main(String[] args) {
        var launcher = new Launcher(args).setRunConfig();
        launcher.loadScenesToManager(getScenesToLoad());
        launcher.startGame(sceneNames[0]);
    }

    private static Scene[] getScenesToLoad() {
        return new Scene[]{
                new SpaceGameScene(sceneNames[0]) {
                    @Override
                    protected void onUserUpdate(float dt) {
                        super.onUserUpdate(dt);
                        pollSceneControls();
                    }
                },
                new MarioScene(sceneNames[1]) {
                    @Override
                    protected void onUserUpdate(float dt) {
                        super.onUserUpdate(dt);
                        pollSceneControls();
                    }
                },
                new SandboxScene(sceneNames[2]) {
                    @Override
                    protected void onUserUpdate(float dt) {
                        super.onUserUpdate(dt);
                        pollSceneControls();
                    }
                },
                new PoolBallsScene(sceneNames[3]) {
                    @Override
                    protected void onUserUpdate(float dt) {
                        super.onUserUpdate(dt);
                        pollSceneControls();
                    }
                }
        };
    }

    private static void pollSceneControls() {
        if (Input.keyPressed("r")) {
            SceneManager.restartScene();
        } else if (Input.keyPressed("p")) {
            SceneManager.toggleScenePaused(); // this is being run twice per frame in SpaceGameScene
        } else if (Input.keyDown("left shift")) {
            for (var i = 0; i < sceneNames.length; i++) {
                if (Input.keyPressed(String.valueOf(i + 1))) SceneManager.loadScene(sceneNames[i]);
            }
        }
    }

}
