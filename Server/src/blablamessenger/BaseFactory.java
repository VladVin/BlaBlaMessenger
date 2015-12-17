package blablamessenger;

public class BaseFactory
{
    public IBase
    create(
        BaseImplementations implementation
    )
    {
        IBase result = null;

        switch ( implementation ) {
            case Default:
                result = new Base();
            break;
        }

        return result;
    }
}
