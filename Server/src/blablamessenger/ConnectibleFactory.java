package blablamessenger;

public class ConnectibleFactory
{
    public
    ConnectibleFactory(
        IBase base
    )
    {
        this.base = base;
    }

    public IConnectible
    create(
        ConnectibleImplementations implementation
    )
    {
        IConnectible result = null;

        switch ( implementation ) {
            case Default:
                result = new SocketConnection( base );
            break;
        }

        return result;
    }

    private final IBase base;
}
