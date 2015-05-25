package data_structures;

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
