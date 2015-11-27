package blablamessenger;

import blablamessenger.ServerController.Controller;
import coreutilities.*;
import blablamessenger.Task.Sources;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Model
{
    public
    Model()
    {
        this.running = true;
    }
    private class
    Base
    {
        public ContactData
        addContact(
            UUID        id,
            ContactData contact
        )
        {
            if ( !running ) {
                return null;
            }

            return contacts.putIfAbsent( id, contact );
        }

        public ContactData
        removeContact(
            UUID contactID
        )
        {
            return contacts.remove( contactID );
        }

        public ArrayList< Contact >
        getContacts()
        {
            return contacts.entrySet().stream().map( entry -> new Contact(entry.getKey(), entry.getValue()) ).
                collect( Collectors.toCollection(ArrayList::new) );
        }

        public ConcurrentConferenceData
        addConference(
            UUID                     id,
            ConcurrentConferenceData conference
        )
        {
            if ( !running ) {
                return null;
            }

            return conferences.putIfAbsent( id, conference );
        }

        public ConcurrentConferenceData
        removeConference(
            UUID conferenceID
        )
        {
            return conferences.remove( conferenceID );
        }

        public ConcurrentConferenceData
        getConference(
            UUID conferenceID
        )
        {
            return conferences.get( conferenceID );
        }

        public Controller
        addController(
            UUID       id,
            Controller controller
        )
        {
            if ( !running ) {
                return null;
            }

            return controllers.putIfAbsent( id, controller );
        }

        public Controller
        removeController(
            UUID controllerID
        )
        {
            return controllers.remove( controllerID );
        }

        public Controller
        getController(
            UUID controllerID
        )
        {
            return controllers.get( controllerID );
        }

        public void
        upload(
            UUID     id,
            FileData fileData
        )
        {
            if ( !running ) {
                return;
            }

            files.put( id, fileData );
        }

        public FileData
        download(
            UUID id
        )
        {
            return files.get( id );
        }

        public FileData
        removeFile(
            UUID id
        )
        {
            return files.remove( id );
        }

        public ArrayList< File >
        getFiles()
        {
            return files.entrySet().stream().map(
                entry -> new File( entry.getKey(), new FileData(entry.getValue().name, null) )
            ).collect( Collectors.toCollection(ArrayList::new) );
        }

        private final ConcurrentHashMap< UUID, ContactData              > contacts    = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, ConcurrentConferenceData > conferences = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, Controller               > controllers = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, FileData                 > files       = new ConcurrentHashMap<>();
    }

    public ResultData
    registerContact(
        Task     task,
        Controller controller
    )
    {
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

    public boolean
    disconnect(
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
        if ( base.removeController(myID) == null ) {
            errorLog( "disconnecting not existing controller in command disconnect" );
            return false;
        }

        if ( base.removeContact(myID) == null ) {
            errorLog( "disconnecting not existing contact in command disconnect" );
            return false;
        }

        deleteMyConferences( myID, myConferences );

        return true;
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

    public ResultData
    refreshContacts()
    {
        return new ResultData( ResultTypes.UpdatedContacts, base.getContacts() );
    }

    public ResultData
    createConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
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
                errorLog( "invalid data in command create conference from Server" );
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

    public ResultData
    addToConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
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
                if ( !myConferences.add( entry.conference.id ) ) {
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

    public ResultData
    removeFromConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
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

    public ResultData
    deleteConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
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

    public ResultData
    sendMessageToConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    )
    {
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

    public ResultData
    sendMessageToContact(
        Task task,
        UUID myID
    )
    {
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
            Controller controller = base.getController( message.contact );
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
            Controller controller = base.getController( contact );
            if ( controller == null ) {
                errorLog( "controller on id" + contact.toString() + " is null" );
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

            Controller controller = base.getController( contact );
            if ( controller == null ) {
                errorLog( "receiver on id" + contact.toString() + " is null" );
                continue;
            }

            controller.addTask( task );
        }
    }

    public ResultData
    refreshStorage()
    {
        return new ResultData( ResultTypes.UpdatedFiles, base.getFiles() );
    }

    public ResultData
    uploadFile(
        Task task
    )
    {
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

    public ResultData
    downloadFile(
        Task task
    )
    {
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

    public ResultData
    removeFile(
        Task task
    )
    {
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

    public void
    release()
    {
        running = false;

        for ( Controller controller : base.controllers.values() ) {
            controller.addTask( new Task(Sources.Server, Commands.Disconnect, null) );
        }
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

    private boolean running;
    private final Base base = new Base();
}
