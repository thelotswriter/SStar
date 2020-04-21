package Features;

import java.util.ArrayList;
import java.util.List;

public class FeatureList
{

    private List<Feature> features;
    private double[] aVWeights;

    public FeatureList(List<Feature> listOfFeatures)
    {
        aVWeights = new double[4];
        aVWeights[0] = 1;
        aVWeights[1] = 1;
        aVWeights[2] = 1;
        aVWeights[3] = 1;
        features = new ArrayList<>();
        features.addAll(listOfFeatures);
    }

    public int size()
    {
        return features.size();
    }

    public boolean isEmpty()
    {
        return features.isEmpty();
    }

    public List<Feature> getListOfFeatures()
    {
        return features;
    }

    public Feature getFeature(int index)
    {
        return features.get(index);
    }

    public int getNumMatches(double threshhold, FeatureList otherFeatureList)
    {
        int nMatches = 0;
        boolean[] matched = new boolean[otherFeatureList.size()];
        for(int i = 0; i < this.size(); i++)
        {
            for(int j = 0; j < otherFeatureList.size(); j++)
            {
                if(!matched[j])
                {
                    double[] dist = features.get(i).measureDistances(otherFeatureList.getFeature(j));
                    double wSum = aVWeights[0] * dist[0] + aVWeights[1] * dist[1] + aVWeights[2] * dist[2] + aVWeights[3] * dist[3];
                    if(wSum <= threshhold)
                    {
                        nMatches++;
                        matched[j] = true;
                    }
                }
            }
        }
        return nMatches;
    }

}
