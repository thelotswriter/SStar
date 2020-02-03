package Game;

import java.util.ArrayList;

public class Player {

    private PlayerType type;
    private ArrayList<Integer> actions;
    private ArrayList<SpeechAct[]> messages;

    public Player(PlayerType playerType) {
        type = playerType;
        actions = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public Player(PlayerType playerType, ArrayList<Integer> actionHistory) {
        type = playerType;
        actions = new ArrayList<Integer>(actionHistory);
        messages = new ArrayList<>();
    }

    public Player(PlayerType playerType, ArrayList<Integer> actionHistory, ArrayList<SpeechAct[]> messageHistory) {
        type = playerType;
        actions = new ArrayList<Integer>(actionHistory);
        messages = new ArrayList<>(messageHistory);
    }

    public PlayerType getPlayerType() {
        return type;
    }

    public ArrayList<Integer> getActionHistory() {
        return actions;
    }

    public ArrayList<SpeechAct[]> getMessageHistory() {
        return messages;
    }

    public int getAction(int time) {
        return actions.get(time);
    }

    public SpeechAct[] getMessage(int time) {
        return messages.get(time);
    }

    public int getActionHistoryLength() {
        return actions.size();
    }

    public boolean hasMessages()
    {
        return messages.size() == actions.size();
    }

    public void setNextAction(int action)
    {
        actions.add(action);
    }

    public void setNextMessage(SpeechAct[] message)
    {
        messages.add(message);
    }

    public void clearHistory()
    {
        actions.clear();
        messages.clear();
    }

}
