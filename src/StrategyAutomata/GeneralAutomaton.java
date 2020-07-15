package StrategyAutomata;

import StrategyTables.GeneralizeStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneralAutomaton
{

    private GeneralState[] states;
    private TransitionProbNode[] transProbNodes;

    public GeneralAutomaton(int memoryLength, List<GeneralState> allStates)
    {
        states = new GeneralState[allStates.size()];
        for(int i = 0; i < allStates.size(); i++)
        {
            states[i] = allStates.get(i);
        }
        initTransitionProbNodes(memoryLength);
    }

    /**
     * Initializes transitionProbNodes
     * @param memLen How many states the automaton remembers
     */
    private void initTransitionProbNodes(int memLen)
    {
        int totalNodes = 1;
        for(int m = 0; m < memLen; m++)
        {
            totalNodes *= states.length;
        }
        transProbNodes = new TransitionProbNode[totalNodes];
        int[] stateIndex = new int[memLen];
        for(int i = 0; i < transProbNodes.length; i++)
        {
            GeneralState[] gState = new GeneralState[memLen];
            for(int j = 0; j < stateIndex.length; j++)
            {
                gState[j] = states[stateIndex[j]];
            }
            transProbNodes[i] = new TransitionProbNode(gState);
            // Advance stateIndex
            boolean updated = false;
            for(int s = stateIndex.length - 1; s >= 0 && !updated; s--)
            {
                if(stateIndex[s] == memLen - 1)
                {
                    stateIndex[s] = 0;
                } else
                {
                    stateIndex[s]++;
                    updated = true;
                }
            }
        }
    }

    public void addObservation(GeneralState[] observation)
    {
        int memLength = transProbNodes[0].memoryLength();
        if(observation.length >= memLength)
        {
            for(int i = 0; i <= observation.length - memLength - 1; i++)
            {
                GeneralState[] prior = new GeneralState[memLength];
                for(int p = 0; p < prior.length; p++)
                {
                    prior[p] = observation[i + p];
                }
                TransitionProbNode tpNode = getTransitionProbNode(prior);
                tpNode.addNextStateObservation(observation[i + memLength]);
            }
        }
    }

    public void addObservations(Collection<GeneralState[]> observations)
    {
        for(GeneralState[] observation : observations)
        {
            addObservation(observation);
        }
    }

    public void removeObservation(GeneralState[] observation)
    {
        int memLength = transProbNodes[0].memoryLength();
        if(observation.length >= memLength)
        {
            for(int i = 0; i <= observation.length - memLength - 1; i++)
            {
                GeneralState[] prior = new GeneralState[memLength];
                for(int p = 0; p < prior.length; p++)
                {
                    prior[p] = observation[i + p];
                }
                TransitionProbNode tpNode = getTransitionProbNode(prior);
                tpNode.removeNextStateObservation(observation[i + memLength]);
            }
        }
    }

    public void removeObservations(Collection<GeneralState[]> observations)
    {
        for(GeneralState[] observation : observations)
        {
            removeObservation(observation);
        }
    }

    public double[] getTransitionProbabilities(GeneralState[] priorStates)
    {
        int memLength = transProbNodes[0].memoryLength();
        if(priorStates.length < memLength)
        {
            List<TransitionProbNode> matchingPTNodes = new ArrayList<>();

            return null;
        } else
        {
            GeneralState[] prior = new GeneralState[memLength];
            for(int p = 0; p < prior.length; p++)
            {
                prior[p] = priorStates[priorStates.length + p - memLength];
            }
            TransitionProbNode tpNode = getTransitionProbNode(prior);
            return tpNode.getTransitionProbabilities();
        }
    }

    /**
     * Turns an unformatted prior of unspecified length
     * into a prior of the propper size, or null if too short
     * @param unformattedPrior A sequence of states to be used as a prior
     * @return A sequence of states of appropriate length
     */
    private GeneralState[] formatPrior(GeneralState[] unformattedPrior)
    {
        int memLength = transProbNodes[0].memoryLength();
        if(unformattedPrior.length == memLength)
        {
            return unformattedPrior;
        } else if(unformattedPrior.length > memLength)
        {
            GeneralState[] formattedPrior = new GeneralState[memLength];
            for(int i = 0; i < memLength; i++)
            {
                formattedPrior[i] = unformattedPrior[i + unformattedPrior.length - memLength];
            }
            return formattedPrior;
        } else
        {
            return null;
        }
    }

    /**
     * Gets the TransitionProbabilityNode associated with the formatted general state sequence
     * @param formattedPrior The formatted general state sequence used as the prior
     * @return The associated TransitionProbNode, or null if no result is found
     */
    private TransitionProbNode getTransitionProbNode(GeneralState[] formattedPrior)
    {
        if(formattedPrior == null)
        {
            return null;
        }
        for(int i = 0; i < transProbNodes.length; i++)
        {
            if(transProbNodes[i].priorEquals(formattedPrior))
            {
                return transProbNodes[i];
            }
        }
        return null;
    }

    public GeneralState[] getGeneralStates()
    {
        return states;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        for(TransitionProbNode tpNode : transProbNodes)
        {
            b.append(tpNode.toString());
            b.append('\n');
        }
        return b.toString();
    }

    private class TransitionProbNode
    {

        private GeneralState[] priorStates;
        private GeneralState[] nextStates;
        private int[] nextStateTallies;
        private int totalTallies;

        public TransitionProbNode(GeneralState[] priorStates)
        {
            this.priorStates = new GeneralState[priorStates.length];
            nextStates = new GeneralState[states.length];
            nextStateTallies = new int[states.length];
            for(int i = 0; i < states.length; i++)
            {
                nextStates[i] = states[i];
                nextStateTallies[i] = 0;
            }
        }

        public void addNextStateObservation(GeneralState observation)
        {
            for(int i = 0; i < nextStates.length; i++)
            {
                if(nextStates[i].equals(observation))
                {
                    nextStateTallies[i]++;
                    totalTallies++;
                    break;
                }
            }
        }

        public void removeNextStateObservation(GeneralState observation)
        {
            for(int i = 0; i < nextStates.length; i++)
            {
                if(nextStates[i].equals(observation))
                {
                    if(nextStateTallies[i] > 0)
                    {
                        nextStateTallies[i]--;
                        totalTallies--;
                    }
                    break;
                }
            }
        }

        public double[] getTransitionProbabilities()
        {
            double[] probabilities = new double[nextStates.length];
            for(int i = 0; i < probabilities.length; i++)
            {
                probabilities[i] = ((double) nextStateTallies[i]) / ((double) totalTallies);
            }
            return probabilities;
        }

        public double getTransitionProbability(GeneralState nextState)
        {
            for(int i = 0; i < nextStates.length; i++)
            {
                if(nextStates[i].equals(nextState))
                {
                    return ((double) nextStateTallies[i]) / ((double) totalTallies);
                }
            }
            return 0;
        }

        public int memoryLength()
        {
            return priorStates.length;
        }

        public boolean priorEquals(GeneralState[] proposedPrior)
        {
            if(proposedPrior.length != priorStates.length)
            {
                return false;
            }
            for(int i = 0; i < priorStates.length; i++)
            {
                if(!proposedPrior[i].equals(priorStates[i]))
                {
                    return false;
                }
            }
            return true;
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(priorStates[0].toString());
            for (int m = 1; m < priorStates.length; m++)
            {
                b.append(',');
                b.append(priorStates[m].toString());
            }
            b.append(" --> ");
            b.append('\n');
            double[] probs = getTransitionProbabilities();
            for (int n = 0; n < nextStates.length; n++) {
                b.append(nextStates[n].toString());
                b.append(" : ");
                b.append(probs[n]);
                b.append('\n');
            }
            return b.toString();
        }
    }

}
