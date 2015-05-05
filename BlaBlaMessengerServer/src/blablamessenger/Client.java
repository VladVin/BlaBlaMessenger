package blablamessenger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    final int port = 2671;
    InetAddress ip = null;
     
    public void start()
    {   
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        try {
            Socket server = new Socket( ip, port );             
           
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).
                    log(Level.SEVERE, null, ex);
        }  
    }
    
    public static void main(String[] args)
    {
        new Client().start();
    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
}
