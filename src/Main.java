import Attitudes.ActionAttitudeConverterTest;
import Attitudes.Attitude;
import Attitudes.AttitudeVector;
import Automata.GameToAutomata;
import Clustering.*;
import Features.Feature;
import Features.GameToFeatureList;
import Game.Game;
import Game.TXTtoGame;
import Game.TXTfileConverter;
import StrategyAutomata.*;
import StrategyTables.StrategyTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Main
{

    private static final String LOAD_MENU = "Where do you want your clusters from?\n" +
            "1 - Restore from previous\n" +
            "2 - Upload game files";
    private static final String MENU = "What would you like to do?\n" +
            "0 - Quit\n" +
            "1 - Cluster game files\n" +
            "2 - Generate mega automaton";

    private static final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";
    private static int nNeighbors = 1;
    private static double pPower = 0.6;
    private static double discount = 0.5;

    private static DistantDataCluster[] dispAVClusters;
    private static DistantDataCluster[] saidAVClusters;

    public static void main(String[] args)
    {
        Scanner keys = new Scanner(System.in);
        boolean loaded = false;
        while (!loaded)
        {
            System.out.println(LOAD_MENU);
            int choice = keys.nextInt();
            switch (choice)
            {
                case 1:
                {
                    loaded = true;
                    break;
                } case 2:
                {
                    JFileChooser fileChooser = new JFileChooser(dir);
                    fileChooser.setMultiSelectionEnabled(true);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    File[] files = fileChooser.getSelectedFiles();
                    System.out.println(files[0].getPath());
                    ArrayList<StrategyTable> tables = new ArrayList<>();
                    ArrayList<Game> games = new ArrayList<>();
                    for(int f = 0; f < files.length / 2; f++)
                    {
                        TXTtoGame txtToGame = new TXTtoGame();
                        games.add(txtToGame.openFile(files[f * 2].getPath(), files[f * 2 + 1].getPath()));
                    }
//                    clusterAttitudeVectors(files, games);
                    // =============START=============
                    List<List<Feature>> featureCollection = new ArrayList<>();
                    for(int g = 0; g < games.size(); g++)
                    {
                        GameToFeatureList gameToFeatureList = new GameToFeatureList(games.get(g));
                        List<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
                        featureCollection.add(fCollection);
                    }
                    int maxDAVClusters = 8;
                    int minDAVClusters = 4;
                    int maxSAVClusters = 8;
                    int minSAVClusters = 4;
                    FeatList2DispAttVecClusts featList2DispAttVecClusts = FeatList2DispAttVecClusts.getInstance();
                    FeatList2SaidAttVecClusts featList2SaidAttVecClusts = FeatList2SaidAttVecClusts.getInstance();
                    dispAVClusters = featList2DispAttVecClusts.getDisplayedAttitudeVectorCluster(minDAVClusters, maxDAVClusters, featureCollection);
                    saidAVClusters = featList2SaidAttVecClusts.getSaidAttitudeVectorCluster(minSAVClusters, maxSAVClusters, featureCollection);
                    // ==============END================
                    loaded = true;
                    break;
                } case 3:
                {
                    ClusteredGameStatGatherer cgsg = new ClusteredGameStatGatherer();
                    cgsg.printResults();
                    break;
                } case 4:
                {
                    MegaAutomatonTest megaAutomatonTest = new MegaAutomatonTest(0.9);
                    megaAutomatonTest.run();
                    break;
                } case 5:
                {
                    ActionAttitudeConverterTest aacTest = new ActionAttitudeConverterTest();
                    aacTest.run();
                    break;
                } case 6:
                {
                    System.out.println("Enter training percentage:");
                    double training = keys.nextDouble();
                    System.out.println("Enter memory length:");
                    int mem = keys.nextInt();
                    System.out.println("Enter number of trees:");
                    int trees = keys.nextInt();
                    SplitAutomataTest splitAutomataTest = new SplitAutomataTest(training, mem, trees);
                    splitAutomataTest.run(1, true);
                    break;
                }
                case 7:
                {
//                    System.out.println("What memory length?");
//                    int mem = keys.nextInt();
//                    System.out.println("How many automata?");
//                    int trees = keys.nextInt();
                    int maxMem = 4;
                    int maxTrees = 1;
                    double[][][][] allAvgResults = new double[maxMem + 1][maxTrees + 1][][];
                    for(int m = 1; m <= maxMem; m++)
                    {
                        for(int t = 1; t <= maxTrees; t++)
                        {
                            System.out.print("========M");;
                            System.out.print(m);
                            System.out.print("T");
                            System.out.print(t);
                            System.out.println("===============");
                            List<double[][]> results = new ArrayList<>();
                            for(int i = 0; i < 200; i++)
                            {
                                System.out.print(i);
                                System.out.print(" ");
                                boolean read = true;
                                while(read)
                                {
                                    try
                                    {
                                        SplitAutomataTest splitAutomataTest = new SplitAutomataTest(0.9, m, t);
                                        double[][] result = splitAutomataTest.run(i + 1, false);
                                        results.add(result);
                                        read = false;
                                    } catch(NullPointerException e)
                                    {
                                        read = true;
                                    }

                                }
                            }
                            System.out.println();
                            double[][] avgResults = new double[7][14];
                            for(double[][] result : results)
                            {
                                for(int r = 0; r < result.length; r++)
                                {
                                    for(int c = 0; c < result[r].length; c++)
                                    {
                                        avgResults[r][c] += result[r][c] / ((double) results.size());
                                    }
                                }
                            }
                            allAvgResults[m][t] = avgResults;
                            publishSplitAutomataResults(results, m, t);
                        }
                    }
                    publishAllAvgSplitResults(allAvgResults);
                    break;
                } case 8:
                {
                    int[] memLengths = new int[4];
                    memLengths[0] = 1;
                    memLengths[1] = 2;
                    memLengths[2] = 4;
                    memLengths[3] = 8;
                    System.out.print("Train column (y/n)? ");
                    String trCol = keys.next();
                    System.out.print("Split (y/n): ");
                    String split = keys.next();
                    List<List<double[][]>> allAvgs = new ArrayList<>();
                    List<List<List<int[][]>>> allEnums = new ArrayList<>();
                    if(!trCol.equalsIgnoreCase("y"))
                    {
                        if(!split.equalsIgnoreCase("y"))
                        {
                            for(int m = 0; m < memLengths.length; m++)
                            {
                                List<double[][]> currentMemAvg = new ArrayList<>();
                                List<List<int[][]>> currentMemEnum = new ArrayList<>();
                                for(int i = 0; i < 200; i++)
                                {
                                    PlayerTwoTest playerTwoTest = new PlayerTwoTest(memLengths[m]);
                                    double[][] avgResults = playerTwoTest.run();
                                    List<int[][]> enumResults = playerTwoTest.getEnumeratedPredictions();
                                    currentMemAvg.add(avgResults);
                                    currentMemEnum.add(enumResults);
                                    System.out.print(i);
                                    System.out.print(" ");
                                }
                                allAvgs.add(currentMemAvg);
                                allEnums.add(currentMemEnum);
                                System.out.println();
                            }
                        } else
                        {
                            for(int m = 0; m < memLengths.length; m++)
                            {
                                List<double[][]> currentMemAvg = new ArrayList<>();
                                List<List<int[][]>> currentMemEnum = new ArrayList<>();
                                for(int i = 0; i < 200; i++)
                                {
                                    PlayerTwoSplitTest playerTwoSplitTest = new PlayerTwoSplitTest(memLengths[m]);
                                    double[][] avgResults = playerTwoSplitTest.run();
                                    List<int[][]> enumResults = playerTwoSplitTest.getEnumeratedPredictions();
                                    currentMemAvg.add(avgResults);
                                    currentMemEnum.add(enumResults);
                                    System.out.print(i);
                                    System.out.print(" ");
                                }
                                allAvgs.add(currentMemAvg);
                                allEnums.add(currentMemEnum);
                                System.out.println();
                            }
                        }
                    } else
                    {
                        if(!split.equalsIgnoreCase("y"))
                        {
                            for(int m = 0; m < memLengths.length; m++)
                            {
                                List<double[][]> currentMemAvg = new ArrayList<>();
                                List<List<int[][]>> currentMemEnum = new ArrayList<>();
                                for(int i = 0; i < 200; i++)
                                {
                                    FinalTest finalTest = new FinalTest(memLengths[m]);
                                    double[][] avgResults = finalTest.run();
                                    List<int[][]> enumResults = finalTest.getEnumeratedPredictions();
                                    currentMemAvg.add(avgResults);
                                    currentMemEnum.add(enumResults);
                                    System.out.print(i);
                                    System.out.print(" ");
                                }
                                allAvgs.add(currentMemAvg);
                                allEnums.add(currentMemEnum);
                                System.out.println();
                            }
                        } else
                        {
                            for(int m = 0; m < memLengths.length; m++)
                            {
                                List<double[][]> currentMemAvg = new ArrayList<>();
                                List<List<int[][]>> currentMemEnum = new ArrayList<>();
                                for(int i = 0; i < 200; i++)
                                {
                                    FinalSplitTest finalSplitTest = new FinalSplitTest(memLengths[m]);
                                    double[][] avgResults = finalSplitTest.run();
                                    List<int[][]> enumResults = finalSplitTest.getEnumeratedPredictions();
                                    currentMemAvg.add(avgResults);
                                    currentMemEnum.add(enumResults);
                                    System.out.print(i);
                                    System.out.print(" ");
                                }
                                allAvgs.add(currentMemAvg);
                                allEnums.add(currentMemEnum);
                                System.out.println();
                            }
                        }
                    }
                    printFinalResults(allAvgs, allEnums);
                    System.out.println("All done!");
                    break;
                } case 9:
                {
                    TXTfileConverter fileConverter = new TXTfileConverter();
                    fileConverter.convertFiles();
                    break;
                } default:
                {
                    System.out.println("Invalid Selection. Try again..");
                }
            }
        }
        boolean quit = false;
        while (!quit)
        {
            System.out.println(MENU);
            int choice = keys.nextInt();
            switch (choice)
            {
                case 0:
                {
                    quit = true;
                    break;
                }
                case 1:
                {
                    JFileChooser fileChooser = new JFileChooser(dir);
                    fileChooser.setMultiSelectionEnabled(true);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    File[] gameFiles = fileChooser.getSelectedFiles();
                    TXTtoGame txtToGame = new TXTtoGame();
                    for(int f = 0; f < gameFiles.length / 2; f++)
                    {
                        Game game = txtToGame.openFile(gameFiles[2 * f].getPath(), gameFiles[2 * f + 1].getPath());
                        printAVClusteredFiles(game, gameFiles[2 * f], dispAVClusters, saidAVClusters);
                    }
                    break;
                } case 2:
                {
                    JFileChooser fileChooser = new JFileChooser(dir);
                    fileChooser.setMultiSelectionEnabled(true);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    File[] files = fileChooser.getSelectedFiles();
                    System.out.println(files[0].getPath());
                    ArrayList<StrategyTable> tables = new ArrayList<>();
                    ArrayList<Game> games = new ArrayList<>();
                    for(int f = 0; f < files.length / 2; f++)
                    {
                        TXTtoGame txtToGame = new TXTtoGame();
                        games.add(txtToGame.openFile(files[f * 2].getPath(), files[f * 2 + 1].getPath()));
                    }
                    int memoryLength = 2;
                    AttitudeVector[] dispAVs = new AttitudeVector[dispAVClusters.length];
                    AttitudeVector[] saidAVs = new AttitudeVector[saidAVClusters.length];
                    DSGeneralAutomaton megaAutomaton = new DSGeneralAutomaton(memoryLength, dispAVs, saidAVs);
                    for(Game game : games)
                    {
                        GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
                        List<Feature> featureList = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
                        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
                        int[][] dsAttVecClustList = fl2dsavcl.getClusterIndexList(featureList, dispAVClusters, saidAVClusters);
                        megaAutomaton.addObservations(dsAttVecClustList);
                    }
                    System.out.print("Total count: ");
                    System.out.println(megaAutomaton.getTotalCount());
                    System.out.print("Sequences: ");
                    System.out.println(megaAutomaton.getNumDistinctSequences());
                    System.out.print("Highest count: ");
                    System.out.println(megaAutomaton.getHighestCount());
                    break;
                } default:
                {
                    System.out.println("Invalid selection");
                }
            }
        }
        System.out.println("Done");
    }

    private static void printFinalResults(List<List<double[][]>> allAvgs, List<List<List<int[][]>>> allEnums)
    {
        printFinalAvgs(allAvgs);
        printFinalEnumResults(allEnums);
    }

    private static void printFinalAvgs(List<List<double[][]>> allAvgs)
    {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append(dir);
        fBuilder.append("\\Final Avg Results.csv");
        try(FileWriter fileWriter = new FileWriter(fBuilder.toString()))
        {
            double[][] combinedAvgs = new double[7][allAvgs.get(0).get(0)[0].length];
            double total = 0;
            int memCount = 0;
            NumberFormat formatter = new DecimalFormat("#0.000");
            for(List<double[][]> singleMemAvgs : allAvgs)
            {
                fileWriter.append("MEMORY ");
                fileWriter.append(Integer.toString(memCount));
                fileWriter.append('\n');
                memCount++;
                for(double[][] singleSampleAvgs : singleMemAvgs)
                {
                    StringBuilder b = new StringBuilder();
                    for(int i = 0; i < singleSampleAvgs.length; i++)
                    {
                        for(int j = 0; j < singleSampleAvgs[i].length; j++)
                        {
                            combinedAvgs[i][j] +=singleSampleAvgs[i][j];
                            b.append(formatter.format(singleSampleAvgs[i][j]));
                            b.append(',');
                        }
                        b.append('\n');
                    }
                    total++;
                    b.append('\n');
                    fileWriter.append(b.toString());
                }
            }
            fileWriter.append('\n');
            fileWriter.append("COMBINED");
            fileWriter.append('\n');
            StringBuilder combinedBuilder = new StringBuilder();
            for(int i = 0; i < combinedAvgs.length; i++)
            {
                for(int j = 0; j < combinedAvgs[i].length; j++)
                {
                    double avg = combinedAvgs[i][j] / total;
                    combinedBuilder.append(formatter.format(avg));
                    combinedBuilder.append(',');
                }
                combinedBuilder.append('\n');
            }
            fileWriter.append(combinedBuilder.toString());
            System.out.println("Finished with Final Avgs!");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void printFinalEnumResults(List<List<List<int[][]>>> allEnums)
    {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append(dir);
        fBuilder.append("\\Final Enum Results.csv");
        try(FileWriter fileWriter = new FileWriter(fBuilder.toString()))
        {
            int[] indivEnumIndices = new int[allEnums.size()];
            int[] gameplayIndices = new int[allEnums.size()];
            int[] memLengthIndices = new int[allEnums.size()];
            boolean write = true;
            int count = 0;
            while (write)
            {
                StringBuilder b = new StringBuilder();
                for(int i = 0; i < allEnums.size(); i++)
                {
                    if(memLengthIndices[i] < allEnums.get(i).size())
                    {
                        if(gameplayIndices[i] < allEnums.get(i).get(memLengthIndices[i]).size())
                        {
                            if(indivEnumIndices[i] < allEnums.get(i).get(memLengthIndices[i]).get(gameplayIndices[i]).length)
                            {
                                for(int j = 0; j < allEnums.get(i).get(memLengthIndices[i]).get(gameplayIndices[i])[indivEnumIndices[i]].length; j++)
                                {
                                    b.append(allEnums.get(i).get(memLengthIndices[i]).get(gameplayIndices[i])[indivEnumIndices[i]][j]);
                                    b.append(',');
                                }
                                indivEnumIndices[i]++;
                            } else
                            {
                                b.append(",,,,,,,,,");
                                indivEnumIndices[i] = 0;
                                gameplayIndices[i]++;
                            }
                        } else
                        {
                            b.append(",,,,,,,,,");
                            gameplayIndices[i] = 0;
                            memLengthIndices[i]++;
                        }
                    } else
                    {
                        b.append(",,,,,,,,,");
                    }
                    b.append(',');
                }
                b.append('\n');
                fileWriter.append(b.toString());
                write = (memLengthIndices[0] < allEnums.get(0).size());
                for(int i = 1; i < allEnums.size(); i++)
                {
                    write = write || (memLengthIndices[i] < allEnums.get(i).size());
                }
                if(count % 1000 == 0)
                {
                    System.out.println(count);
                }
                count++;
            }
            System.out.println("Finished with Final Enum Results");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        List<List<List<double[]>>> enumSummaries = new ArrayList<>();
        for(List<List<int[][]>> sharedMemLengthEnums : allEnums)
        {
            List<List<double[]>> sharedMemLengthAvgs = new ArrayList<>();
            for(List<int[][]> gameplayEnums : sharedMemLengthEnums)
            {
                List<double[]> gameplayAvgs = new ArrayList<>();
                for(int[][] indivGameEnum : gameplayEnums)
                {
                    double[] accuracies = new double[indivGameEnum[0].length - 2];
                    for(int i = 0; i < indivGameEnum.length; i++)
                    {
                        for(int j = 2; j < indivGameEnum[i].length; j++)
                        {
                            if(indivGameEnum[i][0] == indivGameEnum[i][j])
                            {
                                accuracies[j - 2]++;
                            }

                        }
                    }
                    for(int i = 0; i < accuracies.length; i++)
                    {
                        accuracies[i] = accuracies[i] / ((double) indivGameEnum.length);
                    }
                    gameplayAvgs.add(accuracies);
                }
                sharedMemLengthAvgs.add(gameplayAvgs);
            }
            enumSummaries.add(sharedMemLengthAvgs);
        }
        System.out.println("Finished creating enum summaries");
        StringBuilder fBuilder2 = new StringBuilder();
        fBuilder2.append(dir);
        fBuilder2.append("\\Final Enum Avg Results.csv");
        try(FileWriter fileWriter = new FileWriter(fBuilder2.toString()))
        {
            int[] gameplayIndices = new int[enumSummaries.size()];
            int[] memLengthIndices = new int[enumSummaries.size()];
            NumberFormat formatter = new DecimalFormat("#0.000");
            boolean write = true;
            int count = 0;
            while (write)
            {
                StringBuilder b = new StringBuilder();
                for(int i = 0; i < enumSummaries.size(); i++)
                {
                    if(memLengthIndices[i] < enumSummaries.get(i).size())
                    {
                        if(gameplayIndices[i] < enumSummaries.get(i).get(memLengthIndices[i]).size())
                        {
                            for(int j = 0; j < enumSummaries.get(i).get(memLengthIndices[i]).get(gameplayIndices[i]).length; j++)
                            {
                                b.append(formatter.format(enumSummaries.get(i).get(memLengthIndices[i]).get(gameplayIndices[i])[j]));
                                b.append(',');
                            }
                            gameplayIndices[i]++;
                        } else
                        {
                            b.append(",,,,,,,");
                            gameplayIndices[i] = 0;
                            memLengthIndices[i]++;
                        }
                    } else
                    {
                        b.append(",,,,,,,");
                    }
                    b.append(',');
                }
                b.append('\n');
                fileWriter.append(b.toString());
                write = (memLengthIndices[0] < enumSummaries.get(0).size());
                for(int i = 1; i < allEnums.size(); i++)
                {
                    write = write || (memLengthIndices[i] < enumSummaries.get(i).size());
                }
                if(count % 1000 == 0)
                {
                    System.out.print("Avgs ");
                    System.out.println(count);
                }
                count++;
            }
            System.out.println("Finished with Final Enum Avg Results");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void publishAllAvgSplitResults(double[][][][] allAvgResults)
    {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append(dir);
        fBuilder.append("\\Avg Split Results.csv");
        try(FileWriter fileWriter = new FileWriter(fBuilder.toString()))
        {
            for(int m = 1; m < allAvgResults.length; m++)
            {
                for(int r = 0; r < allAvgResults[m][1].length; r++)
                {
                    StringBuilder b = new StringBuilder();
                    for(int t = 1; t < allAvgResults[m].length; t++)
                    {
                        for(int c = 0; c < allAvgResults[m][t][r].length; c++)
                        {
                            b.append(allAvgResults[m][t][r][c]);
                            b.append(',');
                        }
                        b.append(',');
                    }
                    b.append('\n');
                    fileWriter.append(b.toString());
                }
                fileWriter.append('\n');
            }
            publishAllAvgSplitResultsSummary(allAvgResults);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void publishAllAvgSplitResultsSummary(double[][][][] allAvgResults)
    {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append(dir);
        fBuilder.append("\\Summary Avg Split Results.csv");
        double[][][] resultSummary = new double[allAvgResults.length - 1][allAvgResults[1].length - 1][2];
        for(int m = 1; m < allAvgResults.length; m++)
        {
            for(int t = 1; t < allAvgResults[m].length; t++)
            {
                for(int r = 0; r < allAvgResults[m][t].length; r++)
                {
                    for(int c = 0; c < allAvgResults[m][t][r].length; c++)
                    {
                        if(c < 7)
                        {
                            resultSummary[m - 1][t - 1][0] += allAvgResults[m][t][r][c] / 49.0;
                        } else
                        {
                            resultSummary[m - 1][t - 1][1] += allAvgResults[m][t][r][c] / 49.0;
                        }
                    }
                }
            }
        }
        try(FileWriter fileWriter = new FileWriter(fBuilder.toString()))
        {
            NumberFormat formatter = new DecimalFormat("#0.000");
            for(int r = 0; r < resultSummary.length; r++)
            {
                StringBuilder b = new StringBuilder();
                for(int c = 0; c < resultSummary[r].length; c++)
                {
                    b.append(formatter.format(resultSummary[r][c][0]));
                    b.append(" & ");
                    b.append(formatter.format(resultSummary[r][c][1]));
                    b.append(',');
                }
                b.append('\n');
                fileWriter.append(b.toString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        publishAllDiagAvgSplitResultsSummary(allAvgResults);
    }

    private static void publishAllDiagAvgSplitResultsSummary(double[][][][] allAvgResults)
    {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append(dir);
        fBuilder.append("\\Summary Diagonal Avg Split Results.csv");
        double[][][] resultSummary = new double[allAvgResults.length - 1][allAvgResults[1].length - 1][4];
        for(int m = 1; m < allAvgResults.length; m++)
        {
            for(int t = 1; t < allAvgResults[m].length; t++)
            {
                for(int r = 0; r < allAvgResults[m][t].length; r++)
                {
                    for(int c = 0; c < allAvgResults[m][t][r].length; c++)
                    {
                        if(c < 7)
                        {
                            if(r == c)
                            {
                                resultSummary[m - 1][t - 1][0] += allAvgResults[m][t][r][c] / 7.0;
                            } else
                            {
                                resultSummary[m - 1][t - 1][1] += allAvgResults[m][t][r][c] / 42.0;
                            }
                        } else
                        {
                            if(r == c - 7)
                            {
                                resultSummary[m - 1][t - 1][2] += allAvgResults[m][t][r][c] / 7.0;
                            } else
                            {
                                resultSummary[m - 1][t - 1][3] += allAvgResults[m][t][r][c] / 42.0;
                            }
                        }
                    }
                }
            }
        }
        try(FileWriter fileWriter = new FileWriter(fBuilder.toString()))
        {
            NumberFormat formatter = new DecimalFormat("#0.000");
            for(int r = 0; r < resultSummary.length; r++)
            {
                StringBuilder b = new StringBuilder();
                for(int c = 0; c < resultSummary[r].length; c++)
                {
                    b.append(formatter.format(resultSummary[r][c][0]));
                    b.append(" - ");
                    b.append(formatter.format(resultSummary[r][c][1]));
                    b.append(" & ");
                    b.append(formatter.format(resultSummary[r][c][2]));
                    b.append(" - ");
                    b.append(formatter.format(resultSummary[r][c][3]));
                    b.append(',');
                }
                b.append('\n');
                fileWriter.append(b.toString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void publishSplitAutomataResults(List<double[][]> results, int mem, int trees)
    {
        double[][] avgResults = new double[7][14];
        for(double[][] result : results)
        {
            for(int r = 0; r < result.length; r++)
            {
                for(int c = 0; c < result[r].length; c++)
                {
                    avgResults[r][c] += result[r][c] / ((double) results.size());
                }
            }
        }
        NumberFormat formatter = new DecimalFormat("#0.000");
        for(int r = 0; r < avgResults.length; r++)
        {
            for(int c = 0; c < avgResults[r].length; c++)
            {
                System.out.print(formatter.format(avgResults[r][c]));
                System.out.print('\t');
            }
            System.out.println();
        }
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(dir);
        fileNameBuilder.append("\\Split Results Mem");
        fileNameBuilder.append(mem);
        fileNameBuilder.append(" Trees");
        fileNameBuilder.append(trees);
        fileNameBuilder.append(".csv");
        try(FileWriter fWriter = new FileWriter(fileNameBuilder.toString()))
        {
            for(double[][] resultMatrix : results)
            {
                for(int r = 0; r < resultMatrix.length; r++)
                {
                    StringBuilder lineBuilder = new StringBuilder();
                    for(int c = 0; c < resultMatrix[r].length; c++)
                    {
                        lineBuilder.append(resultMatrix[r][c]);
                        lineBuilder.append(',');
                    }
                    lineBuilder.append('\n');
                    fWriter.append(lineBuilder.toString());
                }
                fWriter.append('\n');
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void printAVClusteredFiles(Game game, File originalFile, DistantDataCluster[] dispAVClusters, DistantDataCluster[] saidAVClusters)
    {
        GameToFeatureList gameToFeatureList = new GameToFeatureList(game);
        List<Feature> featureList = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
        FeatList2DSAttVecClustList fl2dsavcl = FeatList2DSAttVecClustList.getInstance();
        int[][] dsAttVecClustList = fl2dsavcl.getClusterIndexList(featureList, dispAVClusters, saidAVClusters);
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(dir + "\\Cluster List Results\\");
        nameBuilder.append(originalFile.getPath().substring(originalFile.getPath().indexOf("Results") + 8, originalFile.getPath().indexOf("activity_")));
        nameBuilder.append("Representative");
        nameBuilder.append(".csv");
        System.out.println(nameBuilder.toString());
        try(FileWriter fWriter = new FileWriter(nameBuilder.toString()))
        {
            fWriter.append("Displayed Attitude Vector Cluster representatives");
            fWriter.append('\n');
            for(int dav = 0; dav < dispAVClusters.length; dav++)
            {
                AttitudeVector centroid = (AttitudeVector) dispAVClusters[dav].getCentroid();
                StringBuilder builder = new StringBuilder();
                builder.append("d");
                builder.append(dav);
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
            fWriter.append("Said Attitude Vector Cluster representatives");
            fWriter.append('\n');
            for(int sav = 0; sav < saidAVClusters.length; sav++)
            {
                AttitudeVector centroid = (AttitudeVector) saidAVClusters[sav].getCentroid();
                StringBuilder builder = new StringBuilder();
                builder.append("s");
                builder.append(sav);
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
            for(int avcl = 0; avcl < dsAttVecClustList.length; avcl++)
            {
                StringBuilder builder = new StringBuilder();
                builder.append("(d");
                builder.append(dsAttVecClustList[avcl][0]);
                builder.append(" s");
                builder.append(dsAttVecClustList[avcl][1]);
                builder.append(" d");
                builder.append(dsAttVecClustList[avcl][2]);
                builder.append(" s");
                builder.append(dsAttVecClustList[avcl][3]);
                builder.append(")");
                fWriter.append(builder.toString());
                fWriter.append('\n');
            }
        } catch (IOException e)
        {
            e.printStackTrace();
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
    }

    private static void testGameToAutomata()
    {
        GameToAutomata.getInstance().generateAutomaton(null, 2);


        System.err.println("Success!");
    }

}
