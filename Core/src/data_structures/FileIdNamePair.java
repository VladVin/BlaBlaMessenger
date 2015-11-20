package data_structures;

import java.util.UUID;

public class FileIdNamePair extends DataObject
{
    public UUID   id;
    public String name;
    
    public
    FileIdNamePair(
        UUID   id,
        String name
    )
    {
        this.id   = id;
        this.name = name;
    }
}
