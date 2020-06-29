package Clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class kMeansClose
{

    private CloseDataCluster[] clusters;
    private List<CloseDatum> cdLists;
    private boolean kMeansDone;

    public kMeansClose(int k, Collection<CloseDatum> closeData)
    {
        clusters = new CloseDataCluster[k];
        cdLists = new ArrayList<>();
        cdLists.addAll(closeData);
        kMeansDone = false;
    }

    public CloseDataCluster[] calculateClusters()
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
        CloseDatum[] centroids = new CloseDatum[clusters.length];
        for(int i = 0; i < initialCentroidIndicess.length; i++)
        {
            centroids[i] = cdLists.get(initialCentroidIndicess[i]);
            clusters[i] = new CloseDataCluster(centroids[i]);
        }
        for(CloseDatum cd : cdLists)
        {
            boolean isCentroid = false;
            for(int centroidNum = 0; centroidNum < centroids.length; centroidNum++)
            {
                if(cd == centroids[centroidNum])
                {
                    isCentroid = true;
                    break;
                }
            }
            if(!isCentroid)
            {
                CloseDataCluster bestCluster = null;
                double closest = Double.MIN_VALUE;
                for(CloseDataCluster cluster : clusters)
                {
                    double closeness = cluster.getCentroid().getCloseness(cd);
                    if(closeness > closest)
                    {
                        closest = closeness;
                        bestCluster = cluster;
                    }
                }
                bestCluster.add(cd);
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
            List<kMeansClose.cdTransferNode> transferNodes = new ArrayList<>();
            for(CloseDatum cd : cdLists)
            {
                CloseDataCluster closestCluster = null;
                double closest = Double.MIN_VALUE;
                for(CloseDataCluster cluster : clusters)
                {
                    CloseDatum centroid = cluster.getCentroid();
                    double closeness = cd.getCloseness(centroid);
                    if(closeness > closest)
                    {
                        closest = closeness;
                        closestCluster = cluster;
                    }
                }
                if(closestCluster != null && !closestCluster.contains(cd))
                {
                    for(CloseDataCluster cluster : clusters)
                    {
                        if(cluster.contains(cd))
                        {
                            transferNodes.add(new kMeansClose.cdTransferNode(cd, cluster, closestCluster));
                            break;
                        }
                    }
                }
            }
            for(kMeansClose.cdTransferNode tNode : transferNodes)
            {
                tNode.getOrigin().remove(tNode.getDatum());
                tNode.getDestination().add(tNode.getDatum());
            }
            CloseDatum[] recalculatedCentroids = new CloseDatum[clusters.length];
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
            int rInt = rand.nextInt(cdLists.size());
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

    private class cdTransferNode
    {

        private CloseDatum cd;
        private CloseDataCluster origin;
        private CloseDataCluster destination;

        public cdTransferNode(CloseDatum closeDatumToMove, CloseDataCluster originCluster,
                              CloseDataCluster destinationCluster)
        {
            cd = closeDatumToMove;
            origin = originCluster;
            destination = destinationCluster;
        }

        public CloseDatum getDatum()
        {
            return cd;
        }

        public CloseDataCluster getOrigin()
        {
            return origin;
        }

        public CloseDataCluster getDestination()
        {
            return destination;
        }
    }

}
