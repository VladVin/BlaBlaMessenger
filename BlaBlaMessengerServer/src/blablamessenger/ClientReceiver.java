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
    
    public void addCommand( CommandData command )
    {   
        try {
            commands.put( command );
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientReceiver.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public ClientReceiver( ClientBase base, Socket newClient )
    {
        clientBase = base;
        socket = newClient;
        READ_TIMEOUT = 50;
    }
    
    public void registrationNewClient()
    {     
        try {
            input = new ObjectInputStream( socket.getInputStream() );
            String clientName = (String) input.readObject();
            
            new ClientProcessor( clientBase, socket, 
                    clientName, this, commands ).start();
        } catch ( IOException | ClassNotFoundException ex ) {
            Logger.getLogger(ClientReceiver.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {   
        registrationNewClient();
        
        try {
            socket.setSoTimeout( READ_TIMEOUT );
        } catch ( SocketException ex ) {
            Logger.getLogger( ClientReceiver.class.getName() ).
                    log(Level.SEVERE, null, ex);
        }
        
        while ( !this.isInterrupted() ) {
            try {
                CommandData command = ( CommandData ) input.readObject();
                addCommand( command );
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ClientReceiver.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
        
    private LinkedBlockingQueue commands = new LinkedBlockingQueue();
    private final int READ_TIMEOUT;
}
