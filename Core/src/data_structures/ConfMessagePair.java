package data_structures;

import java.util.UUID;

public class ConfMessagePair extends DataObject
{
    public UUID   destination;
    public String message;
    
    public
    ConfMessagePair(
        UUID   destination,
        String message
    )
    {
        this.destination = destination;
        this.message     = message;
    }
}
