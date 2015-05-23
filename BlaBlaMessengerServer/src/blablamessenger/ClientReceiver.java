package blablamessenger;

import blablamessenger.Server.ClientBase;
import blablamessenger.Command.Sources;
import blablamessenger.Server.FileBase;

import data_structures.CommandData;
import data_structures.Commands;
import data_structures.ContactId;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientReceiver extends Thread {
    public void addCommand( Command command ) { commands.add( command ); }

    public ClientReceiver( ClientBase clientBase, FileBase fileBase, 
        ContactId myContact, Socket newClient )
    {
        this.clientBase = clientBase;
        client = newClient;
        this.fileBase = fileBase;
        this.myContact = myContact;
        
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
                addLog( "waiting for new command" );
                CommandData newCommand = ( CommandData ) input.readObject();
                addLog( "get " + newCommand.Command.name() + " command" );
                if ( registered && 
                        newCommand.Command != Commands.RegisterContact ) {
                    addCommand( new Command( Sources.Client, newCommand ) );
                    addLog( "added new command" );                    
                } else if ( newCommand.Command == Commands.RegisterContact ) {
                    registered = true;
                    addCommand( new Command( Sources.Client, newCommand ) );
                    addLog( "added new command" );
                }
            } catch ( IOException | ClassNotFoundException ex ) {
                break;
            }
        }
        
        releaseProcessor();
        addLog( "released" );
        
    }
    
    private void createProcessor()
    { 
        new ClientProcessor( clientBase, fileBase, client, myContact, 
            this, commands ).start();
    }
    private void releaseProcessor()
    { addCommand( new Command( Sources.Client, Commands.Disconnect, null ) ); }
    
    private void addLog( String log ) 
    { 
        System.out.println( ClientReceiver.class.getName() + ": " + log );
    }
    
    private final ClientBase clientBase;
    private final FileBase fileBase;
    private final Socket client;
    private final ContactId myContact;
    
    private ObjectInputStream input;
    private ConcurrentLinkedQueue< Command > commands = 
            new ConcurrentLinkedQueue<>();
    private boolean registered = false;
}
