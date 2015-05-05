package data_structures;

import java.util.UUID;

/**
 * Created by VladVin on 25.04.2015.
 */
public class Contact extends DataObject {
    public String Name;
    public UUID Uuid;

    public Contact( String name, UUID id ) {
        Name = name;
        Uuid = id;
    }
}
