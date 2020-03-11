import Automata.GameToAutomata;
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
                TXTtoGame txtToGame = new TXTtoGame();
                games.add(txtToGame.openFile(files[f * 2].getPath(), files[f * 2 + 1].getPath()));
            }
//            for(File file : files)
//            {
//                TXTtoGame txtToGame = new TXTtoGame();
//                games.add(txtToGame.openFile())
////                games.add(TXTtoGame.getInstance().openFile(file.getPath()));
//            }
            graphFeatures(files, games);

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
