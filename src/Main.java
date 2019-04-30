import Game.CSVtoGame;
import Game.Game;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main
{

    public static void main(String[] args)
    {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(null);
        Game testGame = CSVtoGame.getInstance().openFile(fileChooser.getSelectedFile().getPath());
        System.out.println("Success!");
    }
}
