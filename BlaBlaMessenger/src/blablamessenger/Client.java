package blablamessenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    
    static String userName = "";
    final int port = 2671;
    InetAddress ip = null;
    
    static ContactsFrame contacts;
    
    DataInputStream input;
    DataOutputStream output;
    
    public void start()
    {   
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        try {
            Socket server = new Socket( ip, port );
            
            InputStream inStream = server.getInputStream();
            input = new DataInputStream( inStream );
            
            OutputStream outStream = server.getOutputStream();
            output = new DataOutputStream( outStream );
                        
            String message = input.readUTF();
            addLog( message );
            
            output.writeUTF( userName );
            
            refreshContactList();
           
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        
                
    }
    
    public void refreshContactList()
    {
        try {
            output.writeInt( REFRESH_CONTACTS );
            int length = input.readInt();
            
            String[] contactsList = new String[ length ];
            for( int i = 0; i < length; ++i ) {
                contactsList[i] = input.readUTF();
            }
            
            contacts.refreshContactsList( contactsList );
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void addLog( String log )
    {
        System.out.println( log );
    }
    
    public static void main(String[] args)
    {
        GetUserNameFrame nameFrame = new GetUserNameFrame();
        userName = nameFrame.getUserName();
        nameFrame.dispose();
        
        contacts = new ContactsFrame( "BlaBlaMessenger" , userName );
        
        new Client().start();
    }
    
    private static final int REFRESH_CONTACTS = 0;
}
