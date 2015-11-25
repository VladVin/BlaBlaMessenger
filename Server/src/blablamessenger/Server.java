package blablamessenger;

import blablamessenger.Task.Sources;
import data_structures.Commands;
import data_structures.Contact;
import data_structures.File;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread
{
    public
    Server() {}
    
    public class
    Base
    {
        public void
        addContact(
            UUID    id,
            Contact contact
        )
        {
            contacts.put( id, contact );
        }

        public Contact
        removeContact(
            UUID contact
        )
        {
            return contacts.remove( contact );
        }

        public HashMap< UUID, Contact >
        getContacts()
        {
            return new HashMap<>( contacts );
        }
        
        public void
        addConference(
            UUID                 id,
            ConcurrentConference conference
        )
        {
            conferences.put( id, conference );
        }

        public ConcurrentConference
        removeConference(
            UUID conference
        )
        {
            return conferences.remove( conference );
        }

        public ConcurrentConference
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
            File file
        )
        {
            files.put( id, file );
        }

        public File
        download(
            UUID id
        )
        {
            return files.get( id );
        }

        public File
        removeFile(
            UUID id
        )
        {
            return files.remove( id );
        }

        public ArrayList< File >
        getFiles()
        {
            return new ArrayList<>( files.values() );
        }
        
        private final ConcurrentHashMap< UUID, Contact              > contacts    = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, ConcurrentConference > conferences = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, ClientReceiver       > clients     = new ConcurrentHashMap<>();
        private final ConcurrentHashMap< UUID, File                 > files       = new ConcurrentHashMap<>();
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
                    addLog( "Timout on waiting new client" );
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
