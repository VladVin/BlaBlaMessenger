package blablamessenger;

public class ModelFactory
{
    public
    ModelFactory(
        IBase base
    )
    {
        this.base = base;
    }

    public IModel
    create(
        ModelImplementations implementation
    )
    {
        IModel result = null;

        switch( implementation ) {
            case Default:
                result = new Model( base );
            break;
        }

        return result;
    }

    private final IBase base;
}
