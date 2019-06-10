package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Placate implements Attitude
{

    private static Placate singleton;

    public static Placate getInstance()
    {
        if(singleton == null)
        {
            singleton = new Placate();
        }
        return singleton;
    }

    private Placate()
    {

    }
    
    @Override
    public StrategyTable generateSpecificStrategy(Game game)
    {
        double bestValue = Double.MIN_VALUE;
        ArrayList<Integer> bestRows = new ArrayList<>();
        for(int r = 0; r < game.getNumRowActions(); r++)
        {
            for(int c = 0; c < game.getNumColActions(); c++)
            {
                double val = game.getPayoffMatrix().getColPlayerValue(r, c);
                if(val > bestValue)
                {
                    bestValue = val;
                    bestRows.clear();
                    bestRows.add(r);
                } else if(val == bestValue)
                {
                    boolean add = true;
                    for(Integer row : bestRows)
                    {
                        if(row.intValue() == r)
                        {
                            add = false;
                            break;
                        }
                    }
                    if(add)
                    {
                        bestRows.add(r);
                    }
                }
            }
        }
        StrategyTable strategy = new StrategyTable(game.getNumRowActions(), game.getNumColActions(), 0);
        if(bestRows.size() > 0)
        {
            for(int row : bestRows)
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
}
