package Features;

import java.util.List;

public class featureListDistanceMeasurer
{

    private featureListDistanceMeasurer SINGLETON;

    private featureListDistanceMeasurer()
    {

    }

    public featureListDistanceMeasurer getInstance()
    {
        if (SINGLETON == null)
        {
            SINGLETON = new featureListDistanceMeasurer();
        }
        return SINGLETON;
    }

    public double calculateDistance(List<Feature> featureList1, List<Feature> featureList2)
    {
        return 0;
    }

}
