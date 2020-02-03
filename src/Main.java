import Attitudes.*;
import Automata.GameToAutomata;
import Exceptions.NotEnoughOptionsException;
import Game.CSVtoGame;
import Game.Game;
import StrategyTables.GameToTable;
import StrategyTables.GeneralizeStrategy;
import StrategyTables.StrategyTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(null);
            File[] files = fileChooser.getSelectedFiles();
            ArrayList<StrategyTable> tables = new ArrayList<>();
            ArrayList<Game> games = new ArrayList<>();
            for(File file : files)
            {
                games.add(CSVtoGame.getInstance().openFile(file.getPath()));
            }
            graphFeatures(games);

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
//                learnedStrategies = AttitudeStrategyLearner.getInstance().learnStrategies(3, generalizedStrategies, 100);
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

    private static void graphFeatures(ArrayList<Game> games)
    {
        int fileNum = 0;
        boolean lastFound = false;
        while(!lastFound)
        {
            fileNum++;
            StringBuilder builder = new StringBuilder();
            builder.append("FeatureData");
            builder.append(fileNum);
            builder.append(".csv");
            File file = new File(builder.toString());
            lastFound = !file.exists();
        }
        for(Game game : games)
        {
            GameToRawFeatureList gameToFeatures = new GameToRawFeatureList(game);
            List<Feature> gameFeatures = gameToFeatures.generatePlayer1FeatureList();
            StringBuilder builder = new StringBuilder();
            builder.append("FeatureData");
            builder.append(fileNum);
            builder.append(".csv");
            String path = builder.toString();
            try(FileWriter fWriter = new FileWriter(path))
            {
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
