package Clustering;

import java.util.HashSet;
import java.util.Set;

public class DistantDataCluster
{

    private Set<DistantDatum> dataCluster;
    private DistantDatum centroid;
    private boolean recalculate;

    public DistantDataCluster(DistantDatum distantDatum)
    {
        dataCluster = new HashSet<DistantDatum>();
        centroid = distantDatum;
        recalculate = false;
    }

    public void add(DistantDatum element)
    {
        dataCluster.add(element);
        recalculate = true;
    }

    public void remove(DistantDatum element)
    {
        dataCluster.remove(element);
        recalculate = true;
    }

    public boolean contains(DistantDatum element)
    {
        return dataCluster.contains(element);
    }

    public DistantDatum getCentroid()
    {
        return centroid;
    }

    public void calculateCentroid() {
        if (!recalculate)
        {
            return;
        }
        if(size() <= 0)
        {
            centroid = null;
        } else if(size() == 1)
        {
            for(DistantDatum datum : dataCluster)
            {
                centroid = datum;
            }
        } else
        {
            double minDistance = Double.MAX_VALUE;
            for(DistantDatum datum : dataCluster)
            {
                double totalDistance = 0;
                for(DistantDatum otherDatum : dataCluster)
                {
                    if(datum != otherDatum)
                    {
                        totalDistance += datum.getDistance(otherDatum);
                    }
                }
                if(totalDistance < minDistance)
                {
                    minDistance = totalDistance;
                    centroid = datum;
                }
            }
        }
        recalculate = false;
    }

    public int size()
    {
        return  dataCluster.size();
    }

    public boolean isEmpty()
    {
        return dataCluster.isEmpty();
    }
}
