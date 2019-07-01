import StrategyTables.StrategyTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class AttitudeStrategyLearner
{

    private static AttitudeStrategyLearner ourInstance = new AttitudeStrategyLearner();

    public static AttitudeStrategyLearner getInstance() {
        return ourInstance;
    }

    private AttitudeStrategyLearner() {}

    public Collection<StrategyTable> learnStrategies(int numStrategies, Collection<StrategyTable> attitudeStrategyData, int maxEpochs)
    {
        StrategyTable[] representativeStrategies = initializeCentroids(numStrategies, attitudeStrategyData);
        int countdownMax = 10;
        int countdown = countdownMax;
        double lastScore = 0;
        while(maxEpochs > 0 && countdown > 0)
        {
            Collection<StrategyTable>[] clusters = cluster(representativeStrategies, attitudeStrategyData);
            representativeStrategies = calculateCentroids(clusters);
            double score = score(clusters, representativeStrategies);
            if(score == lastScore)
            {
                countdown--;
            } else
            {
                countdown = countdownMax;
            }
            lastScore = score;
            maxEpochs--;
        }
        Collection<StrategyTable> learnedStrategies = new ArrayList<>();
        for(StrategyTable representativeStrategy : representativeStrategies)
        {
            learnedStrategies.add(representativeStrategy);
        }
        return learnedStrategies;
    }

    private StrategyTable[] initializeCentroids(int k, Collection<StrategyTable> strategyData)
    {
        int[] rands = new int[k];
        Random rand = new Random();
        boolean chosen = false;
        while(!chosen)
        {
            chosen = true;
            for(int i = 0; i < k; i++)
            {
                rands[i] = rand.nextInt();
                for(int j = 0; j < i; j++)
                {
                    if(rands[j] == rands[i])
                    {
                        chosen = false;
                        break;
                    }
                }
            }
        }
        ArrayList<StrategyTable> orderedData = new ArrayList<>(strategyData);
        StrategyTable[] centroids = new StrategyTable[rands.length];
        for(int i = 0; i < rands.length; i++)
        {
            centroids[i] = orderedData.get(i);
        }
        return centroids;
    }

    private Collection<StrategyTable>[] cluster(StrategyTable[] centroids, Collection<StrategyTable> data)
    {
        Collection<StrategyTable>[] clusters = new Collection[centroids.length];
        for(StrategyTable datum : data)
        {
            int closestIndex = 0;
            double bestDist = Double.POSITIVE_INFINITY;
            for(int i = 0; i < centroids.length; i++)
            {
                double dist = dist(datum, centroids[i]);
                if(dist < bestDist)
                {
                    closestIndex = i;
                    bestDist = dist;
                }
            }
            clusters[closestIndex].add(datum);
        }
        return clusters;
    }

    private StrategyTable[] calculateCentroids(Collection<StrategyTable>[] clusters)
    {
        StrategyTable[] centroids = new StrategyTable[clusters.length];
        for(int i = 0; i < clusters.length; i++)
        {
            centroids[i] = calculateCentroid(clusters[i]);
        }
        return centroids;
    }

    private StrategyTable calculateCentroid(Collection<StrategyTable> cluster)
    {
        StrategyTable currentBest = null;
        double bestTotalDist = Double.POSITIVE_INFINITY;
        for(StrategyTable table : cluster)
        {
            double currentTotalDist = 0;
            for(StrategyTable otherTable : cluster)
            {
                if(otherTable != table)
                {
                    currentTotalDist += dist(table, otherTable);
                    if(currentTotalDist > bestTotalDist)
                    {
                        break;
                    }
                }
            }
            if(currentTotalDist < bestTotalDist)
            {
                bestTotalDist = currentTotalDist;
                currentBest = table;
            }
        }
        return currentBest;
    }

    private double score(Collection<StrategyTable>[] clusters, StrategyTable[] centroids)
    {
        
        return 0;
    }

    private double dist(StrategyTable s1, StrategyTable s2)
    {
        return 0;
    }

    private double averageDistance(Collection<StrategyTable> cluster, StrategyTable dataPoint)
    {
        if(cluster.size() > 1)
        {
            boolean pointInCluster = false;
            double total = 0;
            for(StrategyTable table : cluster)
            {
                if(table != dataPoint)
                {
                    total += dist(table, dataPoint);
                } else
                {
                    pointInCluster = true;
                }
            }
            if(pointInCluster)
            {
                return total / ((double) (cluster.size() - 1));
            } else
            {
                return total / ((double) (cluster.size()));
            }
        } else if(cluster.size() == 1)
        {
            for(StrategyTable table : cluster)
            {
                if(table != dataPoint)
                {
                    return dist(table, dataPoint);
                } else
                {
                    break;
                }
            }
            return 0;
        } else
        {
            return 0;
        }
    }

}
