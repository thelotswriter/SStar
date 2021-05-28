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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalSplitTest
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

//    private DSGeneralAutomaton altAutomaton;
//    private DSGeneralAutomaton chickenAutomaton;
//    private DSGeneralAutomaton prisonAutomaton;
//    private DSGeneralAutomaton altChickenAutomaton;
//    private DSGeneralAutomaton altPrisonAutomaton;
//    private DSGeneralAutomaton chickenPrisonAutomaton;
//    private DSGeneralAutomaton combinedAutomaton;

    private AutomataGroup altAutomata;
    private AutomataGroup chickenAutomata;
    private AutomataGroup prisonAutomata ;
    private AutomataGroup altChickenAutomata;
    private AutomataGroup altPrisonAutomata;
    private AutomataGroup chickenPrisonAutomata;
    private AutomataGroup combinedAutomata;

    private FeatList2DSAttVecClustList fl2dsavcl;

    private int nNeighbors = 1;
    private double pPower = 1.0;
    private double discount = 0.0;
    private int memoryLength;

    public FinalSplitTest(int memLength)
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
        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
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
        int minSecond = 2;

        int splitNum = 10;
        altAutomata = new AutomataGroup(splitNum, altAutomaton);
        chickenAutomata = new AutomataGroup(splitNum, chickenAutomaton);
        prisonAutomata = new AutomataGroup(splitNum, prisonAutomaton);
        altChickenAutomata = new AutomataGroup(splitNum, altChickenAutomaton);
        altPrisonAutomata = new AutomataGroup(splitNum, altPrisonAutomaton);
        chickenPrisonAutomata = new AutomataGroup(splitNum, chickenPrisonAutomaton);
        combinedAutomata = new AutomataGroup(splitNum, combinedAutomaton);

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
//        resultMatrix[0][0] = calculatePrecitionAccuracy(altAutomata, altTestClustLists);
//        resultMatrix[0][1] = calculatePrecitionAccuracy(altAutomata, chickenTestClustLists);
//        resultMatrix[0][2] = calculatePrecitionAccuracy(altAutomata, prisonTestClustLists);
//        resultMatrix[0][3] = calculatePrecitionAccuracy(altAutomata, altChickenTestClustLists);
//        resultMatrix[0][4] = calculatePrecitionAccuracy(altAutomata, altPrisonTestClustLists);
//        resultMatrix[0][5] = calculatePrecitionAccuracy(altAutomata, chickenPrisonTestClustLists);
//        resultMatrix[0][6] = calculatePrecitionAccuracy(altAutomata, combinedTestClustLists);
//        resultMatrix[0][7] = calculatePrecitionAccuracy(altAutomata, altTrainClustLists);
//        resultMatrix[0][8] = calculatePrecitionAccuracy(altAutomata, chickenTrainClustLists);
//        resultMatrix[0][9] = calculatePrecitionAccuracy(altAutomata, prisonTrainClustLists);
//        resultMatrix[0][10] = calculatePrecitionAccuracy(altAutomata, altChickenTrainClustLists);
//        resultMatrix[0][11] = calculatePrecitionAccuracy(altAutomata, altPrisonTrainClustLists);
//        resultMatrix[0][12] = calculatePrecitionAccuracy(altAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[0][13] = calculatePrecitionAccuracy(altAutomata, combinedTrainClustLists);
//        resultMatrix[1][0] = calculatePrecitionAccuracy(chickenAutomata, altTestClustLists);
//        resultMatrix[1][1] = calculatePrecitionAccuracy(chickenAutomata, chickenTestClustLists);
//        resultMatrix[1][2] = calculatePrecitionAccuracy(chickenAutomata, prisonTestClustLists);
//        resultMatrix[1][3] = calculatePrecitionAccuracy(chickenAutomata, altChickenTestClustLists);
//        resultMatrix[1][4] = calculatePrecitionAccuracy(chickenAutomata, altPrisonTestClustLists);
//        resultMatrix[1][5] = calculatePrecitionAccuracy(chickenAutomata, chickenPrisonTestClustLists);
//        resultMatrix[1][6] = calculatePrecitionAccuracy(chickenAutomata, combinedTestClustLists);
//        resultMatrix[1][7] = calculatePrecitionAccuracy(chickenAutomata, altTrainClustLists);
//        resultMatrix[1][8] = calculatePrecitionAccuracy(chickenAutomata, chickenTrainClustLists);
//        resultMatrix[1][9] = calculatePrecitionAccuracy(chickenAutomata, prisonTrainClustLists);
//        resultMatrix[1][10] = calculatePrecitionAccuracy(chickenAutomata, altChickenTrainClustLists);
//        resultMatrix[1][11] = calculatePrecitionAccuracy(chickenAutomata, altPrisonTrainClustLists);
//        resultMatrix[1][12] = calculatePrecitionAccuracy(chickenAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[1][13] = calculatePrecitionAccuracy(chickenAutomata, combinedTrainClustLists);
//        resultMatrix[2][0] = calculatePrecitionAccuracy(prisonAutomata, altTestClustLists);
//        resultMatrix[2][1] = calculatePrecitionAccuracy(prisonAutomata, chickenTestClustLists);
//        resultMatrix[2][2] = calculatePrecitionAccuracy(prisonAutomata, prisonTestClustLists);
//        resultMatrix[2][3] = calculatePrecitionAccuracy(prisonAutomata, altChickenTestClustLists);
//        resultMatrix[2][4] = calculatePrecitionAccuracy(prisonAutomata, altPrisonTestClustLists);
//        resultMatrix[2][5] = calculatePrecitionAccuracy(prisonAutomata, chickenPrisonTestClustLists);
//        resultMatrix[2][6] = calculatePrecitionAccuracy(prisonAutomata, combinedTestClustLists);
//        resultMatrix[2][7] = calculatePrecitionAccuracy(prisonAutomata, altTrainClustLists);
//        resultMatrix[2][8] = calculatePrecitionAccuracy(prisonAutomata, chickenTrainClustLists);
//        resultMatrix[2][9] = calculatePrecitionAccuracy(prisonAutomata, prisonTrainClustLists);
//        resultMatrix[2][10] = calculatePrecitionAccuracy(prisonAutomata, altChickenTrainClustLists);
//        resultMatrix[2][11] = calculatePrecitionAccuracy(prisonAutomata, altPrisonTrainClustLists);
//        resultMatrix[2][12] = calculatePrecitionAccuracy(prisonAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[2][13] = calculatePrecitionAccuracy(prisonAutomata, combinedTrainClustLists);
//        resultMatrix[3][0] = calculatePrecitionAccuracy(altChickenAutomata, altTestClustLists);
//        resultMatrix[3][1] = calculatePrecitionAccuracy(altChickenAutomata, chickenTestClustLists);
//        resultMatrix[3][2] = calculatePrecitionAccuracy(altChickenAutomata, prisonTestClustLists);
//        resultMatrix[3][3] = calculatePrecitionAccuracy(altChickenAutomata, altChickenTestClustLists);
//        resultMatrix[3][4] = calculatePrecitionAccuracy(altChickenAutomata, altPrisonTestClustLists);
//        resultMatrix[3][5] = calculatePrecitionAccuracy(altChickenAutomata, chickenPrisonTestClustLists);
//        resultMatrix[3][6] = calculatePrecitionAccuracy(altChickenAutomata, combinedTestClustLists);
//        resultMatrix[3][7] = calculatePrecitionAccuracy(altChickenAutomata, altTrainClustLists);
//        resultMatrix[3][8] = calculatePrecitionAccuracy(altChickenAutomata, chickenTrainClustLists);
//        resultMatrix[3][9] = calculatePrecitionAccuracy(altChickenAutomata, prisonTrainClustLists);
//        resultMatrix[3][10] = calculatePrecitionAccuracy(altChickenAutomata, altChickenTrainClustLists);
//        resultMatrix[3][11] = calculatePrecitionAccuracy(altChickenAutomata, altPrisonTrainClustLists);
//        resultMatrix[3][12] = calculatePrecitionAccuracy(altChickenAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[3][13] = calculatePrecitionAccuracy(altChickenAutomata, combinedTrainClustLists);
//        resultMatrix[4][0] = calculatePrecitionAccuracy(altPrisonAutomata, altTestClustLists);
//        resultMatrix[4][1] = calculatePrecitionAccuracy(altPrisonAutomata, chickenTestClustLists);
//        resultMatrix[4][2] = calculatePrecitionAccuracy(altPrisonAutomata, prisonTestClustLists);
//        resultMatrix[4][3] = calculatePrecitionAccuracy(altPrisonAutomata, altChickenTestClustLists);
//        resultMatrix[4][4] = calculatePrecitionAccuracy(altPrisonAutomata, altPrisonTestClustLists);
//        resultMatrix[4][5] = calculatePrecitionAccuracy(altPrisonAutomata, chickenPrisonTestClustLists);
//        resultMatrix[4][6] = calculatePrecitionAccuracy(altPrisonAutomata, combinedTestClustLists);
//        resultMatrix[4][7] = calculatePrecitionAccuracy(altPrisonAutomata, altTrainClustLists);
//        resultMatrix[4][8] = calculatePrecitionAccuracy(altPrisonAutomata, chickenTrainClustLists);
//        resultMatrix[4][9] = calculatePrecitionAccuracy(altPrisonAutomata, prisonTrainClustLists);
//        resultMatrix[4][10] = calculatePrecitionAccuracy(altPrisonAutomata, altChickenTrainClustLists);
//        resultMatrix[4][11] = calculatePrecitionAccuracy(altPrisonAutomata, altPrisonTrainClustLists);
//        resultMatrix[4][12] = calculatePrecitionAccuracy(altPrisonAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[4][13] = calculatePrecitionAccuracy(altPrisonAutomata, combinedTrainClustLists);
//        resultMatrix[5][0] = calculatePrecitionAccuracy(chickenPrisonAutomata, altTestClustLists);
//        resultMatrix[5][1] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenTestClustLists);
//        resultMatrix[5][2] = calculatePrecitionAccuracy(chickenPrisonAutomata, prisonTestClustLists);
//        resultMatrix[5][3] = calculatePrecitionAccuracy(chickenPrisonAutomata, altChickenTestClustLists);
//        resultMatrix[5][4] = calculatePrecitionAccuracy(chickenPrisonAutomata, altPrisonTestClustLists);
//        resultMatrix[5][5] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenPrisonTestClustLists);
//        resultMatrix[5][6] = calculatePrecitionAccuracy(chickenPrisonAutomata, combinedTestClustLists);
//        resultMatrix[5][7] = calculatePrecitionAccuracy(chickenPrisonAutomata, altTrainClustLists);
//        resultMatrix[5][8] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenTrainClustLists);
//        resultMatrix[5][9] = calculatePrecitionAccuracy(chickenPrisonAutomata, prisonTrainClustLists);
//        resultMatrix[5][10] = calculatePrecitionAccuracy(chickenPrisonAutomata, altChickenTrainClustLists);
//        resultMatrix[5][11] = calculatePrecitionAccuracy(chickenPrisonAutomata, altPrisonTrainClustLists);
//        resultMatrix[5][12] = calculatePrecitionAccuracy(chickenPrisonAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[5][13] = calculatePrecitionAccuracy(chickenPrisonAutomata, combinedTrainClustLists);
//        resultMatrix[6][0] = calculatePrecitionAccuracy(combinedAutomata, altTestClustLists);
//        resultMatrix[6][1] = calculatePrecitionAccuracy(combinedAutomata, chickenTestClustLists);
//        resultMatrix[6][2] = calculatePrecitionAccuracy(combinedAutomata, prisonTestClustLists);
//        resultMatrix[6][3] = calculatePrecitionAccuracy(combinedAutomata, altChickenTestClustLists);
//        resultMatrix[6][4] = calculatePrecitionAccuracy(combinedAutomata, altPrisonTestClustLists);
//        resultMatrix[6][5] = calculatePrecitionAccuracy(combinedAutomata, chickenPrisonTestClustLists);
//        resultMatrix[6][6] = calculatePrecitionAccuracy(combinedAutomata, combinedTestClustLists);
//        resultMatrix[6][7] = calculatePrecitionAccuracy(combinedAutomata, altTrainClustLists);
//        resultMatrix[6][8] = calculatePrecitionAccuracy(combinedAutomata, chickenTrainClustLists);
//        resultMatrix[6][9] = calculatePrecitionAccuracy(combinedAutomata, prisonTrainClustLists);
//        resultMatrix[6][10] = calculatePrecitionAccuracy(combinedAutomata, altChickenTrainClustLists);
//        resultMatrix[6][11] = calculatePrecitionAccuracy(combinedAutomata, altPrisonTrainClustLists);
//        resultMatrix[6][12] = calculatePrecitionAccuracy(combinedAutomata, chickenPrisonTrainClustLists);
//        resultMatrix[6][13] = calculatePrecitionAccuracy(combinedAutomata, combinedTrainClustLists);

        int[] altAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] altAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] altAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] altAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] altAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] altAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] chickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] chickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] chickenAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] chickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] prisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] prisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] prisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] prisonAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] prisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] prisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] prisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(prisonAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altChickenAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] altChickenAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] altChickenAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] altChickenAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] altChickenAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] altChickenAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altChickenAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altChickenAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] altPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] altPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] altPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] altPrisonAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] altPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] altPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] altPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(altPrisonAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] chickenPrisonAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] chickenPrisonAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] chickenPrisonAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] chickenPrisonAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] chickenPrisonAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] chickenPrisonAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] chickenPrisonAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(chickenPrisonAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

        int[] combinedAutAltTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, altTestClustLists, altTestConverter, altTestGames);
        int[] combinedAutChickenTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, chickenTestClustLists, chickenTestConverter, chickenTestGames);
        int[] combinedAutPrisonTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, prisonTestClustLists, prisonTestConverter, prisonTestGames);
        int[] combinedAutEndlessTestPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, endlessTestClustLists, endlessTestConverter, endlessTestGames);
        int[] combinedAutAltTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, altTrainClustLists, altConverter, altTrainGames);
        int[] combinedAutChickenTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, chickenTrainClustLists, chickenConverter, chickenTrainGames);
        int[] combinedAutPrisonTrainPredAccuracy = calculateConvertedPredictionAccuracy(combinedAutomata, prisonTrainClustLists, prisonConverter, prisonTrainGames);

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
                        } else if(p == 0)
                        {
                            previouslyCorrect[p] = false;
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
        }
        for(Game g : chickenTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g, chickenTestConverter));
        }
        for(Game g : prisonTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g, endlessTestConverter));
        }
        for(Game g : endlessTestGames)
        {
            enumeratedPredictions.add(getEnumeratedGame(g, prisonTestConverter));
        }
        return enumeratedPredictions;
    }

    private int[][] getEnumeratedGame(Game game, ActionAttitudeConverter converter)
    {
        altAutomata.prepForNewGame();
        chickenAutomata.prepForNewGame();
        prisonAutomata.prepForNewGame();;
        altChickenAutomata.prepForNewGame();
        altPrisonAutomata.prepForNewGame();
        chickenPrisonAutomata.prepForNewGame();
        combinedAutomata.prepForNewGame();

        boolean[] previouslyCorrectAlt = new boolean[altAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectAlt.length; pC++)
        {
            previouslyCorrectAlt[pC] = true;
        }
        boolean[] previouslyCorrectChicken = new boolean[chickenAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectChicken.length; pC++)
        {
            previouslyCorrectChicken[pC] = true;
        }
        boolean[] previouslyCorrectPrison = new boolean[prisonAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectPrison.length; pC++)
        {
            previouslyCorrectPrison[pC] = true;
        }
        boolean[] previouslyCorrectAltChicken = new boolean[altChickenAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectAltChicken.length; pC++)
        {
            previouslyCorrectAltChicken[pC] = true;
        }
        boolean[] previouslyCorrectAltPrison = new boolean[altPrisonAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectAltPrison.length; pC++)
        {
            previouslyCorrectAltPrison[pC] = true;
        }
        boolean[] previouslyCorrectChickenPrison = new boolean[chickenPrisonAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectChickenPrison.length; pC++)
        {
            previouslyCorrectChickenPrison[pC] = true;
        }
        boolean[] previouslyCorrectCombined = new boolean[combinedAutomata.size() + 1];
        for(int pC = 0; pC < previouslyCorrectCombined.length; pC++)
        {
            previouslyCorrectCombined[pC] = true;
        }

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
                int[][] altPredictionArray = altAutomata.getPredictionArray(previouslyCorrectAlt, sequence);
                int[] altPredicted = altPredictionArray[0];
                if(altPredicted[0] >= 0 && altPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][2] = predictedActs[0];
                }
                for(int aut = 0; aut < altPredictionArray.length; aut++)
                {
                    if(altPredictionArray[aut][0] >= 0 && altPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectAlt[aut] = true;
                        } else
                        {
                            previouslyCorrectAlt[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectAlt[aut] = false;
                    }
                }

                int[][] chickenPredictionArray = chickenAutomata.getPredictionArray(previouslyCorrectChicken, sequence);
                int[] chickenPredicted = chickenPredictionArray[0];
                if(chickenPredicted[0] >= 0 && chickenPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[chickenPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][3] = predictedActs[0];
                }
                for(int aut = 0; aut < chickenPredictionArray.length; aut++)
                {
                    if(chickenPredictionArray[aut][0] >= 0 && chickenPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[chickenPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectChicken[aut] = true;
                        } else
                        {
                            previouslyCorrectChicken[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectChicken[aut] = false;
                    }
                }

                int[][] prisonPredictionArray = prisonAutomata.getPredictionArray(previouslyCorrectPrison, sequence);
                int[] prisonPredicted = prisonPredictionArray[0];
                if(prisonPredicted[0] >= 0 && prisonPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[prisonPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][4] = predictedActs[0];
                }
                for(int aut = 0; aut < prisonPredictionArray.length; aut++)
                {
                    if(prisonPredictionArray[aut][0] >= 0 && prisonPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[prisonPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectPrison[aut] = true;
                        } else
                        {
                            previouslyCorrectPrison[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectPrison[aut] = false;
                    }
                }

                int[][] altChickenPredictionArray = altChickenAutomata.getPredictionArray(previouslyCorrectAltChicken, sequence);
                int[] altChickenPredicted = altChickenPredictionArray[0];
                if(altChickenPredicted[0] >= 0 && altChickenPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altChickenPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][5] = predictedActs[0];
                }
                for(int aut = 0; aut < altChickenPredictionArray.length; aut++)
                {
                    if(altChickenPredictionArray[aut][0] >= 0 && altChickenPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altChickenPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectAltChicken[aut] = true;
                        } else
                        {
                            previouslyCorrectAltChicken[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectAltChicken[aut] = false;
                    }
                }

                int[][] altPrisonPredictionArray = altPrisonAutomata.getPredictionArray(previouslyCorrectAltPrison, sequence);
                int[] altPrisonPredicted = altPrisonPredictionArray[0];
                if(altPrisonPredicted[0] >= 0 && altPrisonPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altPrisonPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][6] = predictedActs[0];
                }
                for(int aut = 0; aut < altPrisonPredictionArray.length; aut++)
                {
                    if(altPrisonPredictionArray[aut][0] >= 0 && altPrisonPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[altPrisonPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectAltPrison[aut] = true;
                        } else
                        {
                            previouslyCorrectAltPrison[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectAltPrison[aut] = false;
                    }
                }

                int[][] chickenPrisonPredictionArray = chickenPrisonAutomata.getPredictionArray(previouslyCorrectChickenPrison, sequence);
                int[] chickenPrisonPredicted = chickenPrisonPredictionArray[0];
                if(chickenPrisonPredicted[0] >= 0 && chickenPrisonPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[chickenPrisonPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][7] = predictedActs[0];
                }
                for(int aut = 0; aut < chickenPrisonPredictionArray.length; aut++)
                {
                    if(chickenPrisonPredictionArray[aut][0] >= 0 && chickenPrisonPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[chickenPrisonPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectChickenPrison[aut] = true;
                        } else
                        {
                            previouslyCorrectChickenPrison[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectChickenPrison[aut] = false;
                    }
                }

                int[][] combinedPredictionArray = combinedAutomata.getPredictionArray(previouslyCorrectCombined, sequence);
                int[] combinedPredicted = combinedPredictionArray[0];
                if(combinedPredicted[0] >= 0 && combinedPredicted[1] >= 0)
                {
                    AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[combinedPredicted[0]].getCentroid();
                    int[] prevActPair = game.getActionPair(r);
                    AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                    AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                    int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                    results[r][8] = predictedActs[0];
                }
                for(int aut = 0; aut < combinedPredictionArray.length; aut++)
                {
                    if(combinedPredictionArray[aut][0] >= 0 && combinedPredictionArray[aut][1] >= 0)
                    {
                        AttitudeVector predictedAV = (AttitudeVector) dispAVClusters[combinedPredictionArray[aut][0]].getCentroid();
                        int[] prevActPair = game.getActionPair(r);
                        AttitudeVector prevAttDisp = converter.getAttitudeVectorFromActionPair(prevActPair[0], prevActPair[1]);
                        AttitudeVector extrapolatedAV = AttitudeVector.extrapolate(prevAttDisp, predictedAV);
                        int[] predictedActs = converter.attitudeVectorToActions(extrapolatedAV);
                        if(predictedActs[0] == p1Act)
                        {
                            previouslyCorrectCombined[aut] = true;
                        } else
                        {
                            previouslyCorrectCombined[aut] = false;
                        }
                    } else
                    {
                        previouslyCorrectCombined[aut] = false;
                    }
                }
            }
        }
        return results;
    }

}
