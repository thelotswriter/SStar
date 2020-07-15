package Clustering;

import Features.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatList2DispAttVecClusts
{

    private static FeatList2DispAttVecClusts SINGLETON = null;

    public static FeatList2DispAttVecClusts getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new FeatList2DispAttVecClusts();
        }
        return SINGLETON;
    }

    private FeatList2DispAttVecClusts()
    {

    }

    public DistantDataCluster[] getDisplayedAttitudeVectorCluster(int minClusters, int nClusters, Collection<Collection<Feature>> featureList)
    {
        List<Feature> features = new ArrayList<>();
        for(Collection<Feature> fList : featureList)
        {
            features.addAll(fList);
        }
        return getAttitudeVectorCluster(minClusters, nClusters, features);
    }

    public DistantDataCluster[] getAttitudeVectorCluster(int minClusters, int nClusters, List<Feature> features)
    {
        Collection<DistantDatum> avCollection = new ArrayList<>();
        for(Feature f : features)
        {
            avCollection.add(f.getAttitudeDisplayed());
//            avCollection.add(f.getAttitudeSaid());
            avCollection.add(f.getOtherAttitudeDisplayed());
//            avCollection.add(f.getOtherAttitudeSaid());
        }
        kMeansDistantSilhouette kMeansSilhouette = kMeansDistantSilhouette.getInstance();
        return kMeansSilhouette.doBestKMeans(minClusters,nClusters,avCollection);
    }

}
