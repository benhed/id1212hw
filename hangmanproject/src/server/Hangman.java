package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by Benjamin and Anton on 2017-11-23.
 */
public class Hangman implements Runnable {
    private final String PROMPT = "Guess a letter or word.";
    private Client client;
    private GuessCheck guessCheck = null;
    Server server = null;
    BufferedReader in;
    PrintWriter out;
    private boolean connected = true;

    public Hangman(Client client, Server server) {
        this.server = server;
        this.client = client;
        this.guessCheck = new GuessCheck();

        try {
            in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            out = new PrintWriter(client.socket.getOutputStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                String received = read();
                if (received != null) {
                    guessCheck.check(received, client.session);
                    send(outcome());
                }
            } catch (IOException e) {
                try {
                    client.socket.close();
                    connected = false;
                } catch (IOException e2) {
                    throw new RuntimeException("Failed to close connection");
                }
                throw new RuntimeException("Failed to read the request from client");
            }
        }
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

    private String read() throws IOException{
        String line = in.readLine();
        return line;
    }

    private void send(String msg) throws IOException{
        out.println(msg);
        out.flush();
    }
}
