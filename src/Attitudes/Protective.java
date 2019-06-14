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
    public StrategyTable generateSpecificStrategy(Game game)
    {
        double worstValue = Double.MAX_VALUE;
        ArrayList<Integer> worstCols = new ArrayList<>();
        for(int r = 0; r < game.getNumRowActions(); r++)
        {
            for(int c = 0; c < game.getNumColActions(); c++)
            {
                double val = game.getPayoffMatrix().getRowPlayerValue(r, c);
                if(val < worstValue)
                {
                    worstValue = val;
                    worstCols.clear();
                    worstCols.add(c);
                } else if(val == worstValue)
                {
                    boolean add = true;
                    for(int col : worstCols)
                    {
                        if(col == c)
                        {
                            add = false;
                            break;
                        }
                    }
                    if(add)
                    {
                        worstCols.add(r);
                    }
                }
            }
        }
        double bestValue = Double.MIN_VALUE;
        ArrayList<Integer> bestResponses = new ArrayList<>();
        for(int c : worstCols)
        {
            for(int r = 0; r < game.getNumRowActions(); r++)
            {
                double val = game.getPayoffMatrix().getRowPlayerValue(r, c);
                if(val > bestValue)
                {
                    bestValue = val;
                    bestResponses.clear();
                    bestResponses.add(r);
                } else if(val == bestValue)
                {
                    boolean add = true;
                    for(int row : bestResponses)
                    {
                        if(row == r)
                        {
                            add = false;
                            break;
                        }
                    }
                    if(add)
                    {
                        bestResponses.add(r);
                    }
                }
            }
        }
        StrategyTable strategy = new StrategyTable(game.getNumRowActions(), game.getNumColActions(), 0);
        if(bestResponses.size() > 0)
        {
            for(int row : bestResponses)
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
