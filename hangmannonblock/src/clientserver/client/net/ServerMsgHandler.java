package clientserver.client.net;

/**
 * This is an interface for sending messages handleReceivedMsg from the server to the client without breaking the MVC pattern.
 *
 */
public interface ServerMsgHandler {
    
    void handleReceivedMsg(String msg);
}