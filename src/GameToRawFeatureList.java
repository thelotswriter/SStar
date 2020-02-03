import Game.Game;
import Game.PayoffMatrix;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
        PayoffMatrix pMatrix = game.getPayoffMatrix();
        int rows = pMatrix.getNumRows();
        int cols = pMatrix.getNumCols();
        double highestPayoff = Double.MIN_VALUE;
        double highestOtherPayoff = Double.MIN_VALUE;
        double highestCombinedPayoff = Double.MIN_VALUE;
        double lowestCombinedPayoff = Double.MAX_VALUE;
        List<int[]> greedyPairs = new ArrayList<>();
        List<int[]> placatePairs = new ArrayList<>();
        List<int[]> cooperatePairs = new ArrayList<>();
        List<int[]> absurdPairs = new ArrayList<>();
        for(int r = 0; r < rows; r++)
        {
            for(int c = 0; c < cols; c++)
            {
                double playerValue = pMatrix.getRowPlayerValue(r, c);
                double otherValue = pMatrix.getColPlayerValue(r, c);
                if(playerValue > highestPayoff)
                {
                    highestPayoff = playerValue;
                    greedyPairs.clear();
                }
                if(playerValue == highestPayoff)
                {
                    int[] g = new int[2];
                    g[0] = r;
                    g[1] = c;
                    greedyPairs.add(g);
                }
                if(otherValue > highestOtherPayoff)
                {
                    highestOtherPayoff = otherValue;
                    placatePairs.clear();
                }
                if(otherValue == highestOtherPayoff)
                {
                    int[] p = new int[2];
                    p[0] = r;
                    p[1] = c;
                    placatePairs.add(p);
                }
                if((playerValue + otherValue) > highestCombinedPayoff)
                {
                    highestCombinedPayoff = playerValue + otherValue;
                    cooperatePairs.clear();
                }
                if((playerValue + otherValue) == highestCombinedPayoff)
                {
                    int[] co = new int[2];
                    co[0] = r;
                    co[1] = c;
                    cooperatePairs.add(co);
                }
                if((playerValue + otherValue) < lowestCombinedPayoff)
                {
                    lowestCombinedPayoff = playerValue + otherValue;
                    absurdPairs.clear();
                }
                if((playerValue + otherValue) == lowestCombinedPayoff)
                {
                    int[] a = new int[2];
                    a[0] = r;
                    a[1] = c;
                    absurdPairs.add(a);
                }
            }
        }
        AttitudeVector[][] aMatrix = new AttitudeVector[rows][cols];
        for(int r = 0; r < rows; r++)
        {
            for(int c = 0; c < cols; c++)
            {
                double[] tempAtVec = new double[4];
                boolean added = false;
                double value = pMatrix.getRowPlayerValue(r, c);
                double otherValue = pMatrix.getColPlayerValue(r, c);
                if(value == highestPayoff)
                {
                    tempAtVec[0] = 1;
                    added = true;
                }
                if(otherValue == highestOtherPayoff)
                {
                    tempAtVec[1] = 1;
                    added = true;
                }
                if((value + otherValue) == highestCombinedPayoff)
                {
                    tempAtVec[2] = 1;
                    added = true;
                }
                if((value + otherValue) == lowestCombinedPayoff)
                {
                    tempAtVec[3] = 1;
                    added = true;
                }
                if(!added)
                {
                    double distGreedy = highestPayoff - pMatrix.getRowPlayerValue(r, c);
                    double distPlacate = highestOtherPayoff - pMatrix.getColPlayerValue(r, c);
                    if(distGreedy < distPlacate)
                    {

                    } else if(distGreedy > distPlacate)
                    {

                    } else
                    {
                        double combinedDistance = highestCombinedPayoff - lowestCombinedPayoff;
                        double combinedValue = pMatrix.getRowPlayerValue(r, c)
                                + pMatrix.getColPlayerValue(r, c) - lowestCombinedPayoff;
                        double percentCooperative = combinedValue / combinedDistance;
                        tempAtVec[2] = percentCooperative;
                        tempAtVec[3] = 1 - percentCooperative;
                    }
                }
            }
        }
        List<Feature> featureList = new ArrayList<>();
        for(int round = 0; round < game.getNumRounds(); round++)
        {
            Feature feature = new Feature();
            int[] actionPair = game.getActionPair(round);
            AttitudeVector attitudeDisplayed = new AttitudeVector(aMatrix[actionPair[0]][actionPair[1]]);
            feature.setAttitudeDisplayed(attitudeDisplayed);
            // TODO: Set communicated features and fix Integrity/Deference
            feature.setIntegrity(0);
            feature.setDeference(0);
            featureList.add(feature);
        }
        return featureList;
    }

}
