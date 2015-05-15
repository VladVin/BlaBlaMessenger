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
    public void addCommand( Command command ) { commands.add( command ); }

    public ClientReceiver( ClientBase base, Socket newClient )
    {
        clientBase = base;
        client = newClient;
        
        try {
            input = new ObjectInputStream( client.getInputStream() );
        } catch ( IOException ex ) {
            Logger.getLogger( ClientReceiver.class.getName()).
                    log(Level.SEVERE, null, ex );
        }
    }
    
    @Override
    public void run()
    {
        createProcessor();
        
        while ( true ) {
            try {
                addLog( ClientReceiver.class.getName() + 
                        ": waiting for new command" );
                CommandData newCommand = ( CommandData ) input.readObject();
                addCommand( new Command( Sources.Client, newCommand ) );
                addLog( ClientReceiver.class.getName() + 
                        ": added new command" );
            } catch ( IOException | ClassNotFoundException ex ) {
                break;
            }
        }
        
        addLog( ClientReceiver.class.getName() + 
                ": released" );
    }
    
    private void createProcessor()
    {
        new ClientProcessor( clientBase, client, this, commands ).start();
    }
    
    private void addLog( String log ) { System.out.println( log ); }
    
    private ClientBase clientBase;
    private Socket client;
    
    private ObjectInputStream input;
    private ConcurrentLinkedQueue< Command > commands = 
            new ConcurrentLinkedQueue<>();
}
