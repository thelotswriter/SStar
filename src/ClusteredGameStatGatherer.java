import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClusteredGameStatGatherer
{

    private final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";

    private int nDispAttitudes;
    private int nSaidAttitudes;

    private List<String> files;
    private List<int[]> aDispTallies;
    private List<int[]> aSaidTallies;
    private List<int[]> aODispTallies;
    private List<int[]> aOSaidTallies;
    private int[] aDisplayedTotalTally;
    private int[] aSaidTotalTally;
    private int[] aODisplayedTotalTally;
    private int[] aOSaidTotalTally;

    private List<int[][]> reactions;
    private List<int[][]> otherReactions;

    public ClusteredGameStatGatherer()
    {
        nDispAttitudes = -1;
        nSaidAttitudes = -1;
        files = new ArrayList<>();
        aDispTallies = new ArrayList<>();
        aSaidTallies = new ArrayList<>();
        aODispTallies = new ArrayList<>();
        aOSaidTallies = new ArrayList<>();
        reactions = new ArrayList<>();
        otherReactions = new ArrayList<>();
        readFiles();
    }

    private void readFiles()
    {
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(null);
        File[] files = fileChooser.getSelectedFiles();
        for(File file : files)
        {
            readFile(file);
        }
    }

    private void readFile(File file)
    {
        if(nDispAttitudes < 0)
        {
            readNumDispSaid(file);
        }
        int[] aDT = new int[nDispAttitudes];
        int[] aST = new int[nSaidAttitudes];
        int[] aODT = new int[nDispAttitudes];
        int[] aOST = new int[nSaidAttitudes];
        try(BufferedReader buffy = new BufferedReader(new FileReader(file)))
        {
            int[][] reacts = new int[nDispAttitudes][nDispAttitudes];
            int[][] otherReacts = new int[nDispAttitudes][nDispAttitudes];
            int prevOtherAct = -1;
            int prevAct = -1;
            String line;
            while ((line = buffy.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) == '(')
                {
                    String[] splitLine = line.split(" ");
                    int aDisp = Integer.parseInt(splitLine[0].substring(2));
                    int aSaid = Integer.parseInt(splitLine[1].substring(1));
                    int aODisp = Integer.parseInt(splitLine[2].substring(1));
                    int aOSaid = Integer.parseInt(splitLine[3].substring(1, splitLine[3].length() - 1));
                    aDT[aDisp]++;
                    aST[aSaid]++;
                    aODT[aODisp]++;
                    aOST[aOSaid]++;
                    if(prevOtherAct >= 0)
                    {
                        reacts[prevOtherAct][aDisp]++;
                        otherReacts[prevAct][aODisp]++;
                    }
                    prevOtherAct = aODisp;
                    prevAct = aDisp;
                }
            }
            reactions.add(reacts);
            otherReactions.add(otherReacts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < aDisplayedTotalTally.length; i++)
        {
            aDisplayedTotalTally[i] += aDT[i];
        }
        aDispTallies.add(aDT);
        for (int i = 0; i < aSaidTotalTally.length; i++)
        {
            aSaidTotalTally[i] += aST[i];
        }
        aSaidTallies.add(aST);
        for (int i = 0; i < aODisplayedTotalTally.length; i++)
        {
            aODisplayedTotalTally[i] += aODT[i];
        }
        aODispTallies.add(aODT);
        for(int i = 0; i < aOSaidTotalTally.length; i++)
        {
            aOSaidTotalTally[i] += aOST[i];
        }
        aOSaidTallies.add(aOST);
        files.add(file.getName());
    }

    private void readNumDispSaid(File file)
    {
        try(BufferedReader buffy = new BufferedReader(new FileReader(file)))
        {
            int nDisp = 0;
            String line;
            while ((line = buffy.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) == 'd')
                {
                    nDisp++;
                } else if(nDisp > 0)
                {
                    break;
                }
            }
            int nSaid = 0;
            while ((line = buffy.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) == 's')
                {
                    nSaid++;
                } else if(nSaid > 0)
                {
                    break;
                }
            }
            nDispAttitudes = nDisp;
            nSaidAttitudes = nSaid;
            aDisplayedTotalTally = new int[nDispAttitudes];
            aSaidTotalTally = new int[nSaidAttitudes];
            aODisplayedTotalTally = new int[nDispAttitudes];
            aOSaidTotalTally = new int[nSaidAttitudes];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printResults()
    {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(dir + "\\Cluster List Results\\Stats.csv");
        try(FileWriter fWriter = new FileWriter(nameBuilder.toString()))
        {
            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append("Name");
            for(int d = 0; d < nDispAttitudes; d++)
            {
                titleBuilder.append(",Disp ");
                titleBuilder.append(d);
            }
            for(int s = 0; s < nSaidAttitudes; s++)
            {
                titleBuilder.append(",Said ");
                titleBuilder.append(s);
            }
            for(int d = 0; d < nDispAttitudes; d++)
            {
                titleBuilder.append(",ODisp ");
                titleBuilder.append(d);
            }
            for(int s = 0; s < nSaidAttitudes; s++)
            {
                titleBuilder.append(",OSaid ");
                titleBuilder.append(s);
            }
            titleBuilder.append('\n');
            titleBuilder.append("Total");
            for(int d = 0; d < nDispAttitudes; d++)
            {
                titleBuilder.append(',');
                titleBuilder.append(aDisplayedTotalTally[d]);
            }
            for(int s = 0; s < nSaidAttitudes; s++)
            {
                titleBuilder.append(',');
                titleBuilder.append(aSaidTotalTally[s]);
            }
            for(int d = 0; d < nDispAttitudes; d++)
            {
                titleBuilder.append(',');
                titleBuilder.append(aODisplayedTotalTally[d]);
            }
            for(int s = 0; s < nSaidAttitudes; s++)
            {
                titleBuilder.append(',');
                titleBuilder.append(aOSaidTotalTally[s]);
            }
            for(int i = 0; i < nDispAttitudes; i++)
            {
                titleBuilder.append(",Other did: ");
                titleBuilder.append(i);
            }
            for(int i = 0; i < nDispAttitudes; i++)
            {
                titleBuilder.append(",Player did: ");
                titleBuilder.append(i);
            }
            titleBuilder.append('\n');
            fWriter.append(titleBuilder.toString());
            for(int i = 0; i < files.size(); i++)
            {
                StringBuilder b = new StringBuilder();
                b.append(files.get(i));
                for(int d = 0; d < nDispAttitudes; d++)
                {
                    b.append(',');
                    b.append(aDispTallies.get(i)[d]);
                }
                for(int s = 0; s < nSaidAttitudes; s++)
                {
                    b.append(',');
                    b.append(aSaidTallies.get(i)[s]);
                }
                for(int d = 0; d < nDispAttitudes; d++)
                {
                    b.append(',');
                    b.append(aODispTallies.get(i)[d]);
                }
                for(int s = 0; s < nSaidAttitudes; s++)
                {
                    b.append(',');
                    b.append(aOSaidTallies.get(i)[s]);
                }
                for(int j = 0; j < nDispAttitudes; j++)
                {
                    b.append(',');
                    int highestCount = 0;
                    int mostCommonReaction = -1;
                    for(int k = 0; k < nDispAttitudes; k++)
                    {
                        int count = reactions.get(i)[j][k];
                        if(count > highestCount)
                        {
                            highestCount = count;
                            mostCommonReaction = k;
                        }
                    }
                    b.append(mostCommonReaction);
                }
                for(int j = 0; j < nDispAttitudes; j++)
                {
                    b.append(',');
                    int highestCount = 0;
                    int mostCommonReaction = -1;
                    for(int k = 0; k < nDispAttitudes; k++)
                    {
                        int count = otherReactions.get(i)[j][k];
                        if(count > highestCount)
                        {
                            highestCount = count;
                            mostCommonReaction = k;
                        }
                    }
                    b.append(mostCommonReaction);
                }
                b.append('\n');
                fWriter.append(b.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
