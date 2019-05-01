package Automata;

import java.util.ArrayList;
import java.util.Random;

public class State
{

    private ArrayList<Transition>[] transitions;

    /**
     * A state with a number of actions which may cause a transition
     * @param numActions The number of actions possible to be made in the state
     */
    public State(int numActions)
    {
        transitions = new ArrayList[numActions];
    }

    /**
     * Adds a new transition to another state (or the same state) when an action is performed
     * @param action The action performed
     * @param newTransition The transition being added
     */
    public void addTransition(int action, Transition newTransition)
    {
        transitions[action].add(newTransition);
        normalizeTransitions(action);
    }

    public State transition(int action)
    {
        Random rand = new Random();
        double selection = rand.nextDouble();
        double tot = 0;
        for(int i = 0; i < transitions[action].size(); i++)
        {
            tot += transitions[action].get(i).getProbability();
            if(tot >= selection)
            {
                return transitions[action].get(i).getNextState();
            }
        }
        return transitions[action].get(transitions[action].size() - 1).getNextState();
    }

    public ArrayList<Transition> getTransitions(int action)
    {
        return transitions[action];
    }

    public void setTransitions(int action, ArrayList<Transition> transitions)
    {
        this.transitions[action] = transitions;
        normalizeTransitions(action);
    }

    /**
     * Makes all actions lead to one and only one transition, based on the most probable transition
     */
    public void makeDeterministic()
    {
        for(ArrayList<Transition> transitionArray : transitions)
        {
            int bestIndex = 0;
            double highestProbability = 0;
            for(int i = 0; i < transitionArray.size(); i++)
            {
                if(transitionArray.get(i).getProbability() > highestProbability)
                {
                    highestProbability = transitionArray.get(i).getProbability();
                    bestIndex = i;
                }
            }
            for(int i = 0; i < transitionArray.size(); i++)
            {
                if(i == bestIndex)
                {
                    transitionArray.get(i).setProbability(1);
                } else
                {
                    transitionArray.get(i).setProbability(0);
                }
            }
        }
    }

    private void normalizeTransitions(int action)
    {
        double totalProbability = 0;
        for(Transition t : transitions[action])
        {
            totalProbability += t.getProbability();
        }
        if(totalProbability != 0 && totalProbability != 1)
        {
            for(Transition t : transitions[action])
            {
                t.setProbability(t.getProbability() / totalProbability);
            }
        } else if(totalProbability == 0)
        {
            double totalTransitions = (double) transitions[action].size();
            if(totalTransitions > 0)
            {
                for(Transition t : transitions[action])
                {
                    t.setProbability(1.0 / totalTransitions);
                }
            }
        }
    }

}
