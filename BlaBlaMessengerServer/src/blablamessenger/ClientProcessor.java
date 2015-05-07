package blablamessenger;

import blablamessenger.Server.ClientBase;
import data_structures.CommandData;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientProcessor extends Thread {
    ClientBase clientBase;
    Socket socket;
    
    ObjectOutputStream output;
    UUID client;
    
    boolean running = true;
    
    
    public ClientProcessor( ClientBase base, Socket myClient, String name,
            ClientReceiver receiver, LinkedBlockingQueue inputCommands )
    {
        clientBase = base;
        socket = myClient;
        commands = inputCommands;
        
        try {
            output = new ObjectOutputStream( socket.getOutputStream() );
            
            client = UUID.randomUUID();
            output.writeObject( client );
            output.flush();
            
            clientBase.addContact( new Contact( name, client ) );
            clientBase.addClient( client, receiver );
        } catch (IOException ex) {
            Logger.getLogger(ClientProcessor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public CommandData getCommand()
    {   
        try {
            return ( CommandData ) commands.take();
        } catch ( InterruptedException ex ) {
            Logger.getLogger(ClientProcessor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void run()
    {
        while ( running ) {
            CommandData command = getCommand();
            switch ( command.Command ) {
                case Disconnect:
                    disconnect();
                break;
                case RefreshContacts:
                    refreshContacts();
                break; 
            }
        }
    }    
    
    private void disconnect()
    {
        clientBase.removeContact( client );
        ClientReceiver receiver = clientBase.removeClient( client );
        receiver.interrupt();
        
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
    
    private LinkedBlockingQueue commands = new LinkedBlockingQueue();
    private ArrayList< UUID > myConferences = new ArrayList<>();
}
