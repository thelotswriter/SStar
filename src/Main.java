import Attitudes.AttitudeVector;
import Automata.GameToAutomata;
import Clustering.*;
import Features.Feature;
import Features.GameToFeatureList;
import Game.Game;
import Game.TXTtoGame;
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

    public static void main(String[] args)
    {
        boolean test = false;
        if(test)
        {
            testGameToAutomata();
        } else
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(null);
            File[] files = fileChooser.getSelectedFiles();
            ArrayList<StrategyTable> tables = new ArrayList<>();
            ArrayList<Game> games = new ArrayList<>();
            for(int f = 0; f < files.length / 2; f++)
            {
                System.out.println(f);
//                if(f == 24)
//                {
//                    System.out.println(f);
//                }
                TXTtoGame txtToGame = new TXTtoGame();
                games.add(txtToGame.openFile(files[f * 2].getPath(), files[f * 2 + 1].getPath()));
            }
            clusterFeaturesAndAttitudeVectors(files, games);
//            for(File file : files)
//            {
//                TXTtoGame txtToGame = new TXTtoGame();
//                games.add(txtToGame.openFile())
////                games.add(TXTtoGame.getInstance().openFile(file.getPath()));
//            }
//            graphFeatures(files, games);

//            games.get(0).getPayoffMatrix().print();
//            System.out.println("=============================");
//            for(Game game : games)
//            {
//                tables.add(GameToTable.getInstance().convertToTable(game, 1));
//            }
//            StrategyTable combinedTable = StrategyTable.merge(tables);
//            combinedTable.print();
////            for(int i = 0; i < tables.size(); i++)
////            {
////                System.out.println("--------------");
////                tables.get(i).print();
////            }
//            StrategyTable greedyStrategy = Greedy.getInstance().generateSpecificStrategy(games.get(0));
//            System.out.println("==================GREEDY=====================");
//            greedyStrategy.print();
////            StrategyTable greedyExpandedStrategy = greedyStrategy.generateResizedTable(1);
////            System.out.println("==================GREEDY EXPANDED=====================");
////            greedyExpandedStrategy.print();
//            StrategyTable frustratedStrategy = Frustrated.getInstance().generateSpecificStrategy(games.get(0));
//            System.out.println("==================FRUSTRATED=====================");
//            frustratedStrategy.print();
////            StrategyTable frustratedExpandedStrategy = frustratedStrategy.generateResizedTable(1);
////            System.out.println("==================FRUSTRATED EXPANDED=====================");
////            frustratedExpandedStrategy.print();
//            StrategyTable protectiveStrategy = Protective.getInstance().generateSpecificStrategy(games.get(0));
//            System.out.println("==================PROTECTIVE=====================");
//            protectiveStrategy.print();
////            StrategyTable protectiveExpandedStrategy = protectiveStrategy.generateResizedTable(1);
////            System.out.println("==================PROTECTIVE EXPANDED=====================");
////            protectiveExpandedStrategy.print();
//            StrategyTable meanStrategy = Mean.getInstance().generateSpecificStrategy(games.get(0));
//            System.out.println("==================MEAN=====================");
//            meanStrategy.print();
////            StrategyTable meanExpandedStrategy = meanStrategy.generateResizedTable(1);
////            System.out.println("==================MEAN EXPANDED=====================");
////            meanExpandedStrategy.print();
//            StrategyTable placateStrategy = Placate.getInstance().generateSpecificStrategy(games.get(0));
//            System.out.println("==================PLACATE=====================");
//            placateStrategy.print();
////            StrategyTable placateExpandedStrategy = placateStrategy.generateResizedTable(1);
////            System.out.println("==================PLACATE EXPANDED=====================");
////            placateExpandedStrategy.print();
//            StrategyTable compromiseStrategy = Compromise.getInstance().generateSpecificStrategy(games.get(0));
//            System.out.println("==================COMPROMISE=====================");
//            compromiseStrategy.print();
////            StrategyTable compromiseExpandedStrategy = compromiseStrategy.generateResizedTable(1);
////            System.out.println("==================COMPROMISE EXPANDED=====================");
////            compromiseExpandedStrategy.print();
//            System.out.println("==========================================================");
//            System.out.println("=================GENERAL STRATEGIES=======================");
//            System.out.println("==========================================================");
//            ArrayList<Attitude> attitudes = new ArrayList<>();
//            attitudes.add(Greedy.getInstance());
////            attitudes.add(Mean.getInstance());
//            attitudes.add(Placate.getInstance());
////            attitudes.add(Compromise.getInstance());
//            StrategyTable generalStrat = GeneralizeStrategy.getInstance().convertToAttitudeStrategy(games.get(0), combinedTable, attitudes);
//            generalStrat.print();
//
//            Collection<StrategyTable> generalizedStrategies = new ArrayList<>();
//            for(int i = 0; i < tables.size(); i++)
//            {
//                generalizedStrategies.add(GeneralizeStrategy.getInstance().convertToAttitudeStrategy(games.get(i), tables.get(i), attitudes));
//            }
//            Collection<StrategyTable> learnedStrategies = null;
//            try {
//                learnedStrategies = Attitudes.AttitudeStrategyLearner.getInstance().learnStrategies(3, generalizedStrategies, 100);
//                System.out.println("==========================================================");
//                System.out.println("=================LEARNED STRATEGIES=======================");
//                System.out.println("==========================================================");
//                for (StrategyTable learnedStrategy : learnedStrategies)
//                {
//                    learnedStrategy.print();
//                    System.out.println("==========================================================");
//                }
//                System.out.println("Success!");
//            } catch (NotEnoughOptionsException e) {
//                System.err.println();
//            }

        }
    }

    private static void clusterFeaturesAndAttitudeVectors(File[] files, ArrayList<Game> games)
    {
        Collection<Collection<Feature>> featureCollection = new ArrayList<>();
        for(int g = 0; g < games.size(); g++)
        {
            System.out.println(g);
            GameToFeatureList gameToFeatureList = new GameToFeatureList(games.get(g));
            Collection<Feature> fCollection = gameToFeatureList.generateFeatureList(5, 0.9, 0.5);
            featureCollection.add(fCollection);
        }
        FeaturesToFeatureClusters featuresToFeatureClusters = FeaturesToFeatureClusters.getInstance();
        int numFeatureClusters = 10;
        int numAVClusters = 10;
        DistantDataCluster[] featureClusters = featuresToFeatureClusters.getFeatureCluster(numFeatureClusters, featureCollection);
        FeaturesToAttitudeVectorClusters featuresToAttitudeVectorClusters = FeaturesToAttitudeVectorClusters.getInstance();
        DistantDataCluster[] attitudeVectorClusters = featuresToAttitudeVectorClusters.getAttitudeVectorCluster(numAVClusters, featureCollection);
        Game game = games.get(0);
        GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
        List<Feature> featureList = gameToFeatureList.generateFeatureList(5, 0.9, 0.5);;
        FeatureListToFeatureClusterList fl2fcl = FeatureListToFeatureClusterList.getInstance();
        FeatureListToAttitudeVectorClusterList fl2avcl = FeatureListToAttitudeVectorClusterList.getInstance();
        int[] featureClusterList = fl2fcl.getClusterIndexList(featureList, featureClusters);
        int[][] attitudeVectorClusterList = fl2avcl.getClusterIndexList(featureList, attitudeVectorClusters);
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(files[0].getPath().substring(0, files[0].getPath().indexOf("activity_")));
        nameBuilder.append("kf_");
        nameBuilder.append(numFeatureClusters);
        nameBuilder.append("kav_");
        nameBuilder.append(numAVClusters);
        nameBuilder.append(".csv");
        try(FileWriter fWriter = new FileWriter(nameBuilder.toString()))
        {
            fWriter.append("Feature Cluster representatives");
            fWriter.append('\n');
            for(int f = 0; f < featureClusters.length; f++)
            {
                Feature centroid = (Feature) featureClusters[f].getCentroid();
                StringBuilder builder = new StringBuilder();
                builder.append(f);
                builder.append(",(");
                builder.append(centroid.getAttitudeDisplayed().getGreedy());
                builder.append(" ");
                builder.append(centroid.getAttitudeDisplayed().getPlacate());
                builder.append(" ");
                builder.append(centroid.getAttitudeDisplayed().getCooperate());
                builder.append(" ");
                builder.append(centroid.getAttitudeDisplayed().getAbsurd());
                builder.append("),(");
                builder.append(centroid.getAttitudeSaid().getGreedy());
                builder.append(" ");
                builder.append(centroid.getAttitudeSaid().getPlacate());
                builder.append(" ");
                builder.append(centroid.getAttitudeSaid().getCooperate());
                builder.append(" ");
                builder.append(centroid.getAttitudeSaid().getAbsurd());
                builder.append("),(");
                builder.append(centroid.getOtherAttitudeDisplayed().getGreedy());
                builder.append(" ");
                builder.append(centroid.getOtherAttitudeDisplayed().getPlacate());
                builder.append(" ");
                builder.append(centroid.getOtherAttitudeDisplayed().getCooperate());
                builder.append(" ");
                builder.append(centroid.getOtherAttitudeDisplayed().getAbsurd());
                builder.append("),(");
                builder.append(centroid.getOtherAttitudeSaid().getGreedy());
                builder.append(" ");
                builder.append(centroid.getOtherAttitudeSaid().getPlacate());
                builder.append(" ");
                builder.append(centroid.getOtherAttitudeSaid().getCooperate());
                builder.append(" ");
                builder.append(centroid.getOtherAttitudeSaid().getAbsurd());
                builder.append(")");
                fWriter.append(builder.toString());
                fWriter.append('\n');
            }
            fWriter.append('\n');
            for(int fcl = 0; fcl < featureClusterList.length; fcl++)
            {
                fWriter.append(String.valueOf(featureClusterList[fcl]));
                fWriter.append('\n');
            }
            fWriter.append('\n');
            fWriter.append("Attitude Vector Cluster representatives");
            fWriter.append('\n');
            for(int av = 0; av < attitudeVectorClusters.length; av++)
            {
                AttitudeVector centroid = (AttitudeVector) attitudeVectorClusters[av].getCentroid();
                StringBuilder builder = new StringBuilder();
                builder.append(av);
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
            for(int avcl = 0; avcl < attitudeVectorClusterList.length; avcl++)
            {
                StringBuilder builder = new StringBuilder();
                builder.append("(");
                builder.append(attitudeVectorClusterList[avcl][0]);
                builder.append(" ");
                builder.append(attitudeVectorClusterList[avcl][1]);
                builder.append(" ");
                builder.append(attitudeVectorClusterList[avcl][2]);
                builder.append(" ");
                builder.append(attitudeVectorClusterList[avcl][3]);
                builder.append(")");
                fWriter.append(builder.toString());
                fWriter.append('\n');
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Done");
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
