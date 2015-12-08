package coreutilities;

import java.io.Serializable;

public class FileData implements Serializable
{
    public String name;
    public byte[] data;
    
    public FileData(
        String name,
        byte[] data
    )
    {
        this.name = name;
        this.data = data;
    }
}
