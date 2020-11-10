import Attitudes.ActionAttitudeConverterTest;
import Attitudes.Attitude;
import Attitudes.AttitudeVector;
import Automata.GameToAutomata;
import Clustering.*;
import Features.Feature;
import Features.GameToFeatureList;
import Game.Game;
import Game.TXTtoGame;
import StrategyAutomata.*;
import StrategyTables.StrategyTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main
{

    private static final String LOAD_MENU = "Where do you want your clusters from?\n" +
            "1 - Restore from previous\n" +
            "2 - Upload game files";
    private static final String MENU = "What would you like to do?\n" +
            "0 - Quit\n" +
            "1 - Cluster game files\n" +
            "2 - Generate mega automaton";

    private static final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";
    private static int nNeighbors = 1;
    private static double pPower = 0.6;
    private static double discount = 0.5;

    private static DistantDataCluster[] dispAVClusters;
    private static DistantDataCluster[] saidAVClusters;

    public static void main(String[] args)
    {
        Scanner keys = new Scanner(System.in);
        boolean loaded = false;
        while (!loaded)
        {
            System.out.println(LOAD_MENU);
            int choice = keys.nextInt();
            switch (choice)
            {
                case 1:
                {
                    loaded = true;
                    break;
                } case 2:
                {
                    JFileChooser fileChooser = new JFileChooser(dir);
                    fileChooser.setMultiSelectionEnabled(true);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    File[] files = fileChooser.getSelectedFiles();
                    System.out.println(files[0].getPath());
                    ArrayList<StrategyTable> tables = new ArrayList<>();
                    ArrayList<Game> games = new ArrayList<>();
                    for(int f = 0; f < files.length / 2; f++)
                    {
//                System.out.println(f);
                        TXTtoGame txtToGame = new TXTtoGame();
                        games.add(txtToGame.openFile(files[f * 2].getPath(), files[f * 2 + 1].getPath()));
                    }
//                    clusterAttitudeVectors(files, games);
                    // =============START=============
                    Collection<Collection<Feature>> featureCollection = new ArrayList<>();
                    for(int g = 0; g < games.size(); g++)
                    {
                        GameToFeatureList gameToFeatureList = new GameToFeatureList(games.get(g));
                        Collection<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
                        featureCollection.add(fCollection);
                    }
                    int maxDAVClusters = 8;
                    int minDAVClusters = 4;
                    int maxSAVClusters = 8;
                    int minSAVClusters = 4;
                    FeatList2DispAttVecClusts featList2DispAttVecClusts = FeatList2DispAttVecClusts.getInstance();
                    FeatList2SaidAttVecClusts featList2SaidAttVecClusts = FeatList2SaidAttVecClusts.getInstance();
                    dispAVClusters = featList2DispAttVecClusts.getDisplayedAttitudeVectorCluster(minDAVClusters, maxDAVClusters, featureCollection);
                    saidAVClusters = featList2SaidAttVecClusts.getSaidAttitudeVectorCluster(minSAVClusters, maxSAVClusters, featureCollection);
                    // ==============END================
                    loaded = true;
                    break;
                } case 3:
                {
                    ClusteredGameStatGatherer cgsg = new ClusteredGameStatGatherer();
                    cgsg.printResults();
                    break;
                } case 4:
                {
                    MegaAutomatonTest megaAutomatonTest = new MegaAutomatonTest(0.9);
                    megaAutomatonTest.run();
                    break;
                } case 5:
                {
                    ActionAttitudeConverterTest aacTest = new ActionAttitudeConverterTest();
                    aacTest.run();
                } default:
                {
                    System.out.println("Invalid Selection. Try again..");
                }
            }
        }
        boolean quit = false;
        while (!quit)
        {
            System.out.println(MENU);
            int choice = keys.nextInt();
            switch (choice)
            {
                case 0:
                {
                    quit = true;
                    break;
                }
                case 1:
                {
                    JFileChooser fileChooser = new JFileChooser(dir);
                    fileChooser.setMultiSelectionEnabled(true);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    File[] gameFiles = fileChooser.getSelectedFiles();
                    TXTtoGame txtToGame = new TXTtoGame();
                    for(int f = 0; f < gameFiles.length / 2; f++)
                    {
                        Game game = txtToGame.openFile(gameFiles[2 * f].getPath(), gameFiles[2 * f + 1].getPath());
                        printAVClusteredFiles(game, gameFiles[2 * f], dispAVClusters, saidAVClusters);
                    }
                    break;
                } case 2:
                {
                    JFileChooser fileChooser = new JFileChooser(dir);
                    fileChooser.setMultiSelectionEnabled(true);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    File[] files = fileChooser.getSelectedFiles();
                    System.out.println(files[0].getPath());
                    ArrayList<StrategyTable> tables = new ArrayList<>();
                    ArrayList<Game> games = new ArrayList<>();
                    for(int f = 0; f < files.length / 2; f++)
                    {
                        TXTtoGame txtToGame = new TXTtoGame();
                        games.add(txtToGame.openFile(files[f * 2].getPath(), files[f * 2 + 1].getPath()));
                    }
                    int memoryLength = 2;
                    AttitudeVector[] dispAVs = new AttitudeVector[dispAVClusters.length];
                    AttitudeVector[] saidAVs = new AttitudeVector[saidAVClusters.length];
                    DSGeneralAutomaton megaAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
                    for(Game game : games)
                    {
                        GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
                        List<Feature> featureList = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
                        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
                        int[][] dsAttVecClustList = fl2dsavcl.getClusterIndexList(featureList, dispAVClusters, saidAVClusters);
                        megaAutomaton.addObservations(dsAttVecClustList);
                    }
                    System.out.print("Total count: ");
                    System.out.println(megaAutomaton.getTotalCount());
                    System.out.print("Sequences: ");
                    System.out.println(megaAutomaton.getNumDistinctSequences());
                    System.out.print("Highest count: ");
                    System.out.println(megaAutomaton.getHighestCount());
                    break;
                } default:
                {
                    System.out.println("Invalid selection");
                }
            }
        }
        System.out.println("Done");
    }

    private static void printAVClusteredFiles(Game game, File originalFile, DistantDataCluster[] dispAVClusters, DistantDataCluster[] saidAVClusters)
    {
        GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
        List<Feature> featureList = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
        int[][] dsAttVecClustList = fl2dsavcl.getClusterIndexList(featureList, dispAVClusters, saidAVClusters);
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(dir + "\\Cluster List Results\\");
        nameBuilder.append(originalFile.getPath().substring(originalFile.getPath().indexOf("Results") + 8, originalFile.getPath().indexOf("activity_")));
        nameBuilder.append("Representative");
        nameBuilder.append(".csv");
        System.out.println(nameBuilder.toString());
        try(FileWriter fWriter = new FileWriter(nameBuilder.toString()))
        {
            fWriter.append("Displayed Attitude Vector Cluster representatives");
            fWriter.append('\n');
            for(int dav = 0; dav < dispAVClusters.length; dav++)
            {
                AttitudeVector centroid = (AttitudeVector) dispAVClusters[dav].getCentroid();
                StringBuilder builder = new StringBuilder();
                builder.append("d");
                builder.append(dav);
                builder.append(",(");
                builder.append(centroid.getGreedy());
                builder.append(" ");
                builder.append(centroid.getPlacate());
                builder.append(" ");
                builder.append(centroid.getCooperate());
                builder.append(" ");
                builder.append(centroid.getAbsurd());
                builder.append(")");
                fWriter.append(builder.toString());
                fWriter.append('\n');
            }
            fWriter.append('\n');
            fWriter.append("Said Attitude Vector Cluster representatives");
            fWriter.append('\n');
            for(int sav = 0; sav < saidAVClusters.length; sav++)
            {
                AttitudeVector centroid = (AttitudeVector) saidAVClusters[sav].getCentroid();
                StringBuilder builder = new StringBuilder();
                builder.append("s");
                builder.append(sav);
                builder.append(",(");
                builder.append(centroid.getGreedy());
                builder.append(" ");
                builder.append(centroid.getPlacate());
                builder.append(" ");
                builder.append(centroid.getCooperate());
                builder.append(" ");
                builder.append(centroid.getAbsurd());
                builder.append(")");
                fWriter.append(builder.toString());
                fWriter.append('\n');
            }
            fWriter.append('\n');
            for(int avcl = 0; avcl < dsAttVecClustList.length; avcl++)
            {
                StringBuilder builder = new StringBuilder();
                builder.append("(d");
                builder.append(dsAttVecClustList[avcl][0]);
                builder.append(" s");
                builder.append(dsAttVecClustList[avcl][1]);
                builder.append(" d");
                builder.append(dsAttVecClustList[avcl][2]);
                builder.append(" s");
                builder.append(dsAttVecClustList[avcl][3]);
                builder.append(")");
                fWriter.append(builder.toString());
                fWriter.append('\n');
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void graphFeatures(File[] files, ArrayList<Game> games)
    {

        for(int g = 0; g < games.size(); g++)
        {
            int neighbors = 5;
            double discount = 0.9;
            double predictive = 0.5;
            int discountLabel = (int) (100 * discount);
            int predictiveLabel = (int) (100 * predictive);
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(files[g * 2].getPath().substring(0, files[g * 2].getPath().indexOf("activity_")));
            nameBuilder.append("n");
            nameBuilder.append(neighbors);
            nameBuilder.append("_d");
            nameBuilder.append(discountLabel);
            nameBuilder.append("_p");
            nameBuilder.append(predictiveLabel);
            nameBuilder.append("_");
            String fileBase = nameBuilder.toString();
            System.out.println(fileBase);
            int fileNum = 0;
            boolean lastFound = false;
            while(!lastFound)
            {
                fileNum++;
                StringBuilder builder = new StringBuilder();
                builder.append(fileBase);
                builder.append(fileNum);
                builder.append(".csv");
                File file = new File(builder.toString());
                lastFound = !file.exists();
            }

            GameToFeatureList gameToFeatures = new GameToFeatureList(games.get(g));
            List<Feature> gameFeatures = gameToFeatures.generateFeatureList(neighbors, discount, predictive);
            StringBuilder builder = new StringBuilder();
            builder.append(fileBase);
            builder.append(fileNum);
            builder.append(".csv");
            String path = builder.toString();
            try(FileWriter fWriter = new FileWriter(path))
            {
                String header = "Greed Displayed,Placate Displayed,Cooperation Displayed,Absurdity Displayed," +
                        "Greed Said,Placate Said,Cooperation Said,Absurdity Said," +
                        "Greed Other Did,Placate Other Did,Cooperation Other Did,Absurdity Other Did," +
                        "Integrity,Deference";
                fWriter.append(header);
                fWriter.append('\n');
                for(int i = 0; i < gameFeatures.size(); i++)
                {
                    StringBuilder b = new StringBuilder();
                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getGreedy());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getPlacate());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getCooperate());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getAbsurd());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeSaid().getGreedy());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeSaid().getPlacate());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeSaid().getCooperate());
                    b.append(",");
                    b.append(gameFeatures.get(i).getAttitudeSaid().getAbsurd());
                    b.append(",");
                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getGreedy());
                    b.append(",");
                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getPlacate());
                    b.append(",");
                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getCooperate());
                    b.append(",");
                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getAbsurd());
                    b.append(",");
                    b.append(gameFeatures.get(i).getIntegrity());
                    b.append(",");
                    b.append(gameFeatures.get(i).getDeference());
                    fWriter.append(b.toString());
                    fWriter.append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileNum++;
        }
    }

    private static void testGameToAutomata()
    {
        GameToAutomata.getInstance().generateAutomaton(null, 2);


        System.err.println("Success!");
    }

}
