package blablamessenger;

import coreutilities.ConferenceData;

import java.util.UUID;

public class ConcurrentConferenceData extends ConferenceData
{
    public ConcurrentConferenceData(
        ConferenceData conferenceData
    )
    {
        super( conferenceData.name, conferenceData.members );
    }


    public final UUID lock = UUID.randomUUID();
}
