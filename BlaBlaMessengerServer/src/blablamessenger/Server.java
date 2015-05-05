package blablamessenger;

import data_structures.Conference;
import data_structures.Contact;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {
    final int port = 2671;
    InetAddress ip = null;
      
    ClientBase clientBase = new ClientBase();
    
    public Server()
    {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public class ClientBase {
        public void addContact( Contact contact ) 
        { contacts.add( contact ); }
        public void removeContact( Contact contact ) 
        { contacts.remove( contact ); }
        public List< Contact > getContacts()
        { return contacts.subList( 0 ,  contacts.size() ); }
        
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
        
        private List< Contact > contacts = 
                Collections.synchronizedList( new ArrayList< Contact >() );
        private ConcurrentHashMap< UUID, Conference > conferences =
                new ConcurrentHashMap();
        private ConcurrentHashMap< UUID, ClientReceiver > clients =
                new ConcurrentHashMap();
    }
    
    @Override
    public void run()
    {
        try {
            ServerSocket serverSocket = new ServerSocket( port, 0, ip );
            while ( true ) {
                Socket newClient = serverSocket.accept();
                new ClientReceiver( clientBase, newClient ).start();
            }     
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
}
