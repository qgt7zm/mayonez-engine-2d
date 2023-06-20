package mayonez.input

import mayonez.annotations.*
import mayonez.event.*
import java.awt.event.*

/**
 * Receives keyboard input events from AWT.
 *
 * @author SlavSquatSuperstar
 */
@UsesEngine(EngineType.AWT)
internal class JKeyManager internal constructor() : KeyManager() {

    // Key Callbacks

    override fun keyPressed(e: KeyEvent) {
        setKeyDown(e.keyCode, true)
        EventSystem.broadcast(KeyboardEvent(e.keyCode, true, e.modifiersEx))
//        println("${KeyEvent.getModifiersExText(e.modifiersEx)}${e.keyLocation}")
    }

    override fun keyReleased(e: KeyEvent) {
        setKeyDown(e.keyCode, false)
        EventSystem.broadcast(KeyboardEvent(e.keyCode, false, e.modifiersEx))
    }

    // Key Getters

    override fun keyDown(key: Key?): Boolean {
        return if (key == null) false
        else keyDown(key.awtCode)
    }

    override fun keyPressed(key: Key?): Boolean {
        return if (key == null) false
        else keyPressed(key.awtCode)
    }

}