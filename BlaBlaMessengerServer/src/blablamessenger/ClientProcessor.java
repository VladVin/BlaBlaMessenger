package blablamessenger;

import blablamessenger.Server.ClientBase;
import data_structures.CommandData;
import data_structures.Contact;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientProcessor extends Thread {
    ClientBase clientBase;
    Socket socket;
    boolean disconnect;
    
    ObjectOutputStream output;
    UUID client;
    
    
    public ClientProcessor( ClientBase base, Socket myClient, String name,
            ClientReceiver receiver, LinkedBlockingQueue inputCommands )
    {
        clientBase = base;
        socket = myClient;
        commands = inputCommands;
        
        try {
            output = new ObjectOutputStream( socket.getOutputStream() );
            
            client = UUID.randomUUID();
            output.writeObject( client );
            output.flush();
            
            clientBase.addContact( new Contact( name, client ) );
            clientBase.addClient( client, receiver );
        } catch (IOException ex) {
            Logger.getLogger(ClientProcessor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    public CommandData getCommand()
    {   
        try {
            return ( CommandData ) commands.take();
        } catch ( InterruptedException ex ) {
            Logger.getLogger(ClientProcessor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void run()
    {
        while ( true ) {
            CommandData command = getCommand();
            switch ( command.Command ) {
                case Disconnect:
                    
                break;
            }
        }
    }    
    
    private LinkedBlockingQueue commands = new LinkedBlockingQueue();
    private ArrayList< UUID > myConferences = new ArrayList<>();
}
