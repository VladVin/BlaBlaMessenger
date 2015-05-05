package blablamessenger;

import blablamessenger.Server.ClientBase;
import java.net.Socket;

public class ClientReceiver extends Thread {
    
    ClientBase clientBase;
    Socket client;

    public ClientReceiver( ClientBase base, Socket newClient )
    {
        clientBase = base;
        client = newClient;
    }
    
    @Override
    public void run()
    {

    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
}
