package data_structures.Conference;

import data_structures.DataObject;
import java.util.UUID;

public class ConferenceId extends DataObject {
    public UUID Id;
    
    public ConferenceId() { Id = UUID.randomUUID(); }
}
