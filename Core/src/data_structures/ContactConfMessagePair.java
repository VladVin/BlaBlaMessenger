package data_structures;

import java.util.UUID;

public class ContactConfMessagePair extends DataObject
{
    public UUID            source;
    public ConfMessagePair message;
    
    public
    ContactConfMessagePair(
        UUID            source,
        ConfMessagePair message
    )
    {
        this.source  = source;
        this.message = message;
    }
}
