package blablamessenger;

public class ControllerFactory
{
    public
    ControllerFactory(
        IModel model
    )
    {
        this.model = model;
    }

    public IController
    create(
        ControllerImplementations implementation
    )
    {
        IController result = null;

        switch ( implementation ) {
            case Default:
                result = new Controller( model );
            break;
        }

        return result;
    }

    private final IModel model;
}
