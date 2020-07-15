package StrategyAutomata;

import Attitudes.AttitudeVector;
import Clustering.DistantDataCluster;

import java.util.ArrayList;
import java.util.List;

public class DSAttitudeVectorsToGeneralAttitudeVectorList
{

    private static DSAttitudeVectorsToGeneralAttitudeVectorList SINGLETON = null;

    public static DSAttitudeVectorsToGeneralAttitudeVectorList getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new DSAttitudeVectorsToGeneralAttitudeVectorList();
        }
        return SINGLETON;
    }

    private DSAttitudeVectorsToGeneralAttitudeVectorList()
    {

    }

    public List<GeneralAttitudeVectorState> giveAllStates(DistantDataCluster[] dispAVClusters, DistantDataCluster[] saidAVClusters)
    {
        List<GeneralAttitudeVectorState> allStates = new ArrayList<>();
        for(int d = 0; d < dispAVClusters.length; d++)
        {
            for(int s = 0; s < saidAVClusters.length; s++)
            {
                for(int od = 0; od < dispAVClusters.length; od++)
                {
                    for(int os = 0; os < saidAVClusters.length; os++)
                    {
                        allStates.add(new GeneralAttitudeVectorState((AttitudeVector) dispAVClusters[d].getCentroid(),
                                (AttitudeVector) saidAVClusters[s].getCentroid(), (AttitudeVector) dispAVClusters[od].getCentroid(),
                                (AttitudeVector) saidAVClusters[os].getCentroid()));
                    }
                }
            }
        }
        return allStates;
    }

}
