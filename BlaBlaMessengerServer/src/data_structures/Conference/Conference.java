package data_structures.Conference;

import data_structures.DataObject;
import java.util.ArrayList;
import java.util.UUID;

public class Conference extends DataObject{
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
