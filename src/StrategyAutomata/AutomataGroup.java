package StrategyAutomata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AutomataGroup
{

    private List<DSGeneralAutomaton> automata;
    private int[][] previousPredictions;
    private int[] previousActivePrediction;
    private DSGeneralAutomaton activeAutomaton;

    private double[] historicalAccuracy;
    private int round;

    public AutomataGroup(int splitNumber, DSGeneralAutomaton megaAutomaton)
    {
        round = 0;
        automata = new ArrayList<>();
        automata.add(megaAutomaton);
        boolean endFound = false;
        int index = 0;
        DSGeneralAutomaton automaton = megaAutomaton;
        while (!endFound)
        {
//            System.out.print("Num leaves: ");
//            System.out.println(automaton.getNumDistinctSequences());
            DSGeneralAutomaton extractedAutomaton = automaton.stripAutomaton();
            if(extractedAutomaton != null)
            {
                automata.add(extractedAutomaton);
                automaton = extractedAutomaton;
//                index++;
            } else
            {
                endFound = true;
//                System.out.print("Index: ");
//                System.out.println(index);
            }
        }
//        for(int s = 0; s < splitNumber; s++)
//        {
//            DSGeneralAutomaton largestAutomaton = null;
//            int bestCount = 0;
//            for(DSGeneralAutomaton automaton : automata)
//            {
//                int count = automaton.getTotalCount();
//                if(count > bestCount)
//                {
//                    bestCount = count;
//                    largestAutomaton = automaton;
//                }
//            }
////            DSGeneralAutomaton extractedAutomaton = largestAutomaton.extractAutomaton();
//            DSGeneralAutomaton extractedAutomaton = largestAutomaton.stripAutomaton();
//            if(extractedAutomaton != null)
//            {
//                automata.add(extractedAutomaton);
//            }
//        }
        previousPredictions = new int[automata.size()][2];
        for(int i = 0; i < previousPredictions.length; i++)
        {
            previousPredictions[i][0] = -1;
            previousPredictions[i][1] = -1;
        }
        previousActivePrediction = new int[2];
        previousActivePrediction[0] = -1;
        previousActivePrediction[1] = -1;
        activeAutomaton = automata.get(0);
        historicalAccuracy = new double[automata.size()];
    }

    public int[] getMostProbableActionAndMessage(int[] previouslyCorrect, int[][] history) {
        if (previousActivePrediction[0] != previouslyCorrect[0] || previousActivePrediction[1] != previouslyCorrect[1]) {
            for (int i = 0; i < previousPredictions.length; i++) {
                if (previousPredictions[i][0] == previouslyCorrect[0] && previousPredictions[i][1] == previouslyCorrect[1]) {
                    activeAutomaton = automata.get(i);
                    break;
                }
            }
        }
        for (int a = 0; a < automata.size(); a++)
        {
            int[] prediction = automata.get(a).getMostProbableActionAndMessage(history);
            previousPredictions[a][0] = prediction[0];
            previousPredictions[a][1] = prediction[1];
        }
        previousActivePrediction = activeAutomaton.getMostProbableActionAndMessage(history);
        round++;
        return previousActivePrediction;
    }

    /**
     *
     * @param correctnessArray Tells whether the active, then all automata were previously correct
     * @param history The history leading to the current round
     * @return An array of predictions for the action and message. First index is which automaton (active, then all), second is action (0) or message (1)
     */
    public int[][] getPredictionArray(boolean[] correctnessArray, int[][] history)
    {
        if(round > 0)
        {
            for(int h = 0; h < historicalAccuracy.length; h++)
            {
                historicalAccuracy[h] *= ((double) round - 1) / ((double) round);
                if(correctnessArray[h + 1])
                {
                    historicalAccuracy[h] += 1 / ((double) round);
                }
            }
        }
        int[][] predictionArray = new int[automata.size() + 1][2];
        if(!correctnessArray[0])
        {
            DSGeneralAutomaton prevActive = activeAutomaton;
            double highestAccuracy = 0;

            for(int a = 0; a < automata.size(); a++)
            {
                if(correctnessArray[a + 1])
                {
//                    System.out.println("SWITCH!");
                    activeAutomaton = automata.get(a);
                    break;
                }
            }
        }
        for (int a = 0; a < automata.size(); a++)
        {
            int[] prediction = automata.get(a).getMostProbableActionAndMessage(history);
            previousPredictions[a][0] = prediction[0];
            previousPredictions[a][1] = prediction[1];
            predictionArray[a + 1][0] = prediction[0];
            predictionArray[a + 1][1] = prediction[1];
        }
        previousActivePrediction = activeAutomaton.getMostProbableActionAndMessage(history);
        predictionArray[0][0] = previousActivePrediction[0];
        predictionArray[0][1] = previousActivePrediction[1];
        round++;
        return predictionArray;
    }

    public void prepForNewGame()
    {
        round = 0;
        activeAutomaton = automata.get(0);
        previousActivePrediction[0] = -1;
        previousActivePrediction[1] = -1;
        historicalAccuracy = new double[automata.size()];
    }

    public int activeAutomatonIndex()
    {
        for(int a = 1; a < automata.size(); a++)
        {
            if(automata.get(0).equals(automata.get(a)))
            {
                return a;
            }
        }
        return 0;
    }

    public int size()
    {
        return automata.size();
    }

    public String numDecisionPointsString(int minSecond)
    {
        StringBuilder b = new StringBuilder();
        b.append(automata.get(0).calculateDecisionPoints(minSecond));
        for(int a = 1; a < automata.size(); a++)
        {
            b.append(", ");
            b.append(automata.get(a).calculateDecisionPoints(minSecond));
        }
        return b.toString();
    }

}
