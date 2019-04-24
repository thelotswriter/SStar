package Game;

import java.util.ArrayList;

public class Player
{

    private PlayerType type;
    private ArrayList<Integer> actions;

    public Player(PlayerType playerType)
    {
        type = playerType;
        actions = new ArrayList<Integer>();
    }

    public Player(PlayerType playerType, ArrayList<Integer> actionHistory)
    {
        type = playerType;
        actions = new ArrayList<Integer>(actionHistory);
    }

    public PlayerType getPlayerType()
    {
        return type;
    }

    public ArrayList<Integer> getActionHistory()
    {
        return actions;
    }

    public int getAction(int time)
    {
        return actions.get(time);
    }

    public int getActionHistoryLength()
    {
        return actions.size();
    }

    public void setNextAction(int action)
    {
        actions.add(action);
    }

    public void clearHistory()
    {
        actions.clear();
    }

}
