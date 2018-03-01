package client.view;


import client.connection.ServerMsgHandler;

/**
 * Created by Benjamin on 2017-11-23.
 */
class ServerOutput implements ServerMsgHandler {

    SynchronizedOutput output = new SynchronizedOutput();

    public void handleReceivedMsg(String msg){
        output.println(msg);
    }
}


