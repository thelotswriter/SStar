package StrategyAutomata;

import Attitudes.ActionAttitudeConverter;
import Attitudes.Attitude;
import Attitudes.AttitudeVector;
import Clustering.DistantDataCluster;
import Clustering.FeatList2DSAttVecClustList;
import Clustering.FeatList2DispAttVecClusts;
import Clustering.FeatList2SaidAttVecClusts;
import Features.Feature;
import Features.GameToFeatureList;
import Game.Game;
import Game.SpeechAct;
import Game.TXTtoGame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class MegaAutomatonTest
{

    // Get files and separate into training and test
    // Create mega automaton from training data
    // Try to predict next attitude

    private final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";

    private File[] altFiles;
    private File[] chickenFiles;
    private File[] prisonFiles;

    private Game[] altGames;
    private Game[] chickenGames;
    private Game[] prisonGames;
    private Game[] altTrainGames;
    private Game[] altTestGames;
    private Game[] chickenTrainGames;
    private Game[] chickenTestGames;
    private Game[] prisonTrainGames;
    private Game[] prisonTestGames;

    private ActionAttitudeConverter altConverter;
    private ActionAttitudeConverter chickenConverter;
    private ActionAttitudeConverter prisonConverter;

    private List<List<Feature>> altTrainFeatureCollection;
    private List<List<Feature>> altTestFeatureCollection;
    private List<List<Feature>> chickenTrainFeatureCollection;
    private List<List<Feature>> chickenTestFeatureCollection;
    private List<List<Feature>> prisonTrainFeatureCollection;
    private List<List<Feature>> prisonTestFeatureCollection;
    private ArrayList<List<Feature>> altFeatures;
    private ArrayList<List<Feature>> chickenFeatures;
    private ArrayList<List<Feature>> prisonFeatures;

    private DistantDataCluster[] dispAVClusters;
    private DistantDataCluster[] saidAVClusters;

    private Map<Game, List<Feature>> gameFeatureMap;
    private Map<List<Feature>, int[][]> featureListClusterListMap;

    private int nNeighbors = 1;
    private double pPower = 1.0;
    private double discount = 0.0;
    private int memoryLength = 4;

    public MegaAutomatonTest(double percentTraining)
    {
        initialize(percentTraining);
    }

    private void initialize(double percentTraining)
    {
        System.out.println("What predictive power?");
        Scanner keys = new Scanner(System.in);
        pPower = keys.nextDouble();
        if(pPower > 1)
        {
            pPower = 1;
        } else if(pPower < 0)
        {
            pPower = 0;
        }
        System.out.println("Select Alternator Games");
        JFileChooser altFileChooser = new JFileChooser(dir);
        altFileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter altFilter = new FileNameExtensionFilter("TXT Files", "txt");
        AltFileFilter altNameFilter = new AltFileFilter();
        altFileChooser.setFileFilter(altFilter);
        altFileChooser.setFileFilter(altNameFilter);
        altFileChooser.showOpenDialog(null);
        altFiles = altFileChooser.getSelectedFiles();
        System.out.println("Select Chicken Games");
        JFileChooser chickenFileChooser = new JFileChooser(dir);
        chickenFileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter chickenFilter = new FileNameExtensionFilter("TXT Files", "txt");
        ChickenFileFilter chickenNameFilter = new ChickenFileFilter();
        chickenFileChooser.setFileFilter(chickenFilter);
        chickenFileChooser.setFileFilter(chickenNameFilter);
        chickenFileChooser.showOpenDialog(null);
        chickenFiles = chickenFileChooser.getSelectedFiles();
        System.out.println("Select Prisoner's Dilemma Games");
        JFileChooser prisonFileChooser = new JFileChooser(dir);
        prisonFileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter prisonFilter = new FileNameExtensionFilter("TXT Files", "txt");
        PrisonFileFilter prisonNameFilter = new PrisonFileFilter();
        prisonFileChooser.setFileFilter(prisonFilter);
        prisonFileChooser.setFileFilter(prisonNameFilter);
        prisonFileChooser.showOpenDialog(null);
        prisonFiles = prisonFileChooser.getSelectedFiles();
        TXTtoGame txtToGame = new TXTtoGame();
        altGames = new Game[altFiles.length / 2];
        chickenGames = new Game[chickenFiles.length / 2];
        prisonGames = new Game[prisonFiles.length / 2];
        for(int g = 0; g < altGames.length; g++)
        {
            altGames[g] = txtToGame.openFile(altFiles[2 * g].getPath(), altFiles[2 * g + 1].getPath());
        }
        for(int g = 0; g < chickenGames.length; g++)
        {
            chickenGames[g] = txtToGame.openFile(chickenFiles[g * 2].getPath(), chickenFiles[(g * 2) + 1].getPath());
        }
        for(int g = 0; g < prisonGames.length; g++)
        {
            prisonGames[g] = txtToGame.openFile(prisonFiles[g * 2].getPath(), prisonFiles[(g * 2) + 1].getPath());
        }
        altConverter = new ActionAttitudeConverter(altGames[0].getPayoffMatrix(), pPower);
        chickenConverter = new ActionAttitudeConverter(chickenGames[0].getPayoffMatrix(), pPower);
        prisonConverter = new ActionAttitudeConverter(prisonGames[0].getPayoffMatrix(), pPower);
        int[] altTestIndices = selectTestIndices(altGames.length, 1 - percentTraining);
        int[] chickenTestIndices = selectTestIndices(chickenGames.length, 1 - percentTraining);
        int[] prisonTestIndices = selectTestIndices(prisonGames.length, 1 - percentTraining);
        altTrainGames = new Game[altGames.length - altTestIndices.length];
        altTestGames = new Game[altTestIndices.length];
        chickenTrainGames = new Game[chickenGames.length - chickenTestIndices.length];
        chickenTestGames = new Game[chickenTestIndices.length];
        prisonTrainGames = new Game[prisonGames.length - prisonTestIndices.length];
        prisonTestGames = new Game[prisonTestIndices.length];
        int altTrainIndex = 0;
        int altTestIndex = 0;
        for(int i = 0; i < altGames.length; i++)
        {
            if(altTestIndex < altTestGames.length && i == altTestIndices[altTestIndex])
            {
                altTestGames[altTestIndex] = altGames[i];
                altTestIndex++;
            } else
            {
                altTrainGames[altTrainIndex] = altGames[i];
                altTrainIndex++;
            }
        }
        int chickenTrainIndex = 0;
        int chickenTestIndex = 0;
        for(int i = 0; i < chickenGames.length; i++)
        {
            if(chickenTestIndex < chickenTestGames.length && i == chickenTestIndices[chickenTestIndex])
            {
                chickenTestGames[chickenTestIndex] = chickenGames[i];
                chickenTestIndex++;
            } else
            {
                chickenTrainGames[chickenTrainIndex] = chickenGames[i];
                chickenTrainIndex++;
            }
        }
        int prisonTrainIndex = 0;
        int prisonTestIndex = 0;
        for(int i = 0; i < prisonGames.length; i++)
        {
            if(prisonTestIndex < prisonTestGames.length && i == prisonTestIndices[prisonTestIndex])
            {
                prisonTestGames[prisonTestIndex] = prisonGames[i];
                prisonTestIndex++;
            } else
            {
                prisonTrainGames[prisonTrainIndex] = prisonGames[i];
                prisonTrainIndex++;
            }
        }
        //==========CONVERT GAMES TO FEATURE LISTS
        gameFeatureMap = new HashMap<Game, List<Feature>>();

        List<List<Feature>> featureCollection = new ArrayList<>();
        altFeatures = new ArrayList<>();
        altTrainFeatureCollection = new ArrayList<>();
        for(int g = 0; g < altTrainGames.length; g++)
        {
            Game game = altTrainGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            altTrainFeatureCollection.add(fCollection);
            altFeatures.add(fCollection);
            featureCollection.add(fCollection);
        }
        altTestFeatureCollection = new ArrayList<>();
        for(int g = 0; g < altTestGames.length; g++)
        {
            Game game = altTestGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            altTestFeatureCollection.add(fCollection);
            altFeatures.add(fCollection);
        }
        chickenFeatures = new ArrayList<>();
        chickenTrainFeatureCollection = new ArrayList<>();
        for(int g = 0; g < chickenTrainGames.length; g++)
        {
            Game game = chickenTrainGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            chickenTrainFeatureCollection.add(fCollection);
            chickenFeatures.add(fCollection);
            featureCollection.add(fCollection);
            featureListClusterListMap = new HashMap<List<Feature>, int[][]>();
        }
        chickenTestFeatureCollection = new ArrayList<>();
        for(int g = 0; g < chickenTestGames.length; g++)
        {
            Game game = chickenTestGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            chickenTestFeatureCollection.add(fCollection);
            chickenFeatures.add(fCollection);
        }
        prisonFeatures = new ArrayList<>();
        prisonTrainFeatureCollection = new ArrayList<>();
        for(int g = 0; g < prisonTrainGames.length; g++)
        {
            Game game = prisonTrainGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            prisonTrainFeatureCollection.add(fCollection);
            prisonFeatures.add(fCollection);
            featureCollection.add(fCollection);
        }
        prisonTestFeatureCollection = new ArrayList<>();
        for(int g = 0; g < prisonTestGames.length; g++)
        {
            Game game = prisonTestGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            prisonTestFeatureCollection.add(fCollection);
            prisonFeatures.add(fCollection);
        }
        //===========CLUSTER FEATURE LISTS
        int maxDAVClusters = 8;
        int minDAVClusters = 3;
        int maxSAVClusters = 8;
        int minSAVClusters = 3;
        FeatList2DispAttVecClusts featList2DispAttVecClusts = FeatList2DispAttVecClusts.getInstance();
        FeatList2SaidAttVecClusts featList2SaidAttVecClusts = FeatList2SaidAttVecClusts.getInstance();
        dispAVClusters = featList2DispAttVecClusts.getDisplayedAttitudeVectorCluster(minDAVClusters, maxDAVClusters, featureCollection);
        saidAVClusters = featList2SaidAttVecClusts.getSaidAttitudeVectorCluster(minSAVClusters, maxSAVClusters, featureCollection);
    }

    /**
     * Generates an ordered array of randomly selected indices
     * @param nFiles The number of total files
     * @param percentTest The percent of the total files needed to be randomly chosen
     * @return An ordered array of indices to be used as test indices
     */
    private int[] selectTestIndices(int nFiles, double percentTest)
    {
        Random rand = new Random();
        int added = 0;
        int[] testIndices = new int[(int) (((double) nFiles) * percentTest)];
        for(int i = 0; i < testIndices.length; i++)
        {
            testIndices[i] = -1;
        }
        while (added < testIndices.length)
        {
            int toAdd = rand.nextInt(nFiles);
            boolean add = true;
            for(int i = 0; i < added; i++)
            {
                if(toAdd < testIndices[i])
                {
                    int oldIndex = testIndices[i];
                    testIndices[i] = toAdd;
                    toAdd = oldIndex;
                } else if(toAdd == testIndices[i])
                {
                    add = false;
                    break;
                }
            }
            if(add)
            {
                testIndices[added] = toAdd;
                added++;
            }
        }
        return testIndices;
    }

    /**
     * Generates a matrix of prediction accuracies in the following order:
     * Alternator, Chicken, Prisoner's Dilemma,
     * Alternator + Chicken, Alternator + Prisoner's Dilemma, Chicken + Prisoner's Dilemma,
     * All Games
     * @return The described matrix. First index is the predictor, second is what game it's applied to
     */
    public void run()
    {
        //=========Convert feature lists to cluster lists
        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
        List<int[][]> altTrainClustLists = new ArrayList<>();
        List<int[][]> altTestClustLists = new ArrayList<>();
        List<int[][]> chickenTrainClustLists = new ArrayList<>();
        List<int[][]> chickenTestClustLists = new ArrayList<>();
        List<int[][]> prisonTrainClustLists = new ArrayList<>();
        List<int[][]> prisonTestClustLists = new ArrayList<>();

        List<int[][]> altChickenTrainClustLists = new ArrayList<>();
        List<int[][]> altChickenTestClustLists = new ArrayList<>();
        List<int[][]> altPrisonTrainClustLists = new ArrayList<>();
        List<int[][]> altPrisonTestClustLists = new ArrayList<>();
        List<int[][]> chickenPrisonTrainClustLists = new ArrayList<>();
        List<int[][]> chickenPrisonTestClustLists = new ArrayList<>();
        List<int[][]> combinedTrainClustLists = new ArrayList<>();
        List<int[][]> combinedTestClustLists = new ArrayList<>();
        // Automata
        AttitudeVector[] dispAVs = new AttitudeVector[dispAVClusters.length];
        AttitudeVector[] saidAVs = new AttitudeVector[saidAVClusters.length];
        for(int c = 0; c < dispAVs.length; c++)
        {
            dispAVs[c] = (AttitudeVector) dispAVClusters[c].getCentroid();
        }
        for(int c = 0; c < saidAVs.length; c++)
        {
            saidAVs[c] = (AttitudeVector) saidAVClusters[c].getCentroid();
        }
        DSGeneralAutomaton altAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        DSGeneralAutomaton chickenAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        DSGeneralAutomaton prisonAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        DSGeneralAutomaton altChickenAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        DSGeneralAutomaton altPrisonAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        DSGeneralAutomaton chickenPrisonAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        DSGeneralAutomaton combinedAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        int minSecond = 2;
        System.out.print("Alt Decision Points: ");
        System.out.println(altAutomaton.calculateDecisionPoints(minSecond));
        System.out.print("Chicken Decision Points: ");
        System.out.println(chickenAutomaton.calculateDecisionPoints(minSecond));
        System.out.print("Prison Decision Points: ");
        System.out.println(prisonAutomaton.calculateDecisionPoints(minSecond));
        System.out.print("Alt+Chick Decision Points: ");
        System.out.println(altChickenAutomaton.calculateDecisionPoints(minSecond));
        System.out.print("Alt+PD Decision Points: ");
        System.out.println(altPrisonAutomaton.calculateDecisionPoints(minSecond));
        System.out.print("Chick+PD Decision Points: ");
        System.out.println(chickenPrisonAutomaton.calculateDecisionPoints(minSecond));
        System.out.print("Combined Decision Points: ");
        System.out.println(combinedAutomaton.calculateDecisionPoints(minSecond));
        for(List<Feature> featureList : altTrainFeatureCollection)
        {
//            List<Feature> featureList = new ArrayList<>();
//            featureList.addAll(featureCollection);
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            altTrainClustLists.add(clustIndexList);
            altChickenTrainClustLists.add(clustIndexList);
            altPrisonTrainClustLists.add(clustIndexList);
            combinedTrainClustLists.add(clustIndexList);
            altAutomaton.addObservations(clustIndexList);
            altChickenAutomaton.addObservations(clustIndexList);
            altPrisonAutomaton.addObservations(clustIndexList);
            combinedAutomaton.addObservations(clustIndexList);

        }
        for(List<Feature> featureList : altTestFeatureCollection)
        {
//            List<Feature> featureList = new ArrayList<>();
//            featureList.addAll(featureCollection);
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            altTestClustLists.add(clustIndexList);
            altChickenTestClustLists.add(clustIndexList);
            altPrisonTestClustLists.add(clustIndexList);
            combinedTestClustLists.add(clustIndexList);
        }
        for(List<Feature> featureList : chickenTrainFeatureCollection)
        {
//            List<Feature> featureList = new ArrayList<>();
//            featureList.addAll(featureCollection);
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            chickenTrainClustLists.add(clustIndexList);
            altChickenTrainClustLists.add(clustIndexList);
            chickenPrisonTrainClustLists.add(clustIndexList);
            combinedTrainClustLists.add(clustIndexList);
            chickenAutomaton.addObservations(clustIndexList);
            altChickenAutomaton.addObservations(clustIndexList);
            chickenPrisonAutomaton.addObservations(clustIndexList);
            combinedAutomaton.addObservations(clustIndexList);
        }
        for(List<Feature> featureList : chickenTestFeatureCollection)
        {
//            List<Feature> featureList = new ArrayList<>();
//            featureList.addAll(featureCollection);
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            chickenTestClustLists.add(clustIndexList);
            altChickenTestClustLists.add(clustIndexList);
            chickenPrisonTestClustLists.add(clustIndexList);
            combinedTestClustLists.add(clustIndexList);
        }
        for(List<Feature> featureList : prisonTrainFeatureCollection)
        {
//            List<Feature> featureList = new ArrayList<>();
//            featureList.addAll(featureCollection);
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            prisonTrainClustLists.add(clustIndexList);
            altPrisonTrainClustLists.add(clustIndexList);
            chickenPrisonTrainClustLists.add(clustIndexList);
            combinedTrainClustLists.add(clustIndexList);
            prisonAutomaton.addObservations(clustIndexList);
            altPrisonAutomaton.addObservations(clustIndexList);
            chickenPrisonAutomaton.addObservations(clustIndexList);
            combinedAutomaton.addObservations(clustIndexList);
        }
        for(List<Feature> featureList : prisonTestFeatureCollection)
        {
//            List<Feature> featureList = new ArrayList<>();
//            featureList.addAll(featureCollection);
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            prisonTestClustLists.add(clustIndexList);
            altPrisonTestClustLists.add(clustIndexList);
            chickenPrisonTestClustLists.add(clustIndexList);
            combinedTestClustLists.add(clustIndexList);
        }
//        DSGeneralAutomaton extractedAlt = altAutomaton.extractAutomaton();
//        System.out.print("Remaining alt observations: ");
//        System.out.println(altAutomaton.getTotalCount());
//        System.out.print("Extracted alt observations: ");
//        System.out.println(extractedAlt.getTotalCount());
        // Test predictions
        List<List<Feature>> altChickenTestFeatureCollection = new ArrayList<>();
        List<List<Feature>> altPrisonTestFeatureCollection = new ArrayList<>();
        List<List<Feature>> chickenPrisonTestFeatureCollection = new ArrayList<>();
        List<List<Feature>> combinedTestFeatureCollection = new ArrayList<>();
        List<List<Feature>> altChickenTrainFeatureCollection = new ArrayList<>();
        List<List<Feature>> altPrisonTrainFeatureCollection = new ArrayList<>();
        List<List<Feature>> chickenPrisonTrainFeatureCollection = new ArrayList<>();
        List<List<Feature>> combinedTrainFeatureCollection = new ArrayList<>();
        altChickenTestFeatureCollection.addAll(altTestFeatureCollection);
        altChickenTestFeatureCollection.addAll(chickenTestFeatureCollection);
        altPrisonTestFeatureCollection.addAll(altTestFeatureCollection);
        altPrisonTestFeatureCollection.addAll(prisonTestFeatureCollection);
        chickenPrisonTestFeatureCollection.addAll(chickenTestFeatureCollection);
        chickenPrisonTestFeatureCollection.addAll(prisonTestFeatureCollection);
        combinedTestFeatureCollection.addAll(altTestFeatureCollection);
        combinedTestFeatureCollection.addAll(chickenTestFeatureCollection);
        combinedTestFeatureCollection.addAll(prisonTestFeatureCollection);
        altChickenTrainFeatureCollection.addAll(altTrainFeatureCollection);
        altChickenTrainFeatureCollection.addAll(chickenTrainFeatureCollection);
        altPrisonTrainFeatureCollection.addAll(altTrainFeatureCollection);
        altPrisonTrainFeatureCollection.addAll(prisonTrainFeatureCollection);
        chickenPrisonTrainFeatureCollection.addAll(chickenTrainFeatureCollection);
        chickenPrisonTrainFeatureCollection.addAll(prisonTrainFeatureCollection);
        combinedTrainFeatureCollection.addAll(altTrainFeatureCollection);
        combinedTrainFeatureCollection.addAll(chickenTrainFeatureCollection);
        combinedTestFeatureCollection.addAll(prisonTrainFeatureCollection);
        double[][] resultMatrix = new double[7][14];
        resultMatrix[0][0] = calculatePrecitionAccuracy(altAutomaton, altTestClustLists);
        resultMatrix[0][1] = calculatePrecitionAccuracy(altAutomaton, chickenTestClustLists);
        resultMatrix[0][2] = calculatePrecitionAccuracy(altAutomaton, prisonTestClustLists);
        resultMatrix[0][3] = calculatePrecitionAccuracy(altAutomaton, altChickenTestClustLists);
        resultMatrix[0][4] = calculatePrecitionAccuracy(altAutomaton, altPrisonTestClustLists);
        resultMatrix[0][5] = calculatePrecitionAccuracy(altAutomaton, chickenPrisonTestClustLists);
        resultMatrix[0][6] = calculatePrecitionAccuracy(altAutomaton, combinedTestClustLists);
        resultMatrix[0][7] = calculatePrecitionAccuracy(altAutomaton, altTrainClustLists);
        resultMatrix[0][8] = calculatePrecitionAccuracy(altAutomaton, chickenTrainClustLists);
        resultMatrix[0][9] = calculatePrecitionAccuracy(altAutomaton, prisonTrainClustLists);
        resultMatrix[0][10] = calculatePrecitionAccuracy(altAutomaton, altChickenTrainClustLists);
        resultMatrix[0][11] = calculatePrecitionAccuracy(altAutomaton, altPrisonTrainClustLists);
        resultMatrix[0][12] = calculatePrecitionAccuracy(altAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[0][13] = calculatePrecitionAccuracy(altAutomaton, combinedTrainClustLists);
        resultMatrix[1][0] = calculatePrecitionAccuracy(chickenAutomaton, altTestClustLists);
        resultMatrix[1][1] = calculatePrecitionAccuracy(chickenAutomaton, chickenTestClustLists);
        resultMatrix[1][2] = calculatePrecitionAccuracy(chickenAutomaton, prisonTestClustLists);
        resultMatrix[1][3] = calculatePrecitionAccuracy(chickenAutomaton, altChickenTestClustLists);
        resultMatrix[1][4] = calculatePrecitionAccuracy(chickenAutomaton, altPrisonTestClustLists);
        resultMatrix[1][5] = calculatePrecitionAccuracy(chickenAutomaton, chickenPrisonTestClustLists);
        resultMatrix[1][6] = calculatePrecitionAccuracy(chickenAutomaton, combinedTestClustLists);
        resultMatrix[1][7] = calculatePrecitionAccuracy(chickenAutomaton, altTrainClustLists);
        resultMatrix[1][8] = calculatePrecitionAccuracy(chickenAutomaton, chickenTrainClustLists);
        resultMatrix[1][9] = calculatePrecitionAccuracy(chickenAutomaton, prisonTrainClustLists);
        resultMatrix[1][10] = calculatePrecitionAccuracy(chickenAutomaton, altChickenTrainClustLists);
        resultMatrix[1][11] = calculatePrecitionAccuracy(chickenAutomaton, altPrisonTrainClustLists);
        resultMatrix[1][12] = calculatePrecitionAccuracy(chickenAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[1][13] = calculatePrecitionAccuracy(chickenAutomaton, combinedTrainClustLists);
        resultMatrix[2][0] = calculatePrecitionAccuracy(prisonAutomaton, altTestClustLists);
        resultMatrix[2][1] = calculatePrecitionAccuracy(prisonAutomaton, chickenTestClustLists);
        resultMatrix[2][2] = calculatePrecitionAccuracy(prisonAutomaton, prisonTestClustLists);
        resultMatrix[2][3] = calculatePrecitionAccuracy(prisonAutomaton, altChickenTestClustLists);
        resultMatrix[2][4] = calculatePrecitionAccuracy(prisonAutomaton, altPrisonTestClustLists);
        resultMatrix[2][5] = calculatePrecitionAccuracy(prisonAutomaton, chickenPrisonTestClustLists);
        resultMatrix[2][6] = calculatePrecitionAccuracy(prisonAutomaton, combinedTestClustLists);
        resultMatrix[2][7] = calculatePrecitionAccuracy(prisonAutomaton, altTrainClustLists);
        resultMatrix[2][8] = calculatePrecitionAccuracy(prisonAutomaton, chickenTrainClustLists);
        resultMatrix[2][9] = calculatePrecitionAccuracy(prisonAutomaton, prisonTrainClustLists);
        resultMatrix[2][10] = calculatePrecitionAccuracy(prisonAutomaton, altChickenTrainClustLists);
        resultMatrix[2][11] = calculatePrecitionAccuracy(prisonAutomaton, altPrisonTrainClustLists);
        resultMatrix[2][12] = calculatePrecitionAccuracy(prisonAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[2][13] = calculatePrecitionAccuracy(prisonAutomaton, combinedTrainClustLists);
        resultMatrix[3][0] = calculatePrecitionAccuracy(altChickenAutomaton, altTestClustLists);
        resultMatrix[3][1] = calculatePrecitionAccuracy(altChickenAutomaton, chickenTestClustLists);
        resultMatrix[3][2] = calculatePrecitionAccuracy(altChickenAutomaton, prisonTestClustLists);
        resultMatrix[3][3] = calculatePrecitionAccuracy(altChickenAutomaton, altChickenTestClustLists);
        resultMatrix[3][4] = calculatePrecitionAccuracy(altChickenAutomaton, altPrisonTestClustLists);
        resultMatrix[3][5] = calculatePrecitionAccuracy(altChickenAutomaton, chickenPrisonTestClustLists);
        resultMatrix[3][6] = calculatePrecitionAccuracy(altChickenAutomaton, combinedTestClustLists);
        resultMatrix[3][7] = calculatePrecitionAccuracy(altChickenAutomaton, altTrainClustLists);
        resultMatrix[3][8] = calculatePrecitionAccuracy(altChickenAutomaton, chickenTrainClustLists);
        resultMatrix[3][9] = calculatePrecitionAccuracy(altChickenAutomaton, prisonTrainClustLists);
        resultMatrix[3][10] = calculatePrecitionAccuracy(altChickenAutomaton, altChickenTrainClustLists);
        resultMatrix[3][11] = calculatePrecitionAccuracy(altChickenAutomaton, altPrisonTrainClustLists);
        resultMatrix[3][12] = calculatePrecitionAccuracy(altChickenAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[3][13] = calculatePrecitionAccuracy(altChickenAutomaton, combinedTrainClustLists);
        resultMatrix[4][0] = calculatePrecitionAccuracy(altPrisonAutomaton, altTestClustLists);
        resultMatrix[4][1] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenTestClustLists);
        resultMatrix[4][2] = calculatePrecitionAccuracy(altPrisonAutomaton, prisonTestClustLists);
        resultMatrix[4][3] = calculatePrecitionAccuracy(altPrisonAutomaton, altChickenTestClustLists);
        resultMatrix[4][4] = calculatePrecitionAccuracy(altPrisonAutomaton, altPrisonTestClustLists);
        resultMatrix[4][5] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenPrisonTestClustLists);
        resultMatrix[4][6] = calculatePrecitionAccuracy(altPrisonAutomaton, combinedTestClustLists);
        resultMatrix[4][7] = calculatePrecitionAccuracy(altPrisonAutomaton, altTrainClustLists);
        resultMatrix[4][8] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenTrainClustLists);
        resultMatrix[4][9] = calculatePrecitionAccuracy(altPrisonAutomaton, prisonTrainClustLists);
        resultMatrix[4][10] = calculatePrecitionAccuracy(altPrisonAutomaton, altChickenTrainClustLists);
        resultMatrix[4][11] = calculatePrecitionAccuracy(altPrisonAutomaton, altPrisonTrainClustLists);
        resultMatrix[4][12] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[4][13] = calculatePrecitionAccuracy(altPrisonAutomaton, combinedTrainClustLists);
        resultMatrix[5][0] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altTestClustLists);
        resultMatrix[5][1] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenTestClustLists);
        resultMatrix[5][2] = calculatePrecitionAccuracy(chickenPrisonAutomaton, prisonTestClustLists);
        resultMatrix[5][3] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altChickenTestClustLists);
        resultMatrix[5][4] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altPrisonTestClustLists);
        resultMatrix[5][5] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenPrisonTestClustLists);
        resultMatrix[5][6] = calculatePrecitionAccuracy(chickenPrisonAutomaton, combinedTestClustLists);
        resultMatrix[5][7] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altTrainClustLists);
        resultMatrix[5][8] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenTrainClustLists);
        resultMatrix[5][9] = calculatePrecitionAccuracy(chickenPrisonAutomaton, prisonTrainClustLists);
        resultMatrix[5][10] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altChickenTrainClustLists);
        resultMatrix[5][11] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altPrisonTrainClustLists);
        resultMatrix[5][12] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[5][13] = calculatePrecitionAccuracy(chickenPrisonAutomaton, combinedTrainClustLists);
        resultMatrix[6][0] = calculatePrecitionAccuracy(combinedAutomaton, altTestClustLists);
        resultMatrix[6][1] = calculatePrecitionAccuracy(combinedAutomaton, chickenTestClustLists);
        resultMatrix[6][2] = calculatePrecitionAccuracy(combinedAutomaton, prisonTestClustLists);
        resultMatrix[6][3] = calculatePrecitionAccuracy(combinedAutomaton, altChickenTestClustLists);
        resultMatrix[6][4] = calculatePrecitionAccuracy(combinedAutomaton, altPrisonTestClustLists);
        resultMatrix[6][5] = calculatePrecitionAccuracy(combinedAutomaton, chickenPrisonTestClustLists);
        resultMatrix[6][6] = calculatePrecitionAccuracy(combinedAutomaton, combinedTestClustLists);
        resultMatrix[6][7] = calculatePrecitionAccuracy(combinedAutomaton, altTrainClustLists);
        resultMatrix[6][8] = calculatePrecitionAccuracy(combinedAutomaton, chickenTrainClustLists);
        resultMatrix[6][9] = calculatePrecitionAccuracy(combinedAutomaton, prisonTrainClustLists);
        resultMatrix[6][10] = calculatePrecitionAccuracy(combinedAutomaton, altChickenTrainClustLists);
        resultMatrix[6][11] = calculatePrecitionAccuracy(combinedAutomaton, altPrisonTrainClustLists);
        resultMatrix[6][12] = calculatePrecitionAccuracy(combinedAutomaton, chickenPrisonTrainClustLists);
        resultMatrix[6][13] = calculatePrecitionAccuracy(combinedAutomaton, combinedTrainClustLists);
        double bestResult = 0;
        for(int r = 0; r < resultMatrix.length; r++)
        {
            for(int c = 0; c < resultMatrix[r].length; c++)
            {
                if(resultMatrix[r][c] > bestResult)
                {
                    bestResult = resultMatrix[r][c];
                }
                System.out.print(resultMatrix[r][c]);
                System.out.print('\t');
            }
            System.out.println();
        }
        System.out.println(bestResult);
        print(resultMatrix);

        int[] altAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] altAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] altAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] altAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] altAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] chickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] chickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] chickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] prisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] prisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] prisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] prisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] prisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] prisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altChickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] altChickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] altChickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] altChickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] altChickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altChickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] altPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] altPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] altPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] altPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] chickenPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] chickenPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] chickenPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] combinedAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, altTestClustLists, altConverter, altTestGames);
        int[] combinedAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] combinedAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] combinedAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] combinedAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] combinedAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        double[][] predictedResultMatrix = new double[7][14];
        predictedResultMatrix[0][0] = ((double) altAutAltTestPredAccuracy[0]) / ((double) altAutAltTestPredAccuracy[1]);
        predictedResultMatrix[0][1] = ((double) altAutChickenTestPredAccuracy[0]) / ((double) altAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[0][2] = ((double) altAutPrisonTestPredAccuracy[0]) / ((double) altAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[0][3] = ((double) (altAutAltTestPredAccuracy[0] + altAutChickenTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[0][4] = ((double) (altAutAltTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[0][5] = ((double) (altAutChickenTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0])) / ((double) (altAutChickenTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[0][6] = ((double) (altAutAltTestPredAccuracy[0] + altAutChickenTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutChickenTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[0][7] = ((double) altAutAltTrainPredAccuracy[0]) / ((double) altAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[0][8] = ((double) altAutChickenTrainPredAccuracy[0]) / ((double) altAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[0][9] = ((double) altAutPrisonTrainPredAccuracy[0]) / ((double) altAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[0][10] = ((double) (altAutAltTrainPredAccuracy[0] + altAutChickenTrainPredAccuracy[0])) / ((double) (altAutAltTrainPredAccuracy[1] + altAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[0][11] = ((double) (altAutAltTrainPredAccuracy[0] + altAutPrisonTrainPredAccuracy[0])) / ((double) (altAutAltTrainPredAccuracy[1] + altAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[0][12] = ((double) (altAutChickenTrainPredAccuracy[0] + altAutPrisonTrainPredAccuracy[0])) / ((double) (altAutChickenTrainPredAccuracy[1] + altAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[0][13] = ((double) (altAutAltTrainPredAccuracy[0] + altAutChickenTrainPredAccuracy[0] + altAutPrisonTrainPredAccuracy[0])) / ((double) (altAutAltTrainPredAccuracy[1] + altAutChickenTrainPredAccuracy[1] + altAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[1][0] = ((double) chickenAutAltTestPredAccuracy[0]) / ((double) chickenAutAltTestPredAccuracy[1]);
        predictedResultMatrix[1][1] = ((double) chickenAutChickenTestPredAccuracy[0]) / ((double) chickenAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[1][2] = ((double) chickenAutPrisonTestPredAccuracy[0]) / ((double) chickenAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[1][3] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutChickenTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[1][4] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[1][5] = ((double) (chickenAutChickenTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0])) / ((double) (chickenAutChickenTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[1][6] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutChickenTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutChickenTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[1][7] = ((double) chickenAutAltTrainPredAccuracy[0]) / ((double) chickenAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[1][8] = ((double) chickenAutChickenTrainPredAccuracy[0]) / ((double) chickenAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[1][9] = ((double) chickenAutPrisonTrainPredAccuracy[0]) / ((double) chickenAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[1][10] = ((double) (chickenAutAltTrainPredAccuracy[0] + chickenAutChickenTrainPredAccuracy[0])) / ((double) (chickenAutAltTrainPredAccuracy[1] + chickenAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[1][11] = ((double) (chickenAutAltTrainPredAccuracy[0] + chickenAutPrisonTrainPredAccuracy[0])) / ((double) (chickenAutAltTrainPredAccuracy[1] + chickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[1][12] = ((double) (chickenAutChickenTrainPredAccuracy[0] + chickenAutPrisonTrainPredAccuracy[0])) / ((double) (chickenAutChickenTrainPredAccuracy[1] + chickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[1][13] = ((double) (chickenAutAltTrainPredAccuracy[0] + chickenAutChickenTrainPredAccuracy[0] + chickenAutPrisonTrainPredAccuracy[0])) / ((double) (chickenAutAltTrainPredAccuracy[1] + chickenAutChickenTrainPredAccuracy[1] + chickenAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[2][0] = ((double) prisonAutAltTestPredAccuracy[0]) / ((double) prisonAutAltTestPredAccuracy[1]);
        predictedResultMatrix[2][1] = ((double) prisonAutChickenTestPredAccuracy[0]) / ((double) prisonAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[2][2] = ((double) prisonAutPrisonTestPredAccuracy[0]) / ((double) prisonAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[2][3] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutChickenTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[2][4] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[2][5] = ((double) (prisonAutChickenTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0])) / ((double) (prisonAutChickenTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[2][6] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutChickenTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutChickenTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[2][7] = ((double) prisonAutAltTrainPredAccuracy[0]) / ((double) prisonAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[2][8] = ((double) prisonAutChickenTrainPredAccuracy[0]) / ((double) prisonAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[2][9] = ((double) prisonAutPrisonTrainPredAccuracy[0]) / ((double) prisonAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[2][10] = ((double) (prisonAutAltTrainPredAccuracy[0] + prisonAutChickenTrainPredAccuracy[0])) / ((double) (prisonAutAltTrainPredAccuracy[1] + prisonAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[2][11] = ((double) (prisonAutAltTrainPredAccuracy[0] + prisonAutPrisonTrainPredAccuracy[0])) / ((double) (prisonAutAltTrainPredAccuracy[1] + prisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[2][12] = ((double) (prisonAutChickenTrainPredAccuracy[0] + prisonAutPrisonTrainPredAccuracy[0])) / ((double) (prisonAutChickenTrainPredAccuracy[1] + prisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[2][13] = ((double) (prisonAutAltTrainPredAccuracy[0] + prisonAutChickenTrainPredAccuracy[0] + prisonAutPrisonTrainPredAccuracy[0])) / ((double) (prisonAutAltTrainPredAccuracy[1] + prisonAutChickenTrainPredAccuracy[1] + prisonAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[3][0] = ((double) altChickenAutAltTestPredAccuracy[0]) / ((double) altChickenAutAltTestPredAccuracy[1]);
        predictedResultMatrix[3][1] = ((double) altChickenAutChickenTestPredAccuracy[0]) / ((double) altChickenAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[3][2] = ((double) altChickenAutPrisonTestPredAccuracy[0]) / ((double) altChickenAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[3][3] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutChickenTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[3][4] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[3][5] = ((double) (altChickenAutChickenTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0])) / ((double) (altChickenAutChickenTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[3][6] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutChickenTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutChickenTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[3][7] = ((double) altChickenAutAltTrainPredAccuracy[0]) / ((double) altChickenAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[3][8] = ((double) altChickenAutChickenTrainPredAccuracy[0]) / ((double) altChickenAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[3][9] = ((double) altChickenAutPrisonTrainPredAccuracy[0]) / ((double) altChickenAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[3][10] = ((double) (altChickenAutAltTrainPredAccuracy[0] + altChickenAutChickenTrainPredAccuracy[0])) / ((double) (altChickenAutAltTrainPredAccuracy[1] + altChickenAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[3][11] = ((double) (altChickenAutAltTrainPredAccuracy[0] + altChickenAutPrisonTrainPredAccuracy[0])) / ((double) (altChickenAutAltTrainPredAccuracy[1] + altChickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[3][12] = ((double) (altChickenAutChickenTrainPredAccuracy[0] + altChickenAutPrisonTrainPredAccuracy[0])) / ((double) (altChickenAutChickenTrainPredAccuracy[1] + altChickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[3][13] = ((double) (altChickenAutAltTrainPredAccuracy[0] + altChickenAutChickenTrainPredAccuracy[0] + altChickenAutPrisonTrainPredAccuracy[0])) / ((double) (altChickenAutAltTrainPredAccuracy[1] + altChickenAutChickenTrainPredAccuracy[1] + altChickenAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[4][0] = ((double) altPrisonAutAltTestPredAccuracy[0]) / ((double) altPrisonAutAltTestPredAccuracy[1]);
        predictedResultMatrix[4][1] = ((double) altPrisonAutChickenTestPredAccuracy[0]) / ((double) altPrisonAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[4][2] = ((double) altPrisonAutPrisonTestPredAccuracy[0]) / ((double) altPrisonAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[4][3] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutChickenTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[4][4] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[4][5] = ((double) (altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0])) / ((double) (altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[4][6] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[4][7] = ((double) altPrisonAutAltTrainPredAccuracy[0]) / ((double) altPrisonAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[4][8] = ((double) altPrisonAutChickenTrainPredAccuracy[0]) / ((double) altPrisonAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[4][9] = ((double) altPrisonAutPrisonTrainPredAccuracy[0]) / ((double) altPrisonAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[4][10] = ((double) (altPrisonAutAltTrainPredAccuracy[0] + altPrisonAutChickenTrainPredAccuracy[0])) / ((double) (altPrisonAutAltTrainPredAccuracy[1] + altPrisonAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[4][11] = ((double) (altPrisonAutAltTrainPredAccuracy[0] + altPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (altPrisonAutAltTrainPredAccuracy[1] + altPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[4][12] = ((double) (altPrisonAutChickenTrainPredAccuracy[0] + altPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (altPrisonAutChickenTrainPredAccuracy[1] + altPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[4][13] = ((double) (altPrisonAutAltTrainPredAccuracy[0] + altPrisonAutChickenTrainPredAccuracy[0] + altPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (altPrisonAutAltTrainPredAccuracy[1] + altPrisonAutChickenTrainPredAccuracy[1] + altPrisonAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[5][0] = ((double) chickenPrisonAutAltTestPredAccuracy[0]) / ((double) altPrisonAutAltTestPredAccuracy[1]);
        predictedResultMatrix[5][1] = ((double) chickenPrisonAutChickenTestPredAccuracy[0]) / ((double) altPrisonAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[5][2] = ((double) chickenPrisonAutPrisonTestPredAccuracy[0]) / ((double) chickenPrisonAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[5][3] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutChickenTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[5][4] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[5][5] = ((double) (chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0])) / ((double) (chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[5][6] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[5][7] = ((double) chickenPrisonAutAltTrainPredAccuracy[0]) / ((double) chickenPrisonAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[5][8] = ((double) chickenPrisonAutChickenTrainPredAccuracy[0]) / ((double) chickenPrisonAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[5][9] = ((double) chickenPrisonAutPrisonTrainPredAccuracy[0]) / ((double) chickenPrisonAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[5][10] = ((double) (chickenPrisonAutAltTrainPredAccuracy[0] + chickenPrisonAutChickenTrainPredAccuracy[0])) / ((double) (chickenPrisonAutAltTrainPredAccuracy[1] + chickenPrisonAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[5][11] = ((double) (chickenPrisonAutAltTrainPredAccuracy[0] + chickenPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (chickenPrisonAutAltTrainPredAccuracy[1] + chickenPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[5][12] = ((double) (chickenPrisonAutChickenTrainPredAccuracy[0] + chickenPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (chickenPrisonAutChickenTrainPredAccuracy[1] + chickenPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[5][13] = ((double) (chickenPrisonAutAltTrainPredAccuracy[0] + chickenPrisonAutChickenTrainPredAccuracy[0] + chickenPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (chickenPrisonAutAltTrainPredAccuracy[1] + chickenPrisonAutChickenTrainPredAccuracy[1] + chickenPrisonAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[6][0] = ((double) combinedAutAltTestPredAccuracy[0]) / ((double) combinedAutAltTestPredAccuracy[1]);
        predictedResultMatrix[6][1] = ((double) combinedAutChickenTestPredAccuracy[0]) / ((double) combinedAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[6][2] = ((double) combinedAutPrisonTestPredAccuracy[0]) / ((double) combinedAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[6][3] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutChickenTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[6][4] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[6][5] = ((double) (combinedAutChickenTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0])) / ((double) (combinedAutChickenTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[6][6] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutChickenTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutChickenTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[6][7] = ((double) combinedAutAltTrainPredAccuracy[0]) / ((double) combinedAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[6][8] = ((double) combinedAutChickenTrainPredAccuracy[0]) / ((double) combinedAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[6][9] = ((double) combinedAutPrisonTrainPredAccuracy[0]) / ((double) combinedAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[6][10] = ((double) (combinedAutAltTrainPredAccuracy[0] + combinedAutChickenTrainPredAccuracy[0])) / ((double) (combinedAutAltTrainPredAccuracy[1] + combinedAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[6][11] = ((double) (combinedAutAltTrainPredAccuracy[0] + combinedAutPrisonTrainPredAccuracy[0])) / ((double) (combinedAutAltTrainPredAccuracy[1] + combinedAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[6][12] = ((double) (combinedAutChickenTrainPredAccuracy[0] + combinedAutPrisonTrainPredAccuracy[0])) / ((double) (combinedAutChickenTrainPredAccuracy[1] + combinedAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[6][13] = ((double) (combinedAutAltTrainPredAccuracy[0] + combinedAutChickenTrainPredAccuracy[0] + combinedAutPrisonTrainPredAccuracy[0])) / ((double) (combinedAutAltTrainPredAccuracy[1] + combinedAutChickenTrainPredAccuracy[1] + combinedAutPrisonTrainPredAccuracy[1]));

        double combinedBest = 0;
        for(int r = 0; r < predictedResultMatrix.length; r++)
        {
            for(int c = 0; c < predictedResultMatrix[r].length; c++)
            {
                double predictedResult = predictedResultMatrix[r][c];
                if(predictedResult > combinedBest)
                {
                    combinedBest = predictedResult;
                }
                System.out.print(predictedResult);
                System.out.print('\t');
            }
            System.out.println();
        }
        System.out.println(combinedBest);
        printConverted(predictedResultMatrix);
        printExtractedFiles(altAutomaton, chickenAutomaton, prisonAutomaton, altChickenAutomaton, altPrisonAutomaton, chickenPrisonAutomaton, combinedAutomaton, altConverter, chickenConverter, prisonConverter, fl2dsavcl);
    }

    private double calculatePrecitionAccuracy(DSGeneralAutomaton automaton, List<int[][]> clusteredData)
    {
        int total = 0;
        int totalCorrect = 0;
        for(int[][] clusteredDatum : clusteredData)
        {
            for(int i = 0; i < clusteredDatum.length - memoryLength - 1; i++)
            {
                int[][] sequence = new int[memoryLength][4];
                for(int hist = 0; hist < memoryLength; hist++)
                {
                    sequence[hist][0] = clusteredDatum[i + hist][0];
                    sequence[hist][1] = clusteredDatum[i + hist][1];
                    sequence[hist][2] = clusteredDatum[i + hist][2];
                    sequence[hist][3] = clusteredDatum[i + hist][3];
                }
                int[] predicted = automaton.getMostProbableActionAndMessage(sequence);
                if(predicted[0] == clusteredDatum[i + memoryLength][0] && predicted[1] == clusteredDatum[i + memoryLength][1])
                {
                    totalCorrect++;
                }
                total++;
            }
        }
        return ((double) totalCorrect) / ((double) total);
    }

    private void print(double[][] resultMatrix)
    {
        StringBuilder b = new StringBuilder();
        b.append(dir);
        b.append("\\Automaton Test Results.csv");
        try(FileWriter fWriter = new FileWriter(b.toString()))
        {
            for(int d = 0; d < dispAVClusters.length; d++)
            {
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(d);
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getGreedy());
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getPlacate());
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getCooperate());
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getAbsurd());
                lineBuilder.append('\n');
                fWriter.append(lineBuilder.toString());
            }
            fWriter.append('\n');
            for(int s = 0; s < saidAVClusters.length; s++)
            {
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(s);
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getGreedy());
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getPlacate());
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getCooperate());
                lineBuilder.append(',');
                lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getAbsurd());
                lineBuilder.append('\n');
                fWriter.append(lineBuilder.toString());
            }
            fWriter.append('\n');
            for(int r = 0; r < resultMatrix.length; r++)
            {
                StringBuilder lineBuilder = new StringBuilder();
                for(int c = 0; c < resultMatrix[r].length; c++)
                {
                    lineBuilder.append(resultMatrix[r][c]);
                    lineBuilder.append(',');
                }
                lineBuilder.append('\n');
                fWriter.append(lineBuilder.toString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void printConverted(double[][] resultMatrix)
    {
        {
            StringBuilder b = new StringBuilder();
            b.append(dir);
            b.append("\\Automaton Converted Test Results.csv");
            try(FileWriter fWriter = new FileWriter(b.toString()))
            {
                for(int d = 0; d < dispAVClusters.length; d++)
                {
                    StringBuilder lineBuilder = new StringBuilder();
                    lineBuilder.append(d);
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getGreedy());
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getPlacate());
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getCooperate());
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) dispAVClusters[d].getCentroid()).getAbsurd());
                    lineBuilder.append('\n');
                    fWriter.append(lineBuilder.toString());
                }
                fWriter.append('\n');
                for(int s = 0; s < saidAVClusters.length; s++)
                {
                    StringBuilder lineBuilder = new StringBuilder();
                    lineBuilder.append(s);
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getGreedy());
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getPlacate());
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getCooperate());
                    lineBuilder.append(',');
                    lineBuilder.append(((AttitudeVector) saidAVClusters[s].getCentroid()).getAbsurd());
                    lineBuilder.append('\n');
                    fWriter.append(lineBuilder.toString());
                }
                fWriter.append('\n');
                for(int r = 0; r < resultMatrix.length; r++)
                {
                    StringBuilder lineBuilder = new StringBuilder();
                    for(int c = 0; c < resultMatrix[r].length; c++)
                    {
                        lineBuilder.append(resultMatrix[r][c]);
                        lineBuilder.append(',');
                    }
                    lineBuilder.append('\n');
                    fWriter.append(lineBuilder.toString());
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private int[] calculateConvertedPredictionAccuracy(DSGeneralAutomaton automaton, List<int[][]> clusteredData, ActionAttitudeConverter converter, Game[] games)
    {
        int counter = 0;
        int total = 0;
        int totalCorrect = 0;
        for(int[][] clusteredDatum : clusteredData)
        {
            Game game = games[counter];
            counter++;
            for(int i = 0; i < clusteredDatum.length - memoryLength - 1; i++)
            {
                int[][] sequence = new int[memoryLength][4];
                for(int hist = 0; hist < memoryLength; hist++)
                {
                    sequence[hist][0] = clusteredDatum[i + hist][0];
                    sequence[hist][1] = clusteredDatum[i + hist][1];
                    sequence[hist][2] = clusteredDatum[i + hist][2];
                    sequence[hist][3] = clusteredDatum[i + hist][3];
                }
                int[] predicted = automaton.getMostProbableActionAndMessage(sequence);
                // Convert and check
                if(predicted[0] >= 0 && predicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[predicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(i + memoryLength - 1);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    if(predictedActs[0] == game.getPlayer1().getAction(i + memoryLength))
                    {
                        // Determine if anything should be said or not
                        SpeechAct[] messages = game.getPlayer1().getMessage(i + memoryLength);
                        int[] predictedMessages = converter.attitudeVectorToActions((AttitudeVector) saidAVClusters[predicted[1]].getCentroid());
                        if(((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getGreedy() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getPlacate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getCooperate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getAbsurd() == 0)
                        {
                            if(messages.length == 0)
                            {
                                totalCorrect++;
                            }
                        } else
                        {
                            boolean alternatingMessage = !(predictedMessages[0] == predictedMessages[2]
                                    && predictedMessages[1] == predictedMessages[3]);
                            for(int m = 0; m < messages.length; m++)
                            {
                                if(alternatingMessage && messages[m].size() == 2)
                                {
                                    if((messages[m].getJointAction()[0] == predictedMessages[0]
                                            && messages[m].getJointAction()[1] == predictedMessages[1]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[2]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[3])
                                            || (messages[m].getJointAction()[0] == predictedMessages[2]
                                            && messages[m].getJointAction()[1] == predictedMessages[3]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[0]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[1]))
                                    {
                                        totalCorrect++;
                                        break;
                                    }
                                } else if(!alternatingMessage && messages[m].size() == 1)
                                {
                                    if(messages[m].getJointAction()[0] == predictedMessages[0] && messages[m].getJointAction()[1] == predictedMessages[1])
                                    {
                                        totalCorrect++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                total++;
            }
        }
        int[] result = new int[2];
        result[0] = totalCorrect;
        result[1] = total;
        return result;
    }

    private void printExtractedFiles(DSGeneralAutomaton altAutomaton, DSGeneralAutomaton chickAutomaton,
                                     DSGeneralAutomaton pdAutomaton, DSGeneralAutomaton altChickAutomaton,
                                     DSGeneralAutomaton altPDAutomaton, DSGeneralAutomaton chickPDAutomaton,
                                     DSGeneralAutomaton allAutomaton, ActionAttitudeConverter altConverter,
                                     ActionAttitudeConverter chickenConverter, ActionAttitudeConverter prisonConverter,
                                     FeatList2DSAttVecClustList fl2dsavcl)
    {
        for(int g = 0; g < altGames.length; g++)
        {
            printExtractedFile(altFiles[2 * g], altGames[g], gameFeatureMap.get(altGames[g]), altAutomaton, chickAutomaton, allAutomaton, altConverter, fl2dsavcl, "alternator_", altTrainFeatureCollection.contains(gameFeatureMap.get(altGames[g])));
        }
        for(int g = 0; g < chickenGames.length; g++)
        {
            printExtractedFile(chickenFiles[2 * g], chickenGames[g], gameFeatureMap.get(chickenGames[g]), chickAutomaton, altAutomaton, allAutomaton, chickenConverter, fl2dsavcl, "chicken_", chickenTrainFeatureCollection.contains(gameFeatureMap.get(chickenGames[g])));
        }
        for(int g = 0; g < prisonGames.length; g++)
        {
            printExtractedFile(prisonFiles[2 * g], prisonGames[g], gameFeatureMap.get(prisonGames[g]), pdAutomaton, altAutomaton, allAutomaton, prisonConverter, fl2dsavcl, "prisoners_dilemma_", prisonTrainFeatureCollection.contains(gameFeatureMap.get(prisonGames[g])));
        }
    }

    private void printExtractedFile(File gameFile, Game game, List<Feature> fList, DSGeneralAutomaton matchingAutomaton,
                                    DSGeneralAutomaton otherAutomaton, DSGeneralAutomaton allAutomaton, ActionAttitudeConverter converter,
                                    FeatList2DSAttVecClustList fl2dsavcl, String gameName, boolean training)
    {
//        ArrayList<Feature> fList = new ArrayList<>();
//        fList.addAll(featureList);
        int[][] clustIndexList = fl2dsavcl.getClusterIndexList(fList,dispAVClusters, saidAVClusters);
        int[][] predictedAct = new int[clustIndexList.length - memoryLength][3];
        for(int i = 0; i < clustIndexList.length - memoryLength; i++)
        {
            int[][] sequence = new int[memoryLength][4];
            for(int hist = 0; hist < memoryLength; hist++)
            {
                sequence[hist][0] = clustIndexList[i + hist][0];
                sequence[hist][1] = clustIndexList[i + hist][1];
                sequence[hist][2] = clustIndexList[i + hist][2];
                sequence[hist][3] = clustIndexList[i + hist][3];
            }
            int[] predicted1 = matchingAutomaton.getMostProbableActionAndMessage(sequence);
            int[] predicted2 = otherAutomaton.getMostProbableActionAndMessage(sequence);
            int[] predicted3 = allAutomaton.getMostProbableActionAndMessage(sequence);
            int[] prevActPair = game.getActionPair(i + memoryLength - 1);
            AttitudeVector predictedAV1 = (AttitudeVector) dispAVClusters[predicted1[0]].getCentroid();
            AttitudeVector predictedAV2 = (AttitudeVector) dispAVClusters[predicted2[0]].getCentroid();
            AttitudeVector predictedAV3 = (AttitudeVector) dispAVClusters[predicted3[0]].getCentroid();
            AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
            AttitudeVector extrapolatedAV1 = AttitudeVector.extrapolate(prevAttDisp, predictedAV1);
            AttitudeVector extrapolatedAV2 = AttitudeVector.extrapolate(prevAttDisp, predictedAV2);
            AttitudeVector extrapolatedAV3 = AttitudeVector.extrapolate(prevAttDisp, predictedAV3);
            int[] predictedActs1 = converter.attitudeVectorToActions(extrapolatedAV1);
            int[] predictedActs2 = converter.attitudeVectorToActions(extrapolatedAV2);
            int[] predictedActs3 = converter.attitudeVectorToActions(extrapolatedAV3);
            predictedAct[i][0] = predictedActs1[0];
            predictedAct[i][1] = predictedActs2[0];
            predictedAct[i][2] = predictedActs3[0];
        }
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(dir);
        fileNameBuilder.append("\\Extracted File\\");
        fileNameBuilder.append(gameName);
        fileNameBuilder.append(gameFile.getPath().substring(gameFile.getPath().indexOf('_') + 1, gameFile.getPath().indexOf("_activity")));
        fileNameBuilder.append("_extract.csv");
        try(FileWriter fWriter = new FileWriter(fileNameBuilder.toString()))
        {
            fWriter.append("Round,P1 Act,P1 Message,P2 Act,P2 Message,P1 Payoff,P2 Payoff,P1 AV Displayed,P1 AV Said,P2 AV Displayed,P2 AV Said,Predicted Same,Predicted Different,Predicted All");
            fWriter.append('\n');
            int totCount = 0;
            int sameCorrect = 0;
            int otherCorrect = 0;
            int allCorrect = 0;
            DecimalFormat df = new DecimalFormat("0.###");
            for(int round = 0; round < game.getNumRounds(); round++)
            {
                StringBuilder b = new StringBuilder();
                int[] actPair = game.getActionPair(round);
                SpeechAct[] player1Acts = game.getPlayer1().getMessage(round);
                SpeechAct[] player2Acts = game.getPlayer2().getMessage(round);
                b.append(round + 1);
                b.append(',');
                b.append(actPair[0]);
                b.append(',');
                if(player1Acts.length > 0)
                {
                    b.append('\"');
                    for(int i = 0; i < player1Acts.length; i++)
                    {
                        if(i > 0)
                        {
                            b.append(';');
                        }
                        SpeechAct message = player1Acts[i];
                        if(message.size() > 1)
                        {
                            b.append(message.getJointAction()[0]);
                            b.append(',');
                            b.append(message.getJointAction()[1]);
                            b.append(" + ");
                            b.append(message.getSecondJointAction()[0]);
                            b.append(',');
                            b.append(message.getSecondJointAction()[1]);
                        } else if(message.getAffirmative())
                        {
                            b.append(message.getJointAction()[0]);
                            b.append(',');
                            b.append(message.getJointAction()[1]);
                        } else
                        {
                            b.append('!');
                            b.append(message.getJointAction()[1]);
                        }
                    }
                    b.append('\"');
                } else
                {
                    b.append('-');
                }
                b.append(',');
                b.append(actPair[1]);
                b.append(',');
                if(player2Acts.length > 0)
                {
                    b.append('\"');
                    for(int i = 0; i < player2Acts.length; i++)
                    {
                        if(i > 0)
                        {
                            b.append(';');
                        }
                        SpeechAct message = player2Acts[i];
                        if(message.size() > 1)
                        {
                            b.append(message.getJointAction()[1]);
                            b.append(',');
                            b.append(message.getJointAction()[0]);
                            b.append(" + ");
                            b.append(message.getSecondJointAction()[1]);
                            b.append(',');
                            b.append(message.getSecondJointAction()[0]);
                        } else if(message.getAffirmative())
                        {
                            b.append(message.getJointAction()[1]);
                            b.append(',');
                            b.append(message.getJointAction()[0]);
                        } else
                        {
                            b.append('!');
                            b.append(message.getJointAction()[1]);
                        }
                    }
                    b.append('\"');
                } else
                {
                    b.append('-');
                }
                b.append(',');
                b.append(game.getPayoffMatrix().getRowPlayerValue(actPair[0], actPair[1]));
                b.append(',');
                b.append(game.getPayoffMatrix().getColPlayerValue(actPair[0], actPair[1]));
                b.append(",\"");
                Feature feature = fList.get(round);
                AttitudeVector dAV = feature.getAttitudeDisplayed();
                b.append(df.format(dAV.getGreedy()));
                b.append(',');
                b.append(df.format(dAV.getPlacate()));
                b.append(',');
                b.append(df.format(dAV.getCooperate()));
                b.append(',');
                b.append(df.format(dAV.getAbsurd()));
                b.append("\",\"");
                AttitudeVector sAV = feature.getAttitudeSaid();
                b.append(df.format(sAV.getGreedy()));
                b.append(',');
                b.append(df.format(sAV.getPlacate()));
                b.append(',');
                b.append(df.format(sAV.getCooperate()));
                b.append(',');
                b.append(df.format(sAV.getAbsurd()));
                b.append("\",\"");
                AttitudeVector odAV = feature.getOtherAttitudeDisplayed();
                b.append(df.format(odAV.getGreedy()));
                b.append(',');
                b.append(df.format(odAV.getPlacate()));
                b.append(',');
                b.append(df.format(odAV.getCooperate()));
                b.append(',');
                b.append(df.format(odAV.getAbsurd()));
                b.append("\",\"");
                AttitudeVector osAV = feature.getOtherAttitudeSaid();
                b.append(df.format(osAV.getGreedy()));
                b.append(',');
                b.append(df.format(osAV.getPlacate()));
                b.append(',');
                b.append(df.format(osAV.getCooperate()));
                b.append(',');
                b.append(df.format(osAV.getAbsurd()));
                b.append("\",");
                if(round > memoryLength)
                {
                    b.append(predictedAct[round - memoryLength][0]);
                    if(actPair[0] == predictedAct[round - memoryLength][0])
                    {
                        b.append(" (Correct)");
                        sameCorrect++;
                    } else
                    {
                        b.append(" (Incorrect");
                    }
                    b.append(',');
                    b.append(predictedAct[round - memoryLength][1]);
                    if(actPair[0] == predictedAct[round - memoryLength][1])
                    {
                        b.append(" (Correct)");
                        otherCorrect++;
                    } else
                    {
                        b.append(" (Incorrect");
                    }
                    b.append(',');
                    b.append(predictedAct[round - memoryLength][2]);
                    if(actPair[0] == predictedAct[round - memoryLength][2])
                    {
                        b.append(" (Correct)");
                        allCorrect++;
                    } else
                    {
                        b.append(" (Incorrect");
                    }
                    totCount++;
                } else
                {
                    b.append("-,-,-");
                }
                if((round - memoryLength) == predictedAct.length)
                {
//                    System.out.println("End reached!");
                }
                fWriter.append(b.toString());
                fWriter.append('\n');
            }
            fWriter.append('\n');
            if(training)
            {
                fWriter.append("Training");
            } else
            {
                fWriter.append("Testing");
            }
            fWriter.append(",,,,,,,,,,,");
            StringBuilder endBuilder = new StringBuilder();
            endBuilder.append(sameCorrect);
            endBuilder.append('/');
            endBuilder.append(totCount);
            endBuilder.append(" = ");
            endBuilder.append(df.format((double) sameCorrect / ((double) totCount)));
            endBuilder.append(',');
            endBuilder.append(otherCorrect);
            endBuilder.append('/');
            endBuilder.append(totCount);
            endBuilder.append(" = ");
            endBuilder.append(df.format((double) otherCorrect / ((double) totCount)));
            endBuilder.append(',');
            endBuilder.append(allCorrect);
            endBuilder.append('/');
            endBuilder.append(totCount);
            endBuilder.append(" = ");
            endBuilder.append(df.format((double) allCorrect / ((double) totCount)));
            fWriter.append(endBuilder.toString());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class AltFileFilter extends javax.swing.filechooser.FileFilter
    {

        @Override
        public boolean accept(File f)
        {
            return f.getPath().contains("blocks");
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

    private class ChickenFileFilter extends javax.swing.filechooser.FileFilter
    {

        @Override
        public boolean accept(File f)
        {
            return f.getPath().contains("chicken");
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

    private class PrisonFileFilter extends javax.swing.filechooser.FileFilter
    {

        @Override
        public boolean accept(File f)
        {
            return f.getPath().contains("prison");
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

}