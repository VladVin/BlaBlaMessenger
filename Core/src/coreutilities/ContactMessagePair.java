package coreutilities;

import java.io.Serializable;
import java.util.UUID;

public class ContactMessagePair implements Serializable
{
    public UUID   contact;
    public String text;
    
    public
    ContactMessagePair(
        UUID   contact,
        String text
    )
    {
        this.contact = contact;
        this.text    = text;
    }
}
