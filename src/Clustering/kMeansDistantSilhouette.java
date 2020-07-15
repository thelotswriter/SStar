package Clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class kMeansDistantSilhouette
{

    private static kMeansDistantSilhouette SINGLETON = null;

    public static kMeansDistantSilhouette getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new kMeansDistantSilhouette();
        }
        return SINGLETON;
    }

    private kMeansDistantSilhouette()
    {

    }

    public DistantDataCluster[] doBestKMeans(int minK, int maxK,Collection<DistantDatum> distantData)
    {
        DistantDataCluster[] bestClusters = null;
        double bestSilhouette = -2;
        for(int k = minK; k <= maxK; k++)
        {
            System.out.print("k = ");
            System.out.print(k);
            kMeansDistant kMD = new kMeansDistant(k, distantData);
            DistantDataCluster[] clusters = kMD.calculateClusters();
            double silhouette = calculateSilhouette(clusters);
            System.out.print(" score = ");
            System.out.println(silhouette);
            if(silhouette > bestSilhouette)
            {
                bestSilhouette = silhouette;
                bestClusters = clusters;
            }
        }
        return bestClusters;
    }

    private double calculateSilhouette(DistantDataCluster[] clusters)
    {
        List<Double> indivSilhouttes = new ArrayList<>();
        for(DistantDataCluster cluster : clusters)
        {
            indivSilhouttes.addAll(calculateClusterSilhouettes(cluster, clusters));
        }
        if(indivSilhouttes.size() == 0)
        {
            return -1;
        }
        double sum = 0;
        for(Double silhoutte : indivSilhouttes)
        {
            sum += silhoutte;
        }
        return sum / ((double) indivSilhouttes.size());
    }

    private List<Double> calculateClusterSilhouettes(DistantDataCluster cluster, DistantDataCluster[] allClusters)
    {
        List<Double> silhouettes = new ArrayList<>();
        if(cluster.size() <= 1)
        {
            silhouettes.add(1.0);
            return silhouettes;
        }
        for(DistantDatum datum : cluster.getData())
        {
            silhouettes.add(calculateIndividualSilhouette(datum,
                    cluster, allClusters));
        }
        return silhouettes;
    }

    private double calculateIndividualSilhouette(DistantDatum datum, DistantDataCluster cluster, DistantDataCluster[] allClusters)
    {
        double similarity = calculateSimilarity(datum, cluster);
        double dissimilarity = calculateDissimilarity(datum, cluster, allClusters);
        if(similarity > dissimilarity)
        {
            return (dissimilarity / similarity) - 1;
        } else if(similarity < dissimilarity)
        {
            return 1 - (similarity / dissimilarity);
        } else
        {
            return 0;
        }
    }

    private double calculateSimilarity(DistantDatum datum, DistantDataCluster cluster)
    {
        double distSum = 0;
        for(DistantDatum otherDatum : cluster.getData())
        {
            if(datum != otherDatum)
            {
                distSum += datum.getDistance(otherDatum);
            }
        }
        return distSum / ((double) (cluster.size() - 1));
    }

    private double calculateDissimilarity(DistantDatum datum, DistantDataCluster cluster, DistantDataCluster[] allClusters)
    {
        double bestMeanDist = Double.MAX_VALUE;
        for(DistantDataCluster otherCluster : allClusters)
        {
            if(otherCluster != cluster)
            {
                double meanDistance = calculateMeanDistance(datum, otherCluster);
                if(meanDistance < bestMeanDist)
                {
                    bestMeanDist = meanDistance;
                }
            }
        }
        return bestMeanDist;
    }

    private double calculateMeanDistance(DistantDatum datum, DistantDataCluster otherCluster)
    {
        double distSum = 0;
        for(DistantDatum otherDatum : otherCluster.getData())
        {
            distSum += datum.getDistance(otherDatum);
        }
        return distSum / ((double) otherCluster.size());
    }

}
