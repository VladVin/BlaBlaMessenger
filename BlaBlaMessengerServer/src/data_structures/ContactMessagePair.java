package data_structures;

import data_structures.Contact.ContactId;

public class ContactMessagePair extends DataObject {
    public ContactId Contact;
    public MessageData Message;
    
    public ContactMessagePair( final ContactId contact, 
            final MessageData message )
    {
        Contact = contact;
        Message = message;
    }
}
