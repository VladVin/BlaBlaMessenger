package blablamessenger;

import java.net.Socket;

public class CommunicableFactory
{
    public
    CommunicableFactory(
        IController controller,
        Socket      client
    )
    {
        this.controller = controller;
        this.client     = client;
    }

    public ICommunicable
    create(
        CommunicableImplementations implementation
    )
    {
        ICommunicable result = null;

        switch ( implementation ) {
            case Default:
                result = new Listener( controller, client );
            break;
        }

        return result;
    }

    private final IController controller;
    private final Socket      client;
}
