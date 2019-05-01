package Automata;

import Game.Game;

public class GameToAutomata {
    private static GameToAutomata ourInstance = new GameToAutomata();

    public static GameToAutomata getInstance() {
        return ourInstance;
    }

    private GameToAutomata() {
    }

    /**
     * Creates an automata based on the given game, with states remembering a set history
     * @param game The game to be translated into automata
     * @param history How far states "look back" at previous states. If history is zero, only the most recent action matters
     * @return An array of automata, the first for the row player and the second for the column player
     */
    public LearningAutomaton[] generateAutomata(Game game, int history)
    {
        double[][][] matrix = game.getPayoffMatrix().getMatrix();
        int numPlayer1Actions = matrix.length;
        int numPlayer2Actions = matrix[0].length;
        
        return null;
    }

}
