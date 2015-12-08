package coreutilities;

import java.io.Serializable;
import java.util.UUID;

public class File implements Serializable
{
    public UUID     id;
    public FileData fileData;

    public
    File(
        UUID     id,
        FileData fileData
    )
    {
        this.id       = id;
        this.fileData = fileData;
    }
}
