package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Properties;

public class CLI implements View {
    private String username=null;
    private final BufferedReader in;
    private ObjectOutputStream toServer=null;
    private final boolean windowsTerminal;
    private final Properties properties=new Properties();
    private final Properties characterInfo=new Properties();

    public CLI() {
        this.in=new BufferedReader(new InputStreamReader(System.in));
        try {
            this.properties.load(getClass().getResourceAsStream("/Assets/CLI/CLI.properties"));
            this.characterInfo.load(getClass().getResourceAsStream("/CardData/CharacterCards.properties"));
        }
        catch (IOException io) {
            display(getDisplayColour(Colour.RED.name(),false));
            display("Could not find properties file, defaulting to basic CLI");
            resetDisplayStile();
        }
        this.windowsTerminal=System.getProperty("os.name").contains("Windows");
    }
    @Override
    public void run() {
        try {
            display(getDisplayColour(Colour.RED.name(),false));
            String line;
            BufferedReader print=new BufferedReader(new FileReader(properties.getProperty("LOGO")));
            while ((line=print.readLine())!=null) {
                display(line);
            }
            resetDisplayStile();
        }
        catch (IOException io) {
            display("Eriantys");
        }
        while (toServer==null) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ignored) {
            }
        }
        Message outMessage=null;
        while (outMessage==null || outMessage.getSubType()!=MsgType.CLOSE) {
            try {
                outMessage=parseMessage(in.readLine().split(" "));
                if (outMessage!=null) {
                    sendMessage(outMessage);
                }
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
        }
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
        this.toServer=toServer;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            toServer.writeObject(message);
            toServer.flush();
            toServer.reset();
        }
        catch (IOException io) {
            display("Could not send message");
        }
    }
    @Override
    public String setUsername() {
        String username=null;
        while (username==null) {
            display("Username: ");
            try {
                username=in.readLine();
            }
            catch (IOException io) {
                display("\nError reading input");
            }
        }
        this.username=username;
        return username;
    }
    @Override
    public String setIP(String defaultIP) {
        String ip;
        try {
            System.out.print("Server ip (local or empty for default localhost): ");
            ip=in.readLine();
            if (ip.equals("") || ip.equals("local")) {
                return defaultIP;
            }
            return ip;
        }
        catch (IOException io) {
            display("Error reading input");
            return null;
        }
    }
    @Override
    public int setPort(int defaultPort) {
        String port=null;
        try {
            System.out.print("Server port (local or empty for default 50033): ");
            port=in.readLine();
            if (port.equals("") || port.equals("local")) {
                return defaultPort;
            }
            return Integer.parseInt(port);
        }
        catch (IOException io) {
            display("Error reading input");
            return -1;
        }
        catch (NumberFormatException nfe) {
            display(port+" is not a number");
            return -1;
        }
    }
    @Override
    public void display(String message) {
        System.out.println(message);
    }
    public String getDisplayColour(String colour, boolean background) {
        if (background) {
            return (char)27+properties.getProperty(("ANSI_BG_"+colour.toUpperCase()));
        } else {
            return (char)27+properties.getProperty(("ANSI_"+colour.toUpperCase()));
        }
    }
    public void resetDisplayStile() {
        System.out.print((char)27+properties.getProperty("ANSI_RESET"));
        System.out.print((char)27+properties.getProperty("ANSI_BG_RESET"));
    }

    private void printModel(ReducedGame model) {
        String dash="▬",wall="▌";
        ReducedPlayer firstPlayer,secondPlayer;
        int schoolBoardLength=36;
        String schoolBoardDelimiter=String.format("%-"+schoolBoardLength+"s",dash.repeat(windowsTerminal?23:schoolBoardLength));
        int cardDelimiter=windowsTerminal?5:8;
        clearScreen();
        StringBuilder modelUI=new StringBuilder();
        for (int i=0; i<2; i++) {
            firstPlayer=model.getPlayers().size()>i*2?model.getPlayers().get(i*2):null;
            secondPlayer=model.getPlayers().size()>i*2+1?model.getPlayers().get(i*2+1):null;
            modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer!=null? model.getPlayers().get(i*2).getName()+"'s SchoolBoard":"")).append("  ");
            modelUI.append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?model.getPlayers().get(i*2+1).getName()+"'s SchoolBoard":""));
            modelUI.append(firstPlayer!=null?"\n":"");
            modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer!=null?("Team: "+firstPlayer.getTeam()+(model.isExpertMode()?"  Coins: "+firstPlayer.getSchoolBoard().getCoins():"")):""));
            modelUI.append("  ").append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?("Team: "+secondPlayer.getTeam()+(model.isExpertMode()?"  Coins: "+secondPlayer.getSchoolBoard().getCoins():"")):"")).append("\n");
            if (firstPlayer!=null) {
                modelUI.append(schoolBoardDelimiter).append("  ").append(secondPlayer!=null?schoolBoardDelimiter:" ").append("\n");
            }
            for (Colour c : Colour.values()) {
                if (firstPlayer!=null) {
                    modelUI.append(getSchoolBoardRow(c,firstPlayer.getTeam(),firstPlayer.getSchoolBoard()));
                }
                if (secondPlayer!=null) {
                    modelUI.append(getSchoolBoardRow(c,secondPlayer.getTeam(),secondPlayer.getSchoolBoard()));
                } else {
                    modelUI.append(" ".repeat(schoolBoardLength));
                }
                if (firstPlayer!=null) {
                    modelUI.append("\n");
                }
            }
            if (firstPlayer!=null) {
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
                modelUI.append(getCharacterData(model.getCharacterCards().get(i)));
                modelUI.append(i== model.getCharacterCards().size()-1?"\n":" | ");
            }
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
        System.out.println(modelUI);
    }

    private String getSchoolBoardRow(Colour c, Team t, ReducedSchoolBoard board) {
        String wall="▌";
        String fontColour=getDisplayColour(c.name(),false);
        String resetColour=getDisplayColour ("RESET",false);
        StringBuilder schoolBoardRow=new StringBuilder();
        schoolBoardRow.append(wall).append(windowsTerminal?c.name().charAt(0)+"_":fontColour+" ♦").append(board.getEntrance().get(c)).append(windowsTerminal?"":resetColour).append(wall);
        schoolBoardRow.append(windowsTerminal?(" "+c.name().charAt(0)).repeat(board.getDiningRoom().get(c)):fontColour+" ♦".repeat(board.getDiningRoom().get(c))+resetColour);
        schoolBoardRow.append(" ♦".repeat(10-board.getDiningRoom().get(c))).append(" ").append(wall);
        schoolBoardRow.append(board.getProfessorTable().get(c)?(windowsTerminal?c.name().charAt(0):fontColour+"◘"+resetColour):"◘").append(" ").append(wall);
        schoolBoardRow.append(board.getTowers()>=((2*c.ordinal()+1))?"▲ ":"  ").append((board.getTowers()>=(c.ordinal()+1)*2)?"▲ ":"  ").append(wall).append("  ");
        return schoolBoardRow.toString();
    }

    private String getTileStats(String id, EnumMap<Colour,Integer> contents, boolean motherNature, Team team, int towerNum) {
        StringBuilder tileStats=new StringBuilder();
        tileStats.append(!windowsTerminal && motherNature?getDisplayColour(Colour.PURPLE.name(),true):"");
        tileStats.append(id).append(windowsTerminal && motherNature?"(X):":": ");
        tileStats.append(!windowsTerminal && motherNature?getDisplayColour("RESET",true):"");
        for (Colour c : Colour.values()) {
            tileStats.append(windowsTerminal?" "+c.name().charAt(0)+"_":getDisplayColour(c.name(),false)).append(contents.get(c)).append(" ");
        }
        if (team!=null) {
            tileStats.append(" ").append(windowsTerminal?team.name() + "_" : getDisplayColour(team.name(), false)).append("▲".repeat(towerNum));
            if (!windowsTerminal) {
                tileStats.append(getDisplayColour("RESET", false)).append(getDisplayColour("RESET", true));
            }
        }
        tileStats.append(windowsTerminal?"":getDisplayColour("RESET",false));
        return tileStats.toString();
    }

    private String getCharacterData(ReducedCharacterCard card) {
        return card.getId();
        //TODO add memory data
    }

    private void clearScreen() {
        try {
            String command=windowsTerminal?"cls":"clear";
            Runtime.getRuntime().exec(command);
        }
        catch (IOException io) {
            System.out.println("Could not clear screen");
        }
    }

    public Message parseMessage(String[] input) {
        if (input.length==0) {
            display("For list of commands type help");
            return null;
        }
        Colour diskColour=Colour.parseChosenColour(input[0]);
        if (diskColour!=null) {
            if (input.length<2) {
                display("Error, not enough arguments");
                return null;
            }
            if (input[1].equalsIgnoreCase("DINING")) {
                return MessageCenter.genMessage(MsgType.DISKTODININGROOM,username,"Dining room",diskColour);
            } else {
                return MessageCenter.genMessage(MsgType.DISKTOISLAND,username,input[1],diskColour);
            }
        }
        switch (input[0].toUpperCase()) {
            case "CHAR":
            case "CHARACTER":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                String options="";
                for (int i=2; i<input.length; i++) {
                    options=options.concat(input[i]).concat(" ");
                }
                return MessageCenter.genMessage(MsgType.PLAYCHARCARD,username,input[1],options);
            case "ASSIST":
            case "ASSISTANT":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                return MessageCenter.genMessage(MsgType.PLAYASSISTCARD,username,input[1],input[1]);
            case "REFILL":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                return MessageCenter.genMessage(MsgType.CHOOSECLOUD,username,input[1],input[1]);
            case "MN":
            case "MOTHERNATURE":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                return MessageCenter.genMessage(MsgType.MOVEMN,username,input[1],Integer.parseInt(input[1]));
            case "MSG":
            case "WHISPER":
                if (input.length<3) {
                    display("Not enough arguments");
                    return null;
                }
                String message="";
                for (int i=2; i<input.length; i++) {
                    message=message.concat(input[i]+" ");
                }
                return MessageCenter.genMessage(MsgType.WHISPER,username,input[1],message);
            case "CHARINFO":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                String info;
                boolean found=false;
                System.out.println(input[1]+"_setup");
                System.out.println(characterInfo.getProperty("monk_setup"));
                if ((info=characterInfo.getProperty(input[1].toLowerCase()+"_setup"))!=null) {
                    display("Setup: "+info);
                    found=true;
                }
                if ((info=characterInfo.getProperty(input[1].toLowerCase()+"_effect"))!=null) {
                    display("Effect:"+info);
                    found=true;
                }
                if (!found) {
                    System.out.println("Character id not found");
                }
                return null;
            case "HELP":
                display("Possible commands:\n" +
                        "- [Colour] dining - moves student disk to dining room\n" +
                        "- [Colour] [island id] - moves student disk to island\n" +
                        "- char | character [char id] - play character card\n" +
                        "- assist | assistant [assist id] - play assistant card\n" +
                        "- refill [cloud id] - choose cloud to refill entrance from\n" +
                        "- msg | whisper [username] - send a message to another player\n" +
                        "- charinfo [char name] - prints character card info");
                return null;
            default:
                return MessageCenter.genMessage(MsgType.TEXT,username,"Basic response",input[0]);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        printModel((ReducedGame) evt.getNewValue());
    }
}
