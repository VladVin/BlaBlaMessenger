package blablamessenger;

import coreutilities.CommandData;
import coreutilities.Commands;
import coreutilities.ResultData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Listener
{
    public
    Listener(
        ServerController serverController,
        Socket           client
    )
    {
        this.serverController = serverController;
        this.client           = client;
    }

    public boolean
    addTask(
        Task task
    )
    {
        return tasks.add( task );
    }

    public void
    subscribe()
    {
        new Receiver().start();
        new Sender  ().start();

        serverController.new Controller( this, tasks, results );
    }

    public void
    close()
    {
        try {
            if ( !client.isInputShutdown() ) {
                client.shutdownInput();
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            if ( !client.isOutputShutdown() ) {
                client.shutdownOutput();
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            if ( !client.isClosed() ) {
                client.close();
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private class Receiver extends Thread
    {
        public
        Receiver()
        {
            try {
                in = new ObjectInputStream( client.getInputStream() );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        @Override
        public void
        run()
        {
            while ( !client.isInputShutdown() ) {
                try {
                    addLog( "waiting for new operation" );

                    Object command = in.readObject();
                    if ( !(command instanceof CommandData) ) {
                        errorLog( "read unknown data type" );
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
                        errorLog( "read invalid command" );
                        return;
                    }

                    if ( operation.command == Commands.RegisterContact ) {
                        registered = true;
                    }

                    if ( !addTask(new Task( Task.Sources.Client, operation )) ) {
                        errorLog( "error on adding task to queue" );
                    }
                    addLog( "added new operation" );
                }
                catch ( IOException | ClassNotFoundException ex ) {
                    break;
                }
            }

            try {
                in.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }

            addLog( "released" );
        }

        private ObjectInputStream in;
        private boolean registered = false;
    }


    private class Sender extends Thread
    {
        public
        Sender()
        {
            try {
                out = new ObjectOutputStream( client.getOutputStream() );
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void
        run()
        {
            while ( !client.isOutputShutdown() ) {
                ResultData result;

                do {
                    result = results.poll();
                } while( result == null );

                try {
                    out.writeObject( result );
                }
                catch ( IOException e ) {
                    break;
                }
            }

            try {
                out.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        private ObjectOutputStream out;
    }

    private void
    errorLog(
        String message
    )
    {
        final int CALLING_METHOD = 1;
        Throwable t = new Throwable();
        StackTraceElement trace[] = t.getStackTrace();
        if ( 1 < trace.length ) {
            StackTraceElement element = trace[ CALLING_METHOD ];
            addLog( element.getMethodName() + " " + element.getLineNumber() + " " + message );
        }
    }

    private void
    addLog(
        String log
    )
    {
        System.out.println( Listener.class.getName() + ": " + log );
    }

    private final Socket           client;
    private final ServerController serverController;

    private final ConcurrentLinkedQueue< Task       > tasks   = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue< ResultData > results = new ConcurrentLinkedQueue<>();
}
