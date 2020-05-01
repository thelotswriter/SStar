package Clustering;

import Features.Feature;
import Features.FeatureList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class kMeans
{

    private FeatureListCluster[] clusters;
    private List<FeatureList> fLists;
    private boolean kMeansDone;
    private double threshold;

    public kMeans(int k, List<FeatureList> featureLists)
    {
        threshold = 0.5;
        clusters = new FeatureListCluster[k];
        fLists = new ArrayList<>();
        fLists.addAll(featureLists);
        kMeansDone = false;
    }

    public kMeans(int k, List<FeatureList> featureLists, double distanceThreshold)
    {
        threshold = distanceThreshold;
        clusters = new FeatureListCluster[k];
        fLists = new ArrayList<>();
        fLists.addAll(featureLists);
        kMeansDone = false;
    }

    public FeatureListCluster[] calculateClusters()
    {
        if(!kMeansDone)
        {
            doKMeans();
            kMeansDone = true;
        }
        return clusters;
    }

    private void doKMeans()
    {
        int[] initialCentroidIndicess = generateCentroids();
        FeatureList[] centroids = new FeatureList[clusters.length];
        for(int i = 0; i < initialCentroidIndicess.length; i++)
        {
            centroids[i] = fLists.get(initialCentroidIndicess[i]);
            clusters[i] = new FeatureListCluster(centroids[i], threshold);
        }
        for(FeatureList fList : fLists)
        {
            boolean isCentroid = false;
            for(int centroidNum = 0; centroidNum < centroids.length; centroidNum++)
            {
                if(fList == centroids[centroidNum])
                {
                    isCentroid = true;
                    break;
                }
            }
            if(!isCentroid)
            {
                FeatureListCluster bestCluster = null;
                int bestNShared = -1;
                for(FeatureListCluster cluster : clusters)
                {
                    int nShared = cluster.getCentroid().getNumMatches(threshold, fList);
                    if(nShared > bestNShared)
                    {
                        bestNShared = nShared;
                        bestCluster = cluster;
                    }
                }
                bestCluster.add(fList);
            }
        }
        for(int i = 0; i < clusters.length; i++)
        {
            clusters[i].recalculateCentroid();
            centroids[i] = clusters[i].getCentroid();
        }
        boolean centroidsChanged = true;
        int timer = 10000;
        int counter = 0;
        while (centroidsChanged && timer > 0)
        {
            List<fLTransferNode> transferNodes = new ArrayList<>();
            for(FeatureList fList : fLists)
            {
                FeatureListCluster closestCluster = null;
                int mostShared = -1;
                for(FeatureListCluster cluster : clusters)
                {
                    FeatureList centroid = cluster.getCentroid();
                    int nShared = fList.getNumMatches(threshold, centroid);
                    if(nShared > mostShared)
                    {
                        mostShared = nShared;
                        closestCluster = cluster;
                    }
                }
                if(closestCluster != null && !closestCluster.contains(fList))
                {
                    for(FeatureListCluster cluster : clusters)
                    {
                        if(cluster.contains(fList))
                        {
                            transferNodes.add(new fLTransferNode(fList, cluster, closestCluster));
                        }
                    }
                }
            }
            for(fLTransferNode tNode : transferNodes)
            {
                tNode.getOrigin().remove(tNode.getFeatureList());
                tNode.getDestination().add(tNode.getFeatureList());
            }
            FeatureList[] recalculatedCentroids = new FeatureList[clusters.length];
            boolean newCentroidFound = false;
            for(int c = 0; c < clusters.length; c++)
            {
                clusters[c].recalculateCentroid();
                recalculatedCentroids[c] = clusters[c].getCentroid();
                if(centroids[c] != recalculatedCentroids[c])
                {
                    newCentroidFound = true;
                }
            }
            centroids = recalculatedCentroids;
            if(newCentroidFound)
            {
                counter = 0;
            } else
            {
                counter++;
            }
            if(counter >= 5)
            {
                centroidsChanged = false;
            }
            timer--;
        }
    }

    private int[] generateCentroids()
    {
        int[] randChoices = new int[clusters.length];
        Random rand = new Random();
        for(int i = 0; i < randChoices.length; i++)
        {
            int rInt = rand.nextInt(fLists.size());
            boolean duplicate = false;
            for(int j = 0; j < i; j++)
            {
                if(rInt == randChoices[j])
                {
                    duplicate = true;
                    break;
                }
            }
            if(!duplicate)
            {
                randChoices[i] = rInt;
            } else
            {
                i--;
            }
        }
        return randChoices;
    }

    private class fLTransferNode
    {

        private FeatureList fL;
        private FeatureListCluster origin;
        private FeatureListCluster destination;

        public fLTransferNode(FeatureList featureListToMove, FeatureListCluster originCluster,
                              FeatureListCluster destinationCluster)
        {
            fL = featureListToMove;
            origin = originCluster;
            destination = destinationCluster;
        }

        public FeatureList getFeatureList()
        {
            return fL;
        }

        public FeatureListCluster getOrigin()
        {
            return origin;
        }

        public FeatureListCluster getDestination()
        {
            return destination;
        }
    }

}
