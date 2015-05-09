package data_structures;

import data_structures.DataObject;

public class File extends DataObject {
    public class FileData extends DataObject {
        public byte[] Data;
    
        public FileData( final byte[] data ) { Data = data; }
    }
    public class FileName extends DataObject {
        public String Name;
    
        public FileName( String name ) { Name = name; }
    }
    
    
    public FileName Name;
    public FileData Data;
    
    public File( final FileName name, final FileData data )
    {
        Name = name;
        Data = data;
    }
}
