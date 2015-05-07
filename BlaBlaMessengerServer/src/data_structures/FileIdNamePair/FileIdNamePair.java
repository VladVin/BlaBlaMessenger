package data_structures.FileIdNamePair;

import data_structures.DataObject;
import data_structures.File.FileName;

public class FileIdNamePair extends DataObject {
    public FileId Id;
    public FileName Name;
    
    public FileIdNamePair( final FileId id, final FileName name )
    {
        Id = id;
        Name = name;
    }
}
