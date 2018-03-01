package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * Created by Benjamin and Anton on 2017-11-17.
 */
public class FileHandler {
    private static final Path PATH = Paths.get("src/words.txt");
    private List<String> lines;

    protected FileHandler() {
        try{
            lines = Files.readAllLines(PATH);
        }
        catch(IOException e){
            throw new RuntimeException("Couldn't find specified file in path " + "'" + PATH + "'");
        }
    }

    protected char[] getWord()
    {
        Random rand = new Random();
        return lines.get(rand.nextInt(lines.size()-1)).toCharArray();
    }

}
