package clientserver.client.view;

/**
 * Provides synchronized console output functions.
 */
class SynchronizedOutput {
    synchronized void println(String out){
        System.out.println(out);
    }   
}
