package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Frustrated implements Attitude
{

    private static Frustrated singleton;

    public static Frustrated getInstance()
    {
        if(singleton == null)
        {
            singleton = new Frustrated();
        }
        return singleton;
    }

    private Frustrated()
    {

    }
    
    @Override
    public StrategyTable generateSpecificStrategy(Game game)
    {
        StrategyTable strategy = new StrategyTable(game.getNumRowActions(), game.getNumColActions(), 0);
        for(int row = 0; row < game.getNumRowActions(); row++)
        {
            strategy.addObservation(row, null);
        }
        return strategy;
    }

    @Override
    public boolean isRandom() {
        return true;
    }
}
