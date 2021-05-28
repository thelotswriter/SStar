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
import Game.PayoffMatrix;
import Game.TXTtoGame;
import Game.SpeechAct;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.*;

public class FinalTest
{

    private final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";
    private final String testDir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults\\Extra Results";

    private File[] altFiles;
    private File[] chickenFiles;
    private File[] prisonFiles;
    private File[] altTestFiles;
    private File[] chickenTestFiles;
    private File[] prisonTestFiles;
    private File[] endlessTestFiles;

    private Game[] altGames;
    private Game[] chickenGames;
    private Game[] prisonGames;
    private Game[] altTrainGames;
    private Game[] altTestGames;
    private Game[] chickenTrainGames;
    private Game[] chickenTestGames;
    private Game[] prisonTrainGames;
    private Game[] prisonTestGames;
    private Game[] endlessTestGames;

    private ActionAttitudeConverter altConverter;
    private ActionAttitudeConverter chickenConverter;
    private ActionAttitudeConverter prisonConverter;
    private ActionAttitudeConverter altTestConverter;
    private ActionAttitudeConverter chickenTestConverter;
    private ActionAttitudeConverter prisonTestConverter;
    private ActionAttitudeConverter endlessTestConverter;

    private List<List<Feature>> altTrainFeatureCollection;
    private List<List<Feature>> altTestFeatureCollection;
    private List<List<Feature>> chickenTrainFeatureCollection;
    private List<List<Feature>> chickenTestFeatureCollection;
    private List<List<Feature>> prisonTrainFeatureCollection;
    private List<List<Feature>> prisonTestFeatureCollection;
    private List<List<Feature>> endlessTestFeatureCollection;
    private ArrayList<List<Feature>> altFeatures;
    private ArrayList<List<Feature>> chickenFeatures;
    private ArrayList<List<Feature>> prisonFeatures;

    private DistantDataCluster[] dispAVClusters;
    private DistantDataCluster[] saidAVClusters;

    private Map<Game, List<Feature>> gameFeatureMap;
    private Map<List<Feature>, int[][]> featureListClusterListMap;

    private DSGeneralAutomaton altAutomaton;
    private DSGeneralAutomaton chickenAutomaton;
    private DSGeneralAutomaton prisonAutomaton;
    private DSGeneralAutomaton altChickenAutomaton;
    private DSGeneralAutomaton altPrisonAutomaton;
    private DSGeneralAutomaton chickenPrisonAutomaton;
    private DSGeneralAutomaton combinedAutomaton;

    private FeatList2DSAttVecClustList fl2dsavcl;

    private int nNeighbors = 1;
    private double pPower = 1.0;
    private double discount = 0.0;
    private int memoryLength;

    public FinalTest(int memLength)
    {
        memoryLength = memLength;
        initialize();
    }

    private void initialize()
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
        //Test Files
        File testFolder = new File(testDir);
        List<File> altTestFileList = new ArrayList<>();
        List<File> chickenTestFileList = new ArrayList<>();
        List<File> prisonTestFileList = new ArrayList<>();
        List<File> endlessTestFileList = new ArrayList<>();
        for(File file : testFolder.listFiles())
        {
            if(file.getPath().contains("Blocks"))
            {
                altTestFileList.add(file);
            } else if(file.getPath().contains("Chicken"))
            {
                chickenTestFileList.add(file);
            } else if(file.getPath().contains("Prisoners"))
            {
                prisonTestFileList.add(file);
            } else if(file.getPath().contains("Endless"))
            {
                endlessTestFileList.add(file);
            }
        }
        altTestFiles = new File[altTestFileList.size()];
        chickenTestFiles = new File[chickenTestFileList.size()];
        prisonTestFiles = new File[prisonTestFileList.size()];
        endlessTestFiles = new File[endlessTestFileList.size()];
        for(int f = 0; f < altTestFileList.size(); f++)
        {
            altTestFiles[f] = altTestFileList.get(f);
        }
        for(int f = 0; f < chickenTestFileList.size(); f++)
        {
            chickenTestFiles[f] = chickenTestFileList.get(f);
        }
        for(int f = 0; f < prisonTestFileList.size(); f++)
        {
            prisonTestFiles[f] = prisonTestFileList.get(f);
        }
        for(int f = 0; f < endlessTestFileList.size(); f++)
        {
            endlessTestFiles[f] = endlessTestFileList.get(f);
        }
        // Game setup - RESUME WORK HERE
        TXTtoGame txtToGame = new TXTtoGame();
        altTrainGames = new Game[altFiles.length];
        chickenTrainGames = new Game[chickenFiles.length];
        prisonTrainGames = new Game[prisonFiles.length];
        for(int g = 0; g < altTrainGames.length / 2; g++)
        {
            altTrainGames[g] = txtToGame.openFile(altFiles[2 * g].getPath(), altFiles[2 * g + 1].getPath());
            altTrainGames[g].setName("Alternator");
            altTrainGames[g + altTrainGames.length / 2] = altTrainGames[g].transpose();
        }
        for(int g = 0; g < chickenTrainGames.length / 2; g++)
        {
            chickenTrainGames[g] = txtToGame.openFile(chickenFiles[g * 2].getPath(), chickenFiles[(g * 2) + 1].getPath());
            chickenTrainGames[g].setName("Chicken");
            chickenTrainGames[g + chickenTrainGames.length / 2] = chickenTrainGames[g].transpose();
        }
        for(int g = 0; g < prisonTrainGames.length / 2; g++)
        {
            prisonTrainGames[g] = txtToGame.openFile(prisonFiles[g * 2].getPath(), prisonFiles[(g * 2) + 1].getPath());
            prisonTrainGames[g].setName("Prisoner");
            prisonTrainGames[g + prisonTrainGames.length / 2] = prisonTrainGames[g].transpose();
        }
        altTestGames = new Game[altTestFiles.length / 2];
        chickenTestGames = new Game[chickenTestFiles.length / 2];
        prisonTestGames = new Game[prisonTestFiles.length / 2];
        endlessTestGames = new Game[endlessTestFiles.length / 2];
        double[][][] altMatrix = new double[3][3][2];
        altMatrix[0][0][0] = 0;
        altMatrix[0][0][1] = 0;
        altMatrix[0][1][0] = 35;
        altMatrix[0][1][1] = 70;
        altMatrix[0][2][0] = 100;
        altMatrix[0][2][1] = 40;
        altMatrix[1][0][0] = 70;
        altMatrix[1][0][1] = 35;
        altMatrix[1][1][0] = 10;
        altMatrix[1][1][1] = 10;
        altMatrix[1][2][0] = 45;
        altMatrix[1][2][1] = 30;
        altMatrix[2][0][0] = 40;
        altMatrix[2][0][1] = 100;
        altMatrix[2][1][0] = 30;
        altMatrix[2][1][1] = 45;
        altMatrix[2][2][0] = 40;
        altMatrix[2][2][1] = 40;
        PayoffMatrix altPMatrix = new PayoffMatrix(altMatrix);
        for(int g = 0; g < altTestGames.length; g++)
        {
            altTestGames[g] = txtToGame.openFile(altTestFiles[g * 2].getPath(), altTestFiles[(g * 2) + 1].getPath());
            altTestGames[g].setName("Alternator");
            altTestGames[g].setPayoffMatrix(altPMatrix);
        }
        double[][][] chickenMatrix = new double[2][2][2];
        chickenMatrix[0][0][0] = 0;
        chickenMatrix[0][0][1] = 0;
        chickenMatrix[0][1][0] = 100;
        chickenMatrix[0][1][1] = 33;
        chickenMatrix[1][0][0] = 33;
        chickenMatrix[1][0][1] = 100;
        chickenMatrix[1][1][0] = 84;
        chickenMatrix[1][1][1] = 84;
        PayoffMatrix chickenPMatrix = new PayoffMatrix(chickenMatrix);
        for(int g = 0; g < chickenTestGames.length; g++)
        {
            chickenTestGames[g] = txtToGame.openFile(chickenTestFiles[g * 2].getPath(), chickenTestFiles[(g * 2) + 1].getPath());
            chickenTestGames[g].setName("Chicken");
            chickenTestGames[g].setPayoffMatrix(chickenPMatrix);
        }
        double[][][] prisonMatrix = new double[2][2][2];
        prisonMatrix[0][0][0] = 60;
        prisonMatrix[0][0][1] = 60;
        prisonMatrix[0][1][0] = 0;
        prisonMatrix[0][1][1] = 100;
        prisonMatrix[1][0][0] = 100;
        prisonMatrix[1][0][1] = 0;
        prisonMatrix[1][1][0] = 20;
        prisonMatrix[1][1][1] = 20;
        PayoffMatrix prisonPMatrix = new PayoffMatrix(prisonMatrix);
        for(int g = 0; g < prisonTestGames.length; g++)
        {
            prisonTestGames[g] = txtToGame.openFile(prisonTestFiles[g * 2].getPath(), prisonTestFiles[(g * 2) + 1].getPath());
            prisonTestGames[g].setName("Prison");
            prisonTestGames[g].setPayoffMatrix(prisonPMatrix);
        }
        double[][][] endlessMatrix = new double[2][2][2];
        endlessMatrix[0][0][0] = 33;
        endlessMatrix[0][0][1] = 67;
        endlessMatrix[0][1][0] = 67;
        endlessMatrix[0][1][1] = 100;
        endlessMatrix[1][0][0] = 0;
        endlessMatrix[1][0][1] = 33;
        endlessMatrix[1][1][0] = 100;
        endlessMatrix[1][1][1] = 0;
        PayoffMatrix endlessPMatrix = new PayoffMatrix(endlessMatrix);
        for(int g = 0; g < endlessTestGames.length; g++)
        {
            endlessTestGames[g] = txtToGame.openFile(endlessTestFiles[g * 2].getPath(), endlessTestFiles[(g * 2) + 1].getPath());
            endlessTestGames[g].setName("Endless");
            endlessTestGames[g].setPayoffMatrix(endlessPMatrix);
        }
        //Converters
        altConverter = new ActionAttitudeConverter(altTrainGames[0].getPayoffMatrix(), pPower);
        chickenConverter = new ActionAttitudeConverter(chickenTrainGames[0].getPayoffMatrix(), pPower);
        prisonConverter = new ActionAttitudeConverter(prisonTrainGames[0].getPayoffMatrix(), pPower);
        altTestConverter = new ActionAttitudeConverter(altTestGames[0].getPayoffMatrix(), pPower);
        chickenTestConverter = new ActionAttitudeConverter(chickenTestGames[0].getPayoffMatrix(), pPower);
        prisonTestConverter = new ActionAttitudeConverter(prisonTestGames[0].getPayoffMatrix(), pPower);
        endlessTestConverter = new ActionAttitudeConverter(endlessTestGames[0].getPayoffMatrix(), pPower);

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
        endlessTestFeatureCollection = new ArrayList<>();
        for(int g = 0; g < endlessTestGames.length; g++)
        {
            Game game = endlessTestGames[g];
            GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
            List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            gameFeatureMap.put(game, fCollection);
            endlessTestFeatureCollection.add(fCollection);
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

    public double[][] run()
    {
        //=========Convert feature lists to cluster lists
        fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
        List<int[][]> altTrainClustLists = new ArrayList<>();
        List<int[][]> altTestClustLists = new ArrayList<>();
        List<int[][]> chickenTrainClustLists = new ArrayList<>();
        List<int[][]> chickenTestClustLists = new ArrayList<>();
        List<int[][]> prisonTrainClustLists = new ArrayList<>();
        List<int[][]> prisonTestClustLists = new ArrayList<>();
        List<int[][]> endlessTestClustLists = new ArrayList<>();

        List<int[][]> altChickenTrainClustLists = new ArrayList<>();
        List<int[][]> altChickenTestClustLists = new ArrayList<>();
        List<int[][]> altPrisonTrainClustLists = new ArrayList<>();
        List<int[][]> altPrisonTestClustLists = new ArrayList<>();
        List<int[][]> altEndlessTestClustLists = new ArrayList<>();
        List<int[][]> chickenPrisonTrainClustLists = new ArrayList<>();
        List<int[][]> chickenPrisonTestClustLists = new ArrayList<>();
        List<int[][]> chickenEndlessTestClustLists = new ArrayList<>();
        List<int[][]> prisonEndlessTestClustLists = new ArrayList<>();
        List<int[][]> altChickenPrisonTestClustLists = new ArrayList<>();
        List<int[][]> altChickenEndlessTestClustLists = new ArrayList<>();
        List<int[][]> altPrisonEndlessTestClustLists = new ArrayList<>();
        List<int[][]> chickenPrisonEndlessTestClustLists = new ArrayList<>();
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
        altAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        chickenAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        prisonAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        altChickenAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        altPrisonAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        chickenPrisonAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        combinedAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
        int minSecond = 2;
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
            altChickenPrisonTestClustLists.add(clustIndexList);
            altChickenEndlessTestClustLists.add(clustIndexList);
            altPrisonEndlessTestClustLists.add(clustIndexList);
            chickenPrisonEndlessTestClustLists.add(clustIndexList);
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
            chickenEndlessTestClustLists.add(clustIndexList);
            altChickenPrisonTestClustLists.add(clustIndexList);
            altChickenEndlessTestClustLists.add(clustIndexList);
            chickenPrisonEndlessTestClustLists.add(clustIndexList);
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
            prisonEndlessTestClustLists.add(clustIndexList);
            altChickenPrisonTestClustLists.add(clustIndexList);
            altPrisonEndlessTestClustLists.add(clustIndexList);
            chickenPrisonEndlessTestClustLists.add(clustIndexList);
            combinedTestClustLists.add(clustIndexList);
        }
        for(List<Feature> featureList : endlessTestFeatureCollection)
        {
            int[][] clustIndexList = fl2dsavcl.getClusterIndexList(featureList,dispAVClusters, saidAVClusters);
            featureListClusterListMap.put(featureList, clustIndexList);
            endlessTestClustLists.add(clustIndexList);
            altEndlessTestClustLists.add(clustIndexList);
            chickenEndlessTestClustLists.add(clustIndexList);
            prisonEndlessTestClustLists.add(clustIndexList);
            altChickenEndlessTestClustLists.add(clustIndexList);
            altPrisonEndlessTestClustLists.add(clustIndexList);
            chickenPrisonEndlessTestClustLists.add(clustIndexList);
            combinedTestClustLists.add(clustIndexList);
        }
        // Test predictions
//        List<List<Feature>> altChickenTestFeatureCollection = new ArrayList<>();
//        List<List<Feature>> altPrisonTestFeatureCollection = new ArrayList<>();
//        List<List<Feature>> chickenPrisonTestFeatureCollection = new ArrayList<>();
//        List<List<Feature>> combinedTestFeatureCollection = new ArrayList<>();
//        List<List<Feature>> altChickenTrainFeatureCollection = new ArrayList<>();
//        List<List<Feature>> altPrisonTrainFeatureCollection = new ArrayList<>();
//        List<List<Feature>> chickenPrisonTrainFeatureCollection = new ArrayList<>();
//        List<List<Feature>> combinedTrainFeatureCollection = new ArrayList<>();
//        altChickenTestFeatureCollection.addAll(altTestFeatureCollection);
//        altChickenTestFeatureCollection.addAll(chickenTestFeatureCollection);
//        altPrisonTestFeatureCollection.addAll(altTestFeatureCollection);
//        altPrisonTestFeatureCollection.addAll(prisonTestFeatureCollection);
//        chickenPrisonTestFeatureCollection.addAll(chickenTestFeatureCollection);
//        chickenPrisonTestFeatureCollection.addAll(prisonTestFeatureCollection);
//        combinedTestFeatureCollection.addAll(altTestFeatureCollection);
//        combinedTestFeatureCollection.addAll(chickenTestFeatureCollection);
//        combinedTestFeatureCollection.addAll(prisonTestFeatureCollection);
//        altChickenTrainFeatureCollection.addAll(altTrainFeatureCollection);
//        altChickenTrainFeatureCollection.addAll(chickenTrainFeatureCollection);
//        altPrisonTrainFeatureCollection.addAll(altTrainFeatureCollection);
//        altPrisonTrainFeatureCollection.addAll(prisonTrainFeatureCollection);
//        chickenPrisonTrainFeatureCollection.addAll(chickenTrainFeatureCollection);
//        chickenPrisonTrainFeatureCollection.addAll(prisonTrainFeatureCollection);
//        combinedTrainFeatureCollection.addAll(altTrainFeatureCollection);
//        combinedTrainFeatureCollection.addAll(chickenTrainFeatureCollection);
//        combinedTestFeatureCollection.addAll(prisonTrainFeatureCollection);

//        double[][] resultMatrix = new double[7][14];
//        resultMatrix[0][0] = calculatePrecitionAccuracy(altAutomaton, altTestClustLists);
//        resultMatrix[0][1] = calculatePrecitionAccuracy(altAutomaton, chickenTestClustLists);
//        resultMatrix[0][2] = calculatePrecitionAccuracy(altAutomaton, prisonTestClustLists);
//        resultMatrix[0][3] = calculatePrecitionAccuracy(altAutomaton, altChickenTestClustLists);
//        resultMatrix[0][4] = calculatePrecitionAccuracy(altAutomaton, altPrisonTestClustLists);
//        resultMatrix[0][5] = calculatePrecitionAccuracy(altAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[0][6] = calculatePrecitionAccuracy(altAutomaton, combinedTestClustLists);
//        resultMatrix[0][7] = calculatePrecitionAccuracy(altAutomaton, altTrainClustLists);
//        resultMatrix[0][8] = calculatePrecitionAccuracy(altAutomaton, chickenTrainClustLists);
//        resultMatrix[0][9] = calculatePrecitionAccuracy(altAutomaton, prisonTrainClustLists);
//        resultMatrix[0][10] = calculatePrecitionAccuracy(altAutomaton, altChickenTrainClustLists);
//        resultMatrix[0][11] = calculatePrecitionAccuracy(altAutomaton, altPrisonTrainClustLists);
//        resultMatrix[0][12] = calculatePrecitionAccuracy(altAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[0][13] = calculatePrecitionAccuracy(altAutomaton, combinedTrainClustLists);
//        resultMatrix[1][0] = calculatePrecitionAccuracy(chickenAutomaton, altTestClustLists);
//        resultMatrix[1][1] = calculatePrecitionAccuracy(chickenAutomaton, chickenTestClustLists);
//        resultMatrix[1][2] = calculatePrecitionAccuracy(chickenAutomaton, prisonTestClustLists);
//        resultMatrix[1][3] = calculatePrecitionAccuracy(chickenAutomaton, altChickenTestClustLists);
//        resultMatrix[1][4] = calculatePrecitionAccuracy(chickenAutomaton, altPrisonTestClustLists);
//        resultMatrix[1][5] = calculatePrecitionAccuracy(chickenAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[1][6] = calculatePrecitionAccuracy(chickenAutomaton, combinedTestClustLists);
//        resultMatrix[1][7] = calculatePrecitionAccuracy(chickenAutomaton, altTrainClustLists);
//        resultMatrix[1][8] = calculatePrecitionAccuracy(chickenAutomaton, chickenTrainClustLists);
//        resultMatrix[1][9] = calculatePrecitionAccuracy(chickenAutomaton, prisonTrainClustLists);
//        resultMatrix[1][10] = calculatePrecitionAccuracy(chickenAutomaton, altChickenTrainClustLists);
//        resultMatrix[1][11] = calculatePrecitionAccuracy(chickenAutomaton, altPrisonTrainClustLists);
//        resultMatrix[1][12] = calculatePrecitionAccuracy(chickenAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[1][13] = calculatePrecitionAccuracy(chickenAutomaton, combinedTrainClustLists);
//        resultMatrix[2][0] = calculatePrecitionAccuracy(prisonAutomaton, altTestClustLists);
//        resultMatrix[2][1] = calculatePrecitionAccuracy(prisonAutomaton, chickenTestClustLists);
//        resultMatrix[2][2] = calculatePrecitionAccuracy(prisonAutomaton, prisonTestClustLists);
//        resultMatrix[2][3] = calculatePrecitionAccuracy(prisonAutomaton, altChickenTestClustLists);
//        resultMatrix[2][4] = calculatePrecitionAccuracy(prisonAutomaton, altPrisonTestClustLists);
//        resultMatrix[2][5] = calculatePrecitionAccuracy(prisonAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[2][6] = calculatePrecitionAccuracy(prisonAutomaton, combinedTestClustLists);
//        resultMatrix[2][7] = calculatePrecitionAccuracy(prisonAutomaton, altTrainClustLists);
//        resultMatrix[2][8] = calculatePrecitionAccuracy(prisonAutomaton, chickenTrainClustLists);
//        resultMatrix[2][9] = calculatePrecitionAccuracy(prisonAutomaton, prisonTrainClustLists);
//        resultMatrix[2][10] = calculatePrecitionAccuracy(prisonAutomaton, altChickenTrainClustLists);
//        resultMatrix[2][11] = calculatePrecitionAccuracy(prisonAutomaton, altPrisonTrainClustLists);
//        resultMatrix[2][12] = calculatePrecitionAccuracy(prisonAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[2][13] = calculatePrecitionAccuracy(prisonAutomaton, combinedTrainClustLists);
//        resultMatrix[3][0] = calculatePrecitionAccuracy(altChickenAutomaton, altTestClustLists);
//        resultMatrix[3][1] = calculatePrecitionAccuracy(altChickenAutomaton, chickenTestClustLists);
//        resultMatrix[3][2] = calculatePrecitionAccuracy(altChickenAutomaton, prisonTestClustLists);
//        resultMatrix[3][3] = calculatePrecitionAccuracy(altChickenAutomaton, altChickenTestClustLists);
//        resultMatrix[3][4] = calculatePrecitionAccuracy(altChickenAutomaton, altPrisonTestClustLists);
//        resultMatrix[3][5] = calculatePrecitionAccuracy(altChickenAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[3][6] = calculatePrecitionAccuracy(altChickenAutomaton, combinedTestClustLists);
//        resultMatrix[3][7] = calculatePrecitionAccuracy(altChickenAutomaton, altTrainClustLists);
//        resultMatrix[3][8] = calculatePrecitionAccuracy(altChickenAutomaton, chickenTrainClustLists);
//        resultMatrix[3][9] = calculatePrecitionAccuracy(altChickenAutomaton, prisonTrainClustLists);
//        resultMatrix[3][10] = calculatePrecitionAccuracy(altChickenAutomaton, altChickenTrainClustLists);
//        resultMatrix[3][11] = calculatePrecitionAccuracy(altChickenAutomaton, altPrisonTrainClustLists);
//        resultMatrix[3][12] = calculatePrecitionAccuracy(altChickenAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[3][13] = calculatePrecitionAccuracy(altChickenAutomaton, combinedTrainClustLists);
//        resultMatrix[4][0] = calculatePrecitionAccuracy(altPrisonAutomaton, altTestClustLists);
//        resultMatrix[4][1] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenTestClustLists);
//        resultMatrix[4][2] = calculatePrecitionAccuracy(altPrisonAutomaton, prisonTestClustLists);
//        resultMatrix[4][3] = calculatePrecitionAccuracy(altPrisonAutomaton, altChickenTestClustLists);
//        resultMatrix[4][4] = calculatePrecitionAccuracy(altPrisonAutomaton, altPrisonTestClustLists);
//        resultMatrix[4][5] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[4][6] = calculatePrecitionAccuracy(altPrisonAutomaton, combinedTestClustLists);
//        resultMatrix[4][7] = calculatePrecitionAccuracy(altPrisonAutomaton, altTrainClustLists);
//        resultMatrix[4][8] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenTrainClustLists);
//        resultMatrix[4][9] = calculatePrecitionAccuracy(altPrisonAutomaton, prisonTrainClustLists);
//        resultMatrix[4][10] = calculatePrecitionAccuracy(altPrisonAutomaton, altChickenTrainClustLists);
//        resultMatrix[4][11] = calculatePrecitionAccuracy(altPrisonAutomaton, altPrisonTrainClustLists);
//        resultMatrix[4][12] = calculatePrecitionAccuracy(altPrisonAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[4][13] = calculatePrecitionAccuracy(altPrisonAutomaton, combinedTrainClustLists);
//        resultMatrix[5][0] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altTestClustLists);
//        resultMatrix[5][1] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenTestClustLists);
//        resultMatrix[5][2] = calculatePrecitionAccuracy(chickenPrisonAutomaton, prisonTestClustLists);
//        resultMatrix[5][3] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altChickenTestClustLists);
//        resultMatrix[5][4] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altPrisonTestClustLists);
//        resultMatrix[5][5] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[5][6] = calculatePrecitionAccuracy(chickenPrisonAutomaton, combinedTestClustLists);
//        resultMatrix[5][7] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altTrainClustLists);
//        resultMatrix[5][8] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenTrainClustLists);
//        resultMatrix[5][9] = calculatePrecitionAccuracy(chickenPrisonAutomaton, prisonTrainClustLists);
//        resultMatrix[5][10] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altChickenTrainClustLists);
//        resultMatrix[5][11] = calculatePrecitionAccuracy(chickenPrisonAutomaton, altPrisonTrainClustLists);
//        resultMatrix[5][12] = calculatePrecitionAccuracy(chickenPrisonAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[5][13] = calculatePrecitionAccuracy(chickenPrisonAutomaton, combinedTrainClustLists);
//        resultMatrix[6][0] = calculatePrecitionAccuracy(combinedAutomaton, altTestClustLists);
//        resultMatrix[6][1] = calculatePrecitionAccuracy(combinedAutomaton, chickenTestClustLists);
//        resultMatrix[6][2] = calculatePrecitionAccuracy(combinedAutomaton, prisonTestClustLists);
//        resultMatrix[6][3] = calculatePrecitionAccuracy(combinedAutomaton, altChickenTestClustLists);
//        resultMatrix[6][4] = calculatePrecitionAccuracy(combinedAutomaton, altPrisonTestClustLists);
//        resultMatrix[6][5] = calculatePrecitionAccuracy(combinedAutomaton, chickenPrisonTestClustLists);
//        resultMatrix[6][6] = calculatePrecitionAccuracy(combinedAutomaton, combinedTestClustLists);
//        resultMatrix[6][7] = calculatePrecitionAccuracy(combinedAutomaton, altTrainClustLists);
//        resultMatrix[6][8] = calculatePrecitionAccuracy(combinedAutomaton, chickenTrainClustLists);
//        resultMatrix[6][9] = calculatePrecitionAccuracy(combinedAutomaton, prisonTrainClustLists);
//        resultMatrix[6][10] = calculatePrecitionAccuracy(combinedAutomaton, altChickenTrainClustLists);
//        resultMatrix[6][11] = calculatePrecitionAccuracy(combinedAutomaton, altPrisonTrainClustLists);
//        resultMatrix[6][12] = calculatePrecitionAccuracy(combinedAutomaton, chickenPrisonTrainClustLists);
//        resultMatrix[6][13] = calculatePrecitionAccuracy(combinedAutomaton, combinedTrainClustLists);

        int[] altAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] altAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] altAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] altAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] altAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] altAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] chickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] chickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] chickenAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] chickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] prisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] prisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] prisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] prisonAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] prisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] prisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] prisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altChickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] altChickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] altChickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] altChickenAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] altChickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] altChickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altChickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] altPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] altPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] altPrisonAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] altPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] altPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] chickenPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] chickenPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] chickenPrisonAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] chickenPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] combinedAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, altTestClustLists, altTestConverter, altTestGames);
        int[] combinedAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] combinedAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] combinedAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] combinedAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, altTrainClustLists, altConverter, altTrainGames);
        int[] combinedAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] combinedAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomaton, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        double[][] predictedResultMatrix = new double[7][22];
        predictedResultMatrix[0][0] = ((double) altAutAltTestPredAccuracy[0]) / ((double) altAutAltTestPredAccuracy[1]);
        predictedResultMatrix[0][1] = ((double) altAutChickenTestPredAccuracy[0]) / ((double) altAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[0][2] = ((double) altAutPrisonTestPredAccuracy[0]) / ((double) altAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[0][3] = ((double) altAutEndlessTestPredAccuracy[0]) / ((double) altAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[0][4] = ((double) (altAutAltTestPredAccuracy[0] + altAutChickenTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[0][5] = ((double) (altAutAltTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[0][6] = ((double) (altAutAltTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[0][7] = ((double) (altAutChickenTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0])) / ((double) (altAutChickenTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[0][8] = ((double) (altAutChickenTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutChickenTestPredAccuracy[1] + altAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[0][9] = ((double) (altAutPrisonTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutPrisonTestPredAccuracy[1] + altAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[0][10] = ((double) (altAutAltTestPredAccuracy[0] + altAutChickenTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutChickenTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[0][11] = ((double) (altAutAltTestPredAccuracy[0] + altAutChickenTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutChickenTestPredAccuracy[1] + altAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[0][12] = ((double) (altAutAltTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1] + altAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[0][13] = ((double) (altAutChickenTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutChickenTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1] + altAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[0][14] = ((double) (altAutAltTestPredAccuracy[0] + altAutChickenTestPredAccuracy[0] + altAutPrisonTestPredAccuracy[0] + altAutEndlessTestPredAccuracy[0])) / ((double) (altAutAltTestPredAccuracy[1] + altAutChickenTestPredAccuracy[1] + altAutPrisonTestPredAccuracy[1]) + altAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[0][15] = ((double) altAutAltTrainPredAccuracy[0]) / ((double) altAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[0][16] = ((double) altAutChickenTrainPredAccuracy[0]) / ((double) altAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[0][17] = ((double) altAutPrisonTrainPredAccuracy[0]) / ((double) altAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[0][18] = ((double) (altAutAltTrainPredAccuracy[0] + altAutChickenTrainPredAccuracy[0])) / ((double) (altAutAltTrainPredAccuracy[1] + altAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[0][19] = ((double) (altAutAltTrainPredAccuracy[0] + altAutPrisonTrainPredAccuracy[0])) / ((double) (altAutAltTrainPredAccuracy[1] + altAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[0][20] = ((double) (altAutChickenTrainPredAccuracy[0] + altAutPrisonTrainPredAccuracy[0])) / ((double) (altAutChickenTrainPredAccuracy[1] + altAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[0][21] = ((double) (altAutAltTrainPredAccuracy[0] + altAutChickenTrainPredAccuracy[0] + altAutPrisonTrainPredAccuracy[0])) / ((double) (altAutAltTrainPredAccuracy[1] + altAutChickenTrainPredAccuracy[1] + altAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[1][0] = ((double) chickenAutAltTestPredAccuracy[0]) / ((double) chickenAutAltTestPredAccuracy[1]);
        predictedResultMatrix[1][1] = ((double) chickenAutChickenTestPredAccuracy[0]) / ((double) chickenAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[1][2] = ((double) chickenAutPrisonTestPredAccuracy[0]) / ((double) chickenAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[1][3] = ((double) chickenAutEndlessTestPredAccuracy[0]) / ((double) chickenAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[1][4] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutChickenTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[1][5] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[1][6] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][7] = ((double) (chickenAutChickenTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0])) / ((double) (chickenAutChickenTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[1][8] = ((double) (chickenAutChickenTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutChickenTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][9] = ((double) (chickenAutPrisonTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutPrisonTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][10] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutChickenTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutChickenTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[1][11] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutChickenTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutChickenTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][12] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][13] = ((double) (chickenAutChickenTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutChickenTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][14] = ((double) (chickenAutAltTestPredAccuracy[0] + chickenAutChickenTestPredAccuracy[0] + chickenAutPrisonTestPredAccuracy[0] + chickenAutEndlessTestPredAccuracy[0])) / ((double) (chickenAutAltTestPredAccuracy[1] + chickenAutChickenTestPredAccuracy[1] + chickenAutPrisonTestPredAccuracy[1] + chickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[1][15] = ((double) chickenAutAltTrainPredAccuracy[0]) / ((double) chickenAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[1][16] = ((double) chickenAutChickenTrainPredAccuracy[0]) / ((double) chickenAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[1][17] = ((double) chickenAutPrisonTrainPredAccuracy[0]) / ((double) chickenAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[1][18] = ((double) (chickenAutAltTrainPredAccuracy[0] + chickenAutChickenTrainPredAccuracy[0])) / ((double) (chickenAutAltTrainPredAccuracy[1] + chickenAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[1][19] = ((double) (chickenAutAltTrainPredAccuracy[0] + chickenAutPrisonTrainPredAccuracy[0])) / ((double) (chickenAutAltTrainPredAccuracy[1] + chickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[1][20] = ((double) (chickenAutChickenTrainPredAccuracy[0] + chickenAutPrisonTrainPredAccuracy[0])) / ((double) (chickenAutChickenTrainPredAccuracy[1] + chickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[1][21] = ((double) (chickenAutAltTrainPredAccuracy[0] + chickenAutChickenTrainPredAccuracy[0] + chickenAutPrisonTrainPredAccuracy[0])) / ((double) (chickenAutAltTrainPredAccuracy[1] + chickenAutChickenTrainPredAccuracy[1] + chickenAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[2][0] = ((double) prisonAutAltTestPredAccuracy[0]) / ((double) prisonAutAltTestPredAccuracy[1]);
        predictedResultMatrix[2][1] = ((double) prisonAutChickenTestPredAccuracy[0]) / ((double) prisonAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[2][2] = ((double) prisonAutPrisonTestPredAccuracy[0]) / ((double) prisonAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[2][3] = ((double) prisonAutEndlessTestPredAccuracy[0]) / ((double) prisonAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[2][4] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutChickenTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[2][5] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[2][6] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][7] = ((double) (prisonAutChickenTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0])) / ((double) (prisonAutChickenTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[2][8] = ((double) (prisonAutChickenTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutChickenTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][9] = ((double) (prisonAutPrisonTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutPrisonTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][10] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutChickenTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutChickenTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[2][11] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutChickenTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutChickenTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][12] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][13] = ((double) (prisonAutChickenTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutChickenTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][14] = ((double) (prisonAutAltTestPredAccuracy[0] + prisonAutChickenTestPredAccuracy[0] + prisonAutPrisonTestPredAccuracy[0] + prisonAutEndlessTestPredAccuracy[0])) / ((double) (prisonAutAltTestPredAccuracy[1] + prisonAutChickenTestPredAccuracy[1] + prisonAutPrisonTestPredAccuracy[1] + prisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[2][15] = ((double) prisonAutAltTrainPredAccuracy[0]) / ((double) prisonAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[2][16] = ((double) prisonAutChickenTrainPredAccuracy[0]) / ((double) prisonAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[2][17] = ((double) prisonAutPrisonTrainPredAccuracy[0]) / ((double) prisonAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[2][18] = ((double) (prisonAutAltTrainPredAccuracy[0] + prisonAutChickenTrainPredAccuracy[0])) / ((double) (prisonAutAltTrainPredAccuracy[1] + prisonAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[2][19] = ((double) (prisonAutAltTrainPredAccuracy[0] + prisonAutPrisonTrainPredAccuracy[0])) / ((double) (prisonAutAltTrainPredAccuracy[1] + prisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[2][20] = ((double) (prisonAutChickenTrainPredAccuracy[0] + prisonAutPrisonTrainPredAccuracy[0])) / ((double) (prisonAutChickenTrainPredAccuracy[1] + prisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[2][21] = ((double) (prisonAutAltTrainPredAccuracy[0] + prisonAutChickenTrainPredAccuracy[0] + prisonAutPrisonTrainPredAccuracy[0])) / ((double) (prisonAutAltTrainPredAccuracy[1] + prisonAutChickenTrainPredAccuracy[1] + prisonAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[3][0] = ((double) altChickenAutAltTestPredAccuracy[0]) / ((double) altChickenAutAltTestPredAccuracy[1]);
        predictedResultMatrix[3][1] = ((double) altChickenAutChickenTestPredAccuracy[0]) / ((double) altChickenAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[3][2] = ((double) altChickenAutPrisonTestPredAccuracy[0]) / ((double) altChickenAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[3][3] = ((double) altChickenAutEndlessTestPredAccuracy[0]) / ((double) altChickenAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[3][4] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutChickenTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[3][5] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[3][6] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][7] = ((double) (altChickenAutChickenTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0])) / ((double) (altChickenAutChickenTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[3][8] = ((double) (altChickenAutChickenTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutChickenTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][9] = ((double) (altChickenAutPrisonTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutPrisonTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][10] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutChickenTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutChickenTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[3][11] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutChickenTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutChickenTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][12] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][13] = ((double) (altChickenAutChickenTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutChickenTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][14] = ((double) (altChickenAutAltTestPredAccuracy[0] + altChickenAutChickenTestPredAccuracy[0] + altChickenAutPrisonTestPredAccuracy[0] + altChickenAutEndlessTestPredAccuracy[0])) / ((double) (altChickenAutAltTestPredAccuracy[1] + altChickenAutChickenTestPredAccuracy[1] + altChickenAutPrisonTestPredAccuracy[1] + altChickenAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[3][15] = ((double) altChickenAutAltTrainPredAccuracy[0]) / ((double) altChickenAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[3][16] = ((double) altChickenAutChickenTrainPredAccuracy[0]) / ((double) altChickenAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[3][17] = ((double) altChickenAutPrisonTrainPredAccuracy[0]) / ((double) altChickenAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[3][18] = ((double) (altChickenAutAltTrainPredAccuracy[0] + altChickenAutChickenTrainPredAccuracy[0])) / ((double) (altChickenAutAltTrainPredAccuracy[1] + altChickenAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[3][19] = ((double) (altChickenAutAltTrainPredAccuracy[0] + altChickenAutPrisonTrainPredAccuracy[0])) / ((double) (altChickenAutAltTrainPredAccuracy[1] + altChickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[3][20] = ((double) (altChickenAutChickenTrainPredAccuracy[0] + altChickenAutPrisonTrainPredAccuracy[0])) / ((double) (altChickenAutChickenTrainPredAccuracy[1] + altChickenAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[3][21] = ((double) (altChickenAutAltTrainPredAccuracy[0] + altChickenAutChickenTrainPredAccuracy[0] + altChickenAutPrisonTrainPredAccuracy[0])) / ((double) (altChickenAutAltTrainPredAccuracy[1] + altChickenAutChickenTrainPredAccuracy[1] + altChickenAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[4][0] = ((double) altPrisonAutAltTestPredAccuracy[0]) / ((double) altPrisonAutAltTestPredAccuracy[1]);
        predictedResultMatrix[4][1] = ((double) altPrisonAutChickenTestPredAccuracy[0]) / ((double) altPrisonAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[4][2] = ((double) altPrisonAutPrisonTestPredAccuracy[0]) / ((double) altPrisonAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[4][3] = ((double) altPrisonAutEndlessTestPredAccuracy[0]) / ((double) altPrisonAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[4][4] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutChickenTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[4][5] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[4][6] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][7] = ((double) (altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0])) / ((double) (altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[4][8] = ((double) (altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][9] = ((double) (altPrisonAutPrisonTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutPrisonTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][10] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[4][11] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][12] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][13] = ((double) (altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][14] = ((double) (altPrisonAutAltTestPredAccuracy[0] + altPrisonAutChickenTestPredAccuracy[0] + altPrisonAutPrisonTestPredAccuracy[0] + altPrisonAutEndlessTestPredAccuracy[0])) / ((double) (altPrisonAutAltTestPredAccuracy[1] + altPrisonAutChickenTestPredAccuracy[1] + altPrisonAutPrisonTestPredAccuracy[1] + altPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[4][15] = ((double) altPrisonAutAltTrainPredAccuracy[0]) / ((double) altPrisonAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[4][16] = ((double) altPrisonAutChickenTrainPredAccuracy[0]) / ((double) altPrisonAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[4][17] = ((double) altPrisonAutPrisonTrainPredAccuracy[0]) / ((double) altPrisonAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[4][18] = ((double) (altPrisonAutAltTrainPredAccuracy[0] + altPrisonAutChickenTrainPredAccuracy[0])) / ((double) (altPrisonAutAltTrainPredAccuracy[1] + altPrisonAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[4][19] = ((double) (altPrisonAutAltTrainPredAccuracy[0] + altPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (altPrisonAutAltTrainPredAccuracy[1] + altPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[4][20] = ((double) (altPrisonAutChickenTrainPredAccuracy[0] + altPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (altPrisonAutChickenTrainPredAccuracy[1] + altPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[4][21] = ((double) (altPrisonAutAltTrainPredAccuracy[0] + altPrisonAutChickenTrainPredAccuracy[0] + altPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (altPrisonAutAltTrainPredAccuracy[1] + altPrisonAutChickenTrainPredAccuracy[1] + altPrisonAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[5][0] = ((double) chickenPrisonAutAltTestPredAccuracy[0]) / ((double) altPrisonAutAltTestPredAccuracy[1]);
        predictedResultMatrix[5][1] = ((double) chickenPrisonAutChickenTestPredAccuracy[0]) / ((double) altPrisonAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[5][2] = ((double) chickenPrisonAutPrisonTestPredAccuracy[0]) / ((double) chickenPrisonAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[5][3] = ((double) chickenPrisonAutEndlessTestPredAccuracy[0]) / ((double) chickenPrisonAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[5][4] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutChickenTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[5][5] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[5][6] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][7] = ((double) (chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0])) / ((double) (chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[5][8] = ((double) (chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][9] = ((double) (chickenPrisonAutPrisonTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutPrisonTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][10] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[5][11] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][12] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][13] = ((double) (chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][14] = ((double) (chickenPrisonAutAltTestPredAccuracy[0] + chickenPrisonAutChickenTestPredAccuracy[0] + chickenPrisonAutPrisonTestPredAccuracy[0] + chickenPrisonAutEndlessTestPredAccuracy[0])) / ((double) (chickenPrisonAutAltTestPredAccuracy[1] + chickenPrisonAutChickenTestPredAccuracy[1] + chickenPrisonAutPrisonTestPredAccuracy[1] + chickenPrisonAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[5][15] = ((double) chickenPrisonAutAltTrainPredAccuracy[0]) / ((double) chickenPrisonAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[5][16] = ((double) chickenPrisonAutChickenTrainPredAccuracy[0]) / ((double) chickenPrisonAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[5][17] = ((double) chickenPrisonAutPrisonTrainPredAccuracy[0]) / ((double) chickenPrisonAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[5][18] = ((double) (chickenPrisonAutAltTrainPredAccuracy[0] + chickenPrisonAutChickenTrainPredAccuracy[0])) / ((double) (chickenPrisonAutAltTrainPredAccuracy[1] + chickenPrisonAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[5][19] = ((double) (chickenPrisonAutAltTrainPredAccuracy[0] + chickenPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (chickenPrisonAutAltTrainPredAccuracy[1] + chickenPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[5][20] = ((double) (chickenPrisonAutChickenTrainPredAccuracy[0] + chickenPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (chickenPrisonAutChickenTrainPredAccuracy[1] + chickenPrisonAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[5][21] = ((double) (chickenPrisonAutAltTrainPredAccuracy[0] + chickenPrisonAutChickenTrainPredAccuracy[0] + chickenPrisonAutPrisonTrainPredAccuracy[0])) / ((double) (chickenPrisonAutAltTrainPredAccuracy[1] + chickenPrisonAutChickenTrainPredAccuracy[1] + chickenPrisonAutPrisonTrainPredAccuracy[1]));

        predictedResultMatrix[6][0] = ((double) combinedAutAltTestPredAccuracy[0]) / ((double) combinedAutAltTestPredAccuracy[1]);
        predictedResultMatrix[6][1] = ((double) combinedAutChickenTestPredAccuracy[0]) / ((double) combinedAutChickenTestPredAccuracy[1]);
        predictedResultMatrix[6][2] = ((double) combinedAutPrisonTestPredAccuracy[0]) / ((double) combinedAutPrisonTestPredAccuracy[1]);
        predictedResultMatrix[6][3] = ((double) combinedAutEndlessTestPredAccuracy[0]) / ((double) combinedAutEndlessTestPredAccuracy[1]);
        predictedResultMatrix[6][4] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutChickenTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutChickenTestPredAccuracy[1]));
        predictedResultMatrix[6][5] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[6][6] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][7] = ((double) (combinedAutChickenTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0])) / ((double) (combinedAutChickenTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[6][8] = ((double) (combinedAutChickenTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutChickenTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][9] = ((double) (combinedAutPrisonTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutPrisonTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][10] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutChickenTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutChickenTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1]));
        predictedResultMatrix[6][11] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutChickenTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutChickenTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][12] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][13] = ((double) (combinedAutChickenTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutChickenTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][14] = ((double) (combinedAutAltTestPredAccuracy[0] + combinedAutChickenTestPredAccuracy[0] + combinedAutPrisonTestPredAccuracy[0] + combinedAutEndlessTestPredAccuracy[0])) / ((double) (combinedAutAltTestPredAccuracy[1] + combinedAutChickenTestPredAccuracy[1] + combinedAutPrisonTestPredAccuracy[1] + combinedAutEndlessTestPredAccuracy[1]));
        predictedResultMatrix[6][15] = ((double) combinedAutAltTrainPredAccuracy[0]) / ((double) combinedAutAltTrainPredAccuracy[1]);
        predictedResultMatrix[6][16] = ((double) combinedAutChickenTrainPredAccuracy[0]) / ((double) combinedAutChickenTrainPredAccuracy[1]);
        predictedResultMatrix[6][17] = ((double) combinedAutPrisonTrainPredAccuracy[0]) / ((double) combinedAutPrisonTrainPredAccuracy[1]);
        predictedResultMatrix[6][18] = ((double) (combinedAutAltTrainPredAccuracy[0] + combinedAutChickenTrainPredAccuracy[0])) / ((double) (combinedAutAltTrainPredAccuracy[1] + combinedAutChickenTrainPredAccuracy[1]));
        predictedResultMatrix[6][19] = ((double) (combinedAutAltTrainPredAccuracy[0] + combinedAutPrisonTrainPredAccuracy[0])) / ((double) (combinedAutAltTrainPredAccuracy[1] + combinedAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[6][20] = ((double) (combinedAutChickenTrainPredAccuracy[0] + combinedAutPrisonTrainPredAccuracy[0])) / ((double) (combinedAutChickenTrainPredAccuracy[1] + combinedAutPrisonTrainPredAccuracy[1]));
        predictedResultMatrix[6][21] = ((double) (combinedAutAltTrainPredAccuracy[0] + combinedAutChickenTrainPredAccuracy[0] + combinedAutPrisonTrainPredAccuracy[0])) / ((double) (combinedAutAltTrainPredAccuracy[1] + combinedAutChickenTrainPredAccuracy[1] + combinedAutPrisonTrainPredAccuracy[1]));

        return predictedResultMatrix;
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

    public List<int[][]> getEnumeratedPredictions()
    {
        List<int[][]> enumeratedPredictions = new ArrayList<>();
        for(Game g : altTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g, altTestConverter));
        } for(Game g : chickenTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g, chickenTestConverter));
        }
        for(Game g : prisonTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g, prisonTestConverter));
        }
        for(Game g : endlessTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g,endlessTestConverter));
        }
        return enumeratedPredictions;
    }

    private int[][] getEnumeratedGame(Game game, ActionAttitudeConverter converter)
    {
        int[][] results = new int[game.getPlayer2().getActionHistoryLength()][9];
        List<Feature> fList = gameFeatureMap.get(game);
        int[][] clustList =featureListClusterListMap.get(fList);
        for(int r = 0; r < results.length; r++)
        {
            int p1Act = game.getPlayer1().getAction(r);
            int p2Act = game.getPlayer2().getAction(r);
            results[r][0] = p1Act;
            results[r][1] = p2Act;
            results[r][2] = -1;
            results[r][3] = -1;
            results[r][4] = -1;
            results[r][5] = -1;
            results[r][6] = -1;
            results[r][7] = -1;
            results[r][8] = -1;
            if(r >= memoryLength - 1)
            {
                int[][] sequence = new int[memoryLength][4];
                for(int hist = 0; hist < memoryLength; hist++)
                {
                    sequence[hist][0] = clustList[r + hist - memoryLength + 1][0];
                    sequence[hist][1] = clustList[r + hist - memoryLength + 1][1];
                    sequence[hist][2] = clustList[r + hist - memoryLength + 1][2];
                    sequence[hist][3] = clustList[r + hist - memoryLength + 1][3];
                }
                int[] altPredicted = altAutomaton.getMostProbableActionAndMessage(sequence);
                if(altPredicted[0] >= 0 && altPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][2] = predictedActs[0];
                }
                int[] chickenPredicted = chickenAutomaton.getMostProbableActionAndMessage(sequence);
                if(chickenPredicted[0] >= 0 && chickenPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[chickenPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][3] = predictedActs[0];
                }
                int[] prisonPredicted = prisonAutomaton.getMostProbableActionAndMessage(sequence);
                if(prisonPredicted[0] >= 0 && prisonPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[prisonPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][4] = predictedActs[0];
                }
                int[] altChickenPredicted = altChickenAutomaton.getMostProbableActionAndMessage(sequence);
                if(altChickenPredicted[0] >= 0 && altChickenPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altChickenPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][5] = predictedActs[0];
                }
                int[] altPrisonPredicted = altPrisonAutomaton.getMostProbableActionAndMessage(sequence);
                if(altPrisonPredicted[0] >= 0 && altPrisonPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altPrisonPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][6] = predictedActs[0];
                }
                int[] chickenPrisonPredicted = chickenPrisonAutomaton.getMostProbableActionAndMessage(sequence);
                if(chickenPrisonPredicted[0] >= 0 && chickenPrisonPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[chickenPrisonPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][7] = predictedActs[0];
                }
                int[] combinedPredicted = combinedAutomaton.getMostProbableActionAndMessage(sequence);
                if(combinedPredicted[0] >= 0 && combinedPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[combinedPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][8] = predictedActs[0];
                }
            }
        }
        return results;
    }

}
