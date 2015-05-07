package blablamessenger;

import data_structures.CommandData;
import data_structures.Commands;
import data_structures.Conference;
import data_structures.Contact;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {
    final int port = 4444;
      
    ClientBase clientBase = new ClientBase();
    
    public Server()
    {
        CONNECTION_TIMEOUT = 500;
    }
    
    public class ClientBase {
        public void addContact( Contact contact ) 
        { contacts.put( contact.Uuid, contact ); }
        public void removeContact( UUID contactId ) 
        { contacts.remove( contactId ); }
        public ArrayList< Contact > getContacts()
        { return ( ArrayList< Contact > ) contacts.elements(); }
        
        public void addConference( UUID conferenceId, Conference conference )
        { conferences.put( conferenceId, conference ); }
        public void removeConference( UUID conferenceId )
        { conferences.remove( conferenceId ); }
        public Conference getConference( UUID conferenceId )
        { return conferences.get( conferenceId ); }
        
        public void addClient( UUID clientId, ClientReceiver client )
        { clients.put( clientId, client ); }
        public void removeClient( UUID clientId )
        { clients.remove( clientId ); }
        public ClientReceiver getClient( UUID clientId )
        { return clients.get( clientId ); }
        
        private ConcurrentHashMap< UUID, Contact > contacts = 
                new ConcurrentHashMap();
        private ConcurrentHashMap< UUID, Conference > conferences =
                new ConcurrentHashMap();
        private ConcurrentHashMap< UUID, ClientReceiver > clients =
                new ConcurrentHashMap();
    }
    
    public void release( ServerSocket socket )
    {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        clientBase.clients.entrySet().stream().forEach(
                (Entry< UUID, ClientReceiver > client) -> {
            client.getValue().addCommand( new CommandData(
                    Commands.Disconnect, null, null ) );
        });
    }
    
    @Override
    public void run()
    {
        try ( ServerSocket serverSocket = new ServerSocket( port ) ) {
            serverSocket.setSoTimeout( CONNECTION_TIMEOUT );
            
            while ( !this.isInterrupted() ) {
                try {
                    Socket newClient = serverSocket.accept();
                    addLog( Server.class.getName() + 
                            ": waited new client" );
                    new ClientReceiver( clientBase, newClient ).start();
                } catch ( SocketTimeoutException e ) {}
            }
            
            release( serverSocket );     
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args)
    {
        new Server().start();
    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
    
    private final int CONNECTION_TIMEOUT;
}
