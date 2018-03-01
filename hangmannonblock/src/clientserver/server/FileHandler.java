package clientserver.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * This class retrieves the lines of a text document. The lines are used for the hangman game.
 */
public class FileHandler {
    
    private static final Path PATH = Paths.get("resources/words.txt");
    private List<String> lines;

    public FileHandler() {
        try{
            lines = Files.readAllLines(PATH);
        }
        catch(IOException e){
            throw new RuntimeException("Couldn't find specified file in path " + "'" + PATH + "'");
        }
    }

    public char[] getWord() {
        Random rand = new Random();
        return lines.get(rand.nextInt(lines.size()-1)).toCharArray();
    }
}
