package slavsquatsuperstar;

import slavsquatsuperstar.mayonez.Game;

public class Main {

    public static void main(String[] args) {
        Game game = Game.instance();
        game.start();
        Game.loadScene(0);
    }

}