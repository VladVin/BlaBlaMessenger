package data_structures.File;

import data_structures.DataObject;

public class File extends DataObject {
    public FileName Name;
    public FileData Data;
    
    public File( final FileName name, final FileData data )
    {
        Name = name;
        Data = data;
    }
}
