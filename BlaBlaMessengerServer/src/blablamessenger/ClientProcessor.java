package blablamessenger;

import blablamessenger.Server.ClientBase;

import data_structures.Command;
import data_structures.Conference;
import data_structures.Contact;
import data_structures.Contacts;
import data_structures.ResultData;
import data_structures.ResultTypes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientProcessor extends Thread {
    ClientBase clientBase;
    Socket socket;
    
    ObjectOutputStream output;
    UUID client;
    
    boolean running = true;
    
    
    public ClientProcessor( ClientBase base, Socket myClient, String name,
            ClientReceiver receiver, ConcurrentLinkedQueue inputCommands )
    {
        clientBase = base;
        socket = myClient;
        commands = inputCommands;
        
        try {
            output = new ObjectOutputStream( socket.getOutputStream() );
            
            client = UUID.randomUUID();
            addLog( ClientProcessor.class.getName() + 
                    ": writing clients' uuid -- " +
                    client.toString() );
            output.writeObject( client );
            output.flush();
            addLog( ClientProcessor.class.getName() + 
                    ": wrote client's uuid" );
            
            clientBase.addContact( new Contact( name, client ) );
            clientBase.addClient( client, receiver );
            addLog( ClientProcessor.class.getName() + 
                    ": added new contact and client to client's base" );
        } catch (IOException ex) {
            Logger.getLogger(ClientProcessor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public Command getCommand()
    {   
        Command command = null;
        while ( command == null ) {
            command = commands.poll();
        }
        return command;
    }
    
    @Override
    public void run()
    {
        while ( running ) {
            addLog( ClientProcessor.class.getName() + 
                    ": waiting for new command" );
            Command command = getCommand();
            switch ( command.Command ) {
                case Disconnect:
                    addLog( ClientProcessor.class.getName() + 
                            ": get disconnect command" );
                    disconnect();
                break;
                case RefreshContacts:
                    addLog( ClientProcessor.class.getName() + 
                            ": get refresh contacts command" );
                    refreshContacts();
                break;
                case AddToConference:
                    addLog( ClientProcessor.class.getName() + 
                            ": get add to conference command" );
                break;
            }
        }
    }    
    
    private void disconnect()
    {
        clientBase.removeContact( client );
        clientBase.removeClient( client );
        
        myConferences.stream().forEach( ( UUID conference ) -> {
            Conference myConference = clientBase.getConference( conference );
            synchronized ( myConference ) {
                myConference.ContactsIDs.remove( client );
                if ( myConference.ContactsIDs.isEmpty() ) {
                    clientBase.removeConference( conference );
                }
            }
        });
        
        try {
            output.close();
        } catch ( IOException ex ) {
            Logger.getLogger( ClientProcessor.class.getName() ).
                    log( Level.SEVERE, null, ex );
        }
        
        running = false;
    }
    
    private void refreshContacts()
    {
        Contacts contacts = new Contacts( clientBase.getContacts() );
        ResultData result = new ResultData( ResultTypes.UpdatedContacts,
                contacts );
        
        writeResult( result );
    }
    
    private void addToConference( Command command )
    {
        switch ( command.Source ) {
            case Client:
                
            break;
            case Server:
                
            break;
            default:
                
            break;
        }
    }
    private void notifyNewMemberConference()
    {
        
    }
    
    private void writeResult( ResultData result )
    {
        try {
            output.writeObject( result );
            output.flush();
        } catch (IOException ex) {
            Logger.getLogger( ClientProcessor.class.getName() ).
                    log(Level.SEVERE, null, ex);
        }        
    }
    
    private void addLog( String log )
    {
        System.out.println( log );
    }
    
    private ConcurrentLinkedQueue< Command > commands = 
            new ConcurrentLinkedQueue<>();
    private ArrayList< UUID > myConferences = new ArrayList<>();
}
