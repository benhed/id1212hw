package clientserver.server;

/**
 *
 */
public class GuessCheck {

    public GuessCheck(){};
    
    //Compares the guess to the word chosen by the server
    public void check(String guess, ClientSession session)
    {
        char[] guessed = guess.toLowerCase().toCharArray();
        if(guessed.length == 1){
            checkGuess(guessed[0], session);
        }
        else{
            checkGuess(guessed, session);
        }
    }
    
    //Used when user guesses a whole word
    private void checkGuess(char[] word, ClientSession session) {
        for(int i = 0; i < word.length; i++){
            if(session.word[i] != word[i]){
                session.triesLeft--;
                System.out.println(session.word);
                return;
            }
        }
        session.guessed = word;
    }
    
    //Used when a user guesses a single letter
    private void checkGuess(char c, ClientSession session) {
        boolean found = false;
        for(int i = 0; i < session.word.length; i++){
            if(session.word[i] == c){
                session.guessed[i] = c;
                found = true;
            }
        }
        if(!found){
            session.triesLeft--;
        }
    }
    
}
