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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Main
{

    private static final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";
    private static int nNeighbors = 1;
    private static double pPower = 0.6;
    private static double discount = 0.5;

    public static void main(String[] args)
    {
        boolean test = false;
        if(test)
        {
            testGameToAutomata();
        } else
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
            clusterAttitudeVectors(files, games);
//            Collection<Collection<Feature>> featureCollection = new ArrayList<>();
//            for(int g = 0; g < games.size(); g++)
//            {
//                GameToFeatureList gameToFeatureList = new GameToFeatureList(games.get(g));
//                Collection<Feature> fCollection = gameToFeatureList.generateFeatureList(5, 0.9, 0.5);
//                featureCollection.add(fCollection);
//            }
//            int maxDAVClusters = 25;
//            int minDAVClusters = 4;
//            int maxSAVClusters = 15;
//            int minSAVClusters = 4;
//            FeatList2DispAttVecClusts featList2DispAttVecClusts = FeatList2DispAttVecClusts.getInstance();
//            FeatList2SaidAttVecClusts featList2SaidAttVecClusts = FeatList2SaidAttVecClusts.getInstance();
//            DistantDataCluster[] dispAVClusters = featList2DispAttVecClusts.getDisplayedAttitudeVectorCluster(minDAVClusters, maxDAVClusters, featureCollection);
//            DistantDataCluster[] saidAVClusters = featList2SaidAttVecClusts.getSaidAttitudeVectorCluster(minSAVClusters, maxSAVClusters, featureCollection);
//            DSAttitudeVectorsToGeneralAttitudeVectorList dsav2gavl = DSAttitudeVectorsToGeneralAttitudeVectorList.getInstance();
//            List<GeneralState> allStates = new ArrayList<>(dsav2gavl.giveAllStates(dispAVClusters, saidAVClusters));
//            int memoryLength = 2;
//            GeneralAutomaton megaAutomaton = new GeneralAutomaton(memoryLength, allStates);
//            FeatList2GSList fl2gsl = new FeatList2GSList(dispAVClusters, saidAVClusters, allStates);
//            Collection<GeneralState[]> observations = fl2gsl.convert(featureCollection);
//            megaAutomaton.addObservations(observations);
//            System.out.println(megaAutomaton.toString());
        }
    }

    private static void clusterAttitudeVectors(File[] files, ArrayList<Game> games)
    {
        Collection<Collection<Feature>> featureCollection = new ArrayList<>();
        for(int g = 0; g < games.size(); g++)
        {
//            System.out.println(g);
            GameToFeatureList gameToFeatureList = new GameToFeatureList(games.get(g));
            Collection<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            featureCollection.add(fCollection);
        }
//        FeaturesToFeatureClusters featuresToFeatureClusters = FeaturesToFeatureClusters.getInstance();
//        int numFeatureClusters = 10;
//        int numAVClusters = 20;
        int maxDAVClusters = 25;
        int minDAVClusters = 4;
        int maxSAVClusters = 15;
        int minSAVClusters = 4;
//        DistantDataCluster[] featureClusters = featuresToFeatureClusters.getFeatureCluster(numFeatureClusters, featureCollection);
//        FeaturesToAttitudeVectorClusters featuresToAttitudeVectorClusters = FeaturesToAttitudeVectorClusters.getInstance();
        FeatList2DispAttVecClusts featList2DispAttVecClusts = FeatList2DispAttVecClusts.getInstance();
        FeatList2SaidAttVecClusts featList2SaidAttVecClusts = FeatList2SaidAttVecClusts.getInstance();
//        DistantDataCluster[] attitudeVectorClusters = featuresToAttitudeVectorClusters.getAttitudeVectorCluster(numAVClusters, featureCollection);
        DistantDataCluster[] dispAVClusters = featList2DispAttVecClusts.getDisplayedAttitudeVectorCluster(minDAVClusters, maxDAVClusters, featureCollection);
        DistantDataCluster[] saidAVClusters = featList2SaidAttVecClusts.getSaidAttitudeVectorCluster(minSAVClusters, maxSAVClusters, featureCollection);
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
//        Game game = txtToGame.openFile(gameFiles[0].getPath(), gameFiles[1].getPath()); //games.get(0);
//        GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
//        List<Feature> featureList = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);;
////        FeatureListToFeatureClusterList fl2fcl = FeatureListToFeatureClusterList.getInstance();
////        FeatureListToAttitudeVectorClusterList fl2avcl = FeatureListToAttitudeVectorClusterList.getInstance();
//        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
////        int[] featureClusterList = fl2fcl.getClusterIndexList(featureList, featureClusters);
////        int[][] attitudeVectorClusterList = fl2avcl.getClusterIndexList(featureList, attitudeVectorClusters);
//        int[][] dsAttVecClustList = fl2dsavcl.getClusterIndexList(featureList, dispAVClusters, saidAVClusters);
//        StringBuilder nameBuilder = new StringBuilder();
//        nameBuilder.append(gameFiles[0].getPath().substring(0, gameFiles[0].getPath().indexOf("activity_")));
////        nameBuilder.append("kf_");
////        nameBuilder.append(numFeatureClusters);
////        nameBuilder.append("kav_");
////        nameBuilder.append(numAVClusters);
//        nameBuilder.append("Representative");
//        nameBuilder.append(".csv");
//        try(FileWriter fWriter = new FileWriter(nameBuilder.toString()))
//        {
////            fWriter.append("Feature Cluster representatives");
////            fWriter.append('\n');
////            for(int f = 0; f < featureClusters.length; f++)
////            {
////                Feature centroid = (Feature) featureClusters[f].getCentroid();
////                StringBuilder builder = new StringBuilder();
////                builder.append(f);
////                builder.append(",(");
////                builder.append(centroid.getAttitudeDisplayed().getGreedy());
////                builder.append(" ");
////                builder.append(centroid.getAttitudeDisplayed().getPlacate());
////                builder.append(" ");
////                builder.append(centroid.getAttitudeDisplayed().getCooperate());
////                builder.append(" ");
////                builder.append(centroid.getAttitudeDisplayed().getAbsurd());
////                builder.append("),(");
////                builder.append(centroid.getAttitudeSaid().getGreedy());
////                builder.append(" ");
////                builder.append(centroid.getAttitudeSaid().getPlacate());
////                builder.append(" ");
////                builder.append(centroid.getAttitudeSaid().getCooperate());
////                builder.append(" ");
////                builder.append(centroid.getAttitudeSaid().getAbsurd());
////                builder.append("),(");
////                builder.append(centroid.getOtherAttitudeDisplayed().getGreedy());
////                builder.append(" ");
////                builder.append(centroid.getOtherAttitudeDisplayed().getPlacate());
////                builder.append(" ");
////                builder.append(centroid.getOtherAttitudeDisplayed().getCooperate());
////                builder.append(" ");
////                builder.append(centroid.getOtherAttitudeDisplayed().getAbsurd());
////                builder.append("),(");
////                builder.append(centroid.getOtherAttitudeSaid().getGreedy());
////                builder.append(" ");
////                builder.append(centroid.getOtherAttitudeSaid().getPlacate());
////                builder.append(" ");
////                builder.append(centroid.getOtherAttitudeSaid().getCooperate());
////                builder.append(" ");
////                builder.append(centroid.getOtherAttitudeSaid().getAbsurd());
////                builder.append(")");
////                fWriter.append(builder.toString());
////                fWriter.append('\n');
////            }
////            fWriter.append('\n');
////            for(int fcl = 0; fcl < featureClusterList.length; fcl++)
////            {
////                fWriter.append(String.valueOf(featureClusterList[fcl]));
////                fWriter.append('\n');
////            }
////            fWriter.append('\n');
////            fWriter.append("Attitude Vector Cluster representatives");
////            fWriter.append('\n');
////            for(int av = 0; av < attitudeVectorClusters.length; av++)
////            {
////                AttitudeVector centroid = (AttitudeVector) attitudeVectorClusters[av].getCentroid();
////                StringBuilder builder = new StringBuilder();
////                builder.append(av);
////                builder.append(",(");
////                builder.append(centroid.getGreedy());
////                builder.append(" ");
////                builder.append(centroid.getPlacate());
////                builder.append(" ");
////                builder.append(centroid.getCooperate());
////                builder.append(" ");
////                builder.append(centroid.getAbsurd());
////                builder.append(")");
////                fWriter.append(builder.toString());
////                fWriter.append('\n');
////            }
////            fWriter.append('\n');
//            fWriter.append("Displayed Attitude Vector Cluster representatives");
//            fWriter.append('\n');
//            for(int dav = 0; dav < dispAVClusters.length; dav++)
//            {
//                AttitudeVector centroid = (AttitudeVector) dispAVClusters[dav].getCentroid();
//                StringBuilder builder = new StringBuilder();
//                builder.append("d");
//                builder.append(dav);
//                builder.append(",(");
//                builder.append(centroid.getGreedy());
//                builder.append(" ");
//                builder.append(centroid.getPlacate());
//                builder.append(" ");
//                builder.append(centroid.getCooperate());
//                builder.append(" ");
//                builder.append(centroid.getAbsurd());
//                builder.append(")");
//                fWriter.append(builder.toString());
//                fWriter.append('\n');
//            }
//            fWriter.append('\n');
//            fWriter.append("Said Attitude Vector Cluster representatives");
//            fWriter.append('\n');
//            for(int sav = 0; sav < saidAVClusters.length; sav++)
//            {
//                AttitudeVector centroid = (AttitudeVector) saidAVClusters[sav].getCentroid();
//                StringBuilder builder = new StringBuilder();
//                builder.append("s");
//                builder.append(sav);
//                builder.append(",(");
//                builder.append(centroid.getGreedy());
//                builder.append(" ");
//                builder.append(centroid.getPlacate());
//                builder.append(" ");
//                builder.append(centroid.getCooperate());
//                builder.append(" ");
//                builder.append(centroid.getAbsurd());
//                builder.append(")");
//                fWriter.append(builder.toString());
//                fWriter.append('\n');
//            }
//            fWriter.append('\n');
//            for(int avcl = 0; avcl < dsAttVecClustList.length; avcl++)
//            {
//                StringBuilder builder = new StringBuilder();
//                builder.append("(d");
//                builder.append(dsAttVecClustList[avcl][0]);
//                builder.append(" s");
//                builder.append(dsAttVecClustList[avcl][1]);
//                builder.append(" d");
//                builder.append(dsAttVecClustList[avcl][2]);
//                builder.append(" s");
//                builder.append(dsAttVecClustList[avcl][3]);
//                builder.append(")");
//                fWriter.append(builder.toString());
//                fWriter.append('\n');
//            }
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
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
        for(Game game : games)
        {
//            GameToRawFeatureList gameToFeatures = new GameToRawFeatureList(game);
//            List<Features.Feature> gameFeatures = gameToFeatures.generatePlayer1FeatureList();
//            StringBuilder builder = new StringBuilder();
//            builder.append("FeatureData");
//            builder.append(fileNum);
//            builder.append(".csv");
//            String path = builder.toString();
//            try(FileWriter fWriter = new FileWriter(path))
//            {
//                String header = "Greed Displayed,Placate Displayed,Cooperation Displayed,Absurdity Displayed," +
//                        "Greed Said,Placate Said,Cooperation Said,Absurdity Said," +
//                        "Greed Other Did,Placate Other Did,Cooperation Other Did,Absurdity Other Did," +
//                        "Integrity,Deference";
//                fWriter.append(header);
//                fWriter.append('\n');
//                for(int i = 0; i < gameFeatures.size(); i++)
//                {
//                    StringBuilder b = new StringBuilder();
//                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getGreedy());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getPlacate());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getCooperate());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeDisplayed().getAbsurd());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeSaid().getGreedy());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeSaid().getPlacate());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeSaid().getCooperate());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getAttitudeSaid().getAbsurd());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getGreedy());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getPlacate());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getCooperate());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getOtherAttitudeDisplayed().getAbsurd());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getIntegrity());
//                    b.append(",");
//                    b.append(gameFeatures.get(i).getDeference());
//                    fWriter.append(b.toString());
//                    fWriter.append('\n');
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            fileNum++;
        }
    }

    private static void testGameToAutomata()
    {
        GameToAutomata.getInstance().generateAutomaton(null, 2);


        System.err.println("Success!");
    }

}
