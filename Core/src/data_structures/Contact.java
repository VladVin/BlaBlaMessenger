package data_structures;

import java.util.UUID;

public class Contact extends DataObject
{
    public String name;
    public UUID   id;
    
    public
    Contact(
        String name,
        UUID id
    )
    {
        this.name = name;
        this.id   = id;
    }
}
