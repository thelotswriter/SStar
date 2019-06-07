package Game;

import java.util.ArrayList;
import java.util.List;

public class PayoffMatrix
{

    private double[][][] matrix;

    /**
     * Creates a new, empty payoff matrix with nChoices for both row and column player
     * @param nChoices The number of actions available to the row and column players
     */
    public PayoffMatrix(int nChoices)
    {
        matrix = new double[nChoices][nChoices][2];
    }

    /**
     * Creates a new, empty payoff matrix with a defined number of rows and columns
     * @param nRows The number of rows, determined by the action space of the row player
     * @param nCols The number of columns, determined by the action space of the column player
     */
    public PayoffMatrix(int nRows, int nCols)
    {
        matrix = new double[nRows][nCols][2];
    }

    /**
     * Creates a new payoff matrix from a 3d double array.
     * @param matrix The matrix being read from. Dimensions should be row, column, payoff pair
     */
    public PayoffMatrix(double[][][] matrix)
    {
        this.matrix = new double[matrix.length][matrix[0].length][2];
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix[i].length; j++)
            {
                for(int k = 0; k < 2; k++)
                {
                    this.matrix[i][j][k] = matrix[i][j][k];
                }
            }
        }
    }

    /**
     * Creates a new payoff matrix from a 3d list of doubles
     * @param matrix The matrix being read from. Dimensions should be row, column, payoff pair
     */
    public PayoffMatrix(List<List<List<Double>>> matrix)
    {
        this.matrix = new double[matrix.size()][matrix.get(0).size()][2];
        for(int i = 0; i < matrix.size(); i++)
        {
            for(int j = 0; j < matrix.get(i).size(); j++)
            {
                for(int k = 0; k < 2; k++)
                {
                    this.matrix[i][j][k] = matrix.get(i).get(j).get(k);
                }
            }
        }
    }

    /**
     * Gives a 2d double array representation of a matrix
     * @return The 2d double array representation of the payoff matrix
     */
    public double[][][] getMatrix()
    {
        return matrix;
    }

    /**
     * Gives the value pair for the associated row and column
     * @param row The row the value is to be retrieved from
     * @param col The column the value is to be retrieved from
     * @return The pair of values for the row and column
     */
    public double[] getValuePair(int row, int col)
    {
        return matrix[row][col];
    }

    /**
     * Gives the value obtained by the row player for the given row and column
     * @param row The row the value is to be retrieved from
     * @param col The column the value is to be retrieved from
     * @return The value obtained by the row player
     */
    public double getRowPlayerValue(int row, int col)
    {
        return matrix[row][col][0];
    }

    /**
     * Gives the value obtained by the column player for the given row and column
     * @param row The row the value is to be retrieved from
     * @param col The column the value is to be retrieved from
     * @return The value obtained by the row player
     */
    public double getColPlayerValue(int row, int col)
    {
        return matrix[row][col][1];
    }

    /**
     * Sets the row player's payoff given the row, column selected
     * @param row The row selected
     * @param col The column selected
     * @param value The payoff to the row player for making this selection
     */
    public void setRowPlayerValue(int row, int col, double value)
    {
        matrix[row][col][0] = value;
    }

    /**
     * Sets the column player's payoff given the row, column selected
     * @param row The row selected
     * @param col The column selected
     * @param value The payoff to the column player for making this selection
     */
    public void setColumnPlayerValue(int row, int col, double value)
    {
        matrix[row][col][1] = value;
    }

    /**
     * Sets the value for both player's payoff (assumed the same) when selecting the given row and column
     * @param row The row selected
     * @param col The column selected
     * @param value The payoff for both players for making this selection
     */
    public void setValue(int row, int col, double value)
    {
        matrix[row][col][0] = value;
        matrix[row][col][1] = value;
    }

    public void print()
    {
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix[i].length; j++)
            {
                System.out.print('\t');
                System.out.print(matrix[i][j][0]);
                System.out.print(", ");
                System.out.print(matrix[i][j][1]);
            }
            System.out.println();
        }
    }

}
