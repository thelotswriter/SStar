package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Forgiveness implements Attitude
{

    private static Forgiveness singleton;

    public static Forgiveness getInstance()
    {
        if(singleton == null)
        {
            singleton = new Forgiveness();
        }
        return singleton;
    }

    private Forgiveness()
    {

    }
    
    @Override
    public StrategyTable generateSpecificStrategy(Game game) {
        return null;
    }
}
