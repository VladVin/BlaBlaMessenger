package data_structures.Contact;

import data_structures.DataObject;
import java.util.UUID;

public class ContactId extends DataObject {
    public UUID Id;

    public ContactId( final UUID id ) { Id = id; }
}
