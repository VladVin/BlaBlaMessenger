package data_structures;

import java.io.Serializable;
import java.util.UUID;

public class ContactConfMessagePair implements Serializable
{
    public UUID            source;
    public ConfMessagePair confMessage;
    
    public
    ContactConfMessagePair(
        UUID            source,
        ConfMessagePair confMessage
    )
    {
        this.source      = source;
        this.confMessage = confMessage;
    }
}
