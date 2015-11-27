package blablamessenger;

import blablamessenger.Task.Sources;
import coreutilities.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Server extends Thread
{
    public
    Server() {}
    
    public class
    Base
    {
        public void
        addContact(
            UUID        id,
            ContactData contactData
        )
        {
            contacts.put( id, contactData );
        }

        public ContactData
        removeContact(
            UUID contact
        )
        {
            return contacts.remove( contact );
        }

        public ArrayList< Contact >
        getContacts()
        {
            return contacts.entrySet().stream().map( entry -> new Contact(entry.getKey(), entry.getValue()) ).
                collect( Collectors.toCollection(ArrayList::new) );
        }
        
        public void
        addConference(
            UUID                     id,
            ConcurrentConferenceData conference
        )
        {
            conferences.put( id, conference );
        }

        public ConcurrentConferenceData
        removeConference(
            UUID conference
        )
        {
            return conferences.remove( conference );
        }

        public ConcurrentConferenceData
        getConference(
            UUID conference
        )
        {
            return conferences.get( conference );
        }
        
        public void
        addClient(
            UUID           contact,
            ClientReceiver client
        )
        {
            clients.put( contact, client );
        }

        public ClientReceiver
        removeClient(
            UUID client
        )
        {
            return clients.remove( client );
        }
        public ClientReceiver
        getClient(
            UUID contact
        )
        {
            return clients.get( contact );
        }

        public void
        upload(
            UUID id,
            FileData fileData
        )
        {
            files.put( id, fileData);
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
            return files.entrySet().stream().map( entry ->
                new File(entry.getKey(), new FileData(entry.getValue().name, null)) ).
                collect( Collectors.toCollection(ArrayList::new) );
        }
        
        private final ConcurrentHashMap< UUID, ContactData              > contacts    = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, ConcurrentConferenceData > conferences = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, ClientReceiver           > clients     = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, FileData                 > files       = new ConcurrentHashMap<>();
    }
    
    @Override
    public void
    run()
    {
        final int port    = 2671;
        final int timeout = 500;

        addLog( "server started" );
        try ( ServerSocket serverSocket = new ServerSocket( port ) ) {
            serverSocket.setSoTimeout( timeout );
            
            while ( !this.isInterrupted() ) {
                try {
                    Socket newClient = serverSocket.accept();
                    addLog( "waited new client" );

                    UUID newClientID = UUID.randomUUID();

                    ClientReceiver client = new ClientReceiver( base, newClientID, newClient );

                    client.start();
                    base.addClient( newClientID, client );
                }
                catch ( SocketTimeoutException e ) {
                    addLog( "Timeout on waiting new client" );
                }
            }
            
            release( serverSocket );     
        }
        catch ( IOException ex ) {
            Logger.getLogger( Server.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }
    
    public static void
    main(
        String[] args
    )
    {
        new Server().start();
    }
    
    private void
    release(
        ServerSocket socket
    )
    {
        addLog( "server shutting down" );
        try {
            socket.close();
        }
        catch ( IOException ex ) {
            Logger.getLogger( Server.class.getName() ).log( Level.SEVERE, null, ex );
        }

        for ( ClientReceiver receiver : base.clients.values() ) {
            receiver.addCommand( new Task(Sources.Server, Commands.Disconnect, null) );
        }
        
        addLog( "server is off" );
    }
    
    private void
    addLog(
        String log
    )
    { 
        System.out.println( Server.class.getName() + ": " + log );
    }

    private final Base base = new Base();
}
