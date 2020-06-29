package Clustering;

import Features.Feature;
import Game.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeaturesToFeatureClusters
{

    private static FeaturesToFeatureClusters SINGLETON = null;

    public static FeaturesToFeatureClusters getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new FeaturesToFeatureClusters();
        }
        return SINGLETON;
    }

    private FeaturesToFeatureClusters()
    {

    }

    public DistantDataCluster[] getFeatureCluster(int nClusters, Collection<Collection<Feature>> featureList)
    {
        List<Feature> features = new ArrayList<>();
        for(Collection<Feature> fList : featureList)
        {
            features.addAll(fList);
        }
        return getFeatureCluster(nClusters, features);
    }

    public DistantDataCluster[] getFeatureCluster(int nClusters, List<Feature> features)
    {
        Collection<DistantDatum> fCollection = new ArrayList<>();
        fCollection.addAll(features);
        kMeansDistant kMeans = new kMeansDistant(nClusters, fCollection);
        return kMeans.calculateClusters();
    }

}
