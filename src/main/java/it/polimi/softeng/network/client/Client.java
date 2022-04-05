package it.polimi.softeng.network.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private static final ArrayList<String> errorMessages=new ArrayList<>(Arrays.asList("Disconnected","SERVER FULL","SERVER CLOSED"));
    public static void main(String[] args) throws IOException {
        String inputLine, outputLine;
        Scanner sc= new Scanner(System.in);
        Socket socket=new Socket("127.0.0.1",50033);
        PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while((inputLine=in.readLine())!=null) {
            if (errorMessages.contains(inputLine)) {
                System.out.println("ENDING");
                break;
            }
            System.out.println(inputLine);
            out.println(sc.nextLine());
        }
        out.close();
        in.close();
        socket.close();
    }
}
