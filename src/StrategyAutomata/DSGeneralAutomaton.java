package StrategyAutomata;

import Attitudes.Attitude;
import Attitudes.AttitudeVector;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DSGeneralAutomaton
{

    private AttitudeVector[] dispAVs;
    private AttitudeVector[] saidAVs;
    private int[] nearestDispAVs;
    private int[] nearestSaidAVs;

    private MemoryTree tree;
    private int sequenceLength;
    private MemoryTree[] smallerTrees;

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
        nearestDispAVs = new int[dispAVs.length];
        nearestSaidAVs = new int[saidAVs.length];
        for(int d = 0; d < nearestDispAVs.length; d++)
        {
            double bestDist = Double.MAX_VALUE;
            for(int n = 0; n < dispAVs.length; n++)
            {
                if(n != d)
                {
                    double dist = dispAVs[d].distanceFrom(dispAVs[n]);
                    if(dist < bestDist)
                    {
                        bestDist = dist;
                        nearestDispAVs[d] = n;
                    }
                }
            }
        }
        for(int s = 0; s < nearestSaidAVs.length; s++)
        {
            double bestDist = Double.MAX_VALUE;
            for(int n = 0; n < saidAVs.length; n++)
            {
                if(n != s)
                {
                    double dist = saidAVs[s].distanceFrom(saidAVs[n]);
                    if(dist < bestDist)
                    {
                        bestDist = dist;
                        nearestSaidAVs[s] = n;
                    }
                }
            }
        }
        sequenceLength = memLength + 1; // Add one for "current"
        tree = new MemoryTree((sequenceLength * 4) - 2, displayedAttitudes.length, saidAttitudes.length);
        smallerTrees = new MemoryTree[memLength];
        for(int i = 0; i < smallerTrees.length; i++)
        {
            smallerTrees[i] = new MemoryTree(((memLength - i) * 4) - 2, displayedAttitudes.length, saidAttitudes.length);
        }
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
        int[] sequence = new int[sequenceLength * 4 - 2];
        for(int s = 0; s < sequenceLength - 1; s++)
        {
            sequence[s * 4] = observation[s][0];
            sequence[s * 4 + 1] = observation[s][1];
            sequence[s * 4 + 2] = observation[s][2];
            sequence[s * 4 + 3] = observation[s][3];
        }
        sequence[sequenceLength * 4 - 4] = observation[sequenceLength - 1][0];
        sequence[sequenceLength * 4 - 3] = observation[sequenceLength - 1][1];
        tree.addAVSequence(sequence);
        for(int s = 0; s < smallerTrees.length; s++)
        {
            int[] subsequence = new int[(4 * sequenceLength) - (4 * s) - 6];
            for(int i = 0; i < sequenceLength - s - 2; i++)
            {
                subsequence[i * 4] = observation[i + s + 1][0];
                subsequence[i * 4 + 1] = observation[i + s + 1][1];
                subsequence[i * 4 + 2] = observation[i + s + 1][2];
                subsequence[i * 4 + 3] = observation[i + s + 1][3];
            }
            subsequence[(sequenceLength - s - 2) * 4] = observation[sequenceLength - 1][0];
            subsequence[(sequenceLength - s - 2) * 4 + 1] = observation[sequenceLength - 1][1];
            smallerTrees[s].addAVSequence(subsequence);
        }
    }

    private void removeObservation(int[][] observation)
    {
        int[] sequence = new int[sequenceLength * 4 - 2];
        for(int s = 0; s < sequenceLength - 1; s++)
        {
            sequence[s * 4] = observation[s][0];
            sequence[s * 4 + 1] = observation[s][1];
            sequence[s * 4 + 2] = observation[s][2];
            sequence[s * 4 + 3] = observation[s][3];
        }
        sequence[sequenceLength * 4 - 4] = observation[sequenceLength - 1][0];
        sequence[sequenceLength * 4 - 3] = observation[sequenceLength - 1][1];
        tree.removeAVSequence(sequence);
        for(int s = 0; s < smallerTrees.length; s++)
        {
            int[] subsequence = new int[(4 * sequenceLength) - (4 * s) - 6];
            for(int i = 0; i < sequenceLength - s - 2; i++)
            {
                subsequence[i * 4] = observation[i + s + 1][0];
                subsequence[i * 4 + 1] = observation[i + s + 1][1];
                subsequence[i * 4 + 2] = observation[i + s + 1][2];
                subsequence[i * 4 + 3] = observation[i + s + 1][3];
            }
            subsequence[(sequenceLength - s - 2) * 4] = observation[sequenceLength - 1][0];
            subsequence[(sequenceLength - s - 2) * 4 + 1] = observation[sequenceLength - 1][1];
            smallerTrees[s].removeAVSequence(subsequence);
        }
    }

    public int[] getMostProbableActionAndMessage(int[][] clusteredHistory)
    {
        int[] mostProbableDispSaid = new int[2];
        mostProbableDispSaid[0] = -1;
        mostProbableDispSaid[1] = -1;
        if(clusteredHistory.length == sequenceLength - 1)
        {
            int[] sequence =new int[sequenceLength * 4 - 2];
            // Populate sequence history
            for(int i = 0; i < sequenceLength - 1; i++)
            {
                sequence[i * 4] = clusteredHistory[i][0];
                sequence[i * 4 + 1] = clusteredHistory[i][1];
                sequence[i * 4 + 2] = clusteredHistory[i][2];
                sequence[i * 4 + 3] = clusteredHistory[i][3];
            }
            int highestCount = 0;
            for(int d = 0; d < dispAVs.length; d++)
            {
                for(int s = 0; s < saidAVs.length; s++)
                {
                    sequence[sequence.length - 2] = d;
                    sequence[sequence.length - 1] = s;
                    int count = tree.getCount(sequence);
                    if(count > highestCount)
                    {
                        highestCount = count;
                        mostProbableDispSaid[0] = d;
                        mostProbableDispSaid[1] = s;
                    }
                }
            }
            // If no result, check similar histories
            if(highestCount == 0)
            {
                int[] nearValues = getSimilarHistoryPredictions(tree, sequence);
                highestCount = nearValues[0];
                mostProbableDispSaid[0] = nearValues[1];
                mostProbableDispSaid[1] = nearValues[2];
                int treeCount = 0;
                while(highestCount == 0 && treeCount < smallerTrees.length)
                {
                    // Check for most probable by altering the oldest segment of memory, then iterating through as needed until a nonzero result is found
                    int[] subsequence = new int[sequence.length - (4 * (treeCount + 1))];
                    for(int subseq = 0; subseq < subsequence.length - 2; subseq++)
                    {
                        subsequence[subseq] = sequence[subseq + (4 * (treeCount + 1))];
                    }
                    for(int d = 0; d < dispAVs.length; d++)
                    {
                        for(int s = 0; s < saidAVs.length; s++)
                        {
                            subsequence[subsequence.length - 2] = d;
                            subsequence[subsequence.length - 1] = s;
                            int count = smallerTrees[treeCount].getCount(subsequence);
                            if(count > highestCount)
                            {
                                highestCount = count;
                                mostProbableDispSaid[0] = d;
                                mostProbableDispSaid[1] = s;
                            }
                        }
                    }
                    if(highestCount == 0)
                    {
                        int[] nearSubVals = getSimilarHistoryPredictions(smallerTrees[treeCount], subsequence);
                        highestCount = nearSubVals[0];
                        mostProbableDispSaid[0] = nearSubVals[1];
                        mostProbableDispSaid[1] = nearSubVals[1];
                    }
                    treeCount++;
                }
            }
        }
        return mostProbableDispSaid;
    }

    public DSGeneralAutomaton stripAutomaton()
    {
        DSGeneralAutomaton extractedAutomaton = new DSGeneralAutomaton(sequenceLength - 1, dispAVs, saidAVs);
        boolean extractable = true;
        List<List<int[][]>> extractedObservationLists = new ArrayList<>();
        while (extractable)
        {
            int[] initSequence = calculateExtratingSequence(); // End in action, not message
            if(initSequence == null)
            {
                extractable = false;
            } else
            {
                List<int[][]> extractedObservations = extractFollowingObservations(initSequence);
                extractedObservationLists.add(extractedObservations);
                for(int[][] extractedObservation : extractedObservations)
                {
                    extractedAutomaton.addObservation(extractedObservation);
                }
            }
        }
        if(extractedObservationLists.isEmpty())
        {
            return null;
        }
        if(tree.highestCount() <= 0)
        {
            tree.root = extractedAutomaton.tree.root;
            return null;
        }
        return extractedAutomaton;
    }

    public DSGeneralAutomaton extractAutomaton() throws NullPointerException
    {
        DSGeneralAutomaton extractedAutomaton = new DSGeneralAutomaton(sequenceLength - 1, dispAVs, saidAVs);
        int[] initSequence = calculateExtratingSequence(); // End in action, not message
        if(initSequence == null)
        {
            throw new NullPointerException("Non-extractable automaton");
        }
        List<int[][]> extractedObservations = extractFollowingObservations(initSequence);
        for(int[][] extractedObservation : extractedObservations)
        {
            extractedAutomaton.addObservation(extractedObservation);
        }
        return extractedAutomaton;
    }

    private int[] calculateExtratingSequence()
    {
        int[] extractingSequence = new int[sequenceLength * 4 - 3];
        // Find a sequence by looking for histories with multiple possible ACTIONS
        PriorityQueue<MemoryTree.MemoryNode> pQ = new PriorityQueue<>();
        MemoryTree.MemoryNode root = tree.getRoot();
        for(int child = 0; child < dispAVs.length; child++)
        {
            MemoryTree.MemoryNode childNode = root.getChild(child);
;            if(childNode != null)
            {
                pQ.add(childNode);
            }
        }
        List<MemoryTree.MemoryNode> splittableNodes = new ArrayList<>();
        while(!pQ.isEmpty() && pQ.peek().getTotalCounts() > 2)
        {
            MemoryTree.MemoryNode node = pQ.poll();
            if(node.isLeafGrandparent())
            {
                if(node.nChildren > 1)
                {
                    splittableNodes.add(node);
                }
            } else
            {
                for(int c = 0; c < node.children.length; c++)
                {
                    MemoryTree.MemoryNode child = node.getChild(c);
                    if(child != null)
                    {
                        pQ.add(child);
                    }
                }
            }
        }
        if(!splittableNodes.isEmpty())
        {
            int overallSecondBest = -1;
            MemoryTree.MemoryNode winningNode = null;
            for(MemoryTree.MemoryNode node : splittableNodes)
            {
                int highest = 0;
                int secondHighest = -1;
                MemoryTree.MemoryNode highestNode = null;
                MemoryTree.MemoryNode secondHighestNode = null;
                for(int c = 0; c < node.children.length; c++)
                {
                    MemoryTree.MemoryNode child = node.children[c];
                    if(child != null)
                    {
                        int count = child.getTotalCounts();
                        if(count > highest)
                        {
                            secondHighest = highest;
                            secondHighestNode = highestNode;
                            highest = count;
                            highestNode = child;
                        } else if(count > secondHighest)
                        {
                            secondHighest = count;
                            secondHighestNode = child;
                        }
                    }
                }
                if(secondHighest > overallSecondBest)
                {
                    overallSecondBest = secondHighest;
                    winningNode = secondHighestNode;
                }
            }
            if(winningNode != null)
            {
//                System.out.println(overallSecondBest);
                return winningNode.getPath();
            }
        }
        return null;
//        return extractingSequence;
    }

    /**
     * Generates a list of observations to move from the current automaton to a new one, based on an initial sequence
     * @param initSequence History and next action
     * @return List of all observations to be added (each ending in next action and message)
     */
    private List<int[][]> extractFollowingObservations(int[] initSequence)
    {
        List<int[][]> extractedObservations = new ArrayList<>();
        int[] seekingSequence =  new int[sequenceLength * 4 - 2];
        for(int i = 0; i < initSequence.length; i++)
        {
            seekingSequence[i] = initSequence[i];
        }
        for(int s0 = 0; s0 < saidAVs.length; s0++)
        {
            seekingSequence[seekingSequence.length - 1] = s0;
            int seekCount = tree.getCount(seekingSequence);
            if(seekCount > 0)
            {
                for(int c = 0; c < seekCount; c++)
                {
                    int[][] movedSequence = new int[seekingSequence.length / 4 + 1][4];
                    for(int i = 0; i < movedSequence.length - 1; i++)
                    {
                        movedSequence[i][0] = seekingSequence[i * 4];
                        movedSequence[i][1] = seekingSequence[i * 4 + 1];
                        movedSequence[i][2] = seekingSequence[i * 4 + 2];
                        movedSequence[i][3] = seekingSequence[i * 4 + 3];
                    }
                    movedSequence[movedSequence.length - 1][0] = seekingSequence[seekingSequence.length - 2];
                    movedSequence[movedSequence.length - 1][1] = seekingSequence[seekingSequence.length - 1];
                    removeObservation(movedSequence);
                    extractedObservations.add(movedSequence);
                }
                // Look for "follow ups" to move
                if(seekingSequence.length > 4)
                {
                    int[] stubSequence = new int[seekingSequence.length - 4];
                    for(int i = 0; i < stubSequence.length; i++)
                    {
                        stubSequence[i] = seekingSequence[i + 4];
                    }
                    if(tree.getCount(stubSequence) > 0)
                    {
                        int[] seekingSeq1 = new int[stubSequence.length + 1];
                        for(int i = 0; i < stubSequence.length; i++)
                        {
                            seekingSeq1[i] = stubSequence[i];
                        }
                        for(int otherDisp = 0; otherDisp < dispAVs.length; otherDisp++)
                        {
                            seekingSeq1[seekingSeq1.length - 1] = otherDisp;
                            if(tree.getCount(seekingSeq1) > 0)
                            {
                                int[] seekingSeq2 = new int[seekingSeq1.length + 1];
                                for(int i = 0; i < seekingSeq1.length; i++)
                                {
                                    seekingSeq2[i] = seekingSeq1[i];
                                }
                                for(int otherSaid = 0; otherSaid < saidAVs.length; otherSaid++)
                                {
                                    seekingSeq2[seekingSeq2.length - 1] = otherSaid;
                                    if(tree.getCount(seekingSeq2) > 0)
                                    {
                                        int[] followingObservation = new int[seekingSeq2.length + 1];
                                        for(int i = 0; i < seekingSeq2.length; i++)
                                        {
                                            followingObservation[i] = seekingSeq2[i];
                                        }
                                        for(int displayed = 0; displayed < dispAVs.length; displayed++)
                                        {
                                            followingObservation[followingObservation.length - 1] = displayed;
                                            if(tree.getCount(followingObservation) > 0)
                                            {
                                                extractedObservations.addAll(extractFollowingObservations(followingObservation));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return extractedObservations;
    }

    public int getTotalCount()
    {
        return tree.totalCounts();
    }

    public int getHighestCount()
    {
        return tree.highestCount();
    }

    public int getNumDistinctSequences()
    {
        return tree.getNumLeaves();
    }

    public int calculateDecisionPoints(int minSecond)
    {
        // Find a sequence by looking for histories with multiple possible ACTIONS
        PriorityQueue<MemoryTree.MemoryNode> pQ = new PriorityQueue<>();
        MemoryTree.MemoryNode root = tree.getRoot();
        for(int child = 0; child < dispAVs.length; child++)
        {
            MemoryTree.MemoryNode childNode = root.getChild(child);
            if(childNode != null)
            {
                pQ.add(childNode);
            }
        }
        int nDecisionPoints = 0;
        while(!pQ.isEmpty() && pQ.peek().getTotalCounts() > minSecond)
        {
            MemoryTree.MemoryNode node = pQ.poll();
            if(node.isLeafGrandparent())
            {
                if(node.nChildren > 1)
                {
                    int bigEnoughNodes = 0;
                    for(int c = 0; c < node.children.length; c++)
                    {
                        if(node.getChild(c) != null)
                        {
                            int count = node.getChild(c).getTotalCounts();
                            if(count >= minSecond)
                            {
                                bigEnoughNodes++;
                            }
                        }
                    }
                    if(bigEnoughNodes > 1)
                    {
                        nDecisionPoints += bigEnoughNodes - 1;
                    }
                }
            } else
            {
                for(int c = 0; c < node.children.length; c++)
                {
                    MemoryTree.MemoryNode child = node.getChild(c);
                    if(child != null)
                    {
                        pQ.add(child);
                    }
                }
            }
        }
        return nDecisionPoints;
//        return extractingSequence;
    }

    private int[] getSimilarHistoryPredictions(MemoryTree mTree, int[] seq)
    {
        int[][] countMatrix = calculateNeighboringCountMatrix(0, mTree, seq);
        int[] result = new int[3];
        for(int d = 0; d < dispAVs.length; d++)
        {
            for(int s = 0; s < saidAVs.length; s++)
            {
                if(countMatrix[d][s] > result[0])
                {
                    result[0] = countMatrix[d][s];
                    result[1] = d;
                    result[2] = s;
                }
            }
        }
        return result;
//        return calculateBestSimilarPrediction(0, mTree, seq);
    }

    private int[] calculateBestSimilarPrediction(int iterator, MemoryTree mTree, int[] sequence)
    {
        if(iterator <= sequence.length - 2)
        {
            int[] result = new int[3];
            for(int d = 0; d < dispAVs.length; d++)
            {
                for(int s = 0; s < saidAVs.length; s++)
                {
                    sequence[sequence.length - 2] = d;
                    sequence[sequence.length - 1] = s;
                    int count = mTree.getCount(sequence);
                    if(count > result[0])
                    {
                        result[0] = count;
                        result[1] = d;
                        result[2] = s;
                    }
                }
            }
            return result;
        } else
        {
            int[] unchangedResult = calculateBestSimilarPrediction(iterator + 1, mTree, sequence);
            int origVal = sequence[iterator];
            if(iterator % 2 == 0)
            {
                sequence[iterator] = nearestDispAVs[sequence[iterator]];
            } else
            {
                sequence[iterator] = nearestSaidAVs[sequence[iterator]];
            }
            int[] changedResult = calculateBestSimilarPrediction(iterator + 1, mTree, sequence);
            sequence[iterator] = origVal;
            if(changedResult[0] > unchangedResult[0])
            {
                return changedResult;
            } else
            {
                return unchangedResult;
            }

        }
    }

    private int[][] calculateNeighboringCountMatrix(int iterator, MemoryTree mTree, int[] sequence)
    {
        if(iterator <= sequence.length -2)
        {
            int[][] result = new int[dispAVs.length][saidAVs.length];
            for(int d = 0; d < dispAVs.length; d++)
            {
                for(int s = 0; s < saidAVs.length; s++)
                {
                    sequence[sequence.length - 2] = d;
                    sequence[sequence.length - 1] = s;
                    result[d][s] = mTree.getCount(sequence);
                }
            }
            return result;
        } else
        {
            int[][] unchangedResult = calculateNeighboringCountMatrix(iterator + 1, mTree, sequence);
            int origVal = sequence[iterator];
            if(iterator % 2 == 0)
            {
                sequence[iterator] = nearestDispAVs[sequence[iterator]];
            } else
            {
                sequence[iterator] = nearestSaidAVs[sequence[iterator]];
            }
            int[][] changedResult = calculateNeighboringCountMatrix(iterator + 1, mTree, sequence);
            sequence[iterator] = origVal;
            for(int d = 0; d < dispAVs.length; d++)
            {
                for(int s = 0; s < saidAVs.length; s++)
                {
                    changedResult[d][s] += unchangedResult[d][s];
                }
            }
            return changedResult;
        }
    }

    public void addAutomaton(DSGeneralAutomaton otherAutomaton)
    {
        List<int[][]> otherObservations = otherAutomaton.getAllObservations();
        for(int[][] observation : otherObservations)
        {
            addObservation(observation);
        }
    }

    private List<int[][]> getAllObservations()
    {
        List<int[][]> allObservations = new ArrayList<>();
        List<int[]> allSequences = tree.getAllSequences();
        for(int[] unformattedSequence : allSequences)
        {
            int[][] observation = new int[unformattedSequence.length / 4 + 1][4];
            for(int o = 0; o < observation.length; o++)
            {
                observation[o][0] = unformattedSequence[o * 4];
                observation[o][1] = unformattedSequence[o * 4 + 1];
                if(o < observation.length - 1)
                {
                    observation[o][2] = unformattedSequence[o * 4 + 2];
                    observation[o][3] = unformattedSequence[o * 4 + 3];
                }
            }
        }
        return allObservations;
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
            root = new MemoryNode(null, nDispAVs, nSaidAVs);
        }

        public MemoryNode getRoot()
        {
            return root;
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
                System.out.println("Invalid Sequence");
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

        public List<int[]> getAllSequences()
        {
            List<MemoryNode> leaves = root.getAllLeaves();
            List<int[]> sequences = new ArrayList<>();
            for(MemoryNode leaf : leaves)
            {
                int lCount = leaf.count;
                for(int i = 0; i < lCount; i++)
                {
                    sequences.add(leaf.getPath());
                }
            }
            return sequences;
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

        public int highestCount()
        {
            return root.highestCount();
        }

        public int getNumLeaves()
        {
            return root.getNumLeaves();
        }

        private class MemoryNode implements Comparable
        {

            private int count;
            private MemoryNode[] children;
            private int nChildren;
            private int nChildrensChildren;
            private MemoryNode parent;

            public MemoryNode(MemoryNode parent, int nChildren, int nChildrensChildren)
            {
                this.parent = parent;
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

            public boolean isLeafParent()
            {
                if(hasChildren())
                {
                    for(int c = 0; c < children.length; c++)
                    {
                        if(children[c] != null)
                        {
                            return !children[c].hasChildren();
                        }
                    }
                }
                return false;
            }

            public boolean isLeafGrandparent()
            {
                if(hasChildren())
                {
                    for(int c = 0; c < children.length; c++)
                    {
                        if(children[c] != null)
                        {
                            return children[c].isLeafParent();
                        }
                    }
                }
                return false;
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
                        children[path[iterator]] = new MemoryNode(this, nChildrensChildren, children.length);
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
                            children[path[iterator]].parent = null;
                            children[path[iterator]] = null;
                            nChildren--;
                        }
                        return removed;
                    }
                }
            }

            public int[] getPath()
            {
                List<Integer> pathList = getPathList();
                int[] path = new int[pathList.size()];
                for(int p = 0; p < path.length; p++)
                {
                    path[p] = pathList.get(p);
                }
                return path;
            }

            private List<MemoryNode> getAllLeaves()
            {
                List<MemoryNode> allLeaves = new ArrayList<>();
                if(hasChildren())
                {
                    for(int c = 0; c < children.length; c++)
                    {
                        if(children[c] != null)
                        {
                            allLeaves.addAll(children[c].getAllLeaves());
                        }
                    }
                } else
                {
                    allLeaves.add(this);
                }
                return allLeaves;
            }

            private List<Integer> getPathList()
            {
                if(parent != null)
                {
                    List<Integer> parentList = parent.getPathList();
                    for(int c = 0; c < parent.children.length; c++)
                    {
                        if(parent.children[c] != null && parent.children[c].equals(this))
                        {
                            parentList.add(c);
                            return parentList;
                        }
                    }
                }
                return new ArrayList<>();
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
                    if(hasChildren())
                    {
                        int totCount = 0;
                        for(int c = 0; c < children.length; c++)
                        {
                            if(children[c] != null)
                            {
                                totCount += children[c].getCount(path, iterator);
                            }
                        }
                        return totCount;
                    }
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

            public int highestCount()
            {
                if(hasChildren())
                {
                    int highestCount = 0;
                    for(int c = 0; c < children.length; c++)
                    {
                        if(children[c] != null)
                        {
                            highestCount = Math.max(highestCount, children[c].highestCount());
                        }
                    }
                    return highestCount;
                } else
                {
                    return count;
                }
            }

            public int getNumLeaves()
            {
                if(hasChildren())
                {
                    int nLeaves = 0;
                    for(int c = 0; c < children.length; c++)
                    {
                        if(children[c] != null)
                        {
                            nLeaves += children[c].getNumLeaves();
                        }
                    }
                    return nLeaves;
                } else
                {
                    return 1;
                }
            }

            @Override
            public int compareTo(Object o)
            {
                if(o instanceof MemoryNode)
                {
                    MemoryNode n = (MemoryNode) o;
                    return n.getTotalCounts() - getTotalCounts();
                }
                throw new ClassCastException();
            }
        }

    }

}
