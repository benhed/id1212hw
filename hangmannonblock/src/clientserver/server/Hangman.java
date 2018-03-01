
package clientserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;
/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Hangman implements Runnable{
    
    private final String PROMPT = "Guess a letter or word.";
    private final Client client;
    private GuessCheck guessCheck = null;
    private String clientGuess;
    private Server server = null;
    private final SocketChannel clientChannel;
    private final ByteBuffer receivedMsg = ByteBuffer.allocateDirect(100);
    private final Queue<ByteBuffer> sendQueue = new ArrayDeque<>();
    
    public Hangman(Server server, SocketChannel clientChannel) {
        this.server = server;
        this.guessCheck = new GuessCheck();
        this.client = new Client();
        this.clientChannel = clientChannel;
    }
    
    @Override
    public void run() {
        guessCheck.check(clientGuess, client.session);
        ByteBuffer result = ByteBuffer.wrap(outcome().getBytes());
        Qsend(result);
        server.notifySend(result);      
    }

    
    public void receive() throws IOException {
        receivedMsg.clear();
        int readBytes;
        readBytes = clientChannel.read(receivedMsg);
        if(readBytes == -1){
            throw new IOException("No connection to client");
        }
        receivedMsg.flip();
        byte[] bytes = new byte[receivedMsg.remaining()];
        receivedMsg.get(bytes);
        clientGuess = new String(bytes);
        
        ForkJoinPool.commonPool().execute(this);//Interpreting thread, to not interfere with communicating thread
    }

     private String outcome() {
        if(Arrays.equals(client.session.guessed, client.session.word)){
            client.session.score++;
            String prevWord = String.valueOf(client.session.word);
            client.session.triesLeft = client.session.word.length;
            client.generateWord();
            return "You won. The correct word was indeed: " + prevWord + ". Your score is currently: " + client.session.score + "\n\n" + PROMPT;
        }
        else if(client.session.triesLeft <= 0){
            client.session.score--;
            String prevWord = String.valueOf(client.session.word);
            client.session.triesLeft = client.session.word.length;
            client.generateWord();
            return "You lost. The correct word was: " + prevWord + ". Your score is currently: " + client.session.score + "\n\n" + PROMPT;
        }
        else{
            return Arrays.toString(client.session.guessed) + " You have " + client.session.triesLeft + " tries remaining." + "\n" + PROMPT;
        }
    }

    public void Qsend(ByteBuffer msg){
        synchronized(sendQueue){
            sendQueue.add(msg.duplicate());
        }
    }

    public void send() throws IOException{
        ByteBuffer msg;
        synchronized(sendQueue){
            while((msg = sendQueue.peek()) != null){
                clientChannel.write(msg);
                if(msg.hasRemaining()){
                    throw new IOException("Failed to send message");
                }
                sendQueue.remove();
            }
        }
    }
}
