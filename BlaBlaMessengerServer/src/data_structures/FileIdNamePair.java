package data_structures;

public class FileIdNamePair extends DataObject {
    public FileId Id;
    public FileName Name;
    
    public FileIdNamePair( final FileId id, final FileName name )
    {
        Id = id;
        Name = name;
    }
}
