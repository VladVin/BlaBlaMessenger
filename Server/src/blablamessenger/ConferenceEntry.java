package blablamessenger;

import coreutilities.Conference;

import java.util.UUID;

public class ConferenceEntry
{
    public UUID       contact;
    public Conference conference;

    public
    ConferenceEntry(
        UUID contact,
        Conference conference
    )
    {
        this.contact    = contact;
        this.conference = conference;
    }

}
