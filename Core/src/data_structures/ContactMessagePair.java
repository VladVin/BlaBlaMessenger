package data_structures;

import java.util.UUID;

public class ContactMessagePair extends DataObject
{
    public UUID   contact;
    public String message;
    
    public
    ContactMessagePair(
        UUID   contact,
        String message
    )
    {
        this.contact = contact;
        this.message = message;
    }
}
