package clientserver.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 *
 *
 */
public class ServerConnection implements Runnable{
    private volatile boolean isConnected;
    private InetSocketAddress address;
    private SocketChannel channel;
    private Selector selector;
    private final List<ServerMsgHandler> listeners = new ArrayList<>();
    private final ByteBuffer receivedMsg = ByteBuffer.allocateDirect(100);
    private final Queue<ByteBuffer> sendQueue = new ArrayDeque();
    private boolean isSending = false;
    
    @Override
    public void run(){
        try{
            initConn();
            initSelector();
            
            while(isConnected || !sendQueue.isEmpty()){
                //Inform the selector that we want to send a message
                if(isSending){
                    channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    isSending = false;
                }
                //select will return when a connection is established
                // and it will prevent the rest of the while loop from blocking
                selector.select();
                for(SelectionKey key : selector.selectedKeys()){
                    selector.selectedKeys().remove(key);
                    if(key.isValid()){
                        if(!key.isConnectable()){
                            if(key.isReadable()){
                                receive(key);
                            } else
                            if(key.isWritable()){
                                send(key);
                            }
                        } else
                            channel.finishConnect();
                            key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }    
        } catch(IOException ioe){
            throw new RuntimeException(ioe);
        }        
    }
    
    public void connect(String server, int port, ServerMsgHandler listener){
        this.address = new InetSocketAddress(server, port);
        listeners.add(listener);
        new Thread(this).start();
    }

    private void informReceived(String str){
        Executor pool = ForkJoinPool.commonPool();
        listeners.forEach((listener) -> {
            pool.execute(() -> {
                listener.handleReceivedMsg(str);
            });
        });
    }
    
    public void sendQueue(String msg){
        sendQueue.add(ByteBuffer.wrap(msg.getBytes()));
        isSending = true;
        selector.wakeup();
    }
    
    private void send(SelectionKey key) throws IOException{
        ByteBuffer msg;
        synchronized(sendQueue){
            while((msg = sendQueue.peek()) != null){
                channel.write(msg);
                if(msg.hasRemaining()) return;
                sendQueue.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }
    
    private void receive(SelectionKey key) throws IOException{
        receivedMsg.clear();
        int length = channel.read(receivedMsg);
        if(length == -1){
            throw new IOException("Server reply empty");
        }
        
        receivedMsg.flip();
        byte[] receivedBytes = new byte[receivedMsg.remaining()];
        receivedMsg.get(receivedBytes);
        String receivedStr = new String(receivedBytes);
        informReceived(receivedStr);
    }

    private void initConn() throws IOException{
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        isConnected = true;
    }

    private void initSelector() throws IOException{
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

}
