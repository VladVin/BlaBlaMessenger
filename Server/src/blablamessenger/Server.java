package blablamessenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread
{
    public
    Server(
        ServerController serverController
    )
    {
        this.serverController = serverController;
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

                    new Listener( serverController, newClient ).subscribe();
                }
                catch ( SocketTimeoutException e ) {
                    addLog( "Timeout on waiting new client" );
                }
            }

            release( serverSocket );
        }
        catch ( IOException ex ) {
            Logger.getLogger( Server.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    private void
    release(
        ServerSocket serverSocket
    )
    {
        addLog( "server shutting down" );

        try {
            serverSocket.close();
        }
        catch ( IOException ex ) {
            Logger.getLogger( Server.class.getName() ).log( Level.SEVERE, null, ex );
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

    private final ServerController serverController;
}
