package blablamessenger;

import blablamessenger.Task.Sources;
import blablamessenger.Server.Base;
import coreutilities.CommandData;
import coreutilities.Commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientReceiver extends Thread
{
    public
    ClientReceiver(
        Base   base,
        UUID   myContactID,
        Socket client
    )
    {
        this.base        = base;
        this.myContactID = myContactID;
        this.client      = client;

        try {
            input = new ObjectInputStream( client.getInputStream() );
        }
        catch ( IOException ex ) {
            Logger.getLogger( ClientReceiver.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    public void
    addCommand(
        Task task
    )
    {
        tasks.add( task );
    }

    @Override
    public void
    run()
    {
        createProcessor();
        
        while ( true ) {
            try {
                addLog( "waiting for new operation" );

                Object command = input.readObject();
                if ( !(command instanceof CommandData) ) {
                    errorLog( "readed unknown data type" );
                    continue;
                }

                CommandData operation = (CommandData) command;
                addLog( "get " + operation.command.name() + " operation" );

                if ( operation.command == null ) {
                    errorLog( "type of command is null" );
                    continue;
                }

                if ( !registered && operation.command != Commands.RegisterContact ||
                      registered && operation.command == Commands.RegisterContact ) {
                    errorLog( "readed invalid command" );
                    return;
                }

                if ( operation.command == Commands.RegisterContact ) {
                    registered = true;
                }

                addCommand( new Task(Sources.Client, operation) );
                addLog( "added new operation" );
            }
            catch ( IOException | ClassNotFoundException ex ) {
                break;
            }
        }
        
        releaseProcessor();
        addLog( "released" );
    }
    
    private void
    createProcessor()
    { 
        new ClientProcessor( base, client, myContactID, tasks  ).start();
    }

    private void
    releaseProcessor()
    {
        addCommand( new Task(Sources.Client, Commands.Disconnect, null) );
    }
    
    private void
    addLog(
        String log
    )
    { 
        System.out.println( ClientReceiver.class.getName() + ": " + log );
    }


    private void
    errorLog(
        String message
    )
    {
        final int CALLING_METHOD = 1;
        Throwable t = new Throwable();
        StackTraceElement trace[] = t.getStackTrace();
        if ( 1 < trace.length  ) {
            StackTraceElement element = trace[ CALLING_METHOD ];
            addLog( element.getMethodName() + " " + element.getLineNumber() + " " + message );
        }
    }
    
    private final Base   base;
    private final Socket client;
    private final UUID   myContactID;
    
    private ObjectInputStream             input;
    private ConcurrentLinkedQueue< Task > tasks      = new ConcurrentLinkedQueue<>();
    private boolean                       registered = false;
}
