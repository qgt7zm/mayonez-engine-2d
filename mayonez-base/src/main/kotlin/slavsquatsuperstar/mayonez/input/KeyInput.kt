package slavsquatsuperstar.mayonez.input

import org.lwjgl.glfw.GLFW.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

/**
 * The receiver for all keyboard-related input events.
 *
 * @author SlavSquatSuperstar
 */
// TODO game loop / callback methods shouldn't be accessible from API (called statically)
@Suppress("unused")
object KeyInput : KeyAdapter() {

    // Key Fields
    // represent a bit field with held, pressed, and down bits
    private val keysDown = HashMap<Int, Boolean?>() // if key listener detects this key
    private val keysPressed = HashMap<Int, Boolean?>() // if key was just pressed in this frame
    private val keysHeld = HashMap<Int, Boolean?>() // if key is continuously held down

    // Game Loop Methods
    @JvmStatic
    fun endFrame() { // TODO rename method?
        // Update key input
        for (k in keysDown.keys) {
            if (keysDown[k] == true) {
                if (keysPressed[k] != true && keysHeld[k] != true) { // New key press
                    keysPressed[k] = true
                } else if (keysPressed[k] == true) { // Continued key press
                    keysPressed[k] = false
                    keysHeld[k] = true
                }
            } else { // Released key
                keysPressed[k] = false
                keysHeld[k] = false
            }
        }
    }

    /* Keyboard Callbacks */

    @JvmStatic
    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        when (action) {
            // TODO GL double pressing still occurs
            GLFW_PRESS -> {
                keysDown[key] = true
//                keysPressed[key] = true
//                keysHeld[key] = false
            }
            GLFW_REPEAT -> {
                keysDown[key] = true
//                keysPressed[key] = false
//                keysHeld[key] = true
            }
            GLFW_RELEASE -> {
                keysDown[key] = false
//                keysPressed[key] = false
//                keysHeld[key] = false
            }
        }
    }

    override fun keyPressed(e: KeyEvent) { // Activates when ever a key is down
        keysDown[e.keyCode] = true
    }

    override fun keyReleased(e: KeyEvent) {
        keysDown[e.keyCode] = false
    }

    /* Keyboard Getters */

    @JvmStatic
    fun keyDown(keyCode: Int): Boolean = keysHeld[keyCode] == true || keysPressed[keyCode] == true

    @JvmStatic
    fun keyPressed(keyCode: Int): Boolean = keysPressed[keyCode] == true

    /**
     * Returns whether any of the keys associated with the specified [KeyMapping] is continuously held down.
     *
     * @param keyName the name of the [KeyMapping]
     * @return if the specified key is down
     */
    @JvmStatic
    fun keyDown(keyName: String): Boolean {
        for (m in KeyMapping.values())
            if (m.name.equals(keyName, ignoreCase = true)) // if the desired mapping exists
                for (code in m.keyCodes) if (keyDown(code)) return true
        return false
    }

    /**
     * Returns whether any of the keys associated with the specified [KeyMapping] has been pressed.
     *
     * @param keyName the name of the [KeyMapping]
     * @return if the specified key was pressed
     */
    @JvmStatic
    fun keyPressed(keyName: String): Boolean {
        for (m in KeyMapping.values())
            if (m.name.equals(keyName, ignoreCase = true))
                for (code in m.keyCodes) if (keyPressed(code)) return true
        return false
    }

    @JvmStatic
    fun getAxis(axisName: String): Int {
        for (a in KeyAxis.values())
            if (a.toString().equals(axisName, ignoreCase = true))
                return a.value()
        return 0
    }

}