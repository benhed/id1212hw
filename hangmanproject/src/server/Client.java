package server;

import java.net.Socket;

/**
 * Created by Benjamin and Anton on 2017-11-17.
 */
public class Client {
    protected Socket socket;
    protected ClientSession session;
    protected FileHandler fh;

    public Client() {
        this.session = new ClientSession();
        this.fh = new FileHandler();
        this.generateWord();
    }

    protected void generateWord(){
        char[] newWord = this.fh.getWord();
        setWord(newWord, this.session);
        this.session.triesLeft = this.session.word.length;
    }

    private void setWord(char[] word, ClientSession session){
        session.word = word;
        for(char c : word){
            c = Character.toLowerCase(c);
        }
        session.guessed = new char[word.length];
        for(int i = 0; i < session.guessed.length; i++) {
            session.guessed[i] = ' ';
        }
    }
}
