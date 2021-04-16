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
        for(DistantDatum datum : ddLists)
        {
            if(datum == null)
            {
                System.out.println("This datum null");
            }
        }
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
        DistantDatum[] centroids = generateCentroids();
        for(int c = 0; c < clusters.length; c++)
        {
            clusters[c] = new DistantDataCluster(centroids[c]);
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
                    if(cluster == null)
                    {
                        System.out.println("NULL CLUSTER");
                    }
                    if(dd == null)
                    {
                        System.out.println("NULL DD");
                    }
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
        int timer = 1000;
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

    private DistantDatum[] generateCentroids()
    {
        DistantDatum[] centroids = new DistantDatum[clusters.length];
        Random rand = new Random();
        for(int i = 0; i < centroids.length; i++)
        {
            while (centroids[i] == null)
            {
                centroids[i] = ddLists.get(rand.nextInt(ddLists.size()));
                boolean add = true;
                for(int j = 0; j < i; j++)
                {
                    if(centroids[j].equals(centroids[i]))
                    {
                        add = false;
                    }
                }
                if(!add)
                {
                    centroids[i] = null;
                }
            }
        }
        return centroids;
//        DistantDatum[] randChoices = new DistantDatum[clusters.length];
//        Random rand = new Random();
//        for(int i = 0; i < randChoices.length; i++)
//        {
//            int rInt = rand.nextInt(ddLists.size());
//            DistantDatum randChoice = ddLists.get(rInt);
//            if(randChoice == null)
//            {
//                System.out.println("NULL RANDCHOICE");
//            } else
//            {
//                System.out.println("NOT NULL RANDCHOICE");
//            }
//            boolean duplicate = false;
//            for(int j = 0; j < i; j++)
//            {
//                if(randChoice.equals(randChoices[j]))
//                {
//                    duplicate = true;
//                    break;
//                }
//            }
//            if(!duplicate)
//            {
//                randChoices[i] = randChoice;
//                System.out.print("RANDCHOICE ADDED ");
//                System.out.println(i);
//                if(randChoices[i] == null)
//                {
//                    System.out.println("THIS IS NULL");
//                } else
//                {
//                    System.out.println("THIS IS NOT NULL");
//                }
//            } else
//            {
//                i--;
//            }
//        }
//        for(int rc = 0; rc < randChoices.length; rc++)
//        {
//            System.out.print("NULL: ");
//            System.out.println(rc);
//        }
//        return randChoices;
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
