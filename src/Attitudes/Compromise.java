package Attitudes;

import Game.Game;
import StrategyTables.StrategyTable;

import java.util.ArrayList;

public class Compromise implements Attitude
{

    private static Compromise singleton;

    public static Compromise getInstance()
    {
        if(singleton == null)
        {
            singleton = new Compromise();
        }
        return singleton;
    }

    private Compromise()
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
                double val = game.getPayoffMatrix().getRowPlayerValue(r, c) + game.getPayoffMatrix().getColPlayerValue(r, c);
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

    @Override
    public boolean isRandom() {
        return false;
    }
}
