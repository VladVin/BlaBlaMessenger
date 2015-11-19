package data_structures;

import java.util.UUID;

public class ContactId extends DataObject {
    public UUID Id;

    public ContactId() { Id = UUID.randomUUID(); }
}
