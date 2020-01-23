package StrategyTables;

public class StrategyPercentTable extends StrategyTable
{

    private double[][] percentTable;

    public StrategyPercentTable(int nActions, int nOtherActions, int history)
    {
        super(nActions, nOtherActions, history);
        int rows = 1;
        for(int h = 1; h <= history; h++)
        {
            rows += Math.pow(nActions * nOtherActions, h);
        }
        percentTable = new double[rows][nActions];
    }

    public void addObservation(int playerAction, int[] previousState, double value)
    {
        int r = arrayToInt(previousState);
        percentTable[r][playerAction] += value;
    }

}
