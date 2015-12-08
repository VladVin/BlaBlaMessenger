package coreutilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ConferenceData implements Serializable
{
    public String            name;
    public ArrayList< UUID > members;

    public ConferenceData(
        String            name,
        ArrayList< UUID > members
    )
    {
        this.name    = name;
        this.members = members;
    }
}
