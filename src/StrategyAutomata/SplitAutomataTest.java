package StrategyAutomata;

import Attitudes.ActionAttitudeConverter;
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
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class SplitAutomataTest
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
    private int nTrees;

    public SplitAutomataTest(double percentTraining, int memory, int trees)
    {
        memoryLength = memory;
        nTrees = trees;
        initialize(percentTraining);
    }

    private void initialize(double percentTraining)
    {
//        System.out.println("What predictive power?");
//        Scanner keys = new Scanner(System.in);
        boolean chooseAlt = false;
        pPower = 1;
        if(!chooseAlt)
        {
            File folder = new File(dir);
            List<File> altFileList = new ArrayList<>();
            List<File> chickenFileList = new ArrayList<>();
            List<File> prisonFileList = new ArrayList<>();
            for(File file : folder.listFiles())
            {
                if(file.getPath().contains("blocks"))
                {
                    altFileList.add(file);
                } else if(file.getPath().contains("chicken"))
                {
                    chickenFileList.add(file);
                } else if(file.getPath().contains("prisoners"))
                {
                    prisonFileList.add(file);
                }
            }
            altFiles = new File[altFileList.size()];
            chickenFiles = new File[chickenFileList.size()];
            prisonFiles = new File[prisonFileList.size()];
            for(int f = 0; f < altFileList.size(); f++)
            {
                altFiles[f] = altFileList.get(f);
            }
            for(int f = 0; f < chickenFileList.size(); f++)
            {
                chickenFiles[f] = chickenFileList.get(f);
            }
            for(int f = 0; f < prisonFileList.size(); f++)
            {
                prisonFiles[f] = prisonFileList.get(f);
            }
        }
//        if(pPower > 1)
//        {
//            pPower = 1;
//        } else if(pPower < 0)
//        {
//            pPower = 0;
//        }

        if(chooseAlt)
        {
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
        }
        TXTtoGame txtToGame = new TXTtoGame();
        altGames = new Game[altFiles.length / 2];
        chickenGames = new Game[chickenFiles.length / 2];
        prisonGames = new Game[prisonFiles.length / 2];
        for(int g = 0; g < altGames.length; g++)
        {
//            System.out.println(g);
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
    public double[][] run(int fileNum, boolean verbose)
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
        int minSecond = 2;
//        System.out.print("Alt Decision Points: ");
//        System.out.println(altAutomaton.calculateDecisionPoints(minSecond));
//        System.out.print("Chicken Decision Points: ");
//        System.out.println(chickenAutomaton.calculateDecisionPoints(minSecond));
//        System.out.print("Prison Decision Points: ");
//        System.out.println(prisonAutomaton.calculateDecisionPoints(minSecond));
//        System.out.print("Alt+Chick Decision Points: ");
//        System.out.println(altChickenAutomaton.calculateDecisionPoints(minSecond));
//        System.out.print("Alt+PD Decision Points: ");
//        System.out.println(altPrisonAutomaton.calculateDecisionPoints(minSecond));
//        System.out.print("Chick+PD Decision Points: ");
//        System.out.println(chickenPrisonAutomaton.calculateDecisionPoints(minSecond));
//        System.out.print("Combined Decision Points: ");
//        System.out.println(combinedAutomaton.calculateDecisionPoints(minSecond));
        int splitNum = 10;
        AutomataGroup altAutomata = new AutomataGroup(splitNum, altAutomaton);
        AutomataGroup chickenAutomata = new AutomataGroup(splitNum, chickenAutomaton);
        AutomataGroup prisonAutomata = new AutomataGroup(splitNum, prisonAutomaton);
        AutomataGroup altChickenAutomata = new AutomataGroup(splitNum, altChickenAutomaton);
        AutomataGroup altPrisonAutomata = new AutomataGroup(splitNum, altPrisonAutomaton);
        AutomataGroup chickenPrisonAutomata = new AutomataGroup(splitNum, chickenPrisonAutomaton);
        AutomataGroup combinedAutomata = new AutomataGroup(splitNum, combinedAutomaton);
//        System.out.println("Alt Group Decision Point Numberss: ");
//        System.out.println(altAutomata.numDecisionPointsString(minSecond));
//        System.out.println("Chicken Group Decision Point Numberss: ");
//        System.out.println(chickenAutomata.numDecisionPointsString(minSecond));
//        System.out.println("Prison Group Decision Point Numberss: ");
//        System.out.println(prisonAutomata.numDecisionPointsString(minSecond));
//        System.out.println("Alt+Chick Group Decision Point Numberss: ");
//        System.out.println(altChickenAutomata.numDecisionPointsString(minSecond));
//        System.out.println("Alt+PD Group Decision Point Numberss: ");
//        System.out.println(altPrisonAutomata.numDecisionPointsString(minSecond));
//        System.out.println("Chick+PD Group Decision Point Numberss: ");
//        System.out.println(chickenPrisonAutomata.numDecisionPointsString(minSecond));
//        System.out.println("Combined Group Decision Point Numberss: ");
//        System.out.println(combinedAutomata.numDecisionPointsString(minSecond));
//        DSGeneralAutomaton splitAlt = altAutomaton.extractAutomaton();
//        DSGeneralAutomaton splitChicken = chickenAutomaton.extractAutomaton();
//        DSGeneralAutomaton splitPrison = prisonAutomaton.extractAutomaton();
//        System.out.print("Remaining alt observations: ");
//        System.out.println(altAutomaton.getTotalCount());
//        System.out.print("Extracted alt observations: ");
//        System.out.println(splitAlt.getTotalCount());
//        System.out.print("Remaining chicken observations: ");
//        System.out.println(chickenAutomaton.getTotalCount());
//        System.out.print("Extracted chicken observations: ");
//        System.out.println(splitChicken.getTotalCount());
//        System.out.print("Remaining pd observations: ");
//        System.out.println(prisonAutomaton.getTotalCount());
//        System.out.print("Extracted pd observations: ");
//        System.out.println(splitPrison.getTotalCount());
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
        resultMatrix[0][0] = calculatePrecitionAccuracy(altAutomata, altTestClustLists);
        resultMatrix[0][1] = calculatePrecitionAccuracy(altAutomata, chickenTestClustLists);
        resultMatrix[0][2] = calculatePrecitionAccuracy(altAutomata, prisonTestClustLists);
        resultMatrix[0][3] = calculatePrecitionAccuracy(altAutomata, altChickenTestClustLists);
        resultMatrix[0][4] = calculatePrecitionAccuracy(altAutomata, altPrisonTestClustLists);
        resultMatrix[0][5] = calculatePrecitionAccuracy(altAutomata, chickenPrisonTestClustLists);
        resultMatrix[0][6] = calculatePrecitionAccuracy(altAutomata, combinedTestClustLists);
        resultMatrix[0][7] = calculatePrecitionAccuracy(altAutomata, altTrainClustLists);
        resultMatrix[0][8] = calculatePrecitionAccuracy(altAutomata, chickenTrainClustLists);
        resultMatrix[0][9] = calculatePrecitionAccuracy(altAutomata, prisonTrainClustLists);
        resultMatrix[0][10] = calculatePrecitionAccuracy(altAutomata, altChickenTrainClustLists);
        resultMatrix[0][11] = calculatePrecitionAccuracy(altAutomata, altPrisonTrainClustLists);
        resultMatrix[0][12] = calculatePrecitionAccuracy(altAutomata, chickenPrisonTrainClustLists);
        resultMatrix[0][13] = calculatePrecitionAccuracy(altAutomata, combinedTrainClustLists);
        resultMatrix[1][0] = calculatePrecitionAccuracy(chickenAutomata, altTestClustLists);
        resultMatrix[1][1] = calculatePrecitionAccuracy(chickenAutomata, chickenTestClustLists);
        resultMatrix[1][2] = calculatePrecitionAccuracy(chickenAutomata, prisonTestClustLists);
        resultMatrix[1][3] = calculatePrecitionAccuracy(chickenAutomata, altChickenTestClustLists);
        resultMatrix[1][4] = calculatePrecitionAccuracy(chickenAutomata, altPrisonTestClustLists);
        resultMatrix[1][5] = calculatePrecitionAccuracy(chickenAutomata, chickenPrisonTestClustLists);
        resultMatrix[1][6] = calculatePrecitionAccuracy(chickenAutomata, combinedTestClustLists);
        resultMatrix[1][7] = calculatePrecitionAccuracy(chickenAutomata, altTrainClustLists);
        resultMatrix[1][8] = calculatePrecitionAccuracy(chickenAutomata, chickenTrainClustLists);
        resultMatrix[1][9] = calculatePrecitionAccuracy(chickenAutomata, prisonTrainClustLists);
        resultMatrix[1][10] = calculatePrecitionAccuracy(chickenAutomata, altChickenTrainClustLists);
        resultMatrix[1][11] = calculatePrecitionAccuracy(chickenAutomata, altPrisonTrainClustLists);
        resultMatrix[1][12] = calculatePrecitionAccuracy(chickenAutomata, chickenPrisonTrainClustLists);
        resultMatrix[1][13] = calculatePrecitionAccuracy(chickenAutomata, combinedTrainClustLists);
        resultMatrix[2][0] = calculatePrecitionAccuracy(prisonAutomata, altTestClustLists);
        resultMatrix[2][1] = calculatePrecitionAccuracy(prisonAutomata, chickenTestClustLists);
        resultMatrix[2][2] = calculatePrecitionAccuracy(prisonAutomata, prisonTestClustLists);
        resultMatrix[2][3] = calculatePrecitionAccuracy(prisonAutomata, altChickenTestClustLists);
        resultMatrix[2][4] = calculatePrecitionAccuracy(prisonAutomata, altPrisonTestClustLists);
        resultMatrix[2][5] = calculatePrecitionAccuracy(prisonAutomata, chickenPrisonTestClustLists);
        resultMatrix[2][6] = calculatePrecitionAccuracy(prisonAutomata, combinedTestClustLists);
        resultMatrix[2][7] = calculatePrecitionAccuracy(prisonAutomata, altTrainClustLists);
        resultMatrix[2][8] = calculatePrecitionAccuracy(prisonAutomata, chickenTrainClustLists);
        resultMatrix[2][9] = calculatePrecitionAccuracy(prisonAutomata, prisonTrainClustLists);
        resultMatrix[2][10] = calculatePrecitionAccuracy(prisonAutomata, altChickenTrainClustLists);
        resultMatrix[2][11] = calculatePrecitionAccuracy(prisonAutomata, altPrisonTrainClustLists);
        resultMatrix[2][12] = calculatePrecitionAccuracy(prisonAutomata, chickenPrisonTrainClustLists);
        resultMatrix[2][13] = calculatePrecitionAccuracy(prisonAutomata, combinedTrainClustLists);
        resultMatrix[3][0] = calculatePrecitionAccuracy(altChickenAutomata, altTestClustLists);
        resultMatrix[3][1] = calculatePrecitionAccuracy(altChickenAutomata, chickenTestClustLists);
        resultMatrix[3][2] = calculatePrecitionAccuracy(altChickenAutomata, prisonTestClustLists);
        resultMatrix[3][3] = calculatePrecitionAccuracy(altChickenAutomata, altChickenTestClustLists);
        resultMatrix[3][4] = calculatePrecitionAccuracy(altChickenAutomata, altPrisonTestClustLists);
        resultMatrix[3][5] = calculatePrecitionAccuracy(altChickenAutomata, chickenPrisonTestClustLists);
        resultMatrix[3][6] = calculatePrecitionAccuracy(altChickenAutomata, combinedTestClustLists);
        resultMatrix[3][7] = calculatePrecitionAccuracy(altChickenAutomata, altTrainClustLists);
        resultMatrix[3][8] = calculatePrecitionAccuracy(altChickenAutomata, chickenTrainClustLists);
        resultMatrix[3][9] = calculatePrecitionAccuracy(altChickenAutomata, prisonTrainClustLists);
        resultMatrix[3][10] = calculatePrecitionAccuracy(altChickenAutomata, altChickenTrainClustLists);
        resultMatrix[3][11] = calculatePrecitionAccuracy(altChickenAutomata, altPrisonTrainClustLists);
        resultMatrix[3][12] = calculatePrecitionAccuracy(altChickenAutomata, chickenPrisonTrainClustLists);
        resultMatrix[3][13] = calculatePrecitionAccuracy(altChickenAutomata, combinedTrainClustLists);
        resultMatrix[4][0] = calculatePrecitionAccuracy(altPrisonAutomata, altTestClustLists);
        resultMatrix[4][1] = calculatePrecitionAccuracy(altPrisonAutomata, chickenTestClustLists);
        resultMatrix[4][2] = calculatePrecitionAccuracy(altPrisonAutomata, prisonTestClustLists);
        resultMatrix[4][3] = calculatePrecitionAccuracy(altPrisonAutomata, altChickenTestClustLists);
        resultMatrix[4][4] = calculatePrecitionAccuracy(altPrisonAutomata, altPrisonTestClustLists);
        resultMatrix[4][5] = calculatePrecitionAccuracy(altPrisonAutomata, chickenPrisonTestClustLists);
        resultMatrix[4][6] = calculatePrecitionAccuracy(altPrisonAutomata, combinedTestClustLists);
        resultMatrix[4][7] = calculatePrecitionAccuracy(altPrisonAutomata, altTrainClustLists);
        resultMatrix[4][8] = calculatePrecitionAccuracy(altPrisonAutomata, chickenTrainClustLists);
        resultMatrix[4][9] = calculatePrecitionAccuracy(altPrisonAutomata, prisonTrainClustLists);
        resultMatrix[4][10] = calculatePrecitionAccuracy(altPrisonAutomata, altChickenTrainClustLists);
        resultMatrix[4][11] = calculatePrecitionAccuracy(altPrisonAutomata, altPrisonTrainClustLists);
        resultMatrix[4][12] = calculatePrecitionAccuracy(altPrisonAutomata, chickenPrisonTrainClustLists);
        resultMatrix[4][13] = calculatePrecitionAccuracy(altPrisonAutomata, combinedTrainClustLists);
        resultMatrix[5][0] = calculatePrecitionAccuracy(chickenPrisonAutomata, altTestClustLists);
        resultMatrix[5][1] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenTestClustLists);
        resultMatrix[5][2] = calculatePrecitionAccuracy(chickenPrisonAutomata, prisonTestClustLists);
        resultMatrix[5][3] = calculatePrecitionAccuracy(chickenPrisonAutomata, altChickenTestClustLists);
        resultMatrix[5][4] = calculatePrecitionAccuracy(chickenPrisonAutomata, altPrisonTestClustLists);
        resultMatrix[5][5] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenPrisonTestClustLists);
        resultMatrix[5][6] = calculatePrecitionAccuracy(chickenPrisonAutomata, combinedTestClustLists);
        resultMatrix[5][7] = calculatePrecitionAccuracy(chickenPrisonAutomata, altTrainClustLists);
        resultMatrix[5][8] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenTrainClustLists);
        resultMatrix[5][9] = calculatePrecitionAccuracy(chickenPrisonAutomata, prisonTrainClustLists);
        resultMatrix[5][10] = calculatePrecitionAccuracy(chickenPrisonAutomata, altChickenTrainClustLists);
        resultMatrix[5][11] = calculatePrecitionAccuracy(chickenPrisonAutomata, altPrisonTrainClustLists);
        resultMatrix[5][12] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenPrisonTrainClustLists);
        resultMatrix[5][13] = calculatePrecitionAccuracy(chickenPrisonAutomata, combinedTrainClustLists);
        resultMatrix[6][0] = calculatePrecitionAccuracy(combinedAutomata, altTestClustLists);
        resultMatrix[6][1] = calculatePrecitionAccuracy(combinedAutomata, chickenTestClustLists);
        resultMatrix[6][2] = calculatePrecitionAccuracy(combinedAutomata, prisonTestClustLists);
        resultMatrix[6][3] = calculatePrecitionAccuracy(combinedAutomata, altChickenTestClustLists);
        resultMatrix[6][4] = calculatePrecitionAccuracy(combinedAutomata, altPrisonTestClustLists);
        resultMatrix[6][5] = calculatePrecitionAccuracy(combinedAutomata, chickenPrisonTestClustLists);
        resultMatrix[6][6] = calculatePrecitionAccuracy(combinedAutomata, combinedTestClustLists);
        resultMatrix[6][7] = calculatePrecitionAccuracy(combinedAutomata, altTrainClustLists);
        resultMatrix[6][8] = calculatePrecitionAccuracy(combinedAutomata, chickenTrainClustLists);
        resultMatrix[6][9] = calculatePrecitionAccuracy(combinedAutomata, prisonTrainClustLists);
        resultMatrix[6][10] = calculatePrecitionAccuracy(combinedAutomata, altChickenTrainClustLists);
        resultMatrix[6][11] = calculatePrecitionAccuracy(combinedAutomata, altPrisonTrainClustLists);
        resultMatrix[6][12] = calculatePrecitionAccuracy(combinedAutomata, chickenPrisonTrainClustLists);
        resultMatrix[6][13] = calculatePrecitionAccuracy(combinedAutomata, combinedTrainClustLists);
        if(verbose)
        {
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
            print(resultMatrix, fileNum);
        }

        int[] altAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, altTestClustLists, altConverter, altTestGames);
        int[] altAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] altAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] altAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] altAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, altTestClustLists, altConverter, altTestGames);
        int[] chickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] chickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] chickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] prisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, altTestClustLists, altConverter, altTestGames);
        int[] prisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] prisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] prisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] prisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] prisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altChickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, altTestClustLists, altConverter, altTestGames);
        int[] altChickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] altChickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] altChickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] altChickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altChickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, altTestClustLists, altConverter, altTestGames);
        int[] altPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] altPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] altPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] altPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, altTestClustLists, altConverter, altTestGames);
        int[] chickenPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] chickenPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] chickenPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] combinedAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, altTestClustLists, altConverter, altTestGames);
        int[] combinedAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, chickenTestClustLists, chickenConverter, chickenTestGames);
        int[] combinedAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, prisonTestClustLists, prisonConverter, prisonTestGames);
        int[] combinedAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] combinedAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] combinedAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        System.out.print("Alternator train: ");
        System.out.print(altAutAltTrainPredAccuracy[0]);
        System.out.print(" / ");
        System.out.println(altAutAltTrainPredAccuracy[1]);

        System.out.print("Chicken train: ");
        System.out.print(chickenAutChickenTrainPredAccuracy[0]);
        System.out.print(" / ");
        System.out.println(chickenAutChickenTrainPredAccuracy[1]);

        System.out.print("Prisoner's Dilemma train: ");
        System.out.print(prisonAutPrisonTrainPredAccuracy[0]);
        System.out.print(" / ");
        System.out.println(prisonAutPrisonTrainPredAccuracy[1]);

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

        if(verbose)
        {
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
            printConverted(predictedResultMatrix, fileNum);
            printExtractedFiles(altAutomata, chickenAutomata, prisonAutomata, altChickenAutomata, altPrisonAutomata, chickenPrisonAutomata, combinedAutomata, altConverter, chickenConverter, prisonConverter, fl2dsavcl);
        }
        return predictedResultMatrix;
    }

    private double calculatePrecitionAccuracy(AutomataGroup automata, List<int[][]> clusteredData)
    {
        int total = 0;
        int totalCorrect = 0;
        int[] previouslyCorrect = new int[2];
        previouslyCorrect[0] = -1;
        previouslyCorrect[1] = -1;
        for(int[][] clusteredDatum : clusteredData)
        {
            automata.prepForNewGame();
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
                int[] predicted = automata.getMostProbableActionAndMessage(previouslyCorrect, sequence);
                if(predicted[0] == clusteredDatum[i + memoryLength][0] && predicted[1] == clusteredDatum[i + memoryLength][1])
                {
                    totalCorrect++;
                }
                previouslyCorrect[0] = clusteredDatum[i + memoryLength][0];
                previouslyCorrect[1] = clusteredDatum[i + memoryLength][1];
                total++;
            }
        }
        return ((double) totalCorrect) / ((double) total);
    }

    private void print(double[][] resultMatrix, int fileNum)
    {
        StringBuilder b = new StringBuilder();
        b.append(dir);
        b.append("\\Automaton Test Results ");
        b.append(fileNum);
        b.append("M");
        b.append(memoryLength);
        b.append("T");
        b.append(nTrees);
        b.append(".csv");
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

    private void printConverted(double[][] resultMatrix, int fileNum)
    {
        {
            StringBuilder b = new StringBuilder();
            b.append(dir);
            b.append("\\Automaton Converted Test Results");
            b.append(fileNum);
            b.append(".csv");
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

    private int[] calculateConvertedPredictionAccuracy(AutomataGroup automata, List<int[][]> clusteredData, ActionAttitudeConverter converter, Game[] games)
    {
        int counter = 0;
        int total = 0;
        int totalCorrect = 0;
        for(int[][] clusteredDatum : clusteredData)
        {
            Game game = games[counter];
            counter++;
            automata.prepForNewGame();
            boolean[] previouslyCorrect = new boolean[automata.size() + 1];
            for(int pC = 0; pC < previouslyCorrect.length; pC++)
            {
                previouslyCorrect[pC] = true;
            }
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
                int[][] predictionArray = automata.getPredictionArray(previouslyCorrect, sequence);
                previouslyCorrect = new boolean[predictionArray.length];
                for(int p = 0; p < predictionArray.length; p++)
                {
                    int[] predicted = predictionArray[p];
                    if(predicted[0] >= 0 && predicted[1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[predicted[0]].getCentroid();
                        int[] prevActPair = game.getActionPair(i + memoryLength - 1);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == game.getPlayer1().getAction(i + memoryLength))
                        {
                            if(p == 0)
                            {
                                totalCorrect++;
                            }
                            previouslyCorrect[p] = true;
//                            // Determine if anything should be said or not
//                            SpeechAct[] messages = game.getPlayer1().getMessage(i + memoryLength);
//                            int[] predictedMessages = converter.attitudeVectorToActions((AttitudeVector) saidAVClusters[predicted[1]].getCentroid());
//                            if(((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getGreedy() == 0 &&
//                                    ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getPlacate() == 0 &&
//                                    ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getCooperate() == 0 &&
//                                    ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getAbsurd() == 0)
//                            {
//                                if(messages.length == 0)
//                                {
//                                    previouslyCorrect[p] = true;
//                                    if(p == 0)
//                                    {
//                                        totalCorrect++;
//                                    }
//                                }
//                            } else
//                            {
//                                boolean alternatingMessage = !(predictedMessages[0] == predictedMessages[2]
//                                        && predictedMessages[1] == predictedMessages[3]);
//                                for(int m = 0; m < messages.length; m++)
//                                {
//                                    if(alternatingMessage && messages[m].size() == 2)
//                                    {
//                                        if((messages[m].getJointAction()[0] == predictedMessages[0]
//                                                && messages[m].getJointAction()[1] == predictedMessages[1]
//                                                && messages[m].getSecondJointAction()[0] == predictedMessages[2]
//                                                && messages[m].getSecondJointAction()[1] == predictedMessages[3])
//                                                || (messages[m].getJointAction()[0] == predictedMessages[2]
//                                                && messages[m].getJointAction()[1] == predictedMessages[3]
//                                                && messages[m].getSecondJointAction()[0] == predictedMessages[0]
//                                                && messages[m].getSecondJointAction()[1] == predictedMessages[1]))
//                                        {
//                                            previouslyCorrect[p] = true;
//                                            if(p == 0)
//                                            {
//                                                totalCorrect++;
//                                            }
//                                            break;
//                                        }
//                                    } else if(!alternatingMessage && messages[m].size() == 1)
//                                    {
//                                        if(messages[m].getJointAction()[0] == predictedMessages[0] && messages[m].getJointAction()[1] == predictedMessages[1])
//                                        {
//                                            previouslyCorrect[p] = true;
//                                            if(p == 0)
//                                            {
//                                                totalCorrect++;
//                                            }
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
                        } else if(p == 0)
                        {
                            previouslyCorrect[p] = false;
                        }
                }
//                int[] predicted = predictionArray[0];
                // Convert and check
                }
                total++;
            }
        }
        int[] result = new int[2];
        result[0] = totalCorrect;
        result[1] = total;
        return result;
    }

    private void printExtractedFiles(AutomataGroup altAutomata, AutomataGroup chickAutomata,
                                     AutomataGroup pdAutomata, AutomataGroup altChickAutomata,
                                     AutomataGroup altPDAutomata, AutomataGroup chickPDAutomata,
                                     AutomataGroup allAutomata, ActionAttitudeConverter altConverter,
                                     ActionAttitudeConverter chickenConverter, ActionAttitudeConverter prisonConverter,
                                     FeatList2DSAttVecClustList fl2dsavcl)
    {
        for(int g = 0; g < altGames.length; g++)
        {
            altAutomata.prepForNewGame();
            chickAutomata.prepForNewGame();
            allAutomata.prepForNewGame();
            printExtractedFile(altFiles[2 * g], altGames[g], gameFeatureMap.get(altGames[g]), altAutomata, chickAutomata, allAutomata, altConverter, fl2dsavcl, "alternator_", altTrainFeatureCollection.contains(gameFeatureMap.get(altGames[g])));
        }
        for(int g = 0; g < chickenGames.length; g++)
        {
            chickAutomata.prepForNewGame();
            altAutomata.prepForNewGame();
            allAutomata.prepForNewGame();
            printExtractedFile(chickenFiles[2 * g], chickenGames[g], gameFeatureMap.get(chickenGames[g]), chickAutomata, altAutomata, allAutomata, chickenConverter, fl2dsavcl, "chicken_", chickenTrainFeatureCollection.contains(gameFeatureMap.get(chickenGames[g])));
        }
        for(int g = 0; g < prisonGames.length; g++)
        {
            pdAutomata.prepForNewGame();
            altAutomata.prepForNewGame();
            allAutomata.prepForNewGame();
            printExtractedFile(prisonFiles[2 * g], prisonGames[g], gameFeatureMap.get(prisonGames[g]), pdAutomata, altAutomata, allAutomata, prisonConverter, fl2dsavcl, "prisoners_dilemma_", prisonTrainFeatureCollection.contains(gameFeatureMap.get(prisonGames[g])));
        }
    }

    private void printExtractedFile(File gameFile, Game game, List<Feature> fList, AutomataGroup matchingAutomata,
                                    AutomataGroup otherAutomata, AutomataGroup allAutomata, ActionAttitudeConverter converter,
                                    FeatList2DSAttVecClustList fl2dsavcl, String gameName, boolean training)
    {
//        ArrayList<Feature> fList = new ArrayList<>();
//        fList.addAll(featureList);
        int[][] clustIndexList = fl2dsavcl.getClusterIndexList(fList,dispAVClusters, saidAVClusters);
        boolean[] previouslyCorrect1 = new boolean[matchingAutomata.size() + 1];
        boolean[] previouslyCorrect2 = new boolean[otherAutomata.size() + 1];
        boolean[] previouslyCorrect3 = new boolean[allAutomata.size() + 1];
        for(int i = 0; i < previouslyCorrect1.length; i++)
        {
            previouslyCorrect1[i] = true;
        }
        for(int i = 0; i < previouslyCorrect2.length; i++)
        {
            previouslyCorrect2[i] = true;
        }
        for(int i = 0; i < previouslyCorrect3.length; i++)
        {
            previouslyCorrect3[i] = true;
        }
        int[][] predictedAct = new int[clustIndexList.length - memoryLength][3];
        int[][][] pArray = new int[clustIndexList.length - memoryLength][3][Math.max(Math.max(matchingAutomata.size(),otherAutomata.size()), allAutomata.size())];
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
            //===========INSERTED HERE=====================
            int[][] predictionArray1 = matchingAutomata.getPredictionArray(previouslyCorrect1, sequence);
            int[][] predictionArray2 = otherAutomata.getPredictionArray(previouslyCorrect2, sequence);
            int[][] predictionArray3 = allAutomata.getPredictionArray(previouslyCorrect3, sequence);
            previouslyCorrect1 = new boolean[predictionArray1.length];
            previouslyCorrect2 = new boolean[predictionArray2.length];
            previouslyCorrect3 = new boolean[predictionArray3.length];
            for(int p = 0; p < predictionArray1.length; p++)
            {
                int[] predicted = predictionArray1[p];
                if (predicted[0] >= 0 && predicted[1] >= 0) {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[predicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(i + memoryLength - 1);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    if (predictedActs[0] == game.getPlayer1().getAction(i + memoryLength)) {
                        // Determine if anything should be said or not
                        SpeechAct[] messages = game.getPlayer1().getMessage(i + memoryLength);
                        int[] predictedMessages = converter.attitudeVectorToActions((AttitudeVector) saidAVClusters[predicted[1]].getCentroid());
                        if (((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getGreedy() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getPlacate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getCooperate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getAbsurd() == 0) {
                            if (messages.length == 0) {
                                previouslyCorrect1[p] = true;
                            }
                        } else {
                            boolean alternatingMessage = !(predictedMessages[0] == predictedMessages[2]
                                    && predictedMessages[1] == predictedMessages[3]);
                            for (int m = 0; m < messages.length; m++) {
                                if (alternatingMessage && messages[m].size() == 2) {
                                    if ((messages[m].getJointAction()[0] == predictedMessages[0]
                                            && messages[m].getJointAction()[1] == predictedMessages[1]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[2]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[3])
                                            || (messages[m].getJointAction()[0] == predictedMessages[2]
                                            && messages[m].getJointAction()[1] == predictedMessages[3]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[0]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[1])) {
                                        previouslyCorrect1[p] = true;
                                        break;
                                    }
                                } else if (!alternatingMessage && messages[m].size() == 1) {
                                    if (messages[m].getJointAction()[0] == predictedMessages[0] && messages[m].getJointAction()[1] == predictedMessages[1]) {
                                        previouslyCorrect1[p] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for(int p = 0; p < predictionArray2.length; p++)
            {
                int[] predicted = predictionArray2[p];
                if (predicted[0] >= 0 && predicted[1] >= 0) {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[predicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(i + memoryLength - 1);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    if (predictedActs[0] == game.getPlayer1().getAction(i + memoryLength)) {
                        // Determine if anything should be said or not
                        SpeechAct[] messages = game.getPlayer1().getMessage(i + memoryLength);
                        int[] predictedMessages = converter.attitudeVectorToActions((AttitudeVector) saidAVClusters[predicted[1]].getCentroid());
                        if (((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getGreedy() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getPlacate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getCooperate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getAbsurd() == 0) {
                            if (messages.length == 0)
                            {
                                previouslyCorrect2[p] = true;
                            }
                        } else
                            {
                            boolean alternatingMessage = !(predictedMessages[0] == predictedMessages[2]
                                    && predictedMessages[1] == predictedMessages[3]);
                            for (int m = 0; m < messages.length; m++) {
                                if (alternatingMessage && messages[m].size() == 2) {
                                    if ((messages[m].getJointAction()[0] == predictedMessages[0]
                                            && messages[m].getJointAction()[1] == predictedMessages[1]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[2]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[3])
                                            || (messages[m].getJointAction()[0] == predictedMessages[2]
                                            && messages[m].getJointAction()[1] == predictedMessages[3]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[0]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[1])) {
                                        previouslyCorrect2[p] = true;
                                        break;
                                    }
                                } else if (!alternatingMessage && messages[m].size() == 1) {
                                    if (messages[m].getJointAction()[0] == predictedMessages[0] && messages[m].getJointAction()[1] == predictedMessages[1]) {
                                        previouslyCorrect2[p] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for(int p = 0; p < predictionArray3.length; p++)
            {
                int[] predicted = predictionArray3[p];
                if (predicted[0] >= 0 && predicted[1] >= 0) {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[predicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(i + memoryLength - 1);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    if (predictedActs[0] == game.getPlayer1().getAction(i + memoryLength)) {
                        // Determine if anything should be said or not
                        SpeechAct[] messages = game.getPlayer1().getMessage(i + memoryLength);
                        int[] predictedMessages = converter.attitudeVectorToActions((AttitudeVector) saidAVClusters[predicted[1]].getCentroid());
                        if (((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getGreedy() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getPlacate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getCooperate() == 0 &&
                                ((AttitudeVector) saidAVClusters[predicted[1]].getCentroid()).getAbsurd() == 0) {
                            if (messages.length == 0)
                            {
                                previouslyCorrect3[p] = true;
                            }
                        } else
                        {
                            boolean alternatingMessage = !(predictedMessages[0] == predictedMessages[2]
                                    && predictedMessages[1] == predictedMessages[3]);
                            for (int m = 0; m < messages.length; m++) {
                                if (alternatingMessage && messages[m].size() == 2) {
                                    if ((messages[m].getJointAction()[0] == predictedMessages[0]
                                            && messages[m].getJointAction()[1] == predictedMessages[1]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[2]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[3])
                                            || (messages[m].getJointAction()[0] == predictedMessages[2]
                                            && messages[m].getJointAction()[1] == predictedMessages[3]
                                            && messages[m].getSecondJointAction()[0] == predictedMessages[0]
                                            && messages[m].getSecondJointAction()[1] == predictedMessages[1])) {
                                        previouslyCorrect3[p] = true;
                                        break;
                                    }
                                } else if (!alternatingMessage && messages[m].size() == 1) {
                                    if (messages[m].getJointAction()[0] == predictedMessages[0] && messages[m].getJointAction()[1] == predictedMessages[1]) {
                                        previouslyCorrect3[p] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //=============END INSERT============
//            int[] predicted1 = matchingAutomata.getMostProbableActionAndMessage(previouslyCorrect1, sequence);
//            int[] predicted2 = otherAutomata.getMostProbableActionAndMessage(previouslyCorrect2, sequence);
//            int[] predicted3 = allAutomata.getMostProbableActionAndMessage(previouslyCorrect3, sequence);
            int[] prevActPair = game.getActionPair(i + memoryLength - 1);
            AttitudeVector predictedAV1 = (AttitudeVector) dispAVClusters[predictionArray1[0][0]].getCentroid();
            AttitudeVector predictedAV2 = (AttitudeVector) dispAVClusters[predictionArray2[0][0]].getCentroid();
            AttitudeVector predictedAV3 = (AttitudeVector) dispAVClusters[predictionArray3[0][0]].getCentroid();
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
//            for(int t = 0; t < Math.max(Math.max(matchingAutomata.size(),otherAutomata.size()), allAutomata.size()); t++)
//            {
//                AttitudeVector predictedArrayAV1 = (AttitudeVector) dispAVClusters[predictionArray1[t + 1][0]].getCentroid();
//                AttitudeVector predictedArrayAV2 = (AttitudeVector) dispAVClusters[predictionArray2[t + 1][0]].getCentroid();
//                AttitudeVector predictedArrayAV3 = (AttitudeVector) dispAVClusters[predictionArray3[t + 1][0]].getCentroid();
////                AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
//                AttitudeVector extrapolatedArrayAV1 = AttitudeVector.extrapolate(prevAttDisp, predictedArrayAV1);
//                AttitudeVector extrapolatedArrayAV2 = AttitudeVector.extrapolate(prevAttDisp, predictedArrayAV2);
//                AttitudeVector extrapolatedArrayAV3 = AttitudeVector.extrapolate(prevAttDisp, predictedArrayAV3);
//                int[] predictedArrayActs1 = converter.attitudeVectorToActions(extrapolatedAV1);
//                int[] predictedArrayActs2 = converter.attitudeVectorToActions(extrapolatedAV2);
//                int[] predictedArrayActs3 = converter.attitudeVectorToActions(extrapolatedAV3);
//                pArray[i][0][t] = predictedArrayActs1[0];
//                pArray[i][1][t] = predictedArrayActs2[0];
//                pArray[i][2][t] = predictedArrayActs3[0];
//            }
        }
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(dir);
        fileNameBuilder.append("\\Extracted File\\");
        fileNameBuilder.append(gameName);
        fileNameBuilder.append(gameFile.getPath().substring(gameFile.getPath().indexOf('_') + 1, gameFile.getPath().indexOf("_activity")));
        fileNameBuilder.append("_extract.csv");
        try(FileWriter fWriter = new FileWriter(fileNameBuilder.toString()))
        {
            fWriter.append("Round,P1 Act,P1 Message,P2 Act,P2 Message,P1 Payoff,P2 Payoff,P1 AV Displayed,P1 AV Said,P2 AV Displayed,P2 AV Said,Predicted Same,Predicted Different,Predicted All,Automaton Index Matching,Automaton Index Other,Automaton Index All");
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
                b.append(',');
                b.append(matchingAutomata.activeAutomatonIndex());
                b.append(',');
                b.append(otherAutomata.activeAutomatonIndex());
                b.append(',');
                b.append(allAutomata.activeAutomatonIndex());

//                if(round > memoryLength)
//                {
//                    b.append(',');
//                    for(int t = 0; t < pArray[round - memoryLength][0].length; t++)
//                    {
//                        b.append(pArray[round - memoryLength][0][t]);
//                        b.append(' ');
//                    }
//                    b.append(',');
//                    for(int t = 0; t < pArray[round - memoryLength][1].length; t++)
//                    {
//                        b.append(pArray[round - memoryLength][1][t]);
//                        b.append(' ');
//                    }
//                    b.append(',');
//                    for(int t = 0; t < pArray[round - memoryLength][2].length; t++)
//                    {
//                        b.append(pArray[round - memoryLength][2][t]);
//                        b.append(' ');
//                    }
//                }
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
                System.out.println(fileNameBuilder.toString());
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
