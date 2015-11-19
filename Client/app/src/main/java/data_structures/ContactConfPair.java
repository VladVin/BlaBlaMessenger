package data_structures;

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
