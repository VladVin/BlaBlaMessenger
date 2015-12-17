package blablamessenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketConnection implements IConnectible
{
    public
    SocketConnection(
        IBase base
    )
    {
        this.base = base;
    }

    @Override
    public void
    connect(
        int port
    )
    {
        server.port = port;
        server.start();
    }

    @Override
    public void
    disconnect()
    {
        server.running = false;
    }

    public class Server extends Thread
    {
        public
        Server()
        {
            this.running = true;
        }

        @Override
        public void
        run()
        {
            final int timeout = 500;

            addLog( "server started" );
            try ( ServerSocket serverSocket = new ServerSocket( port ) ) {
                serverSocket.setSoTimeout( timeout );

                while ( isRunning() ) {
                    try {
                        Socket newClient = serverSocket.accept();
                        addLog( "waited new client" );

                        ModelFactory modelFactory = new ModelFactory( base );
                        IModel model = modelFactory.create( ModelImplementations.Default );

                        ControllerFactory controllerFactory = new ControllerFactory( model );
                        IController controller = controllerFactory.create( ControllerImplementations.Default );

                        CommunicableFactory communicableFactory = new CommunicableFactory( controller, newClient );
                        ICommunicable listener = communicableFactory.create( CommunicableImplementations.Default );

                        listener.subscribe();
                    }
                    catch ( SocketTimeoutException e ) {
                        addLog( "Timeout on waiting new client" );
                    }
                }

                release( serverSocket );
            }
            catch ( IOException ex ) {
                Logger.getLogger( SocketConnection.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }

        public boolean
        isRunning()
        {
            return running;
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
                Logger.getLogger( SocketConnection.class.getName() ).log( Level.SEVERE, null, ex );
            }

            base.close();

            addLog( "server is off" );
        }

        private void
        addLog(
                String log
        )
        {
            System.out.println( SocketConnection.class.getName() + ": " + log );
        }

        private int     port    = 2671;
        private boolean running = false;
    }

    private final IBase  base;
    private final Server server = new Server();
}
