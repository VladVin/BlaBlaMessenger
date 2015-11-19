package data_structures;

import java.util.UUID;

public class ConferenceId extends DataObject {
    public UUID Id;

    public ConferenceId() { Id = UUID.randomUUID(); }
}
