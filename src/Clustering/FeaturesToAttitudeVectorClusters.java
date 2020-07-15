package Clustering;

import Attitudes.AttitudeVector;
import Features.Feature;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeaturesToAttitudeVectorClusters
{

    private static FeaturesToAttitudeVectorClusters SINGLETON = null;

    public static FeaturesToAttitudeVectorClusters getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new FeaturesToAttitudeVectorClusters();
        }
        return SINGLETON;
    }

    private FeaturesToAttitudeVectorClusters()
    {

    }

    public DistantDataCluster[] getAttitudeVectorCluster(int nClusters, Collection<Collection<Feature>> featureList)
    {
        List<Feature> features = new ArrayList<>();
        for(Collection<Feature> fList : featureList)
        {
            features.addAll(fList);
        }
        return getAttitudeVectorCluster(nClusters, features);
    }

    public DistantDataCluster[] getAttitudeVectorCluster(int nClusters, List<Feature> features)
    {
        Collection<DistantDatum> avCollection = new ArrayList<>();
        for(Feature f : features)
        {
            avCollection.add(f.getAttitudeDisplayed());
            avCollection.add(f.getAttitudeSaid());
            avCollection.add(f.getOtherAttitudeDisplayed());
            avCollection.add(f.getOtherAttitudeSaid());
        }
//        kMeansDistant kMeans = new kMeansDistant(nClusters, avCollection);
        kMeansDistantSilhouette kMeansSilhouette = kMeansDistantSilhouette.getInstance();
        return kMeansSilhouette.doBestKMeans(2,nClusters,avCollection);
    }

}
