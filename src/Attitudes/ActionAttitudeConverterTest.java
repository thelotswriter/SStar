package Attitudes;

import Features.Feature;
import Features.FeatureList;
import Features.GameToFeatureList;
import Game.Game;
import Game.PayoffMatrix;
import Game.Player;
import Game.TXTtoGame;
import Game.SpeechAct;
import StrategyAutomata.MegaAutomatonTest;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class ActionAttitudeConverterTest
{

    private final String dir = "C:\\Users\\thelo\\Documents\\BYU\\Research\\Spp Games\\newResults";

    private int nNeighbors = 0;
    private double pPower = 0.5;
    private double discount = 0.0;

    public ActionAttitudeConverterTest()
    {

    }

    public void run()
    {
        System.out.println("What predictive power?");
        Scanner keys = new Scanner(System.in);
        pPower = keys.nextDouble();
        if(pPower > 1)
        {
            pPower = 1;
        } else if(pPower < 0)
        {
            pPower = 0;
        }
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.showOpenDialog(null);
        File[] files = fileChooser.getSelectedFiles();
        TXTtoGame txtToGame = new TXTtoGame();
        Game[] games = new Game[files.length / 2];
        for(int g = 0; g < games.length; g++)
        {
            games[g] = txtToGame.openFile(files[2 * g].getPath(), files[2 * g + 1].getPath());
        }
        Collection<Collection<Feature>> featureCollections = new ArrayList<>();
        for(int g = 0; g < games.length; g++)
        {
            GameToFeatureList gameToFeatureList = new GameToFeatureList(games[g]);
            Collection<Feature> fCollection = gameToFeatureList.generateFeatureList(nNeighbors, discount, pPower);
            featureCollections.add(fCollection);
        }
        List<List<Feature>> fLists = new ArrayList<>();
        for(Collection<Feature> featureCollection : featureCollections)
        {
            List<Feature> fList = new ArrayList<>();
            for(Feature feature : featureCollection)
            {
                fList.add(feature);
            }
            fLists.add(fList);
        }
        double[] accuracies = new double[games.length];
        double[] otherAccuracies = new double[games.length];
        for(int g = 0; g < games.length; g++)
        {
            Game game = games[g];
            List<Feature> fList = fLists.get(g);
            ActionAttitudeConverter converter = new ActionAttitudeConverter(game.getPayoffMatrix(), pPower);
            ArrayList<Integer> originalActions =  game.getPlayer1().getActionHistory();
            ArrayList<SpeechAct[]> originalMessages = game.getPlayer1().getMessageHistory();
            ArrayList<Integer> originalOtherActions = game.getPlayer2().getActionHistory();
            ArrayList<SpeechAct[]> originalOtherMessage = game.getPlayer2().getMessageHistory();
            int nMatched = 0;
            int nOtherMatched = 0;
            int[] prevActPair = new int[2];
            int[] prevOtherActPair = new int[2];
            prevActPair[0] = -1;
            prevActPair[1] = -1;
            prevOtherActPair[0] = -1;
            prevOtherActPair[1] = -1;
//            int prevConverted[] = new int[2];
//            prevConverted[0] = -1;
//            prevConverted[1] = -1;
//            prevConverted[2] = -1;
//            prevConverted[3] = -1;
//            int prevOtherConverted[] = new int[4];
//            prevOtherConverted[0] = -1;
//            prevOtherConverted[1] = -1;
//            prevOtherConverted[2] = -1;
//            prevOtherConverted[3] = -1;
            for(int hist = 0; hist < fList.size(); hist++)
            {
                int[] converted = converter.attitudeVectorToActionWithPrevActionPair(prevActPair, fList.get(hist).getAttitudeDisplayed());
//                if(converted[0] == prevConverted[0] && converted[1] == prevConverted[1]
//                        && converted[2] == prevConverted[2] && converted[3] == prevConverted[3])
//                {
//                    int temp0 = converted[0];
//                    int temp1 = converted[1];
//                    converted[0] = converted[2];
//                    converted[1] = converted[3];
//                    converted[2] = temp0;
//                    converted[3] = temp1;
//                }
                if(originalActions.get(hist).equals(converted[0]))
                {
                    nMatched++;
                }
                int[] otherConverted = converter.attitudeVectorToActionWithPrevActionPair(prevOtherActPair, fList.get(hist).getOtherAttitudeDisplayed());
                if(originalOtherActions.get(hist).equals(otherConverted[0]))
                {
                    nOtherMatched++;
                }
                prevActPair[0] = originalActions.get(hist);
                prevActPair[1] = originalOtherActions.get(hist);
                prevOtherActPair[0] = prevActPair[1];
                prevOtherActPair[1] = prevActPair[0];
//                prevConverted = converted;
//                prevOtherConverted = otherConverted;
            }
            accuracies[g] = ((double) nMatched) / ((double) fList.size());
            otherAccuracies[g] = ((double) nOtherMatched) / ((double) fList.size());
            recordResults(files[g], game, fList, converter);
        }
        double avgAccuracy = 0;
        double avgOtherAccuracy = 0;
        for(int a = 0; a < accuracies.length; a++)
        {
            avgAccuracy += accuracies[a];
            avgOtherAccuracy += otherAccuracies[a];
        }
        avgAccuracy = avgAccuracy / ((double) accuracies.length);
        avgOtherAccuracy = avgOtherAccuracy / ((double) otherAccuracies.length);
        System.out.print("Average Accuracy: ");
        System.out.println(avgAccuracy);
        System.out.print("Average other accuracy: ");
        System.out.println(avgOtherAccuracy);
    }

    private void recordResults(File origFile, Game game, List<Feature> featList, ActionAttitudeConverter converter)
    {
        StringBuilder fNameBuilder = new StringBuilder();
        fNameBuilder.append(origFile.getPath().substring(0, origFile.getPath().indexOf("activity_")));
        fNameBuilder.append("conversion_test");
        fNameBuilder.append('_');
        fNameBuilder.append(pPower);
        fNameBuilder.append('_');
        fNameBuilder.append(discount);
        fNameBuilder.append('_');
        fNameBuilder.append(nNeighbors);
        fNameBuilder.append(".csv");
        try(FileWriter fWriter = new FileWriter(fNameBuilder.toString()))
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Predictive Power: ");
            builder.append(pPower);
            builder.append('\n');
            builder.append("Discount: ");
            builder.append(discount);
            builder.append('\n');
            builder.append("Num Neighbors: ");
            builder.append(nNeighbors);
            builder.append('\n');
            builder.append('\n');
            fWriter.append(builder.toString());
            // Payoff Matrix
            StringBuilder pBuilder = new StringBuilder();
            PayoffMatrix pMatrix = game.getPayoffMatrix();
            for(int r = 0; r < pMatrix.getNumRows(); r++)
            {
                for(int c = 0; c < pMatrix.getNumCols(); c++)
                {
                    pBuilder.append('(');
                    pBuilder.append(pMatrix.getRowPlayerValue(r, c));
                    pBuilder.append(' ');
                    pBuilder.append(pMatrix.getColPlayerValue(r, c));
                    pBuilder.append("),");
                }
                pBuilder.append('\n');
            }
            fWriter.append(pBuilder.toString());
            fWriter.append('\n');
            // Converted Payoff Matrix
            StringBuilder avBuilder = new StringBuilder();
            AttitudeVector[][] avMatrix = converter.getAttitudeVectorMatrix();
            for(int r = 0; r < avMatrix.length; r++)
            {
                for(int c = 0; c < avMatrix[r].length; c++)
                {
                    avBuilder.append('(');
                    avBuilder.append(String.valueOf(avMatrix[r][c].getGreedy()).substring(0, Math.min(5, String.valueOf(avMatrix[r][c].getGreedy()).length())));
                    avBuilder.append(' ');
                    avBuilder.append(String.valueOf(avMatrix[r][c].getPlacate()).substring(0, Math.min(5, String.valueOf(avMatrix[r][c].getPlacate()).length())));
                    avBuilder.append(' ');
                    avBuilder.append(String.valueOf(avMatrix[r][c].getCooperate()).substring(0, Math.min(5, String.valueOf(avMatrix[r][c].getCooperate()).length())));
                    avBuilder.append(' ');
                    avBuilder.append(String.valueOf(avMatrix[r][c].getAbsurd()).substring(0, Math.min(5, String.valueOf(avMatrix[r][c].getAbsurd()).length())));
                    avBuilder.append("),");
                }
                avBuilder.append('\n');
            }
            fWriter.append(avBuilder.toString());
            fWriter.append('\n');
            // Converted Payoff Matrix with PPower
            StringBuilder avpBuilder = new StringBuilder();
            AttitudeVector[][] avpMatrix = converter.getAttitudeVectorWithPPowerMatrix();
            for(int r = 0; r < avpMatrix.length; r++)
            {
                for(int c = 0; c < avpMatrix[r].length; c++)
                {
                    avpBuilder.append('(');
                    avpBuilder.append(String.valueOf(avpMatrix[r][c].getGreedy()).substring(0, Math.min(5, String.valueOf(avpMatrix[r][c].getGreedy()).length())));
                    avpBuilder.append(' ');
                    avpBuilder.append(String.valueOf(avpMatrix[r][c].getPlacate()).substring(0, Math.min(5, String.valueOf(avpMatrix[r][c].getPlacate()).length())));
                    avpBuilder.append(' ');
                    avpBuilder.append(String.valueOf(avpMatrix[r][c].getCooperate()).substring(0, Math.min(5, String.valueOf(avpMatrix[r][c].getCooperate()).length())));
                    avpBuilder.append(' ');
                    avpBuilder.append(String.valueOf(avpMatrix[r][c].getAbsurd()).substring(0, Math.min(5, String.valueOf(avpMatrix[r][c].getAbsurd()).length())));
                    avpBuilder.append("),");
                }
                avpBuilder.append('\n');
            }
            fWriter.append(avpBuilder.toString());
            fWriter.append('\n');
            // P1_Orig_Act P2_Orig_Act Feature P1_Conv_Act P2_Conv_Act
            fWriter.append("P1 OG,P2 OG, AV1,AV1,P1 Conv,P2 Conv");
            fWriter.append('\n');
            Player p1 = game.getPlayer1();
            Player p2 = game.getPlayer2();
            for(int i = 0; i < featList.size(); i++)
            {
                StringBuilder b = new StringBuilder();
                b.append(p1.getAction(i));
                b.append(',');
                b.append(p2.getAction(i));
                b.append(",(");
                AttitudeVector av1 = featList.get(i).getAttitudeDisplayed();
                AttitudeVector av2 = featList.get(i).getOtherAttitudeDisplayed();
                b.append(String.valueOf(av1.getGreedy()).substring(0, Math.min(5, String.valueOf(av1.getGreedy()).length())));
                b.append(' ');
                b.append(String.valueOf(av1.getPlacate()).substring(0, Math.min(5, String.valueOf(av1.getPlacate()).length())));
                b.append(' ');
                b.append(String.valueOf(av1.getCooperate()).substring(0, Math.min(5, String.valueOf(av1.getCooperate()).length())));
                b.append(' ');
                b.append(String.valueOf(av1.getAbsurd()).substring(0, Math.min(5, String.valueOf(av1.getAbsurd()).length())));
                b.append("),(");
                b.append(String.valueOf(av2.getGreedy()).substring(0, Math.min(5, String.valueOf(av2.getGreedy()).length())));
                b.append(' ');
                b.append(String.valueOf(av2.getPlacate()).substring(0, Math.min(5, String.valueOf(av2.getPlacate()).length())));
                b.append(' ');
                b.append(String.valueOf(av2.getCooperate()).substring(0, Math.min(5, String.valueOf(av2.getCooperate()).length())));
                b.append(' ');
                b.append(String.valueOf(av2.getAbsurd()).substring(0, Math.min(5, String.valueOf(av2.getAbsurd()).length())));
                b.append("),");
                b.append(converter.attitudeVectorToActionsWithPPower(av1)[0]);
                b.append(',');
                b.append(converter.attitudeVectorToActionsWithPPower(av2)[0]);
                b.append('\n');
                fWriter.append(b.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
