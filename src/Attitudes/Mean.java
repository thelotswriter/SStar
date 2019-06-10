package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Mean implements Attitude
{

    private static Mean singleton;

    public static Mean getInstance()
    {
        if(singleton == null)
        {
            singleton = new Mean();
        }
        return singleton;
    }

    private Mean()
    {

    }
    
    @Override
    public StrategyTable generateSpecificStrategy(Game game) {
        return null;
    }
}
