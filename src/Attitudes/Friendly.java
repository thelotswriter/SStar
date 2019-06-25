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
    public StrategyTable generateSpecificStrategy(Game game)
    {
        double[][] combinedMatrix = new double[game.getNumRowActions()][game.getNumColActions()];
        for(int r = 0; r < game.getNumRowActions(); r++)
        {
            for(int c = 0; c < game.getNumColActions(); c++)
            {
                combinedMatrix[r][c] = game.getPayoffMatrix().getColPlayerValue(r, c) + game.getPayoffMatrix().getRowPlayerValue(r, c);
            }
        }
        return null;
    }

    @Override
    public boolean isRandom() {
        return false;
    }

}
