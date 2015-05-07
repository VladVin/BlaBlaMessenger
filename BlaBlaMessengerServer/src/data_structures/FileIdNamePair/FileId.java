package data_structures.FileIdNamePair;

import data_structures.DataObject;
import java.util.UUID;

public class FileId extends DataObject {
    public UUID Id;
    
    public FileId() { Id = UUID.randomUUID(); }
}
