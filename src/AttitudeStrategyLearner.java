import StrategyTables.StrategyTable;

import java.util.ArrayList;
import java.util.Collection;

public class AttitudeStrategyLearner
{

    private static AttitudeStrategyLearner ourInstance = new AttitudeStrategyLearner();

    public static AttitudeStrategyLearner getInstance() {
        return ourInstance;
    }

    private AttitudeStrategyLearner() {}

    public Collection<StrategyTable> learnStrategies(int numStrategies, Collection<StrategyTable> attitudeStrategyData, int maxEpochs)
    {
        StrategyTable[] representativeStrategies = initializeCentroids(numStrategies, attitudeStrategyData);
        int countdown = 2;

        while(maxEpochs > 0 && countdown > 2)
        {
            Collection<StrategyTable>[] clusters = new ArrayList[numStrategies];

            maxEpochs--;
        }
        Collection<StrategyTable> learnedStrategies = new ArrayList<>();
        for(StrategyTable representativeStrategy : representativeStrategies)
        {
            learnedStrategies.add(representativeStrategy);
        }
        return learnedStrategies;
    }

    private StrategyTable[] initializeCentroids(int k, Collection<StrategyTable> strategyData)
    {
        
        return null;
    }

}
