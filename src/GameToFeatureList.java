import Attitudes.Attitude;
import Game.Game;
import Game.PayoffMatrix;
import Game.SpeechAct;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameToFeatureList
{

    private Game game;

    private AttitudeVector[][] aMatrix;

    public GameToFeatureList(Game gamePlayed)
    {
        init(gamePlayed);
    }

    private void init(Game gamePlayed)
    {
        game = gamePlayed;
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
        double[] greedyCoords = calculateGreedyCoords(highestPayoff, pMatrix, greedyPairs);
        double[] placateCoords = calculatePlacateCoords(highestOtherPayoff, pMatrix, placatePairs);
        double[] cooperateCoords = new double[2];
        double[] absurdCoords = new double[2];
        cooperateCoords[0] = highestCombinedPayoff / 2;
        cooperateCoords[1] = highestCombinedPayoff / 2;
        absurdCoords[0] = lowestCombinedPayoff / 2;
        absurdCoords[1] = lowestCombinedPayoff / 2;
        generateAttitudeMatrix(pMatrix, highestPayoff, highestOtherPayoff,
                highestCombinedPayoff, lowestCombinedPayoff, greedyCoords, placateCoords, cooperateCoords,
                absurdCoords);
    }

    /**
     * Creates a list of features to demonstrate the row player's
     * environment, actions, and message3s
     * @return List of Feature objects representing the row player's environment and reactions
     */
    public List<Feature> generateRawFeatureList()
    {
        List<Feature> featureList = new ArrayList<>();
        for(int round = 0; round < game.getNumRounds(); round++)
        {
//            System.out.println(round);
            Feature feature = new Feature();
            int[] actionPair = game.getActionPair(round);
//            AttitudeVector attitudeDisplayed = new AttitudeVector(aMatrix[actionPair[0]][actionPair[1]]);
//            feature.setAttitudeDisplayed(attitudeDisplayed);
            AttitudeVector attitudeDisplayed = new AttitudeVector(aMatrix[actionPair[0]][actionPair[1]]);
            feature.setAttitudeDisplayed(attitudeDisplayed);
            SpeechAct[] messages = game.getPlayer1().getMessage(round);
            List<int[]> actsSuggested = effectiveActionsSuggested(game.getNumRowActions(), messages);
            if(actsSuggested.isEmpty())
            {
                feature.setAttitudeSaid(new AttitudeVector(0, 0, 0, 0));
                feature.setIntegrity(0);
            } else
            {
                boolean alignedAction = false;
                double[] combinedAttitudes = new double[4];
                for(int[] actSuggested : actsSuggested)
                {
                    if(!alignedAction && actSuggested[0] == actionPair[0])
                    {
                        alignedAction = true;
                        feature.setIntegrity(1);
                    }
                    AttitudeVector aV = aMatrix[actSuggested[0]][actSuggested[1]];
                    combinedAttitudes[0] += aV.getGreedy();
                    combinedAttitudes[1] += aV.getPlacate();
                    combinedAttitudes[2] += aV.getCooperate();
                    combinedAttitudes[3] += aV.getAbsurd();
                }
                if(!alignedAction)
                {
                    feature.setIntegrity(-1);
                }
                if(combinedAttitudes[0] > combinedAttitudes[1])
                {
                    combinedAttitudes[0] -= combinedAttitudes[1];
                    combinedAttitudes[1] = 0;
                } else if(combinedAttitudes[0] < combinedAttitudes[1])
                {
                    combinedAttitudes[1] -= combinedAttitudes[0];
                    combinedAttitudes[0] = 0;
                } else
                {
                    combinedAttitudes[0] = 0;
                    combinedAttitudes[1] = 1;
                }
                if(combinedAttitudes[2] > combinedAttitudes[3])
                {
                    combinedAttitudes[2] -= combinedAttitudes[3];
                    combinedAttitudes[3] = 0;
                } else if(combinedAttitudes[2] < combinedAttitudes[3])
                {
                    combinedAttitudes[3] -= combinedAttitudes[2];
                    combinedAttitudes[2] = 0;
                } else
                {
                    combinedAttitudes[2] = 0;
                    combinedAttitudes[3] = 0;
                }
                feature.setAttitudeSaid(new AttitudeVector(combinedAttitudes[0],
                        combinedAttitudes[1], combinedAttitudes[2], combinedAttitudes[3]));
            }
            AttitudeVector otherAttitudeDisplayed = new AttitudeVector(aMatrix[actionPair[1]][actionPair[0]]);
            feature.setOtherAttitudeDisplayed(otherAttitudeDisplayed);
//            feature.setIntegrity(0);
            //*********************************
            if(actsSuggested.isEmpty())
            {
                feature.setIntegrity(0);
            } else
            {
                boolean isAligned = false;
                for(int[] actSuggested : actsSuggested)
                {
                    if(!isAligned && actSuggested[0] == actionPair[0])
                    {
                        feature.setIntegrity(1);
                        isAligned = true;
                    }
                }
                if(!isAligned)
                {
                    feature.setIntegrity(-1);
                }
            }
            SpeechAct[] otherMessages = game.getPlayer2().getMessage(round);
            List<int[]> otherActsSuggested = effectiveActionsSuggested(game.getNumRowActions(), otherMessages);
            if(otherActsSuggested.isEmpty())
            {
                feature.setDeference(0);
            } else
            {
                boolean isAligned = false;
                for(int[] otherActSuggested : otherActsSuggested)
                {
                    if(!isAligned && otherActSuggested[1] == actionPair[0])
                    {
                        feature.setDeference(1);
                        isAligned = true;
                    }
                }
                if(!isAligned)
                {
                    feature.setDeference(-1);
                }
            }
            featureList.add(feature);
        }
        return featureList;
    }

    /**
     * Generates a list of features representative of the row player's strategy
     * @param neighbors Number of
     * @param predictivePower
     * @return
     */
    public List<Feature> generateFeatureList(int neighbors, double predictivePower)
    {
        List<AttitudeVector> attitudesDisplayed = new ArrayList<>();
        List<Feature> featureList = new ArrayList<>();

        return featureList;
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
    * Generates the AttitudeVector matrix corresponding with the game
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
        aMatrix = new AttitudeVector[rows][cols];
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
                aMatrix[r][c] = new AttitudeVector(tempAtVec[0],
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

    private List<int[]> effectiveActionsSuggested(int numActions, SpeechAct[]  messages)
    {
        List<SpeechAct> positiveMessages = new ArrayList<>();
        List<SpeechAct> negativeMessages = new ArrayList<>();
        for(SpeechAct message : messages)
        {
            if(message.getAffirmative())
            {
                positiveMessages.add(message);
            } else
            {
                negativeMessages.add(message);
            }
        }
        List<int[]> suggestedActions = new ArrayList<>();
        if(!(positiveMessages.isEmpty() || negativeMessages.isEmpty()))
        {
            boolean[] isDenied = new boolean[numActions];
            int numDenied = 0;
            for(SpeechAct message : negativeMessages)
            {
                int actDenied = message.getJointAction()[1];
                if(!isDenied[actDenied])
                {
                    isDenied[actDenied] = true;
                    numDenied++;
                }
                if(numDenied == numActions)
                {
                    return suggestedActions;
                }
            }
            for(SpeechAct message : positiveMessages)
            {
                if(!isDenied[message.getJointAction()[1]])
                {
                    return suggestedActions;
                }
            }
        }
        if(!positiveMessages.isEmpty())
        {
            boolean[][] messageAdded = new boolean[numActions][numActions];
            for(SpeechAct message : positiveMessages)
            {
                int[] suggestion1 = message.getJointAction();
                if(!messageAdded[suggestion1[0]][suggestion1[1]])
                {
                    suggestedActions.add(suggestion1);
                    messageAdded[suggestion1[0]][suggestion1[1]] = true;
                }
                if(message.size() > 1)
                {
                    int[] suggestion2 = message.getSecondJointAction();
                    if(!messageAdded[suggestion2[0]][suggestion2[1]])
                    {
                        suggestedActions.add(suggestion2);
                        messageAdded[suggestion2[0]][suggestion2[1]] = true;
                    }
                }
            }
        } else if(!negativeMessages.isEmpty())
        {
            boolean[] isDenied = new boolean[numActions];
            int numDenied = 0;
            for(SpeechAct message : negativeMessages)
            {
                int actDenied = message.getJointAction()[1];
                if(!isDenied[actDenied])
                {
                    isDenied[actDenied] = true;
                    numDenied++;
                }
                if(numDenied == numActions)
                {
                    return suggestedActions;
                }
                for(int c = 0; c < numActions; c++)
                {
                    if(!isDenied[c])
                    {
                        for(int r = 0; r < numActions; r++)
                        {
                            int[] possibleAction = new int[2];
                            possibleAction[0] = r;
                            possibleAction[1] = c;
                            suggestedActions.add(possibleAction);
                        }
                    }
                }
            }
        }
        return suggestedActions;
    }

}
