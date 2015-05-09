package data_structures;

import data_structures.DataObject;
import data_structures.File.FileName;
import java.util.UUID;

public class FileIdNamePair extends DataObject {
    public class FileId extends DataObject {
        public UUID Id;
    
        public FileId() { Id = UUID.randomUUID(); }
    }

    
    public FileId Id;
    public FileName Name;
    
    public FileIdNamePair( final FileId id, final FileName name )
    {
        Id = id;
        Name = name;
    }
}
