package Game;

import java.util.ArrayList;

public class Game
{

    private Player player1;
    private Player player2;
    private PayoffMatrix payoffMatrix;

    private String name;

    /**
     * Creates a two player game with a payoff matrix and no players
     * @param pMatrix The payoff matrix for the game
     */
    public Game(PayoffMatrix pMatrix)
    {
        payoffMatrix = pMatrix;
        player1 = null;
        player2 = null;
    }

    /**
     * Creates a two player game with a payoff matrix and two players
     * @param pMatrix The payoff matrix for the game
     * @param player1 The first player
     * @param player2 The second player
     */
    public Game(PayoffMatrix pMatrix, Player player1, Player player2, String name)
    {
        payoffMatrix = pMatrix;
        this.player1 = player1;
        this.player2 = player2;
        this.name = name;
    }

    /**
     * Sets the payoff matrix for the game
     * @param payoffMatrix The new payoff matrix
     */
    public void setPayoffMatrix(PayoffMatrix payoffMatrix)
    {
        this.payoffMatrix = payoffMatrix;
    }

    /**
     * Sets new players for the game
     * @param player1 The new first player
     * @param player2 The new second player
     */
    public void setPlayers(Player player1, Player player2)
    {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Gives the pair of actions [player 1's action, player 2's action] at some time
     * @param round The round being queried
     * @return The pair of actions taken by the players at the specified time
     */
    public int[] getActionPair(int round)
    {
        int[] pair = new int[2];
        pair[0] = player1.getAction(round);
        pair[1] = player2.getAction(round);
        return pair;
    }

    /**
     * Gives the payoff matrix for the game
     * @return The game's payoff matrix
     */
    public PayoffMatrix getPayoffMatrix()
    {
        return payoffMatrix;
    }

    /**
     * Gives the first player
     * @return The first player
     */
    public Player getPlayer1()
    {
        return player1;
    }

    /**
     * Gives the second player
     * @return The second player
     */
    public Player getPlayer2()
    {
        return player2;
    }

    public int getNumRowActions()
    {
        return payoffMatrix.getNumRows();
    }

    public int getNumColActions()
    {
        return payoffMatrix.getNumCols();
    }

    public int getNumRounds()
    {
        return Math.min(player1.getActionHistoryLength(), player2.getActionHistoryLength());
    }

    public String getName()
    {
        return name;
    }

    /**
     * Swaps columns and rows
     */
    public Game transpose()
    {
        Game tGame = new Game(payoffMatrix.transpose());
        tGame.setName(name);
        tGame.setPlayers(player2, player1);
        return tGame;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
