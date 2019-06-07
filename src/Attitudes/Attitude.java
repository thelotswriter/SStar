package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public interface Attitude
{

    public int getAction(ArrayList<int[]> previousActionPairs);

    public StrategyTable convertToTable(Game game);

}
