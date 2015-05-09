package data_structures;

import data_structures.Contact.ContactId;
import data_structures.Conference.ConferenceId;

public class ContactConfPair extends DataObject {
    public ContactId Contact;
    public ConferenceId Conference;
    
    public ContactConfPair( final ContactId contact, 
            final ConferenceId conference )
    {
        Contact = contact;
        Conference = conference;
    }
}
