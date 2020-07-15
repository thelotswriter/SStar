package StrategyAutomata;

import Attitudes.Attitude;
import Attitudes.AttitudeVector;
import Features.Feature;

public class GeneralFeatureState implements GeneralState
{

    private Feature state;

    public GeneralFeatureState(Feature feature)
    {
        state = feature;
    }

    public Feature getState()
    {
        return state;
    }

    public boolean equals(Object o)
    {
        return state.equals(o);
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        AttitudeVector aDisplayed = state.getAttitudeDisplayed();
        AttitudeVector aSaid = state.getAttitudeSaid();
        AttitudeVector oDisplayed = state.getOtherAttitudeDisplayed();
        AttitudeVector oSaid = state.getOtherAttitudeSaid();
        b.append("(");
        b.append(aDisplayed.getGreedy());
        b.append(", ");
        b.append(aDisplayed.getPlacate());
        b.append(", ");
        b.append(aDisplayed.getCooperate());
        b.append(", ");
        b.append(aDisplayed.getAbsurd());
        b.append("), (");
        b.append(aSaid.getGreedy());
        b.append(", ");
        b.append(aSaid.getPlacate());
        b.append(", ");
        b.append(aSaid.getCooperate());
        b.append(", ");
        b.append(aSaid.getAbsurd());
        b.append("), (");
        b.append(oDisplayed.getGreedy());
        b.append(", ");
        b.append(oDisplayed.getPlacate());
        b.append(", ");
        b.append(oDisplayed.getCooperate());
        b.append(", ");
        b.append(oDisplayed.getAbsurd());
        b.append("), (");
        b.append(oSaid.getGreedy());
        b.append(", ");
        b.append(oSaid.getPlacate());
        b.append(", ");
        b.append(oSaid.getCooperate());
        b.append(", ");
        b.append(oSaid.getAbsurd());
        b.append(")");;
        return b.toString();
    }

}
