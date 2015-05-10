package blablamessenger;

import blablamessenger.Command.Sources;
import blablamessenger.Server.ClientBase;
import data_structures.Commands;

import data_structures.Conference;
import data_structures.ConferenceId;
import data_structures.Contact;
import data_structures.ContactConfPair;
import data_structures.ContactId;
import data_structures.ContactName;
import data_structures.Contacts;
import data_structures.ResultData;
import data_structures.ResultTypes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientProcessor extends Thread {
    public ClientProcessor( ClientBase base, Socket myClient,
            ClientReceiver receiver, ConcurrentLinkedQueue inputCommands )
    {
        clientBase = base;
        socket = myClient;
        myReceiver = receiver;
        commands = inputCommands;
        
        try {
            output = new ObjectOutputStream( socket.getOutputStream() );
        } catch (IOException ex) {
            Logger.getLogger(ClientProcessor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {
        while ( running ) {
            addLog( ClientProcessor.class.getName() + 
                    ": waiting for new command" );
            processCommand( getCommand() );
        }
    }    
    
    private void processCommand( Command command )
    {
        switch ( command.Command ) {
            case RegisterContact:
                registerContact( command );
            break;
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
            case CreateConference:
                addLog( ClientProcessor.class.getName() + 
                        ": get create conference command" );
                createConference( command );
            break;
            case AddToConference:
                addLog( ClientProcessor.class.getName() + 
                        ": get add to conference command" );
                addToConference( command );
            break;
            case RemoveFromConference:
                addLog( ClientProcessor.class.getName() + 
                        ": get remove from conference command" );
                removeFromConference( command );
            break;
            case DeleteConference:
                addLog( ClientProcessor.class.getName() + 
                        ": get delete conference command" );
            break;
            case SendMessageToContact:
                addLog( ClientProcessor.class.getName() + 
                        ": get send message to contact command" );
            break;
            case SendMessageToConference:
                addLog( ClientProcessor.class.getName() + 
                        ": get send message to conference command" );
            break;
            case RefreshStorage:
                addLog( ClientProcessor.class.getName() + 
                        ": get refresh storage command" );
            break;
            case UploadFile:
                addLog( ClientProcessor.class.getName() + 
                        ": get upload file command" );
            break;
            case DownloadFile:
                addLog( ClientProcessor.class.getName() + 
                        ": get download file command" );
            break;
            case RemoveFile:
                addLog( ClientProcessor.class.getName() + 
                        ": get remove file command" );
            break;
        }
    }
    
    private Command getCommand()
    {   
        Command command = null;
        while ( command == null ) {
            command = commands.poll();
        }
        return command;
    }
    
    private void registerContact( Command command )
    {
        ContactName name = ( ContactName ) command.Data;
        Contact newContact = new Contact( name );
        
        clientBase.addContact( newContact );
        clientBase.addClient( myContact, myReceiver );
        
        ResultData result = new ResultData( ResultTypes.ContactId, 
                myContact );
        writeResult( result );
    }
    
    private void disconnect()
    {
        disconnectClientReceiver();
        deleteFromBase();
        
        myConferences.stream().forEach( (ConferenceId conference) -> {
            Conference myConference = clientBase.getConference( conference );
            if ( myConference != null ) {
                synchronized ( myConference ) {
                    myConference.Members.remove( myContact );
                    if ( myConference.Members.isEmpty() ) {
                        clientBase.removeConference( conference );
                    } else {
                        notifyRemovedMember( myConference, myContact );
                    }
                }
            }
        });
  
        running = false;
    }
    private void deleteFromBase()
    {
        clientBase.removeContact( myContact );
        clientBase.removeClient( myContact );
    }
    private void disconnectClientReceiver()
    {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger( ClientProcessor.class.getName() ).
                    log( Level.SEVERE, null, ex );
        }
    }
    private void notifyRemovedMember( Conference conference, ContactId contact )
    {
        conference.Members.stream().forEach( (ContactId member) -> {
            clientBase.getClient( member ).
                addCommand( new Command(Sources.Server, 
                        Commands.RemoveFromConference,
                        null,
                        new ContactConfPair( contact, conference.Id )
                ) );
        });
    }
    
    private void refreshContacts()
    {
        Contacts contacts = new Contacts( clientBase.getContacts() );
        ResultData result = new ResultData( ResultTypes.UpdatedContacts,
                contacts );
        
        writeResult( result );
    }
    
    private void createConference( Command command )
    {
        Conference newConference = 
            new Conference( (Conference) command.Data );
        myConferences.add( newConference.Id );
        
        switch( command.Source ) {
            case Client:
                clientBase.addConference( newConference );
                notifyNewConference( command, newConference );
                writeResult( new ResultData(ResultTypes.CreatedConference,
                    newConference.Id) );
            break;
            case Server:
                writeResult( new ResultData(ResultTypes.AddedToNewConference,
                    newConference) );
            break;
        }
    }
    private void notifyNewConference( Command command, Conference conference ) {
        conference.Members.stream().
            filter( (contact) -> (contact != myContact) ).
                forEach( (contact) -> {
                        clientBase.getClient( contact ).
                        addCommand( new Command(Sources.Server, command) );
                }
            );
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
    
    private void removeFromConference( Command command ) 
    {
        ContactConfPair remove = ( ContactConfPair ) command.Data;
        if ( remove.Contact == myContact ) {
            removeMyContactFromConference( remove.Conference );
        }
        
        if ( command.Source == Sources.Client ) {
            Conference conference =  
                clientBase.getConference( remove.Conference );
            if ( conference != null ) {
                notifyRemovedMember( conference, remove.Contact );
            }
        }
        
        writeResult( new ResultData( ResultTypes.RemovedFromConference,
            remove ) );
    }
    private void removeMyContactFromConference( ConferenceId conference )
    {
        myConferences.remove( conference );
        Conference myConference = 
                clientBase.getConference( conference );
        if ( myConference != null ) {
            synchronized ( myConference ) {
                myConference.Members.remove( myContact );
                if ( myConference.Members.isEmpty() ) {
                    clientBase.removeConference( conference );
                }
            }
        }       
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
    
    private void addLog( String log ) { System.out.println( log ); }
    
    private ClientBase clientBase;
    private Socket socket;
    
    private ObjectOutputStream output;
    private ContactId myContact;
    private ClientReceiver myReceiver;
    
    private boolean running = true;
    
    private ConcurrentLinkedQueue< Command > commands = 
            new ConcurrentLinkedQueue<>();
    private ArrayList< ConferenceId > myConferences = new ArrayList<>();
}
