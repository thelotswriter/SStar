import Attitudes.*;
import Automata.GameToAutomata;
import Game.CSVtoGame;
import Game.Game;
import StrategyTables.GameToTable;
import StrategyTables.GeneralizeStrategy;
import StrategyTables.StrategyTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
            games.get(0).getPayoffMatrix().print();
            System.out.println("=============================");
            for(Game game : games)
            {
                tables.add(GameToTable.getInstance().convertToTable(game, 1));
            }
            StrategyTable combinedTable = StrategyTable.merge(tables);
            combinedTable.print();
//            for(int i = 0; i < tables.size(); i++)
//            {
//                System.out.println("--------------");
//                tables.get(i).print();
//            }
            StrategyTable greedyStrategy = Greedy.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("==================GREEDY=====================");
            greedyStrategy.print();
//            StrategyTable greedyExpandedStrategy = greedyStrategy.generateResizedTable(1);
//            System.out.println("==================GREEDY EXPANDED=====================");
//            greedyExpandedStrategy.print();
            StrategyTable frustratedStrategy = Frustrated.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("==================FRUSTRATED=====================");
            frustratedStrategy.print();
//            StrategyTable frustratedExpandedStrategy = frustratedStrategy.generateResizedTable(1);
//            System.out.println("==================FRUSTRATED EXPANDED=====================");
//            frustratedExpandedStrategy.print();
            StrategyTable protectiveStrategy = Protective.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("==================PROTECTIVE=====================");
            protectiveStrategy.print();
//            StrategyTable protectiveExpandedStrategy = protectiveStrategy.generateResizedTable(1);
//            System.out.println("==================PROTECTIVE EXPANDED=====================");
//            protectiveExpandedStrategy.print();
            StrategyTable meanStrategy = Mean.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("==================MEAN=====================");
            meanStrategy.print();
//            StrategyTable meanExpandedStrategy = meanStrategy.generateResizedTable(1);
//            System.out.println("==================MEAN EXPANDED=====================");
//            meanExpandedStrategy.print();
            StrategyTable placateStrategy = Placate.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("==================PLACATE=====================");
            placateStrategy.print();
//            StrategyTable placateExpandedStrategy = placateStrategy.generateResizedTable(1);
//            System.out.println("==================PLACATE EXPANDED=====================");
//            placateExpandedStrategy.print();
            StrategyTable compromiseStrategy = Compromise.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("==================COMPROMISE=====================");
            compromiseStrategy.print();
//            StrategyTable compromiseExpandedStrategy = compromiseStrategy.generateResizedTable(1);
//            System.out.println("==================COMPROMISE EXPANDED=====================");
//            compromiseExpandedStrategy.print();
            System.out.println("==========================================================");
            System.out.println("=================GENERAL STRATEGIES=======================");
            System.out.println("==========================================================");
            Collection<Attitude> attitudes = new ArrayList<>();
            attitudes.add(Greedy.getInstance());
//            attitudes.add(Mean.getInstance());
            attitudes.add(Placate.getInstance());
//            attitudes.add(Compromise.getInstance());
            StrategyTable generalStrat = GeneralizeStrategy.getInstance().convertToAttitudeStrategy(games.get(0), combinedTable, attitudes);
            generalStrat.print();
            System.out.println("Success!");
        }
    }

    private static void testGameToAutomata()
    {
        GameToAutomata.getInstance().generateAutomaton(null, 2);


        System.err.println("Success!");
    }

}
