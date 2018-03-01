package clientserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server{
    private static final int LINGERTIME = 10000;
    private static final int PORT = 7080;
    
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private boolean isSending = false;
    
    public static void main(String[] args){
        Server server = new Server();
        server.start();
        }

    public Server(){}
    
    public void start(){
        try{
            initSelector();
            initListeningChannel();
        } catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
        serve();
    }
    
    public void notifySend(ByteBuffer message){
        isSending = true;
        selector.wakeup();
    }
     
    public void serve(){
        try{
            while(true) {
                if(isSending){
                    timeToWrite();
                    isSending = false;
                }
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isValid()){
                        if(key.isAcceptable()){
                            startHandler(key);
                        } else
                        if(key.isReadable()){
                            receive(key);
                        } else
                        if(key.isWritable()){
                            send(key);
                        }
                    }
                }
            }
        } catch(IOException ioe){
            throw new RuntimeException(ioe);
        }
    }

    private void startHandler(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept(); //Establish connection
        clientChannel.configureBlocking(false);
        Hangman hangman = new Hangman(this, clientChannel);
        clientChannel.register(selector, SelectionKey.OP_READ, new ServerClient(hangman));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGERTIME);
    }
    
    private void receive(SelectionKey key) throws IOException{
        ServerClient serverClient = (ServerClient) key.attachment();
        try{
            serverClient.runner.receive();
        } catch (IOException e){
            throw new IOException("Client has closed connection. GG. No code has been added to handle this case, sadly :(", e);
        }
    }
    
    private void send(SelectionKey key) throws IOException{
        ServerClient serverClient = (ServerClient) key.attachment();
        try {
            serverClient.send();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException ioe){
            throw new IOException("error isSending", ioe);
        }
    }
    
    private void timeToWrite() {
        selector.keys().stream().filter((key) -> (key.channel() instanceof SocketChannel && key.isValid())).forEachOrdered((key) -> {
            key.interestOps(SelectionKey.OP_WRITE);
        });
    }

    protected class ServerClient{
        private final Hangman runner;
        
        private ServerClient(Hangman runner){
            this.runner = runner;
        }
        
        private void send() throws IOException {
            runner.send();     
        }
    }

    private void initSelector() throws IOException{
        selector = Selector.open();
    }

    private void initListeningChannel()throws IOException{
        listenChannel = ServerSocketChannel.open();
        listenChannel.configureBlocking(false);
        listenChannel.bind(new InetSocketAddress(PORT));
        listenChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
}
