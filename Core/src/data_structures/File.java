package data_structures;

import java.io.Serializable;

public class File implements Serializable
{
    public String name;
    public byte[] data;
    
    public
    File(
        String name,
        byte[] data
    )
    {
        this.name = name;
        this.data = data;
    }
}
