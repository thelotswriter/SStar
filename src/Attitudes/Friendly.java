package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Friendly implements Attitude
{

    private static Friendly singleton;

    public static Friendly getInstance()
    {
        if(singleton == null)
        {
            singleton = new Friendly();
        }
        return singleton;
    }

    private Friendly()
    {

    }
    
    @Override
    public StrategyTable generateSpecificStrategy(Game game) {
        return null;
    }
}
