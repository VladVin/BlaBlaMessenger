package blablamessenger;

import blablamessenger.Command.Sources;
import data_structures.Commands;
import data_structures.Conference;
import data_structures.ConferenceId;
import data_structures.Contact;
import data_structures.ContactId;
import data_structures.FileData;
import data_structures.FileId;
import data_structures.FileIdNamePair;
import data_structures.FileIdNamePairs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {
    public Server() { CONNECTION_TIMEOUT = 500; }
    
    public class ClientBase {
        public void addContact( Contact contact ) 
        { contacts.put( contact.Id, contact ); }
        public Contact removeContact( ContactId contact ) 
        { return contacts.remove( contact ); }
        public ArrayList< Contact > getContacts()
        { return new ArrayList<>( contacts.values() ); }
        
        public void addConference( Conference conference )
        { conferences.put( conference.Id, conference ); }
        public Conference removeConference( ConferenceId conference )
        { return conferences.remove( conference ); }
        public Conference getConference( ConferenceId conference )
        { return conferences.get( conference ); }
        
        public void addClient( ContactId contact, ClientReceiver client )
        { clients.put( contact, client ); }
        public ClientReceiver removeClient( ContactId client )
        { return clients.remove( client ); }
        public ClientReceiver getClient( ContactId contact )
        { return clients.get( contact ); }
        
        private ConcurrentHashMap< ContactId, Contact > contacts = 
                new ConcurrentHashMap();
        private ConcurrentHashMap< ConferenceId, Conference > conferences =
                new ConcurrentHashMap();
        private ConcurrentHashMap< ContactId, ClientReceiver > clients =
                new ConcurrentHashMap();
    }
   
    public class FileBase {
        public void addFile( FileIdNamePair file )
        { files.put( file.Id, file ); }
        public FileIdNamePair removeFile( FileId file )
        { return files.remove( file ); }
        public FileIdNamePairs getFiles()
        { return new FileIdNamePairs( new ArrayList<>(files.values()) ); }
        
        public void upload( FileId id, FileData data )
        { filesData.put( id, data ); }
        public FileData download( FileId file )
        { return filesData.get( file ); }
        public FileData remove( FileId file )
        { return filesData.remove( file ); }
        
        private ConcurrentHashMap< FileId, FileIdNamePair > files =
                new ConcurrentHashMap();
        private ConcurrentHashMap< FileId, FileData > filesData =
                new ConcurrentHashMap();
    }
    
    @Override
    public void run()
    {
        addLog( "server started" );
        try ( ServerSocket serverSocket = new ServerSocket( port ) ) {
            serverSocket.setSoTimeout( CONNECTION_TIMEOUT );
            
            while ( !this.isInterrupted() ) {
                try {
                    Socket newClient = serverSocket.accept();
                    addLog( "waited new client" );
                    new ClientReceiver( clientBase, fileBase, newClient ).
                            start();
                } catch ( SocketTimeoutException e ) {}
            }
            
            release( serverSocket );     
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main( String[] args ) { new Server().start(); }
    
    private void release( ServerSocket socket )
    {
        addLog( "server shutting down" );
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        clientBase.clients.entrySet().stream().forEach(
                (Entry< ContactId, ClientReceiver > client) -> {
            client.getValue().addCommand( new Command( Sources.Server,
                    Commands.Disconnect, null ) );
        });
        
        addLog( "server is off" );
    }
    
    private void addLog( String log ) 
    { 
        System.out.println( Server.class.getName() + ": " + log );
    }
    
    private final int port = 4444;
    private ClientBase clientBase = new ClientBase();
    private FileBase fileBase = new FileBase();
    
    private final int CONNECTION_TIMEOUT;
}
