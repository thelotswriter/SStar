package Game;

import java.io.*;
import java.util.ArrayList;

/**
 * A class to generate games from csv files
 */
public class CSVtoGame
{

    private static CSVtoGame ourInstance = new CSVtoGame();

    public static CSVtoGame getInstance() {
        return ourInstance;
    }

    private CSVtoGame() {
    }

    /**
     * Generates the game described in the given file path
     * @param path The path to a csv file describing the game
     * @return A game, if one exists at the given path. Otherwise returns null.
     */
    public Game openFile(String path)
    {
        ArrayList<String[]> data = new ArrayList<String[]>();
        try(BufferedReader buffy = new BufferedReader(new FileReader(path)))
        {
            String line = buffy.readLine();
            if(line == null)
            {
                buffy.close();
                return null;
            }
            while ((line = buffy.readLine()) != null)
            {
                String[] cells = line.split(",");
                if(cells.length != 12)
                {
                    buffy.close();
                    return null;
                }
                data.add(cells);
            }
        } catch (FileNotFoundException e)
        {
            return null;
        } catch (IOException e)
        {
            return null;
        }
        Player[] players = generatePlayers(data);
        Player player1 = players[0];
        Player player2 = players[1];
        return new Game(new PayoffMatrix(generatePayoffMatrix(data)), player1, player2, path.substring(path.lastIndexOf("\\")));
    }

    /**
     * Generates two players based on the game data
     * @param data Information about the game and player actions
     * @return An array of two players, as described in the data
     */
    private Player[] generatePlayers(ArrayList<String[]> data)
    {
        String[] firstLine = data.get(0);
        PlayerType p1Type = PlayerType.HUMAN;
        PlayerType p2Type = PlayerType.HUMAN;
        if(!firstLine[2].equalsIgnoreCase("HUMAN"))
        {
            p1Type = PlayerType.NONHUMAN;
        }
        if(!firstLine[3].equalsIgnoreCase("HUMAN"))
        {
            p2Type = PlayerType.NONHUMAN;
        }
        Player player1 = new Player(p1Type);
        Player player2 = new Player(p2Type);
        for(int i = 0; i < data.size(); i++)
        {
            player1.setNextAction(Integer.parseInt(data.get(i)[8]));
            player2.setNextAction(Integer.parseInt(data.get(i)[9]));
        }
        Player[] players = {player1, player2};
        return players;
    }

    /**
     * Creates a new 2d array based on the game described by the data
     * @param data Information about the game and player actions
     * @return A 2d array describing the game played in the data
     */
    private double[][][] generatePayoffMatrix(ArrayList<String[]> data)
    {
        double[][][] matrix = getDefaultMatrix(data);
        boolean[][] check = new boolean[matrix.length][matrix[0].length];
        int totalChecks = check.length * check[0].length;
        int totalChecked = 0;
        for(String[] dataRow : data)
        {
            int rowAct = Integer.parseInt(dataRow[8]);
            int colAct = Integer.parseInt(dataRow[9]);
            if(!check[rowAct][colAct])
            {
                matrix[rowAct][colAct][0] = Double.parseDouble(dataRow[10]);
                matrix[rowAct][colAct][1] = Double.parseDouble(dataRow[11]);
                check[rowAct][colAct] = true;
                totalChecked++;
            }
            if(totalChecked >= totalChecks)
            {
                break;
            }
        }
        return matrix;
    }

    /**
     * Gets a default 2D array based on the named game and player action data
     * @param data Information about the game and player actions
     * @return A default matrix, ready for further refinement
     */
    private double[][][] getDefaultMatrix(ArrayList<String[]> data)
    {
        double[][][] matrix;
        if(data.get(0)[0].equalsIgnoreCase("prisoners"))
        {
            matrix = new double[2][2][2];
            matrix[0][0][0] = 0.6;
            matrix[0][0][1] = 0.6;
            matrix[0][1][0] = 0;
            matrix[0][1][1] = 1;
            matrix[1][0][0] = 1;
            matrix[1][0][1] = 0;
            matrix[1][1][0] = 0.2;
            matrix[1][1][1] = 0.2;
        } else if(data.get(0)[0].equalsIgnoreCase("alternator"))
        {
            matrix = new double[3][3][2];
            matrix[0][0][0] = 0;
            matrix[0][0][1] = 0;
            matrix[0][1][0] = 0.35;
            matrix[0][1][1] = 0.7;
            matrix[0][2][0] = 1;
            matrix[0][2][1] = 0.4;
            matrix[1][0][0] = 0.7;
            matrix[1][0][1] = 0.35;
            matrix[1][1][0] = 0.1;
            matrix[1][1][1] = 0.1;
            matrix[1][2][0] = 0.45;
            matrix[1][2][1] = 0.3;
            matrix[2][0][0] = 0.4;
            matrix[2][0][1] = 1;
            matrix[2][1][0] = 0.3;
            matrix[2][1][1] = 0.45;
            matrix[2][2][0] = 0.4;
            matrix[2][2][1] = 0.4;
        } else if(data.get(0)[0].equalsIgnoreCase("chicken2"))
        {
            matrix = new double[2][2][2];
            matrix[0][0][0] = 0;
            matrix[0][0][1] = 0;
            matrix[0][1][0] = 1;
            matrix[0][1][1] = 0.33;
            matrix[1][0][0] = 0.33;
            matrix[1][0][1] = 1;
            matrix[1][1][0] = 0.84;
            matrix[1][1][1] = 0.84;
        } else
        {
            matrix = new double[2][2][2];
        }
        for(int i = 0; i < data.size(); i++)
        {
            if(Integer.parseInt(data.get(i)[8]) > matrix.length)
            {
                int newRowCount = Integer.parseInt(data.get(i)[8]);
                double[][][] newMatrix = new double[newRowCount][matrix[i].length][2];
                for(int j = 0; j < matrix.length; j++)
                {
                    for(int k = 0; k < matrix[j].length; k++)
                    {
                        for(int l = 0; l < 2; l++)
                        {
                            newMatrix[j][k][l] = matrix[j][k][l];
                        }
                    }
                }
                matrix = newMatrix;
            }
            if(Integer.parseInt(data.get(i)[9]) > matrix[0].length)
            {
                int newColCount = Integer.parseInt(data.get(i)[9]);
                double[][][] newMatrix = new double[matrix.length][newColCount][2];
                for(int j = 0; j < matrix.length; j++)
                {
                    for(int k = 0; k < matrix[j].length; k++)
                    {
                        for(int l = 0; l < 2; l++)
                        {
                            newMatrix[j][k][l] = matrix[j][k][l];
                        }
                    }
                }
                matrix = newMatrix;
            }
        }
        return matrix;
    }

}
