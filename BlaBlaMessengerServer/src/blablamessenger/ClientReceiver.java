package blablamessenger;

import blablamessenger.Server.ClientBase;

import blablamessenger.Command.Sources;
import data_structures.CommandData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientReceiver extends Thread {
    ClientBase clientBase;
    Socket socket;
    
    ObjectInputStream input;
    
    public void addCommand( CommandData command )
    {   
        commands.add( new Command ( Sources.Client, command ) );
    }

    public ClientReceiver( ClientBase base, Socket newClient )
    {
        clientBase = base;
        socket = newClient;
    }
    
    public void registrationNewClient()
    {     
        try {
            addLog( ClientReceiver.class.getName() + 
                    ": waiting for client's name" );
            input = new ObjectInputStream( socket.getInputStream() );
            String clientName = ( String ) input.readObject();
            addLog( ClientReceiver.class.getName() + 
                    ": get client's name -- " +
                    clientName );
            
            new ClientProcessor( clientBase, socket, 
                    clientName, this, commands ).start();
            addLog( ClientReceiver.class.getName() + 
                    ": start new client processor" );
        } catch ( IOException | ClassNotFoundException ex ) {
            Logger.getLogger(ClientReceiver.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {   
        registrationNewClient();
        
        while ( true ) {
            try {
                addLog( ClientReceiver.class.getName() + 
                        ": waiting for new command" );
                addCommand( ( CommandData ) input.readObject() );
                addLog( ClientReceiver.class.getName() + 
                        ": added new command" );
            } catch ( IOException | ClassNotFoundException ex ) {
                break;
            } 
        }
        
        addLog( ClientReceiver.class.getName() + 
                ": released" );
    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
        
    private ConcurrentLinkedQueue< Command > commands = 
            new ConcurrentLinkedQueue<>();
}
