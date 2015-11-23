package data_structures;

import java.io.Serializable;
import java.util.UUID;

public class ContactConfPair implements Serializable
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
