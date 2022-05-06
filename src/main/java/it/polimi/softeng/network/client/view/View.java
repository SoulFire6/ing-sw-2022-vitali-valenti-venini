package it.polimi.softeng.network.client.view;

import java.net.Socket;

public interface View {
    String setUsername();
    String setIP(String defaultIp);
    int setPort(int defaultPort);
    void display(String message);
}
