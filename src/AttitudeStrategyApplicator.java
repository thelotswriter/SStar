import Game.Game;
import StrategyTables.StrategyTable;

import java.util.Collection;

public class AttitudeStrategyApplicator
{

    private static AttitudeStrategyApplicator ourInstance = new AttitudeStrategyApplicator();

    public static AttitudeStrategyApplicator getInstance() {
        return ourInstance;
    }

    private AttitudeStrategyApplicator() {}

    public Collection<StrategyTable> applyAttitudeStrategies(Collection<StrategyTable> attitudeStrategies, Game game)
    {
        return null;
    }

}
