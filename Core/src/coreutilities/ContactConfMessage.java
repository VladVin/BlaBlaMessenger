package coreutilities;

import java.io.Serializable;
import java.util.UUID;

public class ContactConfMessage implements Serializable
{
    public UUID            source;
    public ConfMessagePair confMessage;
    
    public ContactConfMessage(
        UUID            source,
        ConfMessagePair confMessage
    )
    {
        this.source      = source;
        this.confMessage = confMessage;
    }
}
