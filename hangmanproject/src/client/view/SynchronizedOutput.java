package client.view;

/**
 * Created by Benjamin and Anton on 2017-11-17.
 */
public class SynchronizedOutput {
    synchronized void println(String output){
        System.out.println(output);
    }
}
