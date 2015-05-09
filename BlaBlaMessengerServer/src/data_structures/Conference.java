package data_structures;

import data_structures.DataObject;
import java.util.ArrayList;
import java.util.UUID;

public class Conference extends DataObject {
    public class ConferenceId extends DataObject {
        public UUID Id;
    
        public ConferenceId() { Id = UUID.randomUUID(); }
    }
    public class ConferenceName extends DataObject {
        public String Name;
    
        public ConferenceName( String name ) { Name = name; }
    }
    
    public ConferenceName Name;
    public ConferenceId Id;
    public ArrayList< UUID > Contacts;
    
    public Conference( final ConferenceName name, 
            final ArrayList< UUID > contacts )
    {
        Name = name;
        Id = new ConferenceId();
        Contacts = contacts;
    }
}
