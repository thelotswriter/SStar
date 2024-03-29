package Game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class TXTtoGame
{

    private double[][][] matrix;

    public TXTtoGame()
    {

    }

    public Game openFile(String activityPath, String messagePath)
    {
        ArrayList<String[]> activityStrings = readActivityFile(activityPath);
        ArrayList<String[]> messageStrings = readMessageFile(messagePath);
        generateMatrix(activityStrings);
        Player[] players = generatePlayers(activityStrings, messageStrings);
        return new Game(new PayoffMatrix(matrix), players[0], players[1], activityPath.substring(activityPath.lastIndexOf("\\")));
    }

    private ArrayList<String[]> readActivityFile(String path)
    {
        ArrayList<String[]> data = new ArrayList<String[]>();
        try(BufferedReader buffy = new BufferedReader(new FileReader(path)))
        {
            String line; // = buffy.readLine();
//            if(line == null)
//            {
//                buffy.close();
//                return null;
//            }
            while ((line = buffy.readLine()) != null)
            {
                String[] cells = line.split("\t");
                if(cells.length != 4)
                {
                    buffy.close();
                    break;
                }
                data.add(cells);
            }
        } catch (FileNotFoundException e)
        {
            return null;
        } catch (IOException e)
        {
            return null;
        }
        return data;
    }

    private ArrayList<String[]> readMessageFile(String path)
    {
        ArrayList<String[]> data = new ArrayList<String[]>();
        try(BufferedReader buffy = new BufferedReader(new FileReader(path)))
        {
            String line;
            while ((line = buffy.readLine()) != null)
            {
                if(!line.contains("$") || line == null)
                {
                    break;
                }
                String[] cells = new String[2];
                cells[0] = line;
                line = buffy.readLine();
                if(line == null || !line.contains("$"))
                {
                    return null;
//                    break;
                }
                cells[1] = line;
                data.add(cells);
            }
        } catch (FileNotFoundException e)
        {
            System.err.println(e.getMessage());
            System.out.println("Err1");
            return null;
        } catch (IOException e)
        {
            System.err.println(e.getMessage());
            System.out.println("Err2");
            return null;
        }
        return data;
    }

    private Player[] generatePlayers(ArrayList<String[]> activityData, ArrayList<String[]> messageData)
    {
        Player player1 = new Player(PlayerType.HUMAN);
        Player player2 = new Player(PlayerType.HUMAN);
        for(int i = 0; i < activityData.size(); i++)
        {
            player1.setNextAction(Integer.parseInt(activityData.get(i)[0]));
            player2.setNextAction(Integer.parseInt(activityData.get(i)[1]));
        }
        for(int i = 0; i < messageData.size(); i++)
        {
            player1.setNextMessage(generateSpeechActArray(1, messageData.get(i)[0]));
            player2.setNextMessage(generateSpeechActArray(2, messageData.get(i)[1]));
        }
        Player[] players = {player1, player2};
        return players;
    }

    private double[][][] generateMatrix(ArrayList<String[]> activityData)
    {
        int numActions = 0;
        for(int i = 0; i < activityData.size(); i++)
        {
            int act1 = Integer.parseInt(activityData.get(i)[0]);
            int act2 = Integer.parseInt(activityData.get(i)[1]);
            if(act1 + 1 > numActions)
            {
                numActions = act1 + 1;
            }
            if(act2 + 1 > numActions)
            {
                numActions = act2 + 1;
            }
            if(numActions >= 3)
            {
                break;
            }
        }
        if(numActions <= 1)
        {
            numActions = 2;
        }
        matrix = new double[numActions][numActions][2];
        boolean[][] checkedMatrix = new boolean[numActions][numActions];
        int numChecked = 0;
        for(int i = 0; i < activityData.size(); i++)
        {
            int act1 = Integer.parseInt(activityData.get(i)[0]);
            int act2 = Integer.parseInt(activityData.get(i)[1]);
            if(!checkedMatrix[act1][act2])
            {
                double p1 = Double.parseDouble(activityData.get(i)[2]);
                if(act1 == act2)
                {
                    matrix[act1][act2][0] = p1;
                    matrix[act1][act2][1] = p1;
                    checkedMatrix[act1][act2] = true;
                    numChecked++;
                } else
                {
                    double p2 = Double.parseDouble(activityData.get(i)[3]);
                    matrix[act1][act2][0] = p1;
                    matrix[act1][act2][1] = p2;
                    checkedMatrix[act1][act2] = true;
                    matrix[act2][act1][0] = p2;
                    matrix[act2][act1][1] = p1;
                    checkedMatrix[act2][act1] = true;
                    numChecked += 2;
                }
                if(numChecked >= matrix.length * matrix[0].length)
                {
                    break;
                }
            }
        }
        if(numChecked < matrix.length * matrix[0].length)
        {
            if(matrix.length == 2)
            {
                if(matrix[0][0][0] == 0.6 || matrix[0][1][1] == 1.0 || matrix[1][1][0] == 0.2)
                {
                    matrix[0][0][0] = 0.6;
                    matrix[0][0][1] = 0.6;
                    matrix[0][1][0] = 0;
                    matrix[0][1][1] = 1;
                    matrix[1][0][0] = 1;
                    matrix[1][0][1] = 0;
                    matrix[1][1][0] = 0.2;
                    matrix[1][1][1] = 0.2;
                } else
                {
                    matrix[0][1][0] = 1;
                    matrix[0][1][1] = 0.33;
                    matrix[1][0][0] = 0.33;
                    matrix[1][0][1] = 1;
                    matrix[1][1][0] = 0.84;
                    matrix[1][1][1] = 0.84;
                }
//                if(!checkedMatrix[0][0])
//                {
//                    matrix[0][0][0] = 0;
//                    matrix[0][0][1] = 0;
//                }
//                if(!checkedMatrix[0][1])
//                {
//                    matrix[0][1][0] = 0.35;
//                    matrix[0][1][1] = 0.7;
//                    matrix[1][0][0] = 0.7;
//                    matrix[1][0][1] = 0.35;
//                }
//                if(!checkedMatrix[0][2])
//                {
//                    matrix[0][2][0] = 1;
//                    matrix[0][2][1] = 0.4;
//                    matrix[2][0][0] = 0.4;
//                    matrix[2][0][1] = 1;
//                }
//                if(!checkedMatrix[1][1])
//                {
//                    matrix[1][1][0] = 0.2;
//                    matrix[1][1][1] = 0.2;
//                }
//                if(!checkedMatrix[1][2])
//                {
//                    matrix[1][2][0] = 0.45;
//                    matrix[1][2][1] = 0.3;
//                    matrix[2][1][0] = 0.3;
//                    matrix[2][1][1] = 0.45;
//                }
//                if(!checkedMatrix[2][2])
//                {
//                    matrix[2][2][0] = 0.4;
//                    matrix[2][2][1] = 0.4;
//                }
            }
            else
            {
                if(matrix[0][0][0] == 0.0 && (matrix[0][1][0] == 1.0 || matrix[1][1][0] == 0.84))
                {
                    if(!checkedMatrix[0][1])
                    {
                        matrix[0][1][0] = 1.0;
                        matrix[0][1][1] = 0.33;
                        matrix[1][0][0] = 0.33;
                        matrix[1][0][1] = 1.0;
                    }
                    if(!checkedMatrix[1][1])
                    {
                        matrix[1][1][0] = 0.84;
                        matrix[1][1][1] = 0.84;
                    }
                } else
                {
                    if(!checkedMatrix[0][0])
                    {
                        matrix[0][0][0] = 0.6;
                        matrix[0][0][1] = 0.6;
                    }
                    if(!checkedMatrix[0][1])
                    {
                        matrix[0][1][0] = 0;
                        matrix[0][1][1] = 1;
                        matrix[1][0][0] = 1;
                        matrix[1][0][1] = 0;
                    }
                    if(!checkedMatrix[1][1])
                    {
                        matrix[1][1][0] = 0.2;
                        matrix[1][1][1] = 0.2;
                    }
                }
            }
        }
        return matrix;
    }

    private SpeechAct[] generateSpeechActArray(int playerNum, String data)
    {
        if(data.indexOf("$") == 0)
        {
            return new SpeechAct[0];
        }
        String[] splitData = data.split(";");
        ArrayList<SpeechAct> listedSpeechActs = new ArrayList<>();
        for(int i = 0; i < splitData.length - 1; i++)
        {
            if(splitData[i].contains(" "))
            {
                String[] splitMessage = splitData[i].split(" ");
                int mNum = Integer.parseInt(splitMessage[0]);
                switch (mNum)
                {
                    case 15:
                    {

                    } case 16:
                    {
                        int[] jointAction = stringToJointAction(splitMessage[1]);
                        if(playerNum == 2)
                        {
                            int temp = jointAction[1];
                            jointAction[1] = jointAction[0];
                            jointAction[0] = temp;
                        }
                        listedSpeechActs.add(new SpeechAct(true, jointAction));
                        break;
                    } case 17:
                    {
                        int[] jointAction = stringToJointAction(splitMessage[1]);
                        if(playerNum == 2)
                        {
                            int temp = jointAction[1];
                            jointAction[1] = jointAction[0];
                            jointAction[0] = temp;
                        }
                        listedSpeechActs.add(new SpeechAct(false, jointAction));
                        break;
                    } case 18:
                    {
                        int[] jointAction1 = stringToJointAction(splitMessage[1]);
                        int[] jointAction2 = stringToJointAction(splitMessage[2]);
                        if(playerNum == 2)
                        {
                            int temp = jointAction1[1];
                            jointAction1[1] = jointAction1[0];
                            jointAction1[0] = temp;
                            temp = jointAction2[1];
                            jointAction2[1] = jointAction2[0];
                            jointAction2[0] = temp;
                        }
                        listedSpeechActs.add(new SpeechAct(true, jointAction1, jointAction2));
                    }
                }
            }
        }
        if(listedSpeechActs.isEmpty())
        {
            return new SpeechAct[0];
        }
        SpeechAct[] sAArray = new SpeechAct[listedSpeechActs.size()];
        for(int i = 0; i < sAArray.length; i++)
        {
            sAArray[i] = listedSpeechActs.get(i);
        }
        return sAArray;
    }

    private int[] stringToJointAction(String str)
    {
        int[] jointAction = new int[2];
        if(str.length() == 1)
        {
            if(matrix.length < 3)
            {
                switch (str.charAt(0))
                {
                    case 'A':
                    {
                        jointAction[0] = 0;
                        break;
                    } case 'B':
                    {
                        jointAction[0] = 1;
                        break;
                    } case 'C':
                    {
                        jointAction[1] = 0;
                        break;
                    } case 'D':
                    {
                        jointAction[1] = 1;
                    }
                }
            } else
            {
                switch (str.charAt(0))
                {
                    case 'A':
                    {
                        jointAction[0] = 0;
                        break;
                    } case 'B':
                    {
                        jointAction[0] = 1;
                        break;
                    } case 'C':
                    {
                        jointAction[0] = 2;
                        break;
                    } case 'D':
                    {
                        jointAction[1] = 0;
                        break;
                    } case 'E':
                    {
                        jointAction[1] = 1;
                        break;
                    } case 'F':
                    {
                        jointAction[1] = 2;
                    }
                }
            }
        } else
        {
            char[] jointActionLabels = new char[2];
            jointActionLabels[0] = str.charAt(0);
            jointActionLabels[1] = str.charAt(1);
            if(matrix.length < 3)
            {
                switch (jointActionLabels[0])
                {
                    case 'A':
                    {
                        jointAction[0] = 0;
                        break;
                    } case 'B':
                {
                    jointAction[0] = 1;
                    break;
                }
                }
                switch (jointActionLabels[1])
                {
                    case 'C':
                    {
                        jointAction[1] = 0;
                        break;
                    } case 'D':
                {
                    jointAction[1] = 1;
                    break;
                }
                }
            } else
            {
                switch (jointActionLabels[0])
                {
                    case 'A':
                    {
                        jointAction[0] = 0;
                        break;
                    } case 'B':
                {
                    jointAction[0] = 1;
                    break;
                } case 'C':
                {
                    jointAction[0] = 2;
                    break;
                }
                }
                switch (jointActionLabels[1])
                {
                    case 'D':
                    {
                        jointAction[1] = 0;
                        break;
                    } case 'E':
                {
                    jointAction[1] = 1;
                    break;
                } case 'F':
                {
                    jointAction[1] = 2;
                    break;
                }
                }
            }
        }
        return jointAction;
    }

}
