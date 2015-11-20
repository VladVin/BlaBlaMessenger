package data_structures;

public class File extends DataObject
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
