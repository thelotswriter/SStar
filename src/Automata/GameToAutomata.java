package Automata;

import Game.Game;

import java.util.ArrayList;

public class GameToAutomata {
    private static GameToAutomata ourInstance = new GameToAutomata();

    public static GameToAutomata getInstance() {
        return ourInstance;
    }

    private GameToAutomata() {
    }

    /**
     * Creates two automata (one for each player) based on the given game, with states remembering a set history
     * @param game The game to be translated into automata
     * @param history How far states "look back" at previous states. If history is zero, only the most recent action matters
     * @return An array of automata, the first for the row player and the second for the column player
     */
    public LearningAutomaton generateAutomaton(Game game, int history)
    {
        double[][][] matrix = game.getPayoffMatrix().getMatrix();
        int numPlayer1Actions = matrix.length;
        int numPlayer2Actions = matrix[0].length;
//        int numAutomata = 1;
//        for(int i = 0; i <= history; i++)
//        {
//            numAutomata += Math.pow(numPlayer1Actions * numPlayer2Actions, history + 1);
//        }
        State rowStartState = new State(0, numPlayer1Actions, false);
        State colStartState = new State(0, numPlayer2Actions, false);
        ArrayList<ArrayList> rowAutomatonTreeLayers = new ArrayList<>();
        ArrayList<ArrayList> colAutomatonTreeLayers = new ArrayList<>();
        ArrayList<State> rowFirstOptions = new ArrayList<>();
        ArrayList<State> colFirstOptions = new ArrayList<>();
        for(int i = 0; i < numPlayer1Actions; i++)
        {
            State s = new State(i, numPlayer1Actions * numPlayer2Actions, false);
            Transition t = new Transition(s, 1);
            rowStartState.addTransition(i, t);
            rowFirstOptions.add(s);
        }
        rowStartState.normalize();
        for(int i = 0; i < numPlayer2Actions; i++)
        {
            State s = new State(i, numPlayer1Actions * numPlayer2Actions, false);
            Transition t = new Transition(s, 1);
            colStartState.addTransition(i, t);
            colFirstOptions.add(s);
        }
        colStartState.normalize();
        for(int layer = 0; layer < history + 1; layer++)
        {
            int rowLayerStates = numPlayer1Actions * ((int) Math.pow(numPlayer1Actions * numPlayer2Actions, layer + 1));
            int colLayerStates = numPlayer2Actions * ((int) Math.pow(numPlayer1Actions * numPlayer2Actions, layer + 1));
        }
        // Learn the game
        return null;
    }

}
