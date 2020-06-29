package Clustering;

import Features.Feature;
import Game.Game;

import java.util.List;

public class FeatureListToFeatureClusterList
{

    private static FeatureListToFeatureClusterList SINGLETON = null;

    public static FeatureListToFeatureClusterList getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new FeatureListToFeatureClusterList();
        }
        return SINGLETON;
    }

    private FeatureListToFeatureClusterList()
    {

    }

    public int[] getClusterIndexList(List<Feature> featureList, DistantDataCluster[] clusters)
    {
        int[] clusterList = new int[featureList.size()];
        for(int f = 0; f < featureList.size(); f++)
        {
            double bestDistance = Double.MAX_VALUE;
            int bestIndex = -1;
            Feature feature = featureList.get(f);
            for(int c = 0; c < clusters.length; c++)
            {
                double dist = feature.distanceFrom((Feature) (clusters[c].getCentroid()));
                if(dist < bestDistance)
                {
                    bestDistance = dist;
                    bestIndex = c;
                }
            }
            clusterList[f] = bestIndex;
        }
        return clusterList;
    }



}
