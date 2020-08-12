package StrategyAutomata;

import Attitudes.Attitude;
import Attitudes.AttitudeVector;

import java.util.List;

public class DSGeneralAutomaton
{

    private AttitudeVector[] dispAVs;
    private AttitudeVector[] saidAVs;

    private MemoryTree tree;
    private int sequenceLength;

    public DSGeneralAutomaton(int memLength, AttitudeVector[] displayedAttitudes, AttitudeVector[] saidAttitudes)
    {
        dispAVs = new AttitudeVector[displayedAttitudes.length];
        for(int d = 0; d < displayedAttitudes.length; d++)
        {
            dispAVs[d] = displayedAttitudes[d];
        }
        saidAVs = new AttitudeVector[saidAttitudes.length];
        for(int s = 0; s < saidAttitudes.length; s++)
        {
            saidAVs[s] = saidAttitudes[s];
        }
        sequenceLength = memLength + 1; // Add one for "current"
        tree = new MemoryTree(sequenceLength * 4, displayedAttitudes.length, saidAttitudes.length);
    }

    /**
     * Adds observations to the automaton
     * @param observations An integer list representing features. First param corresponds to the round, while the second corresponds with the attitude displayed, attitude said, attitude other displayed, and attitude other sad respectively
     */
    public void addObservations(int[][] observations)
    {
        for(int obs = 0; obs < observations.length - sequenceLength + 1; obs++)
        {
            int[][] observation = new int[sequenceLength][4];
            for(int s = 0; s < sequenceLength; s++)
            {
                observation[s][0] = observations[obs + s][0];
                observation[s][1] = observations[obs + s][1];
                observation[s][2] = observations[obs + s][2];
                observation[s][3] = observations[obs + s][3];
            }
            addObservation(observation);
        }
    }

    private void addObservation(int[][] observation)
    {
        int[] sequence = new int[sequenceLength * 4];
        for(int s = 0; s < sequenceLength; s++)
        {
            sequence[s * 4] = observation[s][0];
            sequence[s * 4 + 1] = observation[s][1];
            sequence[s * 4 + 2] = observation[s][2];
            sequence[s * 4 + 3] = observation[s][3];
        }
        tree.addAVSequence(sequence);
    }

    public int getTotalCount()
    {
        return tree.totalCounts();
    }

    private class MemoryTree
    {

        private int maxDepth;
        private int nDispAVs;
        private int nSaidAVs;

        private MemoryNode root;

        public MemoryTree(int sequenceLength, int nDAV, int nSAV)
        {
            maxDepth = sequenceLength + 1;
            nDispAVs = nDAV;
            nSaidAVs = nSAV;
            root = new MemoryNode(nDispAVs, nSaidAVs);
        }

        /**
         * Adds a sequence to the memory ree
         * @param avSequence The sequence to add
         * @return True if the sequence was valid and added, false if not
         */
        public boolean addAVSequence(int[] avSequence)
        {
            if(validSequence(avSequence))
            {
                return false;
            } else
            {
                root.addToPath(avSequence, 0);
                return true;
            }
        }

        public boolean removeAVSequence(int[] avSequence)
        {
            return root.removeFromPath(avSequence, 0);
        }

        /**
         * Gives the number of times the given sequnnce has been added
         * @param avSequence The sequence to check
         * @return The number of times the sequence was added
         */
        public int getCount(int[] avSequence)
        {
            return root.getCount(avSequence, 0);
        }

        /**
         * Gets the total number of observations added to the tree
         * @return The toital number of observations added
         */
        public int totalCounts()
        {
            return root.getTotalCounts();
        }

        /**
         * Checks the validity of a sequence for storage/retrieval
         * @param sequence The sequence to check
         * @return True if the sequence is valid, otherwise false
         */
        private boolean validSequence(int[] sequence)
        {
            if(sequence.length != maxDepth)
            {
                return false;
            } else
            {
                for(int i = 0; i < sequence.length; i++)
                {
                    if(sequence[i] < 0)
                    {
                        return false;
                    }
                    if(i % 2 == 0)
                    {
                        if(sequence[i] >= nDispAVs)
                        {
                            return false;
                        }
                    } else
                    {
                        if(sequence[i] >= nSaidAVs)
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        private class MemoryNode
        {

            private int count;
            private MemoryNode[] children;
            private int nChildren;
            private int nChildrensChildren;

            public MemoryNode(int nChildren, int nChildrensChildren)
            {
                count = 0;
                children = new MemoryNode[nChildren];
                this.nChildren = 0;
                this.nChildrensChildren = nChildrensChildren;
            }

            /**
             * States whether any children have been initialized
             * @return True if there are any initialized children, otherwise false
             */
            public boolean hasChildren()
            {
                return nChildren > 0;
            }

            /**
             * Gets the specified node
             * @param path The path to the node
             * @param iterator Iterator to track descent down the tree
             * @return The desired node, or null if it doesn't exist
             */
            public MemoryNode getNode(int[] path, int iterator)
            {
                if(iterator == path.length)
                {
                    return this;
                } else if(hasChildren() && children[path[iterator]] != null)
                {
                    return children[path[iterator]].getNode(path, iterator + 1);
                } else
                {
                    return null;
                }
            }

            /**
             * Adds the path, increasing its count
             * @param path The path to count
             * @param iterator The iterator, to track the descent down the path
             */
            public void addToPath(int[] path, int iterator)
            {
                if(iterator == path.length)
                {
                    count++;
                } else
                {
                    MemoryNode child = children[path[iterator]];
                    if(child == null)
                    {
                        children[path[iterator]] = new MemoryNode(nChildrensChildren, children.length);
                        children[path[iterator]].addToPath(path, iterator + 1);
                        nChildren++;
                    } else
                    {
                        child.addToPath(path, iterator + 1);
                    }
                }
            }

            /**
             * Removes the path, decreasing its count. If a leaf drops to zero, it is pruned
             * @param path The path to uncount
             * @param iterator The iterator, to track the descent down the path
             * @return True if the count was lowered on the leaf, otherwise false
             */
            public boolean removeFromPath(int[] path, int iterator)
            {
                if(iterator == path.length)
                {
                    count--;
                    return true;
                } else
                {
                    MemoryNode child = children[path[iterator]];
                    if(child == null)
                    {
                        return false;
                    } else
                    {
                        boolean removed = child.removeFromPath(path, iterator + 1);
                        if(removed && !child.hasChildren() && child.count == 0)
                        {
                            children[path[iterator]] = null;
                            nChildren--;
                        }
                        return removed;
                    }
                }
            }

            /**
             * Gives the number of times the path has been added
             * @param path The path to check the count of
             * @param iterator An iterator to work down the tree
             * @return The number of times the path was added to the tree
             */
            public int getCount(int[] path, int iterator)
            {
                if(iterator == path.length)
                {
                    return count;
                } else
                {
                    if(children[path[iterator]] != null)
                    {
                        return children[path[iterator]].getCount(path, iterator + 1);
                    } else
                    {
                        return 0;
                    }
                }
            }

            /**
             * Gets the count of the node and all its children
             * @return The total of the counts of all descendants, or itself if no children
             */
            public int getTotalCounts()
            {
                if(hasChildren())
                {
                    int total = 0;
                    for(MemoryNode child : children)
                    {
                        if(child != null)
                        {
                            total += child.getTotalCounts();
                        }
                    }
                    return total;
                } else
                {
                    return count;
                }
            }

            /**
             * Retrieves the specified child
             * @param index The index of the child
             * @return The desired child
             */
            public MemoryNode getChild(int index)
            {
                return children[index];
            }

        }

    }

}
