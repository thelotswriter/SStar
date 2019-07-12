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

            Collection<Integer> nullReactionAttitudes = findCorrespondingActions(null, appliedAttitudes, attitudeStrategy);
            for(Integer observation : nullReactionAttitudes)
            {
                appliedStrategy.addObservation(observation.intValue(), null);
            }

//            for(int h = 1; h <= generalizedStrategy.getHistory(); h++)
//            {
//                int[] index = new int[2 * h];
//                while(index != null)
//                {
//                    // Iterate through possible histories
//                    Collection<int[]> specificRows = generateMatchingRows(index, appliedAttitudes); // Generate labels
//                    Collection<Integer> reactionAttitudes = findCorrespondingAttitudes(specificRows, appliedAttitudes, specificStrategy);
//                    for(Integer observation : reactionAttitudes)
//                    {
//                        generalizedStrategy.addObservation(observation.intValue(), index);
//                    }
//                    index = advanceArray(index, appliedAttitudes.size(), appliedAttitudes.size());
//                }
//            }
        }
        return appliedStrategies;
    }

    private Collection<Integer> findCorrespondingActions(Collection<int[]> generalRows, ArrayList<StrategyTable> appliedAttitudes, StrategyTable attitudeStrategy)
    {
        return null;
    }

}
