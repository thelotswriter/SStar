import Automata.GameToAutomata;
import Game.CSVtoGame;
import Game.Game;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main
{

    public static void main(String[] args)
    {
        boolean test = true;
        if(test)
        {
            testGameToAutomata();
        } else
        {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(null);
            Game testGame = CSVtoGame.getInstance().openFile(fileChooser.getSelectedFile().getPath());
            System.out.println("Success!");
        }
    }

    private static void testGameToAutomata()
    {
        GameToAutomata.getInstance().generateAutomaton(null, 2);


        System.err.println("Success!");
    }

}
