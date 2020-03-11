package Clustering;

import Features.Feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeatureListCluster
{

    Set<List<Feature>> featureLists;
    List<Feature> centroid;

    public FeatureListCluster()
    {
        featureLists = new HashSet<>();
        centroid = null;
    }

    public void addFeatureList(List<Feature> featureList)
    {
        featureLists.add(featureList);
        if(featureLists.size() == 1)
        {
            centroid = featureList;
        } else
        {
            calculateCentroid();
        }
    }

    public void removeFeatureList(List<Feature> featureList)
    {
        featureLists.remove(featureList);
    }

    public List<Feature> getCentroid()
    {
        return centroid;
    }

    public int size()
    {
        return featureLists.size();
    }

    public boolean isEmpty()
    {
        return featureLists.isEmpty();
    }

    public void clear()
    {
        featureLists.clear();
    }

    private void calculateCentroid()
    {
        // TODO: This
    }

}
