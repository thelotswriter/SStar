package Game;

public class SpeechAct
{

    private boolean positiveCommand;
    private int[] act1;
    private int[] act2;

    public SpeechAct(boolean affirmative, int[] jointAct)
    {
        positiveCommand = affirmative;
        act1 = new int[2];
        act1[0] = jointAct[0];
        act1[1] = jointAct[1];
    }

    public SpeechAct(boolean affirmative, int[] jointAct1, int[] jointAct2)
    {
        positiveCommand = affirmative;
        act1 = new int[2];
        act1[0] = jointAct1[0];
        act1[1] = jointAct1[1];
        act2 = new int[2];
        act2[0] = jointAct2[0];
        act2[1] = jointAct2[1];
    }

    public boolean getAffirmative()
    {
        return positiveCommand;
    }

    public int size()
    {
        if(act2 == null)
        {
            return 1;
        } else
        {
            return 2;
        }
    }

    public int[] getJointAction()
    {
        return act1;
    }

    public int[] getSecondJointAction()
    {
        return act2;
    }

}
