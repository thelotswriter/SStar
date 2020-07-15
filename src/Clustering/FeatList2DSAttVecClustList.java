package Clustering;

import Attitudes.AttitudeVector;
import Features.Feature;

import java.util.List;

public class FeatList2DSAttVecClustList
{

    private static FeatList2DSAttVecClustList SINGLETON = null;

    public static FeatList2DSAttVecClustList getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new FeatList2DSAttVecClustList();
        }
        return SINGLETON;
    }

    private FeatList2DSAttVecClustList()
    {

    }

    public int[][] getClusterIndexList(List<Feature> featureList, DistantDataCluster[] dispClusters, DistantDataCluster[] saidClusters)
    {
        int[][] clusterList = new int[featureList.size()][4];
        for(int f = 0; f < featureList.size(); f++)
        {
            clusterList[f] = new int[4];
            double bestDistance1 = Double.MAX_VALUE;
            double bestDistance2 = Double.MAX_VALUE;
            double bestDistance3 = Double.MAX_VALUE;
            double bestDistance4 = Double.MAX_VALUE;
            int bestIndex1 = -1;
            int bestIndex2 = -1;
            int bestIndex3 = -1;
            int bestIndex4 = -1;
            AttitudeVector attitudeVector1 = featureList.get(f).getAttitudeDisplayed();
            AttitudeVector attitudeVector2 = featureList.get(f).getAttitudeSaid();
            AttitudeVector attitudeVector3 = featureList.get(f).getOtherAttitudeDisplayed();
            AttitudeVector attitudeVector4 = featureList.get(f).getOtherAttitudeSaid();
            for(int c = 0; c < dispClusters.length; c++)
            {
                double dist1 = attitudeVector1.distanceFrom((AttitudeVector) (dispClusters[c].getCentroid()));
                double dist3 = attitudeVector3.distanceFrom((AttitudeVector) (dispClusters[c].getCentroid()));
                if(dist1 < bestDistance1)
                {
                    bestDistance1 = dist1;
                    bestIndex1 = c;
                }
                if(dist3 < bestDistance3)
                {
                    bestDistance3 = dist3;
                    bestIndex3 = c;
                }
            }
            for(int c = 0; c < saidClusters.length; c++)
            {
                double dist2 = attitudeVector2.distanceFrom((AttitudeVector) (saidClusters[c].getCentroid()));
                double dist4 = attitudeVector4.distanceFrom((AttitudeVector) (saidClusters[c].getCentroid()));
                if(dist2 < bestDistance2)
                {
                    bestDistance2 = dist2;
                    bestIndex2 = c;
                }
                if(dist4 < bestDistance4)
                {
                    bestDistance4 = dist4;
                    bestIndex4 = c;
                }
            }
            clusterList[f][0] = bestIndex1;
            clusterList[f][1] = bestIndex2;
            clusterList[f][2] = bestIndex3;
            clusterList[f][3] = bestIndex4;
        }
        return clusterList;
    }

}
