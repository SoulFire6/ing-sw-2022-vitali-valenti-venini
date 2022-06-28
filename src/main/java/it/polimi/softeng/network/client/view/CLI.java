package it.polimi.softeng.network.client.view;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.MoveNotAllowedException;
import it.polimi.softeng.exceptions.ServerCreationException;
import it.polimi.softeng.model.CharID;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import it.polimi.softeng.network.server.Server;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Properties;
import java.util.regex.Pattern;

public class CLI implements View {
    private String username=null;
    private final BufferedReader in;

    private Socket socket;
    private ObjectOutputStream toServer=null;
    private final boolean windowsTerminal, loadedProperties;
    private final Properties properties=new Properties();
    private final Properties characterInfo=new Properties();
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$";

    public CLI() {
        boolean loadStatus=true;
        this.in=new BufferedReader(new InputStreamReader(System.in));
        try {
            this.properties.load(getClass().getResourceAsStream("/Assets/CLI/CLI.properties"));
            this.characterInfo.load(getClass().getResourceAsStream("/CardData/CharacterCards.properties"));
        }
        catch (IOException io) {
            System.out.println(getDisplayStyle(Colour.RED.name())+"Could not find properties file, defaulting to basic CLI"+getDisplayStyle("RESET"));
            loadStatus=false;
        }
        this.windowsTerminal=System.getProperty("os.name").contains("Windows") && !System.getProperty("java.class.path").contains("idea_rt.jar");
        this.loadedProperties=loadStatus;
    }
    @Override
    public void run() {
        clearScreen();
        System.out.println(getDisplayStyle(Colour.RED.name())+
                "      ▄████████    ▄████████  ▄█     ▄████████ ███▄▄▄▄       ███     ▄██   ▄      ▄████████\n" +
                "      ███    ███   ███    ███ ███    ███    ███ ███▀▀▀██▄ ▀█████████▄ ███   ██▄   ███    ███\n" +
                "      ███    █▀    ███    ███ ███▌   ███    ███ ███   ███    ▀███▀▀██ ███▄▄▄███   ███    █▀\n" +
                "     ▄███▄▄▄      ▄███▄▄▄▄██▀ ███▌   ███    ███ ███   ███     ███   ▀ ▀▀▀▀▀▀███   ███\n" +
                "    ▀▀███▀▀▀     ▀▀███▀▀▀▀▀   ███▌ ▀███████████ ███   ███     ███     ▄██   ███ ▀███████████\n" +
                "      ███    █▄  ▀███████████ ███    ███    ███ ███   ███     ███     ███   ███          ███\n" +
                "      ███    ███   ███    ███ ███    ███    ███ ███   ███     ███     ███   ███    ▄█    ███\n" +
                "      ██████████   ███    ███ █▀     ███    █▀   ▀█   █▀     ▄████▀    ▀█████▀   ▄████████▀" +
                getDisplayStyle("RESET"));
        while (toServer==null) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ignored) {
            }
        }
        Message outMessage=null;
        while (outMessage==null || outMessage.getSubType()!=MsgType.DISCONNECT) {
            try {
                outMessage=parseMessage(in.readLine().split(" "));
                if (outMessage!=null) {
                    sendMessage(outMessage);
                }
            }
            catch (IOException io) {
                System.out.println(getDisplayStyle(Colour.RED.name())+"Error reading input"+getDisplayStyle("RESET"));
            }
            catch (MoveNotAllowedException mnae) {
                System.out.println(getDisplayStyle(Colour.RED.name())+mnae.getMessage()+getDisplayStyle("RESET"));
            }
        }
    }

    @Override
    public ObjectInputStream setUpConnection(String[] args) {
        String ip;
        int port;
        setUsername(args.length>0?args[0]:null);
        while (socket==null) {
            System.out.println("Self host server? (y/n)");
            try {
                switch (in.readLine().toUpperCase()) {
                    case "Y":
                        while (socket == null) {
                            port = setPort(args.length > 2 ? args[2] : null);
                            Integer serverPort = port;
                            Thread serverThread = new Thread(() -> {
                                Server server = new Server();
                                try {
                                    server.main(new String[]{serverPort.toString()});
                                } catch (ServerCreationException sce) {
                                    System.out.println(sce.getMessage());
                                }
                            });
                            serverThread.setDaemon(true);
                            serverThread.start();
                            try {
                                Thread.sleep(200);
                            }
                            catch (InterruptedException ignored) {
                            }

                            try {
                                socket = new Socket(DEFAULT_IP, port);
                            } catch (IOException io) {
                                if (serverThread.isAlive()) {
                                    System.out.println("Error connecting to server");
                                } else {
                                    System.out.println("Could not start server");
                                }
                            }
                        }
                        break;
                    case "N":
                        while (socket == null) {
                            ip = setIp(args.length > 1 ? args[1] : null);
                            port = setPort(args.length > 2 ? args[2] : null);
                            try {
                                socket = new Socket(ip, port);
                            } catch (IOException io) {
                                System.out.println("Error connecting to server");
                                return setUpConnection(new String[]{username, null, null});
                            }
                        }
                        break;
                    default:
                        System.out.println("Invalid response");
                }
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
        }
        try {
            toServer=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
            toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT, username, null, null));
            return objectInputStream;
        }
        catch (IOException io) {
            System.out.println("Error establishing data streams, try again");
            return setUpConnection(new String[]{username,null,null});
        }
    }

    private void setUsername(String username) {
        if (username!=null) {
            if (username.length()<3 || username.length()>10 || username.contains(" ")) {
                System.out.println("Invalid format, username must be 3-10 character and must not contain spaces");
            } else {
                this.username=username;
            }
        }
        while(this.username==null) {
            System.out.print("Username: ");
            try {
                this.username=in.readLine();
                if (this.username.length() < 3 || this.username.length() > 10 || this.username.contains(" ")) {
                    this.username=null;
                    System.out.println("Invalid format, username must be 3-10 character and must not contain spaces");
                }
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
        }
    }

    private String setIp(String ip) {
        Pattern pattern=Pattern.compile(IP_FORMAT);
        if (ip==null) {
            System.out.print("Server ip (local or empty for default localhost): ");
            try {
                return setIp(in.readLine());
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
        } else {
            if (ip.equals("") || ip.equals("local")) {
                return DEFAULT_IP;
            }
            if (pattern.matcher(ip).matches()) {
                return ip;
            }
            System.out.println("Invalid ip format");
        }
        return setIp(null);
    }

    private int setPort(String port) {
        if (port==null) {
            System.out.print("Server port (local or empty for default 50033) [49152-65535]: ");
            try {
                return setPort(in.readLine());
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
        } else {
            if (port.equals("") || port.equalsIgnoreCase("local")) {
                return DEFAULT_PORT;
            } else {
                try {
                    int portNumber=Integer.parseInt(port);
                    if (portNumber<49152 || portNumber>65535) {
                        System.out.println("Port number outside range");
                    }
                }
                catch (NumberFormatException nfe) {
                    System.out.println("Port must be a number");
                }
            }
        }
        return setPort(null);
    }

    @Override
    public void closeConnection() {
        try {
            toServer.close();
            socket.close();
        }
        catch (IOException ignored) {
        }
    }
    @Override
    public void display(Message message) {
        switch (message.getSubType()) {
            case INPUT:
                System.out.println(((Info_Message)message).getInfo());
                if (message.getContext().contains(">")) {
                    System.out.println(message.getContext().replace("-","\n"));
                }
                break;
            case ERROR:
                System.out.println(getDisplayStyle(Colour.RED.name())+"ERROR: "+((Info_Message)message).getInfo()+getDisplayStyle("RESET"));
                break;
            case TURNSTATE:
                System.out.println(message.getContext());
                break;
            default:
                System.out.println(((Info_Message)message).getInfo());
                break;
        }
    }

    public void sendMessage(Message message) {
        try {
            toServer.writeObject(message);
            toServer.flush();
            toServer.reset();
        }
        catch (IOException io) {
            System.out.println("Could not send message");
        }
    }

    public String getDisplayStyle(String styleName) {
        if (windowsTerminal) {
            return "";
        }
        String displayStyle=properties.getProperty("ANSI_"+styleName);
        if (!loadedProperties || displayStyle==null) {
            return "";
        }
        return (char)27+displayStyle;
    }

    private void printModel(ReducedGame model) {
        String dash="▬",wall="▌";
        ReducedPlayer firstPlayer,secondPlayer;
        int schoolBoardLength=35;
        String schoolBoardDelimiter=String.format("%-"+schoolBoardLength+"s",dash.repeat(windowsTerminal?23:schoolBoardLength));
        int cardDelimiter=windowsTerminal?5:8;
        clearScreen();
        StringBuilder modelUI=new StringBuilder();
        for (int i=0; i<2; i++) {
            firstPlayer=model.getPlayers().size()>i*2?model.getPlayers().get(i*2):null;
            secondPlayer=model.getPlayers().size()>i*2+1?model.getPlayers().get(i*2+1):null;
            if (firstPlayer!=null) {
                modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer.getName()+"'s SchoolBoard")).append("  ");
                modelUI.append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?secondPlayer.getName()+"'s SchoolBoard":"")).append("\n");
                modelUI.append(String.format("%-"+schoolBoardLength+"s",("Team: "+firstPlayer.getTeam()+(model.isExpertMode()?"  Coins: "+firstPlayer.getSchoolBoard().getCoins():""))));
                modelUI.append("  ").append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?("Team: "+secondPlayer.getTeam()+(model.isExpertMode()?"  Coins: "+secondPlayer.getSchoolBoard().getCoins():"")):"")).append("\n");
                if (firstPlayer.getSchoolBoard().getLastUsedCard()!=null || secondPlayer!=null && secondPlayer.getSchoolBoard().getLastUsedCard()!=null) {
                    modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer.getSchoolBoard().getLastUsedCard()==null?"":"Turn value: "+firstPlayer.getSchoolBoard().getLastUsedCard().getTurnValue()+", MN value: "+firstPlayer.getSchoolBoard().getLastUsedCard().getMotherNatureValue()));
                    modelUI.append("  ").append(String.format("%-"+schoolBoardLength+"s",secondPlayer==null || secondPlayer.getSchoolBoard()==null || secondPlayer.getSchoolBoard().getLastUsedCard()==null?"":"Turn value: "+secondPlayer.getSchoolBoard().getLastUsedCard().getTurnValue()+", MN value: "+secondPlayer.getSchoolBoard().getLastUsedCard().getMotherNatureValue())).append("\n");
                }
                modelUI.append(schoolBoardDelimiter).append("  ").append(secondPlayer!=null?schoolBoardDelimiter:" ").append("\n");
                for (Colour c : Colour.values()) {
                    modelUI.append(getSchoolBoardRow(c,firstPlayer.getTeam(),firstPlayer.getSchoolBoard()));
                    modelUI.append(secondPlayer==null?String.format("%-"+schoolBoardLength+"s",""):getSchoolBoardRow(c,secondPlayer.getTeam(),secondPlayer.getSchoolBoard()));
                    modelUI.append("\n");
                }
                modelUI.append(schoolBoardDelimiter).append("  ").append(secondPlayer!=null?schoolBoardDelimiter:" ").append("\n");
            }
        }
        modelUI.append("\n");
        for (int i=0; i<model.getIslands().size(); i++) {
            ReducedIsland island=model.getIslands().get(i);
            modelUI.append(getTileStats(island.getID(),island.getContents(),island.hasMotherNature(),island.getTeam(),island.getTowers()));
            modelUI.append((i+1)%4==0?"\n":" | ");
        }
        modelUI.append("\n");
        for (ReducedCloud cloud : model.getClouds()) {
            modelUI.append(getTileStats(cloud.getId(),cloud.getContents(),false,null,0)).append(" | ");
        }
        modelUI.append(getTileStats(model.getBag().getId(),model.getBag().getContents(),false,null,0)).append("\n");
        ArrayList<ReducedAssistantCard> hand=null;
        for (ReducedPlayer player : model.getPlayers()) {
            if (player.getName().equals(username)) {
                hand=player.getSchoolBoard().getHand();
                break;
            }
        }
        if (model.isExpertMode()) {
            for (int i=0; i<model.getCharacterCards().size(); i++) {
                modelUI.append(getCharacterData(model.getCharacterCards().get(i))).append(" | ");
            }
            modelUI.append("Coins: ").append(model.getCoins()).append("\n");
        }
        if (hand!=null) {
            for (int i=0; i<6; i++) {
                switch (i) {
                    case 0:
                    case 5:
                        modelUI.append((" "+dash.repeat(cardDelimiter)+" ").repeat(hand.size()));
                        break;
                    case 1:
                        for (ReducedAssistantCard card : hand) {
                            modelUI.append(wall).append(String.format("%-8s",card.getId())).append(wall);
                        }
                        break;
                    case 4:
                        for (ReducedAssistantCard card : hand) {
                            modelUI.append(wall).append(" ").append(String.format("%-3d", card.getTurnValue())).append(String.format("%3d", card.getMotherNatureValue())).append(" ").append(wall);
                        }
                        break;
                    default:
                        modelUI.append((wall+" ".repeat(8)+wall).repeat(hand.size()));
                        break;
                }
                modelUI.append("\n");
            }
        } else {
            modelUI.append("ERROR LOADING HAND");
        }
        modelUI.append("CURRENT PLAYER: ").append(model.getCurrentPlayer()).append("\nCURRENT PHASE: ").append(model.getCurrentPhase().getDescription()).append(model.getCurrentPhase() == TurnManager.TurnState.MOVE_STUDENTS_PHASE ? " (Remaining moves: " + model.getRemainingMoves() + ")" : "");
        System.out.println(modelUI);
    }

    private String getSchoolBoardRow(Colour c, Team t, ReducedSchoolBoard board) {
        String wall="▌";
        String tower=windowsTerminal?"▲ ":getDisplayStyle(t.name())+"▲"+getDisplayStyle("RESET")+" ";
        String fontColour=getDisplayStyle(c.name());
        String resetColour=getDisplayStyle ("RESET");
        StringBuilder schoolBoardRow=new StringBuilder();
        schoolBoardRow.append(wall).append(windowsTerminal?c.name().charAt(0)+"_":fontColour+"♦ ").append(board.getEntrance().get(c)).append(windowsTerminal?"":resetColour).append(wall);
        schoolBoardRow.append(windowsTerminal?(" "+c.name().charAt(0)).repeat(board.getDiningRoom().get(c)):fontColour+" ♦".repeat(board.getDiningRoom().get(c))+resetColour);
        schoolBoardRow.append(" ♦".repeat(10-board.getDiningRoom().get(c))).append(" ").append(wall);
        schoolBoardRow.append(board.getProfessorTable().get(c)?(windowsTerminal?c.name().charAt(0):fontColour+"◘"+resetColour):"◘").append(" ").append(wall);
        schoolBoardRow.append(board.getTowers()>=((2*c.ordinal()+1))?tower:"  ").append((board.getTowers()>=(c.ordinal()+1)*2)?tower:"  ").append(wall).append("  ");
        return schoolBoardRow.toString();
    }

    private String getTileStats(String id, EnumMap<Colour,Integer> contents, boolean motherNature, Team team, int towerNum) {
        StringBuilder tileStats=new StringBuilder();
        tileStats.append(!windowsTerminal && motherNature?getDisplayStyle("UNDERLINE")+getDisplayStyle(Colour.RED.name()):"");
        tileStats.append(id).append(windowsTerminal && motherNature?"(X):":getDisplayStyle("RESET")+": ");
        for (Colour c : Colour.values()) {
            tileStats.append(windowsTerminal?" "+c.name().charAt(0)+"_":getDisplayStyle(c.name())).append(contents.get(c)).append(" ");
        }
        tileStats.append(windowsTerminal?"":getDisplayStyle("RESET"));
        if (team!=null) {
            tileStats.append(" (").append(windowsTerminal?team.name()+" ":getDisplayStyle(team.name())).append(towerNum).append(" ▲").append(windowsTerminal?"":getDisplayStyle("RESET")).append(")");
        }
        return tileStats.toString();
    }

    private String getCharacterData(ReducedCharacterCard card) {
        StringBuilder cardData=new StringBuilder();
        cardData.append(card.getId()).append(" (").append(card.getCost()).append(") ");
        try {
            CharID.MemType memType=CharID.MemType.valueOf(card.getMemoryType());
            EnumMap<Colour,?> cardMem;
            switch (memType) {
                case INTEGER_COLOUR_MAP:
                    cardMem=card.getMemory(Integer.class);
                    break;
                case BOOLEAN_COLOUR_MAP:
                    cardMem=card.getMemory(Boolean.class);
                    break;
                case PLAYER_COLOUR_MAP:
                    cardMem=card.getMemory(String.class);
                    break;
                case INTEGER:
                    cardData.append(card.getMemory());
                case NONE:
                default:
                    return cardData.toString();
            }
            if (cardMem==null) {
                return cardData.append(" Error reading data").toString();
            }
            for (Colour c : Colour.values()) {
                cardData.append(windowsTerminal?c.name().toLowerCase().charAt(0)+": ":getDisplayStyle(c.name()));
                cardData.append(memType==CharID.MemType.BOOLEAN_COLOUR_MAP?((Boolean)cardMem.get(c)?"Y":"N"):cardMem.get(c));
                cardData.append(windowsTerminal?"":getDisplayStyle("RESET")).append(" ");
            }
        }
        catch (IllegalArgumentException iae) {
            cardData.append(" memory error");
        }
        return cardData.toString();
    }

    private void clearScreen() {
        try {
            if (windowsTerminal) {
                Runtime.getRuntime().exec("cls");
            } else {
                System.out.println((char)27+properties.getProperty("ANSI_CLEAR"));
                System.out.flush();
            }
        }
        catch (IOException io) {
            System.out.println("Could not clear screen");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //CLI only prints model after every update has been made, as otherwise there would be more prints than needed
        if (evt.getPropertyName().equals(ReducedGame.UpdateType.TURN_STATE.name()) || evt.getPropertyName().equals(ReducedGame.UpdateType.LOADED_GAME.name())) {
            printModel((ReducedGame) evt.getNewValue());
        }
    }

    public Message parseMessage(String[] input) throws MoveNotAllowedException {
        if (input.length==0 || input[0].isEmpty()) {
            System.out.println("For list of commands type help");
            return null;
        }
        Colour diskColour=Colour.parseChosenColour(input[0]);
        if (diskColour!=null) {
            if (input.length<2) {
                System.out.println(getDisplayStyle(Colour.RED.name())+"For list of commands type help"+getDisplayStyle("RESET"));
                return null;
            }
            if (input[1].equalsIgnoreCase("DINING")) {
                return MessageCenter.genMessage(MsgType.DISKTODININGROOM,username,"Dining room",diskColour);
            } else {
                return MessageCenter.genMessage(MsgType.DISKTOISLAND,username,input[1],diskColour);
            }
        }
        StringBuilder arguments=new StringBuilder();
        switch (input[0].toUpperCase()) {
            case "CHAR":
            case "CHARACTER":
                if (input.length<2) {
                    throw new MoveNotAllowedException("Not enough arguments");
                }
                for (int i=2; i<input.length; i++) {
                    arguments.append(input[i]).append(" ");
                }
                return MessageCenter.genMessage(MsgType.PLAYCHARCARD,username,arguments.toString(),input[1]);
            case "ASSIST":
            case "ASSISTANT":
                if (input.length<2) {
                    throw new MoveNotAllowedException("Not enough arguments");
                }
                return MessageCenter.genMessage(MsgType.PLAYASSISTCARD,username,input[1],input[1]);
            case "REFILL":
                if (input.length<2) {
                    throw new MoveNotAllowedException("Not enough arguments");
                }
                return MessageCenter.genMessage(MsgType.CHOOSECLOUD,username,input[1],input[1]);
            case "MN":
                if (input.length<2) {
                    throw new MoveNotAllowedException("Not enough arguments");
                }
                return MessageCenter.genMessage(MsgType.MOVEMN,username,input[1],Integer.parseInt(input[1]));
            case "MSG":
            case "WHISPER":
                if (input.length<3) {
                    throw new MoveNotAllowedException("Not enough arguments");
                }
                for (int i=2; i<input.length; i++) {
                    arguments.append(input[i]).append(" ");
                }
                return MessageCenter.genMessage(MsgType.WHISPER,username,input[1],arguments.toString());
            case "CHARINFO":
                if (input.length<2) {
                    throw new MoveNotAllowedException("Not enough arguments");
                }
                for (int i=1; i<input.length; i++) {
                    arguments.append(input[i]).append("_");
                }
                arguments.deleteCharAt(arguments.length()-1);
                String info;
                boolean found=false;
                if ((info=characterInfo.getProperty(arguments.toString().toUpperCase()+"_SETUP"))!=null) {
                    System.out.println("Setup: "+info);
                    found=true;
                }
                if ((info=characterInfo.getProperty(arguments.toString().toUpperCase()+"_EFFECT"))!=null) {
                    System.out.println("Effect: "+info);
                    found=true;
                }
                if ((info=characterInfo.getProperty(arguments.toString().toUpperCase()+"_HELP"))!=null) {
                    System.out.println("How to use: "+info);
                    found=true;
                }
                if (!found) {
                    throw new MoveNotAllowedException("Character id not found");
                }
                return null;
            case "DISCONNECT":
                return MessageCenter.genMessage(MsgType.DISCONNECT,username,"Disconnecting",null);
            case "HELP":
                System.out.println("Possible commands:\n" +
                        "- char | character [char id] - play character card\n" +
                        "- assist | assistant [assist id] - play assistant card\n" +
                        "- [Colour] dining - moves student disk to dining room\n" +
                        "- [Colour] [island id] - moves student disk to island\n" +
                        "- mn [amount] - move mother nature by the amount specified\n" +
                        "- refill [cloud id] - choose cloud to refill entrance from\n" +
                        "- msg | whisper [username] - send a message to another player\n" +
                        "- charinfo [char name] - prints character card info\n" +
                        "- disconnect - quit game, triggers game over for other players");
                return null;
            default:
                return MessageCenter.genMessage(MsgType.TEXT,username,"Basic response",input[0]);
        }
    }
}
