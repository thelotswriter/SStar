import Game.Game;

import java.util.List;

public class GameToRawFeatureList
{

    private Game game;

    public GameToRawFeatureList(Game gamePlayed)
    {
        game = gamePlayed;
    }

    public List<Feature> generatePlayer1FeatureList()
    {
        int cooperate = 0;
        int greedy = 0;
        int placate = 0;
        int absurd = 0;
        double highestPayoff = Double.MIN_VALUE;
        double highestCombinedPayoff = Double.MIN_VALUE;
        double lowestPayoff = Double.MAX_VALUE;
        double lowestCombinedPayoff = Double.MAX_VALUE;
        for(int r = 0; r < game.getPayoffMatrix().getNumRows(); r++)
        {
            for(int c = 0; c < game.getPayoffMatrix().getNumCols(); c++)
            {

            }
        }
        return null;
    }

}
