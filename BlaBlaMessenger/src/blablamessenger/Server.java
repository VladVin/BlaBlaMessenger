package blablamessenger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
    final int port = 2671;
    InetAddress ip = null;
    ServerSocket serverSocket;
    
    ClientBase clientBase = new ClientBase();
    
    public Server()
    {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public class Client {
        
        public Client( ServerThread thread, String name, UUID uuid )
        {
            serverThread = thread;
            Name = name;
            id = uuid;
        }
        
        public ServerThread getServerThread()
        {
            return serverThread;
        }
        public String getName()
        {
            return Name;
        }
        public UUID getId()
        {
            return id;
        }
        
        private ServerThread serverThread;
        private String Name;
        private UUID id;
     
    }    
    public class ClientBase {
        
        synchronized public void addClient( ServerThread thread, String name,
                UUID id )
        {
            Client client = new Client( thread, name, id );
            clients.add( client );
        }
        
        synchronized public Client getClient( int client )
        {
            return clients.get( client );
        }
        
        synchronized public void removeClient( int client )
        {
            clients.remove( client );
        }
        
        synchronized public String[] getContactsList()
        {
            String[] names = new String[ clients.size() ];  
            for ( int i = 0; i < names.length; ++i ) {
                names[i] = clients.get(i).getName();
            }
            return names;
        }
        
        private ArrayList<Client> clients = new ArrayList<>();
    }
 
    public void addLog( String log )
    {
        System.out.println( log );
    }
    
    @Override
    public void run()
    {
        Socket newClient;
        
        try {
            serverSocket = new ServerSocket( port, 0, ip );
            addLog( "Server start" );
            
            while ( true ) {
                addLog( "Waiting for next client" );
                newClient = serverSocket.accept();
                addLog( "New client accepted" );
                
                ServerThread serverThread = 
                        new ServerThread( clientBase, newClient );
                serverThread.start();
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
}
