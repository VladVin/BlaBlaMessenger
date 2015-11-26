package coreutilities;

import java.io.Serializable;
import java.util.UUID;

public class File implements Serializable
{
    public UUID     id;
    public FileData data;

    public
    File(
        UUID     id,
        FileData data
    )
    {
        this.id   = id;
        this.data = data;
    }
}
