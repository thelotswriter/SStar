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

    /**
     * Creates a list of features to demonstrate the row player's
     * environment, actions, and message3s
     * @return List of Feature objects representing the row player's environment and reactions
     */
    public List<Feature> generatePlayer1FeatureList()
    {
        // Determine joint actions corresponding to each attitude
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
        // Create a matrix of AttitudeVectors based on attitudes
        AttitudeVector[][] aMatrix = new AttitudeVector[rows][cols];
        double[] greedyCoords = new double[2];
        double[] placateCoords = new double[2];
        double[] cooperateCoords = new double[2];
        double[] absurdCoords = new double[2];
        greedyCoords[0] = highestPayoff;
        greedyCoords[1] = Double.MAX_VALUE;
        for(int[] greedyPair : greedyPairs)
        {
            double otherVal = pMatrix.getColPlayerValue(greedyPair[0], greedyPair[1]);
            if(otherVal < greedyCoords[1])
            {
                greedyCoords[1] = otherVal;
            }
        }
        placateCoords[0] = Double.MAX_VALUE;
        for(int[] placatePair : placatePairs)
        {
            double val = pMatrix.getRowPlayerValue(placatePair[0], placatePair[1]);
            if(val < placateCoords[0])
            {
                placateCoords[0] = val;
            }
        }
        placateCoords[1] = highestOtherPayoff;
        cooperateCoords[0] = highestCombinedPayoff / 2;
        cooperateCoords[1] = highestCombinedPayoff / 2;
        absurdCoords[0] = lowestCombinedPayoff / 2;
        absurdCoords[1] = lowestCombinedPayoff / 2;
        for(int r = 0; r < rows; r++)
        {
            for(int c = 0; c < cols; c++)
            {
                // Determine if the joint action corresponds to any pure attitudes
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
                // If no pure attitudes are demonstrated by the joint action,
                // determine what percent of each attitude is displayed
                if(!added)
                {
                    double distGreedy = highestPayoff - pMatrix.getRowPlayerValue(r, c);
                    double distPlacate = highestOtherPayoff - pMatrix.getColPlayerValue(r, c);

                    if(distGreedy < distPlacate)
                    {
                        double[] intersection = calculateIntersection(greedyCoords[0], greedyCoords[1],
                                cooperateCoords[0], cooperateCoords[1], absurdCoords[0],
                                absurdCoords[1], pMatrix.getRowPlayerValue(r, c), pMatrix.getColPlayerValue(r, c));
                        double absurdDist = intersection[0] + intersection[2] - lowestCombinedPayoff;
                        double absurdValue = pMatrix.getRowPlayerValue(r, c) + pMatrix.getColPlayerValue(r,c)
                                - lowestCombinedPayoff;
                        double percentAbsurd = 1.0 - absurdValue / absurdDist;
                        double greedyDist = highestPayoff - highestCombinedPayoff / 2;
                        double greedyValue = intersection[0] - highestCombinedPayoff / 2;
                        double greedyPercent = greedyValue / greedyDist;
                        tempAtVec[0] = greedyPercent;
                        tempAtVec[2] = 1.0 - greedyPercent;
                        tempAtVec[3] = percentAbsurd;
                    } else if(distGreedy > distPlacate)
                    {
                        double[] intersection = calculateIntersection(placateCoords[0], placateCoords[1],
                                cooperateCoords[0], cooperateCoords[1], absurdCoords[0],
                                absurdCoords[1], pMatrix.getRowPlayerValue(r, c), pMatrix.getColPlayerValue(r, c));
                        double absurdDist = intersection[0] + intersection[2] - lowestCombinedPayoff;
                        double absurdValue = pMatrix.getRowPlayerValue(r, c) + pMatrix.getColPlayerValue(r,c)
                                - lowestCombinedPayoff;
                        double percentAbsurd = 1.0 - absurdValue / absurdDist;
                        double placateDist = highestOtherPayoff - highestCombinedPayoff / 2;
                        double placateValue = intersection[1] - highestCombinedPayoff / 2;
                        double placatePercent = placateValue / placateDist;
                        tempAtVec[0] = placatePercent;
                        tempAtVec[2] = 1.0 - placatePercent;
                        tempAtVec[3] = percentAbsurd;
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
//            feature.setAttitudeSaid();
//            feature.setOtherAttitudeDisplayed();
            feature.setIntegrity(0);
            feature.setDeference(0);
            featureList.add(feature);
        }
        return featureList;
    }

    /**
     * Calculates the intersection of two lines
     * @param x1 The x coordinate of one point on the first line
     * @param y1 The y coordinate of one point on the first line
     * @param x2 The x coordinate of another point on the first line
     * @param y2 The y coordinate of another point on the first line
     * @param x3 The x coordinate of one point on the second line
     * @param y3 The y coordinate of one point on the second line
     * @param x4 The x coordinate of another point on the second line
     * @param y4 The y coordinate of another point on the second line
     * @return The intersection of the two lines defined
     */
    private double[] calculateIntersection(double x1, double y1,
                                           double x2, double y2,
                                           double x3, double y3,
                                           double x4, double y4)
    {
        double a1 = (y2 - y1) / (x2 - x1);
        double b1 = y1 - a1 * x1;
        double a2 = (y4 - y3) / (x4 - x3);
        double b2 = y3 - a2 * x3;
        double[] intersect = new double[2];
        intersect[0] = (b1 - b2) / (a2 - a1);
        intersect[1] = a1 * intersect[0] + b1;
        return intersect;
    }

}
