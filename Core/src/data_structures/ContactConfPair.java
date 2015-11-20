package data_structures;

import java.util.UUID;

public class ContactConfPair extends DataObject
{
    public UUID contact;
    public UUID conference;
    
    public
    ContactConfPair(
        UUID contact,
        UUID conference
    )
    {
        this.contact    = contact;
        this.conference = conference;
    }
}
