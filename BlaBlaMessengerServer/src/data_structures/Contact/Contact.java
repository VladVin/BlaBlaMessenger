package data_structures.Contact;

import data_structures.DataObject;

public class Contact extends DataObject {
    public ContactName Name;
    public ContactId Id;
    
    public Contact( final ContactName name, final ContactId id )
    {
        Name = name;
        Id = id;
    }
}
