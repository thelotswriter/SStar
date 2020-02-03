package Game;

public class SpeechAct
{

    private boolean positiveCommand;
    private int[] act;

    public SpeechAct(boolean affirmative, int act)
    {
        positiveCommand = affirmative;
        this.act = new int[1];
        this.act[0] = act;
    }

    public SpeechAct(boolean affirmative, int act1, int act2)
    {
        positiveCommand = affirmative;
        act = new int[2];
        act[0] = act1;
        act[1] = act2;
    }

    public boolean getAffirmative()
    {
        return positiveCommand;
    }

    public int size()
    {
        return act.length;
    }

    public int getFirstAction()
    {
        return act[0];
    }

    public int getSecondAction()
    {
        return act[1];
    }

}
