package StrategyAutomata;

import Attitudes.AttitudeVector;
import Clustering.DistantDataCluster;
import Clustering.FeatList2DSAttVecClustList;
import Features.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatList2GSList
{

    private DistantDataCluster[] dispDataClusters;
    private DistantDataCluster[] saidDataClusters;
    private List<GeneralState> allStates;

    public FeatList2GSList(DistantDataCluster[] dispCluster, DistantDataCluster[] saidCluster, List<GeneralState> allStates)
    {
        this.dispDataClusters = dispCluster;
        this.saidDataClusters = saidCluster;
        this.allStates = allStates;
    }

    public Collection<GeneralState[]> convert(Collection<Collection<Feature>> featureCollections) {
        Collection<GeneralState[]> convertedFeatureCollections = new ArrayList<>();
        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
        for (Collection<Feature> featureCollection : featureCollections)
        {
            int[][] dsAttVecClustList = fl2dsavcl.getClusterIndexList((List) featureCollection, dispDataClusters, saidDataClusters);
            GeneralState[] convertedFeatures = new GeneralState[dsAttVecClustList.length];
            for(int i = 0; i < convertedFeatures.length; i++)
            {
                GeneralAttitudeVectorState gavs = new GeneralAttitudeVectorState((AttitudeVector) dispDataClusters[dsAttVecClustList[i][0]].getCentroid(),
                        (AttitudeVector) dispDataClusters[dsAttVecClustList[i][1]].getCentroid(),
                        (AttitudeVector) dispDataClusters[dsAttVecClustList[i][2]].getCentroid(),
                        (AttitudeVector) dispDataClusters[dsAttVecClustList[i][3]].getCentroid());
                convertedFeatures[i] = gavs;
            }
            convertedFeatureCollections.add(convertedFeatures);
        }
        return convertedFeatureCollections;
    }

}
