package Automata;

import Game.Game;

import java.util.ArrayList;

public class GameToAutomata {
    private static GameToAutomata ourInstance = new GameToAutomata();

    public static GameToAutomata getInstance() {
        return ourInstance;
    }

    private GameToAutomata() {
    }

    /**
     * Creates two automata (one for each player) based on the given game, with states remembering a set history
     * @param game The game to be translated into automata
     * @param history How far states "look back" at previous states. If history is zero, only the most recent action matters
     * @return An array of automata, the first for the row player and the second for the column player
     */
    public LearningAutomaton generateAutomaton(Game game, int history)
    {
//        double[][][] matrix = game.getPayoffMatrix().getMatrix();
//        int numPlayer1Actions = matrix.length;
//        int numPlayer2Actions = matrix[0].length;
        AutomataTree rowTree = new AutomataTree(history, 3, 2);
        int x = 1;
        x++;

//        State rowStartState = new State(0, numPlayer1Actions, false);
//        State colStartState = new State(0, numPlayer2Actions, false);

//        ArrayList<ArrayList> rowAutomatonTreeLayers = new ArrayList<>();
//        ArrayList<ArrayList> colAutomatonTreeLayers = new ArrayList<>();
//        ArrayList<State> rowFirstOptions = new ArrayList<>();
//        ArrayList<State> colFirstOptions = new ArrayList<>();
//        for(int i = 0; i < numPlayer1Actions; i++)
//        {
//            State s = new State(i, numPlayer1Actions * numPlayer2Actions, false);
//            Transition t = new Transition(s, 1);
//            rowStartState.addTransition(i, t);
//            rowFirstOptions.add(s);
//        }
//        rowStartState.normalize();
//        for(int i = 0; i < numPlayer2Actions; i++)
//        {
//            State s = new State(i, numPlayer1Actions * numPlayer2Actions, false);
//            Transition t = new Transition(s, 1);
//            colStartState.addTransition(i, t);
//            colFirstOptions.add(s);
//        }
//        colStartState.normalize();
//        rowAutomatonTreeLayers.add(rowFirstOptions);
//        colAutomatonTreeLayers.add(colFirstOptions);
//        for(int layer = 0; layer < history + 1; layer++)
//        {
////            int rowLayerStates = numPlayer1Actions * ((int) Math.pow(numPlayer1Actions * numPlayer2Actions, layer + 1));
////            int colLayerStates = numPlayer2Actions * ((int) Math.pow(numPlayer1Actions * numPlayer2Actions, layer + 1));
//
//        }
        // Learn the game
        return null;
    }

    private class AutomataTree
    {

        private Node root;
        private int act1;
        private int act2;
        private int actionDepth;

        private AutomataTree(int history, int actions1, int actions2)
        {
            root = new Node();
            act1 = actions1;
            act2 = actions2;
            actionDepth = 0;
            root.addState(new State(0, 0, false));
            generateTree(history, actions1, actions2);
        }

        private void generateTree(int history, int actions1, int actions2)
        {
            Node[] layer = new Node[actions1];
            root.addNodes(actions1);
            for(int i = 0; i < actions1; i++)
            {
                layer[i] = root.getNode(i);
                layer[i].addState(new State(i, actions2, false));
            }
            actionDepth++;
            for(int i = 0; i < history; i++)
            {
                layer = generateNextLayer(layer, actions1 * actions2);
                for(int j = 0; j < layer.length; j++)
                {
                    layer[j].addState(new State(j % actions1, actions2, false));
                }
                actionDepth += 2;
            }
            connect(root);
            connectLeaves();
        }

        private Node[] generateNextLayer(Node[] currentLayer, int numChildren)
        {
            Node[] newLayer = new Node[currentLayer.length * numChildren];
            for(int i = 0; i < currentLayer.length; i++)
            {
                currentLayer[i].addNodes(numChildren);
                for(int j = 0; j < numChildren; j++)
                {
                    int ind = i * numChildren + j;
                    newLayer[ind] = currentLayer[i].getNode(j);
                }
            }
            return newLayer;
        }

        private void connect(Node node)
        {
            int nChildren = node.getNumChildren();
            for(int i = 0; i < nChildren; i++)
            {
                Node child = node.getNode(i);
                connect(child);
                Transition t = new Transition(child.getState(), 1);
                node.getState().addTransition(i / act2, t);
            }
            if(nChildren > 0)
            {
                node.getState().normalize();
            }
        }

        private void connectLeaves()
        {
            int[] currentNodeIndex = new int[actionDepth];
            int[] maxVals = new int[actionDepth];
            for(int i = 0; i < actionDepth; i++)
            {
                if(i % 2 == 0)
                {
                    maxVals[i] = act1;
                } else
                {
                    maxVals[i] = act2;
                }
            }
            boolean covered = false;
            while(!covered)
            {
                State currentState = getState(currentNodeIndex);
                // Connect leaves
                int[] lookupKey = new int[actionDepth];
                for(int i = 2; i < actionDepth; i++)
                {
                    lookupKey[i - 2] = currentNodeIndex[i];
                }
                for(int i = 0; i < act2; i++)
                {
                    lookupKey[actionDepth - 2] = i;
                    for(int j = 0; j < act1; j++)
                    {
                        lookupKey[actionDepth - 1] = j;
                        Transition t = new Transition(getState(lookupKey), 1);
                        currentState.addTransition(i, t);
                    }
                }
                // Advance currentNodeIndex
                for(int i = actionDepth - 1; i >=0; i--)
                {
                    currentNodeIndex[i]++;
                    covered = false;
                    if(currentNodeIndex[i] >= maxVals[i])
                    {
                        currentNodeIndex[i] = 0;
                        covered = true;
                    }
                }
            }
        }

        private State getState(int[] position)
        {
            if(position.length % 2 != 1)
            {
                return null;
            }
            Node reachedNode = root.getNode(position[0]);
            for(int i = 1; i + 1 < position.length; i += 2)
            {
                reachedNode = reachedNode.getNode(position[i] * act2 + position[i + 1]);
            }
            return reachedNode.getState();
        }

        private class Node
        {
            private State state;
            private Node[] children;

            private Node()
            {
                children = null;
            }

            private void addState(State newState)
            {
                state = newState;
            }

            private void addNodes(int number)
            {
                children = new Node[number];
                for(int i = 0; i < number; i++)
                {
                    children[i] = new Node();
                }
            }

            private State getState()
            {
                return state;
            }

            private Node getNode(int index)
            {
                return children[index];
            }

            private int getNumChildren()
            {
                if(children == null)
                {
                    return 0;
                } else
                {
                    return children.length;
                }
            }

        }

    }

}
