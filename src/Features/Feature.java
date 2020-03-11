package Features;

import Attitudes.AttitudeVector;

public class Feature
{

    private AttitudeVector attitudeDisplayed;
    private AttitudeVector attitudeSaid;
    private AttitudeVector otherAttitudeDisplayed;
    private double integrity;
    private double deference;

    public Feature()
    {
        attitudeDisplayed = new AttitudeVector();
        attitudeSaid = new AttitudeVector();
        otherAttitudeDisplayed = new AttitudeVector();
        integrity = 0.0;
        deference = 0.0;
    }

    public Feature(AttitudeVector playerAttitudeDisplayed, AttitudeVector playerAttitudeSaid,
                   AttitudeVector otherPlayerAttitudeDisplayed, double playerIntegrity,
                   double playerDeference)
    {
        attitudeDisplayed = playerAttitudeDisplayed;
        attitudeSaid = playerAttitudeSaid;
        otherAttitudeDisplayed = otherPlayerAttitudeDisplayed;
        integrity = playerIntegrity;
        deference = playerDeference;
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

    public void setIntegrity(double newIntegrity)
    {
        integrity = newIntegrity;
    }

    public void setDeference(double newDeference)
    {
        deference = newDeference;
    }

}
