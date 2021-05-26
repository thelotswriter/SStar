package Game;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TXTfileConverter
{

    private final String TARGET_DIR = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults\\Extra Results";
    private final String SOURCE_DIR1 = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults\\Extra Results\\Logs1";
    private final String SOURCE_DIR2 = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults\\Extra Results\\Logs2";

    public TXTfileConverter()
    {

    }

    public void convertFiles()
    {
        convert1Files();
        convert2Files();
    }

    private void convert1Files()
    {
        File folder = new File(SOURCE_DIR1);
        for(File file : folder.listFiles())
        {
            convert1File(file);
        }
    }

    private void convert2Files()
    {
        File folder = new File(SOURCE_DIR2);
        for(File file : folder.listFiles())
        {
            convert2File(file);
        }
    }

    private void convert1File(File file)
    {
        List<String[]> convertedFile = new ArrayList<>();
        String name = file.getPath().substring(file.getPath().indexOf("_") + 1);
        try(BufferedReader buffy = new BufferedReader(new FileReader(file)))
        {
            String line = buffy.readLine();
            while((line = buffy.readLine()) != null)
            {
                String[] cells = line.split("\t");
                if(cells.length == 14)
                {
                    String[] keyCells = new String[6];
                    keyCells[0] = cells[9];
                    keyCells[1] = cells[10];
                    keyCells[2] = cells[11];
                    keyCells[3] = cells[12];
                    keyCells[4] = cells[7];
                    keyCells[5] = cells[8];
                    convertedFile.add(keyCells);
                }
            }
        } catch (FileNotFoundException e)
        {
            System.err.print("Oops, you bwoke it");
        } catch (IOException e)
        {
            System.err.print("Oops, you bwoke it");
        }
        writeFiles(name, convertedFile);
    }

    private void convert2File(File file)
    {
        List<String[]> convertedFile = new ArrayList<>();
        String name = file.getPath().substring(file.getPath().indexOf("_") + 1);
        try(BufferedReader buffy = new BufferedReader(new FileReader(file)))
        {
            String line = buffy.readLine();
            while((line = buffy.readLine()) != null)
            {
                String[] cells = line.split("\t");
                if(cells.length == 15)
                {
                    String[] keyCells = new String[6];
                    keyCells[0] = cells[9];
                    keyCells[1] = cells[10];
                    keyCells[2] = cells[11];
                    keyCells[3] = cells[12];
                    keyCells[4] = cells[7];
                    keyCells[5] = cells[8];
                    convertedFile.add(keyCells);
                }
            }
        } catch (FileNotFoundException e)
        {
            System.err.print("Oops, you bwoke it");
        } catch (IOException e)
        {
            System.err.print("Oops, you bwoke it");
        }
        writeFiles(name, convertedFile);
    }

    private void writeFiles(String name, List<String[]> converted)
    {
        writeActivityFile(name, converted);
        writeMessageFile(name, converted);
    }

    private void writeActivityFile(String name, List<String[]> converted)
    {
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(TARGET_DIR);
        fileBuilder.append("\\");
        fileBuilder.append(name.substring(0, name.indexOf(".txt")));
        fileBuilder.append("_activity_0.txt");
        try(FileWriter fileWriter = new FileWriter(fileBuilder.toString()))
        {
            for(String[] convertedLine : converted)
            {
                StringBuilder b = new StringBuilder();
                b.append(convertedLine[0]);
                b.append('\t');
                b.append(convertedLine[1]);
                b.append('\t');
                b.append(convertedLine[2]);
                b.append('\t');
                b.append(convertedLine[3]);
                b.append('\n');
                fileWriter.append(b.toString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void writeMessageFile(String name, List<String[]> converted)
    {
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(TARGET_DIR);
        fileBuilder.append("\\");
        fileBuilder.append(name.substring(0, name.indexOf(".txt")));
        fileBuilder.append("_messages_0.txt");
        try(FileWriter fileWriter = new FileWriter(fileBuilder.toString()))
        {
            for(String[] convertedLine : converted)
            {
                StringBuilder b = new StringBuilder();
                b.append(convertMessageString(convertedLine[4]));
                b.append("$");
                b.append('\n');
                b.append(convertMessageString(convertedLine[5]));
                b.append("$");
                b.append('\n');
                fileWriter.append(b.toString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String convertMessageString(String sourceString)
    {
        StringBuilder convertedBuilder = new StringBuilder();
        String[] messages = sourceString.split("\\.");
        if(messages.length == 0)
        {
            messages = new String[1];
            messages[0] = sourceString;
        } else
        {
            System.out.println("NON-ZERO!");
            for(String message : messages)
            {
                System.out.println(message);
            }
        }
        for(String message : messages)
        {
            if(message.contains("Let's always play"))
            {
                convertedBuilder.append("15 ");
                String s1 = message.substring(message.indexOf("always play"));
                convertedBuilder.append(convertActions(s1.substring(12)));
                convertedBuilder.append(';');
            } else if(message.contains("Let's alternate between"))
            {
                convertedBuilder.append("18 ");
                String s1 = message.substring(message.indexOf("between"));
                convertedBuilder.append(convertActions(s1.substring(8)));
                convertedBuilder.append(" ");
                convertedBuilder.append(convertActions(s1.substring(12)));
                convertedBuilder.append(';');
            } else if(message.contains("This round, let's play"))
            {
                convertedBuilder.append("16 ");
                String s1 = message.substring(message.indexOf(", let's play"));
                convertedBuilder.append(convertActions(s1.substring(13)));
                convertedBuilder.append(';');
            } else if(message.contains("Don't play"))
            {
                convertedBuilder.append("17 ");
                String s1 = message.substring(message.indexOf("t play"));
                convertedBuilder.append(convertActions(s1.substring(7,8)));
                convertedBuilder.append(';');
            }
        }
        return convertedBuilder.toString();
    }

    private String convertActions(String original)
    {
        StringBuilder b = new StringBuilder();
        if(original.length() == 1)
        {
            if(original.equalsIgnoreCase("X"))
            {
                b.append("A");
            } else if(original.equalsIgnoreCase("Y"))
            {
                b.append("B");
            } else if(original.equalsIgnoreCase("Z"))
            {
                b.append("C");
            } else
            {
                b.append(original.toUpperCase());
            }
        } else
        {
            b.append(original.substring(0, 1).toUpperCase());
            String c2 = original.substring(2,3);
            if(c2.equalsIgnoreCase("X"))
            {
                b.append("A");
            } else if(c2.equalsIgnoreCase("Y"))
            {
                b.append("B");
            } else if(c2.equalsIgnoreCase("Z"))
            {
                b.append("C");
            } else
            {
                b.append(c2.toUpperCase());
            }
        }
        return b.toString();
    }

}
