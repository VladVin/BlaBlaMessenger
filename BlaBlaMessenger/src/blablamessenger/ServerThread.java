package blablamessenger;

import blablamessenger.Server.ClientBase;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {
    
    ClientBase clientBase;
    Socket client;
    
    DataInputStream input;
    DataOutputStream output;
    
    public ServerThread( ClientBase base, Socket newClient )
    {
        clientBase = base;
        client = newClient;
    }
    
    @Override
    public void run()
    {
        try {
            InputStream inStream = client.getInputStream();
            input = new DataInputStream( inStream );
            
            OutputStream ouputStream = client.getOutputStream();
            output = new DataOutputStream( ouputStream );
            
            output.writeUTF( "Connection accepted" );
            
            String name = input.readUTF();
            UUID id = UUID.randomUUID();
            
            clientBase.addClient( this, name, id );
            
            while ( true ) {
                int command = input.readInt();
                switch( command ) {
                    case REFRESH_CONTACTS:
                        refreshContacts( name );
                        break;
                        
                    default:
                        output.writeUTF( "Unknown command" );
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
    
    public void refreshContacts( String clientName )
    {
        String[] contacts = clientBase.getContactsList();
        try {
            output.writeInt( contacts.length - 1 );
            for ( int i = 0; i < contacts.length; ++i ) {
                if ( !contacts[i].equals( clientName ) ) {
                    output.writeUTF( contacts[i] );
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    private static final int REFRESH_CONTACTS = 0;
}
