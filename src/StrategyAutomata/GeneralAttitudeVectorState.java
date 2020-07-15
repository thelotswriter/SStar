package StrategyAutomata;

import Attitudes.Attitude;
import Attitudes.AttitudeVector;

public class GeneralAttitudeVectorState implements GeneralState
{

    private AttitudeVector aDisplayed;
    private AttitudeVector aSaid;
    private AttitudeVector oDisplayed;
    private AttitudeVector oSaid;

    public GeneralAttitudeVectorState(AttitudeVector attitudeDisplayed,
                                      AttitudeVector attitudeSaid, AttitudeVector otherDisplayed,
                                      AttitudeVector otherSaid)
    {
        aDisplayed = attitudeDisplayed;
        aSaid = attitudeSaid;
        oDisplayed = otherDisplayed;
        oSaid = otherSaid;
    }

}
