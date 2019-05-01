package Automata;

public class Transition
{

    private State nextState;
    private double weight;

    public Transition(State nextState)
    {
        this.nextState = nextState;
        weight = 1;
    }

    public Transition(State nextState, double probability)
    {
        this.nextState = nextState;
        weight = Math.min(1, Math.max(probability, 0));
    }

    public void setProbability(double newProbability)
    {
        weight = Math.min(1, Math.max(newProbability, 0));
    }

    public State getNextState()
    {
        return nextState;
    }

    public double getProbability()
    {
        return weight;
    }

}
