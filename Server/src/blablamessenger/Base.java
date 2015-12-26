package blablamessenger;

import coreutilities.*;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Base implements IBase
{
    public
    Base()
    {
        running = true;
    }

    @Override
    public ContactData
    addContact(
        UUID        id,
        ContactData contact
    )
    {
        return contacts.putIfAbsent( id, contact );
    }

    @Override
    public ContactData
    removeContact(
        UUID contactID
    )
    {
        return contacts.remove( contactID );
    }

    @Override
    public ArrayList< Contact >
    getContacts()
    {
        return contacts.entrySet().stream().map( entry -> new Contact(entry.getKey(), entry.getValue()) ).
            collect( Collectors.toCollection(ArrayList::new) );
    }

    @Override
    public ConcurrentConferenceData
    addConference(
        UUID                     id,
        ConcurrentConferenceData conference
    )
    {
        return conferences.putIfAbsent( id, conference );
    }

    @Override
    public ConcurrentConferenceData
    removeConference(
        UUID conferenceID
    )
    {
        return conferences.remove( conferenceID );
    }

    @Override
    public ConcurrentConferenceData
    getConference(
        UUID conferenceID
    )
    {
        return conferences.get( conferenceID );
    }

    @Override
    public IController
    addController(
        UUID        id,
        IController controller
    )
    {
        return controllers.putIfAbsent( id, controller );
    }

    @Override
    public IController
    removeController(
        UUID controllerID
    )
    {
        return controllers.remove( controllerID );
    }

    @Override
    public IController
    getController(
        UUID controllerID
    )
    {
        return controllers.get( controllerID );
    }

    @Override
    public void
    upload(
        UUID     id,
        FileData fileData
    )
    {
        files.put( id, fileData );
    }

    @Override
    public FileData
    download(
        UUID id
    )
    {
        return files.get( id );
    }

    @Override
    public FileData
    removeFile(
        UUID id
    )
    {
        return files.remove( id );
    }

    @Override
    public ArrayList< File >
    getFiles()
    {
        return files.entrySet().stream().map(
                entry -> new File( entry.getKey(), new FileData(entry.getValue().name, null) )
        ).collect( Collectors.toCollection(ArrayList::new) );
    }

    @Override
    public boolean
    isRunning()
    {
        return running;
    }

    @Override
    public void
    close()
    {
        running = false;

        for ( IController controller : controllers.values() ) {
            controller.addTask( new Task(Task.Sources.Server, Commands.Disconnect, null) );
        }
    }

    private final ConcurrentHashMap< UUID, ContactData              > contacts    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap< UUID, ConcurrentConferenceData > conferences = new ConcurrentHashMap<>();
    private final ConcurrentHashMap< UUID, IController              > controllers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap< UUID, FileData                 > files       = new ConcurrentHashMap<>();

    private boolean running = false;
}
