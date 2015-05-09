package data_structures;

import data_structures.DataObject;
import java.util.UUID;

public class Contact extends DataObject {
    public class ContactId extends DataObject {
        public UUID Id;

        public ContactId() { Id = UUID.randomUUID(); }
    }
    public class ContactName extends DataObject {
        public String Name;
    
        public ContactName( final String name  ) { Name = name; }
    }

    
    
    public ContactName Name;
    public ContactId Id;
    
    public Contact( final ContactName name )
    {
        Name = name;
        Id = new ContactId();
    }
}
