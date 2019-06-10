package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Vengeance implements Attitude
{

    private static Vengeance singleton;

    public static Vengeance getInstance()
    {
        if(singleton == null)
        {
            singleton = new Vengeance();
        }
        return singleton;
    }

    private Vengeance()
    {

    }
    
    @Override
    public StrategyTable generateSpecificStrategy(Game game) {
        return null;
    }
}
