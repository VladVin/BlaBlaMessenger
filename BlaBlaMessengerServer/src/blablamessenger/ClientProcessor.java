package blablamessenger;

import blablamessenger.Command.Sources;
import blablamessenger.Server.ClientBase;
import blablamessenger.Server.FileBase;

import data_structures.Commands;
import data_structures.Conference;
import data_structures.ConferenceId;
import data_structures.Contact;
import data_structures.ContactConfMessagePair;
import data_structures.ContactConfPair;
import data_structures.ContactId;
import data_structures.ContactMessagePair;
import data_structures.ContactName;
import data_structures.Contacts;
import data_structures.File;
import data_structures.FileId;
import data_structures.FileIdNamePair;
import data_structures.FileIdNamePairs;
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
    public ClientProcessor( ClientBase clientBase, FileBase fileBase,
            Socket myClient, ContactId myContact,
            ClientReceiver receiver, ConcurrentLinkedQueue inputCommands )
    {
        this.clientBase = clientBase;
        socket = myClient;
        myReceiver = receiver;
        this.myContact = myContact;
        commands = inputCommands;
        this.fileBase = fileBase;
        
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
            addLog( "waiting for new command" );
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
                addLog( "get disconnect command" );
                disconnect();
            break;
            case RefreshContacts:
                addLog( "get refresh contacts command" );
                refreshContacts();
            break;
            case CreateConference:
                addLog( "get create conference command" );
                createConference( command );
            break;
            case AddToConference:
                addLog( "get add to conference command" );
                addToConference( command );
            break;
            case RemoveFromConference:
                addLog( "get remove from conference command" );
                removeFromConference( command );
            break;
            case DeleteConference:
                addLog( "get delete conference command" );
                deleteConference( command );
            break;
            case SendMessageToContact:
                addLog( "get send message to contact command" );
                sendMessageToContact( command );
            break;
            case SendMessageToConference:
                addLog( "get send message to conference command" );
                sendMessageToConference( command );
            break;
            case RefreshStorage:
                addLog( "get refresh storage command" );
                refreshStorage();
            break;
            case UploadFile:
                addLog( "get upload file command" );
                uploadFile( command );
            break;
            case DownloadFile:
                addLog( "get download file command" );
                downloadFile( command );
            break;
            case RemoveFile:
                addLog( "get remove file command" );
                removeFile( command );
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
        Contact newContact = new Contact( name, myContact );

        addToBase( newContact );
        
        if ( myContact == null || myContact.Id == null ) {
            errorLog( "myContact is null" );
        }

        writeResult( new ResultData( ResultTypes.ContactId, myContact ) );
    }
    private void addToBase( Contact contact )
    {
        addMyContactToBase( contact );
        addMyReceiverToBase();       
    }
    private void addMyContactToBase( Contact contact )
    {
        if ( myContact.Id != null ) {
                clientBase.addContact( contact );
        } else { errorLog( "myContact is null" ); }
    }
    private void addMyReceiverToBase()
    {
        if ( myContact.Id != null ) {
            if ( myReceiver != null ) {
                clientBase.addClient( myContact, myReceiver );                   
            } else { errorLog( "myReceiver is null" ); }
        } else { errorLog( "myContact is null" ); }
    }
    
    private void disconnect()
    {
        disconnectClientReceiver();
        deleteFromBase();
        deleteMyConferences();
        running = false;           
    }
    private void deleteFromBase()
    {
        removeMyContactFromBase();
        removeMyReceiverFromBase();
    }
    private void deleteMyConferences()
    {
        myConferences.stream().forEach( ( ConferenceId conference ) -> {
            if ( conference != null ) {
                Conference myConference = 
                        clientBase.getConference( conference );
                if ( myConference != null ) {
                    synchronized ( myConference ) {
                        removeMeFromConference( myConference );
                        if ( myConference.Members.isEmpty() ) {
                            clientBase.removeConference( conference );
                        } else {
                            notifyRemoveMember( myConference, myContact );
                        }
                    }
                }
            }
        });
        myConferences.clear();
    }
    private void removeMeFromConference( Conference conference )
    { 
        if ( myContact.Id != null ) {
            conference.Members.remove( myContact );  
        } else { errorLog( "myContact is null" ); }
    }
    
    private void addMeToConference( Conference conference )
    { 
        if ( myContact.Id != null ) {
            conference.Members.add( myContact );  
        } else { errorLog( "myContact is null" ); }
    }
    
    private void removeMyContactFromBase()
    { 
        if ( myContact.Id != null ) {
            clientBase.removeContact( myContact );  
        } else { errorLog( "myContact is null" ); }
    }
    private void removeMyReceiverFromBase()
    { 
        if ( myReceiver != null ) {
            clientBase.removeClient( myContact );  
        } else { errorLog( "myReceiver is null" ); } 
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
    private void notifyRemoveMember( Conference conference, ContactId contact )
    {
        notifyMembers( new Command
                (   Sources.Server, 
                    Commands.RemoveFromConference, 
                    new ContactConfPair( contact, conference.Id )
                ),
                conference );
    }
    
    private void refreshContacts()
    {
        Contacts contacts = new Contacts( clientBase.getContacts() );
        contacts.Contacts.stream().forEach((Contact) -> {
            addLog(Contact.Name.Name);
        });
        writeResult( new ResultData( ResultTypes.UpdatedContacts, contacts ) );           
    }
    
    private void createConference( Command command )
    {
        Conference conference = (Conference) command.Data;
        if ( conference.Members.isEmpty() ) {
            errorLog( "empty conference" );
            return;
        }
        
        Conference newConference = new Conference( conference );
        if ( newConference.Id == null ) {
            errorLog( "id of new conference is null" );
            return;
        }
        
        myConferences.add( newConference.Id );
        if ( command.Source == Sources.Client ) {
            
            clientBase.addConference( newConference );
            synchronized ( newConference ) {
                removeMeFromConference( newConference );
                notifyNewConference( command, newConference );
                addMeToConference( newConference );
            }
            
            ResultData result = new ResultData( ResultTypes.CreatedConference,
                newConference.Id );
            writeResult( result );
            
        } else if ( command.Source == Sources.Server ) {
            
            ResultData res = new ResultData( ResultTypes.AddedToNewConference,
                newConference );
            writeResult( res );
            
        }
              
    }
    private void notifyNewConference( Command command, Conference conference ) 
    { notifyMembers( new Command(Sources.Server, command), conference ); }
    
    private void addToConference( Command command )
    {
        ContactConfPair add = ( ContactConfPair ) command.Data;
        if ( add.Contact.Id == null ) {
            errorLog( "id of contact is null" );
            return;
        }
        
        if ( add.Conference.Id == null ) {
            errorLog( "id of conference is null" );
            return;
        }
        
        if ( command.Source == Sources.Client ) {
            
            if ( add.Contact.Id.compareTo(myContact.Id) == 0 ) {
                errorLog( "add to conference myself" );
                return;
            }
            
            Conference conference = clientBase.getConference( add.Conference );
            if ( conference != null ) {
                synchronized ( conference ) {
                    notifyNewMember( command, conference );                    
                }
                ResultData result = 
                    new ResultData( ResultTypes.AddedToConference, add );
                writeResult( result );
            } else { errorLog( "add to unknown conference" ); }
            
        } else if ( command.Source == Sources.Server ) {
            
            if ( add.Contact.Id.compareTo(myContact.Id) == 0 ) {
                Conference newConference = 
                    clientBase.getConference( add.Conference );
                if ( newConference != null ) {
                    myConferences.add( add.Conference );
                    synchronized ( newConference ) {
                        addMeToConference( newConference );
                    }
                    
                    ResultData result = 
                        new ResultData( ResultTypes.AddedConference, 
                            newConference );
                    writeResult( result );
                    
                } else { errorLog( "added to unknown conference" ); }
            } else {
                ResultData result = 
                    new ResultData( ResultTypes.AddedToConference, add );
                writeResult( result );
            }
              
        }
        
        if ( add.Contact.Id.compareTo(myContact.Id) == 0 ) {
            Conference conference =  clientBase.getConference( add.Conference );
            if ( conference != null ) {
                myConferences.add( add.Conference );
                synchronized ( conference ) {
                    notifyNewMember( command, conference );
                    conference.Members.add( myContact );                  
                    writeResult( new ResultData(ResultTypes.AddedConference,
                        conference) );
                }
            }
        } else {
            writeResult( new ResultData(ResultTypes.AddedToConference, add) );
        } 
    }
    private void notifyNewMember( Command command, Conference conference )
    { notifyMembers( new Command(Sources.Server, command), conference ); }
    
    private void removeFromConference( Command command ) 
    {
        ContactConfPair remove = ( ContactConfPair ) command.Data;
        if ( remove.Conference.Id == null ) {
            errorLog( "id of conference is null" );
            return;
        }
        if ( remove.Contact.Id == null ) {
            errorLog( "id of contact is null" );
            return;
        }
        
        Conference conference = clientBase.getConference( remove.Conference );
        ResultData result = new ResultData( ResultTypes.RemovedFromConference,
            remove );
        
        if ( remove.Contact.Id.compareTo(myContact.Id) == 0 ) {
            myConferences.remove( remove.Conference );
            if ( conference != null ) {
                synchronized ( conference ) {
                    removeMeFromConference( conference );
                    /* Если больше никого нет, то не оповещаю */
                    if ( conference.Members.isEmpty() ) {
                        clientBase.removeConference( remove.Conference );
                        writeResult( result );
                        return;
                    }
                }
            }           
        }
        
        if ( command.Source == Sources.Client ) {
            notifyRemoveMember( conference, remove.Contact );
        }      
        
        writeResult( result );           
    }

    private void deleteConference( Command command ) 
    {
        ConferenceId remove = ( ConferenceId ) command.Data;
        if ( remove.Id == null ) {
            errorLog( "id of conference is null" );
            return;
        }
        
        myConferences.remove( remove );
        if ( command.Source == Sources.Client ) {
            Conference conference = clientBase.removeConference( remove );
            if ( conference != null ) {
                synchronized ( conference ) {
                    removeMeFromConference( conference );
                    notifyDeleteConference( command, conference );
                }
                ResultData result = 
                    new ResultData( ResultTypes.DeletedConference, remove );
                writeResult( result );
           }            
        } else if ( command.Source == Sources.Server ) {
            ResultData result = 
                new ResultData( ResultTypes.DeletedConference, remove );
            writeResult( result );           
        }         
    }
    private void notifyDeleteConference( Command command, 
            Conference conference )
    { notifyMembers( new Command(Sources.Server, command), conference ); }
    
    private void sendMessageToContact( Command command ) 
    {
        ContactMessagePair send = ( ContactMessagePair ) command.Data;
        if ( send.Contact.Id == null ) {
            errorLog( "id of contact is null" );
            return;
        }
        
        if ( command.Source == Sources.Client ) {
            ClientReceiver client = clientBase.getClient( send.Contact );
            if ( client != null ) {
                client.addCommand( new Command
                    (   Sources.Server,
                        Commands.SendMessageToContact,
                        new ContactMessagePair(myContact, send.Message)
                    ) );    
            } else { addLog( "client is null" ); }
            
            addLog( "myContact = " + myContact.Id.toString() );
            addLog( "send.Contact = " + send.Contact.Id.toString() );
            
            if ( myReceiver == null ) {
                addLog( "myReceiver is null" );
            }
                   
        }
        writeResult( new ResultData(ResultTypes.Message, send) );            
    }
    
    
    private void sendMessageToConference( Command command ) 
    {
        ContactConfMessagePair send = ( ContactConfMessagePair ) command.Data;
        if ( send.Source.Id == null ) {
            errorLog( "id of source is null" );
            return;
        }
        
        if ( send.Message.Destination.Id == null ) {
            errorLog( "id of destination is null" );
            return;
        }
        
        if ( command.Source == Sources.Client ) {
            
            Conference conference = 
                clientBase.getConference( send.Message.Destination );
            if ( conference != null ) {
                synchronized ( conference ) {
                    notifyMessage( command, conference );
                }
            }
            
        } else if ( command.Source == Sources.Server ) {
            
            writeResult( new ResultData(ResultTypes.Message, send) );
            
        }               
    }
    private void notifyMessage( Command command, Conference conference )
    { notifyMembers( new Command(Sources.Server, command), conference ); }
    
    private void notifyMembers( Command command, Conference conference )
    {
        if ( conference != null ) {
            conference.Members.stream().forEach( (ContactId contact) -> {
                if ( contact != null ) {
                    ClientReceiver receiver = clientBase.getClient( contact );
                    if ( receiver != null ) {
                        receiver.addCommand( command );
                    } else { 
                        errorLog( "receiver on id" + 
                                contact.toString() + 
                                " is null" );
                    }
                } else { conference.Members.remove( contact ); }
            });           
        } else { errorLog( "conference is null" ); }
    }
    
    private void refreshStorage() 
    {
        FileIdNamePairs files = fileBase.getFiles();
        writeResult( new ResultData(ResultTypes.UpdatedFiles, files) );
    }
    
    
    private void uploadFile( Command command ) 
    {
        File newFile = ( File ) command.Data;
        FileId newId = new FileId();
        
        addToBase( newId, newFile );
        writeResult( new ResultData(ResultTypes.UploadedFile, newId) );
    }
    private void addToBase( FileId id, File file )
    {
        fileBase.addFile( new FileIdNamePair( id, file.Name ) );
        fileBase.upload( id, file.Data );
    }
    
    private void downloadFile( Command command ) 
    {
        FileId file = ( FileId ) command.Data;
        writeResult( new ResultData(ResultTypes.DownloadedFile, 
                fileBase.download( file )) );
    }
    
    private void removeFile( Command command ) 
    {
        FileId file = ( FileId ) command.Data;
        deleteFromBase( file );
        writeResult( new ResultData(ResultTypes.RemovedFile, file) );
    }
    private void deleteFromBase( FileId file )
    {
        fileBase.remove( file );
        fileBase.removeFile( file );
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
    { System.out.println( ClientProcessor.class.getName() + ": " + log ); }
    private void errorLog( String message )
    {   
        Throwable t = new Throwable();
        StackTraceElement trace[] = t.getStackTrace();
        if ( trace.length > 1 ) {
            StackTraceElement element = trace[ CALLING_METHOD ];
            addLog( element.getMethodName() + " " +
                    element.getLineNumber() + " " + message );
        }
    }
    
    private final ClientBase clientBase;
    private final FileBase fileBase;
    private final Socket socket;
    
    private ObjectOutputStream output;
    private final ContactId myContact;
    private final ClientReceiver myReceiver;
    
    private final int CALLING_METHOD = 1;
    private boolean running = true;
    
    private ConcurrentLinkedQueue< Command > commands = 
            new ConcurrentLinkedQueue<>();
    private final ArrayList< ConferenceId > myConferences = new ArrayList<>();
}
