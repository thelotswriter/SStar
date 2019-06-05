package StrategyTables;

import Game.*;

import java.util.ArrayList;

public class GameToTable {
    private static GameToTable ourInstance = new GameToTable();

    public static GameToTable getInstance() {
        return ourInstance;
    }

    private GameToTable() {
    }

    public StrategyTable convertToTable(Game game, int history)
    {
        StrategyTable table = new StrategyTable(game.getNumRowActions(), game.getNumColActions(), history);
        ArrayList<int[]> actionHistory = new ArrayList<>();
        for(int i = 0; i < game.getNumRounds(); i++)
        {
            int[] currentActionPair = game.getActionPair(i);
            if(actionHistory.size() == 0)
            {
                table.addObservation(currentActionPair[0], null);
            } else if(actionHistory.size() < history)
            {
                int[] previousState = new int[actionHistory.size() * 2];
                for(int j = 0; j < actionHistory.size(); j++)
                {
                    previousState[2 * j] = actionHistory.get(i)[0];
                    previousState[2 * j + 1] = actionHistory.get(i)[1];
                }
                table.addObservation(currentActionPair[0], previousState);
            } else
            {
                int[] previousState = new int[history * 2];
                for(int j = 0; j < history; j++)
                {
                    previousState[2 * j] = actionHistory.get(actionHistory.size() - history + j)[0];
                    previousState[2 * j + 1] = actionHistory.get(actionHistory.size() - history + j)[1];
                }
            }
            actionHistory.add(currentActionPair);
        }
        return table;
    }

}
