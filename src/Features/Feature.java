package Features;

import Attitudes.AttitudeVector;
import Clustering.DistantDatum;
import StrategyAutomata.GeneralState;

public class Feature implements DistantDatum, GeneralState
{

    private AttitudeVector attitudeDisplayed;
    private AttitudeVector attitudeSaid;
    private AttitudeVector otherAttitudeDisplayed;
    private AttitudeVector otherAttitudeSaid;
    private double integrity;
    private double deference;

    public Feature()
    {
        attitudeDisplayed = new AttitudeVector();
        attitudeSaid = new AttitudeVector();
        otherAttitudeDisplayed = new AttitudeVector();
        otherAttitudeSaid = new AttitudeVector();
    }

    public Feature(AttitudeVector playerAttitudeDisplayed, AttitudeVector playerAttitudeSaid,
                   AttitudeVector otherPlayerAttitudeDisplayed, AttitudeVector otherPlayerAttitudeSaid)
    {
        attitudeDisplayed = playerAttitudeDisplayed;
        attitudeSaid = playerAttitudeSaid;
        otherAttitudeDisplayed = otherPlayerAttitudeDisplayed;
        otherAttitudeSaid = otherPlayerAttitudeSaid;
    }

    public AttitudeVector getAttitudeDisplayed()
    {
        return attitudeDisplayed;
    }

    public AttitudeVector getAttitudeSaid()
    {
        return attitudeSaid;
    }

    public AttitudeVector getOtherAttitudeDisplayed()
    {
        return otherAttitudeDisplayed;
    }

    public AttitudeVector getOtherAttitudeSaid()
    {
        return otherAttitudeSaid;
    }

    public double getIntegrity()
    {
        return integrity;
    }

    public double getDeference()
    {
        return deference;
    }

    public void setAttitudeDisplayed(AttitudeVector newAttitudeDisplayed)
    {
        attitudeDisplayed = new AttitudeVector(newAttitudeDisplayed);
    }

    public void setAttitudeSaid(AttitudeVector newAttitudeSaid)
    {
        attitudeSaid = new AttitudeVector(newAttitudeSaid);
    }

    public void setOtherAttitudeDisplayed(AttitudeVector newOtherAttitudeDisplayed)
    {
        otherAttitudeDisplayed = new AttitudeVector(newOtherAttitudeDisplayed);
    }

    public void setOtherAttitudeSaid(AttitudeVector newOtherAttitudeSaid)
    {
        otherAttitudeSaid = new AttitudeVector(newOtherAttitudeSaid);
    }

    public void setIntegrity(double newIntegrity)
    {
        integrity = newIntegrity;
    }

    public void setDeference(double newDeference)
    {
        deference = newDeference;
    }

    public double distanceFrom(Feature otherFeature)
    {
        double totalDist = 0;
        totalDist += attitudeDisplayed.distanceFrom(otherFeature.attitudeDisplayed) * attitudeDisplayed.distanceFrom(otherFeature.attitudeDisplayed);
        totalDist += attitudeSaid.distanceFrom(otherFeature.attitudeSaid) * attitudeSaid.distanceFrom(otherFeature.attitudeSaid);
        totalDist += otherAttitudeDisplayed.distanceFrom(otherFeature.otherAttitudeDisplayed) * otherAttitudeDisplayed.distanceFrom(otherFeature.otherAttitudeDisplayed);
        totalDist += otherAttitudeSaid.distanceFrom(otherFeature.otherAttitudeSaid) * otherAttitudeSaid.distanceFrom(otherFeature.otherAttitudeSaid);
        return totalDist;
    }

    /**
     * Measures the distances between the four attitude vectors of the feature
     * @param otherFeature The feature to me measured against
     * @return An array consisting of distances between: attitudes displayed, attitudes said, attitudes displayed by the other player, and attitudes said by the other player
     */
    public double[] measureDistances(Feature otherFeature)
    {
        double[] distances = new double[4];
        distances[0] = attitudeDisplayed.distanceFrom(otherFeature.attitudeDisplayed) * attitudeDisplayed.distanceFrom(otherFeature.attitudeDisplayed);
        distances[1] = attitudeSaid.distanceFrom(otherFeature.attitudeSaid) * attitudeSaid.distanceFrom(otherFeature.attitudeSaid);
        distances[2] = otherAttitudeDisplayed.distanceFrom(otherFeature.otherAttitudeDisplayed) * otherAttitudeDisplayed.distanceFrom(otherFeature.otherAttitudeDisplayed);
        distances[3] = otherAttitudeSaid.distanceFrom(otherFeature.otherAttitudeSaid) * otherAttitudeSaid.distanceFrom(otherFeature.otherAttitudeSaid);
        return distances;
    }

    @Override
    public double getDistance(DistantDatum otherDatum)
    {
        if(otherDatum instanceof Feature)
        {
            double[] dists = measureDistances((Feature) otherDatum);
            return Math.sqrt((dists[0] * dists[0]) + (dists[1] * dists[1]) +
                    (dists[2] * dists[2]) + (dists[3] * dists[3]));
        } else
        {
            return Double.MAX_VALUE;
        }
    }

    public boolean equals(Object otherObject)
    {
        if(otherObject instanceof Feature)
        {
            Feature otherFeature = (Feature) otherObject;
            if((attitudeDisplayed.distanceFrom(otherFeature.getAttitudeDisplayed()) == 0) &&
                    (attitudeSaid.distanceFrom(otherFeature.getAttitudeSaid()) == 0) &&
                    (otherAttitudeDisplayed.distanceFrom(otherFeature.getOtherAttitudeDisplayed()) == 0) &&
                    (otherAttitudeSaid.distanceFrom(otherFeature.getOtherAttitudeSaid()) == 0))
            {
                return true;
            }
        }
        return false;
    }
}
