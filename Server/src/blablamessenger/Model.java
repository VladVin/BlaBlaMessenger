package blablamessenger;

import coreutilities.*;
import blablamessenger.Task.Sources;

import java.util.ArrayList;
import java.util.UUID;

public class Model implements IModel
{
    public
    Model(
        IBase base
    )
    {
        this.base = base;
    }

    @Override
    public ResultData
    registerContact(
        Task        task,
        IController controller
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof String) ) {
            errorLog( "invalid data in command register contact" );
            return null;
        }

        String      name    = (String) task.operation.data;
        ContactData contact = new ContactData( name );

        UUID id = UUID.randomUUID();

        if ( base.addContact(id, contact) != null ) {
            errorLog( "register contact: contact is already registered" );
            return null;
        }

        if ( base.addController(id, controller) != null ) {
            errorLog( "register contact: controller is already registered" );
            return null;
        }

        return new ResultData( ResultTypes.ContactID, id );
    }

    @Override
    public ResultData
    disconnect(
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        IController controller = base.removeController(myID);
        if ( controller == null ) {
            errorLog( "disconnecting not existing controller in command disconnect" );
            return null;
        }
        controller.stop();

        if ( base.removeContact(myID) == null ) {
            errorLog( "disconnecting not existing contact in command disconnect" );
            return null;
        }

        deleteMyConferences( myID, myConferences );

        return new ResultData( ResultTypes.Disconnected, null );
    }

    private void
    deleteMyConferences(
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        for ( UUID conferenceID : myConferences ) {
            ConcurrentConferenceData conference = base.getConference( conferenceID );
            if ( conference == null ) {
                continue;
            }

            synchronized ( conference.lock ) {
                if ( !conference.members.remove(myID) ) {
                    errorLog( "Removing myself from other conference" );
                    continue;
                }

                if ( conference.members.isEmpty() ) {
                    base.removeConference( conferenceID );
                }
                else {
                    ContactConfPair data = new ContactConfPair( myID, conferenceID );
                    Task task            = new Task( Task.Sources.Server, Commands.RemoveFromConference, data );

                    notifyMembers( task, conference );
                }
            }
        }

        myConferences.clear();
    }

    @Override
    public ResultData
    refreshContacts()
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        return new ResultData( ResultTypes.UpdatedContacts, base.getContacts() );
    }

    @Override
    public ResultData
    createConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        ResultData result = null;

        if ( task.source == Sources.Client ) {
            if ( !(task.operation.data instanceof ConferenceData) ) {
                errorLog( "invalid data in command create conference from client" );
                return null;
            }

            ConcurrentConferenceData conference = new ConcurrentConferenceData( (ConferenceData) task.operation.data );
            if ( conference.members == null ) {
                errorLog( "null members in conference" );
                return null;
            }

            if ( conference.members.isEmpty() ) {
                errorLog( "empty conference" );
                return null;
            }

            UUID conferenceID = UUID.randomUUID();
            if ( base.addConference(conferenceID, conference) != null ) {
                errorLog( "conference is already registered" );
                return null;
            }

            Conference newConference = new Conference( conferenceID, (ConferenceData) task.operation.data );
            Task notify = new Task( Sources.Server, Commands.CreateConference, newConference );

            synchronized ( conference.lock ) {
                notifyMembers( notify, conference, myID );
            }

            if ( !myConferences.add( conferenceID ) ) {
                errorLog( "create conference from client: client is already in conference" );
            }

            result = new ResultData( ResultTypes.CreatedConference, conferenceID );
        }
        else if ( task.source == Sources.Server ) {
            if ( !(task.operation.data instanceof Conference) ) {
                errorLog( "invalid data in command create conference from SocketConnection" );
                return null;
            }

            Conference conference = (Conference) task.operation.data;
            if ( !myConferences.add( conference.id ) ) {
                errorLog( "create conference from server: client is already in conference" );
            }

            result = new ResultData( ResultTypes.AddedToNewConference, conference );
        }

        return result;
    }

    @Override
    public ResultData
    addToConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        ResultData result = null;

        if ( task.source == Sources.Client ) {
            if ( !(task.operation.data instanceof ContactConfPair) ) {
                errorLog( "invalid data in command add to conference from client" );
                return null;
            }

            ContactConfPair newMember = (ContactConfPair) task.operation.data;
            if ( newMember.contact == null || newMember.conference == null ) {
                errorLog( "id of contact or conference is null" );
                return null;
            }

            if ( newMember.contact.equals(myID) ) {
                errorLog( "add to conference myself" );
                return null;
            }

            ConcurrentConferenceData conference = base.getConference( newMember.conference );
            if ( conference == null ) {
                errorLog( "invalid conference id in command add to conference" );
                return null;
            }

            synchronized ( conference.lock ) {
                conference.members.add( newMember.contact );

                Conference newConference = new Conference( newMember.conference, conference.name, conference.members );
                ConferenceEntry notifyData = new ConferenceEntry( newMember.contact, newConference );
                Task notify = new Task( Sources.Server, Commands.AddToConference, notifyData );

                notifyMembers( notify, conference, myID );
            }

            result = new ResultData( ResultTypes.AddedToConference, newMember );
        }
        else if ( task.source == Sources.Server ) {
            if ( !(task.operation.data instanceof ConferenceEntry) ) {
                errorLog( "invalid data in command add to conference from server" );
                return null;
            }

            ConferenceEntry entry = (ConferenceEntry) task.operation.data;
            if ( entry.contact == null || entry.conference == null ) {
                errorLog( "contact or conference is null in command add to conference from server" );
                return null;
            }

            if ( entry.contact.equals(myID) ) {
                if ( !myConferences.add(entry.conference.id) ) {
                    errorLog( "add to conference from server: client is already in conference" );
                }
                result = new ResultData( ResultTypes.AddedConference, entry.conference );
            }
            else {
                ContactConfPair newMember = new ContactConfPair( entry.contact, entry.conference.id );
                result = new ResultData( ResultTypes.AddedToConference, newMember );
            }
        }

        return result;
    }

    @Override
    public ResultData
    removeFromConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof ContactConfPair) ) {
            errorLog( "invalid data in command remove from conference" );
            return null;
        }

        ContactConfPair remove = (ContactConfPair) task.operation.data;
        if ( remove.conference == null || remove.contact == null ) {
            errorLog( "id of conference or contact is null" );
            return null;
        }

        if ( !myConferences.contains(remove.conference) ) {
            errorLog( "remove from unknown conference" );
            return null;
        }

        if ( task.source == Sources.Client ) {
            ConcurrentConferenceData conference = base.getConference( remove.conference );
            if ( conference == null ) {
                errorLog( "removing from not existing conference" );
                return null;
            }

            synchronized ( conference.lock ) {
                if ( !conference.members.contains(remove.contact) ) {
                    errorLog( "remove from conference from client: contact is already left conference" );
                    return null;
                }

                notifyMembers( new Task(Task.Sources.Server, task.operation), conference, myID );

                if ( remove.contact.equals(myID) ) {
                    myConferences.remove( remove.conference );
                }

                conference.members.remove( remove.contact );
                if ( conference.members.isEmpty() ) {
                    base.removeConference( remove.conference );
                }
            }
        }
        else if ( task.source == Sources.Server ) {
            if ( remove.contact.equals(myID) ) {
                myConferences.remove( remove.conference );
            }
        }

        return new ResultData( ResultTypes.RemovedFromConference, remove );
    }

    @Override
    public ResultData
    deleteConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof UUID) ) {
            errorLog( "invalid data in command delete conference" );
            return null;
        }

        UUID conferenceID = (UUID) task.operation.data;
        if ( !myConferences.remove(conferenceID) ) {
            errorLog( "delete unknown conference" );
            return null;
        }

        if ( task.source == Task.Sources.Client ) {
            ConcurrentConferenceData conference = base.removeConference( conferenceID );
            if ( conference == null ) {
                errorLog( "deleting not existing conference" );
                return null;
            }

            if ( conference.members.isEmpty() ) {
                errorLog( "deleting empty conference" );
                return null;
            }

            synchronized ( conference.lock ) {
                notifyMembers( new Task(Task.Sources.Server, task.operation), conference, myID );
            }
        }

        return new ResultData( ResultTypes.DeletedConference, conferenceID );
    }

    @Override
    public ResultData
    sendMessageToConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof ContactConfMessage) ) {
            errorLog( "invalid data in command send text to conference" );
            return null;
        }

        ContactConfMessage message = (ContactConfMessage) task.operation.data;
        if ( message.source == null || message.confMessage == null ) {
            errorLog( "id of source or confMessagePair is null" );
            return null;
        }

        if ( !myConferences.contains(message.confMessage.conference) ) {
            errorLog( "message to unknown conference" );
            return null;
        }

        if ( task.source == Task.Sources.Client ) {
            ConcurrentConferenceData conference = base.getConference( message.confMessage.conference );
            if ( conference == null ) {
                errorLog( "message to not existing conference" );
                return null;
            }

            synchronized ( conference.lock ) {
                notifyMembers( new Task(Task.Sources.Server, task.operation), conference, myID );
            }
        }

        return new ResultData( ResultTypes.MessageToConference, message );
    }

    @Override
    public ResultData
    sendMessageToContact(
        Task task,
        UUID myID
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof ContactMessagePair) ) {
            errorLog( "invalid data in command send text to contact" );
            return null;
        }

        ContactMessagePair message = (ContactMessagePair) task.operation.data;
        if ( message.contact == null || message.text == null ) {
            errorLog( "id of contact or text is null" );
            return null;
        }

        if ( task.source == Task.Sources.Client ) {
            IController controller = base.getController( message.contact );
            if ( controller == null ) {
                errorLog( "contact is null" );
                return null;
            }

            ContactMessagePair data = new ContactMessagePair( myID, message.text );
            controller.addTask( new Task(Task.Sources.Server, Commands.SendMessageToContact, data) );
        }

        return new ResultData(ResultTypes.MessageToContact, message);
    }

    private void
    notifyMembers(
        Task                     task,
        ConcurrentConferenceData conference
    )
    {
        if ( task == null || conference == null ) {
            return;
        }

        for ( UUID contact : conference.members ) {
            IController controller = base.getController( contact );
            if ( controller == null ) {
                errorLog( "controllerThread on id" + contact.toString() + " is null" );
                continue;
            }

            controller.addTask( task );
        }
    }

    private void
    notifyMembers(
        Task                     task,
        ConcurrentConferenceData conference,
        UUID                     mask
    )
    {
        if ( task == null || conference == null ) {
            return;
        }

        for ( UUID contact : conference.members ) {
            if ( mask.equals( contact ) ) {
                continue;
            }

            IController controller = base.getController( contact );
            if ( controller == null ) {
                errorLog( "receiver on id" + contact.toString() + " is null" );
                continue;
            }

            controller.addTask( task );
        }
    }

    @Override
    public ResultData
    refreshStorage()
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        return new ResultData( ResultTypes.UpdatedFiles, base.getFiles() );
    }

    @Override
    public ResultData
    uploadFile(
        Task task
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof FileData) ) {
            errorLog( "invalid data in command upload file" );
            return null;
        }

        FileData fileData = (FileData) task.operation.data;
        if ( fileData.data == null ) {
            errorLog( "uploading file with null data" );
            return null;
        }

        if ( fileData.data.length <= 0 ) {
            errorLog( "uploading empty file" );
        }

        UUID id = UUID.randomUUID();
        base.upload( id, fileData );

        return new ResultData( ResultTypes.UploadedFile, id );
    }

    @Override
    public ResultData
    downloadFile(
        Task task
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof UUID) ) {
            errorLog( "invalid data in command download file" );
            return null;
        }

        UUID id = (UUID) task.operation.data;

        FileData fileData = base.download( id );
        if ( fileData == null ) {
            errorLog( "downloading not existing file" );
            return null;
        }

        return new ResultData( ResultTypes.DownloadedFile, new File(id, fileData) );
    }

    @Override
    public ResultData
    removeFile(
        Task task
    )
    {
        if ( !base.isRunning() ) {
            return new ResultData( ResultTypes.Disconnected, null );
        }

        if ( !(task.operation.data instanceof UUID) ) {
            errorLog( "invalid data in command upload file" );
            return null;
        }

        UUID id = (UUID) task.operation.data;
        if ( base.removeFile(id) == null ) {
            errorLog( "deleting not existing file" );
            return null;
        }

        return new ResultData( ResultTypes.RemovedFile, id );
    }

    private void
    addLog(
        String log
    )
    {
        System.out.println( Model.class.getName() + ": " + log );
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

    private final IBase base;
}
