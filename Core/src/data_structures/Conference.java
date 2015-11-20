package data_structures;

import java.util.ArrayList;
import java.util.UUID;

public class Conference extends DataObject
{
    public String            name;
    public UUID              id;
    public ArrayList< UUID > members;
    
    public
    Conference(
        String            name,
        ArrayList< UUID > members
    )
    {
        this.name    = name;
        this.id      = null;
        this.members = members;
    }
    
    public void
    copyConference(
        Conference conference
    )
    {
        this.name    = conference.name;
        this.members = conference.members;

        this.id = UUID.randomUUID();
    }

}
