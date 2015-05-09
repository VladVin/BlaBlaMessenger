package data_structures;

public class Contact extends DataObject {
    public ContactName Name;
    public ContactId Id;
    
    public Contact( final ContactName name )
    {
        Name = name;
        Id = new ContactId();
    }
}
