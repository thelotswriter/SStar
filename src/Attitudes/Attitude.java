package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public interface Attitude
{

    public StrategyTable generateSpecificStrategy(Game game);

    public boolean isRandom();

}
