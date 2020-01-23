package StrategyTables;

import java.util.Collection;

public class StrategyTable
{

    private int[][] table;
    private int nActions;
    private int nOtherActions;
    private int history;

    private double[][] percentTable;

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
        percentTable = null;
    }

    public void addObservation(int playerAction, int[] previousState)
    {
        int r = arrayToInt(previousState);
        table[r][playerAction]++;
        percentTable = null;
    }

    public int getHistory()
    {
        return history;
    }

    public int getCount(int[] state, int action)
    {
        return table[arrayToInt(state)][action];
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

    public double[] getObservationPercents(int[] state)
    {
        double[] percents = new double[nActions];
        for(int i = 0; i < nActions; i++)
        {
            percents[i] = getPercent(state, i);
        }
        return percents;
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

    public int getNumRows()
    {
        return table.length;
    }

    /**
     * Calculates the dissimilarity of two tables
     * @param otherTable The table whose dissimilarity is being measured from this table
     * @return A dissimilarity score, zero or greater.
     */
    public double calculateDissimilarity(StrategyTable otherTable)
    {
        if(percentTable == null)
        {
            fillPercentTable();
        }
        if(otherTable.percentTable == null)
        {
            return otherTable.calculateDissimilarity(this);
        } else
        {
            double dist = 0;
            for(int r = 0; r < percentTable.length; r++)
            {
                for(int c = 0; c < percentTable[r].length; c++)
                {
                    dist += Math.abs(percentTable[r][c] - otherTable.percentTable[r][c]);
                }
            }
            return dist;
        }
    }

    private void fillPercentTable()
    {
        percentTable = new double[table.length][table[0].length];
        for(int r = 0; r < table.length; r++)
        {
            double sum = 0;
            for(int c = 0; c < table[r].length; c++)
            {
                sum += table[r][c];
            }
            if(sum > 0)
            {
                for(int c = 0; c < table[r].length; c++)
                {
                    percentTable[r][c] = ((double) table[r][c] / sum);
                }
            }
        }
    }

    public StrategyTable generateResizedTable(int newHistory)
    {
        if(newHistory == history)
        {
            return this;
        } else if(newHistory > history)
        {
            return generateExpandedTable(newHistory);
        } else
        {
            if(newHistory < 0)
            {
                return generateResizedTable(0);
            } else
            {
                return generateCollapsedTable(newHistory);
            }
        }
    }

    private StrategyTable generateCollapsedTable(int newHistory)
    {
        StrategyTable newTable = new StrategyTable(nActions, nOtherActions, newHistory);
        //TODO: Code if needed
        return newTable;
    }

    private StrategyTable generateExpandedTable(int newHistory)
    {
        StrategyTable newTable = new StrategyTable(nActions, nOtherActions, newHistory);
        for(int r = 0; r < table.length; r++)
        {
            for(int c = 0; c < table[r].length; c++)
            {
                newTable.table[newTable.table.length - r - 1][c] = table[r][c];
            }
        }
        for(int h = newHistory; h > history; h--)
        {
            int[] index = new int[2 * h];
            while(index != null)
            {
                int[] correspondingIndex = new int[2 * history];
                for(int i = 0; i < correspondingIndex.length; i++)
                {
                    correspondingIndex[i] = index[i + 2 * (h - history)];
                }
                for(int c = 0; c < nActions; c++)
                {
                    int newRow = newTable.arrayToInt(index);
                    newTable.table[newRow][c] = table[arrayToInt(correspondingIndex)][c];
                }
                index = advanceArray(index);
            }
        }
        return newTable;
    }



    public void print()
    {
        for(int i = 0; i < table.length; i++)
        {
            for(int j = 0; j < table[i].length; j++)
            {
                System.out.print('\t');
                System.out.print(table[i][j]);
            }
            System.out.println();
        }
    }

    public static StrategyTable merge(Collection<StrategyTable> tables)
    {
        int nRows = 0;
        int nCols = 0;
        StrategyTable sT = null;
        for(StrategyTable table : tables)
        {
            if(sT == null)
            {
                sT = new StrategyTable(table.nActions, table.nOtherActions, table.history);
                nRows = table.table.length;
                nCols = table.table[0].length;
            }
            if(sT.nActions == table.nActions && sT.nOtherActions == table.nOtherActions && sT.history == table.history)
            {
                for(int i = 0; i < sT.table.length; i++)
                {
                    for(int j = 0; j < sT.table[i].length; j++)
                    {
                        sT.table[i][j] += table.table[i][j];
                    }
                }
            }
        }
        return sT;
    }

    protected int arrayToInt(int[] row)
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
        for(int i = 0; i < histIter; i++)
        {
            rowNum += row[2* (histIter - i) - 1] * ((int) Math.pow(nActions, i))* ((int) Math.pow(nOtherActions, i));
            rowNum += row[2 * (histIter - i) - 2] * ((int) Math.pow(nActions, i))* ((int) Math.pow(nOtherActions, i + 1));
        }
//        if(histIter > 0)
//        {
//            for(; histIter > 0; histIter--)
//            {
//                rowNum += (int) (Math.pow(nActions, histIter) * Math.pow(nOtherActions, histIter + 1)) * row[2 * histIter - 1];
//                rowNum += (int) (Math.pow(nActions, histIter) * Math.pow(nOtherActions, histIter)) * row[2 * histIter - 2];
//            }
//        }
        return rowNum;
    }

    private int[] advanceArray(int[] prevRow)
    {
        int finalIndex = prevRow.length - 1;
        if(prevRow[finalIndex] < nOtherActions - 1)
        {
            prevRow[finalIndex]++;
            return prevRow;
        } else
        {
            prevRow[finalIndex] = 0;
            return advanceArray(prevRow, finalIndex - 1);
        }
    }

    private int[] advanceArray(int[] prevRow, int index)
    {
        if((index % 2 == 0 && prevRow[index] < nActions - 1) || (index % 2 == 1 && prevRow[index] < nOtherActions - 1))
        {
            prevRow[index]++;
            return prevRow;
        } else if(index > 0)
        {
            prevRow[index] = 0;
            return advanceArray(prevRow, index - 1);
        } else
        {
            return null;
        }
    }

}
