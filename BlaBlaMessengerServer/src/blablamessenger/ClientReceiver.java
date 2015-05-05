package blablamessenger;

import blablamessenger.Server.ClientBase;
import data_structures.CommandData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientReceiver extends Thread {
    ClientBase clientBase;
    Socket socket;
    
    ObjectInputStream input;
    
    public void addCommand( CommandData command ) throws InterruptedException
    { commands.put( command ); }

    public ClientReceiver( ClientBase base, Socket newClient )
    {
        clientBase = base;
        socket = newClient;
        READ_TIMEOUT = 5000;
    }
    
    public void registrationNewClient() throws ClassNotFoundException
    {     
        try {
            input = new ObjectInputStream( socket.getInputStream() );
            String clientName = (String) input.readObject();
            
            new ClientProcessor( clientBase, socket, 
                    clientName, this, commands ).start();
        } catch ( IOException ex ) {
            Logger.getLogger(ClientReceiver.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {   
        try {
            registrationNewClient();
        } catch ( ClassNotFoundException ex ) {
            Logger.getLogger(ClientReceiver.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        try {
            socket.setSoTimeout( READ_TIMEOUT );
        } catch ( SocketException ex ) {
            Logger.getLogger( ClientReceiver.class.getName() ).
                    log(Level.SEVERE, null, ex);
        }
        
        while ( !this.isInterrupted() ) {
            
        }
    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
        
    private LinkedBlockingQueue commands = new LinkedBlockingQueue();
    private final int READ_TIMEOUT;
}
