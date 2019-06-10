package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Protective implements Attitude
{

    private static Protective singleton;

    public static Protective getInstance()
    {
        if(singleton == null)
        {
            singleton = new Protective();
        }
        return singleton;
    }

    private Protective()
    {

    }

    @Override
    public StrategyTable generateSpecificStrategy(Game game) {
        return null;
    }

}
