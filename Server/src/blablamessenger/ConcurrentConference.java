package blablamessenger;

import data_structures.Conference;

import java.util.UUID;

public class ConcurrentConference extends Conference
{
    public
    ConcurrentConference(
        Conference conference
    )
    {
        this.name    = conference.name;
        this.members = conference.members;
    }


    public final UUID lock = UUID.randomUUID();
}
