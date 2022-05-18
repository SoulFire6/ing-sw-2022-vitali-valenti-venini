package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.Message;

import java.io.ObjectOutputStream;

public interface View {
    void main();
    void setToServer(ObjectOutputStream toServer);
    void setModel(ReducedGame model);
    void sendMessage(Message message);
    String setUsername();
    String setIP(String defaultIp);
    int setPort(int defaultPort);
    void display(String message);
}
