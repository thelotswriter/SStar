package StrategyTables;

import Attitudes.Attitude;
import Attitudes.Frustrated;
import Game.Game;

import java.util.ArrayList;
import java.util.Collection;

public class GeneralizeStrategy
{

    private static GeneralizeStrategy ourInstance = new GeneralizeStrategy();

    public static GeneralizeStrategy getInstance() {
        return ourInstance;
    }

    private GeneralizeStrategy() {}

    public StrategyTable convertToAttitudeStrategy(Game game, StrategyTable specificStrategy, Collection<Attitude> attitudes)
    {
        ArrayList<StrategyTable> appliedAttitudes = new ArrayList<>();
        for(Attitude attitude : attitudes)
        {
            if(!attitude.isRandom())
            {
                appliedAttitudes.add(attitude.generateSpecificStrategy(game));
            }
        }
        appliedAttitudes.add(Frustrated.getInstance().generateSpecificStrategy(game));
        StrategyTable generalizedStrategy = new StrategyTable(appliedAttitudes.size(), appliedAttitudes.size(), specificStrategy.getHistory());
        for(int h = 1; h < generalizedStrategy.getHistory(); h++)
        {
            int[] index = new int[2 * h];
            while(index != null)
            {
                // Iterate through possible histories
                Collection<int[]> specificRows = generateMatchingRows(index, appliedAttitudes);
                Collection<Integer> reactionAttitudes = findCorrespondingAttitudes(specificRows, appliedAttitudes, specificStrategy);
                for(Integer observation : reactionAttitudes)
                {
                    generalizedStrategy.addObservation(observation.intValue(), index);
                }
                index = advanceArray(index, appliedAttitudes.size(), appliedAttitudes.size());
            }
        }
        return generalizedStrategy;
    }

    private double dist(double[] row1, double[] row2)
    {
        if(row1.length != row2.length)
        {
            return Double.POSITIVE_INFINITY;
        }
        double dist = 0;
        for(int i = 0; i < row1.length; i++)
        {
            dist += Math.abs(row1[i] - row2[i]);
        }
        return dist;
    }

    private Collection<int[]> generateMatchingRows(int[] generalRow, ArrayList<StrategyTable> appliedAttitudes)
    {
        return null;
    }

    private Collection<Integer> findCorrespondingAttitudes(Collection<int[]> specificRows, ArrayList<StrategyTable> appliedAttitudes, StrategyTable specificStrategy)
    {
        ArrayList<Integer> attitudes = new ArrayList<>();
        for(int[] row : specificRows)
        {
            double dist = Double.POSITIVE_INFINITY;
            ArrayList<Integer> currentAttitudes = new ArrayList<>();
            for(int i = 0; i < appliedAttitudes.size(); i++)
            {
                double currentDist = dist(specificStrategy.getObservationPercents(row), appliedAttitudes.get(i).getObservationPercents(row));
                if(currentDist > dist)
                {
                    currentAttitudes.clear();
                    currentAttitudes.add(i);
                    dist = currentDist;
                } else if(currentDist == dist)
                {
                    currentAttitudes.add(i);
                }
            }
            attitudes.addAll(currentAttitudes);
        }
        return attitudes;
    }

    private int[] advanceArray(int[] prevRow, int nActions, int nOtherActions)
    {
        int finalIndex = prevRow.length - 1;
        if(prevRow[finalIndex] < nOtherActions - 1)
        {
            prevRow[finalIndex]++;
            return prevRow;
        } else
        {
            prevRow[finalIndex] = 0;
            return advanceArray(prevRow, nActions, nOtherActions, finalIndex - 1);
        }
    }

    private int[] advanceArray(int[] prevRow, int nActions, int nOtherActions, int index)
    {
        if((index % 2 == 0 && prevRow[index] < nActions - 1) || (index % 2 == 1 && prevRow[index] < nOtherActions - 1))
        {
            prevRow[index]++;
            return prevRow;
        } else if(index > 0)
        {
            prevRow[index] = 0;
            return advanceArray(prevRow, nActions, nOtherActions, index - 1);
        } else
        {
            return null;
        }
    }

}
