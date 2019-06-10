import Attitudes.Frustrated;
import Attitudes.Greedy;
import Automata.GameToAutomata;
import Game.CSVtoGame;
import Game.Game;
import StrategyTables.GameToTable;
import StrategyTables.StrategyTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;

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
                tables.add(GameToTable.getInstance().convertToTable(game, 2));
            }
            StrategyTable combinedTable = StrategyTable.merge(tables);
            combinedTable.print();
//            for(int i = 0; i < tables.size(); i++)
//            {
//                System.out.println("--------------");
//                tables.get(i).print();
//            }
            StrategyTable greedyStrategy = Greedy.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("=======================================");
            greedyStrategy.print();
            StrategyTable frustratedStrategy = Frustrated.getInstance().generateSpecificStrategy(games.get(0));
            System.out.println("=======================================");
            frustratedStrategy.print();
            System.out.println("Success!");
        }
    }

    private static void testGameToAutomata()
    {
        GameToAutomata.getInstance().generateAutomaton(null, 2);


        System.err.println("Success!");
    }

}
