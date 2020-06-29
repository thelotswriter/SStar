package Clustering;

import Features.FeatureList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class kMeansDistant
{

    private DistantDataCluster[] clusters;
    private List<DistantDatum> ddLists;
    private boolean kMeansDone;

    public kMeansDistant(int k,Collection<DistantDatum> distantData)
    {
        clusters = new DistantDataCluster[k];
        ddLists = new ArrayList<>();
        ddLists.addAll(distantData);
        kMeansDone = false;
    }

    public DistantDataCluster[] calculateClusters()
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
        DistantDatum[] centroids = new DistantDatum[clusters.length];
        for(int i = 0; i < initialCentroidIndicess.length; i++)
        {
            centroids[i] = ddLists.get(initialCentroidIndicess[i]);
            clusters[i] = new DistantDataCluster(centroids[i]);
        }
        for(DistantDatum dd : ddLists)
        {
            boolean isCentroid = false;
            for(int centroidNum = 0; centroidNum < centroids.length; centroidNum++)
            {
                if(dd == centroids[centroidNum])
                {
                    isCentroid = true;
                    break;
                }
            }
            if(!isCentroid)
            {
                DistantDataCluster bestCluster = null;
                double bestDistance = Double.MAX_VALUE;
                for(DistantDataCluster cluster : clusters)
                {
                    double distance = cluster.getCentroid().getDistance(dd);
                    if(bestDistance > distance)
                    {
                        bestDistance = distance;
                        bestCluster = cluster;
                    }
                }
                bestCluster.add(dd);
            }
        }
        for(int i = 0; i < clusters.length; i++)
        {
            clusters[i].calculateCentroid();
            centroids[i] = clusters[i].getCentroid();
        }
        boolean centroidsChanged = true;
        int timer = 10000;
        int counter = 0;
        while (centroidsChanged && timer > 0)
        {
            List<kMeansDistant.ddTransferNode> transferNodes = new ArrayList<>();
            for(DistantDatum dd : ddLists)
            {
                DistantDataCluster closestCluster = null;
                double bestDistance = Double.MAX_VALUE;
                for(DistantDataCluster cluster : clusters)
                {
                    DistantDatum centroid = cluster.getCentroid();
                    double distance = dd.getDistance(centroid);
                    if(distance < bestDistance)
                    {
                        bestDistance = distance;
                        closestCluster = cluster;
                    }
                }
                if(closestCluster != null && !closestCluster.contains(dd))
                {
                    for(DistantDataCluster cluster : clusters)
                    {
                        if(cluster.contains(dd))
                        {
                            transferNodes.add(new kMeansDistant.ddTransferNode(dd, cluster, closestCluster));
                            break;
                        }
                    }
                }
            }
            for(kMeansDistant.ddTransferNode tNode : transferNodes)
            {
                tNode.getOrigin().remove(tNode.getDatum());
                tNode.getDestination().add(tNode.getDatum());
            }
            DistantDatum[] recalculatedCentroids = new DistantDatum[clusters.length];
            boolean newCentroidFound = false;
            for(int c = 0; c < clusters.length; c++)
            {
                clusters[c].calculateCentroid();
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
            int rInt = rand.nextInt(ddLists.size());
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

    private class ddTransferNode
    {

        private DistantDatum dd;
        private DistantDataCluster origin;
        private DistantDataCluster destination;

        public ddTransferNode(DistantDatum distantDatumToMove, DistantDataCluster originCluster,
                              DistantDataCluster destinationCluster)
        {
            dd = distantDatumToMove;
            origin = originCluster;
            destination = destinationCluster;
        }

        public DistantDatum getDatum()
        {
            return dd;
        }

        public DistantDataCluster getOrigin()
        {
            return origin;
        }

        public DistantDataCluster getDestination()
        {
            return destination;
        }
    }

}
