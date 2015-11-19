package data_structures;

public class ContactConfMessagePair extends DataObject {
    public ContactId Source;
    public ConfMessagePair Message;
    
    public ContactConfMessagePair( final ContactId source,
        final ConfMessagePair message )
    {
        Source = source;
        Message = message;
    }
}
