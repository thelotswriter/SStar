package StrategyTables;

import Attitudes.Attitude;
import Attitudes.Frustrated;
import Game.Game;

import java.util.ArrayList;
import java.util.Collection;

public class AttitudeStrategyApplicator
{

    private static AttitudeStrategyApplicator ourInstance = new AttitudeStrategyApplicator();

    public static AttitudeStrategyApplicator getInstance() {
        return ourInstance;
    }

    private AttitudeStrategyApplicator() {}

    public Collection<StrategyTable> applyAttitudeStrategies(Collection<StrategyTable> attitudeStrategies, Collection<Attitude> attitudes, Game game)
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
        Collection<StrategyTable> appliedStrategies = new ArrayList<>();
        for(StrategyTable attitudeStrategy : attitudeStrategies)
        {
            StrategyTable appliedStrategy = new StrategyTable(game.getNumRowActions(), game.getNumColActions(), attitudeStrategy.getHistory());
            StrategyPercentTable probabilityTable = new StrategyPercentTable(game.getNumRowActions(), game.getNumColActions(), attitudeStrategy.getHistory());

            // Initial (no history) Case
            double[] attitudeStrategyInitialPercents = attitudeStrategy.getObservationPercents(null);
            for(int i = 0; i < attitudeStrategyInitialPercents.length; i++)
            {
                double[] appliedAttitudePercents = appliedAttitudes.get(i).getObservationPercents(null);
                for(int j = 0; j < appliedAttitudePercents.length; j++)
                {
                    probabilityTable.addObservation(j, null, attitudeStrategyInitialPercents[i] * appliedAttitudePercents[j]);
                }
            }

            // With history
            for(int h = 1; h <= attitudeStrategy.getHistory(); h++)
            {
                int[] index = new int[2 * h];
                while(index != null)
                {
                    Collection<int[]> correspondingActionHistories = generateMatchingRows(index, appliedAttitudes);
                    double[] attitudeStrategyPercents = attitudeStrategy.getObservationPercents(index);
                    for(int[] possibleActionHistory : correspondingActionHistories)
                    {
                        for(int i = 0; i < attitudeStrategyPercents.length; i++)
                        {
                            double[] appliedAttitudePercents = appliedAttitudes.get(i).getObservationPercents(null);
                            for(int j = 0; j < appliedAttitudePercents.length; j++)
                            {
                                probabilityTable.addObservation(j, possibleActionHistory, attitudeStrategyPercents[i] * appliedAttitudePercents[j]);
                            }
                        }
                    }
                    // Move the index along. Check that this is correct
                    index = advanceArray(index, appliedAttitudes.size(), appliedAttitudes.size());
                }
            }

//
//            Collection<Integer> nullReactionAttitudes = findCorrespondingActions(null, appliedAttitudes, attitudeStrategy);
//            for(Integer observation : nullReactionAttitudes)
//            {
//                appliedStrategy.addObservation(observation.intValue(), null);
//            }
//
//            for(int h = 1; h <= attitudeStrategy.getHistory(); h++)
//            {
//                int[] index = new int[2 * h];
//                while(index != null)
//                {
//                    // Iterate through possible histories
//                    Collection<int[]> generalRows = generateMatchingRows(index, appliedAttitudes); // Generate labels
//                    Collection<Integer> reactionAttitudes = findCorrespondingStrategies(generalRows, appliedAttitudes, attitudeStrategy);
//                    for(Integer observation : reactionAttitudes)
//                    {
//                        attitudeStrategy.addObservation(observation.intValue(), index);
//                    }
//                    index = advanceArray(index, appliedAttitudes.size(), appliedAttitudes.size());
//                }
//            }
            appliedStrategies.add(appliedStrategy);
        }
        return appliedStrategies;
    }

    private Collection<Integer> findCorrespondingActions(Collection<int[]> generalRows, ArrayList<StrategyTable> appliedAttitudes, StrategyTable attitudeStrategy)
    {
        return null;
    }

    private Collection<int[]> generateMatchingRows(int[] index, ArrayList<StrategyTable> appliedAttitudes)
    {
        return null;
    }

    private Collection<Integer> findCorrespondingStrategies(Collection<int[]> generalRows, ArrayList<StrategyTable> appliedAttitudes, StrategyTable attitudeStrategy)
    {
        return null;
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
