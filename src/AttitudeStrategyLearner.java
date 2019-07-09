import Exceptions.NotEnoughOptionsException;
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

    public Collection<StrategyTable> learnStrategies(int numStrategies, Collection<StrategyTable> attitudeStrategyData, int maxEpochs) throws NotEnoughOptionsException {
        StrategyTable[] representativeStrategies = initializeCentroids(numStrategies, attitudeStrategyData);
        int countdownMax = 10;
        int countdown = countdownMax;
        double lastScore = 0;
        while(maxEpochs > 0 && countdown > 0)
        {
            Collection<StrategyTable>[] clusters = cluster(representativeStrategies, attitudeStrategyData);
            representativeStrategies = calculateCentroids(clusters);
            double score = score(clusters);
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

    private StrategyTable[] initializeCentroids(int k, Collection<StrategyTable> strategyData) throws NotEnoughOptionsException {
        int[] rands = new int[k];
        Random rand = new Random();
        boolean chosen = false;
        int countdown = 1000;
        ArrayList<StrategyTable> orderedData = new ArrayList<>(strategyData);
        StrategyTable[] centroids = new StrategyTable[rands.length];
        while(!chosen || countdown <= 0)
        {
            if(countdown <= 0)
            {
                throw new NotEnoughOptionsException();
            }
            chosen = true;
            for(int i = 0; i < k; i++)
            {
                centroids[i] = orderedData.get(rand.nextInt(orderedData.size()));
                for(int j = 0; j < i; j++)
                {
                    if(dist(orderedData.get(rands[j]), orderedData.get(rands[i])) == 0.0)
                    {
                        chosen = false;
                        break;
                    }
                }
            }
            countdown--;
        }
        return centroids;
    }

    private Collection<StrategyTable>[] cluster(StrategyTable[] centroids, Collection<StrategyTable> data)
    {
        Collection<StrategyTable>[] clusters = new Collection[centroids.length];
        for(int i = 0; i < clusters.length; i++)
        {
            clusters[i] = new ArrayList<>();
        }
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

    private double score(Collection<StrategyTable>[] clusters)
    {
        double score = 0;
        for(Collection<StrategyTable> cluster : clusters)
        {
            score += averageSilhouette(cluster, clusters);
        }
        return score;
    }

    private double averageSilhouette(Collection<StrategyTable> cluster, Collection<StrategyTable>[] clusters)
    {
        double s = 0;
        for(StrategyTable dataPoint : cluster)
        {
            double a = 0;
            double b = Double.POSITIVE_INFINITY;
            for(Collection<StrategyTable> otherCluster : clusters)
            {
                if(cluster == otherCluster)
                {
                    for(StrategyTable otherDataPoint : cluster)
                    {
                        a += dist(dataPoint, otherDataPoint);
                    }
                    if(cluster.size() > 1)
                    {
                        a = a / ((double) cluster.size() - 1);
                    }
                } else
                {
                    double avgDissimilarity = 0;
                    for(StrategyTable otherDataPoint : otherCluster)
                    {
                        avgDissimilarity += dist(dataPoint, otherDataPoint);
                    }
                    avgDissimilarity = avgDissimilarity / ((double) otherCluster.size());
                    if(avgDissimilarity < b)
                    {
                        b = avgDissimilarity;
                    }
                }
            }
            if(a < b)
            {
                s += 1 - a / b;
            } else if(a > b)
            {
                s += b / a - 1;
            }
        }
        return s;
    }

    private double dist(StrategyTable s1, StrategyTable s2)
    {
        if(s1 == null)
        {
            boolean check = !true;
        }
        if(s2 == null)
        {
            boolean check = !false;
        }
        return s1.calculateDissimilarity(s2);
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
