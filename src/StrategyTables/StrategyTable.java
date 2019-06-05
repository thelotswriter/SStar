package StrategyTables;

public class StrategyTable
{

    private int[][] table;
    private int nActions;
    private int nOtherActions;
    private int history;

    public StrategyTable(int nActions, int nOtherActions, int history)
    {
        this.nActions = nActions;
        this.nOtherActions = nOtherActions;
        this.history = history;
        int rows = 1;
        for(int h = 1; h <= history; h++)
        {
            rows += Math.pow(nActions * nOtherActions, h);
        }
        table = new int[rows][nActions];
    }

    public void addObservation(int playerAction, int[] previousState)
    {
        table[arrayToInt(previousState)][playerAction]++;
    }

    public int getCount(int[] state, int action)
    {
        return table[arrayToInt(state)][action]++;
    }

    public int getTotalCount(int[] state)
    {
        int row = arrayToInt(state);
        int sum = 0;
        for(int i = 0; i < table[row].length; i++)
        {
            sum += table[row][i];
        }
        return sum;
    }

    public double getPercent(int[] state, int action)
    {
        double tot = (double) getTotalCount(state);
        if(tot == 0)
        {
            return 0.0;
        } else
        {
            return ((double) getCount(state, action)) / tot;
        }
    }

    private int arrayToInt(int[] row)
    {
        int len = 0;
        if(row != null)
        {
            len = row.length;
        }
        if(len % 2 != 0)
        {
            return -1;
        }
        int rowNum = 0;
        int histIter = history;
        while (len < 2 * histIter)
        {
            rowNum += (int) Math.pow(nActions * nOtherActions, histIter);
            histIter--;
        }
        if(histIter > 0)
        {
            for(; histIter >= 0; histIter--)
            {
                rowNum += (int) (Math.pow(nActions, histIter) * Math.pow(nOtherActions, histIter + 1)) * row[2 * histIter + 1];
                rowNum += (int) (Math.pow(nActions, histIter) * Math.pow(nOtherActions, histIter)) * row[2 * histIter];
            }
        }
        return rowNum;
    }

}
