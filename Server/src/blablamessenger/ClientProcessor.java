package blablamessenger;

import blablamessenger.Task.Sources;
import blablamessenger.Server.Base;
import coreutilities.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientProcessor extends Thread
{
    public
    ClientProcessor(
        Base                          base,
        Socket                        myClientSocket,
        UUID                          myContactID,
        ConcurrentLinkedQueue< Task > inputTasks
    )
    {
        this.base        = base;
        this.socket      = myClientSocket;
        this.myContactID = myContactID;
        this.tasks       = inputTasks;

        try {
            output = new ObjectOutputStream( socket.getOutputStream() );
        }
        catch ( IOException ex ) {
            Logger.getLogger( ClientProcessor.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }
    
    @Override
    public void
    run()
    {
        while ( running ) {
            addLog( "waiting for new operation" );
            processCommand( getCommand() );
        }
    }    
    
    private void
    processCommand(
        Task task
    )
    {
        switch ( task.getCommand() ) {
            case RegisterContact:
                registerContact( task );
            break;
            case Disconnect:
                addLog( "get disconnect task" );
                disconnect();
            break;
            case RefreshContacts:
                addLog( "get refresh contacts task" );
                refreshContacts();
            break;
            case CreateConference:
                addLog( "get create conference task" );
                createConference( task );
            break;
            case AddToConference:
                addLog( "get add to conference task" );
                addToConference( task );
            break;
            case RemoveFromConference:
                addLog( "get remove from conference task" );
                removeFromConference( task );
            break;
            case DeleteConference:
                addLog( "get delete conference task" );
                deleteConference( task );
            break;
            case SendMessageToContact:
                addLog( "get send text to contact task" );
                sendMessageToContact( task );
            break;
            case SendMessageToConference:
                addLog( "get send text to conference task" );
                sendMessageToConference( task );
            break;
            case RefreshStorage:
                addLog( "get refresh storage task" );
                refreshStorage();
            break;
            case UploadFile:
                addLog( "get upload file task" );
                uploadFile( task );
            break;
            case DownloadFile:
                addLog( "get download file task" );
                downloadFile( task );
            break;
            case RemoveFile:
                addLog( "get remove file task" );
                removeFile( task );
            break;
            default:
                errorLog( "read unknown type of command" );
        }
    }
    
    private Task
    getCommand()
    {   
        Task task;

        do {
            task = tasks.poll();
        } while ( task == null );

        return task;
    }

    private void
    registerContact(
        Task task
    )
    {
        if ( !(task.operation.data instanceof String) ) {
            errorLog( "invalid data in command register contact" );
            return;
        }

        String  name    = (String) task.operation.data;
        Contact contact = new Contact( name );

        base.addContact( myContactID, contact );

        writeResult( new ResultData(ResultTypes.ContactID, myContactID) );
    }

    private void
    disconnect()
    {
        disconnectClientReceiver();
        deleteFromBase();
        deleteMyConferences();

        running = false;           
    }

    private void
    disconnectClientReceiver()
    {
        try {
            socket.close();
        }
        catch ( IOException ex ) {
            Logger.getLogger( ClientProcessor.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    private void
    deleteFromBase()
    {
        if ( base.removeContact(myContactID) == null ) {
            errorLog( "deleting myself, but not registered" );
        }

        if ( base.removeClient(myContactID) == null ) {
            errorLog( "deleting mine receiver, but not registered" );
        }
    }

    private void
    deleteMyConferences()
    {
        for ( UUID conferenceID : myConferences ) {
            ConcurrentConference conference = base.getConference( conferenceID );
            if ( conference == null ) {
                continue;
            }

            synchronized ( conference.lock ) {
                if ( !conference.members.remove( myContactID ) ) {
                    errorLog( "Removing myself from other conference" );
                    continue;
                }

                if ( conference.members.isEmpty() ) {
                    base.removeConference( conferenceID );
                }
                else {
                    ContactConfPair data = new ContactConfPair( myContactID, conferenceID );
                    Task task            = new Task( Sources.Server, Commands.RemoveFromConference, data );

                    notifyMembers( task, conference );
                }
            }
        }

        myConferences.clear();
    }

    private void
    refreshContacts()
    {
        HashMap< UUID, Contact > contacts = base.getContacts();
        writeResult( new ResultData(ResultTypes.UpdatedContacts, contacts) );
    }

    private void
    createConference(
        Task task
    )
    {
        if ( !(task.operation.data instanceof Conference) ) {
            errorLog( "invalid data in command create conference" );
            return;
        }

        ConcurrentConference conference = new ConcurrentConference( (Conference) task.operation.data );
        if ( conference.members == null ) {
            errorLog( "null members in conference" );
            return;
        }

        if ( conference.members.isEmpty() ) {
            errorLog( "empty conference" );
            return;
        }

        ResultData result = null;
        UUID conferenceID = UUID.randomUUID();

        if ( task.source == Sources.Client ) {
            base.addConference( conferenceID, conference );

            synchronized ( conference.lock ) {
                notifyMembers( new Task(Sources.Server, task.operation), conference, myContactID );
            }

            result = new ResultData( ResultTypes.CreatedConference, conferenceID );
        }
        else if ( task.source == Sources.Server ) {
            result = new ResultData( ResultTypes.AddedToNewConference, conference );
        }

        writeResult( result );
        myConferences.add( conferenceID );
    }

    private void
    addToConference(
        Task task
    )
    {
        if ( !(task.operation.data instanceof ContactConfPair) ) {
            errorLog( "invalid data in command add to conference" );
            return;
        }

        ContactConfPair newMember = (ContactConfPair) task.operation.data;
        if ( newMember.contact == null || newMember.conference == null ) {
            errorLog( "id of contact or conference is null" );
            return;
        }

        ResultData result = null;

        if ( task.source == Sources.Client ) {
            if ( newMember.contact.equals(myContactID) ) {
                errorLog( "add to conference myself" );
                return;
            }

            Conference target = base.getConference( newMember.conference );
            if ( target == null ) {
                errorLog( "null conference in command add to conference" );
                return;
            }

            ConcurrentConference conference = new ConcurrentConference( target );
            synchronized ( conference.lock ) {
                conference.members.add( newMember.contact );
                notifyMembers( new Task(Sources.Server, task.operation), conference );
            }

            result = new ResultData( ResultTypes.AddedToConference, newMember );
        } else if ( task.source == Sources.Server ) {
            if ( newMember.contact.equals(myContactID) ) {
                myConferences.add( newMember.conference );
                result = new ResultData( ResultTypes.AddedConference, newMember.conference );
            }
            else {
                result = new ResultData( ResultTypes.AddedToConference, newMember );
            }
        }

        writeResult( result );
    }

    private void
    removeFromConference(
        Task task
    )
    {
        if ( !(task.operation.data instanceof ContactConfPair) ) {
            errorLog( "invalid data in command remove from conference" );
            return;
        }

        ContactConfPair remove = (ContactConfPair) task.operation.data;
        if ( remove.conference == null || remove.contact == null ) {
            errorLog( "id of conference or contact is null" );
            return;
        }

        if ( remove.contact.equals( myContactID ) ) {
            myConferences.remove( remove.conference );

            ConcurrentConference conference = base.getConference( remove.conference );
            if ( conference == null ) {
                errorLog( "removing from not existing conference" );
                return;
            }

            synchronized ( conference.lock ) {
                if ( !conference.members.remove(myContactID) ) {
                    errorLog( "removing myself from other conference" );
                    return;
                }

                if ( conference.members.isEmpty() ) {
                    base.removeConference( remove.conference );
                }
                else {
                    notifyMembers( new Task(Sources.Server, task.operation), conference );
                }
            }
        }

        ResultData   result = new ResultData( ResultTypes.RemovedFromConference, remove );
        writeResult( result );
    }

    private void
    deleteConference(
        Task task
    )
    {
        if ( !(task.operation.data instanceof UUID) ) {
            errorLog( "invalid data in command delete conference" );
            return;
        }

        UUID conferenceID = (UUID) task.operation.data;
        if ( !myConferences.remove(conferenceID) ) {
            errorLog( "delete unknown conference" );
            return;
        }

        if ( task.source == Sources.Client ) {
            ConcurrentConference conference = base.removeConference( conferenceID );
            if ( conference == null ) {
                errorLog( "deleting not existing conference" );
                return;
            }

            if ( conference.members.isEmpty() ) {
                errorLog( "deleting empty conference" );
                return;
            }

            synchronized ( conference.lock ) {
                notifyMembers( new Task(Sources.Server, task.operation), conference, myContactID );
            }
        }

        ResultData result = new ResultData( ResultTypes.DeletedConference, conferenceID );
        writeResult( result );
    }
    
    private void
    sendMessageToContact(
        Task task
    )
    {
        if ( !(task.operation.data instanceof ContactMessagePair) ) {
            errorLog( "invalid data in command send text to contact" );
            return;
        }

        ContactMessagePair message = (ContactMessagePair) task.operation.data;
        if ( message.contact == null || message.text == null ) {
            errorLog( "id of contact or text is null" );
            return;
        }
        
        if ( task.source == Sources.Client ) {
            ClientReceiver client = base.getClient( message.contact );
            if ( client == null ) {
                errorLog( "contact is null" );
                return;
            }

            ContactMessagePair data = new ContactMessagePair( myContactID, message.text );
            client.addCommand( new Task(Sources.Server, Commands.SendMessageToContact, data) );
        }

        writeResult( new ResultData(ResultTypes.MessageToContact, message) );
    }
    
    
    private void
    sendMessageToConference(
        Task task
    )
    {
        if ( !(task.operation.data instanceof ContactConfMessage) ) {
            errorLog( "invalid data in command send text to conference" );
            return;
        }

        ContactConfMessage message = (ContactConfMessage) task.operation.data;
        if ( message.source == null || message.confMessage == null ) {
            errorLog( "id of source or confMessagePair is null" );
            return;
        }

        if ( !myConferences.contains(message.confMessage.conference) ) {
            errorLog( "message to other conference" );
            return;
        }
        
        if ( task.source == Sources.Client ) {
            ConcurrentConference conference = base.getConference( message.confMessage.conference );
            if ( conference == null ) {
                errorLog( "message to not existing conference" );
                return;
            }

            synchronized ( conference.lock ) {
                notifyMembers( new Task(Sources.Server, task.operation), conference, myContactID );
            }
        }

        writeResult( new ResultData(ResultTypes.MessageToConference, message) );
    }
    
    private void
    notifyMembers(
        Task       task,
        Conference conference
    )
    {
        if ( task == null || conference == null ) {
            return;
        }

        for ( UUID contact : conference.members ) {
            ClientReceiver receiver = base.getClient( contact );
            if ( receiver == null ) {
                errorLog( "receiver on id" + contact.toString() + " is null" );
                continue;
            }

            receiver.addCommand( task );
        }
    }

    private void
    notifyMembers(
        Task       task,
        Conference conference,
        UUID       mask
    )
    {
        if ( task == null || conference == null ) {
            return;
        }

        for ( UUID contact : conference.members ) {
            if ( mask.equals( contact ) ) {
                continue;
            }

            ClientReceiver receiver = base.getClient( contact );
            if ( receiver == null ) {
                errorLog( "receiver on id" + contact.toString() + " is null" );
                continue;
            }

            receiver.addCommand( task );
        }
    }
    
    private void
    refreshStorage()
    {
        writeResult( new ResultData(ResultTypes.UpdatedFiles, base.getFiles()) );
    }


    private void
    uploadFile(
        Task task
    )
    {
        if ( !(task.operation.data instanceof File) ) {
            errorLog( "invalid data in command upload file" );
            return;
        }

        File file = (File) task.operation.data;
        if ( file.data == null ) {
            errorLog( "uploading file with null data" );
            return;
        }

        if ( file.data.length <= 0 ) {
            errorLog( "uploading empty file" );
            return;
        }

        UUID id = UUID.randomUUID();
        base.upload( id, file );

        writeResult( new ResultData(ResultTypes.UploadedFile, id) );
    }

    private void
    downloadFile(
        Task task
    )
    {
        if ( !(task.operation.data instanceof UUID) ) {
            errorLog( "invalid data in command download file" );
            return;
        }

        UUID id   = (UUID) task.operation.data;
        File file = base.download( id );

        if ( file == null ) {
            errorLog( "downloading not existing file" );
            return;
        }

        writeResult( new ResultData(ResultTypes.DownloadedFile, file) );
    }

    private void
    removeFile(
        Task task
    )
    {
        if ( !(task.operation.data instanceof UUID) ) {
            errorLog( "invalid data in command upload file" );
            return;
        }

        UUID id = (UUID) task.operation.data;
        File file = base.removeFile( id );
        if ( file == null ) {
            errorLog( "deleting not existing file" );
            return;
        }

        writeResult( new ResultData(ResultTypes.RemovedFile, id) );
    }
    
    private void
    writeResult(
        ResultData result
    )
    {
        try {
            output.writeObject( result );
            output.flush();
        }
        catch ( IOException ex ) {
            Logger.getLogger( ClientProcessor.class.getName() ).log( Level.SEVERE, null, ex );
        }        
    }
    
    private void
    addLog(
        String log
    )
    {
        System.out.println( ClientProcessor.class.getName() + ": " + log );
    }

    private void
    errorLog(
        String message
    )
    {
        final int CALLING_METHOD = 1;
        Throwable t = new Throwable();
        StackTraceElement trace[] = t.getStackTrace();
        if ( 1 < trace.length  ) {
            StackTraceElement element = trace[ CALLING_METHOD ];
            addLog( element.getMethodName() + " " + element.getLineNumber() + " " + message );
        }
    }

    private final Base   base;
    private final Socket socket;
    
    private ObjectOutputStream output;
    private final UUID         myContactID;

    private boolean running = true;
    
    private ConcurrentLinkedQueue< Task > tasks         = new ConcurrentLinkedQueue<>();
    private final ArrayList< UUID >       myConferences = new ArrayList<>();
}
