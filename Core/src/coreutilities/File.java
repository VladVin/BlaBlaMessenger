package coreutilities;

import java.util.UUID;

public class File
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
