package data_structures;

import java.util.ArrayList;

public class Conference extends DataObject {
    public ConferenceName Name;
    public ConferenceId Id;
    public ArrayList< ContactId > Members;
    
    public Conference( final ConferenceName name, 
            final ArrayList< ContactId > members )
    {
        Name = name;
        Id = null;
        Members = members;
    }
    
    public Conference( final Conference conference )
    {
        Name = conference.Name;
        Id = new ConferenceId();
        Members = conference.Members;
    }
}
