package Clustering;

import Features.Feature;
import Features.FeatureList;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeatureListCluster
{

    private Set<FeatureList> fLists;
    private FeatureList centroid;
    private boolean needsRecalculation;

    public FeatureListCluster(FeatureList featureList)
    {
        fLists = new HashSet<>();
        fLists.add(featureList);
        centroid = featureList;
        needsRecalculation = false;
    }

    public boolean add(FeatureList newFeatureList)
    {
        needsRecalculation = true;
        return fLists.add(newFeatureList);
    }

    public boolean contains(FeatureList featureList)
    {
        return fLists.contains(featureList);
    }

    public void remove(FeatureList featureList)
    {
        needsRecalculation = true;
        fLists.remove(featureList);
        if(featureList == centroid)
        {
            recalculateCentroid();
        }
    }

    public FeatureList getCentroid()
    {
        return centroid;
    }

    public void recalculateCentroid()
    {
        if(needsRecalculation)
        {
            FeatureList currentBest = null;
            int maxTotalShared = 0;
            for(FeatureList featureList : fLists)
            {
                int totalShared = 0;
                for(FeatureList otherFeatureList :fLists)
                {
                    if (featureList != otherFeatureList)
                    {
                        totalShared += featureList.getNumMatches(otherFeatureList);
                    }
                }
                if(totalShared > maxTotalShared)
                {
                    maxTotalShared = totalShared;
                    currentBest = featureList;
                }
            }
            centroid = currentBest;
        }
    }

}
