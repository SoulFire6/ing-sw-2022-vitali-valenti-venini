package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.Message;

import java.io.ObjectOutputStream;

public interface View extends Runnable {
    void setToServer(ObjectOutputStream toServer);
    void sendMessage(Message message);
    String setUsername();
    String setIP(String defaultIp);
    int setPort(int defaultPort);
    void display(String message);
    void modelSync(ReducedGame model);
}
