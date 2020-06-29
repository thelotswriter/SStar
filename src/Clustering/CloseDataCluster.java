package Clustering;

import java.util.HashSet;
import java.util.Set;

public class CloseDataCluster
{

    private Set<CloseDatum> dataCluster;
    private CloseDatum centroid;
    private boolean recalculate;

    public CloseDataCluster(CloseDatum closeDatum)
    {
        dataCluster = new HashSet<CloseDatum>();
        centroid = closeDatum;
        recalculate = false;
    }

    public void add(CloseDatum element)
    {
        dataCluster.add(element);
        recalculate = true;
    }

    public void remove(CloseDatum element)
    {
        dataCluster.remove(element);
        recalculate = true;
    }

    public boolean contains(CloseDatum element)
    {
        return dataCluster.contains(element);
    }

    public CloseDatum getCentroid()
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
            for(CloseDatum datum : dataCluster)
            {
                centroid = datum;
            }
        } else
        {
            double maxCloseness = Double.MIN_VALUE;
            for(CloseDatum datum : dataCluster)
            {
                double totalCloseness = 0;
                for(CloseDatum otherDatum : dataCluster)
                {
                    if(datum != otherDatum)
                    {
                        totalCloseness += datum.getCloseness(otherDatum);
                    }
                }
                if(totalCloseness > maxCloseness)
                {
                    maxCloseness = totalCloseness;
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
