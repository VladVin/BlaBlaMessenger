package coreutilities;

import java.io.Serializable;
import java.util.UUID;

public class FileIdNamePair implements Serializable
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
