package blablamessenger;

import coreutilities.ConferenceData;

import java.util.UUID;

public class ConcurrentConferenceData extends ConferenceData
{
    public ConcurrentConferenceData(
        ConferenceData conferenceData
    )
    {
        this.name    = conferenceData.name;
        this.members = conferenceData.members;
    }


    public final UUID lock = UUID.randomUUID();
}
