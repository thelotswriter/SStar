package Features;

import Attitudes.ActionAttitudeConverter;
import Attitudes.AttitudeVector;
import Features.Feature;
import Game.Game;
import Game.PayoffMatrix;
import Game.SpeechAct;
import Game.Player;

import java.util.ArrayList;
import java.util.List;

public class GameToFeatureList
{

    private Game game;
    private ActionAttitudeConverter converter;

//    private AttitudeVector[][] aMatrix;
//    double highestPayoff;
//    double highestOtherPayoff;
//    double highestCombinedPayoff;
//    double lowestCombinedPayoff;
//
//    double[] greedyCoords;
//    double[] placateCoords;
//    double[] cooperateCoords;
//    double[] absurdCoords;

    public GameToFeatureList(Game gamePlayed)
    {
        init(gamePlayed);
    }

    private void init(Game gamePlayed)
    {
        game = gamePlayed;
        converter = new ActionAttitudeConverter(game.getPayoffMatrix(), 1.0);
    }

    /**
     * Generates a list of features representative of the row player's strategy
     * @param neighbors Number of
     * @param predictivePower
     * @return
     */
    public List<Feature> generateFeatureList(int neighbors, double discount, double predictivePower)
    {
        List<Feature> featureList = new ArrayList<>();
        // Determine message features
        List<SpeechAct[]> messagesSaid = extractMessagesSaid(game.getPlayer1());
        List<SpeechAct[]> messagesOtherSaid = extractMessagesSaid(game.getPlayer2());
        double[] messageDiscounts = generateMessageDiscounts(messagesSaid, discount);
        double[] otherMessageDiscounts = generateMessageDiscounts(messagesOtherSaid, discount);
        //***********REVISED VERSION START***************
//        double prevIntegrity = 0;
//        double prevDeference = 0;
//        List<int[]> prevMessage = new ArrayList<>();
//        int[] prevAction = null;
//        List<int[]> prevOtherMessage = new ArrayList<>();
        List<double[]> attitudeDisplayed = new ArrayList<>();
        List<Boolean> aDNeedsAveraging = new ArrayList<>();
        List<double[]> attitudeOtherDisplayed = new ArrayList<>();
        for(int round = 0; round < game.getNumRounds(); round++)
        {
            int[] actPair = game.getActionPair(round);
            Feature feature = new Feature();
            List<int[]> actsSuggested = effectiveActionsSuggested(game.getNumRowActions(), messagesSaid.get(round));
            if(messageDiscounts[round] == 1)
            {
                actsSuggested = sortActionsSuggested(actsSuggested, messagesSaid.get(round));
//                prevMessage = actsSuggested;
            }
            List<int[]> actsOtherSuggested = effectiveActionsSuggested(game.getNumRowActions(), messagesOtherSaid.get(round));
            if(otherMessageDiscounts[round] == 1)
            {
                actsOtherSuggested = sortActionsSuggested(actsOtherSuggested, messagesOtherSaid.get(round));
//                prevOtherMessage = actsOtherSuggested;
            }
            double[] jointAttitudesSaid = combineMessages(actsSuggested);
            double[] jointAttitudesOtherSaid = combineMessages(actsOtherSuggested);
            feature.setAttitudeSaid(new AttitudeVector(jointAttitudesSaid[0], jointAttitudesSaid[1],
                    jointAttitudesSaid[2], jointAttitudesSaid[3]));
            feature.setOtherAttitudeSaid(new AttitudeVector(jointAttitudesOtherSaid[0], jointAttitudesOtherSaid[1],
                    jointAttitudesOtherSaid[2], jointAttitudesOtherSaid[3]));
            feature.setAttitudeDisplayed(converter.getAttitudeVectorFromActionPair(actPair[0], actPair[1]));
            feature.setOtherAttitudeDisplayed(converter.getAttitudeVectorFromActionPair(actPair[1], actPair[0]));
//            attitudeDisplayed.add(combineAttitudeArrays(generateAttitudeArray(actPair[0], actPair[1]),
//                    generateAverageAttitude(actPair[0]), predictivePower));
//            aDNeedsAveraging.add(true);
////            }
//            attitudeOtherDisplayed.add(combineAttitudeArrays(generateAttitudeArray(actPair[1], actPair[0]),
//                    generateAverageAttitude(actPair[1]), predictivePower));
            featureList.add(feature);
//            prevAction = actPair;
        }
        for(int f = 1; f < featureList.size(); f++)
        {
            int[] prevActPair = game.getActionPair(f - 1);
            AttitudeVector unalteredAD = featureList.get(f).getAttitudeDisplayed();
            AttitudeVector unalteredOAD = featureList.get(f).getOtherAttitudeDisplayed();
            AttitudeVector unalteredPrevAD = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
            AttitudeVector unalteredPrevAOD = converter.getAttitudeVectorFromActionPair(prevActPair[1], prevActPair[0]);
            featureList.get(f).setAttitudeDisplayed(AttitudeVector.average(unalteredPrevAD, unalteredAD));
            featureList.get(f).setOtherAttitudeDisplayed(AttitudeVector.average(unalteredPrevAOD, unalteredOAD));
        }
//        AttitudeVector prevAV = null;
//        AttitudeVector prevOtherAV = null;
//        for(int f = 0; f < featureList.size(); f++)
//        {
//            AttitudeVector oldAV = featureList.get(f).getAttitudeDisplayed();
//            AttitudeVector oldOtherAV = featureList.get(f).getOtherAttitudeDisplayed();
//            if(prevAV != null && prevOtherAV != null)
//            {
//                AttitudeVector newAV = new AttitudeVector(prevAV.getGreedy() + oldAV.getGreedy(), prevAV.getPlacate() + oldAV.getPlacate(),prevAV.getCooperate() + oldAV.getCooperate(), prevAV.getAbsurd() + oldAV.getAbsurd());
//                AttitudeVector newOtherAV = new AttitudeVector(prevOtherAV.getGreedy() + oldOtherAV.getGreedy(), prevOtherAV.getPlacate() + oldOtherAV.getPlacate(), prevOtherAV.getCooperate() + oldOtherAV.getCooperate(), prevOtherAV.getAbsurd() + oldOtherAV.getAbsurd());
//                featureList.get(f).setAttitudeDisplayed(newAV);
//                featureList.get(f).setOtherAttitudeDisplayed(newOtherAV);
//            }
//            prevAV = oldAV;
//            prevOtherAV = oldOtherAV;
//        }
//        List<double[]> unconvertedAttitudesDisplayed
//                = averageAttitudesDisplayed(attitudeDisplayed, generateDiscountArray(neighbors, discount));
//        List<double[]> unconvertedOtherAttitudesDisplayed
//                = averageAttitudesDisplayed(attitudeOtherDisplayed, generateDiscountArray(neighbors, discount));
//        for(int round = 0; round < game.getNumRounds(); round++)
//        {
//            if(aDNeedsAveraging.get(round).booleanValue())
//            {
//                featureList.get(round).setAttitudeDisplayed(convertToAttitudeVector(
//                        unconvertedAttitudesDisplayed.get(round)));
//            } else
//            {
//                featureList.get(round).setAttitudeDisplayed(
//                        convertToAttitudeVector(attitudeDisplayed.get(round)));
//            }
//            featureList.get(round).setOtherAttitudeDisplayed(
//                    convertToAttitudeVector(unconvertedOtherAttitudesDisplayed.get(round)));
//        }
        return featureList;
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

    private List<int[]> sortActionsSuggested(List<int[]> unsorted, SpeechAct[] messages)
    {
        if(unsorted.size() == 2)
        {
            for(SpeechAct message : messages)
            {
                if(message.getAffirmative() && message.size() == 1)
                {
                    int[] jointAct = message.getJointAction();
                    if(jointAct[0] != unsorted.get(0)[0] || jointAct[1] != unsorted.get(0)[1])
                    {
                        int[] second = unsorted.get(0);
                        unsorted.remove(0);
                        unsorted.add(second);
                    }
                    break;
                }
            }

        }
        return unsorted;
    }

    private List<SpeechAct[]> extractMessagesSaid(Player player)
    {
        List<SpeechAct[]> extractedMessages = new ArrayList<>();
        for(int round = 0; round < game.getNumRounds(); round++)
        {
            extractedMessages.add(player.getMessage(round));;
        }
        return extractedMessages;
    }

    private double[] generateMessageDiscounts(List<SpeechAct[]> messages, double discount)
    {
        discount = Math.min(Math.max(discount, 0), 1);
        double[] discounts = new double[game.getNumRounds()];
        boolean communicated = false;
        for(int round = 0; round < game.getNumRounds(); round++)
        {
            if(messages.get(round).length == 0)
            {
                if(communicated)
                {
                    discounts[round] = discounts[round - 1] * discount;
                }
            } else
            {
                communicated = true;
                discounts[round] = 1;
            }
        }
        return scrubDiscounts(discounts, messages);
    }

    private double[] scrubDiscounts(double[] discounts, List <SpeechAct[]> messages)
    {
        boolean conflicted = false;
        for(int round = 0; round < game.getNumRounds(); round++)
        {
            if(messages.get(round).length > 0)
            {
                conflicted = messageContradicts(messages.get(round));
            }
            if(conflicted)
            {
                discounts[round] = 0;
            }
        }
        return discounts;
    }

    private boolean messageContradicts(SpeechAct[] message)
    {
        List<SpeechAct> positiveSingleParts = new ArrayList<>();
        List<SpeechAct> positiveAlternatingParts = new ArrayList<>();
        boolean[] negated = new boolean[game.getNumRowActions()];
        int nNegated = 0;
        for(SpeechAct messagePart : message)
        {
            if(messagePart.getAffirmative())
            {
                if(messagePart.size() > 1)
                {
                    positiveAlternatingParts.add(messagePart);
                } else
                {
                    positiveSingleParts.add(messagePart);
                }
            } else
            {
                int negAct = messagePart.getJointAction()[1];
                if(!negated[negAct])
                {
                    negated[negAct] = true;
                    nNegated++;
                    if(nNegated == game.getNumRowActions())
                    {
                        return true;
                    }
                }
            }
        }
        boolean altStrat = !positiveAlternatingParts.isEmpty();
        int[][] altActs = new int[2][2];
        if(altStrat)
        {
            altActs[0][0] = positiveAlternatingParts.get(0).getJointAction()[0];
            altActs[0][1] = positiveAlternatingParts.get(0).getJointAction()[1];
            altActs[1][0] = positiveAlternatingParts.get(0).getSecondJointAction()[0];
            altActs[1][1] = positiveAlternatingParts.get(0).getSecondJointAction()[1];
            for(int i = 1; i < positiveAlternatingParts.size(); i++)
            {
                SpeechAct pAPa = positiveAlternatingParts.get(i);
                if(pAPa.getJointAction()[0] == altActs[0][0] && pAPa.getJointAction()[1] == altActs[0][1])
                {
                    if(!(pAPa.getSecondJointAction()[0] == altActs[1][0] && pAPa.getSecondJointAction()[1] == altActs[1][1]))
                    {
                        return true;
                    }
                } else if(pAPa.getJointAction()[0] == altActs[1][0] && pAPa.getJointAction()[1] == altActs[1][1])
                {
                    if(!(pAPa.getSecondJointAction()[0] == altActs[0][0] && pAPa.getSecondJointAction()[1] == altActs[0][1]))
                    {
                        return true;
                    }
                } else
                {
                    return true;
                }
                if(negated[pAPa.getJointAction()[1]] && negated[pAPa.getSecondJointAction()[1]])
                {
                    return true;
                }
            }
        }
        int[] prevSMessage = new int[2];
        if(positiveSingleParts.size() > 0)
        {
            prevSMessage = positiveSingleParts.get(0).getJointAction();
        }
        for(SpeechAct pSPart : positiveSingleParts)
        {
            if(altStrat)
            {
                int[] jointAct = pSPart.getJointAction();
                if(!((altActs[0][0] == jointAct[0] && altActs[0][1] == jointAct[1])
                        || (altActs[1][0] == jointAct[0] && altActs[1][1] == jointAct[1])))
                {
                    return true;
                }
            }
            if(negated[pSPart.getJointAction()[1]])
            {
                return true;
            }
            if(prevSMessage[0] != pSPart.getJointAction()[0]
                    || prevSMessage[1] != pSPart.getJointAction()[1])
            {
                return true;
            }
        }
        return false;
    }

    private double[] combineMessages(List<int[]> messages)
    {
        double[] combinedMessage = new double[4];
        for(int[] message : messages)
        {
            AttitudeVector selVec = converter.getAttitudeVectorFromActionPair(message[0], message[1]); // aMatrix[message[0]][message[1]];
            combinedMessage[0] += selVec.getGreedy();
            combinedMessage[1] += selVec.getPlacate();
            combinedMessage[2] += selVec.getCooperate();
            combinedMessage[3] += selVec.getAbsurd();
        }
        if(combinedMessage[0] > combinedMessage[1])
        {
            combinedMessage[0] -= combinedMessage[1];
            combinedMessage[1] = 0;
        } else if(combinedMessage[0] < combinedMessage[1])
        {
            combinedMessage[1] -= combinedMessage[0];
            combinedMessage[0] = 0;
        } else
        {
            combinedMessage[0] = 0;
            combinedMessage[1] = 0;
        }
        return combinedMessage;
    }

//    private double calculateIntegrity(double prevIntegrity, int[] prevAction, List<int[]> message, int playerAct, boolean newMessage)
//    {
//        if(newMessage)
//        {
//            if(message.get(0)[0] == playerAct)
//            {
//                return 1;
//            }
//            return -1;
//        } else if(prevIntegrity > 0)
//        {
//            if(message.size() > 1)
//            {
//                if(message.get(0)[0] == prevAction[0])
//                {
//                    if(message.get(1)[0] == playerAct)
//                    {
//                        return 1;
//                    }
//                    return 0;
//                } else
//                {
//                    if(message.get(0)[0] == playerAct)
//                    {
//                        return 1;
//                    }
//                    return 0;
//                }
//            } else
//            {
//                if(message.get(0)[0] == playerAct)
//                {
//                    return 1;
//                }
//                return 0;
//            }
//        } else
//        {
//            return 0;
//        }
//    }
//
//    private double calculateDeference(double prevDeference, int[] prevAction, List<int[]> message, int playerAct, boolean newMessage)
//    {
//        if(newMessage)
//        {
//            if(message.get(0)[1] == playerAct)
//            {
//                return 1;
//            }
//            return -1;
//        } else if(prevDeference > 0)
//        {
//            if(message.size() > 1)
//            {
//                if(message.get(0)[1] == prevAction[0])
//                {
//                    if(message.get(1)[1] == playerAct)
//                    {
//                        return 1;
//                    }
//                    return 0;
//                } else
//                {
//                    if(message.get(0)[1] == playerAct)
//                    {
//                        return 1;
//                    }
//                    return 0;
//                }
//            } else
//            {
//                if(message.get(0)[1] == playerAct)
//                {
//                    return 1;
//                }
//                return 0;
//            }
//        } else
//        {
//            return 0;
//        }
//    }
//
//    private double[] generateAverageAttitude(int actTaken)
//    {
//        double[] attitudeArray = new double[4];
//        for(int act = 0; act < game.getNumRowActions(); act++)
//        {
//            attitudeArray[0] += converter.getAttitudeVectorFromActionPair(actTaken, act).getGreedy(); //aMatrix[actTaken][act].getGreedy();
//            attitudeArray[1] += converter.getAttitudeVectorFromActionPair(actTaken, act).getPlacate(); //aMatrix[actTaken][act].getPlacate();
//            attitudeArray[2] += converter.getAttitudeVectorFromActionPair(actTaken, act).getCooperate(); //aMatrix[actTaken][act].getCooperate();
//            attitudeArray[3] += converter.getAttitudeVectorFromActionPair(actTaken, act).getAbsurd(); //aMatrix[actTaken][act].getAbsurd();
//        }
//        if(attitudeArray[0] > attitudeArray[1])
//        {
//            attitudeArray[0] -= attitudeArray[1];
//            attitudeArray[1] = 0;
//        } else if(attitudeArray[0] < attitudeArray[1])
//        {
//            attitudeArray[1] -= attitudeArray[0];
//            attitudeArray[0] = 0;
//        } else
//        {
//            attitudeArray[0] = 0;
//            attitudeArray[1] = 0;
//        }
//        double total = attitudeArray[0] + attitudeArray[1] + attitudeArray[2] + attitudeArray[3];
//        attitudeArray[0] = attitudeArray[0] / total;
//        attitudeArray[1] = attitudeArray[1] / total;
//        attitudeArray[2] = attitudeArray[2] / total;
//        attitudeArray[3] = attitudeArray[3] / total;
//        return attitudeArray;
//    }
//
//    private double[] generateAttitudeArray(int rowAct, int colAct)
//    {
//        AttitudeVector aV = new AttitudeVector(converter.getAttitudeVectorFromActionPair(rowAct, colAct)); //(aMatrix[rowAct][colAct]);
//        double[] attitudeArray = new double[4];attitudeArray[0] = aV.getGreedy();
//        attitudeArray[0] = aV.getGreedy();
//        attitudeArray[1] = aV.getPlacate();
//        attitudeArray[2] = aV.getCooperate();
//        attitudeArray[3] = aV.getAbsurd();
//         return attitudeArray;
//    }
//
//    private double[] combineAttitudeArrays(double[] pickedArray, double[] blindArray, double guessingPower)
//    {
//        double[] combinedArray = new double[4];
//        if(guessingPower >= 1)
//        {
//            combinedArray[0] = pickedArray[0];
//            combinedArray[1] = pickedArray[1];
//            combinedArray[2] = pickedArray[2];
//            combinedArray[3] = pickedArray[3];
//        } else if(guessingPower <= 0)
//        {
//            combinedArray[0] = blindArray[0];
//            combinedArray[1] = blindArray[1];
//            combinedArray[2] = blindArray[2];
//            combinedArray[3] = blindArray[3];
//        } else
//        {
//            combinedArray[0] = pickedArray[0] * guessingPower + blindArray[0] * (1 - guessingPower);
//            combinedArray[1] = pickedArray[1] * guessingPower + blindArray[1] * (1 - guessingPower);
//            combinedArray[2] = pickedArray[2] * guessingPower + blindArray[2] * (1 - guessingPower);
//            combinedArray[3] = pickedArray[3] * guessingPower + blindArray[3] * (1 - guessingPower);
//            if(combinedArray[0] > combinedArray[1])
//            {
//                combinedArray[0] -= combinedArray[1];
//                combinedArray[1] = 0;
//            } else if(combinedArray[0] < combinedArray[1])
//            {
//                combinedArray[1] -= combinedArray[0];
//                combinedArray[0] = 0;
//            } else
//            {
//                combinedArray[0] = 0;
//                combinedArray[1] = 0;
//            }
//        }
//        return combinedArray;
//    }
//
//    private double[] generateDiscountArray(int neighbors, double discount)
//    {
//        double[] discountArray = new double[neighbors * 2 + 1];
//        discountArray[neighbors] = 1;
//        if(discount < 0)
//        {
//            discount = 0;
//        } else if(discount > 1)
//        {
//            discount = 1;
//        }
//        for(int d = 1; d <= neighbors; d++)
//        {
//            discountArray[neighbors + d] = discountArray[neighbors + d - 1] * discount;
//            discountArray[neighbors - d] = discountArray[neighbors - d + 1] * discount;
//        }
//        return discountArray;
//    }
//
//    private List<double[]> averageAttitudesDisplayed(List<double[]> attitudes, double[] discountArray)
//    {
//        List<double[]> averagedAttitudesDisplayed = new ArrayList<>();
//        for(int round = 0; round < game.getNumRounds(); round++)
//        {
//            double[] avAttDisp = new double[4];
//            for(int d = 0; d < discountArray.length; d++)
//            {
//                int scanRound = round - discountArray.length / 2 + d;
//                if(scanRound >= 0 && scanRound < game.getNumRounds())
//                {
//                    avAttDisp[0] += discountArray[d] * attitudes.get(scanRound)[0];
//                    avAttDisp[1] += discountArray[d] * attitudes.get(scanRound)[1];
//                    avAttDisp[2] += discountArray[d] * attitudes.get(scanRound)[2];
//                    avAttDisp[3] += discountArray[d] * attitudes.get(scanRound)[3];
//                }
//            }
//            if(avAttDisp[0] > avAttDisp[1])
//            {
//                avAttDisp[0] -= avAttDisp[1];
//                avAttDisp[1] = 0;
//            } else if(avAttDisp[0] < avAttDisp[1])
//            {
//                avAttDisp[1] -= avAttDisp[0];
//                avAttDisp[0] = 0;
//            } else
//            {
//                avAttDisp[0] = 0;
//                avAttDisp[1] = 0;
//            }
//            averagedAttitudesDisplayed.add(avAttDisp);
//        }
//        return averagedAttitudesDisplayed;
//    }
//
//    private List<AttitudeVector> convertToAttitudeVectorList(List<double[]> unconvertedAVs)
//    {
//        List<AttitudeVector> convertedList = new ArrayList<>();
//        for(int i = 0; i < unconvertedAVs.size(); i++)
//        {
//            double[] unconv = unconvertedAVs.get(i);
//            convertedList.add(new AttitudeVector(unconv[0], unconv[1], unconv[2], unconv[3]));
//        }
//        return convertedList;
//    }
//
//    private double[] calculatePositiveIntegrity(int[] prevAction, List<int[]> message)
//    {
//        double[] intendedAttitude = new double[4];
//        if(message.size() > 1)
//        {
//            int[] act1 = message.get(0);
//            int[] act2 = message.get(1);
//            intendedAttitude = mergeActionPairs(act1, act2);
//        } else
//        {
//            int[] act = message.get(0);
//            intendedAttitude[0] = converter.getAttitudeVectorFromActionPair(act[0][1]).getGreedy(); //aMatrix[act[0]][act[1]].getGreedy();
//            intendedAttitude[1] = converter.getAttitudeVectorFromActionPair(act[0][1]).getPlacate(); //aMatrix[act[0]][act[1]].getPlacate();
//            intendedAttitude[2] = converter.getAttitudeVectorFromActionPair(act[0][1]).getCooperate(); //aMatrix[act[0]][act[1]].getCooperate();
//            intendedAttitude[3] = converter.getAttitudeVectorFromActionPair(act[0][1]).getAbsurd(); //aMatrix[act[0]][act[1]].getAbsurd();
//        }
//        return intendedAttitude;
//    }
//
//    private double[] calculatePositiveDeference(int[] prevAction, List<int[]> message)
//    {
//        double[] intendedAttitude = new double[4];
//        if(message.size() > 1)
//        {
//            int[] act1 = message.get(0);
//            int[] act2 = message.get(1);
//            intendedAttitude = mergeActionPairs(act1, act2);
//        } else
//        {
//            int[] act = message.get(0);
//            intendedAttitude[0] = converter.getAttitudeVectorFromActionPair(act[1][0]).getGreedy(); //aMatrix[act[1]][act[0]].getGreedy();
//            intendedAttitude[1] = converter.getAttitudeVectorFromActionPair(act[1][0]).getPlacate(); //aMatrix[act[1]][act[0]].getPlacate();
//            intendedAttitude[2] = converter.getAttitudeVectorFromActionPair(act[1][0]).getCooperate(); //aMatrix[act[1]][act[0]].getCooperate();
//            intendedAttitude[3] = converter.getAttitudeVectorFromActionPair(act[1][0]).getAbsurd(); //aMatrix[act[1]][act[0]].getAbsurd();
//        }
//        return intendedAttitude;
//    }
//
//    private double[] calculateNegativeIntegrity(int[] playerAction, List<int[]> message)
//    {
//        int[] intendedActionPair = new int[2];
//        intendedActionPair[0] = playerAction[0];
//        intendedActionPair[1] = message.get(0)[1];
//        double[] intendedAttitude = new double[4];
//        intendedAttitude[0] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getGreedy();
//        intendedAttitude[1] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getPlacate();
//        intendedAttitude[2] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getCooperate();
//        intendedAttitude[3] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getAbsurd();
//        return intendedAttitude;
//    }
//
//    private double[] calculateNegativeDeference(int[] playerAction, List<int[]> message)
//    {
//        int[] intendedActionPair = new int[2];
//        intendedActionPair[0] = playerAction[0];
//        intendedActionPair[1] = message.get(0)[0];
//        double[] intendedAttitude = new double[4];
//        intendedAttitude[0] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getGreedy();
//        intendedAttitude[1] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getPlacate();
//        intendedAttitude[2] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getCooperate();
//        intendedAttitude[3] = aMatrix[intendedActionPair[0]][intendedActionPair[1]].getAbsurd();
//        return intendedAttitude;
//    }
//
//    private AttitudeVector convertToAttitudeVector(double[] unconverted)
//    {
//        return new AttitudeVector(unconverted[0], unconverted[1], unconverted[2], unconverted[3]);
//    }
//
//    private double[] mergeActionPairs(int[] actPair1, int[] actPair2)
//    {
//        double[] mergedAttitude = new double[4];
//        double[] averagedPayoffs = new double[2];
//        averagedPayoffs[0] = (game.getPayoffMatrix().getRowPlayerValue(actPair1[0], actPair1[1]) +
//                game.getPayoffMatrix().getRowPlayerValue(actPair2[0], actPair2[1])) / 2;
//        averagedPayoffs[1] = (game.getPayoffMatrix().getColPlayerValue(actPair1[0], actPair1[1]) +
//                game.getPayoffMatrix().getColPlayerValue(actPair2[0], actPair2[1])) / 2;
//        if(averagedPayoffs[0] + averagedPayoffs[1] >= highestCombinedPayoff)
//        {
//            mergedAttitude[0] = 1;
//        } else if(averagedPayoffs[0] >= highestPayoff)
//        {
//            mergedAttitude[1] = 1;
//        } else if(averagedPayoffs[1] >= highestOtherPayoff)
//        {
//            mergedAttitude[2] = 1;
//        } else if(averagedPayoffs[0] + averagedPayoffs[1] <= lowestCombinedPayoff)
//        {
//            mergedAttitude[3] = 1;
//        } else
//        {
//            double distGreedy = highestPayoff - averagedPayoffs[0];
//            double distPlacate = highestOtherPayoff - averagedPayoffs[1];
//            if(distGreedy > distPlacate)
//            {
//                double[] intersection = calculateIntersection(greedyCoords[0], greedyCoords[1],
//                        cooperateCoords[0], cooperateCoords[1], absurdCoords[0],
//                        absurdCoords[1], averagedPayoffs[0], averagedPayoffs[1]);
//                double absurdDist = intersection[0] + intersection[1] - lowestCombinedPayoff;
//                double absurdValue = averagedPayoffs[0] + averagedPayoffs[1]
//                        - lowestCombinedPayoff;
//                double percentAbsurd = 1.0 - absurdValue / absurdDist;
//                double greedyDist = highestPayoff - highestCombinedPayoff / 2;
//                double greedyValue = intersection[0] - highestCombinedPayoff / 2;
//                double greedyPercent = greedyValue / greedyDist;
//                mergedAttitude[0] = greedyPercent;
//                mergedAttitude[2] = 1.0 - greedyPercent;
//                mergedAttitude[3] = percentAbsurd;
//            } else if(distGreedy < distPlacate)
//            {
//                double[] intersection = calculateIntersection(placateCoords[0], placateCoords[1],
//                        cooperateCoords[0], cooperateCoords[1], absurdCoords[0],
//                        absurdCoords[1], averagedPayoffs[0], averagedPayoffs[1]);
//                double absurdDist = intersection[0] + intersection[1] - lowestCombinedPayoff;
//                double absurdValue = averagedPayoffs[0] + averagedPayoffs[1]
//                        - lowestCombinedPayoff;
//                double percentAbsurd = 1.0 - absurdValue / absurdDist;
//                double placateDist = highestOtherPayoff - highestCombinedPayoff / 2;
//                double placateValue = intersection[1] - highestCombinedPayoff / 2;
//                double placatePercent = placateValue / placateDist;
//                mergedAttitude[1] = placatePercent;
//                mergedAttitude[2] = 1.0 - placatePercent;
//                mergedAttitude[3] = percentAbsurd;
//            } else
//            {
//                double combinedDistance = highestCombinedPayoff - lowestCombinedPayoff;
//                double combinedValue = averagedPayoffs[0]
//                        + averagedPayoffs[1] - lowestCombinedPayoff;
//                double percentCooperative = combinedValue / combinedDistance;
//                mergedAttitude[2] = percentCooperative;
//                mergedAttitude[3] = 1 - percentCooperative;
//            }
//        }
//        return mergedAttitude;
//    }

}
