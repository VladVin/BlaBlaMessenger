package data_structures;

public class File extends DataObject {
    public FileName Name;
    public FileData Data;
    
    public File( final FileName name, final FileData data )
    {
        Name = name;
        Data = data;
    }
}
