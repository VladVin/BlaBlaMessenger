package data_structures;

import java.util.ArrayList;

public class Conference extends DataObject {
    public ConferenceName Name;
    public ConferenceId Id;
    public ArrayList< ContactId > Members;
    
    public Conference( final ConferenceName name, 
            final ConferenceId id,
            final ArrayList< ContactId > members )
    {
        Name = name;
        Id = id;
        Members = members;
    }
    
    public Conference( final ConferenceName name, 
            final ArrayList< ContactId > members )
    {
        Name = name;
        Id = new ConferenceId();
        Members = members;
    }
}
