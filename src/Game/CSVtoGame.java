package Game;

import java.io.File;

public class CSVtoGame {
    private static CSVtoGame ourInstance = new CSVtoGame();

    public static CSVtoGame getInstance() {
        return ourInstance;
    }

    private CSVtoGame() {
    }

    public boolean openFile(File file)
    {
        return false;
    }
}
