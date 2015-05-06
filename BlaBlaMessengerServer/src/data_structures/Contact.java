package data_structures;

import java.util.UUID;

public class Contact extends DataObject {
    public String Name;
    public UUID Uuid;

    public Contact( String name, UUID id ) {
        Name = name;
        Uuid = id;
    }
}
