package Attitudes;

import Game.PayoffMatrix;

import java.util.ArrayList;
import java.util.List;

public class ActionAttitudeConverter
{

    private PayoffMatrix pMatrix;
    private AttitudeVector[][] avMatrix;
    private AttitudeVector[][][][] combinedAVMatrix;
    private AttitudeVector[][] avPredMatrix;

    private double pPower;

    double highestPayoff;
    double highestOtherPayoff;
    double highestCombinedPayoff;
    double lowestCombinedPayoff;

    double[] greedyCoords;
    double[] placateCoords;
    double[] cooperateCoords;
    double[] absurdCoords;

    public ActionAttitudeConverter(PayoffMatrix payoffMatrix, double predPower)
    {
        pMatrix = payoffMatrix;
        pPower = predPower;
        generateAVMatrix();
        generateCombinedMatrix();
        generateAVPredMatrix();
    }

    public int[] attitudeVectorToActions(AttitudeVector attitudeVector)
    {
        double bestDistance = Double.MAX_VALUE;
        int[] closest = new int[4];
        for(int r1 = 0; r1 < combinedAVMatrix.length; r1++)
        {
            for(int c1 = 0; c1 < combinedAVMatrix[r1].length; c1++)
            {
                for(int r2 = r1; r2 < combinedAVMatrix[r1][c1].length; r2++)
                {
                    for(int c2 = c1; c2 < combinedAVMatrix[r1][c1][r2].length; c2++)
                    {
                        double dist = attitudeVector.distanceFrom(combinedAVMatrix[r1][c1][r2][c2]);
                        if(dist < bestDistance)
                        {
                            bestDistance = dist;
                            closest[0] = r1;
                            closest[1] = c1;
                            closest[2] = r2;
                            closest[3] = c2;
                        }
                    }
                }
            }
        }
        if(closest[0] != closest[2] || closest[1] != closest[3])
        {
            if(attitudeVector.distanceFrom(avMatrix[closest[0]][closest[1]]) > attitudeVector.distanceFrom(avMatrix[closest[2]][closest[3]]));
            {
                int tempr = closest[0];
                int tempc = closest[1];
                closest[0] = closest[2];
                closest[1] = closest[3];
                closest[2] = tempr;
                closest[3] = tempc;
            }
        }
        return closest;
    }

    public int[] attitudeVectorToActionsWithPPower(AttitudeVector attitudeVector)
    {
        double bestDistance = Double.MAX_VALUE;
        int[] closest = new int[2];
        for(int r = 0; r < avPredMatrix.length; r++)
        {
            for(int c = 0; c < avPredMatrix[r].length; c++)
            {
                double dist = attitudeVector.distanceFrom(avPredMatrix[r][c]);
                if(dist < bestDistance)
                {
                    bestDistance = dist;
                    closest[0] = r;
                    closest[1] = c;
                }
            }
        }
        return closest;
    }

    public int[] attitudeVectorToActionWithPrevActionPair(int[] prevActPair, AttitudeVector currentAttitudeVector)
    {
        int[] bestPair = new int[2];

        AttitudeVector avgAV;
        if(prevActPair[0] == -1 || prevActPair[1] == -1)
        {
            avgAV = new AttitudeVector(currentAttitudeVector);
        } else
        {
            AttitudeVector prevPureAV = avMatrix[prevActPair[0]][prevActPair[1]];
            avgAV = new AttitudeVector(prevPureAV.getGreedy() + currentAttitudeVector.getGreedy(),
                    prevPureAV.getPlacate() + currentAttitudeVector.getPlacate(),
                    prevPureAV.getCooperate() + currentAttitudeVector.getCooperate(),
                    prevPureAV.getAbsurd() + currentAttitudeVector.getAbsurd());
        }
        double bestDist = Double.MAX_VALUE;
        for(int r = 0; r < avMatrix.length; r++)
        {
            for(int c = 0; c < avMatrix[r].length; c++)
            {
                double dist = avgAV.distanceFrom(avMatrix[r][c]);
                if(dist < bestDist)
                {
                    bestPair[0] = r;
                    bestPair[1] = c;
                    bestDist = dist;
                }
            }
        }
        return bestPair;
    }

    public AttitudeVector getAttitudeVectorFromActionPair(int rowAct, int colAct)
    {
        if(rowAct < 0 || rowAct >= avMatrix.length)
        {
            return null;
        } else if(colAct < 0 || colAct >= avMatrix[rowAct].length)
        {
            return null;
        }
        return avMatrix[rowAct][colAct];
    }

    public AttitudeVector[][] getAttitudeVectorMatrix()
    {
        return avMatrix;
    }

    public AttitudeVector[][] getAttitudeVectorWithPPowerMatrix()
    {
        return avPredMatrix;
    }

    private void generateAVMatrix()
    {
        int rows = pMatrix.getNumRows();
        int cols = pMatrix.getNumCols();
        highestPayoff = Double.MIN_VALUE;
        highestOtherPayoff = Double.MIN_VALUE;
        highestCombinedPayoff = Double.MIN_VALUE;
        lowestCombinedPayoff = Double.MAX_VALUE;
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
        greedyCoords = calculateGreedyCoords(highestPayoff, pMatrix, greedyPairs);
        placateCoords = calculatePlacateCoords(highestOtherPayoff, pMatrix, placatePairs);
        cooperateCoords = new double[2];
        absurdCoords = new double[2];
        cooperateCoords[0] = highestCombinedPayoff / 2;
        cooperateCoords[1] = highestCombinedPayoff / 2;
        absurdCoords[0] = lowestCombinedPayoff / 2;
        absurdCoords[1] = lowestCombinedPayoff / 2;
        generateAttitudeMatrix(pMatrix, highestPayoff, highestOtherPayoff,
                highestCombinedPayoff, lowestCombinedPayoff, greedyCoords, placateCoords, cooperateCoords,
                absurdCoords);
    }

    private void generateAVPredMatrix()
    {
        avPredMatrix = new AttitudeVector[avMatrix.length][avMatrix[0].length];
        double[][][] avSplitVals = new double[avPredMatrix.length][avPredMatrix[0].length][4];
        double[][] avSplitRowVals = new double[avPredMatrix.length][4];
        for(int r = 0; r < avPredMatrix.length; r++)
        {
            for(int c = 0; c < avPredMatrix[r].length; c++)
            {
                avSplitVals[r][c][0] = avMatrix[r][c].getGreedy();
                avSplitVals[r][c][1] = avMatrix[r][c].getPlacate();
                avSplitVals[r][c][2] = avMatrix[r][c].getCooperate();
                avSplitVals[r][c][3] = avMatrix[r][c].getAbsurd();
                avSplitRowVals[r][0] += avMatrix[r][c].getGreedy();
                avSplitRowVals[r][1] += avMatrix[r][c].getPlacate();
                avSplitRowVals[r][2] += avMatrix[r][c].getCooperate();
                avSplitRowVals[r][3] += avMatrix[r][c].getAbsurd();
            }
        }
        for(int r = 0; r < avSplitRowVals.length; r++)
        {
            if(avSplitRowVals[r][0] > avSplitRowVals[r][1])
            {
                avSplitRowVals[r][0] -= avSplitRowVals[r][1];
                avSplitRowVals[r][1] = 0;
            } else if(avSplitRowVals[r][0] < avSplitRowVals[r][1])
            {
                avSplitRowVals[r][1] -= avSplitRowVals[r][0];
                avSplitRowVals[r][0] = 0;
            } else
            {
                avSplitRowVals[r][0] = 0;
                avSplitRowVals[r][1] = 0;
            }
        }
        for(int r = 0; r < avPredMatrix.length; r++)
        {
            for(int c = 0; c < avPredMatrix[r].length; c++)
            {
                double greed = pPower * avSplitVals[r][c][0] + (1 - pPower) * avSplitRowVals[r][0];
                double plac = pPower * avSplitVals[r][c][1] + (1 - pPower) * avSplitRowVals[r][1];
                double coop = pPower * avSplitVals[r][c][2] + (1 - pPower) * avSplitRowVals[r][2];
                double absu = pPower * avSplitVals[r][c][3] + (1 - pPower) * avSplitRowVals[r][3];
                avPredMatrix[r][c] = new AttitudeVector(greed, plac, coop, absu);
            }
        }
    }

    private void generateCombinedMatrix()
    {
        combinedAVMatrix = new AttitudeVector[avMatrix.length][avMatrix[0].length][avMatrix.length][avMatrix[0].length];
        for(int r1 = 0; r1 < combinedAVMatrix.length; r1++)
        {
            for(int c1 = 0; c1 < combinedAVMatrix[r1].length; c1++)
            {
                AttitudeVector av1 = avMatrix[r1][c1];
                for(int r2 = 0; r2 < combinedAVMatrix[r1][c1].length; r2++)
                {
                    for(int c2 = 0; c2 < combinedAVMatrix[r1][c1][r2].length; c2++)
                    {
                        AttitudeVector av2 = avMatrix[r2][c2];
                        combinedAVMatrix[r1][c1][r2][c2] = new AttitudeVector(av1.getGreedy() + av2.getGreedy(),
                                av1.getPlacate() + av2.getPlacate(), av1.getCooperate() + av2.getCooperate(),
                                av1.getAbsurd() + av2.getAbsurd());
                    }
                }
            }
        }
    }

    private double[] calculateGreedyCoords(double highestVal,
                                           PayoffMatrix payoffMatrix,
                                           List<int[]> greedyPairs)
    {
        double[] pair = new double[2];
        pair[0] = highestVal;
        pair[1] = Double.MAX_VALUE;
        for(int[] greedyPair : greedyPairs)
        {
            double otherVal = payoffMatrix.getColPlayerValue(greedyPair[0], greedyPair[1]);
            if(otherVal < pair[1])
            {
                pair[1] = otherVal;
            }
        }
        return pair;
    }

    private double[] calculatePlacateCoords(double highestOtherVal,
                                            PayoffMatrix payoffMatrix,
                                            List<int[]> placatePairs)
    {
        double[] pair = new double[2];
        pair[0] = Double.MAX_VALUE;
        for(int[] placatePair : placatePairs)
        {
            double val = payoffMatrix.getRowPlayerValue(placatePair[0], placatePair[1]);
            if(val < pair[0])
            {
                pair[0] = val;
            }
        }
        pair[1] = highestOtherVal;
        return pair;
    }

    /**
     * Generates the Attitudes.AttitudeVector matrix corresponding with the game
     * @param pMatrix The payoff matrix
     * @param highestPayoff The highest possible payoff for the row player
     * @param highestOtherPayoff The highest payoff for the column player
     * @param highestCombinedPayoff The highest payoff when combining the row and column player values
     * @param lowestCombinedPayoff The lowest payoff when combining the row and column player values
     * @param greedyCoords Coordinates of the greediest action's payoff
     * @param placateCoords Coordinates of the most placating action's payoff
     * @param cooperateCoords Coordinates of the most cooperative value
     * @param absurdCoords Coordinates of the most absurd values
     */
    private void generateAttitudeMatrix(PayoffMatrix pMatrix, double highestPayoff,
                                        double highestOtherPayoff, double highestCombinedPayoff,
                                        double lowestCombinedPayoff, double[] greedyCoords,
                                        double[] placateCoords, double[] cooperateCoords,
                                        double[] absurdCoords)
    {
        int rows = pMatrix.getNumRows();
        int cols = pMatrix.getNumCols();
        avMatrix = new AttitudeVector[rows][cols];
        for(int r = 0; r < rows; r++)
        {
            for(int c = 0; c < cols; c++)
            {
                // Determine if the joint action corresponds to any pure attitudes
                double[] tempAtVec = new double[4];
                boolean added = false;
                double value = pMatrix.getRowPlayerValue(r, c);
                double otherValue = pMatrix.getColPlayerValue(r, c);
                if(!(value == highestPayoff && otherValue == highestOtherPayoff))
                {
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
                        double absurdDist = intersection[0] + intersection[1] - lowestCombinedPayoff;
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
                        double absurdDist = intersection[0] + intersection[1] - lowestCombinedPayoff;
                        double absurdValue = pMatrix.getRowPlayerValue(r, c) + pMatrix.getColPlayerValue(r,c)
                                - lowestCombinedPayoff;
                        double percentAbsurd = 1.0 - absurdValue / absurdDist;
                        double placateDist = highestOtherPayoff - highestCombinedPayoff / 2;
                        double placateValue = intersection[1] - highestCombinedPayoff / 2;
                        double placatePercent = placateValue / placateDist;
                        tempAtVec[1] = placatePercent;
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
                avMatrix[r][c] = new AttitudeVector(tempAtVec[0],
                        tempAtVec[1], tempAtVec[2], tempAtVec[3]);
            }
        }
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
