package coreutilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Conference implements Serializable
{
    public UUID           id;
    public ConferenceData data;

    public
    Conference(
        UUID              id,
        String            name,
        ArrayList< UUID > members
    )
    {
        this.id   = id;
        this.data = new ConferenceData( name, members );
    }

    public
    Conference(
        UUID           id,
        ConferenceData data
    )
    {
        this.id   = id;
        this.data = data;
    }
}
