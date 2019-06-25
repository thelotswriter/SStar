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
    public StrategyTable generateSpecificStrategy(Game game)
    {
        double worstValue = Double.MAX_VALUE;
        ArrayList<Integer> worstRows = new ArrayList<>();
        for(int r = 0; r < game.getNumRowActions(); r++)
        {
            for(int c = 0; c < game.getNumColActions(); c++)
            {
                double val = game.getPayoffMatrix().getColPlayerValue(r, c);
                if(val < worstValue)
                {
                    worstValue = val;
                    worstRows.clear();
                    worstRows.add(r);
                } else if(val == worstValue)
                {
                    boolean add = true;
                    for(Integer row : worstRows)
                    {
                        if(row.intValue() == r)
                        {
                            add = false;
                            break;
                        }
                    }
                    if(add)
                    {
                        worstRows.add(r);
                    }
                }
            }
        }
        StrategyTable strategy = new StrategyTable(game.getNumRowActions(), game.getNumColActions(), 0);
        if(worstRows.size() > 0)
        {
            for(int row : worstRows)
            {
                strategy.addObservation(row, null);
            }
        } else
        {
            for(int row = 0; row < game.getNumRowActions(); row++)
            {
                strategy.addObservation(row, null);
            }
        }
        return strategy;
    }

    @Override
    public boolean isRandom() {
        return false;
    }
}
